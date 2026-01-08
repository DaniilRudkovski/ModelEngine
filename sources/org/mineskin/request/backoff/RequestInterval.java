/*
 * Decompiled with CFR 0.152.
 */
package org.mineskin.request.backoff;

import org.mineskin.request.backoff.ExponentialBackoff;

public interface RequestInterval {
    public int getInterval(int var1);

    public static RequestInterval constant(int intervalMillis) {
        return attempt -> intervalMillis;
    }

    public static ExponentialBackoff exponential() {
        return new ExponentialBackoff(200, 2000, 2.0, 3);
    }
}

