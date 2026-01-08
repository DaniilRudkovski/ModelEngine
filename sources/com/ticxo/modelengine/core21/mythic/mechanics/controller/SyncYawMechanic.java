/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.lumine.mythic.api.adapters.AbstractEntity
 *  io.lumine.mythic.api.config.MythicLineConfig
 *  io.lumine.mythic.api.skills.ITargetedEntitySkill
 *  io.lumine.mythic.api.skills.SkillMetadata
 *  io.lumine.mythic.api.skills.SkillResult
 *  org.bukkit.entity.Entity
 */
package com.ticxo.modelengine.core21.mythic.mechanics.controller;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.nms.entity.EntityHandler;
import com.ticxo.modelengine.core21.mythic.utils.MythicMechanic;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import java.util.Locale;
import org.bukkit.entity.Entity;

@MythicMechanic(name="syncyaw", aliases={})
public class SyncYawMechanic
implements ITargetedEntitySkill {
    private final String target;
    private final String body;

    public SyncYawMechanic(MythicLineConfig mlc) {
        this.target = mlc.getString(new String[]{"target", "t", "to"}, "yaw", new String[0]);
        this.body = mlc.getString(new String[]{"body", "b", "src", "from"}, "head", new String[0]);
    }

    public SkillResult castAtEntity(SkillMetadata meta, AbstractEntity target) {
        Entity entity = target.getBukkitEntity();
        EntityHandler handler = ModelEngineAPI.getEntityHandler();
        float yaw = switch (this.body.toLowerCase(Locale.ENGLISH)) {
            case "yaw" -> handler.getYRot(entity);
            case "body", "true" -> handler.getYBodyRot(entity);
            default -> handler.getYHeadRot(entity);
        };
        switch (this.target.toLowerCase(Locale.ENGLISH)) {
            case "yaw": {
                handler.setYRot(entity, yaw);
                break;
            }
            case "body": {
                handler.setYBodyRot(entity, yaw);
                break;
            }
            case "head": {
                handler.setYHeadRot(entity, yaw);
                break;
            }
            case "all": {
                handler.setYRot(entity, yaw);
                handler.setYBodyRot(entity, yaw);
                handler.setYHeadRot(entity, yaw);
            }
        }
        return SkillResult.SUCCESS;
    }
}

