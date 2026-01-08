/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.joml.Quaternionfc
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 */
package com.ticxo.modelengine.core.model.bone.behavior;

import com.ticxo.modelengine.api.model.bone.ModelBone;
import com.ticxo.modelengine.api.model.bone.behavior.BoneBehaviorData;
import com.ticxo.modelengine.api.model.bone.behavior.BoneBehaviorType;
import com.ticxo.modelengine.api.utils.math.SafeTransform;
import com.ticxo.modelengine.core.model.bone.behavior.AbstractSegmentImpl;
import org.bukkit.Location;
import org.joml.Quaternionfc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class SegmentImpl
extends AbstractSegmentImpl<SegmentImpl> {
    public SegmentImpl(ModelBone bone, BoneBehaviorType<SegmentImpl> type, BoneBehaviorData data) {
        super(bone, type, data);
    }

    @Override
    public void onApply() {
        super.onApply();
        if (this.backPivot) {
            this.resetPivot();
        }
    }

    @Override
    public void onModelInitialized() {
        super.onModelInitialized();
        if (!this.backPivot) {
            this.resetPivot();
        }
    }

    @Override
    public void postGlobalCalculation() {
        super.postGlobalCalculation();
        this.resetPivot();
    }

    @Override
    public void postTransformDecompose() {
        SafeTransform global = this.bone.getGlobalTransform();
        this.bone.getTransformMatrix().identity().rotate((Quaternionfc)global.getLeftQuaternion()).scale((Vector3fc)global.getScale()).rotate((Quaternionfc)global.getRightQuaternion());
        global.mutatePosition(Vector3f::zero);
    }

    private void resetPivot() {
        this.bone.setPivotLocation(this.toLocation(this.worldLocation));
    }

    private Location toLocation(Vector3f vec) {
        return this.bone.getBaseLocation().clone().set((double)vec.x, (double)vec.y, (double)vec.z);
    }
}

