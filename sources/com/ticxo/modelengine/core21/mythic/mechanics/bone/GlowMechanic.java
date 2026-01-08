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
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.core.skills.placeholders.PlaceholderMeta;
import java.util.Map;

@MythicMechanic(name="glow", aliases={"glowbone"})
public class GlowMechanic
implements ITargetedEntitySkill {
    private final PlaceholderString modelId;
    private final PlaceholderString partId;
    private final PlaceholderString color;
    private final boolean glowing;
    private final boolean exactMatch;
    private final boolean child;

    public GlowMechanic(MythicLineConfig mlc) {
        this.modelId = mlc.getPlaceholderString(new String[]{"m", "mid", "model", "modelid"}, null, new String[0]);
        this.partId = mlc.getPlaceholderString(new String[]{"p", "pid", "part", "partid"}, "", new String[0]);
        this.glowing = mlc.getBoolean(new String[]{"g", "glow"}, true);
        this.color = mlc.getPlaceholderString(new String[]{"c", "color"}, null, new String[0]);
        this.exactMatch = mlc.getBoolean(new String[]{"em", "exact", "match", "exactmatch"}, true);
        this.child = mlc.getBoolean(new String[]{"c", "child"}, false);
    }

    public SkillResult castAtEntity(SkillMetadata meta, AbstractEntity target) {
        Integer color;
        ModeledEntity model = ModelEngineAPI.getModeledEntity(target.getUniqueId());
        if (model == null) {
            return SkillResult.CONDITION_FAILED;
        }
        String colorString = MythicUtils.getOrNull(this.color, (PlaceholderMeta)meta, target);
        if (colorString != null) {
            if (colorString.startsWith("#")) {
                colorString = colorString.substring(1);
            }
            color = Integer.parseInt(colorString, 16);
        } else {
            color = null;
        }
        String partId = MythicUtils.getOrNullLowercase(this.partId, (PlaceholderMeta)meta, target);
        String modelId = MythicUtils.getOrNullLowercase(this.modelId, (PlaceholderMeta)meta, target);
        MythicUtils.executeOptModelId(model, modelId, activeModel -> this.glow((ActiveModel)activeModel, partId, color));
        return SkillResult.SUCCESS;
    }

    private void glow(ActiveModel activeModel, String partId, Integer color) {
        if (partId.isBlank()) {
            activeModel.setGlowing(this.glowing);
            activeModel.setGlowColor(color);
            return;
        }
        if (this.exactMatch) {
            activeModel.getBone(partId).ifPresent(bone -> MythicUtils.executeBoneChild(this.child, bone, bone1 -> this.setGlow((ModelBone)bone1, color)));
            return;
        }
        for (Map.Entry<String, ModelBone> entry : activeModel.getBones().entrySet()) {
            if (!entry.getKey().contains(partId)) continue;
            MythicUtils.executeBoneChild(this.child, entry.getValue(), bone -> this.setGlow((ModelBone)bone, color));
        }
    }

    private void setGlow(ModelBone bone, Integer color) {
        bone.setGlowing(this.glowing);
        bone.setGlowColor(color);
    }
}

