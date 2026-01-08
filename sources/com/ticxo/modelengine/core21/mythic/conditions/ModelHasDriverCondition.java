/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.lumine.mythic.api.adapters.AbstractEntity
 *  io.lumine.mythic.api.skills.conditions.IEntityCondition
 */
package com.ticxo.modelengine.core21.mythic.conditions;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.model.ModeledEntity;
import com.ticxo.modelengine.api.model.bone.manager.MountData;
import com.ticxo.modelengine.api.model.bone.manager.MountManager;
import com.ticxo.modelengine.core21.mythic.utils.MythicCondition;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.skills.conditions.IEntityCondition;

@MythicCondition(name="modelhasdriver", aliases={"modeldriver"})
public class ModelHasDriverCondition
implements IEntityCondition {
    public boolean check(AbstractEntity abstractEntity) {
        ModeledEntity model = ModelEngineAPI.getModeledEntity(abstractEntity.getUniqueId());
        if (model == null) {
            return false;
        }
        Object mountManager = ((MountData)model.getMountData()).getMainMountManager();
        if (mountManager == null) {
            return false;
        }
        return ((MountManager)mountManager).isControlled();
    }
}

