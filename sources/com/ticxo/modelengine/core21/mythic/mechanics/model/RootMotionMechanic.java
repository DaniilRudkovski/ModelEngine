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
 *  io.lumine.mythic.core.skills.placeholders.PlaceholderMeta
 */
package com.ticxo.modelengine.core21.mythic.mechanics.model;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.animation.rootmotion.RootMotionHandler;
import com.ticxo.modelengine.api.model.ModeledEntity;
import com.ticxo.modelengine.core21.mythic.utils.MythicMechanic;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.placeholders.PlaceholderBoolean;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.core.skills.placeholders.PlaceholderMeta;

@MythicMechanic(name="rootmotion", aliases={})
public class RootMotionMechanic
implements ITargetedEntitySkill {
    private final PlaceholderDouble weight;
    private final PlaceholderDouble baseWeight;
    private final PlaceholderBoolean override;

    public RootMotionMechanic(MythicLineConfig mlc) {
        this.weight = mlc.getPlaceholderDouble(new String[]{"w", "weight"}, -1.0, new String[0]);
        this.baseWeight = mlc.getPlaceholderDouble(new String[]{"bw", "baseweight"}, -1.0, new String[0]);
        this.override = mlc.getPlaceholderBoolean(new String[]{"ov", "override"}, null);
    }

    public SkillResult castAtEntity(SkillMetadata meta, AbstractEntity target) {
        Boolean override;
        ModeledEntity model = ModelEngineAPI.getModeledEntity(target.getUniqueId());
        if (model == null) {
            return SkillResult.CONDITION_FAILED;
        }
        RootMotionHandler handler = model.getRootMotionHandler();
        double weight = this.weight == null ? -1.0 : this.weight.get((PlaceholderMeta)meta, target);
        double baseWeight = this.baseWeight == null ? -1.0 : this.baseWeight.get((PlaceholderMeta)meta, target);
        Boolean bl = override = this.override == null ? null : Boolean.valueOf(this.override.get((PlaceholderMeta)meta, target));
        if (weight >= 0.0) {
            handler.setWeight(weight);
        }
        if (baseWeight >= 0.0) {
            handler.setBaseWeight(baseWeight);
        }
        if (override != null) {
            handler.setOverride(override);
        }
        return SkillResult.SUCCESS;
    }
}

