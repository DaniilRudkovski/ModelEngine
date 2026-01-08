/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 */
package com.ticxo.modelengine.api.model.bone;

import com.ticxo.modelengine.api.model.bone.ManualAnimator;
import com.ticxo.modelengine.api.model.bone.ModelBone;
import com.ticxo.modelengine.api.utils.math.SafeTransform;
import lombok.Generated;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class SimpleManualAnimator
implements ManualAnimator {
    private final Vector3f position = new Vector3f();
    private final Quaternionf rotation = new Quaternionf();
    private final Vector3f scale = new Vector3f(1.0f);

    public SimpleManualAnimator() {
    }

    public SimpleManualAnimator(ModelBone bone) {
        SafeTransform transform = bone.getLocalTransform();
        this.position.set((Vector3fc)transform.getSafePosition());
        this.rotation.set((Quaternionfc)transform.getSafeLeftQuaternion());
        this.scale.set((Vector3fc)transform.getSafeScale());
    }

    @Override
    public void animate(ModelBone bone) {
        SafeTransform transform = bone.getLocalTransform();
        transform.mutatePosition(vector3f -> vector3f.add((Vector3fc)this.position));
        transform.mutateLeftQuaternion(quaternionf -> quaternionf.mul((Quaternionfc)this.rotation));
        transform.mutateScale(vector3f -> vector3f.mul((Vector3fc)this.scale));
    }

    @Generated
    public Vector3f getPosition() {
        return this.position;
    }

    @Generated
    public Quaternionf getRotation() {
        return this.rotation;
    }

    @Generated
    public Vector3f getScale() {
        return this.scale;
    }
}

