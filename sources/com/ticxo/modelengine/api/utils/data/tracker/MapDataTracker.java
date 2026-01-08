/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.ticxo.modelengine.api.utils.data.tracker;

import com.ticxo.modelengine.api.utils.data.tracker.DataTracker;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MapDataTracker<T, U>
extends DataTracker<Map<T, U>>
implements Map<T, U> {
    public MapDataTracker(Map<T, U> value) {
        super(value);
    }

    @Override
    public void set(Map<T, U> map, Runnable ifDirty) {
        if (this.equal.test((Map)this.value, map)) {
            return;
        }
        this.isDirty = true;
        ((Map)this.value).clear();
        ((Map)this.value).putAll(map);
        if (ifDirty != null) {
            ifDirty.run();
        }
    }

    @Override
    public int size() {
        return ((Map)this.value).size();
    }

    @Override
    public boolean isEmpty() {
        return ((Map)this.value).isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return ((Map)this.value).containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return ((Map)this.value).containsValue(value);
    }

    @Override
    public U get(Object key) {
        return (U)((Map)this.value).get(key);
    }

    @Override
    @Nullable
    public U put(T key, U value) {
        U prev = ((Map)this.value).put(key, value);
        this.isDirty |= prev != value;
        return prev;
    }

    @Override
    public U remove(Object key) {
        if (!this.isDirty) {
            this.isDirty = ((Map)this.value).containsKey(key);
        }
        return (U)((Map)this.value).remove(key);
    }

    @Override
    public void putAll(@NotNull Map<? extends T, ? extends U> m) {
        if (!this.isDirty) {
            for (Map.Entry<T, U> entry : m.entrySet()) {
                Object t = ((Map)this.value).get(entry.getKey());
                if (t != null && t == entry.getValue()) continue;
                this.isDirty = true;
                break;
            }
        }
        ((Map)this.value).putAll(m);
    }

    @Override
    public void clear() {
        if (((Map)this.value).isEmpty()) {
            return;
        }
        ((Map)this.value).clear();
        this.isDirty = true;
    }

    @Override
    @NotNull
    public Set<T> keySet() {
        return ((Map)this.value).keySet();
    }

    @Override
    @NotNull
    public Collection<U> values() {
        return ((Map)this.value).values();
    }

    @Override
    @NotNull
    public Set<Map.Entry<T, U>> entrySet() {
        return ((Map)this.value).entrySet();
    }
}

