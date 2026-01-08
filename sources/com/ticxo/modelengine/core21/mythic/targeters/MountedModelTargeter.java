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
import com.ticxo.modelengine.api.entity.BaseEntity;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.core21.mythic.utils.MythicTargeter;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.targeters.IEntityTargeter;
import io.lumine.mythic.bukkit.BukkitAdapter;
import java.util.Collection;
import java.util.HashSet;
import org.bukkit.entity.Entity;

@MythicTargeter(name="mountedmodel", aliases={})
public class MountedModelTargeter
implements IEntityTargeter {
    public Collection<AbstractEntity> getEntities(SkillMetadata skillMetadata) {
        BaseEntity<?> base;
        Object obj;
        HashSet<AbstractEntity> targets = new HashSet<AbstractEntity>();
        SkillCaster caster = skillMetadata.getCaster();
        ActiveModel model = ModelEngineAPI.getMountPairManager().getMountedPair(caster.getEntity().getUniqueId());
        if (model != null && (obj = (base = model.getModeledEntity().getBase()).getOriginal()) instanceof Entity) {
            Entity entity = (Entity)obj;
            targets.add(BukkitAdapter.adapt((Entity)entity));
        }
        return targets;
    }
}

