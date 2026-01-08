/*
 * Decompiled with CFR 0.152.
 */
package com.ticxo.modelengine.api.utils.registry;

import com.ticxo.modelengine.api.utils.registry.TRegistry;

public abstract class TUnaryRegistry<T>
extends TRegistry<T, T> {
    @Override
    public T get(String id) {
        if (id == null || !this.registry.containsKey(id)) {
            return this.getDefault();
        }
        return (T)this.registry.get(id);
    }

    @Override
    public T getDefault() {
        return (T)this.defaultItem;
    }

    @Override
    protected T convert(T item) {
        return item;
    }
}

