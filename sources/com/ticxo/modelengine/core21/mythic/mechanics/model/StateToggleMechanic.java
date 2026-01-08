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
import com.ticxo.modelengine.api.animation.handler.AnimationHandler;
import com.ticxo.modelengine.api.animation.handler.IStateMachineHandler;
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

@MythicMechanic(name="statetoggle", aliases={"togglestate"})
public class StateToggleMechanic
implements ITargetedEntitySkill {
    private final PlaceholderString modelId;
    private final PlaceholderString state;
    private final boolean ignoreLerp;
    private final boolean force;
    private final PlaceholderInt lerpIn;
    private final PlaceholderInt lerpOut;
    private final PlaceholderInt priority;
    private final PlaceholderDouble speed;

    public StateToggleMechanic(MythicLineConfig mlc) {
        this.modelId = mlc.getPlaceholderString(new String[]{"m", "mid", "model", "modelid"}, null, new String[0]);
        this.state = mlc.getPlaceholderString(new String[]{"s", "state"}, null, new String[0]);
        this.speed = mlc.getPlaceholderDouble(new String[]{"sp", "speed"}, 1.0, new String[0]);
        this.lerpIn = mlc.getPlaceholderInteger(new String[]{"li", "lerpin"}, 0, new String[0]);
        this.lerpOut = mlc.getPlaceholderInteger(new String[]{"lo", "lerpout"}, 0, new String[0]);
        this.ignoreLerp = mlc.getBoolean(new String[]{"i", "ignorelerp"}, false);
        this.force = mlc.getBoolean(new String[]{"f", "force"}, true);
        this.priority = mlc.getPlaceholderInteger(new String[]{"p", "pr", "priority"}, 1, new String[0]);
    }

    public SkillResult castAtEntity(SkillMetadata meta, AbstractEntity target) {
        ModeledEntity model = ModelEngineAPI.getModeledEntity(target.getUniqueId());
        if (model == null) {
            return SkillResult.CONDITION_FAILED;
        }
        String state = MythicUtils.getOrNullLowercase(this.state, (PlaceholderMeta)meta, target);
        if (state == null) {
            return SkillResult.INVALID_CONFIG;
        }
        String modelId = MythicUtils.getOrNullLowercase(this.modelId, (PlaceholderMeta)meta, target);
        MythicUtils.executeOptModelId(model, modelId, activeModel -> {
            AnimationHandler handler = activeModel.getAnimationHandler();
            if (handler instanceof IStateMachineHandler) {
                IStateMachineHandler stateMachineHandler = (IStateMachineHandler)handler;
                if (stateMachineHandler.isPlayingAnimation(state)) {
                    this.removeAnimation(stateMachineHandler, state, meta, target);
                } else {
                    this.addAnimation(stateMachineHandler, state, meta, target);
                }
            } else if (handler.isPlayingAnimation(state)) {
                this.removeAnimation(handler, state);
            } else {
                this.addAnimation(handler, state, meta, target);
            }
        });
        return SkillResult.SUCCESS;
    }

    private void removeAnimation(AnimationHandler handler, String state) {
        if (this.ignoreLerp) {
            handler.forceStopAnimation(state);
        } else {
            handler.stopAnimation(state);
        }
    }

    private void removeAnimation(IStateMachineHandler handler, String state, SkillMetadata meta, AbstractEntity target) {
        int priority = this.priority.get((PlaceholderMeta)meta, target);
        if (this.ignoreLerp) {
            handler.forceStopAnimation(priority, state);
        } else {
            handler.stopAnimation(priority, state);
        }
    }

    private void addAnimation(AnimationHandler handler, String state, SkillMetadata meta, AbstractEntity target) {
        int lerpIn = this.lerpIn.get((PlaceholderMeta)meta, target);
        int lerpOut = this.lerpOut.get((PlaceholderMeta)meta, target);
        double speed = this.speed.get((PlaceholderMeta)meta, target);
        handler.playAnimation(state, (double)lerpIn * 0.05, (double)lerpOut * 0.05, speed, this.force);
    }

    private void addAnimation(IStateMachineHandler handler, String state, SkillMetadata meta, AbstractEntity target) {
        int lerpIn = this.lerpIn.get((PlaceholderMeta)meta, target);
        int lerpOut = this.lerpOut.get((PlaceholderMeta)meta, target);
        double speed = this.speed.get((PlaceholderMeta)meta, target);
        int priority = this.priority.get((PlaceholderMeta)meta, target);
        handler.playAnimation(priority, state, (double)lerpIn * 0.05, (double)lerpOut * 0.05, speed, this.force);
    }
}

