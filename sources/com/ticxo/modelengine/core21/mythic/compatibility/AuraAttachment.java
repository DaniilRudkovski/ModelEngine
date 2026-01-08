/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.lumine.mythic.api.adapters.AbstractEntity
 *  io.lumine.mythic.bukkit.BukkitAdapter
 *  io.lumine.mythic.bukkit.compatibility.AbstractModelEngineSupport$MEGAttachment
 *  io.lumine.mythic.bukkit.compatibility.AbstractModelEngineSupport$MEGAttachmentData
 *  io.lumine.mythic.core.logging.MythicLogger
 *  org.bukkit.Color
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Player
 */
package com.ticxo.modelengine.core21.mythic.compatibility;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.animation.BlueprintAnimation;
import com.ticxo.modelengine.api.animation.handler.AnimationHandler;
import com.ticxo.modelengine.api.animation.handler.IStateMachineHandler;
import com.ticxo.modelengine.api.animation.property.SimpleProperty;
import com.ticxo.modelengine.api.entity.data.BukkitEntityData;
import com.ticxo.modelengine.api.entity.data.IEntityData;
import com.ticxo.modelengine.api.generator.blueprint.ModelBlueprint;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.ModeledEntity;
import com.ticxo.modelengine.api.model.bone.BoneBehaviorTypes;
import com.ticxo.modelengine.api.model.bone.ModelBone;
import com.ticxo.modelengine.api.model.bone.type.Segment;
import com.ticxo.modelengine.core21.mythic.compatibility.FakeEntity;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.compatibility.AbstractModelEngineSupport;
import io.lumine.mythic.core.logging.MythicLogger;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Color;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class AuraAttachment
implements AbstractModelEngineSupport.MEGAttachment {
    private Entity entity;
    private FakeEntity attachedEntity;
    private ModelBlueprint modelBlueprint;
    private ActiveModel activeModel;
    private ModeledEntity modeledEntity;

    public AuraAttachment(AbstractModelEngineSupport.MEGAttachmentData modelData, AbstractEntity target) {
        IStateMachineHandler stateMachineHandler;
        this.entity = BukkitAdapter.adapt((AbstractEntity)target);
        String modelId = modelData.getModelId();
        String state = modelData.getState();
        double scale = modelData.getScale();
        String color = modelData.getColor();
        boolean enchanted = modelData.isEnchanted();
        boolean glowing = modelData.isGlowing();
        String glowColor = modelData.getGlowColor();
        this.modelBlueprint = ModelEngineAPI.getBlueprint(modelId);
        if (this.modelBlueprint == null) {
            MythicLogger.error((String)("Failed to spawn Aura Attachment: Model " + modelId + " not found"));
            return;
        }
        this.activeModel = ModelEngineAPI.createActiveModel(this.modelBlueprint);
        this.activeModel.setCanHurt(false);
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
                stateMachineHandler = (IStateMachineHandler)handler;
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
        this.attachedEntity = new FakeEntity(this.entity, modelData.getOffset(), modelData.getAnchor());
        this.attachedEntity.setCollidableWith(this.entity, false);
        this.modeledEntity = ModelEngineAPI.createModeledEntity(this.attachedEntity);
        this.modeledEntity.getBase().getBodyRotationController().setPlayerMode(true);
        this.modeledEntity.setBaseEntityVisible(true);
        this.modeledEntity.addModel(this.activeModel, false);
        this.activeModel.setPivotOverride(ModelEngineAPI.getPivotOverrideRegistry().getOrCreate(this.entity));
        stateMachineHandler = this.entity;
        if (stateMachineHandler instanceof Player) {
            Player player = (Player)stateMachineHandler;
            IEntityData iEntityData = this.modeledEntity.getBase().getData();
            if (iEntityData instanceof BukkitEntityData) {
                BukkitEntityData data = (BukkitEntityData)iEntityData;
                data.getTracked().addForcedPairing(player.getUniqueId());
            }
        }
    }

    public UUID getEntityId() {
        return this.attachedEntity.getUUID();
    }

    public void setRenderRadius(int radius) {
        BukkitEntityData data = this.attachedEntity.getData();
    }

    public void disableCulling() {
        BukkitEntityData data = this.attachedEntity.getData();
        data.setVerticalCull(false);
        data.setBackCull(false);
        data.setBlockedCull(false);
        data.cullUpdate();
    }

    public void close() throws Exception {
        BlueprintAnimation animation = this.modelBlueprint.getAnimations().get("death");
        if (animation == null) {
            ModelEngineAPI.removeModeledEntity(this.attachedEntity.getUUID());
        } else {
            SimpleProperty property = new SimpleProperty(this.activeModel, animation, 1.0, 0.0, 1.0);
            property.setForceLoopMode(BlueprintAnimation.LoopMode.ONCE);
            property.setForceOverride(BlueprintAnimation.OverrideMode.OVERRIDE);
            property.setOnEndTask(() -> ModelEngineAPI.removeModeledEntity(this.attachedEntity.getUUID()));
            if (!this.activeModel.getAnimationHandler().playAnimation(property, true)) {
                ModelEngineAPI.removeModeledEntity(this.attachedEntity.getUUID());
            }
        }
    }
}

