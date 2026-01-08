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
 *  it.unimi.dsi.fastutil.Pair
 */
package com.ticxo.modelengine.core21.mythic.mechanics.model;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.animation.ModelState;
import com.ticxo.modelengine.api.animation.handler.AnimationHandler;
import com.ticxo.modelengine.api.animation.handler.IPriorityHandler;
import com.ticxo.modelengine.api.animation.handler.IStateMachineHandler;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.ModeledEntity;
import com.ticxo.modelengine.api.utils.MiscUtils;
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
import it.unimi.dsi.fastutil.Pair;

@MythicMechanic(name="defaultstate", aliases={"defaultanimation"})
public class DefaultStateMechanic
implements ITargetedEntitySkill {
    private final MythicLineConfig config;
    private final PlaceholderString modelId;
    private final PlaceholderString type;
    private ModelState stateType;

    public DefaultStateMechanic(MythicLineConfig mlc) {
        this.config = mlc;
        this.modelId = mlc.getPlaceholderString(new String[]{"m", "mid", "model", "modelid"}, null, new String[0]);
        this.type = mlc.getPlaceholderString(new String[]{"t", "type"}, null, new String[0]);
    }

    public SkillResult castAtEntity(SkillMetadata meta, AbstractEntity target) {
        ModeledEntity model = ModelEngineAPI.getModeledEntity(target.getUniqueId());
        if (model == null) {
            return SkillResult.CONDITION_FAILED;
        }
        String type = MythicUtils.getOrNull(this.type, (PlaceholderMeta)meta, target);
        if (type == null) {
            return SkillResult.INVALID_CONFIG;
        }
        this.stateType = ModelState.get(type);
        String modelId = MythicUtils.getOrNullLowercase(this.modelId, (PlaceholderMeta)meta, target);
        MythicUtils.executeOptModelId(model, modelId, activeModel -> this.configureModel((ActiveModel)activeModel, meta, target));
        return SkillResult.SUCCESS;
    }

    private void configureModel(ActiveModel activeModel, SkillMetadata meta, AbstractEntity target) {
        IPriorityHandler priorityHandler;
        AnimationHandler animationHandler = activeModel.getAnimationHandler();
        AnimationHandler.DefaultProperty property = animationHandler.getDefaultProperty(this.stateType);
        PlaceholderString statePlaceholder = this.config.getPlaceholderString(new String[]{"s", "state"}, property.getAnimation(), new String[0]);
        PlaceholderInt lerpInPlaceholder = this.config.getPlaceholderInteger(new String[]{"li", "lerpin"}, (int)(property.getLerpIn() * 20.0), new String[0]);
        PlaceholderInt lerpOutPlaceholder = this.config.getPlaceholderInteger(new String[]{"lo", "lerpout"}, (int)(property.getLerpOut() * 20.0), new String[0]);
        PlaceholderDouble speedPlaceholder = this.config.getPlaceholderDouble(new String[]{"sp", "speed"}, property.getSpeed(), new String[0]);
        PlaceholderBoolean mergePlaceholder = this.config.getPlaceholderBoolean(new String[]{"mg", "merge"}, Boolean.valueOf(property.isMerge()));
        Pair<String, String> statePair = MiscUtils.getAnimationRef(statePlaceholder.get((PlaceholderMeta)meta, target));
        PlaceholderString srcId = this.config.getPlaceholderString(new String[]{"src", "sid", "srcmodel", "srcmodelid"}, (String)statePair.first(), new String[0]);
        String state = MiscUtils.createAnimationRef(MythicUtils.getOrNull(srcId, (PlaceholderMeta)meta, target), (String)statePair.second());
        double lerpIn = (double)lerpInPlaceholder.get((PlaceholderMeta)meta, target) * 0.05;
        double lerpOut = (double)lerpOutPlaceholder.get((PlaceholderMeta)meta, target) * 0.05;
        double speed = speedPlaceholder.get((PlaceholderMeta)meta, target);
        boolean merge = mergePlaceholder.get((PlaceholderMeta)meta, target);
        animationHandler.setDefaultProperty(new AnimationHandler.DefaultProperty(this.stateType, state, lerpIn, lerpOut, speed, merge));
        if (animationHandler instanceof IPriorityHandler && (priorityHandler = (IPriorityHandler)animationHandler).isPlayingAnimation(property.getAnimation())) {
            priorityHandler.stopAnimation(property.getAnimation());
            priorityHandler.playState(this.stateType);
        } else if (animationHandler instanceof IStateMachineHandler) {
            IStateMachineHandler stateMachineHandler = (IStateMachineHandler)animationHandler;
            stateMachineHandler.refreshState(property);
        }
    }
}

