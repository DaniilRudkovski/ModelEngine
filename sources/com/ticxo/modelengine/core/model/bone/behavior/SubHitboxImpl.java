/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  lombok.Generated
 *  org.bukkit.Location
 *  org.bukkit.entity.Entity
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 */
package com.ticxo.modelengine.core.model.bone.behavior;

import com.google.common.collect.Maps;
import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.entity.Hitbox;
import com.ticxo.modelengine.api.model.bone.ModelBone;
import com.ticxo.modelengine.api.model.bone.behavior.AbstractBoneBehavior;
import com.ticxo.modelengine.api.model.bone.behavior.BoneBehaviorData;
import com.ticxo.modelengine.api.model.bone.behavior.BoneBehaviorType;
import com.ticxo.modelengine.api.model.bone.type.SubHitbox;
import com.ticxo.modelengine.api.nms.entity.HitboxEntity;
import com.ticxo.modelengine.api.utils.config.DebugToggle;
import com.ticxo.modelengine.api.utils.math.OrientedBoundingBox;
import com.ticxo.modelengine.api.utils.math.SafeTransform;
import com.ticxo.modelengine.api.utils.math.TMath;
import com.ticxo.modelengine.api.utils.ticker.DualTicker;
import java.util.Map;
import java.util.UUID;
import lombok.Generated;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class SubHitboxImpl
extends AbstractBoneBehavior<SubHitboxImpl>
implements SubHitbox {
    private final boolean isOBB;
    private final Vector3f fixedDimension = new Vector3f();
    private final Vector3f origin = new Vector3f();
    private final Vector3f cubeRotation = new Vector3f();
    private final Vector3f cubeOrigin = new Vector3f();
    private final int hitboxId;
    private final Vector3f dimension = new Vector3f();
    private final Vector3f location = new Vector3f();
    private final Quaternionf rotation = new Quaternionf();
    private final Map<UUID, Entity> boundEntities = Maps.newConcurrentMap();
    private float yaw;
    private HitboxEntity hitboxEntity;
    private float damageMultiplier = 1.0f;
    private boolean canBeHitByProjectile = true;

    public SubHitboxImpl(ModelBone bone, BoneBehaviorType<SubHitboxImpl> type, BoneBehaviorData data) {
        super(bone, type, data);
        Hitbox hitbox = (Hitbox)data.get("dimension");
        if (hitbox == null) {
            throw new RuntimeException("Unable to retrieve sub-hitbox dimension of bone " + bone.getUniqueBoneId());
        }
        this.hitboxId = ModelEngineAPI.getEntityHandler().getNextEntityId();
        this.isOBB = data.get("obb", false);
        Vector3f bonePosition = bone.getBlueprintBone().getGlobalPosition();
        this.origin.set((Vector3fc)data.get("origin", bonePosition)).sub((Vector3fc)bonePosition);
        this.cubeRotation.set((Vector3fc)data.get("rotation", this.cubeRotation));
        this.cubeOrigin.set((Vector3fc)data.get("cube_origin", bonePosition)).sub((Vector3fc)bonePosition);
        this.origin.sub((Vector3fc)this.cubeOrigin).rotateX(this.cubeRotation.x).rotateY(this.cubeRotation.y).rotateZ(this.cubeRotation.z).add((Vector3fc)this.cubeOrigin);
        if (this.isOBB) {
            this.fixedDimension.set(hitbox.getWidth(), hitbox.getHeight(), hitbox.getDepth());
        } else {
            this.fixedDimension.set(hitbox.getMaxWidth(), hitbox.getHeight(), hitbox.getMaxWidth());
        }
    }

    @Override
    public void onApply() {
        Location baseLocation = this.bone.calculatePivotLocation();
        this.removeOld();
        this.hitboxEntity = ModelEngineAPI.getEntityHandler().createHitbox(baseLocation, this.bone, this);
        this.bone.getBlueprintBone().getLocalPosition().rotateY(-this.bone.getYaw() * ((float)Math.PI / 180), this.location).add((float)baseLocation.getX(), (float)baseLocation.getY(), (float)baseLocation.getZ());
        ModelEngineAPI.getInteractionTracker().setEntityRelay(this.hitboxId, this.hitboxEntity.getEntityId());
    }

    @Override
    public void onRemove() {
        ModelEngineAPI.getInteractionTracker().removeEntityRelay(this.hitboxId);
        this.removeOld();
        DualTicker.queueSyncTask(() -> this.boundEntities.forEach((uuid, entity) -> entity.remove()));
    }

    @Override
    public void postAnimation() {
        this.bone.getLocalTransform().mutatePosition(vector3f -> vector3f.add((Vector3fc)this.origin));
        this.bone.getLocalTransform().mutateLeftQuaternion(quaternionf -> quaternionf.mul((Quaternionfc)TMath.toQuaternion(this.cubeRotation)));
    }

    @Override
    public void onFinalize() {
        OrientedBoundingBox obb;
        SafeTransform transform = this.bone.getGlobalTransform();
        this.location.set((Vector3fc)transform.getPosition()).rotateY(-this.bone.getYaw() * ((float)Math.PI / 180));
        this.rotation.set((Quaternionfc)transform.getLeftQuaternion()).mul((Quaternionfc)transform.getRightQuaternion()).rotateLocalY(-this.bone.getYaw() * ((float)Math.PI / 180));
        transform.getScale().mul((Vector3fc)this.fixedDimension, this.dimension);
        this.dimension.mul((float)this.bone.getBlueprintBone().getScale()).mul(this.bone.getActiveModel().getScale());
        Location baseLocation = this.bone.calculatePivotLocation();
        this.location.add((float)baseLocation.getX(), (float)baseLocation.getY(), (float)baseLocation.getZ());
        if (!this.isOBB) {
            this.location.sub(0.0f, this.dimension.y * 0.5f, 0.0f);
        }
        if (this.hitboxEntity == null) {
            return;
        }
        this.hitboxEntity.queueLocation(this.location);
        if (DebugToggle.isDebugging(DebugToggle.SHOW_OBB) && this.isOBB && (obb = this.hitboxEntity.getOrientedBoundingBox()) != null) {
            obb.visualize(baseLocation.getWorld());
        }
    }

    @Override
    public void addBoundEntity(Entity entity) {
        this.getBoundEntities().put(entity.getUniqueId(), entity);
        ModelEngineAPI.getEntityHandler().forceDespawn(entity);
        ModelEngineAPI.setRenderCanceled(entity.getEntityId(), true);
    }

    @Override
    public void removeBoundEntity(Entity entity) {
        this.getBoundEntities().remove(entity.getUniqueId(), entity);
        ModelEngineAPI.setRenderCanceled(entity.getEntityId(), false);
        ModelEngineAPI.getEntityHandler().forceSpawn(entity);
    }

    private void removeOld() {
        if (this.hitboxEntity != null) {
            this.hitboxEntity.markRemoved();
        }
    }

    @Override
    @Generated
    public boolean isOBB() {
        return this.isOBB;
    }

    @Override
    @Generated
    public Vector3f getFixedDimension() {
        return this.fixedDimension;
    }

    @Override
    @Generated
    public Vector3f getOrigin() {
        return this.origin;
    }

    @Generated
    public Vector3f getCubeRotation() {
        return this.cubeRotation;
    }

    @Generated
    public Vector3f getCubeOrigin() {
        return this.cubeOrigin;
    }

    @Override
    @Generated
    public int getHitboxId() {
        return this.hitboxId;
    }

    @Override
    @Generated
    public Vector3f getDimension() {
        return this.dimension;
    }

    @Override
    @Generated
    public Vector3f getLocation() {
        return this.location;
    }

    @Override
    @Generated
    public Quaternionf getRotation() {
        return this.rotation;
    }

    @Override
    @Generated
    public Map<UUID, Entity> getBoundEntities() {
        return this.boundEntities;
    }

    @Override
    @Generated
    public float getYaw() {
        return this.yaw;
    }

    @Override
    @Generated
    public HitboxEntity getHitboxEntity() {
        return this.hitboxEntity;
    }

    @Override
    @Generated
    public float getDamageMultiplier() {
        return this.damageMultiplier;
    }

    @Override
    @Generated
    public boolean isCanBeHitByProjectile() {
        return this.canBeHitByProjectile;
    }

    @Override
    @Generated
    public void setDamageMultiplier(float damageMultiplier) {
        this.damageMultiplier = damageMultiplier;
    }

    @Override
    @Generated
    public void setCanBeHitByProjectile(boolean canBeHitByProjectile) {
        this.canBeHitByProjectile = canBeHitByProjectile;
    }
}

