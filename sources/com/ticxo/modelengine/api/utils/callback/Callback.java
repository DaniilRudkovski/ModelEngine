/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 */
package com.ticxo.modelengine.api.utils.callback;

import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

public class Callback<T> {
    private final Function<Collection<T>, T> invoker;
    private final Map<UUID, T> callbacks = Maps.newConcurrentMap();

    public Callback(Function<Collection<T>, T> invoker) {
        this.invoker = invoker;
    }

    public UUID subscribe(T callback) {
        return this.subscribe(UUID.randomUUID(), callback);
    }

    public UUID subscribe(UUID uuid, T callback) {
        this.callbacks.put(uuid, callback);
        return uuid;
    }

    public void unsubscribe(UUID uuid) {
        this.callbacks.remove(uuid);
    }

    public T invoker() {
        return this.invoker.apply(this.callbacks.values());
    }
}

