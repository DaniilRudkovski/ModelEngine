/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 */
package com.ticxo.modelengine.api.utils.data.tracker;

import com.ticxo.modelengine.api.utils.data.tracker.DataTracker;
import java.util.Collection;
import java.util.Iterator;
import org.jetbrains.annotations.NotNull;

public class CollectionDataTracker<T>
extends DataTracker<Collection<T>>
implements Collection<T> {
    public CollectionDataTracker(Collection<T> value) {
        super(value);
    }

    @Override
    public int size() {
        return ((Collection)this.value).size();
    }

    @Override
    public boolean isEmpty() {
        return ((Collection)this.value).isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return ((Collection)this.value).contains(o);
    }

    @Override
    @NotNull
    public Iterator<T> iterator() {
        return ((Collection)this.value).iterator();
    }

    @Override
    @NotNull
    public Object[] toArray() {
        return ((Collection)this.value).toArray();
    }

    @Override
    @NotNull
    public <T1> T1[] toArray(@NotNull T1[] a) {
        return ((Collection)this.value).toArray(a);
    }

    @Override
    public boolean add(T t) {
        boolean flag = ((Collection)this.value).add(t);
        this.isDirty |= flag;
        return flag;
    }

    @Override
    public boolean remove(Object o) {
        boolean flag = ((Collection)this.value).remove(o);
        this.isDirty |= flag;
        return flag;
    }

    @Override
    public boolean containsAll(@NotNull Collection<?> c2) {
        return ((Collection)this.value).containsAll(c2);
    }

    @Override
    public boolean addAll(@NotNull Collection<? extends T> c2) {
        boolean flag = ((Collection)this.value).addAll(c2);
        this.isDirty |= flag;
        return flag;
    }

    @Override
    public boolean removeAll(@NotNull Collection<?> c2) {
        boolean flag = ((Collection)this.value).removeAll(c2);
        this.isDirty |= flag;
        return flag;
    }

    @Override
    public boolean retainAll(@NotNull Collection<?> c2) {
        boolean flag = ((Collection)this.value).retainAll(c2);
        this.isDirty |= flag;
        return flag;
    }

    @Override
    public void clear() {
        if (((Collection)this.value).isEmpty()) {
            return;
        }
        ((Collection)this.value).clear();
        this.isDirty = true;
    }
}

