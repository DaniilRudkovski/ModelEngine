/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 *  org.jetbrains.annotations.Nullable
 */
package com.ticxo.modelengine.api.animation.interpolator;

import com.ticxo.modelengine.api.animation.keyframe.type.AbstractKeyframe;
import com.ticxo.modelengine.api.animation.property.IAnimationProperty;
import com.ticxo.modelengine.api.model.bone.ModelBone;
import java.util.TreeMap;
import java.util.function.Supplier;
import lombok.Generated;
import org.jetbrains.annotations.Nullable;

public class KeyframeInterpolator<IN extends AbstractKeyframe<OUT>, OUT>
extends TreeMap<Float, IN> {
    protected Interpolation<IN, OUT> interpolateFunc;
    protected Supplier<OUT> defaultValue = null;

    public KeyframeInterpolator<IN, OUT> setInterpolateFunc(Interpolation<IN, OUT> interpolateFunc) {
        this.interpolateFunc = interpolateFunc;
        return this;
    }

    public KeyframeInterpolator<IN, OUT> setDefaultValue(Supplier<OUT> value) {
        this.defaultValue = value;
        return this;
    }

    @Nullable
    public OUT interpolate(ModelBone bone, IAnimationProperty property) {
        float lastKey;
        if (this.isEmpty()) {
            return this.defaultValue.get();
        }
        float time = (float)property.getTime();
        if (this.containsKey(Float.valueOf(time))) {
            return (OUT)((AbstractKeyframe)this.get(Float.valueOf(time))).getValue(0, property);
        }
        float nextKey = this.getHigherKey(time);
        if (nextKey == (lastKey = this.getLowerKey(time))) {
            return (OUT)((AbstractKeyframe)this.get(Float.valueOf(lastKey))).getValue(0, property);
        }
        float t = (time - lastKey) / (nextKey - lastKey);
        Object next = ((AbstractKeyframe)this.get(Float.valueOf(nextKey))).getValue(0, property);
        Object prev = ((AbstractKeyframe)this.get(Float.valueOf(lastKey))).getValue(0, property);
        return this.interpolateFunc.interpolate(new Context(lastKey, nextKey, property, bone, this), prev, next, t);
    }

    public OUT interpolateAndDerive(ModelBone bone, IAnimationProperty property) {
        throw new UnsupportedOperationException();
    }

    public float getHigherKey(float time) {
        Float high = this.higherKey(Float.valueOf(time));
        if (high == null) {
            return ((Float)this.lastKey()).floatValue();
        }
        return high.floatValue();
    }

    public float getLowerKey(float time) {
        Float low = this.lowerKey(Float.valueOf(time));
        if (low == null) {
            return ((Float)this.firstKey()).floatValue();
        }
        return low.floatValue();
    }

    @FunctionalInterface
    public static interface Interpolation<IN extends AbstractKeyframe<OUT>, OUT> {
        public OUT interpolate(Context<IN, OUT> var1, OUT var2, OUT var3, float var4);
    }

    public static class Context<IN extends AbstractKeyframe<OUT>, OUT> {
        public final float prevKey;
        public final float nextKey;
        public final IAnimationProperty property;
        public final ModelBone bone;
        public final KeyframeInterpolator<IN, OUT> interpolator;

        @Generated
        public Context(float prevKey, float nextKey, IAnimationProperty property, ModelBone bone, KeyframeInterpolator<IN, OUT> interpolator) {
            this.prevKey = prevKey;
            this.nextKey = nextKey;
            this.property = property;
            this.bone = bone;
            this.interpolator = interpolator;
        }
    }
}

