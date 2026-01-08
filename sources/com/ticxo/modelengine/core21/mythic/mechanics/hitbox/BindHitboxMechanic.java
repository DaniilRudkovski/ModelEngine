/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.lumine.mythic.api.adapters.AbstractEntity
 *  io.lumine.mythic.api.adapters.AbstractLocation
 *  io.lumine.mythic.api.config.MythicLineConfig
 *  io.lumine.mythic.api.mobs.MythicMob
 *  io.lumine.mythic.api.mobs.entities.SpawnReason
 *  io.lumine.mythic.api.skills.ITargetedEntitySkill
 *  io.lumine.mythic.api.skills.SkillCaster
 *  io.lumine.mythic.api.skills.SkillMetadata
 *  io.lumine.mythic.api.skills.SkillResult
 *  io.lumine.mythic.api.skills.ThreadSafetyLevel
 *  io.lumine.mythic.api.skills.placeholders.PlaceholderString
 *  io.lumine.mythic.bukkit.adapters.BukkitEntityType
 *  io.lumine.mythic.core.logging.MythicLogger
 *  io.lumine.mythic.core.mobs.ActiveMob
 *  io.lumine.mythic.core.skills.placeholders.PlaceholderMeta
 *  org.bukkit.entity.Entity
 *  org.joml.Vector3f
 */
package com.ticxo.modelengine.core21.mythic.mechanics.hitbox;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.model.ModeledEntity;
import com.ticxo.modelengine.api.model.bone.BoneBehaviorTypes;
import com.ticxo.modelengine.api.model.bone.ModelBone;
import com.ticxo.modelengine.api.model.bone.type.SubHitbox;
import com.ticxo.modelengine.core21.mythic.MythicUtils;
import com.ticxo.modelengine.core21.mythic.utils.MythicMechanic;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.api.mobs.entities.SpawnReason;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.adapters.BukkitEntityType;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.skills.placeholders.PlaceholderMeta;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.bukkit.entity.Entity;
import org.joml.Vector3f;

@MythicMechanic(name="bindhitbox", aliases={})
public class BindHitboxMechanic
implements ITargetedEntitySkill {
    private final PlaceholderString modelId;
    private final PlaceholderString partId;
    private final String strType;
    private MythicMob mm;
    private BukkitEntityType me;

    public BindHitboxMechanic(MythicLineConfig mlc) {
        this.modelId = mlc.getPlaceholderString(new String[]{"m", "mid", "model", "modelid"}, null, new String[0]);
        this.partId = mlc.getPlaceholderString(new String[]{"p", "pid", "part", "partid"}, null, new String[0]);
        this.strType = mlc.getString(new String[]{"type", "t", "mob", "m"}, "SKELETON", new String[0]);
        this.getPlugin().getSkillManager().queueSecondPass(() -> {
            this.mm = this.getPlugin().getMobManager().getMythicMob(this.strType).orElse(null);
            if (this.mm == null) {
                this.me = BukkitEntityType.getMythicEntity((String)this.strType);
                if (this.me == null) {
                    MythicLogger.errorGenericConfig((MythicLineConfig)mlc, (String)"The 'type' attribute must be a valid MythicMob or MythicEntity type.");
                }
            }
        });
    }

    public SkillResult castAtEntity(SkillMetadata meta, AbstractEntity target) {
        ModeledEntity model = ModelEngineAPI.getModeledEntity(target.getUniqueId());
        if (model == null) {
            return SkillResult.CONDITION_FAILED;
        }
        String modelId = MythicUtils.getOrNullLowercase(this.modelId, (PlaceholderMeta)meta, target);
        model.getModel(modelId).ifPresent(activeModel -> {
            String pbone = MythicUtils.getOrNullLowercase(this.partId, (PlaceholderMeta)meta, target);
            if (pbone == null) {
                return;
            }
            List<String> hitboxes = List.of(pbone.split(","));
            ArrayList<SubHitbox> subHitboxes = new ArrayList<SubHitbox>();
            for (String hitboxId : hitboxes) {
                ModelBone bone;
                Optional<? extends SubHitbox> maybeHitbox;
                Optional<ModelBone> maybeBone = activeModel.getBone(hitboxId);
                if (maybeBone.isEmpty() || (maybeHitbox = (bone = maybeBone.get()).getBoneBehavior(BoneBehaviorTypes.SUB_HITBOX)).isEmpty()) continue;
                subHitboxes.add(maybeHitbox.get());
            }
            this.createBoundEntity(meta.getCaster(), subHitboxes);
        });
        return SkillResult.SUCCESS;
    }

    public ThreadSafetyLevel getThreadSafetyLevel() {
        return ThreadSafetyLevel.SYNC_ONLY;
    }

    private void createBoundEntity(SkillCaster caster, List<SubHitbox> subHitboxes) {
        if (subHitboxes.isEmpty()) {
            return;
        }
        AbstractEntity abstractCaster = caster.getEntity();
        Vector3f vec = subHitboxes.get(0).getLocation();
        AbstractLocation l = new AbstractLocation(abstractCaster.getWorld(), (double)vec.x, (double)vec.y, (double)vec.z);
        if (this.mm != null) {
            ActiveMob ams = this.mm.spawn(l, caster.getLevel(), SpawnReason.SUMMON, entity -> {
                for (SubHitbox hitbox : subHitboxes) {
                    hitbox.addBoundEntity((Entity)entity);
                }
            });
            ams.setParent(caster);
            ams.setOwnerUUID(abstractCaster.getUniqueId());
            if (caster instanceof ActiveMob) {
                ActiveMob am = (ActiveMob)caster;
                ams.setFaction(am.getFaction());
            }
        } else if (this.me != null) {
            this.me.spawn(l, SpawnReason.SUMMON, entity -> {
                for (SubHitbox hitbox : subHitboxes) {
                    hitbox.addBoundEntity((Entity)entity);
                }
            });
        }
    }
}

