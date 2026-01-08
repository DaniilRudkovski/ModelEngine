/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 */
package com.ticxo.modelengine.api.utils.data;

import java.util.HashSet;
import java.util.Set;
import lombok.Generated;

public class UpdateScheme<T> {
    private final Set<T> added = new HashSet<T>();
    private final Set<T> updated = new HashSet<T>();
    private final Set<T> removed = new HashSet<T>();

    public void addUpdated(T element) {
        this.updated.add(element);
    }

    public void addAdded(T element) {
        this.added.add(element);
        this.removed.remove(element);
        this.updated.remove(element);
    }

    public void addRemove(T element) {
        this.removed.add(element);
        this.added.remove(element);
        this.updated.remove(element);
    }

    public boolean hasUpdate() {
        return !this.added.isEmpty() || !this.updated.isEmpty() || !this.removed.isEmpty();
    }

    public Mode getUpdateMode(T element) {
        if (this.added.contains(element)) {
            return Mode.ADD;
        }
        if (this.updated.contains(element)) {
            return Mode.UPDATE;
        }
        if (this.removed.contains(element)) {
            return Mode.REMOVE;
        }
        return Mode.NONE;
    }

    public void reset() {
        this.added.clear();
        this.updated.clear();
        this.removed.clear();
    }

    @Generated
    public Set<T> getAdded() {
        return this.added;
    }

    @Generated
    public Set<T> getUpdated() {
        return this.updated;
    }

    @Generated
    public Set<T> getRemoved() {
        return this.removed;
    }

    public static enum Mode {
        NONE,
        UPDATE,
        ADD,
        REMOVE;

    }
}

