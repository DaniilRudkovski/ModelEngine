/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Entity
 *  org.bukkit.plugin.Plugin
 */
package com.ticxo.modelengine.api.utils.promise;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.utils.promise.AbstractFoliaPromise;
import com.ticxo.modelengine.api.utils.promise.AbstractPromise;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;

public class EntityPromise<T>
extends AbstractFoliaPromise<T> {
    private final Entity entity;

    private EntityPromise(Entity entity) {
        this.entity = entity;
    }

    private EntityPromise(Entity entity, T val) {
        super(val);
        this.entity = entity;
    }

    @Override
    protected <U> AbstractPromise<U> createEmpty() {
        return EntityPromise.empty(this.entity);
    }

    @Override
    protected void executeSync(Runnable runnable) {
        this.entity.getScheduler().execute((Plugin)ModelEngineAPI.getAPI(), runnable, null, 0L);
    }

    @Override
    protected void executeSync(Runnable runnable, int delay) {
        this.entity.getScheduler().execute((Plugin)ModelEngineAPI.getAPI(), runnable, null, (long)delay);
    }

    static <T> EntityPromise<T> empty(Entity entity) {
        return new EntityPromise<T>(entity);
    }

    static <T> EntityPromise<T> completed(Entity entity, T val) {
        return new EntityPromise<T>(entity, val);
    }
}

