/*
 * Decompiled with CFR 0.152.
 */
package com.ticxo.modelengine.api.nms.entity.wrapper;

import com.ticxo.modelengine.api.nms.entity.wrapper.MovementOverride;

public interface LookController {
    public void lookAt(double var1, double var3, double var5);

    public void setPitch(float var1);

    public void setHeadYaw(float var1);

    public void setBodyYaw(float var1);

    default public void setMovementOverride(MovementOverride override) {
    }
}

