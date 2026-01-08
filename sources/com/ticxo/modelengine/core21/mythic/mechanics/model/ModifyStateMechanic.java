/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.lumine.mythic.api.adapters.AbstractEntity
 *  io.lumine.mythic.api.config.MythicLineConfig
 *  io.lumine.mythic.api.skills.ITargetedEntitySkill
 *  io.lumine.mythic.api.skills.SkillMetadata
 *  io.lumine.mythic.api.skills.SkillResult
 *  io.lumine.mythic.api.skills.placeholders.PlaceholderDouble
 *  io.lumine.mythic.api.skills.placeholders.PlaceholderInt
 *  io.lumine.mythic.api.skills.placeholders.PlaceholderString
 *  io.lumine.mythic.core.skills.placeholders.PlaceholderMeta
 */
package com.ticxo.modelengine.core21.mythic.mechanics.model;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.animation.BlueprintAnimation;
import com.ticxo.modelengine.api.animation.handler.AnimationHandler;
import com.ticxo.modelengine.api.animation.handler.IStateMachineHandler;
import com.ticxo.modelengine.api.animation.property.IAnimationProperty;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.ModeledEntity;
import com.ticxo.modelengine.core21.mythic.MythicUtils;
import com.ticxo.modelengine.core21.mythic.utils.MythicMechanic;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.api.skills.placeholders.PlaceholderInt;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.core.skills.placeholders.PlaceholderMeta;

@MythicMechanic(name="modifystate", aliases={"modifyanimation", "modstate"})
public class ModifyStateMechanic
implements ITargetedEntitySkill {
    private final MythicLineConfig mlc;
    private final PlaceholderString modelId;
    private final PlaceholderString state;
    private final PlaceholderInt priority;

    public ModifyStateMechanic(MythicLineConfig mlc) {
        this.mlc = mlc;
        this.modelId = mlc.getPlaceholderString(new String[]{"m", "mid", "model", "modelid"}, null, new String[0]);
        this.state = mlc.getPlaceholderString(new String[]{"s", "state"}, null, new String[0]);
        this.priority = mlc.getPlaceholderInteger(new String[]{"p", "pr", "priority"}, 1, new String[0]);
    }

    public SkillResult castAtEntity(SkillMetadata meta, AbstractEntity target) {
        ModeledEntity model = ModelEngineAPI.getModeledEntity(target.getUniqueId());
        if (model == null) {
            return SkillResult.CONDITION_FAILED;
        }
        String modelId = MythicUtils.getOrNullLowercase(this.modelId, (PlaceholderMeta)meta, target);
        String state = MythicUtils.getOrNullLowercase(this.state, (PlaceholderMeta)meta, target);
        model.getModel(modelId).ifPresentOrElse(activeModel -> this.configureAnimation(state, (ActiveModel)activeModel, meta, target), () -> {
            for (ActiveModel activeModel : model.getModels().values()) {
                this.configureAnimation(state, activeModel, meta, target);
            }
        });
        return SkillResult.SUCCESS;
    }

    private void configureAnimation(String state, ActiveModel activeModel, SkillMetadata meta, AbstractEntity target) {
        IAnimationProperty property;
        AnimationHandler handler = activeModel.getAnimationHandler();
        if (handler instanceof IStateMachineHandler) {
            IStateMachineHandler stateMachineHandler = (IStateMachineHandler)handler;
            int priority = this.priority.get((PlaceholderMeta)meta, target);
            property = stateMachineHandler.getAnimation(priority, state);
        } else {
            property = handler.getAnimation(state);
        }
        if (property == null) {
            return;
        }
        PlaceholderDouble speedPlaceholder = this.mlc.getPlaceholderDouble(new String[]{"sp", "speed"}, property.getSpeed(), new String[0]);
        PlaceholderInt lerpInPlaceholder = this.mlc.getPlaceholderInteger(new String[]{"li", "lerpin"}, (int)(property.getLerpIn() * 20.0), new String[0]);
        PlaceholderInt lerpOutPlaceholder = this.mlc.getPlaceholderInteger(new String[]{"lo", "lerpout"}, (int)(property.getLerpOut() * 20.0), new String[0]);
        BlueprintAnimation.LoopMode loopMode = property.getForceLoopMode();
        PlaceholderString loopModePlaceholder = this.mlc.getPlaceholderString(new String[]{"l", "loop"}, loopMode == null ? null : loopMode.name(), new String[0]);
        loopMode = BlueprintAnimation.LoopMode.getOrNull(MythicUtils.getOrNull(loopModePlaceholder, (PlaceholderMeta)meta, target));
        BlueprintAnimation.OverrideMode override = property.getForceOverride();
        PlaceholderString overridePlaceholder = this.mlc.getPlaceholderString(new String[]{"ov", "override"}, override == null ? null : override.name(), new String[0]);
        override = BlueprintAnimation.OverrideMode.getOrNull(MythicUtils.getOrNull(overridePlaceholder, (PlaceholderMeta)meta, target));
        property.setSpeed(speedPlaceholder.get((PlaceholderMeta)meta, target));
        property.setLerpInTime((double)lerpInPlaceholder.get((PlaceholderMeta)meta, target) * 0.05);
        property.setLerpOutTime((double)lerpOutPlaceholder.get((PlaceholderMeta)meta, target) * 0.05);
        property.setForceLoopMode(loopMode);
        property.setForceOverride(override);
    }
}

