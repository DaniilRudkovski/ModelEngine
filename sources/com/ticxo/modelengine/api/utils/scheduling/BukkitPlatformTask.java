/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.scheduler.BukkitTask
 */
package com.ticxo.modelengine.api.utils.scheduling;

import com.ticxo.modelengine.api.utils.scheduling.PlatformTask;
import org.bukkit.scheduler.BukkitTask;

public class BukkitPlatformTask
implements PlatformTask {
    protected BukkitTask task;

    public BukkitPlatformTask(BukkitTask task) {
        this.task = task;
    }

    @Override
    public void cancel() {
        this.task.cancel();
    }
}

