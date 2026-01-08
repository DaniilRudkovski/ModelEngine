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
 *  org.bukkit.Color
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
import org.bukkit.Color;

@MythicMechanic(name="tint", aliases={"color"})
public class TintMechanic
implements ITargetedEntitySkill {
    private final PlaceholderString modelId;
    private final PlaceholderString partId;
    private final PlaceholderString color;
    private final boolean damageTint;
    private final boolean exactMatch;
    private final boolean child;

    public TintMechanic(MythicLineConfig mlc) {
        this.modelId = mlc.getPlaceholderString(new String[]{"m", "mid", "model", "modelid"}, null, new String[0]);
        this.partId = mlc.getPlaceholderString(new String[]{"p", "pid", "part", "partid"}, "", new String[0]);
        this.damageTint = mlc.getBoolean(new String[]{"d", "dmg", "damage", "damagetint"}, false);
        this.color = mlc.getPlaceholderString(new String[]{"c", "color"}, this.damageTint ? "FF6666" : "FFFFFF", new String[0]);
        this.exactMatch = mlc.getBoolean(new String[]{"em", "exact", "match", "exactmatch"}, true);
        this.child = mlc.getBoolean(new String[]{"c", "child"}, false);
    }

    public SkillResult castAtEntity(SkillMetadata meta, AbstractEntity target) {
        ModeledEntity model = ModelEngineAPI.getModeledEntity(target.getUniqueId());
        if (model == null) {
            return SkillResult.CONDITION_FAILED;
        }
        String colorString = MythicUtils.getOrNull(this.color, (PlaceholderMeta)meta, target);
        if (colorString.startsWith("#")) {
            colorString = colorString.substring(1);
        }
        Color color = Color.fromRGB((int)Integer.parseInt(colorString, 16));
        String partId = MythicUtils.getOrNullLowercase(this.partId, (PlaceholderMeta)meta, target);
        String modelId = MythicUtils.getOrNullLowercase(this.modelId, (PlaceholderMeta)meta, target);
        MythicUtils.executeOptModelId(model, modelId, activeModel -> this.color((ActiveModel)activeModel, partId, color));
        return SkillResult.SUCCESS;
    }

    private void color(ActiveModel activeModel, String partId, Color color) {
        if (partId.isBlank()) {
            if (this.damageTint) {
                activeModel.setDamageTint(color);
            } else {
                activeModel.setDefaultTint(color);
            }
            return;
        }
        if (this.exactMatch) {
            activeModel.getBone(partId).ifPresent(bone -> MythicUtils.executeBoneChild(this.child, bone, v -> this.setColor((ModelBone)v, color)));
            return;
        }
        for (Map.Entry<String, ModelBone> entry : activeModel.getBones().entrySet()) {
            if (!entry.getKey().contains(partId)) continue;
            MythicUtils.executeBoneChild(this.child, entry.getValue(), bone -> this.setColor((ModelBone)bone, color));
        }
    }

    private void setColor(ModelBone bone, Color color) {
        if (this.damageTint) {
            bone.setDamageTint(color);
        } else {
            bone.setDefaultTint(color);
        }
    }
}

