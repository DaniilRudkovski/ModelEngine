/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.lumine.mythic.api.adapters.AbstractEntity
 *  io.lumine.mythic.api.skills.SkillCaster
 *  io.lumine.mythic.api.skills.SkillMetadata
 *  io.lumine.mythic.api.skills.targeters.IEntityTargeter
 *  io.lumine.mythic.bukkit.BukkitAdapter
 *  org.bukkit.entity.Entity
 */
package com.ticxo.modelengine.core21.mythic.targeters;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.model.ModeledEntity;
import com.ticxo.modelengine.api.model.bone.manager.MountData;
import com.ticxo.modelengine.api.model.bone.manager.MountManager;
import com.ticxo.modelengine.core21.mythic.utils.MythicTargeter;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.targeters.IEntityTargeter;
import io.lumine.mythic.bukkit.BukkitAdapter;
import java.util.Collection;
import java.util.HashSet;
import org.bukkit.entity.Entity;

@MythicTargeter(name="modeldriver", aliases={})
public class ModelDriverTargeter
implements IEntityTargeter {
    public Collection<AbstractEntity> getEntities(SkillMetadata skillMetadata) {
        HashSet<AbstractEntity> targets = new HashSet<AbstractEntity>();
        SkillCaster caster = skillMetadata.getCaster();
        ModeledEntity model = ModelEngineAPI.getModeledEntity(caster.getEntity().getUniqueId());
        if (model == null) {
            return targets;
        }
        Object main = ((MountData)model.getMountData()).getMainMountManager();
        if (((MountManager)main).isControlled()) {
            targets.add(BukkitAdapter.adapt((Entity)((MountManager)main).getDriver()));
        }
        return targets;
    }
}

