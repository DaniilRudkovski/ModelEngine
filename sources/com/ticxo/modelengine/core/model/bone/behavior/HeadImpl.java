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
import com.ticxo.modelengine.api.model.bone.behavior.AbstractBoneBehavior;
import com.ticxo.modelengine.api.model.bone.behavior.BoneBehaviorData;
import com.ticxo.modelengine.api.model.bone.behavior.BoneBehaviorType;
import com.ticxo.modelengine.api.model.bone.type.Head;
import com.ticxo.modelengine.api.utils.math.TMath;
import com.ticxo.modelengine.core.model.bone.behavior.HeadForcedImpl;
import java.util.Optional;
import lombok.Generated;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;

public class HeadImpl
extends AbstractBoneBehavior<HeadImpl>
implements Head {
    protected boolean shouldRotate;
    protected boolean inherited;
    protected boolean local;

    public HeadImpl(ModelBone bone, BoneBehaviorType<HeadImpl> type, BoneBehaviorData data) {
        super(bone, type, data);
        this.onParentSwap(bone.getParent());
        this.local = data.get("local", false);
        this.inherited = data.get("inherited", false);
    }

    @Override
    public void onParentSwap(@Nullable ModelBone parent) {
        this.shouldRotate = true;
        while (parent != null) {
            Optional maybeBehavior = parent.getBoneBehavior(this.type);
            boolean bl = this.shouldRotate = maybeBehavior.isEmpty() || maybeBehavior.get() instanceof HeadForcedImpl;
            if (!this.shouldRotate) {
                return;
            }
            parent = parent.getParent();
        }
    }

    @Override
    public void postTransformDecompose() {
        if (!this.shouldRotate || this.local) {
            return;
        }
        ActiveModel activeModel = this.bone.getActiveModel();
        ModeledEntity modeledEntity = activeModel.getModeledEntity();
        float yaw = TMath.degreeDifference(modeledEntity.getYBodyRot(), activeModel.getYHeadRot());
        float pitch = activeModel.getXHeadRot();
        Quaternionf q = new Quaternionf().rotateY(-yaw * ((float)Math.PI / 180)).rotateX(pitch * ((float)Math.PI / 180));
        this.bone.getGlobalTransform().mutateLeftQuaternion(quaternionf -> quaternionf.premul((Quaternionfc)q));
    }

    @Override
    public void postGlobalRotate() {
        if (!this.shouldRotate || !this.local) {
            return;
        }
        ActiveModel activeModel = this.bone.getActiveModel();
        ModeledEntity modeledEntity = activeModel.getModeledEntity();
        float yaw = TMath.degreeDifference(modeledEntity.getYBodyRot(), activeModel.getYHeadRot());
        float pitch = activeModel.getXHeadRot();
        Quaternionf q = new Quaternionf().rotateY(-yaw * ((float)Math.PI / 180)).rotateX(pitch * ((float)Math.PI / 180));
        this.bone.getTransformMatrix().rotate((Quaternionfc)q);
    }

    @Generated
    public boolean isInherited() {
        return this.inherited;
    }

    @Generated
    public void setInherited(boolean inherited) {
        this.inherited = inherited;
    }

    @Override
    @Generated
    public boolean isLocal() {
        return this.local;
    }

    @Override
    @Generated
    public void setLocal(boolean local) {
        this.local = local;
    }
}

