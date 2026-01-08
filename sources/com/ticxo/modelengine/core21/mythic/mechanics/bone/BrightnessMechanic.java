/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.lumine.mythic.api.adapters.AbstractEntity
 *  io.lumine.mythic.api.config.MythicLineConfig
 *  io.lumine.mythic.api.skills.ITargetedEntitySkill
 *  io.lumine.mythic.api.skills.SkillMetadata
 *  io.lumine.mythic.api.skills.SkillResult
 *  io.lumine.mythic.api.skills.placeholders.PlaceholderInt
 *  io.lumine.mythic.api.skills.placeholders.PlaceholderString
 *  io.lumine.mythic.core.skills.placeholders.PlaceholderMeta
 */
package com.ticxo.modelengine.core21.mythic.mechanics.bone;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.ModeledEntity;
import com.ticxo.modelengine.api.model.bone.ModelBone;
import com.ticxo.modelengine.core21.mythic.MythicUtils;
import com.ticxo.modelengine.core21.mythic.utils.MythicMechanic;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.placeholders.PlaceholderInt;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.core.skills.placeholders.PlaceholderMeta;
import java.util.Map;

@MythicMechanic(name="brightness", aliases={"light"})
public class BrightnessMechanic
implements ITargetedEntitySkill {
    private final PlaceholderString modelId;
    private final PlaceholderString partId;
    private final PlaceholderInt blockLight;
    private final PlaceholderInt skyLight;
    private final boolean exactMatch;
    private final boolean child;

    public BrightnessMechanic(MythicLineConfig mlc) {
        this.modelId = mlc.getPlaceholderString(new String[]{"m", "mid", "model", "modelid"}, null, new String[0]);
        this.partId = mlc.getPlaceholderString(new String[]{"p", "pid", "part", "partid"}, "", new String[0]);
        this.blockLight = mlc.getPlaceholderInteger(new String[]{"block", "b"}, -1, new String[0]);
        this.skyLight = mlc.getPlaceholderInteger(new String[]{"sky", "s"}, -1, new String[0]);
        this.exactMatch = mlc.getBoolean(new String[]{"em", "exact", "match", "exactmatch"}, true);
        this.child = mlc.getBoolean(new String[]{"c", "child"}, false);
    }

    public SkillResult castAtEntity(SkillMetadata meta, AbstractEntity target) {
        ModeledEntity model = ModelEngineAPI.getModeledEntity(target.getUniqueId());
        if (model == null) {
            return SkillResult.CONDITION_FAILED;
        }
        String partId = MythicUtils.getOrNullLowercase(this.partId, (PlaceholderMeta)meta, target);
        String modelId = MythicUtils.getOrNullLowercase(this.modelId, (PlaceholderMeta)meta, target);
        int block = this.blockLight.get((PlaceholderMeta)meta, target);
        int sky = this.skyLight.get((PlaceholderMeta)meta, target);
        MythicUtils.executeOptModelId(model, modelId, activeModel -> this.light((ActiveModel)activeModel, partId, block, sky));
        return SkillResult.SUCCESS;
    }

    private void light(ActiveModel activeModel, String partId, int block, int sky) {
        if (partId.isBlank()) {
            activeModel.setBlockLight(block);
            activeModel.setSkyLight(sky);
            return;
        }
        if (this.exactMatch) {
            activeModel.getBone(partId).ifPresent(bone -> MythicUtils.executeBoneChild(this.child, bone, v -> this.setLight((ModelBone)v, block, sky)));
            return;
        }
        for (Map.Entry<String, ModelBone> entry : activeModel.getBones().entrySet()) {
            if (!entry.getKey().contains(partId)) continue;
            MythicUtils.executeBoneChild(this.child, entry.getValue(), bone -> this.setLight((ModelBone)bone, block, sky));
        }
    }

    private void setLight(ModelBone bone, int block, int sky) {
        bone.setBlockLight(block);
        bone.setSkyLight(sky);
    }
}

