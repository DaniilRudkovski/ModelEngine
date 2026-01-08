/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.lumine.mythic.api.adapters.AbstractLocation
 *  io.lumine.mythic.api.adapters.AbstractVector
 *  io.lumine.mythic.bukkit.BukkitAdapter
 *  io.lumine.mythic.bukkit.compatibility.AbstractModelEngineSupport$MEGProjectile
 *  io.lumine.mythic.bukkit.compatibility.AbstractModelEngineSupport$MEGProjectileData
 *  io.lumine.mythic.core.logging.MythicLogger
 *  org.bukkit.Color
 */
package com.ticxo.modelengine.core21.mythic.compatibility;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.animation.BlueprintAnimation;
import com.ticxo.modelengine.api.animation.handler.AnimationHandler;
import com.ticxo.modelengine.api.animation.handler.IStateMachineHandler;
import com.ticxo.modelengine.api.animation.property.SimpleProperty;
import com.ticxo.modelengine.api.entity.Dummy;
import com.ticxo.modelengine.api.entity.data.AbstractEntityData;
import com.ticxo.modelengine.api.entity.data.DummyEntityData;
import com.ticxo.modelengine.api.entity.data.IEntityData;
import com.ticxo.modelengine.api.generator.blueprint.ModelBlueprint;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.ModeledEntity;
import com.ticxo.modelengine.api.model.bone.BoneBehaviorTypes;
import com.ticxo.modelengine.api.model.bone.ModelBone;
import com.ticxo.modelengine.api.model.bone.type.Segment;
import com.ticxo.modelengine.api.nms.entity.wrapper.BodyRotationController;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.adapters.AbstractVector;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.compatibility.AbstractModelEngineSupport;
import io.lumine.mythic.core.logging.MythicLogger;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Color;

public class ProjectileBullet
implements AbstractModelEngineSupport.MEGProjectile {
    private Dummy<?> entity = new Dummy();
    private ModelBlueprint modelBlueprint;
    private ActiveModel activeModel;
    private ModeledEntity modeledEntity;

    public ProjectileBullet(AbstractModelEngineSupport.MEGProjectileData modelData, AbstractLocation location) {
        String modelId = modelData.getModelId();
        String state = modelData.getState();
        double scale = modelData.getScale();
        String color = modelData.getColor();
        boolean enchanted = modelData.isEnchanted();
        boolean glowing = modelData.isGlowing();
        String glowColor = modelData.getGlowColor();
        this.entity.setLocation(BukkitAdapter.adapt((AbstractLocation)location));
        this.modelBlueprint = ModelEngineAPI.getBlueprint(modelId);
        if (this.modelBlueprint == null) {
            MythicLogger.error((String)("Failed to spawn Projectile Bullet: Model " + modelId + " not found"));
            return;
        }
        this.modeledEntity = ModelEngineAPI.createModeledEntity(this.entity);
        this.activeModel = ModelEngineAPI.createActiveModel(this.modelBlueprint);
        this.activeModel.setScale(scale);
        this.activeModel.setHitboxScale(0.0);
        if (color != null) {
            if (color.startsWith("#")) {
                color = color.substring(1);
            }
            this.activeModel.setDefaultTint(Color.fromRGB((int)Integer.parseInt(color, 16)));
        }
        if (glowing) {
            this.activeModel.setGlowing(true);
            if (glowColor != null) {
                if (glowColor.startsWith("#")) {
                    glowColor = glowColor.substring(1);
                }
                this.activeModel.setGlowColor(Integer.parseInt(glowColor, 16));
            }
        }
        this.modeledEntity.addModel(this.activeModel, true);
        BodyRotationController rotationController = this.modeledEntity.getBase().getBodyRotationController();
        rotationController.setMaxHeadAngle(0.0f);
        rotationController.setMinHeadAngle(0.0f);
        rotationController.setMaxBodyAngle(0.0f);
        rotationController.setMinBodyAngle(0.0f);
        rotationController.setRotationDelay(0);
        rotationController.setRotationDuration(0);
        for (Map.Entry<String, ModelBone> entry : this.activeModel.getBones().entrySet()) {
            ModelBone bone = entry.getValue();
            if (enchanted) {
                bone.setEnchanted(true);
            }
            bone.getBoneBehavior(BoneBehaviorTypes.SEGMENT).ifPresent(segment -> ((Segment)((Object)segment)).setBounded(false));
        }
        if (state != null) {
            AnimationHandler handler = this.activeModel.getAnimationHandler();
            if (handler instanceof IStateMachineHandler) {
                IStateMachineHandler stateMachineHandler = (IStateMachineHandler)handler;
                property = stateMachineHandler.playAnimation(100, state, 0.0, 0.0, 1.0, true);
                if (property != null) {
                    property.setForceLoopMode(BlueprintAnimation.LoopMode.LOOP);
                }
            } else {
                property = handler.playAnimation(state, 0.0, 0.0, 1.0, true);
                if (property != null) {
                    property.setForceLoopMode(BlueprintAnimation.LoopMode.LOOP);
                }
            }
        }
    }

    public UUID getEntityId() {
        return this.entity.getUUID();
    }

    public void setLocation(AbstractLocation location) {
        this.entity.setLocation(BukkitAdapter.adapt((AbstractLocation)location));
    }

    public void setDirection(AbstractVector direction) {
        double yaw = Math.toDegrees(Math.atan2(-direction.getX(), direction.getZ()));
        double pitch = -Math.toDegrees(Math.asin(direction.getY() / direction.length()));
        this.entity.setXHeadRot((float)pitch);
        this.entity.setYHeadRot((float)yaw);
        this.entity.setYBodyRot((float)yaw);
    }

    public void setRenderRadius(int radius) {
        IEntityData data = this.entity.getData();
        ((DummyEntityData)data).setRenderRadius(radius);
    }

    public void disableCulling() {
        IEntityData data = this.entity.getData();
        ((AbstractEntityData)data).setVerticalCull(false);
        ((AbstractEntityData)data).setBackCull(false);
        ((AbstractEntityData)data).setBlockedCull(false);
        ((DummyEntityData)data).cullUpdate();
    }

    public void close() throws Exception {
        BlueprintAnimation animation = this.modelBlueprint.getAnimations().get("death");
        if (animation == null) {
            ModelEngineAPI.removeModeledEntity(this.entity.getUUID());
            this.entity.setRemoved(true);
        } else {
            SimpleProperty property = new SimpleProperty(this.activeModel, animation, 1.0, 0.0, 1.0);
            property.setForceLoopMode(BlueprintAnimation.LoopMode.ONCE);
            property.setForceOverride(BlueprintAnimation.OverrideMode.OVERRIDE);
            property.setOnEndTask(() -> {
                ModelEngineAPI.removeModeledEntity(this.entity.getUUID());
                this.entity.setRemoved(true);
            });
            if (!this.activeModel.getAnimationHandler().playAnimation(property, true)) {
                ModelEngineAPI.removeModeledEntity(this.entity.getUUID());
                this.entity.setRemoved(true);
            }
        }
    }
}

