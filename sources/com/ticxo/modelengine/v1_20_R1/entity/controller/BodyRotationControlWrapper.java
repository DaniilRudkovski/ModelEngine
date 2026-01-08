/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 *  net.minecraft.world.entity.EntityInsentient
 *  net.minecraft.world.entity.ai.control.EntityAIBodyControl
 */
package com.ticxo.modelengine.v1_20_R1.entity.controller;

import com.ticxo.modelengine.api.nms.entity.wrapper.BodyRotationController;
import com.ticxo.modelengine.api.utils.math.TMath;
import lombok.Generated;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.ai.control.EntityAIBodyControl;

public class BodyRotationControlWrapper
extends EntityAIBodyControl
implements BodyRotationController {
    private final EntityInsentient mob;
    private boolean isHeadClampUneven;
    private boolean isBodyClampUneven;
    private float maxHeadAngle;
    private float maxBodyAngle;
    private float minHeadAngle;
    private float minBodyAngle;
    private boolean playerMode;
    private float stableAngle = 15.0f;
    private int rotationDelay = 10;
    private int rotationDuration = 10;
    private int headStableTime;
    private float lastStableYHeadRot;

    public BodyRotationControlWrapper(EntityInsentient mob) {
        super(mob);
        this.mob = mob;
        this.maxHeadAngle = mob.fC();
        this.maxBodyAngle = mob.fC();
        this.minHeadAngle = -this.maxHeadAngle;
        this.minBodyAngle = -this.maxBodyAngle;
    }

    public void a() {
        if (this.getMob().es()) {
            return;
        }
        if (this.isMoving()) {
            this.mob.aV = this.mob.dy();
            this.rotateHeadIfNecessary();
            this.lastStableYHeadRot = this.mob.aX;
            this.headStableTime = 0;
        } else if (this.notCarryingMobPassengers()) {
            if (Math.abs(this.mob.aX - this.lastStableYHeadRot) > this.stableAngle) {
                this.headStableTime = 0;
                this.lastStableYHeadRot = this.mob.aX;
                this.rotateBodyIfNecessary();
            } else if (!this.playerMode) {
                ++this.headStableTime;
                if (this.headStableTime > this.rotationDelay) {
                    this.rotateHeadTowardsFront();
                }
            }
        }
    }

    @Override
    public float getYHeadRot() {
        return this.mob.aX;
    }

    @Override
    public void setYHeadRot(float rot) {
        this.mob.aX = rot;
    }

    @Override
    public float getXHeadRot() {
        return this.mob.dA();
    }

    @Override
    public void setXHeadRot(float rot) {
        this.mob.b_(rot);
    }

    @Override
    public float getYBodyRot() {
        return this.mob.aV;
    }

    @Override
    public void setYBodyRot(float rot) {
        this.mob.aV = rot;
    }

    private void rotateBodyIfNecessary() {
        this.mob.aV = TMath.rotateIfNecessary(this.mob.aV, this.mob.aX, this.isBodyClampUneven ? this.minBodyAngle : -this.maxBodyAngle, this.maxBodyAngle);
    }

    private void rotateHeadIfNecessary() {
        this.mob.aX = TMath.rotateIfNecessary(this.mob.aX, this.mob.aV, this.isHeadClampUneven ? this.minHeadAngle : -this.maxHeadAngle, this.maxHeadAngle);
    }

    private void rotateHeadTowardsFront() {
        float ratio = (float)(this.headStableTime - this.rotationDelay) / (float)this.rotationDuration;
        float clampedRatio = TMath.clamp(ratio, 0.0f, 1.0f);
        float maxClamp = this.maxHeadAngle * (1.0f - clampedRatio);
        float minClamp = (this.isHeadClampUneven ? this.minHeadAngle : -this.maxHeadAngle) * (1.0f - clampedRatio);
        this.mob.aV = TMath.rotateIfNecessary(this.mob.aV, this.mob.aX, minClamp, maxClamp);
    }

    private boolean notCarryingMobPassengers() {
        return !(this.mob.cO() instanceof EntityInsentient);
    }

    private boolean isMoving() {
        double dZ;
        double dX = this.mob.dn() - this.mob.J;
        return dX * dX + (dZ = this.mob.dt() - this.mob.L) * dZ > 2.500000277905201E-7;
    }

    @Generated
    public EntityInsentient getMob() {
        return this.mob;
    }

    @Override
    @Generated
    public boolean isHeadClampUneven() {
        return this.isHeadClampUneven;
    }

    @Override
    @Generated
    public boolean isBodyClampUneven() {
        return this.isBodyClampUneven;
    }

    @Override
    @Generated
    public float getMaxHeadAngle() {
        return this.maxHeadAngle;
    }

    @Override
    @Generated
    public float getMaxBodyAngle() {
        return this.maxBodyAngle;
    }

    @Override
    @Generated
    public float getMinHeadAngle() {
        return this.minHeadAngle;
    }

    @Override
    @Generated
    public float getMinBodyAngle() {
        return this.minBodyAngle;
    }

    @Override
    @Generated
    public boolean isPlayerMode() {
        return this.playerMode;
    }

    @Override
    @Generated
    public float getStableAngle() {
        return this.stableAngle;
    }

    @Override
    @Generated
    public int getRotationDelay() {
        return this.rotationDelay;
    }

    @Override
    @Generated
    public int getRotationDuration() {
        return this.rotationDuration;
    }

    @Generated
    public int getHeadStableTime() {
        return this.headStableTime;
    }

    @Generated
    public float getLastStableYHeadRot() {
        return this.lastStableYHeadRot;
    }

    @Override
    @Generated
    public void setHeadClampUneven(boolean isHeadClampUneven) {
        this.isHeadClampUneven = isHeadClampUneven;
    }

    @Override
    @Generated
    public void setBodyClampUneven(boolean isBodyClampUneven) {
        this.isBodyClampUneven = isBodyClampUneven;
    }

    @Override
    @Generated
    public void setMaxHeadAngle(float maxHeadAngle) {
        this.maxHeadAngle = maxHeadAngle;
    }

    @Override
    @Generated
    public void setMaxBodyAngle(float maxBodyAngle) {
        this.maxBodyAngle = maxBodyAngle;
    }

    @Override
    @Generated
    public void setMinHeadAngle(float minHeadAngle) {
        this.minHeadAngle = minHeadAngle;
    }

    @Override
    @Generated
    public void setMinBodyAngle(float minBodyAngle) {
        this.minBodyAngle = minBodyAngle;
    }

    @Override
    @Generated
    public void setPlayerMode(boolean playerMode) {
        this.playerMode = playerMode;
    }

    @Override
    @Generated
    public void setStableAngle(float stableAngle) {
        this.stableAngle = stableAngle;
    }

    @Override
    @Generated
    public void setRotationDelay(int rotationDelay) {
        this.rotationDelay = rotationDelay;
    }

    @Override
    @Generated
    public void setRotationDuration(int rotationDuration) {
        this.rotationDuration = rotationDuration;
    }

    @Generated
    public void setHeadStableTime(int headStableTime) {
        this.headStableTime = headStableTime;
    }

    @Generated
    public void setLastStableYHeadRot(float lastStableYHeadRot) {
        this.lastStableYHeadRot = lastStableYHeadRot;
    }
}

