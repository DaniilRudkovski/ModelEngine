/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 */
package com.ticxo.modelengine.api.animation.keyframe.data;

import com.ticxo.modelengine.api.animation.keyframe.data.IKeyframeData;
import com.ticxo.modelengine.api.animation.property.IAnimationProperty;
import lombok.Generated;

public class DoubleData
implements IKeyframeData {
    private final double data;

    @Override
    public double getValue(IAnimationProperty property) {
        return this.data;
    }

    @Generated
    public DoubleData(double data) {
        this.data = data;
    }
}

