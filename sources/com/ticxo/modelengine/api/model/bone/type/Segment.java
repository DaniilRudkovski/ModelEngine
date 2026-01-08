/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joml.Vector3f
 */
package com.ticxo.modelengine.api.model.bone.type;

import org.joml.Vector3f;

public interface Segment {
    public void setBounded(boolean var1);

    public boolean isBounded();

    public void setMinX(float var1);

    public float getMinX();

    public void setMaxX(float var1);

    public float getMaxX();

    public void setMinY(float var1);

    public float getMinY();

    public void setMaxY(float var1);

    public float getMaxY();

    public void setMinZ(float var1);

    public float getMinZ();

    public void setMaxZ(float var1);

    public float getMaxZ();

    public void setAlignRate(float var1);

    public float getAlignRate();

    public void setElasticity(float var1);

    public float getElasticity();

    public Vector3f getWorldLocation();

    public Vector3f getDelta();
}

