/*
 * Decompiled with CFR 0.152.
 */
package com.ticxo.modelengine.api.animation.keyframe.type;

import com.ticxo.modelengine.api.animation.keyframe.data.IKeyframeData;

public interface IVectorType {
    public IVectorType setX(IKeyframeData var1);

    public IVectorType setY(IKeyframeData var1);

    public IVectorType setZ(IKeyframeData var1);

    public IVectorType setPostX(IKeyframeData var1);

    public IVectorType setPostY(IKeyframeData var1);

    public IVectorType setPostZ(IKeyframeData var1);

    public IVectorType setXFactor(float var1);

    public IVectorType setYFactor(float var1);

    public IVectorType setZFactor(float var1);
}

