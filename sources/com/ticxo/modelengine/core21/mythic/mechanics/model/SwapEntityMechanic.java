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
import java.util.Optional;

@MythicMechanic(name="swapentity", aliases={})
public class SwapEntityMechanic
implements ITargetedEntitySkill {
    private final PlaceholderString modelId;
    private final boolean hitbox;
    private final boolean invisible;
    private final PlaceholderDouble stepHeight;
    private final PlaceholderInt viewRadius;

    public SwapEntityMechanic(MythicLineConfig mlc) {
        this.modelId = mlc.getPlaceholderString(new String[]{"m", "mid", "model", "modelid"}, null, new String[0]);
        this.hitbox = mlc.getBoolean(new String[]{"h", "hitbox"}, true);
        this.invisible = mlc.getBoolean(new String[]{"i", "invis", "invisible"}, true);
        this.stepHeight = mlc.getPlaceholderDouble(new String[]{"s", "step"}, 0.5, new String[0]);
        this.viewRadius = mlc.getPlaceholderInteger(new String[]{"rad", "radius"}, 0, new String[0]);
    }

    public SkillResult castAtEntity(SkillMetadata meta, AbstractEntity target) {
        AbstractEntity caster = meta.getCaster().getEntity();
        ModeledEntity casterModel = ModelEngineAPI.getModeledEntity(caster.getUniqueId());
        if (casterModel == null) {
            return SkillResult.CONDITION_FAILED;
        }
        String modelId = MythicUtils.getOrNullLowercase(this.modelId, (PlaceholderMeta)meta, target);
        if (modelId == null) {
            return SkillResult.INVALID_CONFIG;
        }
        if (!casterModel.getModels().containsKey(modelId)) {
            return SkillResult.INVALID_CONFIG;
        }
        ModeledEntity targetModel = ModelEngineAPI.getOrCreateModeledEntity(target.getBukkitEntity());
        if (targetModel.getModel(modelId).isPresent()) {
            return SkillResult.CONDITION_FAILED;
        }
        double stepHeight = this.stepHeight.get((PlaceholderMeta)meta, target);
        int viewRadius = this.viewRadius.get((PlaceholderMeta)meta, target);
        Optional<ActiveModel> maybeModel = casterModel.removeModel(modelId);
        maybeModel.ifPresent(activeModel -> {
            targetModel.setBaseEntityVisible(!this.invisible);
            targetModel.getBase().setMaxStepHeight(stepHeight);
            if (viewRadius > 0) {
                targetModel.getBase().setRenderRadius(viewRadius);
            }
            targetModel.addModel((ActiveModel)activeModel, this.hitbox).ifPresent(ActiveModel::destroy);
        });
        return SkillResult.SUCCESS;
    }
}

