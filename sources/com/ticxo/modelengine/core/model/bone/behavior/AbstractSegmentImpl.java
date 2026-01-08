/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 *  org.joml.AxisAngle4f
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 */
package com.ticxo.modelengine.core.model.bone.behavior;

import com.ticxo.modelengine.api.model.bone.ModelBone;
import com.ticxo.modelengine.api.model.bone.behavior.AbstractBoneBehavior;
import com.ticxo.modelengine.api.model.bone.behavior.BoneBehavior;
import com.ticxo.modelengine.api.model.bone.behavior.BoneBehaviorData;
import com.ticxo.modelengine.api.model.bone.behavior.BoneBehaviorType;
import com.ticxo.modelengine.api.model.bone.type.Segment;
import com.ticxo.modelengine.api.utils.math.TMath;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import lombok.Generated;
import org.joml.AxisAngle4f;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class AbstractSegmentImpl<T extends BoneBehavior>
extends AbstractBoneBehavior<T>
implements Segment {
    protected final boolean backPivot;
    protected boolean bounded;
    protected float minX;
    protected float maxX;
    protected float minY;
    protected float maxY;
    protected float minZ;
    protected float maxZ;
    protected float alignRate;
    protected float elasticity;
    protected final Vector3f delta = new Vector3f();
    protected final Vector3f deltaNorm = new Vector3f();
    protected final Vector3f worldLocation = new Vector3f();
    protected UUID scaleCallback;
    protected float boneLength;
    protected float modelScale = 1.0f;

    public AbstractSegmentImpl(ModelBone bone, BoneBehaviorType<T> type, BoneBehaviorData data) {
        super(bone, type, data);
        this.backPivot = data.get("back_pivot", true);
        this.bounded = data.get("bounded", true);
        this.minX = data.get("min_x", Float.valueOf(-50.0f)).floatValue();
        this.maxX = data.get("max_x", Float.valueOf(50.0f)).floatValue();
        this.minY = data.get("min_y", Float.valueOf(-50.0f)).floatValue();
        this.maxY = data.get("max_y", Float.valueOf(50.0f)).floatValue();
        this.minZ = data.get("min_z", Float.valueOf(-50.0f)).floatValue();
        this.maxZ = data.get("max_z", Float.valueOf(50.0f)).floatValue();
        this.alignRate = data.get("align_rate", Float.valueOf(0.25f)).floatValue();
        this.elasticity = data.get("elasticity", Float.valueOf(0.0f)).floatValue();
    }

    @Override
    public void onApply() {
        if (!this.backPivot) {
            return;
        }
        this.delta.set((Vector3fc)this.bone.getBlueprintBone().getLocalPosition());
        this.delta.normalize(this.deltaNorm);
        this.boneLength = this.delta.length();
        Vector3fc scale = this.bone.getActiveModel().getScale();
        this.modelScale = scale.get(scale.maxComponent());
        this.scaleCallback = this.bone.getActiveModel().getScaleCallback().subscribe((model, s) -> {
            this.modelScale = (float)s;
        });
        this.worldLocation.set((Vector3fc)this.bone.getLocation().toVector().toVector3f());
    }

    @Override
    public void onModelInitialized() {
        if (this.backPivot) {
            return;
        }
        Optional<ModelBone> maybeControlBone = this.getControlBone();
        if (maybeControlBone.isEmpty()) {
            throw new RuntimeException("Missing control bone for: " + this.bone.getBoneId());
        }
        ModelBone controlBone = maybeControlBone.get();
        this.delta.set((Vector3fc)controlBone.getBlueprintBone().getLocalPosition());
        this.delta.normalize(this.deltaNorm);
        this.boneLength = this.delta.length();
        Vector3fc scale = this.bone.getActiveModel().getScale();
        this.modelScale = scale.get(scale.maxComponent());
        this.scaleCallback = this.bone.getActiveModel().getScaleCallback().subscribe((model, s) -> {
            this.modelScale = (float)s;
        });
        this.worldLocation.set((Vector3fc)controlBone.getLocation().toVector().toVector3f());
    }

    private Optional<ModelBone> getControlBone() {
        Map<String, ModelBone> children = this.bone.getChildren();
        if (children.isEmpty()) {
            return Optional.empty();
        }
        if (children.size() == 1) {
            return children.values().stream().findFirst();
        }
        return Optional.ofNullable(children.get(this.bone.getBoneId() + "_ctrl"));
    }

    @Override
    public void onRemove() {
        this.bone.getActiveModel().getScaleCallback().unsubscribe(this.scaleCallback);
    }

    @Override
    public void postGlobalCalculation() {
        if (this.backPivot) {
            this.backPivotCalculation();
        } else {
            this.frontPivotCalculation();
        }
    }

    private void frontPivotCalculation() {
        Matrix4f parentTransform = this.getParent(parent -> new Matrix4f((Matrix4fc)parent.getTransformMatrix()), new Matrix4f());
        parentTransform.translate((Vector3fc)this.bone.getLocalTransform().getPosition()).rotate((Quaternionfc)this.bone.getBlueprintBone().getLocalQuaternion());
        Matrix4f invParentTransform = parentTransform.invert(new Matrix4f());
        Vector3f pivotLocation = this.bone.calculatePivotLocation().toVector().toVector3f();
        Vector3f parentLocation = TMath.transform(this.bone.getTransformMatrix(), new Vector3f()).rotateY(-this.bone.getYaw() * ((float)Math.PI / 180)).mul(this.modelScale).add((Vector3fc)pivotLocation);
        Matrix4f transformMatrix = this.bone.getTransformMatrix();
        Vector3f originPosition = TMath.transform(transformMatrix, this.delta).rotateY(-this.bone.getYaw() * ((float)Math.PI / 180)).mul(this.modelScale).add((Vector3fc)pivotLocation);
        float animatedBoneLength = originPosition.distance((Vector3fc)parentLocation);
        float finalBoneLength = TMath.lerp(this.boneLength * this.modelScale, animatedBoneLength, this.elasticity);
        this.worldLocation.lerp((Vector3fc)originPosition, this.alignRate).sub((Vector3fc)parentLocation).normalize(finalBoneLength).add((Vector3fc)parentLocation);
        Vector3f globalPosition = this.worldLocation.sub((Vector3fc)pivotLocation, new Vector3f()).mul(1.0f / this.modelScale).rotateY(this.bone.getYaw() * ((float)Math.PI / 180));
        Vector3f localPosition = TMath.transform(invParentTransform, globalPosition);
        Quaternionf q = new Quaternionf();
        if (this.bounded) {
            Vector3f normLocalPosition = localPosition.normalize(new Vector3f());
            Vector3f axis = this.deltaNorm.cross((Vector3fc)normLocalPosition, new Vector3f()).normalize();
            float angle = (float)Math.acos(this.deltaNorm.dot((Vector3fc)normLocalPosition));
            q.set(new AxisAngle4f(angle, (Vector3fc)axis));
            if (!q.isFinite()) {
                this.deltaNorm.rotationTo((Vector3fc)localPosition, q);
            } else {
                Vector3f euler = TMath.toEulerZYX(q, new Vector3f());
                euler.x = TMath.clamp(euler.x, this.minX, this.maxX) * ((float)Math.PI / 180);
                euler.y = TMath.clamp(euler.y, this.minY, this.maxY) * ((float)Math.PI / 180);
                euler.z = TMath.clamp(euler.z, this.minZ, this.maxZ) * ((float)Math.PI / 180);
                TMath.toQuaternion(euler, q);
                this.deltaNorm.rotate((Quaternionfc)q, localPosition).mul(finalBoneLength / this.modelScale);
            }
        } else {
            this.deltaNorm.rotationTo((Vector3fc)localPosition, q);
        }
        transformMatrix.set((Matrix4fc)parentTransform);
        this.worldLocation.set((Vector3fc)TMath.transform(transformMatrix, localPosition)).rotateY(-this.bone.getYaw() * ((float)Math.PI / 180)).mul(this.modelScale).add((Vector3fc)pivotLocation);
        transformMatrix.rotate((Quaternionfc)q);
    }

    private void backPivotCalculation() {
        Matrix4f parentTransform = this.getParent(ModelBone::getTransformMatrix, new Matrix4f());
        Matrix4f invParentTransform = parentTransform.invert(new Matrix4f());
        Vector3f pivotLocation = this.bone.calculatePivotLocation().toVector().toVector3f();
        Vector3f parentLocation = this.getParent(parent -> parent.getLocationUnsafe().toVector().toVector3f(), pivotLocation);
        Matrix4f transformMatrix = this.bone.getTransformMatrix();
        Vector3f originPosition = TMath.transform(transformMatrix, new Vector3f()).rotateY(-this.bone.getYaw() * ((float)Math.PI / 180)).mul(this.modelScale).add((Vector3fc)pivotLocation);
        float animatedBoneLength = originPosition.distance((Vector3fc)parentLocation);
        float finalBoneLength = TMath.lerp(this.boneLength * this.modelScale, animatedBoneLength, this.elasticity);
        this.worldLocation.lerp((Vector3fc)originPosition, this.alignRate).sub((Vector3fc)parentLocation).normalize(finalBoneLength).add((Vector3fc)parentLocation);
        Vector3f globalPosition = this.worldLocation.sub((Vector3fc)pivotLocation, new Vector3f()).mul(1.0f / this.modelScale).rotateY(this.bone.getYaw() * ((float)Math.PI / 180));
        Vector3f localPosition = TMath.transform(invParentTransform, globalPosition);
        Quaternionf q = new Quaternionf();
        if (this.bounded) {
            Vector3f normLocalPosition = localPosition.normalize(new Vector3f());
            Vector3f axis = this.deltaNorm.cross((Vector3fc)normLocalPosition, new Vector3f()).normalize();
            float angle = (float)Math.acos(this.deltaNorm.dot((Vector3fc)normLocalPosition));
            q.set(new AxisAngle4f(angle, (Vector3fc)axis));
            if (!q.isFinite()) {
                this.deltaNorm.rotationTo((Vector3fc)localPosition, q);
            } else {
                Vector3f euler = TMath.toEulerZYX(q, new Vector3f());
                euler.x = TMath.clamp(euler.x, this.minX, this.maxX) * ((float)Math.PI / 180);
                euler.y = TMath.clamp(euler.y, this.minY, this.maxY) * ((float)Math.PI / 180);
                euler.z = TMath.clamp(euler.z, this.minZ, this.maxZ) * ((float)Math.PI / 180);
                TMath.toQuaternion(euler, q);
                this.deltaNorm.rotate((Quaternionfc)q, localPosition).mul(finalBoneLength / this.modelScale);
            }
        } else {
            this.deltaNorm.rotationTo((Vector3fc)localPosition, q);
        }
        transformMatrix.set((Matrix4fc)parentTransform).translate((Vector3fc)localPosition);
        this.worldLocation.set((Vector3fc)TMath.transform(transformMatrix, new Vector3f())).rotateY(-this.bone.getYaw() * ((float)Math.PI / 180)).mul(this.modelScale).add((Vector3fc)pivotLocation);
        transformMatrix.rotate((Quaternionfc)q).rotate((Quaternionfc)this.bone.getBlueprintBone().getLocalQuaternion());
    }

    private <R> R getParent(Function<ModelBone, R> function, R def) {
        return this.bone.getParent() == null ? def : function.apply(this.bone.getParent());
    }

    @Generated
    public boolean isBackPivot() {
        return this.backPivot;
    }

    @Override
    @Generated
    public boolean isBounded() {
        return this.bounded;
    }

    @Override
    @Generated
    public float getMinX() {
        return this.minX;
    }

    @Override
    @Generated
    public float getMaxX() {
        return this.maxX;
    }

    @Override
    @Generated
    public float getMinY() {
        return this.minY;
    }

    @Override
    @Generated
    public float getMaxY() {
        return this.maxY;
    }

    @Override
    @Generated
    public float getMinZ() {
        return this.minZ;
    }

    @Override
    @Generated
    public float getMaxZ() {
        return this.maxZ;
    }

    @Override
    @Generated
    public float getAlignRate() {
        return this.alignRate;
    }

    @Override
    @Generated
    public float getElasticity() {
        return this.elasticity;
    }

    @Override
    @Generated
    public Vector3f getDelta() {
        return this.delta;
    }

    @Generated
    public Vector3f getDeltaNorm() {
        return this.deltaNorm;
    }

    @Override
    @Generated
    public Vector3f getWorldLocation() {
        return this.worldLocation;
    }

    @Generated
    public UUID getScaleCallback() {
        return this.scaleCallback;
    }

    @Generated
    public float getBoneLength() {
        return this.boneLength;
    }

    @Generated
    public float getModelScale() {
        return this.modelScale;
    }

    @Override
    @Generated
    public void setBounded(boolean bounded) {
        this.bounded = bounded;
    }

    @Override
    @Generated
    public void setMinX(float minX) {
        this.minX = minX;
    }

    @Override
    @Generated
    public void setMaxX(float maxX) {
        this.maxX = maxX;
    }

    @Override
    @Generated
    public void setMinY(float minY) {
        this.minY = minY;
    }

    @Override
    @Generated
    public void setMaxY(float maxY) {
        this.maxY = maxY;
    }

    @Override
    @Generated
    public void setMinZ(float minZ) {
        this.minZ = minZ;
    }

    @Override
    @Generated
    public void setMaxZ(float maxZ) {
        this.maxZ = maxZ;
    }

    @Override
    @Generated
    public void setAlignRate(float alignRate) {
        this.alignRate = alignRate;
    }

    @Override
    @Generated
    public void setElasticity(float elasticity) {
        this.elasticity = elasticity;
    }

    @Generated
    public void setScaleCallback(UUID scaleCallback) {
        this.scaleCallback = scaleCallback;
    }

    @Generated
    public void setBoneLength(float boneLength) {
        this.boneLength = boneLength;
    }

    @Generated
    public void setModelScale(float modelScale) {
        this.modelScale = modelScale;
    }
}

