/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package com.ticxo.modelengine.api.model.bone.behavior;

import com.ticxo.modelengine.api.events.BoneTransformReadEvent;
import com.ticxo.modelengine.api.model.bone.ModelBone;
import com.ticxo.modelengine.api.model.bone.behavior.BoneBehaviorData;
import com.ticxo.modelengine.api.model.bone.behavior.BoneBehaviorType;
import com.ticxo.modelengine.api.utils.data.io.DataIO;
import com.ticxo.modelengine.api.utils.data.io.SavedData;
import org.jetbrains.annotations.Nullable;

public interface BoneBehavior
extends DataIO {
    public ModelBone getBone();

    public BoneBehaviorType<?> getType();

    public BoneBehaviorData getData();

    default public void onApply() {
    }

    default public void onRemove() {
    }

    default public void onModelInitialized() {
    }

    default public void onParentSwap(@Nullable ModelBone parent) {
    }

    default public void preAnimation() {
    }

    default public void onAnimation() {
    }

    default public void postAnimation() {
    }

    default public void onGlobalCalculation() {
    }

    default public void preGlobalCalculation() {
    }

    default public void preGlobalTranslate() {
    }

    default public void postGlobalTranslate() {
    }

    default public void preGlobalRotate() {
    }

    default public void postGlobalRotate() {
    }

    default public void preGlobalScale() {
    }

    default public void postGlobalScale() {
    }

    default public void preGlobalShear() {
    }

    default public void postGlobalShear() {
    }

    default public void postGlobalCalculation() {
    }

    default public void preTransformDecompose() {
    }

    default public void postTransformDecompose() {
    }

    default public void postTransformRecompose() {
    }

    default public float onUpdateYaw(float yaw) {
        return yaw;
    }

    @Deprecated
    default public void preChildCalculation() {
    }

    @Deprecated
    default public void postChildCalculation() {
    }

    default public void onFinalize() {
    }

    default public void modifyTransform(BoneTransformReadEvent event) {
    }

    default public void preRender() {
    }

    default public void onRender() {
    }

    default public void postRender() {
    }

    default public boolean isHidden() {
        return false;
    }

    @Override
    default public void save(SavedData data) {
    }

    @Override
    default public void load(SavedData data) {
    }
}

