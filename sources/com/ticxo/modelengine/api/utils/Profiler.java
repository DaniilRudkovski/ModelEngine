/*
 * Decompiled with CFR 0.152.
 */
package com.ticxo.modelengine.api.utils;

import java.util.LinkedList;
import java.util.Queue;

public class Profiler {
    private final Queue<Long> times;
    private final int maxSize;
    private long startTime;

    public Profiler(int maxSize) {
        this.maxSize = maxSize;
        this.times = new LinkedList<Long>();
    }

    public void startProfiling() {
        this.startTime = System.nanoTime();
    }

    public void stopProfiling() {
        if (this.startTime == 0L) {
            throw new IllegalStateException("Profiling has not been started.");
        }
        long endTime = System.nanoTime();
        long duration = endTime - this.startTime;
        this.addTime(duration);
        this.startTime = 0L;
    }

    private void addTime(long duration) {
        if (this.times.size() >= this.maxSize) {
            this.times.poll();
        }
        this.times.offer(duration);
    }

    public long getMinTime() {
        return this.times.stream().min(Long::compare).orElse(0L);
    }

    public long getMaxTime() {
        return this.times.stream().max(Long::compare).orElse(0L);
    }

    public double getAverageTime() {
        return this.times.stream().mapToLong(Long::longValue).average().orElse(0.0);
    }

    public int getSampleSize() {
        return this.times.size();
    }
}

