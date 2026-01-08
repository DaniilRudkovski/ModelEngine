/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.lumine.mythic.api.adapters.AbstractEntity
 *  io.lumine.mythic.api.skills.conditions.IEntityCondition
 */
package com.ticxo.modelengine.core21.mythic.conditions;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.bone.manager.BehaviorManager;
import com.ticxo.modelengine.api.model.bone.manager.MountManager;
import com.ticxo.modelengine.core21.mythic.utils.MythicCondition;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.skills.conditions.IEntityCondition;
import java.util.Optional;

@MythicCondition(name="ridingmodel")
public class IsRidingModelCondition
implements IEntityCondition {
    public boolean check(AbstractEntity abstractEntity) {
        ActiveModel model = ModelEngineAPI.getMountPairManager().getMountedPair(abstractEntity.getUniqueId());
        if (model == null) {
            return false;
        }
        Optional maybeManager = model.getMountManager();
        if (maybeManager.isEmpty()) {
            return false;
        }
        BehaviorManager mountManager = (BehaviorManager)maybeManager.get();
        Optional mount = ((MountManager)((Object)mountManager)).getMount(abstractEntity.getBukkitEntity());
        return mount.isPresent() && mount.get() != ((MountManager)((Object)mountManager)).getDriverBone();
    }
}

