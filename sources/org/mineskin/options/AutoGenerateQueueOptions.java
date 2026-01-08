/*
 * Decompiled with CFR 0.152.
 */
package org.mineskin.options;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import org.mineskin.MineSkinClient;
import org.mineskin.MineSkinClientImpl;
import org.mineskin.data.User;
import org.mineskin.options.IQueueOptions;
import org.mineskin.response.UserResponse;

public class AutoGenerateQueueOptions
implements IQueueOptions {
    private static final int MIN_INTERVAL_MILLIS = 100;
    private static final int MAX_INTERVAL_MILLIS = 1000;
    private static final int MIN_CONCURRENCY = 1;
    private static final int MAX_CONCURRENCY = 30;
    private final ScheduledExecutorService scheduler;
    private MineSkinClient client;
    private final AtomicLong lastRefresh = new AtomicLong(0L);
    private final AtomicInteger intervalMillis = new AtomicInteger(1000);
    private final AtomicInteger concurrency = new AtomicInteger(100);

    public AutoGenerateQueueOptions(ScheduledExecutorService scheduler) {
        this.scheduler = scheduler;
    }

    public AutoGenerateQueueOptions() {
        this(Executors.newSingleThreadScheduledExecutor());
    }

    public void setClient(MineSkinClient client) {
        this.client = client;
        this.reloadGrants().exceptionally(throwable -> {
            MineSkinClientImpl.LOGGER.log(Level.WARNING, "Failed to load grants", (Throwable)throwable);
            return null;
        });
    }

    @Override
    public ScheduledExecutorService scheduler() {
        return this.scheduler;
    }

    @Override
    public int intervalMillis() {
        this.reloadIfNeeded();
        return this.intervalMillis.get();
    }

    @Override
    public int concurrency() {
        this.reloadIfNeeded();
        return this.concurrency.get();
    }

    private void reloadIfNeeded() {
        long now = System.currentTimeMillis();
        if (now - this.lastRefresh.get() > TimeUnit.MINUTES.toMillis(5L)) {
            this.reloadGrants().exceptionally(throwable -> {
                MineSkinClientImpl.LOGGER.log(Level.WARNING, "Failed to reload grants", (Throwable)throwable);
                return null;
            });
        }
    }

    public CompletableFuture<Void> reloadGrants() {
        if (this.client == null) {
            return CompletableFuture.completedFuture(null);
        }
        this.lastRefresh.set(System.currentTimeMillis());
        return ((CompletableFuture)((CompletableFuture)this.client.misc().getUser().thenApply(UserResponse::getUser)).thenApply(User::grants)).thenAccept(grants -> {
            grants.concurrency().ifPresent(rawConcurrent -> {
                int concurrent = Math.min(Math.max(rawConcurrent, 1), 30);
                int previous = this.concurrency.getAndSet(concurrent);
                if (previous != rawConcurrent) {
                    this.client.getLogger().log(Level.FINE, "[QueueOptions] Updated concurrency from {0} to {1}", new Object[]{previous, concurrent});
                }
            });
            grants.perMinute().ifPresent(rawPerMinute -> {
                int interval = Math.min(Math.max(60000 / rawPerMinute, 100), 1000);
                int previous = this.intervalMillis.getAndSet(interval);
                if (previous != interval) {
                    this.client.getLogger().log(Level.FINE, "[QueueOptions] Updated interval from {0}ms to {1}ms (perMinute={2})", new Object[]{previous, interval, rawPerMinute});
                }
            });
        });
    }
}

