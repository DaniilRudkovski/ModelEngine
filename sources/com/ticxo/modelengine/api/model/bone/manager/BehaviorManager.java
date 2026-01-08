/*
 * Decompiled with CFR 0.152.
 */
package com.ticxo.modelengine.api.model.bone.manager;

import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.bone.behavior.BoneBehavior;
import com.ticxo.modelengine.api.model.bone.behavior.BoneBehaviorType;
import com.ticxo.modelengine.api.utils.data.io.DataIO;
import com.ticxo.modelengine.api.utils.data.io.SavedData;

public interface BehaviorManager<T extends BoneBehavior>
extends DataIO {
    public ActiveModel getActiveModel();

    public BoneBehaviorType<T> getType();

    default public void onCreate() {
    }

    default public void onDestroy() {
    }

    default public void preBoneTick() {
    }

    default public void postBoneTick() {
    }

    default public void preScriptTick() {
    }

    default public void postScriptTick() {
    }

    default public void preBoneRender() {
    }

    default public void postBoneRender() {
    }

    @Override
    default public void load(SavedData data) {
    }

    @Override
    default public void save(SavedData data) {
    }
}

