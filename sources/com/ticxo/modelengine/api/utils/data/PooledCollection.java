/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 *  org.jetbrains.annotations.Nullable
 */
package com.ticxo.modelengine.api.utils.data;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;
import lombok.Generated;
import org.jetbrains.annotations.Nullable;

public class PooledCollection<T> {
    private final Supplier<T> instanceSupplier;
    private final Queue<T> available = new LinkedList<T>();
    private final Set<T> inUse = new HashSet<T>();
    private final Set<T> created = new HashSet<T>();
    private final Set<T> released = new HashSet<T>();

    public T getOrCreate() {
        T instance = this.available.poll();
        if (instance == null) {
            instance = this.instanceSupplier.get();
            this.created.add(instance);
        }
        this.inUse.add(instance);
        this.released.remove(instance);
        return instance;
    }

    public void release(T instance) {
        if (this.inUse.remove(instance)) {
            this.available.add(instance);
            this.released.add(instance);
        }
    }

    public void releaseAll() {
        this.released.addAll(this.inUse);
        this.released.addAll(this.created);
        this.available.addAll(this.inUse);
        this.available.addAll(this.created);
        this.inUse.clear();
        this.created.clear();
    }

    public Set<T> getAll() {
        HashSet<T> set = new HashSet<T>(this.available);
        set.addAll(this.inUse);
        return set;
    }

    public void processAll(@Nullable Consumer<T> createdConsumer, @Nullable Consumer<T> oldConsumer, @Nullable Consumer<T> releasedConsumer) {
        if (createdConsumer != null || oldConsumer != null) {
            for (T t : this.inUse) {
                if (this.created.contains(t)) {
                    if (createdConsumer == null) continue;
                    createdConsumer.accept(t);
                    continue;
                }
                if (oldConsumer == null) continue;
                oldConsumer.accept(t);
            }
        }
        if (releasedConsumer != null) {
            for (T t : this.released) {
                releasedConsumer.accept(t);
            }
        }
    }

    @Generated
    public PooledCollection(Supplier<T> instanceSupplier) {
        this.instanceSupplier = instanceSupplier;
    }

    @Generated
    public Set<T> getInUse() {
        return this.inUse;
    }

    @Generated
    public Set<T> getCreated() {
        return this.created;
    }

    @Generated
    public Set<T> getReleased() {
        return this.released;
    }
}

