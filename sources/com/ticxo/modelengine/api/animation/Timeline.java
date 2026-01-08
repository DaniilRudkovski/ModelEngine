/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  lombok.Generated
 */
package com.ticxo.modelengine.api.animation;

import com.google.common.collect.Maps;
import com.ticxo.modelengine.api.animation.BlueprintAnimation;
import com.ticxo.modelengine.api.animation.interpolator.KeyframeInterpolator;
import com.ticxo.modelengine.api.animation.keyframe.KeyframeType;
import com.ticxo.modelengine.api.animation.keyframe.type.AbstractKeyframe;
import java.util.Map;
import lombok.Generated;

public class Timeline {
    private final BlueprintAnimation animation;
    private final boolean globalRotation;
    private final Map<KeyframeType<?, ?>, KeyframeInterpolator<?, ?>> interpolators = Maps.newConcurrentMap();

    public boolean hasInterpolator(KeyframeType<?, ?> type) {
        return this.interpolators.containsKey(type);
    }

    public <KEY extends AbstractKeyframe<DATA>, DATA> KeyframeInterpolator<KEY, DATA> getInterpolator(KeyframeType<KEY, DATA> type) {
        return this.interpolators.computeIfAbsent(type, keyframeType -> keyframeType.createInterpolator(this));
    }

    public <KEY extends AbstractKeyframe<DATA>, DATA> KEY getKeyframe(float time, KeyframeType<KEY, DATA> type) {
        KeyframeInterpolator<KEY, DATA> interpolator = this.getInterpolator(type);
        return (KEY)interpolator.computeIfAbsent(Float.valueOf(time), t -> type.createKeyframe());
    }

    @Generated
    public Timeline(BlueprintAnimation animation, boolean globalRotation) {
        this.animation = animation;
        this.globalRotation = globalRotation;
    }

    @Generated
    public BlueprintAnimation getAnimation() {
        return this.animation;
    }

    @Generated
    public boolean isGlobalRotation() {
        return this.globalRotation;
    }
}

