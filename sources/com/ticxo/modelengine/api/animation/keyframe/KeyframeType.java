/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 */
package com.ticxo.modelengine.api.animation.keyframe;

import com.ticxo.modelengine.api.animation.Timeline;
import com.ticxo.modelengine.api.animation.handler.AnimationHandler;
import com.ticxo.modelengine.api.animation.interpolator.KeyframeInterpolator;
import com.ticxo.modelengine.api.animation.keyframe.type.AbstractKeyframe;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.bone.ModelBone;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import lombok.Generated;

public class KeyframeType<KEY extends AbstractKeyframe<DATA>, DATA> {
    private final String id;
    private final Supplier<KEY> keyframeSupplier;
    private final Function<Timeline, KeyframeInterpolator<KEY, DATA>> interpolatorSupplier;
    private final Map<Class<?>, ModelUpdater> modelUpdaters;
    private final Map<Class<?>, BoneUpdater> boneUpdaters;
    private final boolean global;

    public KeyframeInterpolator<KEY, DATA> createInterpolator(Timeline animation) {
        return this.interpolatorSupplier.apply(animation);
    }

    public KEY createKeyframe() {
        return (KEY)((AbstractKeyframe)this.keyframeSupplier.get());
    }

    public void updateModel(Class<?> handlerClass, AnimationHandler handler, Object ... data) {
        ModelUpdater updater = this.modelUpdaters.get(handlerClass);
        if (updater == null) {
            return;
        }
        updater.update(handler, handler.getActiveModel(), data);
    }

    public void updateBone(Class<?> handlerClass, AnimationHandler handler, ModelBone bone, Object ... data) {
        BoneUpdater updater = this.boneUpdaters.get(handlerClass);
        if (updater == null) {
            return;
        }
        updater.update(handler, bone, data);
    }

    @Generated
    public String getId() {
        return this.id;
    }

    @Generated
    public Supplier<KEY> getKeyframeSupplier() {
        return this.keyframeSupplier;
    }

    @Generated
    public Function<Timeline, KeyframeInterpolator<KEY, DATA>> getInterpolatorSupplier() {
        return this.interpolatorSupplier;
    }

    @Generated
    public Map<Class<?>, ModelUpdater> getModelUpdaters() {
        return this.modelUpdaters;
    }

    @Generated
    public Map<Class<?>, BoneUpdater> getBoneUpdaters() {
        return this.boneUpdaters;
    }

    @Generated
    public boolean isGlobal() {
        return this.global;
    }

    @Generated
    protected KeyframeType(String id, Supplier<KEY> keyframeSupplier, Function<Timeline, KeyframeInterpolator<KEY, DATA>> interpolatorSupplier, Map<Class<?>, ModelUpdater> modelUpdaters, Map<Class<?>, BoneUpdater> boneUpdaters, boolean global) {
        this.id = id;
        this.keyframeSupplier = keyframeSupplier;
        this.interpolatorSupplier = interpolatorSupplier;
        this.modelUpdaters = modelUpdaters;
        this.boneUpdaters = boneUpdaters;
        this.global = global;
    }

    @FunctionalInterface
    public static interface ModelUpdater {
        public void update(AnimationHandler var1, ActiveModel var2, Object ... var3);
    }

    @FunctionalInterface
    public static interface BoneUpdater {
        public void update(AnimationHandler var1, ModelBone var2, Object ... var3);
    }

    public static class Builder<KEY extends AbstractKeyframe<DATA>, DATA> {
        private final String id;
        private final Supplier<KEY> keyframeSupplier;
        private final Map<Class<?>, ModelUpdater> modelUpdaters = new HashMap();
        private final Map<Class<?>, BoneUpdater> boneUpdaters = new HashMap();
        private Function<Timeline, KeyframeInterpolator<KEY, DATA>> interpolatorSupplier = blueprintAnimation -> new KeyframeInterpolator();
        private boolean global;

        public static <KEY extends AbstractKeyframe<DATA>, DATA> Builder<KEY, DATA> of(String id, Supplier<KEY> keyframeSupplier) {
            return new Builder<KEY, DATA>(id, keyframeSupplier);
        }

        public Builder<KEY, DATA> interpolator(Function<Timeline, KeyframeInterpolator<KEY, DATA>> interpolatorSupplier) {
            this.interpolatorSupplier = interpolatorSupplier;
            return this;
        }

        public Builder<KEY, DATA> registerModelUpdater(Class<?> handlerClass, ModelUpdater updater) {
            this.modelUpdaters.put(handlerClass, updater);
            return this;
        }

        public Builder<KEY, DATA> registerBoneUpdater(Class<?> handlerClass, BoneUpdater updater) {
            this.boneUpdaters.put(handlerClass, updater);
            return this;
        }

        public Builder<KEY, DATA> global() {
            this.global = true;
            return this;
        }

        public KeyframeType<KEY, DATA> build() {
            return new KeyframeType<KEY, DATA>(this.id, this.keyframeSupplier, this.interpolatorSupplier, this.modelUpdaters, this.boneUpdaters, this.global);
        }

        @Generated
        protected Builder(String id, Supplier<KEY> keyframeSupplier) {
            this.id = id;
            this.keyframeSupplier = keyframeSupplier;
        }
    }
}

