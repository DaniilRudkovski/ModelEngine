/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.lumine.mythic.api.adapters.AbstractEntity
 *  io.lumine.mythic.api.skills.INoTargetSkill
 *  io.lumine.mythic.api.skills.ITargetedEntitySkill
 *  io.lumine.mythic.api.skills.SkillMetadata
 *  io.lumine.mythic.api.skills.SkillResult
 *  org.joml.Vector3f
 */
package com.ticxo.modelengine.core21.mythic.mechanics.vfx;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.vfx.VFX;
import com.ticxo.modelengine.core21.mythic.MythicUtils;
import com.ticxo.modelengine.core21.mythic.utils.MythicMechanic;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.skills.INoTargetSkill;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import java.util.UUID;
import org.joml.Vector3f;

@MythicMechanic(name="vfxscalereset", aliases={"vfxsclreset", "vfxscareset"})
public class VFXResetScaleMechanic
implements ITargetedEntitySkill,
INoTargetSkill {
    public SkillResult castAtEntity(SkillMetadata meta, AbstractEntity target) {
        VFX vfx = ModelEngineAPI.getVFX(target.getBukkitEntity());
        if (vfx == null) {
            return SkillResult.INVALID_TARGET;
        }
        vfx.setScale(new Vector3f(1.0f));
        return SkillResult.SUCCESS;
    }

    public SkillResult cast(SkillMetadata meta) {
        UUID uuid = MythicUtils.getVFXUniqueId(meta);
        VFX vfx = ModelEngineAPI.getAPI().getVFXUpdater().getVFX(uuid);
        if (vfx == null) {
            return SkillResult.INVALID_TARGET;
        }
        vfx.setScale(new Vector3f(1.0f));
        return SkillResult.SUCCESS;
    }
}

