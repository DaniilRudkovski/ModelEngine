/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 *  org.bukkit.Location
 *  org.bukkit.entity.Entity
 *  org.joml.Vector3f
 */
package com.ticxo.modelengine.core.model.bone.behavior;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.model.bone.ModelBone;
import com.ticxo.modelengine.api.model.bone.behavior.AbstractBoneBehavior;
import com.ticxo.modelengine.api.model.bone.behavior.BoneBehavior;
import com.ticxo.modelengine.api.model.bone.behavior.BoneBehaviorData;
import com.ticxo.modelengine.api.model.bone.behavior.BoneBehaviorType;
import com.ticxo.modelengine.api.model.bone.manager.LeashManager;
import com.ticxo.modelengine.api.model.bone.type.Leash;
import lombok.Generated;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.joml.Vector3f;

public class LeashImpl
extends AbstractBoneBehavior<LeashImpl>
implements Leash {
    private final boolean mainLeash;
    private final int id;
    private final Vector3f location = new Vector3f();
    private Entity connectedEntity;
    private Leash connectedLeash;

    public LeashImpl(ModelBone bone, BoneBehaviorType<LeashImpl> type, BoneBehaviorData data) {
        super(bone, type, data);
        this.mainLeash = data.get("main", false);
        this.id = ModelEngineAPI.getEntityHandler().getNextEntityId();
    }

    @Override
    public void onApply() {
        this.bone.getActiveModel().getLeashManager().ifPresent(leashManager -> ((LeashManager)((Object)leashManager)).registerLeash(this));
        Location baseLocation = this.bone.calculatePivotLocation();
        this.bone.getBlueprintBone().getLocalPosition().rotateY(-this.bone.getYaw() * ((float)Math.PI / 180), this.location).add((float)baseLocation.getX(), (float)baseLocation.getY(), (float)baseLocation.getZ());
    }

    @Override
    public void onFinalize() {
        Location baseLocation = this.bone.calculatePivotLocation();
        this.bone.getGlobalTransform().mutatePosition(vector3f -> vector3f.rotateY(-this.bone.getYaw() * ((float)Math.PI / 180), this.location).add((float)baseLocation.getX(), (float)baseLocation.getY(), (float)baseLocation.getZ()));
    }

    @Override
    public void connect(Entity entity) {
        this.connectedEntity = entity;
    }

    @Override
    public <T extends Leash & BoneBehavior> void connect(T leash) {
        if (leash == this) {
            return;
        }
        this.connectedLeash = leash;
    }

    @Override
    public void disconnect() {
        this.connectedEntity = null;
        this.connectedLeash = null;
    }

    @Override
    public <T extends Leash & BoneBehavior> T getConnectedLeash() {
        return (T)this.connectedLeash;
    }

    @Override
    @Generated
    public boolean isMainLeash() {
        return this.mainLeash;
    }

    @Override
    @Generated
    public int getId() {
        return this.id;
    }

    @Override
    @Generated
    public Vector3f getLocation() {
        return this.location;
    }

    @Override
    @Generated
    public Entity getConnectedEntity() {
        return this.connectedEntity;
    }
}

