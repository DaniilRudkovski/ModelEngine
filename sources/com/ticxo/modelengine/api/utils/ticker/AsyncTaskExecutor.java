/*
 * Decompiled with CFR 0.152.
 */
package com.ticxo.modelengine.api.utils.ticker;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AsyncTaskExecutor {
    private final ExecutorService executor;
    private final List<CompletableFuture<Void>> futures = new ArrayList<CompletableFuture<Void>>();

    public AsyncTaskExecutor() {
        this.executor = Executors.newWorkStealingPool();
    }

    public AsyncTaskExecutor(int threads) {
        this.executor = Executors.newWorkStealingPool(threads);
    }

    public CompletableFuture<Void> run(Runnable runnable) {
        CompletableFuture<Void> future = CompletableFuture.runAsync(runnable, this.executor);
        this.futures.add(future);
        return future;
    }

    public void join() {
        CompletableFuture.allOf((CompletableFuture[])this.futures.toArray(CompletableFuture[]::new)).join();
        this.futures.clear();
    }
}

