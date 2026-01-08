/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.lumine.mythic.api.adapters.AbstractEntity
 *  io.lumine.mythic.api.config.MythicLineConfig
 *  io.lumine.mythic.api.skills.ITargetedEntitySkill
 *  io.lumine.mythic.api.skills.SkillMetadata
 *  io.lumine.mythic.api.skills.SkillResult
 *  org.bukkit.entity.Player
 */
package com.ticxo.modelengine.core21.mythic.mechanics.entity;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.entity.data.BukkitEntityData;
import com.ticxo.modelengine.api.entity.data.IEntityData;
import com.ticxo.modelengine.api.model.ModeledEntity;
import com.ticxo.modelengine.core21.mythic.utils.MythicMechanic;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import org.bukkit.entity.Player;

@MythicMechanic(name="pairmodel", aliases={})
public class PairModelMechanic
implements ITargetedEntitySkill {
    private final boolean hidden;
    private final boolean remove;

    public PairModelMechanic(MythicLineConfig mlc) {
        this.hidden = mlc.getBoolean(new String[]{"h", "hidden"}, false);
        this.remove = mlc.getBoolean(new String[]{"r", "remove"}, false);
    }

    public SkillResult castAtEntity(SkillMetadata meta, AbstractEntity target) {
        ModeledEntity model = ModelEngineAPI.getModeledEntity(meta.getCaster().getEntity().getUniqueId());
        if (model == null) {
            return SkillResult.CONDITION_FAILED;
        }
        IEntityData iEntityData = model.getBase().getData();
        if (iEntityData instanceof BukkitEntityData) {
            BukkitEntityData data = (BukkitEntityData)iEntityData;
            iEntityData = target.getBukkitEntity();
            if (iEntityData instanceof Player) {
                Player player = (Player)iEntityData;
                if (this.hidden) {
                    if (this.remove) {
                        data.getTracked().removeForcedHidden(player.getUniqueId());
                    } else {
                        data.getTracked().addForcedHidden(player.getUniqueId());
                    }
                } else if (this.remove) {
                    data.getTracked().removeForcedPairing(player.getUniqueId());
                } else {
                    data.getTracked().addForcedPairing(player.getUniqueId());
                }
            }
        }
        return SkillResult.SUCCESS;
    }

    public boolean getTargetsCreatives() {
        return true;
    }
}

