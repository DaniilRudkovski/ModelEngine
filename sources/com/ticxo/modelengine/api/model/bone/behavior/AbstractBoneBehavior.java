/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 */
package com.ticxo.modelengine.api.model.bone.behavior;

import com.ticxo.modelengine.api.model.bone.ModelBone;
import com.ticxo.modelengine.api.model.bone.behavior.BoneBehavior;
import com.ticxo.modelengine.api.model.bone.behavior.BoneBehaviorData;
import com.ticxo.modelengine.api.model.bone.behavior.BoneBehaviorType;
import lombok.Generated;

public abstract class AbstractBoneBehavior<T extends BoneBehavior>
implements BoneBehavior {
    protected final ModelBone bone;
    protected final BoneBehaviorType<T> type;
    protected final BoneBehaviorData data;

    @Override
    @Generated
    public ModelBone getBone() {
        return this.bone;
    }

    @Generated
    public BoneBehaviorType<T> getType() {
        return this.type;
    }

    @Override
    @Generated
    public BoneBehaviorData getData() {
        return this.data;
    }

    @Generated
    public AbstractBoneBehavior(ModelBone bone, BoneBehaviorType<T> type, BoneBehaviorData data) {
        this.bone = bone;
        this.type = type;
        this.data = data;
    }
}

