/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.Location
 *  org.bukkit.entity.Entity
 *  org.bukkit.plugin.Plugin
 */
package com.ticxo.modelengine.api.utils.scheduling;

import com.ticxo.modelengine.api.utils.scheduling.BukkitPlatformTask;
import com.ticxo.modelengine.api.utils.scheduling.PlatformScheduler;
import com.ticxo.modelengine.api.utils.scheduling.PlatformTask;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;

public class BukkitPlatformScheduler
implements PlatformScheduler {
    @Override
    public PlatformTask scheduleRepeating(Plugin plugin, Runnable task, long delay, long period) {
        return new BukkitPlatformTask(Bukkit.getScheduler().runTaskTimer(plugin, task, delay, period));
    }

    @Override
    public PlatformTask scheduleRepeating(Plugin plugin, Entity entity, Runnable task, long delay, long period) {
        return this.scheduleRepeating(plugin, task, delay, period);
    }

    @Override
    public PlatformTask scheduleRepeating(Plugin plugin, Location location, Runnable task, long delay, long period) {
        return this.scheduleRepeating(plugin, task, delay, period);
    }

    @Override
    public PlatformTask scheduleRepeatingAsync(Plugin plugin, Runnable task, long delay, long period) {
        return new BukkitPlatformTask(Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, task, delay, period));
    }
}

