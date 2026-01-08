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
package com.ticxo.modelengine.core21.mythic.mechanics.leash;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.model.ModeledEntity;
import com.ticxo.modelengine.api.model.bone.manager.LeashManager;
import com.ticxo.modelengine.core21.mythic.MythicUtils;
import com.ticxo.modelengine.core21.mythic.utils.MythicMechanic;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.core.skills.placeholders.PlaceholderMeta;

@MythicMechanic(name="leashself", aliases={})
public class LeashSelfMechanic
implements ITargetedEntitySkill {
    private final PlaceholderString modelId;
    private final PlaceholderString sourceId;
    private final PlaceholderString destId;

    public LeashSelfMechanic(MythicLineConfig mlc) {
        this.modelId = mlc.getPlaceholderString(new String[]{"m", "mid", "model", "modelid"}, null, new String[0]);
        this.sourceId = mlc.getPlaceholderString(new String[]{"s", "sid", "src", "source", "sourceid"}, null, new String[0]);
        this.destId = mlc.getPlaceholderString(new String[]{"d", "did", "dest", "destid"}, null, new String[0]);
    }

    public SkillResult castAtEntity(SkillMetadata meta, AbstractEntity target) {
        AbstractEntity caster = meta.getCaster().getEntity();
        ModeledEntity model = ModelEngineAPI.getModeledEntity(caster.getUniqueId());
        if (model == null) {
            return SkillResult.CONDITION_FAILED;
        }
        String modelId = MythicUtils.getOrNullLowercase(this.modelId, (PlaceholderMeta)meta, target);
        model.getModel(modelId).ifPresent(activeModel -> activeModel.getLeashManager().ifPresent(leashManager -> {
            String sourceId = MythicUtils.getOrNullLowercase(this.sourceId, (PlaceholderMeta)meta, target);
            String destId = MythicUtils.getOrNullLowercase(this.destId, (PlaceholderMeta)meta, target);
            ((LeashManager)((Object)leashManager)).connectLeash(destId, sourceId);
        }));
        return SkillResult.SUCCESS;
    }
}

