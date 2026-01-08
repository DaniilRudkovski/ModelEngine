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
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Player
 */
package com.ticxo.modelengine.core21.mythic.mechanics.disguise;

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
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.core.skills.placeholders.PlaceholderMeta;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

@MythicMechanic(name="undisguise", aliases={"modelundisguise"})
public class UndisguiseMechanic
implements ITargetedEntitySkill {
    private final PlaceholderString modelId;

    public UndisguiseMechanic(MythicLineConfig mlc) {
        this.modelId = mlc.getPlaceholderString(new String[]{"m", "mid", "model", "modelid"}, null, new String[0]);
    }

    public SkillResult castAtEntity(SkillMetadata meta, AbstractEntity target) {
        if (!target.isPlayer()) {
            return SkillResult.INVALID_TARGET;
        }
        Player player = (Player)target.getBukkitEntity();
        ModeledEntity modeledEntity = ModelEngineAPI.getModeledEntity((Entity)player);
        if (modeledEntity != null) {
            String modelId = MythicUtils.getOrNullLowercase(this.modelId, (PlaceholderMeta)meta, target);
            if (modelId == null) {
                modeledEntity.markRemoved();
                ModelEngineAPI.getEntityHandler().setForcedInvisible((Entity)player, false);
                ModelEngineAPI.getEntityHandler().forceSpawn((Entity)player);
            } else {
                modeledEntity.removeModel(modelId).ifPresent(ActiveModel::destroy);
                if (modeledEntity.getModels().isEmpty()) {
                    modeledEntity.markRemoved();
                    ModelEngineAPI.getEntityHandler().setForcedInvisible((Entity)player, false);
                    ModelEngineAPI.getEntityHandler().forceSpawn((Entity)player);
                }
            }
        }
        return SkillResult.SUCCESS;
    }

    public boolean getTargetsCreatives() {
        return true;
    }
}

