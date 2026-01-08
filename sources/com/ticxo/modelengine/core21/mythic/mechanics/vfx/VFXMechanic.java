/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.lumine.mythic.api.adapters.AbstractEntity
 *  io.lumine.mythic.api.config.MythicLineConfig
 *  io.lumine.mythic.api.skills.INoTargetSkill
 *  io.lumine.mythic.api.skills.IParentSkill
 *  io.lumine.mythic.api.skills.ITargetedEntitySkill
 *  io.lumine.mythic.api.skills.SkillMetadata
 *  io.lumine.mythic.api.skills.SkillResult
 *  io.lumine.mythic.api.skills.placeholders.PlaceholderInt
 *  io.lumine.mythic.api.skills.placeholders.PlaceholderString
 *  io.lumine.mythic.core.skills.placeholders.PlaceholderMeta
 *  io.lumine.mythic.core.skills.projectiles.ProjectileBulletableTracker
 *  org.bukkit.Color
 */
package com.ticxo.modelengine.core21.mythic.mechanics.vfx;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.nms.entity.wrapper.BodyRotationController;
import com.ticxo.modelengine.api.vfx.VFX;
import com.ticxo.modelengine.core21.mythic.MythicUtils;
import com.ticxo.modelengine.core21.mythic.compatibility.ProjectileEntity;
import com.ticxo.modelengine.core21.mythic.utils.MythicMechanic;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.INoTargetSkill;
import io.lumine.mythic.api.skills.IParentSkill;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.placeholders.PlaceholderInt;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.core.skills.placeholders.PlaceholderMeta;
import io.lumine.mythic.core.skills.projectiles.ProjectileBulletableTracker;
import java.util.UUID;
import org.bukkit.Color;

@MythicMechanic(name="vfx", aliases={})
public class VFXMechanic
implements ITargetedEntitySkill,
INoTargetSkill {
    private final PlaceholderString modelId;
    private final PlaceholderString partId;
    private final boolean remove;
    private final PlaceholderInt radius;
    private final PlaceholderString color;
    private final boolean enchant;
    private final boolean visible;
    private final boolean baseVisible;

    public VFXMechanic(MythicLineConfig mlc) {
        this.modelId = mlc.getPlaceholderString(new String[]{"m", "mid", "model", "modelid"}, null, new String[0]);
        this.partId = mlc.getPlaceholderString(new String[]{"p", "pid", "part", "partid"}, null, new String[0]);
        this.remove = mlc.getBoolean(new String[]{"r", "remove"}, false);
        this.radius = mlc.getPlaceholderInteger(new String[]{"rad", "radius"}, 0, new String[0]);
        this.color = mlc.getPlaceholderString(new String[]{"c", "color"}, "FFFFFF", new String[0]);
        this.enchant = mlc.getBoolean(new String[]{"en", "enchant"}, false);
        this.visible = mlc.getBoolean(new String[]{"v", "visible"}, true);
        this.baseVisible = mlc.getBoolean(new String[]{"bv", "bvisible"}, false);
    }

    public SkillResult castAtEntity(SkillMetadata meta, AbstractEntity target) {
        if (this.remove) {
            VFX vfx2 = ModelEngineAPI.getAPI().getVFXUpdater().getVFX(target.getUniqueId());
            if (vfx2 == null) {
                return SkillResult.CONDITION_FAILED;
            }
            vfx2.markRemoved();
            return SkillResult.SUCCESS;
        }
        String modelId = MythicUtils.getOrNullLowercase(this.modelId, (PlaceholderMeta)meta, target);
        String partId = MythicUtils.getOrNullLowercase(this.partId, (PlaceholderMeta)meta, target);
        if (modelId == null || partId == null) {
            return SkillResult.INVALID_CONFIG;
        }
        int radius = this.radius.get((PlaceholderMeta)meta, target);
        Color color = MythicUtils.getColor(MythicUtils.getOrNull(this.color, (PlaceholderMeta)meta, target));
        ModelEngineAPI.createVFX(target.getBukkitEntity(), vfx -> {
            BodyRotationController brc = vfx.getBase().getBodyRotationController();
            brc.setYBodyRot(brc.getYHeadRot());
            vfx.useModel(modelId, partId);
            if (radius > 0) {
                vfx.getBase().setRenderRadius(radius);
            }
            vfx.setColor(color);
            vfx.setEnchanted(this.enchant);
            vfx.setVisible(this.visible);
            vfx.setBaseEntityVisible(this.baseVisible);
        });
        return SkillResult.SUCCESS;
    }

    public SkillResult cast(SkillMetadata meta) {
        IParentSkill iParentSkill = meta.getCallingEvent();
        if (!(iParentSkill instanceof ProjectileBulletableTracker)) {
            return SkillResult.INVALID_TARGET;
        }
        ProjectileBulletableTracker projectileTracker = (ProjectileBulletableTracker)iParentSkill;
        if (this.remove) {
            UUID uuid = MythicUtils.getVFXUniqueId(meta);
            VFX vfx = ModelEngineAPI.getAPI().getVFXUpdater().getVFX(uuid);
            if (vfx == null) {
                return SkillResult.CONDITION_FAILED;
            }
            vfx.markRemoved();
            return SkillResult.SUCCESS;
        }
        String modelId = MythicUtils.getOrNullLowercase(this.modelId, (PlaceholderMeta)meta);
        String partId = MythicUtils.getOrNullLowercase(this.partId, (PlaceholderMeta)meta);
        if (modelId == null || partId == null) {
            return SkillResult.INVALID_CONFIG;
        }
        int radius = this.radius.get((PlaceholderMeta)meta);
        Color color = MythicUtils.getColor(MythicUtils.getOrNull(this.color, (PlaceholderMeta)meta));
        MythicUtils.castProjectileEntity(projectileTracker).ifPresent(tracker -> ModelEngineAPI.createVFX(new ProjectileEntity<ProjectileBulletableTracker>((ProjectileBulletableTracker)tracker), vfx -> {
            BodyRotationController brc = vfx.getBase().getBodyRotationController();
            brc.setYBodyRot(brc.getYHeadRot());
            vfx.useModel(modelId, partId);
            if (radius > 0) {
                vfx.getBase().setRenderRadius(radius);
            }
            vfx.setColor(color);
            vfx.setEnchanted(this.enchant);
            vfx.setVisible(this.visible);
            vfx.setBaseEntityVisible(this.baseVisible);
        }));
        return SkillResult.SUCCESS;
    }
}

