/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  io.lumine.mythic.api.adapters.AbstractEntity
 *  io.lumine.mythic.api.adapters.AbstractLocation
 *  io.lumine.mythic.api.config.MythicConfig
 *  io.lumine.mythic.api.mobs.MythicMob
 *  io.lumine.mythic.bukkit.BukkitAdapter
 *  io.lumine.mythic.bukkit.MythicBukkit
 *  io.lumine.mythic.bukkit.compatibility.AbstractModelEngineSupport
 *  io.lumine.mythic.bukkit.compatibility.AbstractModelEngineSupport$MEGAttachment
 *  io.lumine.mythic.bukkit.compatibility.AbstractModelEngineSupport$MEGAttachmentData
 *  io.lumine.mythic.bukkit.compatibility.AbstractModelEngineSupport$MEGProjectile
 *  io.lumine.mythic.bukkit.compatibility.AbstractModelEngineSupport$MEGProjectileData
 *  io.lumine.mythic.bukkit.compatibility.AbstractModelEngineSupport$ModelConfig
 *  io.lumine.mythic.core.logging.MythicLogger
 *  io.lumine.mythic.core.mobs.model.MobModel
 *  io.lumine.mythic.core.skills.projectiles.ProjectileBulletableTracker
 *  lombok.Generated
 *  org.bukkit.entity.Entity
 *  org.bukkit.util.BoundingBox
 *  org.joml.Vector3f
 */
package com.ticxo.modelengine.core21.mythic.compatibility;

import com.google.common.collect.Maps;
import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.generator.ModelGenerator;
import com.ticxo.modelengine.api.generator.blueprint.BlueprintBone;
import com.ticxo.modelengine.api.generator.blueprint.ModelBlueprint;
import com.ticxo.modelengine.api.nms.entity.HitboxEntity;
import com.ticxo.modelengine.api.utils.math.OrientedBoundingBox;
import com.ticxo.modelengine.api.utils.math.TMath;
import com.ticxo.modelengine.core21.mythic.compatibility.AuraAttachment;
import com.ticxo.modelengine.core21.mythic.compatibility.MEGModel;
import com.ticxo.modelengine.core21.mythic.compatibility.ProjectileBullet;
import com.ticxo.modelengine.core21.mythic.compatibility.ProjectileEntity;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicConfig;
import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.compatibility.AbstractModelEngineSupport;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.mobs.model.MobModel;
import io.lumine.mythic.core.skills.projectiles.ProjectileBulletableTracker;
import java.util.Map;
import java.util.UUID;
import lombok.Generated;
import org.bukkit.entity.Entity;
import org.bukkit.util.BoundingBox;
import org.joml.Vector3f;

public class ModelEngineSupportImpl
extends AbstractModelEngineSupport {
    private final Map<ProjectileBulletableTracker, ProjectileEntity<?>> trackers = Maps.newConcurrentMap();

    public ModelEngineSupportImpl() {
        super(MythicBukkit.inst());
    }

    public boolean isSubHitbox(UUID uuid) {
        return ModelEngineAPI.getInteractionTracker().getHitbox(uuid) != null;
    }

    public boolean isBoundToSubHitbox(UUID subHitbox, UUID bound) {
        HitboxEntity hitbox = ModelEngineAPI.getInteractionTracker().getHitbox(subHitbox);
        return hitbox != null && hitbox.getSubHitbox().getBoundEntities().containsKey(bound);
    }

    public UUID getParentUUID(UUID uuid) {
        HitboxEntity hitbox = ModelEngineAPI.getInteractionTracker().getHitbox(uuid);
        if (hitbox == null) {
            return uuid;
        }
        return hitbox.getBone().getActiveModel().getModeledEntity().getBase().getUUID();
    }

    public AbstractEntity getParent(AbstractEntity abstractEntity) {
        HitboxEntity hitbox = ModelEngineAPI.getInteractionTracker().getHitbox(abstractEntity.getUniqueId());
        if (hitbox == null) {
            return abstractEntity;
        }
        Object obj = hitbox.getBone().getActiveModel().getModeledEntity().getBase().getOriginal();
        if (obj instanceof Entity) {
            Entity entity = (Entity)obj;
            return BukkitAdapter.adapt((Entity)entity);
        }
        return abstractEntity;
    }

    public boolean overlapsOOBB(BoundingBox boundingBox, AbstractEntity abstractEntity) {
        HitboxEntity hitbox = ModelEngineAPI.getInteractionTracker().getHitbox(abstractEntity.getUniqueId());
        if (hitbox == null) {
            return false;
        }
        OrientedBoundingBox obb = hitbox.getOrientedBoundingBox();
        return obb != null && obb.intersects(boundingBox);
    }

    public AbstractModelEngineSupport.ModelConfig getBoneModel(String modelId, String boneId) throws IllegalArgumentException {
        ModelBlueprint blueprint = ModelEngineAPI.getBlueprint(modelId);
        if (blueprint == null) {
            throw new IllegalArgumentException("Unknown model " + modelId + ".");
        }
        BlueprintBone blueprintBone = blueprint.getFlatMap().get(boneId);
        if (blueprintBone == null) {
            throw new IllegalArgumentException("Unknown bone " + boneId + ".");
        }
        if (!blueprintBone.isRenderer()) {
            throw new IllegalArgumentException(boneId + " is not a renderer bone.");
        }
        return new AbstractModelEngineSupport.ModelConfig(blueprintBone.getDataId(), blueprintBone.getBaseItem().getMaterial(), false);
    }

    public MobModel createMobModel(MythicMob mythicMob, MythicConfig mythicConfig) {
        return new MEGModel(mythicMob, mythicConfig);
    }

    public AbstractModelEngineSupport.MEGProjectile createMEGBullet(AbstractModelEngineSupport.MEGProjectileData model, AbstractLocation location) {
        return new ProjectileBullet(model, location);
    }

    public AbstractModelEngineSupport.MEGAttachment createMEGAttachment(AbstractModelEngineSupport.MEGAttachmentData model, AbstractEntity entity) {
        return new AuraAttachment(model, entity);
    }

    public void queuePostModelRegistration(Runnable runnable) {
        ModelEngineAPI.getAPI().getModelGenerator().queueTask(ModelGenerator.Phase.POST_ASSETS, runnable);
    }

    public double distanceSquaredToSubHitbox(AbstractLocation abstractLocation, AbstractEntity abstractEntity) {
        HitboxEntity hitbox = ModelEngineAPI.getInteractionTracker().getHitbox(abstractEntity.getUniqueId());
        OrientedBoundingBox obb = hitbox.getOrientedBoundingBox();
        if (obb != null) {
            return obb.distanceSquared(new Vector3f((float)abstractLocation.getX(), (float)abstractLocation.getY(), (float)abstractLocation.getZ()));
        }
        return TMath.distanceSquaredToBoundingBox(abstractLocation.toLocus().toVector(), abstractEntity.getBukkitEntity().getBoundingBox());
    }

    public void load(MythicBukkit mythicBukkit) {
        MythicLogger.log((String)"Model Engine Compatibility Loaded.");
    }

    public void unload() {
    }

    @Generated
    public Map<ProjectileBulletableTracker, ProjectileEntity<?>> getTrackers() {
        return this.trackers;
    }
}

