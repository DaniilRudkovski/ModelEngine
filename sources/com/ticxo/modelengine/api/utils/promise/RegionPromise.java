/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.Location
 *  org.bukkit.plugin.Plugin
 */
package com.ticxo.modelengine.api.utils.promise;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.utils.promise.AbstractFoliaPromise;
import com.ticxo.modelengine.api.utils.promise.AbstractPromise;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;

public class RegionPromise<T>
extends AbstractFoliaPromise<T> {
    private final Location location;

    private RegionPromise(Location location) {
        this.location = location;
    }

    private RegionPromise(Location location, T val) {
        super(val);
        this.location = location;
    }

    @Override
    protected <U> AbstractPromise<U> createEmpty() {
        return RegionPromise.empty(this.location);
    }

    @Override
    protected void executeSync(Runnable runnable) {
        Bukkit.getRegionScheduler().execute((Plugin)ModelEngineAPI.getAPI(), this.location, runnable);
    }

    @Override
    protected void executeSync(Runnable runnable, int delay) {
        Bukkit.getRegionScheduler().runDelayed((Plugin)ModelEngineAPI.getAPI(), this.location, task -> runnable.run(), (long)delay);
    }

    static <T> RegionPromise<T> empty(Location location) {
        return new RegionPromise<T>(location);
    }

    static <T> RegionPromise<T> completed(Location location, T val) {
        return new RegionPromise<T>(location, val);
    }
}

