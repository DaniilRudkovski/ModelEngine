/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 */
package com.ticxo.modelengine.api.model.bone.manager;

import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.bone.ModelBone;
import com.ticxo.modelengine.api.model.bone.behavior.BoneBehavior;
import com.ticxo.modelengine.api.model.bone.behavior.BoneBehaviorType;
import com.ticxo.modelengine.api.model.bone.manager.BehaviorManager;
import java.util.Optional;
import lombok.Generated;

public abstract class AbstractBehaviorManager<T extends BoneBehavior>
implements BehaviorManager<T> {
    protected final ActiveModel activeModel;
    protected final BoneBehaviorType<T> type;

    protected Optional<T> getBoneBehavior(String boneId) {
        Optional<ModelBone> bone = this.getActiveModel().getBone(boneId);
        if (bone.isEmpty()) {
            return Optional.empty();
        }
        return bone.get().getBoneBehavior(this.getType());
    }

    @Override
    @Generated
    public ActiveModel getActiveModel() {
        return this.activeModel;
    }

    @Override
    @Generated
    public BoneBehaviorType<T> getType() {
        return this.type;
    }

    @Generated
    public AbstractBehaviorManager(ActiveModel activeModel, BoneBehaviorType<T> type) {
        this.activeModel = activeModel;
        this.type = type;
    }
}

