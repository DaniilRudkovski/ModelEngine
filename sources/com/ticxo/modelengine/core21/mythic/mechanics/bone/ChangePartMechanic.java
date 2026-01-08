/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.lumine.mythic.api.adapters.AbstractEntity
 *  io.lumine.mythic.api.config.MythicLineConfig
 *  io.lumine.mythic.api.skills.ITargetedEntitySkill
 *  io.lumine.mythic.api.skills.SkillMetadata
 *  io.lumine.mythic.api.skills.SkillResult
 *  io.lumine.mythic.api.skills.placeholders.PlaceholderString
 *  io.lumine.mythic.core.skills.placeholders.PlaceholderMeta
 */
package com.ticxo.modelengine.core21.mythic.mechanics.bone;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.generator.blueprint.BlueprintBone;
import com.ticxo.modelengine.api.generator.blueprint.ModelBlueprint;
import com.ticxo.modelengine.api.model.ModeledEntity;
import com.ticxo.modelengine.core21.mythic.MythicUtils;
import com.ticxo.modelengine.core21.mythic.utils.MythicMechanic;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.core.skills.placeholders.PlaceholderMeta;

@MythicMechanic(name="changepart", aliases={})
public class ChangePartMechanic
implements ITargetedEntitySkill {
    private final PlaceholderString modelId;
    private final PlaceholderString partId;
    private final PlaceholderString nModelId;
    private final PlaceholderString nPartId;

    public ChangePartMechanic(MythicLineConfig mlc) {
        this.modelId = mlc.getPlaceholderString(new String[]{"m", "mid", "model", "modelid"}, null, new String[0]);
        this.partId = mlc.getPlaceholderString(new String[]{"p", "pid", "part", "partid"}, null, new String[0]);
        this.nModelId = mlc.getPlaceholderString(new String[]{"nm", "nmid", "newmodel", "newmodelid"}, null, new String[0]);
        this.nPartId = mlc.getPlaceholderString(new String[]{"np", "npid", "newpart", "newpartid"}, null, new String[0]);
    }

    public SkillResult castAtEntity(SkillMetadata meta, AbstractEntity target) {
        ModeledEntity model = ModelEngineAPI.getModeledEntity(target.getUniqueId());
        if (model == null) {
            return SkillResult.CONDITION_FAILED;
        }
        String modelId = MythicUtils.getOrNullLowercase(this.modelId, (PlaceholderMeta)meta, target);
        String partId = MythicUtils.getOrNullLowercase(this.partId, (PlaceholderMeta)meta, target);
        String nModelId = MythicUtils.getOrNullLowercase(this.nModelId, (PlaceholderMeta)meta, target);
        String nPartId = MythicUtils.getOrNullLowercase(this.nPartId, (PlaceholderMeta)meta, target);
        if (modelId == null || partId == null || nModelId == null || nPartId == null) {
            return SkillResult.INVALID_CONFIG;
        }
        model.getModel(modelId).flatMap(activeModel -> activeModel.getBone(partId)).ifPresent(bone -> {
            if (!bone.isRenderer()) {
                return;
            }
            ModelBlueprint blueprint = ModelEngineAPI.getBlueprint(nModelId);
            if (blueprint == null) {
                return;
            }
            BlueprintBone blueprintBone = blueprint.getFlatMap().get(nPartId);
            if (blueprintBone == null || !blueprintBone.isRenderer()) {
                return;
            }
            bone.setModelScale(blueprintBone.getScale());
            bone.setModel(blueprintBone);
        });
        return SkillResult.SUCCESS;
    }
}

