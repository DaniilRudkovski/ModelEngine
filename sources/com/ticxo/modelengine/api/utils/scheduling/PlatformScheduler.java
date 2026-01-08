/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.entity.Entity
 *  org.bukkit.plugin.Plugin
 */
package com.ticxo.modelengine.api.utils.scheduling;

import com.ticxo.modelengine.api.utils.scheduling.PlatformTask;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;

public interface PlatformScheduler {
    public PlatformTask scheduleRepeating(Plugin var1, Runnable var2, long var3, long var5);

    public PlatformTask scheduleRepeating(Plugin var1, Entity var2, Runnable var3, long var4, long var6);

    public PlatformTask scheduleRepeating(Plugin var1, Location var2, Runnable var3, long var4, long var6);

    public PlatformTask scheduleRepeatingAsync(Plugin var1, Runnable var2, long var3, long var5);
}

