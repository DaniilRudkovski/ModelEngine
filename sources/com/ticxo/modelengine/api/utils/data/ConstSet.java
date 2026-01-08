/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 */
package com.ticxo.modelengine.api.utils.data;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import org.jetbrains.annotations.NotNull;

public class ConstSet<T>
implements Set<T> {
    private final HashMap<Integer, T> map = new HashMap();

    @Override
    public int size() {
        return this.map.size();
    }

    @Override
    public boolean isEmpty() {
        return this.map.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return this.map.containsKey(o.hashCode());
    }

    @Override
    @NotNull
    public Iterator<T> iterator() {
        return this.map.values().iterator();
    }

    @Override
    @NotNull
    public Object[] toArray() {
        return this.map.values().toArray();
    }

    @Override
    @NotNull
    public <T1> T1[] toArray(@NotNull T1[] t1s) {
        return this.map.values().toArray(t1s);
    }

    @Override
    public boolean add(T t) {
        return this.map.put(t.hashCode(), t) == null;
    }

    @Override
    public boolean remove(Object o) {
        return this.map.remove(o.hashCode()) != null;
    }

    @Override
    public boolean containsAll(@NotNull Collection<?> collection) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(@NotNull Collection<? extends T> collection) {
        collection.forEach(this::add);
        return true;
    }

    @Override
    public boolean retainAll(@NotNull Collection<?> collection) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(@NotNull Collection<?> collection) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        this.map.clear();
    }

    public T fromHash(int hash) {
        return this.map.get(hash);
    }

    public String toString() {
        return this.map.values().toString();
    }
}

