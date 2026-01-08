/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.papermc.paper.threadedregions.scheduler.ScheduledTask
 */
package com.ticxo.modelengine.api.utils.scheduling;

import com.ticxo.modelengine.api.utils.scheduling.PlatformTask;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;

public class FoliaPlatformTask
implements PlatformTask {
    protected ScheduledTask task;

    public FoliaPlatformTask(ScheduledTask task) {
        this.task = task;
    }

    @Override
    public void cancel() {
        this.task.cancel();
    }
}

