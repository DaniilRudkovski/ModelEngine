/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.plugin.Plugin
 */
package com.ticxo.modelengine.api.utils.promise;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.utils.promise.AbstractFoliaPromise;
import com.ticxo.modelengine.api.utils.promise.AbstractPromise;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class GlobalPromise<T>
extends AbstractFoliaPromise<T> {
    private GlobalPromise() {
    }

    private GlobalPromise(T val) {
        super(val);
    }

    @Override
    protected <U> AbstractPromise<U> createEmpty() {
        return GlobalPromise.empty();
    }

    @Override
    protected void executeSync(Runnable runnable) {
        Bukkit.getGlobalRegionScheduler().execute((Plugin)ModelEngineAPI.getAPI(), runnable);
    }

    @Override
    protected void executeSync(Runnable runnable, int delay) {
        Bukkit.getGlobalRegionScheduler().runDelayed((Plugin)ModelEngineAPI.getAPI(), task -> runnable.run(), (long)delay);
    }

    static <T> GlobalPromise<T> empty() {
        return new GlobalPromise<T>();
    }

    static <T> GlobalPromise<T> completed(T val) {
        return new GlobalPromise<T>(val);
    }
}

