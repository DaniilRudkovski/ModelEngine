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

import com.ticxo.modelengine.api.utils.scheduling.FoliaPlatformTask;
import com.ticxo.modelengine.api.utils.scheduling.PlatformScheduler;
import com.ticxo.modelengine.api.utils.scheduling.PlatformTask;
import java.util.concurrent.TimeUnit;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;

public class FoliaPlatformScheduler
implements PlatformScheduler {
    @Override
    public PlatformTask scheduleRepeating(Plugin plugin, Runnable task, long delay, long period) {
        return new FoliaPlatformTask(Bukkit.getGlobalRegionScheduler().runAtFixedRate(plugin, ignore -> task.run(), delay, period));
    }

    @Override
    public PlatformTask scheduleRepeating(Plugin plugin, Entity entity, Runnable task, long delay, long period) {
        return new FoliaPlatformTask(entity.getScheduler().runAtFixedRate(plugin, ignore -> task.run(), null, delay, period));
    }

    @Override
    public PlatformTask scheduleRepeating(Plugin plugin, Location location, Runnable task, long delay, long period) {
        return new FoliaPlatformTask(Bukkit.getRegionScheduler().runAtFixedRate(plugin, location, ignore -> task.run(), delay, period));
    }

    @Override
    public PlatformTask scheduleRepeatingAsync(Plugin plugin, Runnable task, long delay, long period) {
        return new FoliaPlatformTask(Bukkit.getAsyncScheduler().runAtFixedRate(plugin, ignore -> task.run(), delay * 50L, period * 50L, TimeUnit.MILLISECONDS));
    }
}

