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
package com.ticxo.modelengine.core21.mythic.mechanics.model;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.generator.blueprint.BlueprintBone;
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
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.core.skills.placeholders.PlaceholderMeta;
import java.util.Set;

@MythicMechanic(name="remapmodel", aliases={"remap"})
public class RemapModelMechanic
implements ITargetedEntitySkill {
    private final PlaceholderString modelId;
    private final PlaceholderString newModelId;
    private final PlaceholderString map;

    public RemapModelMechanic(MythicLineConfig mlc) {
        this.modelId = mlc.getPlaceholderString(new String[]{"m", "mid", "model", "modelid"}, null, new String[0]);
        this.newModelId = mlc.getPlaceholderString(new String[]{"n", "nid", "newmodel", "newmodelid"}, null, new String[0]);
        this.map = mlc.getPlaceholderString(new String[]{"map"}, null, new String[0]);
    }

    public SkillResult castAtEntity(SkillMetadata meta, AbstractEntity target) {
        ModeledEntity model = ModelEngineAPI.getModeledEntity(target.getUniqueId());
        if (model == null) {
            return SkillResult.CONDITION_FAILED;
        }
        String modelId = MythicUtils.getOrNullLowercase(this.modelId, (PlaceholderMeta)meta, target);
        ActiveModel activeModel = MythicUtils.getActiveModelOrNull(model, modelId);
        if (activeModel == null) {
            return SkillResult.INVALID_CONFIG;
        }
        String newModelId = MythicUtils.getOrNullLowercase(this.newModelId, (PlaceholderMeta)meta, target);
        ModelBlueprint blueprint = MythicUtils.getBlueprintOrNull(newModelId);
        if (blueprint == null) {
            return SkillResult.INVALID_CONFIG;
        }
        if (this.map != null) {
            String mapId = MythicUtils.getOrNullLowercase(this.map, (PlaceholderMeta)meta, target);
            ModelBlueprint mapBlueprint = MythicUtils.getBlueprintOrNull(mapId);
            if (mapBlueprint == null) {
                return SkillResult.INVALID_CONFIG;
            }
            for (String bone : mapBlueprint.getFlatMap().keySet()) {
                BlueprintBone blueprintBone = blueprint.getFlatMap().get(bone);
                if (blueprintBone == null || !blueprintBone.isRenderer()) continue;
                activeModel.getBone(bone).ifPresent(replaced -> {
                    if (replaced.isRenderer()) {
                        replaced.setModel(blueprintBone);
                    }
                });
            }
        } else {
            Set<String> bones = blueprint.getFlatMap().size() < activeModel.getBones().size() ? blueprint.getFlatMap().keySet() : activeModel.getBones().keySet();
            for (String bone : bones) {
                BlueprintBone blueprintBone = blueprint.getFlatMap().get(bone);
                if (blueprintBone == null || !blueprintBone.isRenderer()) continue;
                activeModel.getBone(bone).ifPresent(replaced -> {
                    if (replaced.isRenderer()) {
                        replaced.setModelScale(blueprintBone.getScale());
                        replaced.setModel(blueprintBone);
                    }
                });
            }
        }
        return null;
    }
}

