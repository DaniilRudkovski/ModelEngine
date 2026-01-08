/*
 * Decompiled with CFR 0.152.
 */
package com.ticxo.modelengine.api.animation.keyframe.data;

import com.ticxo.modelengine.api.animation.property.IAnimationProperty;

public interface IKeyframeData {
    public static final IKeyframeData EMPTY = property -> 0.0;

    public double getValue(IAnimationProperty var1);
}

