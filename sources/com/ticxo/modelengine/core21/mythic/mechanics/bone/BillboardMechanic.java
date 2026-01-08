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
 *  org.bukkit.entity.Display$Billboard
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
import java.util.Locale;
import java.util.Map;
import org.bukkit.entity.Display;

@MythicMechanic(name="billboard", aliases={})
public class BillboardMechanic
implements ITargetedEntitySkill {
    private final PlaceholderString modelId;
    private final PlaceholderString partId;
    private final PlaceholderString billboard;
    private final boolean exactMatch;
    private final boolean child;

    public BillboardMechanic(MythicLineConfig mlc) {
        this.modelId = mlc.getPlaceholderString(new String[]{"m", "mid", "model", "modelid"}, null, new String[0]);
        this.partId = mlc.getPlaceholderString(new String[]{"p", "pid", "part", "partid"}, "", new String[0]);
        this.billboard = mlc.getPlaceholderString(new String[]{"b", "bb", "bill", "billboard"}, null, new String[0]);
        this.exactMatch = mlc.getBoolean(new String[]{"em", "exact", "match", "exactmatch"}, true);
        this.child = mlc.getBoolean(new String[]{"c", "child"}, false);
    }

    public SkillResult castAtEntity(SkillMetadata meta, AbstractEntity target) {
        ModeledEntity model = ModelEngineAPI.getModeledEntity(target.getUniqueId());
        if (model == null) {
            return SkillResult.CONDITION_FAILED;
        }
        try {
            String billboard = MythicUtils.getOrNull(this.billboard, (PlaceholderMeta)meta, target).toUpperCase(Locale.ENGLISH);
            Display.Billboard bb = Display.Billboard.valueOf((String)billboard);
            String partId = MythicUtils.getOrNullLowercase(this.partId, (PlaceholderMeta)meta, target);
            String modelId = MythicUtils.getOrNullLowercase(this.modelId, (PlaceholderMeta)meta, target);
            MythicUtils.executeOptModelId(model, modelId, activeModel -> this.billboard((ActiveModel)activeModel, partId, bb));
            return SkillResult.SUCCESS;
        }
        catch (Exception e) {
            e.printStackTrace();
            return SkillResult.ERROR;
        }
    }

    private void billboard(ActiveModel activeModel, String partId, Display.Billboard billboard) {
        if (partId.isBlank()) {
            activeModel.setBillboard(billboard);
            return;
        }
        if (this.exactMatch) {
            activeModel.getBone(partId).ifPresent(bone -> MythicUtils.executeBoneChild(this.child, bone, v -> v.setBillboard(billboard)));
            return;
        }
        for (Map.Entry<String, ModelBone> entry : activeModel.getBones().entrySet()) {
            if (!entry.getKey().contains(partId)) continue;
            MythicUtils.executeBoneChild(this.child, entry.getValue(), bone -> bone.setBillboard(billboard));
        }
    }
}

