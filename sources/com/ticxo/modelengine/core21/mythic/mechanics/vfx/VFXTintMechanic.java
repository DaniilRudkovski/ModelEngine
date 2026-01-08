/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.lumine.mythic.api.adapters.AbstractEntity
 *  io.lumine.mythic.api.config.MythicLineConfig
 *  io.lumine.mythic.api.skills.INoTargetSkill
 *  io.lumine.mythic.api.skills.ITargetedEntitySkill
 *  io.lumine.mythic.api.skills.SkillMetadata
 *  io.lumine.mythic.api.skills.SkillResult
 *  io.lumine.mythic.api.skills.placeholders.PlaceholderInt
 *  io.lumine.mythic.api.skills.placeholders.PlaceholderString
 *  io.lumine.mythic.core.skills.placeholders.PlaceholderMeta
 *  org.bukkit.Color
 *  org.bukkit.plugin.Plugin
 */
package com.ticxo.modelengine.core21.mythic.mechanics.vfx;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.entity.BaseEntity;
import com.ticxo.modelengine.api.entity.BukkitEntity;
import com.ticxo.modelengine.api.utils.scheduling.PlatformScheduler;
import com.ticxo.modelengine.api.utils.scheduling.PlatformTask;
import com.ticxo.modelengine.api.vfx.VFX;
import com.ticxo.modelengine.core21.mythic.MythicUtils;
import com.ticxo.modelengine.core21.mythic.utils.MythicMechanic;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.INoTargetSkill;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.placeholders.PlaceholderInt;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.core.skills.placeholders.PlaceholderMeta;
import java.util.UUID;
import org.bukkit.Color;
import org.bukkit.plugin.Plugin;

@MythicMechanic(name="vfxtint", aliases={"vfxcolor"})
public class VFXTintMechanic
implements ITargetedEntitySkill,
INoTargetSkill {
    private final PlaceholderInt duration;
    private final PlaceholderString colorA;
    private final PlaceholderString colorB;

    public VFXTintMechanic(MythicLineConfig mlc) {
        this.duration = mlc.getPlaceholderInteger(new String[]{"duration", "d"}, 0, new String[0]);
        this.colorA = mlc.getPlaceholderString(new String[]{"c", "color", "ca", "colora"}, "FFFFFF", new String[0]);
        this.colorB = mlc.getPlaceholderString(new String[]{"cb", "colorb"}, "FFFFFF", new String[0]);
    }

    public SkillResult castAtEntity(SkillMetadata meta, AbstractEntity target) {
        VFX vfx = ModelEngineAPI.getVFX(target.getBukkitEntity());
        if (vfx == null) {
            return SkillResult.INVALID_TARGET;
        }
        int duration = this.duration.get((PlaceholderMeta)meta, target);
        Color colorA = MythicUtils.getColor(MythicUtils.getOrNull(this.colorA, (PlaceholderMeta)meta, target));
        if (duration <= 0) {
            vfx.setColor(colorA);
        } else {
            Color colorB = MythicUtils.getColor(MythicUtils.getOrNull(this.colorB, (PlaceholderMeta)meta, target));
            new Animator(vfx, duration, colorA, colorB);
        }
        return SkillResult.SUCCESS;
    }

    public SkillResult cast(SkillMetadata meta) {
        UUID uuid = MythicUtils.getVFXUniqueId(meta);
        VFX vfx = ModelEngineAPI.getAPI().getVFXUpdater().getVFX(uuid);
        if (vfx == null) {
            return SkillResult.INVALID_TARGET;
        }
        int duration = this.duration.get((PlaceholderMeta)meta);
        Color colorA = MythicUtils.getColor(MythicUtils.getOrNull(this.colorA, (PlaceholderMeta)meta));
        if (duration <= 0) {
            vfx.setColor(colorA);
        } else {
            Color colorB = MythicUtils.getColor(MythicUtils.getOrNull(this.colorB, (PlaceholderMeta)meta));
            new Animator(vfx, duration, colorA, colorB);
        }
        return SkillResult.SUCCESS;
    }

    private static class Animator
    implements Runnable {
        private final VFX target;
        private final double duration;
        private final FloatColor colorA;
        private final FloatColor colorB;
        private final PlatformTask task;
        private double iteration;

        public Animator(VFX entity, int duration, Color colorA, Color colorB) {
            this.duration = duration;
            PlatformScheduler scheduler = ModelEngineAPI.getAPI().getScheduler();
            BaseEntity<?> baseEntity = entity.getBase();
            if (baseEntity instanceof BukkitEntity) {
                BukkitEntity bukkitEntity = (BukkitEntity)baseEntity;
                this.task = scheduler.scheduleRepeating((Plugin)ModelEngineAPI.getAPI(), bukkitEntity.getOriginal(), (Runnable)this, 0L, 1L);
            } else {
                this.task = scheduler.scheduleRepeating((Plugin)ModelEngineAPI.getAPI(), this, 0L, 1L);
            }
            this.colorA = new FloatColor(colorA);
            this.colorB = new FloatColor(colorB);
            this.target = entity;
        }

        @Override
        public void run() {
            if (this.iteration > this.duration) {
                this.task.cancel();
            } else {
                this.target.setColor(this.lerpColor(this.iteration / this.duration));
                this.iteration += 1.0;
            }
        }

        private Color lerpColor(double ratio) {
            int r = (int)(Math.sqrt((1.0 - ratio) * (double)this.colorA.r + ratio * (double)this.colorB.r) * 255.0);
            int g = (int)(Math.sqrt((1.0 - ratio) * (double)this.colorA.g + ratio * (double)this.colorB.g) * 255.0);
            int b = (int)(Math.sqrt((1.0 - ratio) * (double)this.colorA.b + ratio * (double)this.colorB.b) * 255.0);
            return Color.fromRGB((int)r, (int)g, (int)b);
        }
    }

    private static class FloatColor {
        protected final float r;
        protected final float g;
        protected final float b;

        public FloatColor(Color color) {
            this.r = (float)(color.getRed() * color.getRed()) / 65025.0f;
            this.g = (float)(color.getGreen() * color.getGreen()) / 65025.0f;
            this.b = (float)(color.getBlue() * color.getBlue()) / 65025.0f;
        }
    }
}

