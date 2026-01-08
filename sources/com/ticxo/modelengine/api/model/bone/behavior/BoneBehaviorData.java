/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.ticxo.modelengine.api.model.bone.behavior;

import java.util.Map;
import lombok.Generated;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BoneBehaviorData {
    private final Map<String, Object> data;

    @Nullable
    public <T> T get(String key) {
        try {
            return (T)this.data.get(key);
        }
        catch (ClassCastException e) {
            e.printStackTrace();
            return null;
        }
    }

    @NotNull
    public <T> T get(String key, @NotNull T def) {
        Class<?> valClass;
        Object value = this.data.get(key);
        if (value == null) {
            return def;
        }
        Class<?> defClass = def.getClass();
        if (defClass.isAssignableFrom(valClass = value.getClass())) {
            return (T)value;
        }
        new ClassCastException(String.format("Could not cast %s to %s. Returning default value.", valClass.getSimpleName(), defClass.getSimpleName())).printStackTrace();
        return def;
    }

    @Generated
    public BoneBehaviorData(Map<String, Object> data) {
        this.data = data;
    }
}

