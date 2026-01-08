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
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class LegacyPromise<T>
extends AbstractPromise<T> {
    private LegacyPromise() {
    }

    private LegacyPromise(T val) {
        super(val);
    }

    @Override
    protected <U> AbstractPromise<U> createEmpty() {
        return LegacyPromise.empty();
    }

    @Override
    protected void executeSync(Runnable runnable) {
        if (Bukkit.isPrimaryThread()) {
            runnable.run();
        } else {
            Bukkit.getScheduler().runTask((Plugin)ModelEngineAPI.getAPI(), runnable);
        }
    }

    @Override
    protected void executeSync(Runnable runnable, int delay) {
        Bukkit.getScheduler().runTaskLater((Plugin)ModelEngineAPI.getAPI(), runnable, (long)delay);
    }

    @Override
    protected void executeAsync(Runnable runnable) {
        if (Bukkit.isPrimaryThread()) {
            Bukkit.getScheduler().runTaskAsynchronously((Plugin)ModelEngineAPI.getAPI(), runnable);
        } else {
            runnable.run();
        }
    }

    @Override
    protected void executeAsync(Runnable runnable, int delay) {
        Bukkit.getScheduler().runTaskLaterAsynchronously((Plugin)ModelEngineAPI.getAPI(), runnable, (long)delay);
    }

    static <T> LegacyPromise<T> empty() {
        return new LegacyPromise<T>();
    }

    static <T> LegacyPromise<T> completed(T val) {
        return new LegacyPromise<T>(val);
    }
}

