/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package com.ticxo.modelengine.api.animation.interpolator;

import com.ticxo.modelengine.api.animation.interpolator.KeyframeInterpolator;
import com.ticxo.modelengine.api.animation.keyframe.type.AbstractKeyframe;
import com.ticxo.modelengine.api.animation.property.IAnimationProperty;
import com.ticxo.modelengine.api.model.bone.ModelBone;
import com.ticxo.modelengine.api.utils.math.TMath;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import org.jetbrains.annotations.Nullable;

public class PrePostInterpolator<IN extends AbstractKeyframe<OUT>, OUT>
extends KeyframeInterpolator<IN, OUT> {
    private final BiFunction<OUT, OUT, OUT> derivativeFunc;
    private final BiConsumer<KeyframeInterpolator.Context<IN, OUT>, IN> finalizerFunc;

    public PrePostInterpolator(KeyframeInterpolator.Interpolation<IN, OUT> interpolateFunc, BiConsumer<KeyframeInterpolator.Context<IN, OUT>, IN> finalizerFunc, BiFunction<OUT, OUT, OUT> derivativeFunc) {
        this(interpolateFunc, finalizerFunc, derivativeFunc, () -> null);
    }

    public PrePostInterpolator(KeyframeInterpolator.Interpolation<IN, OUT> interpolateFunc, BiConsumer<KeyframeInterpolator.Context<IN, OUT>, IN> finalizerFunc, BiFunction<OUT, OUT, OUT> derivativeFunc, Supplier<OUT> def) {
        this.setInterpolateFunc(interpolateFunc);
        this.derivativeFunc = derivativeFunc;
        this.finalizerFunc = finalizerFunc;
        this.setDefaultValue(def);
    }

    @Override
    @Nullable
    public OUT interpolate(ModelBone bone, IAnimationProperty property) {
        return this.interpolate(bone, property, (float)property.getTime());
    }

    @Override
    public OUT interpolateAndDerive(ModelBone bone, IAnimationProperty property) {
        float time = (float)property.getTime();
        float lastTime = (float)TMath.clamp(property.getLastTime(), 0.0, property.getTime());
        OUT curr = this.interpolate(bone, property, time);
        if (TMath.isSimilar(time, lastTime)) {
            return this.derivativeFunc.apply(curr, curr);
        }
        OUT prev = this.interpolate(bone, property, lastTime);
        return this.derivativeFunc.apply(prev, curr);
    }

    @Nullable
    private OUT interpolate(ModelBone bone, IAnimationProperty property, float time) {
        float lastKey;
        if (this.isEmpty()) {
            return (OUT)this.defaultValue.get();
        }
        if (this.containsKey(Float.valueOf(time))) {
            AbstractKeyframe frame = (AbstractKeyframe)this.get(Float.valueOf(time));
            this.finalizerFunc.accept(new KeyframeInterpolator.Context(time, time, property, bone, this), frame);
            return (OUT)frame.getValue(0, property);
        }
        float nextKey = this.getHigherKey(time);
        if (nextKey == (lastKey = this.getLowerKey(time))) {
            AbstractKeyframe frame = (AbstractKeyframe)this.get(Float.valueOf(lastKey));
            this.finalizerFunc.accept(new KeyframeInterpolator.Context(lastKey, nextKey, property, bone, this), frame);
            return (OUT)frame.getValue(0, property);
        }
        float t = (time - lastKey) / (nextKey - lastKey);
        Object next = ((AbstractKeyframe)this.get(Float.valueOf(nextKey))).getValue(0, property);
        Object prev = ((AbstractKeyframe)this.get(Float.valueOf(lastKey))).getValue(1, property);
        return this.interpolateFunc.interpolate(new KeyframeInterpolator.Context(lastKey, nextKey, property, bone, this), prev, next, t);
    }
}

