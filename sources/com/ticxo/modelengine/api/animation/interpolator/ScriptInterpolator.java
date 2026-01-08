/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 *  org.jetbrains.annotations.Nullable
 */
package com.ticxo.modelengine.api.animation.interpolator;

import com.ticxo.modelengine.api.animation.interpolator.KeyframeInterpolator;
import com.ticxo.modelengine.api.animation.keyframe.type.AbstractKeyframe;
import com.ticxo.modelengine.api.animation.property.IAnimationProperty;
import com.ticxo.modelengine.api.model.bone.ModelBone;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import lombok.Generated;
import org.jetbrains.annotations.Nullable;

public class ScriptInterpolator<IN extends AbstractKeyframe<OUT>, OUT>
extends KeyframeInterpolator<IN, OUT> {
    private final Supplier<OUT> supplier;
    private final BiConsumer<OUT, OUT> combiner;

    @Override
    @Nullable
    public OUT interpolate(ModelBone bone, IAnimationProperty property) {
        Object data;
        if (this.isEmpty()) {
            return null;
        }
        OUT combined = this.supplier.get();
        float lastTime = (float)property.getLastTime();
        float currTime = (float)property.getTime();
        float frame = lastTime;
        float lastFrame = ((Float)this.lastKey()).floatValue();
        if (lastTime > currTime) {
            while (frame < lastFrame) {
                float f;
                frame = this.getHigherKey(frame);
                if (!(f <= lastFrame)) break;
                data = ((AbstractKeyframe)this.get(Float.valueOf(frame))).getValue(0, property);
                this.combiner.accept(combined, data);
            }
            frame = -1.0f;
        }
        while (frame < lastFrame) {
            float f;
            frame = this.getHigherKey(frame);
            if (!(f <= currTime)) break;
            data = ((AbstractKeyframe)this.get(Float.valueOf(frame))).getValue(0, property);
            this.combiner.accept(combined, data);
        }
        return combined;
    }

    @Generated
    public ScriptInterpolator(Supplier<OUT> supplier, BiConsumer<OUT, OUT> combiner) {
        this.supplier = supplier;
        this.combiner = combiner;
    }
}

