/*
 * Decompiled with CFR 0.152.
 */
package com.ticxo.modelengine.core.model.bone.behavior;

import com.ticxo.modelengine.api.model.bone.BoneItems;
import com.ticxo.modelengine.api.model.bone.ModelBone;
import com.ticxo.modelengine.api.model.bone.behavior.AbstractBoneBehavior;
import com.ticxo.modelengine.api.model.bone.behavior.BoneBehaviorData;
import com.ticxo.modelengine.api.model.bone.behavior.BoneBehaviorType;
import com.ticxo.modelengine.api.model.bone.type.Ghost;

public class GhostImpl
extends AbstractBoneBehavior<GhostImpl>
implements Ghost {
    public GhostImpl(ModelBone bone, BoneBehaviorType<GhostImpl> type, BoneBehaviorData data) {
        super(bone, type, data);
    }

    @Override
    public void onApply() {
        this.bone.setRenderer(true);
        this.bone.clearModel();
    }

    @Override
    public BoneItems getModel() {
        return this.bone.getModels();
    }
}

