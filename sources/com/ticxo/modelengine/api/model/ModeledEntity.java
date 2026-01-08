/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.ticxo.modelengine.api.model;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.animation.rootmotion.RootMotionHandler;
import com.ticxo.modelengine.api.entity.BaseEntity;
import com.ticxo.modelengine.api.lod.AnimationLODHandler;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.bone.behavior.BoneBehavior;
import com.ticxo.modelengine.api.model.bone.behavior.BoneBehaviorType;
import com.ticxo.modelengine.api.model.bone.behavior.GlobalBehaviorData;
import com.ticxo.modelengine.api.model.bone.manager.MountData;
import com.ticxo.modelengine.api.utils.callback.Callback;
import com.ticxo.modelengine.api.utils.data.io.DataIO;
import com.ticxo.modelengine.api.utils.meta.IMetaHolder;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ModeledEntity
extends DataIO,
IMetaHolder {
    public BaseEntity<?> getBase();

    public AnimationLODHandler getAnimationLodHandler();

    public int getTick();

    public boolean tick();

    public void destroy();

    public boolean isInitialized();

    public boolean isDestroyed();

    public void markRemoved();

    public void restore();

    public void queuePostInitTask(Runnable var1);

    public boolean isBaseEntityVisible();

    public void setBaseEntityVisible(boolean var1);

    public void markHurt();

    public int getHurtTick();

    public boolean shouldBeSaved();

    public void setSaved(boolean var1);

    public boolean isModelRotationLocked();

    public void setModelRotationLocked(boolean var1);

    public boolean isGlowing();

    public int getGlowColor();

    public float getYHeadRot();

    public float getXHeadRot();

    public float getYBodyRot();

    public void setYHeadRot(float var1);

    public void setXHeadRot(float var1);

    public void setYBodyRot(float var1);

    public void setYHeadRotImmediately(float var1);

    public void setXHeadRotImmediately(float var1);

    public void setYBodyRotImmediately(float var1);

    public void setRotationTick(int var1);

    public Optional<ActiveModel> addModel(@NotNull ActiveModel var1, boolean var2);

    public Optional<ActiveModel> removeModel(String var1);

    public Optional<ActiveModel> getModel(@Nullable String var1);

    public Map<String, ActiveModel> getModels();

    public <T extends BoneBehavior> GlobalBehaviorData getOrCreateGlobalBehaviorData(BoneBehaviorType<T> var1, Supplier<GlobalBehaviorData> var2);

    public <T extends BoneBehavior> GlobalBehaviorData getGlobalBehaviorData(BoneBehaviorType<T> var1);

    public <T extends BoneBehavior> GlobalBehaviorData removeGlobalBehaviorData(BoneBehaviorType<T> var1);

    public Map<BoneBehaviorType<?>, GlobalBehaviorData> getAllGlobalBehaviorData(BoneBehaviorType<?> var1);

    public <T extends GlobalBehaviorData & MountData> T getMountData();

    public RootMotionHandler getRootMotionHandler();

    public boolean isPivotOverride(int var1);

    public UUID registerTickTask(Phase var1, Function<ModeledEntity, Boolean> var2);

    public UUID registerTickTask(Phase var1, Consumer<ModeledEntity> var2);

    public void removeTickTask(UUID var1);

    public void runTickTasks(Phase var1);

    public Callback<Runnable> getDestroyCallback();

    default public void registerSelf() {
        ModelEngineAPI.getAPI().getModelUpdaters().registerModeledEntity(this.getBase(), this).ifPresent(ModeledEntity::destroy);
        this.getBase().registerData();
    }

    public static enum Phase {
        PRE_DATA_SYNC,
        PRE_MODEL_TICK,
        PRE_MODEL_RENDER,
        POST_MODEL_RENDER;

    }
}

