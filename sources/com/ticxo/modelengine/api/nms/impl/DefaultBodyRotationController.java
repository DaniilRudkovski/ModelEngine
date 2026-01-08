/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Mob
 */
package com.ticxo.modelengine.api.nms.impl;

import com.ticxo.modelengine.api.entity.BaseEntity;
import com.ticxo.modelengine.api.nms.entity.wrapper.BodyRotationController;
import com.ticxo.modelengine.api.utils.math.TMath;
import lombok.Generated;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;

public class DefaultBodyRotationController
implements BodyRotationController {
    private final BaseEntity<?> entity;
    private float xHeadRot;
    private float yHeadRot;
    private float yBodyRot;
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

    public DefaultBodyRotationController(BaseEntity<?> entity) {
        this.entity = entity;
        this.xHeadRot = entity.getXHeadRot();
        this.yHeadRot = entity.getYHeadRot();
        this.yBodyRot = entity.getYBodyRot();
        this.maxHeadAngle = 75.0f;
        this.maxBodyAngle = 75.0f;
        this.minHeadAngle = -this.maxHeadAngle;
        this.minBodyAngle = -this.maxBodyAngle;
    }

    @Override
    public void tick() {
        this.xHeadRot = this.entity.getXHeadRot();
        this.yHeadRot = this.entity.getYHeadRot();
        if (this.entity.isWalking()) {
            this.yBodyRot = this.entity.getYRot();
            this.rotateHeadIfNecessary();
            this.lastStableYHeadRot = this.yHeadRot;
            this.headStableTime = 0;
        } else if (this.notCarryingMobPassengers()) {
            if (Math.abs(this.yHeadRot - this.lastStableYHeadRot) > this.stableAngle) {
                this.headStableTime = 0;
                this.lastStableYHeadRot = this.yHeadRot;
                this.rotateBodyIfNecessary();
            } else if (!this.playerMode) {
                ++this.headStableTime;
                if (this.headStableTime > this.rotationDelay) {
                    this.rotateHeadTowardsFront();
                }
            }
        }
    }

    private void rotateBodyIfNecessary() {
        this.yBodyRot = TMath.rotateIfNecessary(this.yBodyRot, this.yHeadRot, this.isBodyClampUneven ? this.minBodyAngle : -this.maxBodyAngle, this.maxBodyAngle);
    }

    private void rotateHeadIfNecessary() {
        this.yHeadRot = TMath.rotateIfNecessary(this.yHeadRot, this.yBodyRot, this.isHeadClampUneven ? this.minHeadAngle : -this.maxHeadAngle, this.maxHeadAngle);
    }

    private void rotateHeadTowardsFront() {
        float ratio = (float)(this.headStableTime - this.rotationDelay) / (float)this.rotationDuration;
        float clampedRatio = TMath.clamp(ratio, 0.0f, 1.0f);
        float maxClamp = this.maxHeadAngle * (1.0f - clampedRatio);
        float minClamp = (this.isHeadClampUneven ? this.minHeadAngle : -this.maxHeadAngle) * (1.0f - clampedRatio);
        this.yBodyRot = TMath.rotateIfNecessary(this.yBodyRot, this.yHeadRot, minClamp, maxClamp);
    }

    private boolean notCarryingMobPassengers() {
        for (Entity entity : this.entity.getPassengers()) {
            if (!(entity instanceof Mob)) continue;
            return false;
        }
        return true;
    }

    @Generated
    public BaseEntity<?> getEntity() {
        return this.entity;
    }

    @Override
    @Generated
    public float getXHeadRot() {
        return this.xHeadRot;
    }

    @Override
    @Generated
    public float getYHeadRot() {
        return this.yHeadRot;
    }

    @Override
    @Generated
    public float getYBodyRot() {
        return this.yBodyRot;
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
    public void setXHeadRot(float xHeadRot) {
        this.xHeadRot = xHeadRot;
    }

    @Override
    @Generated
    public void setYHeadRot(float yHeadRot) {
        this.yHeadRot = yHeadRot;
    }

    @Override
    @Generated
    public void setYBodyRot(float yBodyRot) {
        this.yBodyRot = yBodyRot;
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

