/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 *  org.joml.Vector3f
 */
package com.ticxo.modelengine.api.animation.keyframe.type;

import com.ticxo.modelengine.api.animation.keyframe.data.IKeyframeData;
import com.ticxo.modelengine.api.animation.keyframe.type.AbstractKeyframe;
import com.ticxo.modelengine.api.animation.keyframe.type.IVectorType;
import com.ticxo.modelengine.api.animation.property.IAnimationProperty;
import lombok.Generated;
import org.joml.Vector3f;

public class VectorKeyframe
extends AbstractKeyframe<Vector3f>
implements IVectorType {
    private final IKeyframeData[] preVector = new IKeyframeData[3];
    private final IKeyframeData[] postVector = new IKeyframeData[3];
    private boolean isDiscontinuous;
    private float xFactor = 1.0f;
    private float yFactor = 1.0f;
    private float zFactor = 1.0f;

    @Override
    public VectorKeyframe setX(IKeyframeData x) {
        this.preVector[0] = x;
        return this;
    }

    @Override
    public VectorKeyframe setY(IKeyframeData y) {
        this.preVector[1] = y;
        return this;
    }

    @Override
    public VectorKeyframe setZ(IKeyframeData z) {
        this.preVector[2] = z;
        return this;
    }

    @Override
    public VectorKeyframe setPostX(IKeyframeData x) {
        this.postVector[0] = x;
        return this;
    }

    @Override
    public VectorKeyframe setPostY(IKeyframeData y) {
        this.postVector[1] = y;
        return this;
    }

    @Override
    public VectorKeyframe setPostZ(IKeyframeData z) {
        this.postVector[2] = z;
        return this;
    }

    @Override
    public IVectorType setXFactor(float x) {
        this.xFactor = x;
        return this;
    }

    @Override
    public IVectorType setYFactor(float y) {
        this.yFactor = y;
        return this;
    }

    @Override
    public IVectorType setZFactor(float z) {
        this.zFactor = z;
        return this;
    }

    @Override
    public Vector3f getValue(int index, IAnimationProperty property) {
        return index == 0 || !this.isDiscontinuous ? new Vector3f((float)this.preVector[0].getValue(property) * this.xFactor, (float)this.preVector[1].getValue(property) * this.yFactor, (float)this.preVector[2].getValue(property) * this.zFactor) : new Vector3f((float)this.postVector[0].getValue(property) * this.xFactor, (float)this.postVector[1].getValue(property) * this.yFactor, (float)this.postVector[2].getValue(property) * this.zFactor);
    }

    @Generated
    public IKeyframeData[] getPreVector() {
        return this.preVector;
    }

    @Generated
    public IKeyframeData[] getPostVector() {
        return this.postVector;
    }

    @Generated
    public boolean isDiscontinuous() {
        return this.isDiscontinuous;
    }

    @Generated
    public float getXFactor() {
        return this.xFactor;
    }

    @Generated
    public float getYFactor() {
        return this.yFactor;
    }

    @Generated
    public float getZFactor() {
        return this.zFactor;
    }

    @Generated
    public void setDiscontinuous(boolean isDiscontinuous) {
        this.isDiscontinuous = isDiscontinuous;
    }
}

