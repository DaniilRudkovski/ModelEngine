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
 */
package com.ticxo.modelengine.core21.mythic.mechanics.mounting;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.model.ModeledEntity;
import com.ticxo.modelengine.api.model.bone.behavior.BoneBehavior;
import com.ticxo.modelengine.api.model.bone.manager.MountData;
import com.ticxo.modelengine.api.model.bone.manager.MountManager;
import com.ticxo.modelengine.core21.mythic.MythicUtils;
import com.ticxo.modelengine.core21.mythic.utils.MythicMechanic;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.core.skills.placeholders.PlaceholderMeta;
import java.util.Optional;
import org.bukkit.entity.Entity;

@MythicMechanic(name="dismountmodel", aliases={})
public class DismountModelMechanic
implements ITargetedEntitySkill {
    private final boolean driver;
    private PlaceholderString modelId;
    private PlaceholderString pBone;

    public DismountModelMechanic(MythicLineConfig mlc) {
        this.driver = mlc.getBoolean(new String[]{"d", "drive", "driver"}, true);
        if (!this.driver) {
            this.modelId = mlc.getPlaceholderString(new String[]{"m", "mid", "model", "modelid"}, null, new String[0]);
            this.pBone = mlc.getPlaceholderString(new String[]{"p", "pbone", "seat"}, null, new String[0]);
        }
    }

    public SkillResult castAtEntity(SkillMetadata meta, AbstractEntity target) {
        ModeledEntity model = ModelEngineAPI.getModeledEntity(meta.getCaster().getEntity().getUniqueId());
        if (model == null) {
            return SkillResult.CONDITION_FAILED;
        }
        if (this.driver) {
            ((MountManager)((MountData)model.getMountData()).getMainMountManager()).dismountDriver();
            return SkillResult.SUCCESS;
        }
        String modelId = MythicUtils.getOrNullLowercase(this.modelId, (PlaceholderMeta)meta, target);
        model.getModel(modelId).ifPresent(activeModel -> activeModel.getMountManager().ifPresent(mountManager -> {
            Entity entity = target.getBukkitEntity();
            String part = MythicUtils.getOrNullLowercase(this.pBone, (PlaceholderMeta)meta, target);
            if (part == null) {
                ((MountManager)((Object)mountManager)).dismountRider(entity);
                return;
            }
            String[] seats = part.split(",");
            Optional<BoneBehavior> maybeMount = ((MountManager)((Object)mountManager)).getMount(entity);
            maybeMount.ifPresent(mount -> {
                if (this.contains(mount.getBone().getUniqueBoneId(), seats)) {
                    ((MountManager)((Object)mountManager)).dismountRider(entity);
                }
            });
        }));
        return SkillResult.SUCCESS;
    }

    public boolean getTargetsCreatives() {
        return true;
    }

    private boolean contains(String seat, String ... seats) {
        for (String s : seats) {
            if (!seat.equals(s)) continue;
            return true;
        }
        return false;
    }
}

