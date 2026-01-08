/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  org.jetbrains.annotations.Nullable
 */
package com.ticxo.modelengine.api.model.bone.behavior;

import com.google.common.collect.ImmutableSet;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ticxo.modelengine.api.model.bone.behavior.BoneBehaviorType;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.jetbrains.annotations.Nullable;

public class BoneBehaviorRegistry {
    private final Map<String, BoneBehaviorType<?>> idRegistry = new HashMap();
    private Gson gson;

    public void register(BoneBehaviorType<?> type) {
        this.idRegistry.put(type.getId(), type);
    }

    @Nullable
    public BoneBehaviorType<?> getById(String id) {
        return this.idRegistry.get(id);
    }

    public Set<String> getIds() {
        return ImmutableSet.copyOf(this.idRegistry.keySet());
    }

    public Gson getGson() {
        if (this.gson == null) {
            GsonBuilder builder = new GsonBuilder();
            this.idRegistry.forEach((s, boneBehaviorType) -> boneBehaviorType.getDataDeserializer().forEach((arg_0, arg_1) -> ((GsonBuilder)builder).registerTypeAdapter(arg_0, arg_1)));
            this.gson = builder.create();
        }
        return this.gson;
    }
}

