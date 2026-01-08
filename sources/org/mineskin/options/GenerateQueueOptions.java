/*
 * Decompiled with CFR 0.152.
 */
package org.mineskin.options;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import org.mineskin.QueueOptions;
import org.mineskin.options.AutoGenerateQueueOptions;

public abstract class GenerateQueueOptions {
    public static QueueOptions create(ScheduledExecutorService scheduler) {
        return new QueueOptions(scheduler, 200, 1);
    }

    public static QueueOptions create() {
        return GenerateQueueOptions.create(Executors.newSingleThreadScheduledExecutor());
    }

    public static AutoGenerateQueueOptions createAuto(ScheduledExecutorService scheduler) {
        return new AutoGenerateQueueOptions(scheduler);
    }

    public static AutoGenerateQueueOptions createAuto() {
        return GenerateQueueOptions.createAuto(Executors.newSingleThreadScheduledExecutor());
    }
}

