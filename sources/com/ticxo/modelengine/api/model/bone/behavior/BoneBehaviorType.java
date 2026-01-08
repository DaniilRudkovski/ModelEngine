/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonDeserializer
 *  lombok.Generated
 *  org.jetbrains.annotations.Nullable
 */
package com.ticxo.modelengine.api.model.bone.behavior;

import com.google.gson.JsonDeserializer;
import com.ticxo.modelengine.api.error.ErrorMissingBoneBehaviorData;
import com.ticxo.modelengine.api.error.ErrorWrongBoneBehaviorDataType;
import com.ticxo.modelengine.api.generator.blueprint.BlueprintBone;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.bone.ModelBone;
import com.ticxo.modelengine.api.model.bone.behavior.BoneBehavior;
import com.ticxo.modelengine.api.model.bone.behavior.BoneBehaviorData;
import com.ticxo.modelengine.api.model.bone.behavior.ProceduralType;
import com.ticxo.modelengine.api.model.bone.manager.BehaviorManager;
import com.ticxo.modelengine.api.model.bone.render.DefaultRenderType;
import com.ticxo.modelengine.api.model.bone.render.IRenderType;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import lombok.Generated;
import org.jetbrains.annotations.Nullable;

public class BoneBehaviorType<T extends BoneBehavior> {
    private final BehaviorProvider<T> behaviorProvider;
    private final BehaviorManagerProvider<T> behaviorManagerProvider;
    private final String id;
    private final Map<String, Class<?>> requiredArguments;
    private final Map<String, Class<?>> optionalArguments;
    private final Map<Class<?>, JsonDeserializer<?>> dataDeserializer;
    private final IRenderType renderType;
    private final Set<ProceduralType> proceduralTypes;
    private final Predicate<Set<BoneBehaviorType<?>>> predicate;
    private final BehaviorProvider<T> forcedBehaviorProvider;
    private final boolean ignoreCubes;
    private final boolean pivot;

    public void assignCachedProvider(BlueprintBone bone, Map<String, Object> data) {
        Object value;
        Class<?> clazz;
        String key;
        HashMap<String, Object> verifiedData = new HashMap<String, Object>();
        for (Map.Entry<String, Class<?>> entry : this.requiredArguments.entrySet()) {
            key = entry.getKey();
            clazz = entry.getValue();
            value = data.get(key);
            if (value == null) {
                new ErrorMissingBoneBehaviorData(bone.getName(), this, key);
                return;
            }
            if (!clazz.isAssignableFrom(value.getClass())) {
                new ErrorWrongBoneBehaviorDataType(bone.getName(), this, key, clazz, value.getClass());
                return;
            }
            verifiedData.put(key, value);
        }
        for (Map.Entry<String, Class<?>> entry : this.optionalArguments.entrySet()) {
            key = entry.getKey();
            clazz = entry.getValue();
            value = data.get(key);
            if (value == null) continue;
            if (!clazz.isAssignableFrom(value.getClass())) {
                new ErrorWrongBoneBehaviorDataType(bone.getName(), this, key, clazz, value.getClass());
                return;
            }
            verifiedData.put(key, value);
        }
        bone.getCachedBehaviorProvider().put(this, new CachedProvider<T>(this.behaviorProvider, this, new BoneBehaviorData(verifiedData)));
    }

    public void assignForcedCachedProvider(BlueprintBone bone) {
        if (this.forcedBehaviorProvider == null) {
            return;
        }
        if (!bone.getCachedBehaviorProvider().containsKey(this)) {
            bone.getCachedBehaviorProvider().put(this, new CachedProvider<T>(this.forcedBehaviorProvider, this, new BoneBehaviorData(new HashMap<String, Object>())));
        }
    }

    public boolean test(Set<BoneBehaviorType<?>> types) {
        return this.predicate.test(types);
    }

    public static boolean noProcedural(Set<BoneBehaviorType<?>> types) {
        for (BoneBehaviorType<?> type : types) {
            if (type.getProceduralTypes().isEmpty()) continue;
            return false;
        }
        return true;
    }

    @Generated
    public BehaviorProvider<T> getBehaviorProvider() {
        return this.behaviorProvider;
    }

    @Generated
    public BehaviorManagerProvider<T> getBehaviorManagerProvider() {
        return this.behaviorManagerProvider;
    }

    @Generated
    public String getId() {
        return this.id;
    }

    @Generated
    public Map<String, Class<?>> getRequiredArguments() {
        return this.requiredArguments;
    }

    @Generated
    public Map<String, Class<?>> getOptionalArguments() {
        return this.optionalArguments;
    }

    @Generated
    public Map<Class<?>, JsonDeserializer<?>> getDataDeserializer() {
        return this.dataDeserializer;
    }

    @Generated
    public IRenderType getRenderType() {
        return this.renderType;
    }

    @Generated
    public Set<ProceduralType> getProceduralTypes() {
        return this.proceduralTypes;
    }

    @Generated
    public Predicate<Set<BoneBehaviorType<?>>> getPredicate() {
        return this.predicate;
    }

    @Generated
    public BehaviorProvider<T> getForcedBehaviorProvider() {
        return this.forcedBehaviorProvider;
    }

    @Generated
    public boolean isIgnoreCubes() {
        return this.ignoreCubes;
    }

    @Generated
    public boolean isPivot() {
        return this.pivot;
    }

    @Generated
    protected BoneBehaviorType(BehaviorProvider<T> behaviorProvider, BehaviorManagerProvider<T> behaviorManagerProvider, String id, Map<String, Class<?>> requiredArguments, Map<String, Class<?>> optionalArguments, Map<Class<?>, JsonDeserializer<?>> dataDeserializer, IRenderType renderType, Set<ProceduralType> proceduralTypes, Predicate<Set<BoneBehaviorType<?>>> predicate, BehaviorProvider<T> forcedBehaviorProvider, boolean ignoreCubes, boolean pivot) {
        this.behaviorProvider = behaviorProvider;
        this.behaviorManagerProvider = behaviorManagerProvider;
        this.id = id;
        this.requiredArguments = requiredArguments;
        this.optionalArguments = optionalArguments;
        this.dataDeserializer = dataDeserializer;
        this.renderType = renderType;
        this.proceduralTypes = proceduralTypes;
        this.predicate = predicate;
        this.forcedBehaviorProvider = forcedBehaviorProvider;
        this.ignoreCubes = ignoreCubes;
        this.pivot = pivot;
    }

    public static class CachedProvider<T extends BoneBehavior> {
        private final BehaviorProvider<T> behaviorProvider;
        private final BoneBehaviorType<T> type;
        private final BoneBehaviorData data;

        public T create(ModelBone bone) {
            return this.behaviorProvider.create(bone, this.type, this.data);
        }

        @Generated
        public CachedProvider(BehaviorProvider<T> behaviorProvider, BoneBehaviorType<T> type, BoneBehaviorData data) {
            this.behaviorProvider = behaviorProvider;
            this.type = type;
            this.data = data;
        }

        @Generated
        public BoneBehaviorType<T> getType() {
            return this.type;
        }

        @Generated
        public BoneBehaviorData getData() {
            return this.data;
        }
    }

    @FunctionalInterface
    public static interface BehaviorProvider<T extends BoneBehavior> {
        public T create(ModelBone var1, BoneBehaviorType<T> var2, BoneBehaviorData var3);
    }

    @FunctionalInterface
    public static interface BehaviorManagerProvider<T extends BoneBehavior> {
        public BehaviorManager<T> create(ActiveModel var1, BoneBehaviorType<T> var2);
    }

    public static class Builder<T extends BoneBehavior> {
        private final BehaviorProvider<T> behaviorProvider;
        private final BehaviorManagerProvider<T> behaviorManagerProvider;
        private final String id;
        private final Map<String, Class<?>> requiredArguments = new HashMap();
        private final Map<String, Class<?>> optionalArguments = new HashMap();
        private final Map<Class<?>, JsonDeserializer<?>> dataDeserializer = new HashMap();
        private final Set<ProceduralType> proceduralTypes = new HashSet<ProceduralType>();
        private IRenderType renderType = DefaultRenderType.ANY;
        private Predicate<Set<BoneBehaviorType<?>>> predicate = boneBehaviorTypes -> true;
        private BehaviorProvider<T> forcedBehaviorProvider;
        private boolean ignoreCubes;
        private boolean pivot;

        public static <T extends BoneBehavior> Builder<T> of(BehaviorProvider<T> behaviorProvider, @Nullable BehaviorManagerProvider<T> behaviorManagerProvider, String id) {
            return new Builder<T>(behaviorProvider, behaviorManagerProvider, id);
        }

        public Builder<T> required(String key, Class<?> type) {
            this.requiredArguments.put(key, type);
            return this;
        }

        public <S> Builder<T> required(String key, Class<S> type, JsonDeserializer<S> deserializer) {
            this.requiredArguments.put(key, type);
            this.dataDeserializer.put(type, deserializer);
            return this;
        }

        public Builder<T> optional(String key, Class<?> type) {
            this.optionalArguments.put(key, type);
            return this;
        }

        public <S> Builder<T> optional(String key, Class<S> type, JsonDeserializer<S> deserializer) {
            this.optionalArguments.put(key, type);
            this.dataDeserializer.put(type, deserializer);
            return this;
        }

        public Builder<T> renderType(IRenderType type) {
            this.renderType = type;
            return this;
        }

        public Builder<T> procedural(ProceduralType ... types) {
            this.proceduralTypes.addAll(Arrays.asList(types));
            return this;
        }

        public Builder<T> predicate(Predicate<Set<BoneBehaviorType<?>>> predicate) {
            this.predicate = predicate;
            return this;
        }

        public Builder<T> forced(BehaviorProvider<T> forcedBehaviorProvider) {
            this.forcedBehaviorProvider = forcedBehaviorProvider;
            return this;
        }

        public Builder<T> ignoreCubes() {
            this.ignoreCubes = true;
            return this;
        }

        public Builder<T> pivot() {
            this.pivot = true;
            return this;
        }

        public BoneBehaviorType<T> build() {
            return new BoneBehaviorType<T>(this.behaviorProvider, this.behaviorManagerProvider, this.id, this.requiredArguments, this.optionalArguments, this.dataDeserializer, this.renderType, this.proceduralTypes, this.predicate, this.forcedBehaviorProvider, this.ignoreCubes, this.pivot);
        }

        @Generated
        protected Builder(BehaviorProvider<T> behaviorProvider, BehaviorManagerProvider<T> behaviorManagerProvider, String id) {
            this.behaviorProvider = behaviorProvider;
            this.behaviorManagerProvider = behaviorManagerProvider;
            this.id = id;
        }
    }
}

