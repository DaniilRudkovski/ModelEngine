/*
 * Decompiled with CFR 0.152.
 */
package org.mineskin.request.backoff;

import org.mineskin.request.backoff.RequestInterval;

public final class ExponentialBackoff
implements RequestInterval {
    private final int initialDelayMillis;
    private final int maxDelayMillis;
    private final double multiplier;
    private final int freeAttempts;

    ExponentialBackoff(int initialDelayMillis, int maxDelayMillis, double multiplier, int freeAttempts) {
        this.initialDelayMillis = initialDelayMillis;
        this.maxDelayMillis = maxDelayMillis;
        this.multiplier = multiplier;
        this.freeAttempts = freeAttempts;
    }

    public ExponentialBackoff withInitialDelay(int initialDelayMillis) {
        return new ExponentialBackoff(initialDelayMillis, this.maxDelayMillis, this.multiplier, this.freeAttempts);
    }

    public ExponentialBackoff withMaxDelay(int maxDelayMillis) {
        return new ExponentialBackoff(this.initialDelayMillis, maxDelayMillis, this.multiplier, this.freeAttempts);
    }

    public ExponentialBackoff withMultiplier(double multiplier) {
        return new ExponentialBackoff(this.initialDelayMillis, this.maxDelayMillis, multiplier, this.freeAttempts);
    }

    public ExponentialBackoff withFreeAttempts(int freeAttempts) {
        return new ExponentialBackoff(this.initialDelayMillis, this.maxDelayMillis, this.multiplier, freeAttempts);
    }

    @Override
    public int getInterval(int attempt) {
        if (attempt <= this.freeAttempts) {
            return this.initialDelayMillis;
        }
        double delay = (double)this.initialDelayMillis * Math.pow(this.multiplier, Math.max(0, attempt - this.freeAttempts));
        return (int)Math.min(delay, (double)this.maxDelayMillis);
    }

    public int initialDelayMillis() {
        return this.initialDelayMillis;
    }

    public int maxDelayMillis() {
        return this.maxDelayMillis;
    }

    public double multiplier() {
        return this.multiplier;
    }

    public int freeAttempts() {
        return this.freeAttempts;
    }
}

