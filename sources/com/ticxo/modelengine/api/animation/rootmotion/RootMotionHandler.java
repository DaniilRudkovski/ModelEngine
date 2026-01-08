/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 *  org.bukkit.util.Vector
 */
package com.ticxo.modelengine.api.animation.rootmotion;

import com.ticxo.modelengine.api.animation.rootmotion.RootMotion;
import com.ticxo.modelengine.api.animation.rootmotion.RootMotionDelta;
import com.ticxo.modelengine.api.model.ModeledEntity;
import com.ticxo.modelengine.api.model.bone.ModelBone;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import lombok.Generated;
import org.bukkit.util.Vector;

public class RootMotionHandler {
    private final ModeledEntity modeledEntity;
    private final Queue<RootMotion> velocityQueue = new ConcurrentLinkedQueue<RootMotion>();
    private ModelBone rootBone;
    private double baseWeight = 1.0;
    private double weight = 0.0;
    private boolean override;
    private int queued;

    public void queueVelocity(RootMotion rootMotion) {
        this.queued = 2;
        this.velocityQueue.add(rootMotion);
    }

    public boolean isQueued() {
        return this.queued > 0;
    }

    public RootMotionDelta calculateRootMotion(Vector base) {
        if (this.isQueued()) {
            --this.queued;
        }
        if (this.velocityQueue.isEmpty() || this.rootBone == null) {
            return null;
        }
        double total = this.baseWeight + this.weight;
        RootMotion vel = this.velocityQueue.poll();
        base.multiply(this.override ? 0.0 : this.baseWeight / total);
        Vector delta = vel.delta().multiply(this.override ? 1.0 : this.weight / total);
        return new RootMotionDelta(delta, vel.boneOnGround());
    }

    @Generated
    public ModeledEntity getModeledEntity() {
        return this.modeledEntity;
    }

    @Generated
    public Queue<RootMotion> getVelocityQueue() {
        return this.velocityQueue;
    }

    @Generated
    public ModelBone getRootBone() {
        return this.rootBone;
    }

    @Generated
    public double getBaseWeight() {
        return this.baseWeight;
    }

    @Generated
    public double getWeight() {
        return this.weight;
    }

    @Generated
    public boolean isOverride() {
        return this.override;
    }

    @Generated
    public void setRootBone(ModelBone rootBone) {
        this.rootBone = rootBone;
    }

    @Generated
    public void setBaseWeight(double baseWeight) {
        this.baseWeight = baseWeight;
    }

    @Generated
    public void setWeight(double weight) {
        this.weight = weight;
    }

    @Generated
    public void setOverride(boolean override) {
        this.override = override;
    }

    @Generated
    public RootMotionHandler(ModeledEntity modeledEntity) {
        this.modeledEntity = modeledEntity;
    }
}

