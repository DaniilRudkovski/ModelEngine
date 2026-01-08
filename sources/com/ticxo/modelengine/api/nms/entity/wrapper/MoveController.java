/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Entity
 *  org.bukkit.util.Vector
 */
package com.ticxo.modelengine.api.nms.entity.wrapper;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.nms.entity.wrapper.MovementOverride;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

public interface MoveController {
    public void move(float var1, float var2, float var3, float var4);

    public void globalMove(float var1, float var2, float var3, float var4);

    public void jump();

    public void setVelocity(double var1, double var3, double var5);

    public void addVelocity(double var1, double var3, double var5);

    public void nullifyFallDistance();

    default public void movePassenger(Entity entity, double x, double y, double z) {
        ModelEngineAPI.getEntityHandler().movePassenger(entity, x, y, z);
    }

    public boolean isOnGround();

    public boolean isInWater();

    public float getSpeed();

    public Vector getVelocity();

    public void queuePostTick(Runnable var1);

    default public void setMovementOverride(MovementOverride override) {
    }
}

