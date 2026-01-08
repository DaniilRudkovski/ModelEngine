/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Maps
 *  lombok.Generated
 *  org.bukkit.Color
 *  org.bukkit.entity.Display$Billboard
 *  org.bukkit.entity.Entity
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 */
package com.ticxo.modelengine.core.model;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.animation.handler.AnimationHandler;
import com.ticxo.modelengine.api.entity.Hitbox;
import com.ticxo.modelengine.api.entity.data.IEntityData;
import com.ticxo.modelengine.api.generator.blueprint.BlueprintBone;
import com.ticxo.modelengine.api.generator.blueprint.ModelBlueprint;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.ModeledEntity;
import com.ticxo.modelengine.api.model.PivotOverride;
import com.ticxo.modelengine.api.model.bone.ModelBone;
import com.ticxo.modelengine.api.model.bone.behavior.BoneBehavior;
import com.ticxo.modelengine.api.model.bone.behavior.BoneBehaviorType;
import com.ticxo.modelengine.api.model.bone.manager.BehaviorManager;
import com.ticxo.modelengine.api.model.bone.render.BehaviorRenderer;
import com.ticxo.modelengine.api.model.render.ModelRenderer;
import com.ticxo.modelengine.api.nms.entity.EntityHandler;
import com.ticxo.modelengine.api.utils.callback.Callback;
import com.ticxo.modelengine.api.utils.config.ConfigProperty;
import com.ticxo.modelengine.api.utils.data.io.SavedData;
import com.ticxo.modelengine.api.utils.logger.TLogger;
import com.ticxo.modelengine.api.utils.meta.MetadataRegistry;
import com.ticxo.modelengine.core.animation.handler.PriorityHandler;
import com.ticxo.modelengine.core.animation.handler.StateMachineHandler;
import com.ticxo.modelengine.core.model.BoneSequencer;
import com.ticxo.modelengine.core.model.bone.ModelBoneImpl;
import com.ticxo.modelengine.core.model.render.DisplayRendererImpl;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import lombok.Generated;
import org.bukkit.Color;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class ActiveModelImpl
implements ActiveModel {
    private final ModelBlueprint blueprint;
    private final MetadataRegistry metadata = new MetadataRegistry();
    private final ModelRenderer modelRenderer;
    private final AnimationHandler animationHandler;
    private final Map<String, ModelBone> bones = Maps.newConcurrentMap();
    private final Map<String, ModelBone> roots = Maps.newConcurrentMap();
    private final Map<BoneBehaviorType<?>, BehaviorManager<?>> behaviorManagers = new LinkedHashMap();
    private final Map<BoneBehaviorType<?>, BehaviorRenderer> behaviorRenderers = new LinkedHashMap();
    private final Vector3f scale = new Vector3f(1.0f, 1.0f, 1.0f);
    private final Vector3f hitboxScale = new Vector3f(1.0f, 1.0f, 1.0f);
    private UUID pivotOverride;
    private ModeledEntity modeledEntity;
    private boolean mainHitbox;
    private boolean generated;
    private boolean destroyed;
    private boolean removed;
    private boolean autoRendererInitialization = true;
    private boolean hitboxVisible = true;
    private boolean shadowVisible = true;
    private boolean canHurt = true;
    private Color defaultTint = Color.fromRGB((int)0xFFFFFF);
    private Color damageTint = Color.fromRGB((int)0xFF6666);
    private boolean wasMarkedHurt;
    private Boolean glowing;
    private Integer glowColor;
    private int blockLight = -1;
    private int skyLight = -1;
    private Display.Billboard billboard = Display.Billboard.FIXED;
    private Boolean onFire;
    private Boolean renderFire;
    private boolean lockPitch;
    private boolean lockYaw;
    private boolean invisUpdate;
    private final BoneSequencer boneSequencer = new BoneSequencer();
    private final Callback<ActiveModel.Scale> scaleCallback = new Callback<ActiveModel.Scale>(scales -> (model, scale) -> scales.forEach(callback -> callback.onScale(model, scale)));

    public ActiveModelImpl(@NotNull ModelBlueprint blueprint, @Nullable Function<ActiveModel, ModelRenderer> rendererSupplier, @Nullable Function<ActiveModel, AnimationHandler> handlerSupplier) {
        this.blueprint = blueprint;
        ModelRenderer renderer = rendererSupplier == null ? new DisplayRendererImpl(this) : rendererSupplier.apply(this);
        this.modelRenderer = renderer == null ? new DisplayRendererImpl(this) : renderer;
        AnimationHandler handler = handlerSupplier == null ? ActiveModelImpl.createDefaultHandler(this) : handlerSupplier.apply(this);
        this.animationHandler = handler == null ? ActiveModelImpl.createDefaultHandler(this) : handler;
    }

    private static AnimationHandler createDefaultHandler(ActiveModel activeModel) {
        return ConfigProperty.USE_STATE_MACHINE.getBoolean() ? new StateMachineHandler(activeModel) : new PriorityHandler(activeModel);
    }

    public static ActiveModel fromData(SavedData data) {
        try {
            return ModelEngineAPI.createActiveModel(data.getString("blueprint"), null, activeModel -> {
                activeModel.setMainHitbox(data.getBoolean("main_hitbox"));
                return data.getData("animation_handler").map(handlerData -> ModelEngineAPI.getAnimationHandlerRegistry().createHandler((ActiveModel)activeModel, (SavedData)handlerData)).orElse(null);
            });
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Optional<PivotOverride> getPivotOverride() {
        return ModelEngineAPI.getPivotOverrideRegistry().get(this.pivotOverride);
    }

    @Override
    public void setPivotOverride(PivotOverride override) {
        this.modelRenderer.updatePivotOverride(override);
        this.pivotOverride = override.getUuid();
    }

    @Override
    public Map<String, ModelBone> getBones() {
        return ImmutableMap.copyOf(this.bones);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Map<BoneBehaviorType<?>, BehaviorManager<?>> getBehaviorManagers() {
        Map<BoneBehaviorType<?>, BehaviorManager<?>> map = this.behaviorManagers;
        synchronized (map) {
            return ImmutableMap.copyOf(this.behaviorManagers);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Map<BoneBehaviorType<?>, BehaviorRenderer> getBehaviorRenderers() {
        Map<BoneBehaviorType<?>, BehaviorRenderer> map = this.behaviorRenderers;
        synchronized (map) {
            return ImmutableMap.copyOf(this.behaviorRenderers);
        }
    }

    @Override
    public Vector3fc getScale() {
        return this.scale;
    }

    @Override
    public void setScale(double scale) {
        this.scale.set(scale);
        this.scaleCallback.invoker().onScale(this, scale);
        if (!this.mainHitbox || this.modeledEntity == null) {
            return;
        }
        Vector3fc scaleVec = this.getScale();
        Hitbox mainHitbox = this.blueprint.getMainHitbox();
        Hitbox scaledRenderHitbox = new Hitbox(mainHitbox.getWidth() * (double)scaleVec.x(), mainHitbox.getHeight() * (double)scaleVec.y(), mainHitbox.getDepth() * (double)scaleVec.z(), mainHitbox.getEyeHeight() * (double)scaleVec.y());
        this.modeledEntity.getBase().getData().setCullHitbox(scaledRenderHitbox);
    }

    @Override
    public Vector3fc getHitboxScale() {
        return this.hitboxScale;
    }

    @Override
    public void setHitboxScale(double scale) {
        this.hitboxScale.set(scale);
        if (!this.mainHitbox || this.modeledEntity == null) {
            return;
        }
        Object obj = this.modeledEntity.getBase().getOriginal();
        if (obj instanceof Entity) {
            Entity entity = (Entity)obj;
            EntityHandler entityHandler = ModelEngineAPI.getEntityHandler();
            Vector3fc hitboxScale = this.getHitboxScale();
            Hitbox mainHitbox = this.blueprint.getMainHitbox();
            Hitbox scaledHitbox = new Hitbox(mainHitbox.getWidth() * (double)hitboxScale.x(), mainHitbox.getHeight() * (double)hitboxScale.y(), mainHitbox.getDepth() * (double)hitboxScale.z(), mainHitbox.getEyeHeight() * (double)hitboxScale.y());
            entityHandler.setHitbox(entity, scaledHitbox);
        }
    }

    @Override
    public void tick() {
        if (this.isDestroyed()) {
            return;
        }
        if (this.modeledEntity.getBase().getData().hasTracking()) {
            this.animationHandler.prepare();
            this.forManagers(BehaviorManager::preBoneTick);
            this.boneSequencer.reorder(this.roots);
            this.boneSequencer.getBoneOrders().forEach(boneId -> this.getBone((String)boneId).ifPresent(ModelBone::tick));
            this.forManagers(BehaviorManager::postBoneTick);
            this.forManagers(BehaviorManager::preScriptTick);
            this.animationHandler.tickGlobal();
            this.forManagers(BehaviorManager::postScriptTick);
            this.modelRenderer.readModelData();
            this.wasMarkedHurt = this.isMarkedHurt();
        } else {
            this.animationHandler.prepare();
            this.forBones(ModelBone::lazyTick);
            this.animationHandler.tickGlobal();
        }
    }

    @Override
    public void destroy() {
        this.forBones(ModelBone::destroy);
        this.forManagers(BehaviorManager::onDestroy);
        this.bones.clear();
        this.getData().markModelGlowing(this, false);
        this.modelRenderer.destroy(ModelEngineAPI.getNMSHandler().getGlobalParsers());
        this.destroyed = true;
    }

    @Override
    public void initializeRenderer() {
        if (!this.modelRenderer.isInitialized()) {
            this.modelRenderer.initialize();
        }
    }

    @Override
    public void generateModel() {
        if (this.generated) {
            return;
        }
        this.generated = true;
        for (Map.Entry<String, BlueprintBone> entry : this.blueprint.getFlatMap().entrySet()) {
            BlueprintBone blueprint = entry.getValue();
            ModelBone parent = blueprint.getParent() == null ? null : this.bones.get(blueprint.getParent().getName());
            ModelBoneImpl bone2 = new ModelBoneImpl(this, blueprint);
            if (parent != null) {
                bone2.setParent(parent);
                if (parent.isPivotOverride()) {
                    bone2.setPivot(parent);
                } else if (parent.getPivot() != null) {
                    bone2.setPivot(parent.getPivot());
                }
            } else {
                this.roots.put(bone2.getUniqueBoneId(), bone2);
            }
            for (Map.Entry<BoneBehaviorType<?>, BoneBehaviorType.CachedProvider<?>> behaviorEntry : blueprint.getCachedBehaviorProvider().entrySet()) {
                BoneBehaviorType<?> type = behaviorEntry.getKey();
                BoneBehaviorType.CachedProvider<?> provider = behaviorEntry.getValue();
                this.getBehaviorManager(type);
                this.getBehaviorRenderer(type);
                bone2.addBoneBehavior((BoneBehavior)provider.create(bone2));
            }
            this.bones.put(bone2.getUniqueBoneId(), bone2);
        }
        this.bones.forEach((s, bone) -> bone.forBehaviors(BoneBehavior::onModelInitialized));
        if (this.autoRendererInitialization) {
            this.modelRenderer.initialize();
        }
    }

    @Override
    public void forceGenerateBone(String parentId, String prefix, final BlueprintBone blueprintBone) {
        ModelBone parentBone = parentId == null ? null : (ModelBone)this.getBone(parentId).orElse(null);
        HashMap<String, ModelBone> forceBoneMap = new HashMap<String, ModelBone>();
        LinkedList<BlueprintBone> queue = new LinkedList<BlueprintBone>(){
            {
                this.add(blueprintBone);
            }
        };
        while (!queue.isEmpty()) {
            BlueprintBone blueprint = (BlueprintBone)queue.pop();
            queue.addAll(blueprint.getChildren().values());
            String name = blueprint.getName();
            String customId = prefix + name;
            if (this.bones.containsKey(customId)) {
                TLogger.error("Unable to force generate custom bone: ID " + customId + " already exists.");
                continue;
            }
            ModelBone parent = blueprint.getParent() == null ? parentBone : forceBoneMap.getOrDefault(blueprint.getParent().getName(), parentBone);
            ModelBoneImpl bone = new ModelBoneImpl(this, blueprint);
            bone.setCustomId(customId);
            if (parent != null) {
                bone.setParent(parent);
                if (parent.isPivotOverride()) {
                    bone.setPivot(parent);
                } else if (parent.getPivot() != null) {
                    bone.setPivot(parent.getPivot());
                }
            } else {
                this.roots.put(bone.getUniqueBoneId(), bone);
            }
            for (Map.Entry<BoneBehaviorType<?>, BoneBehaviorType.CachedProvider<?>> behaviorEntry : blueprint.getCachedBehaviorProvider().entrySet()) {
                BoneBehaviorType<?> type = behaviorEntry.getKey();
                BoneBehaviorType.CachedProvider<?> provider = behaviorEntry.getValue();
                this.getBehaviorManager(type);
                this.getBehaviorRenderer(type);
                bone.addBoneBehavior((BoneBehavior)provider.create(bone));
            }
            forceBoneMap.put(name, bone);
            this.bones.put(bone.getUniqueBoneId(), bone);
        }
    }

    @Override
    public void removeBone(String bone) {
        ModelBone removed = this.bones.remove(bone);
        if (removed == null) {
            return;
        }
        removed.setParent(null);
        this.roots.remove(bone);
    }

    @Override
    public boolean canHurt() {
        return this.canHurt;
    }

    @Override
    public boolean wasMarkedHurt() {
        return this.wasMarkedHurt;
    }

    @Override
    public boolean isMarkedHurt() {
        return this.canHurt && this.modeledEntity != null && this.modeledEntity.getHurtTick() > 0;
    }

    @Override
    public boolean isGlowing() {
        return this.glowing == null ? this.modeledEntity.isGlowing() : this.glowing.booleanValue();
    }

    @Override
    public void setGlowing(@Nullable Boolean flag) {
        this.glowing = flag;
        this.getData().markModelGlowing(this, this.glowing != null && this.glowing != false);
    }

    @Override
    public int getGlowColor() {
        return this.glowColor == null ? this.modeledEntity.getGlowColor() : this.glowColor.intValue();
    }

    @Override
    public boolean isOnFire() {
        return this.onFire == null ? this.modeledEntity.getBase().isOnFire() : this.onFire.booleanValue();
    }

    @Override
    public boolean canRenderFire() {
        return this.renderFire == null ? ModelEngineAPI.getConfigCache().isRenderFire() : this.renderFire.booleanValue();
    }

    @Override
    public float getXHeadRot() {
        return this.lockPitch ? 0.0f : this.modeledEntity.getXHeadRot();
    }

    @Override
    public float getYHeadRot() {
        return this.lockYaw ? this.modeledEntity.getYBodyRot() : this.modeledEntity.getYHeadRot();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public <T extends BoneBehavior> Optional<BehaviorManager<T>> getBehaviorManager(BoneBehaviorType<T> type) {
        BoneBehaviorType.BehaviorManagerProvider provider = type.getBehaviorManagerProvider();
        if (provider == null) {
            return Optional.empty();
        }
        Map<BoneBehaviorType<?>, BehaviorManager<?>> map = this.behaviorManagers;
        synchronized (map) {
            return Optional.ofNullable(this.behaviorManagers.computeIfAbsent(type, boneBehaviorType -> {
                BehaviorManager manager = provider.create(this, type);
                if (manager == null) {
                    return null;
                }
                manager.onCreate();
                return manager;
            }));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Optional<BehaviorRenderer> getBehaviorRenderer(BoneBehaviorType<?> type) {
        Map<BoneBehaviorType<?>, BehaviorRenderer> map = this.behaviorRenderers;
        synchronized (map) {
            BehaviorRenderer renderer = this.behaviorRenderers.get(type);
            if (renderer != null) {
                return Optional.of(renderer);
            }
            renderer = type.getRenderType().createBehaviorRenderer(this);
            if (renderer != null) {
                this.behaviorRenderers.put(type, renderer);
            }
            return Optional.ofNullable(renderer);
        }
    }

    private void forBones(Consumer<ModelBone> consumer) {
        for (ModelBone bone : this.roots.values()) {
            consumer.accept(bone);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void forManagers(Consumer<BehaviorManager<?>> consumer) {
        Map<BoneBehaviorType<?>, BehaviorManager<?>> map = this.behaviorManagers;
        synchronized (map) {
            for (BehaviorManager<?> manager : this.behaviorManagers.values()) {
                consumer.accept(manager);
            }
        }
    }

    @Override
    public void save(SavedData data) {
        Map<BoneBehaviorType<?>, BehaviorRenderer> renderers;
        data.putString("blueprint", this.blueprint.getName());
        data.putFloat("render_scale", Float.valueOf(this.getScale().x()));
        data.putFloat("hitbox_scale", Float.valueOf(this.getHitboxScale().x()));
        data.putBoolean("can_hurt", this.canHurt());
        data.putInt("default_tint", this.defaultTint.asRGB());
        data.putInt("damage_tint", this.damageTint.asRGB());
        data.putBoolean("lock_pitch", this.lockPitch);
        data.putBoolean("lock_yaw", this.lockYaw);
        data.putBoolean("hitbox_visible", this.hitboxVisible);
        data.putBoolean("shadow_visible", this.shadowVisible);
        data.putBoolean("main_hitbox", this.mainHitbox);
        data.putBoolean("glowing", this.glowing);
        data.putInt("glow_color", this.glowColor);
        data.putInt("block_light", this.blockLight);
        data.putInt("sky_light", this.skyLight);
        HashSet<String> removed = new HashSet<String>();
        SavedData boneDataMap = new SavedData();
        for (String boneId : this.blueprint.getFlatMap().keySet()) {
            ModelBone bone = this.bones.get(boneId);
            if (bone == null) {
                removed.add(boneId);
                continue;
            }
            bone.save().ifPresent(boneData -> boneDataMap.putData(boneId, (SavedData)boneData));
        }
        data.putList("removed", removed);
        data.putData("default_bones", boneDataMap);
        this.animationHandler.save().ifPresent(animationData -> data.putData("animation_handler", (SavedData)animationData));
        Map<BoneBehaviorType<?>, BehaviorManager<?>> managers = this.getBehaviorManagers();
        if (!managers.isEmpty()) {
            SavedData managerData = new SavedData();
            managers.forEach((boneBehaviorType, manager) -> manager.save().ifPresent(savedData -> managerData.putData(boneBehaviorType.getId(), (SavedData)savedData)));
            data.putData("managers", managerData);
        }
        if (!(renderers = this.getBehaviorRenderers()).isEmpty()) {
            SavedData rendererData = new SavedData();
            renderers.forEach((boneBehaviorType, renderer) -> renderer.save().ifPresent(savedData -> rendererData.putData(boneBehaviorType.getId(), (SavedData)savedData)));
            data.putData("renderers", rendererData);
        }
    }

    @Override
    public void load(SavedData data) {
        this.setScale(data.getFloat("render_scale").floatValue());
        this.setHitboxScale(data.getFloat("hitbox_scale").floatValue());
        this.setCanHurt(data.getBoolean("can_hurt"));
        this.setDefaultTint(Color.fromRGB((int)data.getInt("default_tint")));
        this.setDamageTint(Color.fromRGB((int)data.getInt("damage_tint")));
        this.setLockPitch(data.getBoolean("lock_pitch"));
        this.setLockYaw(data.getBoolean("lock_yaw"));
        this.setHitboxVisible(data.getBoolean("hitbox_visible"));
        this.setShadowVisible(data.getBoolean("shadow_visible"));
        this.setGlowing(data.getBoolean("glowing"));
        this.setGlowColor(data.getInt("glow_color"));
        this.setBlockLight(data.getInt("block_light", -1));
        this.setSkyLight(data.getInt("sky_light", -1));
        for (String remove : data.getList("removed")) {
            this.removeBone(remove);
        }
        data.getData("default_bones").ifPresent(boneDataMap -> {
            for (String key : boneDataMap.keySet()) {
                this.getBone(key).ifPresent(modelBone -> boneDataMap.getData(key).ifPresent(modelBone::load));
            }
        });
        Optional<SavedData> managerData = data.getData("managers");
        managerData.ifPresent(savedData -> this.getBehaviorManagers().forEach((boneBehaviorType, manager) -> savedData.getData(boneBehaviorType.getId()).ifPresent(manager::load)));
        Optional<SavedData> renderersData = data.getData("renderers");
        renderersData.ifPresent(savedData -> this.getBehaviorRenderers().forEach((boneBehaviorType, renderer) -> savedData.getData(boneBehaviorType.getId()).ifPresent(renderer::load)));
    }

    private IEntityData getData() {
        return this.modeledEntity.getBase().getData();
    }

    @Override
    @Generated
    public ModelBlueprint getBlueprint() {
        return this.blueprint;
    }

    @Override
    @Generated
    public MetadataRegistry getMetadata() {
        return this.metadata;
    }

    @Override
    @Generated
    public ModelRenderer getModelRenderer() {
        return this.modelRenderer;
    }

    @Override
    @Generated
    public AnimationHandler getAnimationHandler() {
        return this.animationHandler;
    }

    @Override
    @Generated
    public ModeledEntity getModeledEntity() {
        return this.modeledEntity;
    }

    @Override
    @Generated
    public boolean isMainHitbox() {
        return this.mainHitbox;
    }

    @Generated
    public boolean isGenerated() {
        return this.generated;
    }

    @Override
    @Generated
    public boolean isDestroyed() {
        return this.destroyed;
    }

    @Override
    @Generated
    public boolean isRemoved() {
        return this.removed;
    }

    @Generated
    public boolean isAutoRendererInitialization() {
        return this.autoRendererInitialization;
    }

    @Override
    @Generated
    public boolean isHitboxVisible() {
        return this.hitboxVisible;
    }

    @Override
    @Generated
    public boolean isShadowVisible() {
        return this.shadowVisible;
    }

    @Generated
    public boolean isCanHurt() {
        return this.canHurt;
    }

    @Override
    @Generated
    public Color getDefaultTint() {
        return this.defaultTint;
    }

    @Override
    @Generated
    public Color getDamageTint() {
        return this.damageTint;
    }

    @Generated
    public boolean isWasMarkedHurt() {
        return this.wasMarkedHurt;
    }

    @Override
    @Generated
    public int getBlockLight() {
        return this.blockLight;
    }

    @Override
    @Generated
    public int getSkyLight() {
        return this.skyLight;
    }

    @Override
    @Generated
    public Display.Billboard getBillboard() {
        return this.billboard;
    }

    @Override
    @Generated
    public boolean isLockPitch() {
        return this.lockPitch;
    }

    @Override
    @Generated
    public boolean isLockYaw() {
        return this.lockYaw;
    }

    @Override
    @Generated
    public boolean isInvisUpdate() {
        return this.invisUpdate;
    }

    @Generated
    public BoneSequencer getBoneSequencer() {
        return this.boneSequencer;
    }

    @Override
    @Generated
    public void setModeledEntity(ModeledEntity modeledEntity) {
        this.modeledEntity = modeledEntity;
    }

    @Override
    @Generated
    public void setMainHitbox(boolean mainHitbox) {
        this.mainHitbox = mainHitbox;
    }

    @Override
    @Generated
    public void setRemoved(boolean removed) {
        this.removed = removed;
    }

    @Override
    @Generated
    public void setAutoRendererInitialization(boolean autoRendererInitialization) {
        this.autoRendererInitialization = autoRendererInitialization;
    }

    @Override
    @Generated
    public void setHitboxVisible(boolean hitboxVisible) {
        this.hitboxVisible = hitboxVisible;
    }

    @Override
    @Generated
    public void setShadowVisible(boolean shadowVisible) {
        this.shadowVisible = shadowVisible;
    }

    @Override
    @Generated
    public void setCanHurt(boolean canHurt) {
        this.canHurt = canHurt;
    }

    @Override
    @Generated
    public void setDefaultTint(Color defaultTint) {
        this.defaultTint = defaultTint;
    }

    @Override
    @Generated
    public void setDamageTint(Color damageTint) {
        this.damageTint = damageTint;
    }

    @Override
    @Generated
    public void setGlowColor(Integer glowColor) {
        this.glowColor = glowColor;
    }

    @Override
    @Generated
    public void setBlockLight(int blockLight) {
        this.blockLight = blockLight;
    }

    @Override
    @Generated
    public void setSkyLight(int skyLight) {
        this.skyLight = skyLight;
    }

    @Override
    @Generated
    public void setBillboard(Display.Billboard billboard) {
        this.billboard = billboard;
    }

    @Override
    @Generated
    public void setOnFire(Boolean onFire) {
        this.onFire = onFire;
    }

    @Override
    @Generated
    public void setRenderFire(Boolean renderFire) {
        this.renderFire = renderFire;
    }

    @Override
    @Generated
    public void setLockPitch(boolean lockPitch) {
        this.lockPitch = lockPitch;
    }

    @Override
    @Generated
    public void setLockYaw(boolean lockYaw) {
        this.lockYaw = lockYaw;
    }

    @Override
    @Generated
    public void setInvisUpdate(boolean invisUpdate) {
        this.invisUpdate = invisUpdate;
    }

    @Override
    @Generated
    public Callback<ActiveModel.Scale> getScaleCallback() {
        return this.scaleCallback;
    }
}

