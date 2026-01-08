/*
 * Decompiled with CFR 0.152.
 */
package org.mineskin;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.mineskin.options.AutoGenerateQueueOptions;
import org.mineskin.options.GenerateQueueOptions;
import org.mineskin.options.GetQueueOptions;
import org.mineskin.options.IQueueOptions;

public record QueueOptions(ScheduledExecutorService scheduler, int intervalMillis, int concurrency) implements IQueueOptions
{
    @Deprecated
    public static QueueOptions createGenerate(ScheduledExecutorService scheduler) {
        return GenerateQueueOptions.create(scheduler);
    }

    @Deprecated
    public static QueueOptions createGenerate() {
        return GenerateQueueOptions.create();
    }

    @Deprecated
    public static AutoGenerateQueueOptions createAutoGenerate(ScheduledExecutorService scheduler) {
        return GenerateQueueOptions.createAuto(scheduler);
    }

    @Deprecated
    public static AutoGenerateQueueOptions createAutoGenerate() {
        return GenerateQueueOptions.createAuto();
    }

    @Deprecated
    public static QueueOptions createGet(ScheduledExecutorService scheduler) {
        return GetQueueOptions.create(scheduler);
    }

    @Deprecated
    public static QueueOptions createGet() {
        return GetQueueOptions.create();
    }

    public QueueOptions withInterval(int interval, TimeUnit unit) {
        return new QueueOptions(this.scheduler, (int)unit.toMillis(interval), this.concurrency);
    }

    public QueueOptions withConcurrency(int concurrency) {
        return new QueueOptions(this.scheduler, this.intervalMillis, concurrency);
    }
}

