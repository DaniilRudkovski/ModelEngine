/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 */
package com.ticxo.modelengine.core.model.bone.behavior;

import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.ModeledEntity;
import com.ticxo.modelengine.api.model.bone.ModelBone;
import com.ticxo.modelengine.api.model.bone.behavior.BoneBehaviorData;
import com.ticxo.modelengine.api.model.bone.behavior.BoneBehaviorType;
import com.ticxo.modelengine.api.utils.math.TMath;
import com.ticxo.modelengine.core.model.bone.behavior.HeadImpl;
import java.util.Optional;
import lombok.Generated;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;

public class HeadForcedImpl
extends HeadImpl {
    protected boolean inherited;

    public HeadForcedImpl(ModelBone bone, BoneBehaviorType<HeadImpl> type, BoneBehaviorData data) {
        super(bone, type, data);
        this.onParentSwap(bone.getParent());
    }

    @Override
    public void onParentSwap(@Nullable ModelBone parent) {
        this.shouldRotate = false;
        if (parent == null) {
            return;
        }
        Optional<HeadImpl> maybeBehavior = parent.getBoneBehavior(this.type);
        maybeBehavior.ifPresent(head -> {
            this.inherited = head.inherited;
            if (this.inherited || head instanceof HeadForcedImpl) {
                return;
            }
            this.shouldRotate = true;
            this.local = head.isLocal();
        });
    }

    @Override
    public void preGlobalRotate() {
        if (!this.shouldRotate || this.local) {
            return;
        }
        ActiveModel activeModel = this.bone.getActiveModel();
        ModeledEntity modeledEntity = activeModel.getModeledEntity();
        float yaw = -TMath.degreeDifference(modeledEntity.getYBodyRot(), activeModel.getYHeadRot());
        float pitch = -activeModel.getXHeadRot();
        Quaternionf q = new Quaternionf().rotateX(pitch * ((float)Math.PI / 180)).rotateY(-yaw * ((float)Math.PI / 180));
        this.bone.getTransformMatrix().rotate((Quaternionfc)q);
    }

    @Override
    public void postGlobalRotate() {
        if (!this.shouldRotate || !this.local) {
            return;
        }
        ActiveModel activeModel = this.bone.getActiveModel();
        ModeledEntity modeledEntity = activeModel.getModeledEntity();
        float yaw = -TMath.degreeDifference(modeledEntity.getYBodyRot(), activeModel.getYHeadRot());
        float pitch = -activeModel.getXHeadRot();
        Quaternionf q = new Quaternionf().rotateX(pitch * ((float)Math.PI / 180)).rotateY(-yaw * ((float)Math.PI / 180));
        this.bone.getTransformMatrix().rotate((Quaternionfc)q);
    }

    @Override
    public boolean isHidden() {
        return true;
    }

    @Override
    @Generated
    public boolean isInherited() {
        return this.inherited;
    }

    @Override
    @Generated
    public void setInherited(boolean inherited) {
        this.inherited = inherited;
    }
}

