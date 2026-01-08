/*
 * Decompiled with CFR 0.152.
 */
package com.ticxo.modelengine.api.utils.data;

import java.util.LinkedHashSet;

public class NullableHashSet<T>
extends LinkedHashSet<T> {
    @Override
    public boolean add(T t) {
        if (t == null) {
            return false;
        }
        return super.add(t);
    }
}

