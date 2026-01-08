/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Maps
 *  lombok.Generated
 *  org.bukkit.entity.Entity
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Vector3fc
 */
package com.ticxo.modelengine.core.model;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.animation.rootmotion.RootMotionHandler;
import com.ticxo.modelengine.api.entity.BaseEntity;
import com.ticxo.modelengine.api.entity.Hitbox;
import com.ticxo.modelengine.api.events.AddModelEvent;
import com.ticxo.modelengine.api.events.ModelSetupEvent;
import com.ticxo.modelengine.api.events.RemoveModelEvent;
import com.ticxo.modelengine.api.lod.AnimationLODHandler;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.ModeledEntity;
import com.ticxo.modelengine.api.model.bone.BoneBehaviorTypes;
import com.ticxo.modelengine.api.model.bone.behavior.BoneBehavior;
import com.ticxo.modelengine.api.model.bone.behavior.BoneBehaviorType;
import com.ticxo.modelengine.api.model.bone.behavior.GlobalBehaviorData;
import com.ticxo.modelengine.api.model.bone.manager.MountData;
import com.ticxo.modelengine.api.nms.entity.wrapper.BodyRotationController;
import com.ticxo.modelengine.api.utils.callback.Callback;
import com.ticxo.modelengine.api.utils.data.io.SavedData;
import com.ticxo.modelengine.api.utils.data.tracker.DataTracker;
import com.ticxo.modelengine.api.utils.math.TMath;
import com.ticxo.modelengine.api.utils.meta.MetadataRegistry;
import com.ticxo.modelengine.core.model.ActiveModelImpl;
import com.ticxo.modelengine.core.model.bone.manager.MountDataImpl;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import lombok.Generated;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3fc;

public class ModeledEntityImpl
implements ModeledEntity {
    private final BaseEntity<?> base;
    private final MetadataRegistry metadata = new MetadataRegistry();
    private final AnimationLODHandler animationLodHandler;
    private final Map<String, ActiveModel> models = Maps.newConcurrentMap();
    private final Map<BoneBehaviorType<?>, GlobalBehaviorData> data = Maps.newConcurrentMap();
    private final Map<ModeledEntity.Phase, Map<UUID, Function<ModeledEntity, Boolean>>> tickTasks = Maps.newConcurrentMap();
    private final Callback<Runnable> destroyCallback = new Callback<Runnable>(runs -> () -> runs.forEach(Runnable::run));
    private final boolean initialized;
    private final List<Runnable> queuedTask = new ArrayList<Runnable>();
    private final DataTracker<Float> trueYHeadRot = new DataTracker<Float>(Float.valueOf(0.0f), TMath::isSimilar);
    private final DataTracker<Float> trueXHeadRot = new DataTracker<Float>(Float.valueOf(0.0f), TMath::isSimilar);
    private final DataTracker<Float> trueYBodyRot = new DataTracker<Float>(Float.valueOf(0.0f), TMath::isSimilar);
    private final RootMotionHandler rootMotionHandler = new RootMotionHandler(this);
    private int tick;
    private boolean isBaseEntityVisible = true;
    private boolean destroyed;
    private boolean removed;
    private int hurtTick = 0;
    private boolean isModelRotationLocked;
    private int rotationTick = -1;
    private float yHeadRot = 0.0f;
    private float xHeadRot = 0.0f;
    private float yBodyRot = 0.0f;
    private boolean shouldSave = true;
    private ActiveModel lastHitboxOverride = null;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public ModeledEntityImpl(@NotNull BaseEntity<?> base, @Nullable Consumer<ModeledEntity> consumer) {
        this.base = base;
        this.animationLodHandler = new AnimationLODHandler(this);
        if (consumer != null) {
            consumer.accept(this);
        }
        List<Runnable> list = this.queuedTask;
        synchronized (list) {
            this.queuedTask.forEach(Runnable::run);
            this.initialized = true;
        }
        this.registerSelf();
    }

    @Override
    public boolean tick() {
        if (!this.initialized) {
            return true;
        }
        if (this.hurtTick > 0) {
            --this.hurtTick;
        }
        if (this.base.isAlive()) {
            if (!this.isModelRotationLocked) {
                BodyRotationController bodyRotationController = this.base.getBodyRotationController();
                bodyRotationController.tick();
                this.trueYHeadRot.set(Float.valueOf(bodyRotationController.getYHeadRot()));
                this.trueXHeadRot.set(Float.valueOf(bodyRotationController.getXHeadRot()));
                this.trueYBodyRot.set(Float.valueOf(bodyRotationController.getYBodyRot()));
            }
            if (this.rotationTick == -1) {
                this.yHeadRot = this.trueYHeadRot.get().floatValue();
                this.xHeadRot = this.trueXHeadRot.get().floatValue();
                this.yBodyRot = this.trueYBodyRot.get().floatValue();
                this.trueYHeadRot.clearDirty();
                this.trueXHeadRot.clearDirty();
                this.trueYBodyRot.clearDirty();
                this.rotationTick = 0;
            }
            if (!this.base.isWalking()) {
                this.yBodyRot = this.trueYBodyRot.get().floatValue();
            }
            if (this.trueYHeadRot.isDirty() || this.trueXHeadRot.isDirty() || this.trueYBodyRot.isDirty()) {
                this.rotationTick = 3;
                this.trueYHeadRot.clearDirty();
                this.trueXHeadRot.clearDirty();
                this.trueYBodyRot.clearDirty();
            }
            if (this.rotationTick > 0) {
                this.yHeadRot = TMath.rotLerp(this.yHeadRot, this.trueYHeadRot.get().floatValue(), (double)(1.0f / (float)this.rotationTick));
                this.xHeadRot = TMath.rotLerp(this.xHeadRot, this.trueXHeadRot.get().floatValue(), (double)(1.0f / (float)this.rotationTick));
                this.yBodyRot = TMath.rotLerp(this.yBodyRot, this.trueYBodyRot.get().floatValue(), (double)(1.0f / (float)this.rotationTick));
                --this.rotationTick;
            }
        }
        this.validateYaws();
        ++this.tick;
        if (this.removed || this.base.isRemoved()) {
            return false;
        }
        if (this.base.isAlive()) {
            for (ActiveModel model : this.models.values()) {
                model.tick();
            }
            return true;
        }
        boolean hasFinished = true;
        for (ActiveModel model : this.models.values()) {
            model.tick();
            if (!hasFinished) continue;
            hasFinished = model.getAnimationHandler().hasFinishedAllAnimations();
        }
        this.base.setForcedAlive(!hasFinished);
        return !hasFinished && !this.base.getData().getTracking().isEmpty();
    }

    private void validateYaws() {
        if (Float.isNaN(this.trueYHeadRot.get().floatValue())) {
            this.trueYHeadRot.set(Float.valueOf(0.0f));
        }
        if (Float.isNaN(this.trueXHeadRot.get().floatValue())) {
            this.trueXHeadRot.set(Float.valueOf(0.0f));
        }
        if (Float.isNaN(this.trueYBodyRot.get().floatValue())) {
            this.trueYBodyRot.set(Float.valueOf(0.0f));
        }
        this.yHeadRot = Float.isNaN(this.yHeadRot) ? 0.0f : this.yHeadRot;
        this.xHeadRot = Float.isNaN(this.xHeadRot) ? 0.0f : this.xHeadRot;
        this.yBodyRot = Float.isNaN(this.yBodyRot) ? 0.0f : this.yBodyRot;
    }

    @Override
    public void destroy() {
        this.destroyCallback.invoker().run();
        this.destroyed = true;
        this.animationLodHandler.destroy();
        this.models.forEach((s, model) -> model.destroy());
        this.models.clear();
    }

    @Override
    public void markRemoved() {
        this.removed = true;
    }

    @Override
    public void restore() {
        this.removed = false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void queuePostInitTask(Runnable runnable) {
        List<Runnable> list = this.queuedTask;
        synchronized (list) {
            if (this.initialized) {
                runnable.run();
            } else {
                this.queuedTask.add(runnable);
            }
        }
    }

    @Override
    public void setBaseEntityVisible(boolean flag) {
        if (this.isBaseEntityVisible() == flag) {
            return;
        }
        this.isBaseEntityVisible = flag;
        this.base.setVisible(this.isBaseEntityVisible);
    }

    @Override
    public void markHurt() {
        this.hurtTick = 10;
    }

    @Override
    public boolean shouldBeSaved() {
        return this.shouldSave;
    }

    @Override
    public void setSaved(boolean flag) {
        this.shouldSave = flag;
    }

    @Override
    public boolean isGlowing() {
        return this.base.isGlowing();
    }

    @Override
    public int getGlowColor() {
        return this.base.getGlowColor();
    }

    @Override
    public void setYHeadRot(float rotation) {
        this.trueYHeadRot.set(Float.valueOf(rotation));
    }

    @Override
    public void setXHeadRot(float rotation) {
        this.trueXHeadRot.set(Float.valueOf(rotation));
    }

    @Override
    public void setYBodyRot(float rotation) {
        this.trueYBodyRot.set(Float.valueOf(rotation));
    }

    @Override
    public void setYHeadRotImmediately(float rotation) {
        this.trueYHeadRot.set(Float.valueOf(rotation));
        this.yHeadRot = rotation;
    }

    @Override
    public void setXHeadRotImmediately(float rotation) {
        this.trueXHeadRot.set(Float.valueOf(rotation));
        this.xHeadRot = rotation;
    }

    @Override
    public void setYBodyRotImmediately(float rotation) {
        this.trueYBodyRot.set(Float.valueOf(rotation));
        this.yBodyRot = rotation;
    }

    @Override
    public void setRotationTick(int rotationTick) {
        this.rotationTick = rotationTick;
    }

    @Override
    public Optional<ActiveModel> addModel(@NotNull ActiveModel model, boolean overrideHitbox) {
        assert (!this.isDestroyed()) : "Modeled Entity has been destroyed!";
        assert (model.getModeledEntity() == null || model.isRemoved()) : "Active Model already belongs to a different Modeled Entity";
        AddModelEvent event = new AddModelEvent(this, model);
        event.setOverrideHitbox(overrideHitbox);
        ModelEngineAPI.callEvent(event);
        if (event.isCancelled()) {
            return Optional.empty();
        }
        model.setRemoved(false);
        model.setModeledEntity(this);
        model.generateModel();
        Optional<ActiveModel> previous = this.removeModel(model.getBlueprint().getName());
        this.models.put(model.getBlueprint().getName(), model);
        model.getMountManager().ifPresent(mountManager -> {
            Object mountData = this.getMountData();
            if (((MountData)mountData).getMainMountManager() == null) {
                ((MountData)mountData).setMainMountManager(mountManager);
            }
        });
        if (overrideHitbox) {
            if (this.lastHitboxOverride != null) {
                this.lastHitboxOverride.setMainHitbox(false);
            }
            model.setMainHitbox(true);
            this.lastHitboxOverride = model;
            Hitbox mainHitbox = model.getBlueprint().getMainHitbox();
            Vector3fc scale = model.getScale();
            Hitbox scaledRenderHitbox = new Hitbox(mainHitbox.getWidth() * (double)scale.x(), mainHitbox.getHeight() * (double)scale.y(), mainHitbox.getDepth() * (double)scale.z(), mainHitbox.getEyeHeight() * (double)scale.y());
            this.base.getData().setCullHitbox(scaledRenderHitbox);
            Object obj = this.base.getOriginal();
            if (obj instanceof Entity) {
                Entity entity = (Entity)obj;
                Vector3fc hitboxScale = model.getHitboxScale();
                Hitbox scaledHitbox = new Hitbox(mainHitbox.getWidth() * (double)hitboxScale.x(), mainHitbox.getHeight() * (double)hitboxScale.y(), mainHitbox.getDepth() * (double)hitboxScale.z(), mainHitbox.getEyeHeight() * (double)hitboxScale.y());
                ModelEngineAPI.getEntityHandler().setHitbox(entity, scaledHitbox);
            }
        }
        ModelEngineAPI.callEvent(new ModelSetupEvent(this, model));
        return previous;
    }

    @Override
    public Optional<ActiveModel> removeModel(String id) {
        assert (!this.isDestroyed()) : "Modeled Entity has been destroyed!";
        ActiveModel model = this.models.get(id);
        if (model == null) {
            return Optional.empty();
        }
        RemoveModelEvent event = new RemoveModelEvent(this, model);
        ModelEngineAPI.callEvent(event);
        if (event.isCancelled()) {
            return Optional.empty();
        }
        this.models.remove(id);
        model.setRemoved(true);
        return Optional.of(model);
    }

    @Override
    public Optional<ActiveModel> getModel(@Nullable String id) {
        return Optional.ofNullable(id == null ? null : this.models.get(id));
    }

    @Override
    public Map<String, ActiveModel> getModels() {
        return ImmutableMap.copyOf(this.models);
    }

    @Override
    public <T extends BoneBehavior> GlobalBehaviorData getOrCreateGlobalBehaviorData(BoneBehaviorType<T> type, Supplier<GlobalBehaviorData> supplier) {
        return this.data.computeIfAbsent(type, boneBehaviorType -> (GlobalBehaviorData)supplier.get());
    }

    @Override
    public <T extends BoneBehavior> GlobalBehaviorData getGlobalBehaviorData(BoneBehaviorType<T> type) {
        return this.data.get(type);
    }

    @Override
    public <T extends BoneBehavior> GlobalBehaviorData removeGlobalBehaviorData(BoneBehaviorType<T> type) {
        return this.data.remove(type);
    }

    @Override
    public Map<BoneBehaviorType<?>, GlobalBehaviorData> getAllGlobalBehaviorData(BoneBehaviorType<?> type) {
        return ImmutableMap.copyOf(this.data);
    }

    @Override
    public <T extends GlobalBehaviorData & MountData> T getMountData() {
        return (T)this.getOrCreateGlobalBehaviorData(BoneBehaviorTypes.MOUNT, MountDataImpl::new);
    }

    @Override
    public boolean isPivotOverride(int id) {
        return this.models.values().stream().anyMatch(model -> model.getPivotOverride().map(override -> override.getId() == id).orElse(false));
    }

    @Override
    public UUID registerTickTask(ModeledEntity.Phase phase, Function<ModeledEntity, Boolean> function) {
        UUID uuid = UUID.randomUUID();
        this.tickTasks.computeIfAbsent(phase, phase1 -> Maps.newConcurrentMap()).put(uuid, function);
        return uuid;
    }

    @Override
    public UUID registerTickTask(ModeledEntity.Phase phase, Consumer<ModeledEntity> consumer) {
        return this.registerTickTask(phase, (ModeledEntity modeledEntity) -> {
            consumer.accept((ModeledEntity)modeledEntity);
            return false;
        });
    }

    @Override
    public void removeTickTask(UUID uuid) {
        for (Map.Entry<ModeledEntity.Phase, Map<UUID, Function<ModeledEntity, Boolean>>> entry : this.tickTasks.entrySet()) {
            if (entry.getValue().remove(uuid) == null) continue;
            return;
        }
    }

    @Override
    public void runTickTasks(ModeledEntity.Phase phase) {
        Map<UUID, Function<ModeledEntity, Boolean>> tasks = this.tickTasks.get((Object)phase);
        if (tasks != null) {
            HashSet remover = new HashSet();
            tasks.forEach((uuid, consumer) -> {
                if (((Boolean)consumer.apply(this)).booleanValue()) {
                    remover.add(uuid);
                }
            });
            remover.forEach(tasks::remove);
        }
    }

    @Override
    public void save(SavedData data) {
        data.putString("version", "R4.0.3");
        data.putBoolean("base_visible", this.isBaseEntityVisible());
        data.putBoolean("rotation_locked", this.isModelRotationLocked());
        data.putFloat("head_y", this.trueYHeadRot.get());
        data.putFloat("head_x", this.trueXHeadRot.get());
        data.putFloat("body_y", this.trueYBodyRot.get());
        ArrayList list = new ArrayList();
        for (ActiveModel activeModel : this.models.values()) {
            activeModel.save().ifPresent(list::add);
        }
        data.putList("models", list);
        this.base.save().ifPresent(entityData -> data.putData("base_entity", (SavedData)entityData));
    }

    @Override
    public void load(SavedData data) {
        this.setBaseEntityVisible(data.getBoolean("base_visible"));
        this.setModelRotationLocked(data.getBoolean("rotation_locked"));
        this.yHeadRot = data.getFloat("head_y", Float.valueOf(0.0f)).floatValue();
        this.trueYHeadRot.set(Float.valueOf(this.yHeadRot));
        this.xHeadRot = data.getFloat("head_x", Float.valueOf(0.0f)).floatValue();
        this.trueXHeadRot.set(Float.valueOf(this.xHeadRot));
        this.yBodyRot = data.getFloat("body_y", Float.valueOf(0.0f)).floatValue();
        this.trueYBodyRot.set(Float.valueOf(this.yBodyRot));
        this.trueYHeadRot.clearDirty();
        this.trueXHeadRot.clearDirty();
        this.trueYBodyRot.clearDirty();
        this.base.getBodyRotationController().setYBodyRot(this.yBodyRot);
        List<SavedData> list = data.getList("models", SavedData.class);
        for (SavedData modelData : list) {
            ActiveModel model = ActiveModelImpl.fromData(modelData);
            if (model == null) continue;
            model.setAutoRendererInitialization(false);
            this.addModel(model, model.isMainHitbox()).ifPresent(ActiveModel::destroy);
            model.load(modelData);
            model.initializeRenderer();
        }
        data.getData("base_entity").ifPresent(this.base::load);
    }

    @Override
    @Generated
    public BaseEntity<?> getBase() {
        return this.base;
    }

    @Override
    @Generated
    public MetadataRegistry getMetadata() {
        return this.metadata;
    }

    @Override
    @Generated
    public AnimationLODHandler getAnimationLodHandler() {
        return this.animationLodHandler;
    }

    @Generated
    public Map<BoneBehaviorType<?>, GlobalBehaviorData> getData() {
        return this.data;
    }

    @Generated
    public Map<ModeledEntity.Phase, Map<UUID, Function<ModeledEntity, Boolean>>> getTickTasks() {
        return this.tickTasks;
    }

    @Override
    @Generated
    public Callback<Runnable> getDestroyCallback() {
        return this.destroyCallback;
    }

    @Override
    @Generated
    public boolean isInitialized() {
        return this.initialized;
    }

    @Generated
    public List<Runnable> getQueuedTask() {
        return this.queuedTask;
    }

    @Generated
    public DataTracker<Float> getTrueYHeadRot() {
        return this.trueYHeadRot;
    }

    @Generated
    public DataTracker<Float> getTrueXHeadRot() {
        return this.trueXHeadRot;
    }

    @Generated
    public DataTracker<Float> getTrueYBodyRot() {
        return this.trueYBodyRot;
    }

    @Override
    @Generated
    public RootMotionHandler getRootMotionHandler() {
        return this.rootMotionHandler;
    }

    @Override
    @Generated
    public int getTick() {
        return this.tick;
    }

    @Override
    @Generated
    public boolean isBaseEntityVisible() {
        return this.isBaseEntityVisible;
    }

    @Override
    @Generated
    public boolean isDestroyed() {
        return this.destroyed;
    }

    @Generated
    public boolean isRemoved() {
        return this.removed;
    }

    @Override
    @Generated
    public int getHurtTick() {
        return this.hurtTick;
    }

    @Override
    @Generated
    public boolean isModelRotationLocked() {
        return this.isModelRotationLocked;
    }

    @Generated
    public int getRotationTick() {
        return this.rotationTick;
    }

    @Override
    @Generated
    public float getYHeadRot() {
        return this.yHeadRot;
    }

    @Override
    @Generated
    public float getXHeadRot() {
        return this.xHeadRot;
    }

    @Override
    @Generated
    public float getYBodyRot() {
        return this.yBodyRot;
    }

    @Generated
    public boolean isShouldSave() {
        return this.shouldSave;
    }

    @Generated
    public ActiveModel getLastHitboxOverride() {
        return this.lastHitboxOverride;
    }

    @Override
    @Generated
    public void setModelRotationLocked(boolean isModelRotationLocked) {
        this.isModelRotationLocked = isModelRotationLocked;
    }
}

