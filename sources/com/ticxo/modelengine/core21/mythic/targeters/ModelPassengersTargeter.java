/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.lumine.mythic.api.adapters.AbstractEntity
 *  io.lumine.mythic.api.config.MythicLineConfig
 *  io.lumine.mythic.api.skills.SkillCaster
 *  io.lumine.mythic.api.skills.SkillMetadata
 *  io.lumine.mythic.api.skills.placeholders.PlaceholderString
 *  io.lumine.mythic.api.skills.targeters.IEntityTargeter
 *  io.lumine.mythic.bukkit.BukkitAdapter
 *  io.lumine.mythic.core.skills.placeholders.PlaceholderMeta
 *  org.bukkit.entity.Entity
 */
package com.ticxo.modelengine.core21.mythic.targeters;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.ModeledEntity;
import com.ticxo.modelengine.api.model.bone.manager.MountManager;
import com.ticxo.modelengine.api.model.bone.type.Mount;
import com.ticxo.modelengine.core21.mythic.MythicUtils;
import com.ticxo.modelengine.core21.mythic.utils.MythicTargeter;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.api.skills.targeters.IEntityTargeter;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.skills.placeholders.PlaceholderMeta;
import java.util.Collection;
import java.util.HashSet;
import org.bukkit.entity.Entity;

@MythicTargeter(name="modelpassengers", aliases={})
public class ModelPassengersTargeter
implements IEntityTargeter {
    private final PlaceholderString modelId;
    private final PlaceholderString pbone;

    public ModelPassengersTargeter(MythicLineConfig mlc) {
        this.modelId = mlc.getPlaceholderString(new String[]{"m", "mid", "model", "modelid"}, null, new String[0]);
        this.pbone = mlc.getPlaceholderString(new String[]{"p", "pbone", "seat"}, null, new String[0]);
    }

    public Collection<AbstractEntity> getEntities(SkillMetadata skillMetadata) {
        HashSet<AbstractEntity> targets = new HashSet<AbstractEntity>();
        SkillCaster caster = skillMetadata.getCaster();
        ModeledEntity model = ModelEngineAPI.getModeledEntity(caster.getEntity().getUniqueId());
        if (model == null) {
            return targets;
        }
        String parts = MythicUtils.getOrNullLowercase(this.pbone, (PlaceholderMeta)skillMetadata);
        if (parts == null) {
            return targets;
        }
        String[] seats = parts.split(",");
        String modelId = MythicUtils.getOrNullLowercase(this.modelId, (PlaceholderMeta)skillMetadata);
        model.getModel(modelId).ifPresentOrElse(activeModel -> activeModel.getMountManager().ifPresent(mountManager -> {
            for (String seat : seats) {
                ((MountManager)((Object)mountManager)).getSeat(seat).ifPresent(mount -> {
                    for (Entity passenger : ((Mount)((Object)mount)).getPassengers()) {
                        targets.add(BukkitAdapter.adapt((Entity)passenger));
                    }
                });
            }
        }), () -> {
            for (ActiveModel activeModel : model.getModels().values()) {
                activeModel.getMountManager().ifPresent(mountManager -> {
                    for (String seat : seats) {
                        ((MountManager)((Object)mountManager)).getSeat(seat).ifPresent(mount -> {
                            for (Entity passenger : ((Mount)((Object)mount)).getPassengers()) {
                                targets.add(BukkitAdapter.adapt((Entity)passenger));
                            }
                        });
                    }
                });
            }
        });
        return targets;
    }
}

