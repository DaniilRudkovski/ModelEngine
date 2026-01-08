/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.lumine.mythic.api.adapters.AbstractEntity
 *  io.lumine.mythic.api.config.MythicLineConfig
 *  io.lumine.mythic.api.skills.ITargetedEntitySkill
 *  io.lumine.mythic.api.skills.SkillMetadata
 *  io.lumine.mythic.api.skills.SkillResult
 *  io.lumine.mythic.api.skills.placeholders.PlaceholderBoolean
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
import com.ticxo.modelengine.api.generator.blueprint.ModelBlueprint;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.ModeledEntity;
import com.ticxo.modelengine.core21.mythic.MythicUtils;
import com.ticxo.modelengine.core21.mythic.utils.MythicMechanic;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.placeholders.PlaceholderBoolean;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.api.skills.placeholders.PlaceholderInt;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.core.skills.placeholders.PlaceholderMeta;
import java.util.Optional;

@MythicMechanic(name="state", aliases={"animation"})
public class StateMechanic
implements ITargetedEntitySkill {
    private final PlaceholderString modelId;
    private final PlaceholderString srcId;
    private final PlaceholderString state;
    private final PlaceholderString forceLoopMode;
    private final PlaceholderString forceOverride;
    private final boolean remove;
    private final boolean ignoreLerp;
    private final boolean force;
    private final PlaceholderInt lerpIn;
    private final PlaceholderInt lerpOut;
    private final PlaceholderInt priority;
    private final PlaceholderDouble speed;
    private final PlaceholderBoolean skipLastFrame;
    private final PlaceholderBoolean merge;
    private final PlaceholderBoolean emptyZero;

    public StateMechanic(MythicLineConfig mlc) {
        this.modelId = mlc.getPlaceholderString(new String[]{"m", "mid", "model", "modelid"}, null, new String[0]);
        this.srcId = mlc.getPlaceholderString(new String[]{"src", "sid", "srcmodel", "srcmodelid"}, null, new String[0]);
        this.state = mlc.getPlaceholderString(new String[]{"s", "state"}, null, new String[0]);
        this.remove = mlc.getBoolean(new String[]{"r", "remove"}, false);
        this.speed = mlc.getPlaceholderDouble(new String[]{"sp", "speed"}, 1.0, new String[0]);
        this.lerpIn = mlc.getPlaceholderInteger(new String[]{"li", "lerpin"}, 0, new String[0]);
        this.lerpOut = mlc.getPlaceholderInteger(new String[]{"lo", "lerpout"}, 0, new String[0]);
        this.ignoreLerp = mlc.getBoolean(new String[]{"i", "ignorelerp"}, false);
        this.force = mlc.getBoolean(new String[]{"f", "force"}, true);
        this.priority = mlc.getPlaceholderInteger(new String[]{"p", "pr", "priority"}, 1, new String[0]);
        this.forceLoopMode = mlc.getPlaceholderString(new String[]{"l", "loop"}, null, new String[0]);
        this.forceOverride = mlc.getPlaceholderString(new String[]{"ov", "override"}, null, new String[0]);
        this.skipLastFrame = mlc.getPlaceholderBoolean(new String[]{"skip", "skiplastframe"}, null);
        this.merge = mlc.getPlaceholderBoolean(new String[]{"mg", "merge"}, Boolean.valueOf(false));
        this.emptyZero = mlc.getPlaceholderBoolean(new String[]{"ez", "zero"}, null);
    }

    public SkillResult castAtEntity(SkillMetadata meta, AbstractEntity target) {
        if (this.remove) {
            return this.removeAnimation(meta, target);
        }
        return this.addAnimation(meta, target);
    }

    private SkillResult removeAnimation(SkillMetadata meta, AbstractEntity target) {
        ModeledEntity model = ModelEngineAPI.getModeledEntity(target.getUniqueId());
        if (model == null) {
            return SkillResult.CONDITION_FAILED;
        }
        String state = MythicUtils.getOrNullLowercase(this.state, (PlaceholderMeta)meta, target);
        if (state == null) {
            return SkillResult.INVALID_CONFIG;
        }
        String modelId = MythicUtils.getOrNullLowercase(this.modelId, (PlaceholderMeta)meta, target);
        String srcId = Optional.ofNullable(MythicUtils.getOrNullLowercase(this.srcId, (PlaceholderMeta)meta, target)).orElse(modelId);
        MythicUtils.executeOptModelId(model, modelId, activeModel -> this.removeAnimation((ActiveModel)activeModel, srcId, state, meta, target));
        return SkillResult.SUCCESS;
    }

    private void removeAnimation(ActiveModel activeModel, String srcId, String state, SkillMetadata meta, AbstractEntity target) {
        ModelBlueprint srcBlueprint = Optional.ofNullable(MythicUtils.getBlueprintOrNull(srcId)).orElse(activeModel.getBlueprint());
        AnimationHandler handler = activeModel.getAnimationHandler();
        if (handler instanceof IStateMachineHandler) {
            IStateMachineHandler stateMachineHandler = (IStateMachineHandler)handler;
            int priority = this.priority.get((PlaceholderMeta)meta, target);
            if (this.ignoreLerp) {
                stateMachineHandler.forceStopAnimation(priority, srcBlueprint, state);
            } else {
                stateMachineHandler.stopAnimation(priority, srcBlueprint, state);
            }
        } else if (this.ignoreLerp) {
            handler.forceStopAnimation(state);
        } else {
            handler.stopAnimation(state);
        }
    }

    private SkillResult addAnimation(SkillMetadata meta, AbstractEntity target) {
        ModeledEntity model = ModelEngineAPI.getModeledEntity(target.getUniqueId());
        if (model == null) {
            return SkillResult.CONDITION_FAILED;
        }
        String state = MythicUtils.getOrNullLowercase(this.state, (PlaceholderMeta)meta, target);
        if (state == null) {
            return SkillResult.INVALID_CONFIG;
        }
        String modelId = MythicUtils.getOrNullLowercase(this.modelId, (PlaceholderMeta)meta, target);
        String srcId = Optional.ofNullable(MythicUtils.getOrNullLowercase(this.srcId, (PlaceholderMeta)meta, target)).orElse(modelId);
        MythicUtils.executeOptModelId(model, modelId, activeModel -> this.addAnimation((ActiveModel)activeModel, srcId, state, meta, target));
        return SkillResult.SUCCESS;
    }

    private void addAnimation(ActiveModel activeModel, String srcId, String state, SkillMetadata meta, AbstractEntity target) {
        ModelBlueprint srcBlueprint = Optional.ofNullable(MythicUtils.getBlueprintOrNull(srcId)).orElse(activeModel.getBlueprint());
        int lerpIn = this.lerpIn.get((PlaceholderMeta)meta, target);
        int lerpOut = this.lerpOut.get((PlaceholderMeta)meta, target);
        double speed = this.speed.get((PlaceholderMeta)meta, target);
        BlueprintAnimation.LoopMode loopMode = BlueprintAnimation.LoopMode.getOrNull(MythicUtils.getOrNull(this.forceLoopMode, (PlaceholderMeta)meta, target));
        BlueprintAnimation.OverrideMode override = BlueprintAnimation.OverrideMode.getOrNull(MythicUtils.getOrNull(this.forceOverride, (PlaceholderMeta)meta, target));
        Boolean skip = this.skipLastFrame != null ? Boolean.valueOf(this.skipLastFrame.get((PlaceholderMeta)meta, target)) : null;
        boolean merge = this.merge != null && this.merge.get((PlaceholderMeta)meta, target);
        Boolean emptyZero = this.emptyZero != null ? Boolean.valueOf(this.emptyZero.get((PlaceholderMeta)meta, target)) : null;
        AnimationHandler handler = activeModel.getAnimationHandler();
        if (handler instanceof IStateMachineHandler) {
            IStateMachineHandler stateMachineHandler = (IStateMachineHandler)handler;
            int priority = this.priority.get((PlaceholderMeta)meta, target);
            IAnimationProperty property = stateMachineHandler.playAnimation(priority, srcBlueprint, state, (double)lerpIn * 0.05, (double)lerpOut * 0.05, speed, this.force);
            if (property != null) {
                property.setMergeLerp(merge);
                if (emptyZero != null) {
                    property.setEmptyZero(emptyZero);
                }
                property.setForceLoopMode(loopMode);
                if (this.forceOverride != null) {
                    property.setForceOverride(override);
                }
                if (skip != null) {
                    property.setSkipLastFrame(skip);
                }
            }
        } else {
            IAnimationProperty property = handler.playAnimation(state, (double)lerpIn * 0.05, (double)lerpOut * 0.05, speed, this.force);
            if (property != null) {
                property.setMergeLerp(merge);
                if (emptyZero != null) {
                    property.setEmptyZero(emptyZero);
                }
                property.setForceLoopMode(loopMode);
                if (this.forceOverride != null) {
                    property.setForceOverride(override);
                }
                if (skip != null) {
                    property.setSkipLastFrame(skip);
                }
            }
        }
    }
}

