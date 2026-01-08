/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 */
package com.ticxo.modelengine.api.utils.ticker;

import java.util.function.Consumer;
import lombok.Generated;

public class Task {
    private final Consumer<Task> task;
    private final int startDelay;
    private final int interval;
    private final boolean isRepeating;
    private int delay;
    private int tick;
    private int runCount;
    private boolean canceled;

    public boolean tick() {
        if (this.delay++ < this.startDelay || this.tick-- > 0) {
            return this.canceled;
        }
        this.tick = this.interval;
        this.task.accept(this);
        ++this.runCount;
        return this.canceled || !this.isRepeating;
    }

    public void cancel() {
        this.canceled = true;
    }

    @Generated
    public Task(Consumer<Task> task, int startDelay, int interval, boolean isRepeating) {
        this.task = task;
        this.startDelay = startDelay;
        this.interval = interval;
        this.isRepeating = isRepeating;
    }

    @Generated
    public int getStartDelay() {
        return this.startDelay;
    }

    @Generated
    public int getInterval() {
        return this.interval;
    }

    @Generated
    public boolean isRepeating() {
        return this.isRepeating;
    }

    @Generated
    public int getDelay() {
        return this.delay;
    }

    @Generated
    public int getTick() {
        return this.tick;
    }

    @Generated
    public int getRunCount() {
        return this.runCount;
    }

    @Generated
    public boolean isCanceled() {
        return this.canceled;
    }
}

