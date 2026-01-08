/*
 * Decompiled with CFR 0.152.
 */
package org.mineskin;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.mineskin.options.IJobCheckOptions;
import org.mineskin.request.backoff.RequestInterval;

public final class JobCheckOptions
implements IJobCheckOptions {
    private final ScheduledExecutorService scheduler;
    private final RequestInterval interval;
    private final int initialDelayMillis;
    private final int maxAttempts;
    private final boolean useEta;

    @Deprecated
    public JobCheckOptions(ScheduledExecutorService scheduler, RequestInterval interval, int initialDelayMillis, int maxAttempts, boolean useEta) {
        this.scheduler = scheduler;
        this.interval = interval;
        this.initialDelayMillis = initialDelayMillis;
        this.maxAttempts = maxAttempts;
        this.useEta = useEta;
    }

    @Deprecated
    public JobCheckOptions(ScheduledExecutorService scheduler, int intervalMillis, int initialDelayMillis, int maxAttempts, boolean useEta) {
        this.scheduler = scheduler;
        this.interval = RequestInterval.constant(intervalMillis);
        this.initialDelayMillis = initialDelayMillis;
        this.maxAttempts = maxAttempts;
        this.useEta = useEta;
    }

    @Deprecated
    public JobCheckOptions(ScheduledExecutorService scheduler, int intervalMillis, int initialDelayMillis, int maxAttempts) {
        this(scheduler, intervalMillis, initialDelayMillis, maxAttempts, false);
    }

    public static JobCheckOptions create(ScheduledExecutorService scheduler) {
        return new JobCheckOptions(scheduler, 1000, 2000, 10, false);
    }

    public static JobCheckOptions create() {
        return JobCheckOptions.create(Executors.newSingleThreadScheduledExecutor());
    }

    public JobCheckOptions withInterval(RequestInterval interval) {
        return new JobCheckOptions(this.scheduler, interval, this.initialDelayMillis, this.maxAttempts, this.useEta);
    }

    public JobCheckOptions withInitialDelay(int initialDelayMillis) {
        return new JobCheckOptions(this.scheduler, this.interval, initialDelayMillis, this.maxAttempts, this.useEta);
    }

    public JobCheckOptions withInitialDelay(int initialDelay, TimeUnit unit) {
        return new JobCheckOptions(this.scheduler, this.interval, (int)unit.toMillis(initialDelay), this.maxAttempts, this.useEta);
    }

    public JobCheckOptions withMaxAttempts(int maxAttempts) {
        return new JobCheckOptions(this.scheduler, this.interval, this.initialDelayMillis, maxAttempts, this.useEta);
    }

    public JobCheckOptions withUseEta() {
        return new JobCheckOptions(this.scheduler, this.interval, this.initialDelayMillis, this.maxAttempts, true);
    }

    @Override
    public ScheduledExecutorService scheduler() {
        return this.scheduler;
    }

    @Override
    public RequestInterval interval() {
        return this.interval;
    }

    @Override
    @Deprecated
    public int intervalMillis() {
        return this.interval.getInterval(1);
    }

    @Override
    public int initialDelayMillis() {
        return this.initialDelayMillis;
    }

    @Override
    public int maxAttempts() {
        return this.maxAttempts;
    }

    @Override
    public boolean useEta() {
        return this.useEta;
    }
}

