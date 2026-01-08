/*
 * Decompiled with CFR 0.152.
 */
package com.ticxo.modelengine.api.nms.entity.wrapper;

import com.ticxo.modelengine.api.utils.data.io.DataIO;
import com.ticxo.modelengine.api.utils.data.io.SavedData;

public interface BodyRotationController
extends DataIO {
    default public void tick() {
    }

    public float getYHeadRot();

    public void setYHeadRot(float var1);

    public float getXHeadRot();

    public void setXHeadRot(float var1);

    public float getYBodyRot();

    public void setYBodyRot(float var1);

    public boolean isHeadClampUneven();

    public void setHeadClampUneven(boolean var1);

    public boolean isBodyClampUneven();

    public void setBodyClampUneven(boolean var1);

    public float getMaxHeadAngle();

    public void setMaxHeadAngle(float var1);

    public float getMaxBodyAngle();

    public void setMaxBodyAngle(float var1);

    public float getMinHeadAngle();

    public void setMinHeadAngle(float var1);

    public float getMinBodyAngle();

    public void setMinBodyAngle(float var1);

    public float getStableAngle();

    public void setStableAngle(float var1);

    public boolean isPlayerMode();

    public void setPlayerMode(boolean var1);

    public int getRotationDelay();

    public void setRotationDelay(int var1);

    public int getRotationDuration();

    public void setRotationDuration(int var1);

    @Override
    default public void save(SavedData data) {
        data.putBoolean("head", this.isHeadClampUneven());
        data.putBoolean("body", this.isBodyClampUneven());
        data.putFloat("max_head", Float.valueOf(this.getMaxHeadAngle()));
        data.putFloat("max_body", Float.valueOf(this.getMaxBodyAngle()));
        data.putFloat("min_head", Float.valueOf(this.getMinHeadAngle()));
        data.putFloat("min_body", Float.valueOf(this.getMinBodyAngle()));
        data.putFloat("stable", Float.valueOf(this.getStableAngle()));
        data.putBoolean("player", this.isPlayerMode());
        data.putInt("delay", this.getRotationDelay());
        data.putInt("duration", this.getRotationDuration());
    }

    @Override
    default public void load(SavedData data) {
        this.setHeadClampUneven(data.getBoolean("head"));
        this.setBodyClampUneven(data.getBoolean("body"));
        this.setMaxHeadAngle(data.getFloat("max_head").floatValue());
        this.setMaxBodyAngle(data.getFloat("max_body").floatValue());
        this.setMinHeadAngle(data.getFloat("min_head").floatValue());
        this.setMinBodyAngle(data.getFloat("min_body").floatValue());
        this.setStableAngle(data.getFloat("stable").floatValue());
        this.setPlayerMode(data.getBoolean("player"));
        this.setRotationDelay(data.getInt("delay"));
        this.setRotationDuration(data.getInt("duration"));
    }
}

