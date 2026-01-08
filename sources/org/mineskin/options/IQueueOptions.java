/*
 * Decompiled with CFR 0.152.
 */
package org.mineskin.options;

import java.util.concurrent.ScheduledExecutorService;

public interface IQueueOptions {
    public ScheduledExecutorService scheduler();

    public int intervalMillis();

    public int concurrency();
}

