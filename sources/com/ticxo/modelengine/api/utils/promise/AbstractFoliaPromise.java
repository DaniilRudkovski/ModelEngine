/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.plugin.Plugin
 */
package com.ticxo.modelengine.api.utils.promise;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.utils.promise.AbstractPromise;
import java.util.concurrent.TimeUnit;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public abstract class AbstractFoliaPromise<T>
extends AbstractPromise<T> {
    protected AbstractFoliaPromise() {
    }

    protected AbstractFoliaPromise(T val) {
        super(val);
    }

    @Override
    protected final void executeAsync(Runnable runnable) {
        Bukkit.getAsyncScheduler().runNow((Plugin)ModelEngineAPI.getAPI(), task -> runnable.run());
    }

    @Override
    protected final void executeAsync(Runnable runnable, int delay) {
        Bukkit.getAsyncScheduler().runDelayed((Plugin)ModelEngineAPI.getAPI(), task -> runnable.run(), (long)(delay * 50), TimeUnit.MILLISECONDS);
    }
}

