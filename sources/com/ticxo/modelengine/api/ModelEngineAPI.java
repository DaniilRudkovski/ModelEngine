/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Sets
 *  com.google.gson.Gson
 *  lombok.Generated
 *  org.bukkit.Bukkit
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Event
 *  org.bukkit.plugin.java.JavaPlugin
 */
package com.ticxo.modelengine.api;

import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.ticxo.modelengine.api.animation.AnimationHandlerRegistry;
import com.ticxo.modelengine.api.animation.AnimationPropertyRegistry;
import com.ticxo.modelengine.api.animation.handler.AnimationHandler;
import com.ticxo.modelengine.api.animation.keyframe.KeyframeTypeRegistry;
import com.ticxo.modelengine.api.animation.keyframe.data.KeyframeReaderRegistry;
import com.ticxo.modelengine.api.animation.script.ScriptReaderRegistry;
import com.ticxo.modelengine.api.entity.BaseEntity;
import com.ticxo.modelengine.api.entity.BukkitEntity;
import com.ticxo.modelengine.api.entity.BukkitPlayer;
import com.ticxo.modelengine.api.entity.EntityDataTrackers;
import com.ticxo.modelengine.api.generator.ModelGenerator;
import com.ticxo.modelengine.api.generator.blueprint.ModelBlueprint;
import com.ticxo.modelengine.api.generator.skin.SkinGeneratorService;
import com.ticxo.modelengine.api.generator.skin.SkinSplitter;
import com.ticxo.modelengine.api.interaction.InteractionTracker;
import com.ticxo.modelengine.api.menu.ScreenManager;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.ModelRegistry;
import com.ticxo.modelengine.api.model.ModelUpdaters;
import com.ticxo.modelengine.api.model.ModeledEntity;
import com.ticxo.modelengine.api.model.PivotOverrideRegistry;
import com.ticxo.modelengine.api.model.bone.behavior.BoneBehaviorRegistry;
import com.ticxo.modelengine.api.model.limb.UserLimbRegistry;
import com.ticxo.modelengine.api.model.render.ModelRenderer;
import com.ticxo.modelengine.api.mount.MountControllerTypeRegistry;
import com.ticxo.modelengine.api.mount.MountPairManager;
import com.ticxo.modelengine.api.nms.NMSHandler;
import com.ticxo.modelengine.api.nms.entity.EntityHandler;
import com.ticxo.modelengine.api.nms.network.CancelType;
import com.ticxo.modelengine.api.nms.network.NetworkHandler;
import com.ticxo.modelengine.api.nms.ui.AnvilHandler;
import com.ticxo.modelengine.api.utils.CompatibilityManager;
import com.ticxo.modelengine.api.utils.config.ConfigCache;
import com.ticxo.modelengine.api.utils.config.ConfigManager;
import com.ticxo.modelengine.api.utils.scheduling.PlatformScheduler;
import com.ticxo.modelengine.api.utils.ticker.DualTicker;
import com.ticxo.modelengine.api.vfx.VFX;
import com.ticxo.modelengine.api.vfx.VFXUpdater;
import com.ticxo.modelengine.api.vfx.render.VFXRenderer;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import lombok.Generated;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class ModelEngineAPI
extends JavaPlugin {
    protected static ModelEngineAPI API;
    protected final Set<Integer> renderCanceled = Sets.newConcurrentHashSet();
    protected ConfigManager configManager;
    protected ConfigCache configCache;
    protected Gson gson;
    protected PlatformScheduler scheduler;
    protected ModelRegistry modelRegistry;
    protected ModelGenerator modelGenerator;
    protected KeyframeTypeRegistry keyframeTypeRegistry;
    protected KeyframeReaderRegistry keyframeReaderRegistry;
    protected ScriptReaderRegistry scriptReaderRegistry;
    protected BoneBehaviorRegistry boneBehaviorRegistry;
    protected AnimationHandlerRegistry animationHandlerRegistry;
    protected AnimationPropertyRegistry animationPropertyRegistry;
    protected DualTicker ticker;
    protected ModelUpdaters modelUpdaters;
    protected VFXUpdater vfxUpdater;
    protected EntityDataTrackers dataTrackers;
    protected InteractionTracker interactionTracker;
    protected MountPairManager mountPairManager;
    protected MountControllerTypeRegistry mountControllerTypeRegistry;
    protected NMSHandler nmsHandler;
    protected CompatibilityManager compatibilityManager;
    protected ScreenManager screenManager;
    protected SkinSplitter skinSplitter;
    protected SkinGeneratorService skinGenerator;
    protected UserLimbRegistry userLimbRegistry;
    protected PivotOverrideRegistry pivotOverrideRegistry;

    public static ConfigCache getConfigCache() {
        return ModelEngineAPI.getAPI().configCache;
    }

    public static NMSHandler getNMSHandler() {
        return ModelEngineAPI.getAPI().nmsHandler;
    }

    public static EntityHandler getEntityHandler() {
        return ModelEngineAPI.getNMSHandler().getEntityHandler();
    }

    public static AnvilHandler getAnvilHandler() {
        return ModelEngineAPI.getNMSHandler().getAnvilHandler();
    }

    public static NetworkHandler getNetworkHandler() {
        return ModelEngineAPI.getNMSHandler().getNetworkHandler();
    }

    public static InteractionTracker getInteractionTracker() {
        return ModelEngineAPI.getAPI().interactionTracker;
    }

    public static MountPairManager getMountPairManager() {
        return ModelEngineAPI.getAPI().mountPairManager;
    }

    public static MountControllerTypeRegistry getMountControllerTypeRegistry() {
        return ModelEngineAPI.getAPI().mountControllerTypeRegistry;
    }

    public static AnimationHandlerRegistry getAnimationHandlerRegistry() {
        return ModelEngineAPI.getAPI().animationHandlerRegistry;
    }

    public static AnimationPropertyRegistry getAnimationPropertyRegistry() {
        return ModelEngineAPI.getAPI().animationPropertyRegistry;
    }

    public static UserLimbRegistry getUserLimbRegistry() {
        return ModelEngineAPI.getAPI().userLimbRegistry;
    }

    public static SkinGeneratorService getSkinGeneratorService() {
        return ModelEngineAPI.getAPI().skinGenerator;
    }

    public static PivotOverrideRegistry getPivotOverrideRegistry() {
        return ModelEngineAPI.getAPI().pivotOverrideRegistry;
    }

    public VFXUpdater getVFXUpdater() {
        return this.vfxUpdater;
    }

    public static ModeledEntity createModeledEntity(Entity base) {
        return ModelEngineAPI.createModeledEntity(base, null);
    }

    public static ModeledEntity createModeledEntity(Entity base, Consumer<ModeledEntity> consumer) {
        BukkitEntity bukkitEntity;
        if (base instanceof Player) {
            Player player = (Player)base;
            bukkitEntity = new BukkitPlayer(player);
        } else {
            bukkitEntity = new BukkitEntity(base);
        }
        return ModelEngineAPI.createModeledEntity(bukkitEntity, consumer);
    }

    public static ModeledEntity createModeledEntity(BaseEntity<?> base) {
        return ModelEngineAPI.createModeledEntity(base, null);
    }

    public static ModeledEntity createModeledEntity(BaseEntity<?> base, Consumer<ModeledEntity> consumer) {
        return ModelEngineAPI.getAPI().createModeledEntityImpl(base, consumer);
    }

    public static ModeledEntity getModeledEntity(Entity entity) {
        return ModelEngineAPI.getModeledEntity(entity.getUniqueId());
    }

    public static ModeledEntity getModeledEntity(int id) {
        return ModelEngineAPI.getAPI().getModelUpdaters().getModeledEntity(id);
    }

    public static ModeledEntity getModeledEntity(UUID uuid) {
        return ModelEngineAPI.getAPI().getModelUpdaters().getModeledEntity(uuid);
    }

    public static ModeledEntity getOrCreateModeledEntity(Entity base) {
        ModeledEntity model = ModelEngineAPI.getModeledEntity(base);
        return model != null ? model : ModelEngineAPI.createModeledEntity(base, null);
    }

    public static ModeledEntity getOrCreateModeledEntity(Entity base, Consumer<ModeledEntity> consumer) {
        ModeledEntity modeledEntity;
        ModeledEntity model = ModelEngineAPI.getModeledEntity(base);
        if (model != null) {
            modeledEntity = model;
        } else {
            BukkitEntity bukkitEntity;
            if (base instanceof Player) {
                Player player = (Player)base;
                bukkitEntity = new BukkitPlayer(player);
            } else {
                bukkitEntity = new BukkitEntity(base);
            }
            modeledEntity = ModelEngineAPI.createModeledEntity(bukkitEntity, consumer);
        }
        return modeledEntity;
    }

    public static ModeledEntity getOrCreateModeledEntity(UUID uuid, Supplier<BaseEntity<?>> baseEntitySupplier) {
        ModeledEntity model = ModelEngineAPI.getModeledEntity(uuid);
        return model != null ? model : ModelEngineAPI.createModeledEntity(baseEntitySupplier.get(), null);
    }

    public static ModeledEntity getOrCreateModeledEntity(UUID uuid, Supplier<BaseEntity<?>> baseEntitySupplier, Consumer<ModeledEntity> consumer) {
        ModeledEntity model = ModelEngineAPI.getModeledEntity(uuid);
        return model != null ? model : ModelEngineAPI.getAPI().createModeledEntityImpl(baseEntitySupplier.get(), consumer);
    }

    public static ModeledEntity removeModeledEntity(Entity entity) {
        return ModelEngineAPI.removeModeledEntity(entity.getUniqueId());
    }

    public static ModeledEntity removeModeledEntity(int id) {
        return ModelEngineAPI.getAPI().getModelUpdaters().removeModeledEntity(id);
    }

    public static ModeledEntity removeModeledEntity(UUID uuid) {
        return ModelEngineAPI.getAPI().getModelUpdaters().removeModeledEntity(uuid);
    }

    public static boolean isModeledEntity(UUID uuid) {
        return ModelEngineAPI.getModeledEntity(uuid) != null;
    }

    public static ActiveModel createActiveModel(String modelId) {
        return ModelEngineAPI.createActiveModel(modelId, null, null);
    }

    public static ActiveModel createActiveModel(String modelId, Function<ActiveModel, ModelRenderer> rendererSupplier, Function<ActiveModel, AnimationHandler> handlerSupplier) {
        ModelBlueprint blueprint = (ModelBlueprint)ModelEngineAPI.getAPI().getModelRegistry().get(modelId);
        if (blueprint == null) {
            throw new RuntimeException("Error while creating ActiveModel. Unknown model: " + modelId);
        }
        return ModelEngineAPI.createActiveModel(blueprint, rendererSupplier, handlerSupplier);
    }

    public static ActiveModel createActiveModel(ModelBlueprint blueprint) {
        return ModelEngineAPI.createActiveModel(blueprint, null, null);
    }

    public static ActiveModel createActiveModel(ModelBlueprint blueprint, Function<ActiveModel, ModelRenderer> rendererSupplier, Function<ActiveModel, AnimationHandler> handlerSupplier) {
        return ModelEngineAPI.getAPI().createActiveModelImpl(blueprint, rendererSupplier, handlerSupplier);
    }

    public static AnimationHandler createPriorityHandler(ActiveModel activeModel) {
        return ModelEngineAPI.getAPI().getPriorityHandler(activeModel);
    }

    public static AnimationHandler createStateMachineHandler(ActiveModel activeModel) {
        return ModelEngineAPI.getAPI().getStateMachineHandler(activeModel);
    }

    public static VFX createVFX(Entity base) {
        return ModelEngineAPI.createVFX(base, null, null);
    }

    public static VFX createVFX(Entity base, Consumer<VFX> consumer) {
        return ModelEngineAPI.createVFX(base, consumer, null);
    }

    public static VFX createVFX(Entity base, Consumer<VFX> consumer, Function<VFX, VFXRenderer> rendererSupplier) {
        BukkitEntity bukkitEntity;
        if (base instanceof Player) {
            Player player = (Player)base;
            bukkitEntity = new BukkitPlayer(player);
        } else {
            bukkitEntity = new BukkitEntity(base);
        }
        return ModelEngineAPI.createVFX(bukkitEntity, consumer, rendererSupplier);
    }

    public static VFX createVFX(BaseEntity<?> base) {
        return ModelEngineAPI.createVFX(base, null, null);
    }

    public static VFX createVFX(BaseEntity<?> base, Consumer<VFX> consumer) {
        return ModelEngineAPI.createVFX(base, consumer, null);
    }

    public static VFX createVFX(BaseEntity<?> base, Consumer<VFX> consumer, Function<VFX, VFXRenderer> rendererSupplier) {
        return ModelEngineAPI.getAPI().createVFXImpl(base, rendererSupplier, consumer);
    }

    public static VFX getVFX(Entity entity) {
        return ModelEngineAPI.getVFX(entity.getUniqueId());
    }

    public static VFX getVFX(int id) {
        return ModelEngineAPI.getAPI().getVFXUpdater().getVFX(id);
    }

    public static VFX getVFX(UUID uuid) {
        return ModelEngineAPI.getAPI().getVFXUpdater().getVFX(uuid);
    }

    public static boolean isVFX(UUID uuid) {
        return ModelEngineAPI.getVFX(uuid) != null;
    }

    public static ModelBlueprint getBlueprint(String id) {
        return (ModelBlueprint)ModelEngineAPI.getAPI().getModelRegistry().get(id);
    }

    public static void setRenderCanceled(int id, boolean flag) {
        if (flag) {
            ModelEngineAPI.getAPI().renderCanceled.add(id);
        } else {
            ModelEngineAPI.getAPI().renderCanceled.remove(id);
        }
    }

    public static boolean isRenderCanceled(int id) {
        return ModelEngineAPI.getAPI().renderCanceled.contains(id);
    }

    public static CancelType shouldShow(Player player, int id) {
        if (player.getEntityId() == id) {
            return CancelType.SHOW;
        }
        ModeledEntity entity = ModelEngineAPI.getAPI().modelUpdaters.getModeledEntity(id);
        if (entity == null || entity.isBaseEntityVisible()) {
            return ModelEngineAPI.isRenderCanceled(id) ? CancelType.HIDE : CancelType.SHOW;
        }
        return entity.isPivotOverride(id) ? CancelType.INVIS : CancelType.HIDE;
    }

    public static void callEvent(Event event) {
        Bukkit.getPluginManager().callEvent(event);
    }

    public static int getPlayerProtocolVersion(UUID uuid) {
        return ModelEngineAPI.getAPI().playerProtocolVersion(uuid);
    }

    public abstract ModeledEntity createModeledEntityImpl(BaseEntity<?> var1, Consumer<ModeledEntity> var2);

    public abstract ActiveModel createActiveModelImpl(ModelBlueprint var1, Function<ActiveModel, ModelRenderer> var2, Function<ActiveModel, AnimationHandler> var3);

    public abstract VFX createVFXImpl(BaseEntity<?> var1, Function<VFX, VFXRenderer> var2, Consumer<VFX> var3);

    public abstract UUID getDisguiseRelayOrDefault(UUID var1);

    public abstract AnimationHandler getPriorityHandler(ActiveModel var1);

    public abstract AnimationHandler getStateMachineHandler(ActiveModel var1);

    public abstract int playerProtocolVersion(UUID var1);

    @Generated
    public Set<Integer> getRenderCanceled() {
        return this.renderCanceled;
    }

    @Generated
    public ConfigManager getConfigManager() {
        return this.configManager;
    }

    @Generated
    public Gson getGson() {
        return this.gson;
    }

    @Generated
    public PlatformScheduler getScheduler() {
        return this.scheduler;
    }

    @Generated
    public ModelRegistry getModelRegistry() {
        return this.modelRegistry;
    }

    @Generated
    public ModelGenerator getModelGenerator() {
        return this.modelGenerator;
    }

    @Generated
    public KeyframeTypeRegistry getKeyframeTypeRegistry() {
        return this.keyframeTypeRegistry;
    }

    @Generated
    public KeyframeReaderRegistry getKeyframeReaderRegistry() {
        return this.keyframeReaderRegistry;
    }

    @Generated
    public ScriptReaderRegistry getScriptReaderRegistry() {
        return this.scriptReaderRegistry;
    }

    @Generated
    public BoneBehaviorRegistry getBoneBehaviorRegistry() {
        return this.boneBehaviorRegistry;
    }

    @Generated
    public DualTicker getTicker() {
        return this.ticker;
    }

    @Generated
    public ModelUpdaters getModelUpdaters() {
        return this.modelUpdaters;
    }

    @Generated
    public EntityDataTrackers getDataTrackers() {
        return this.dataTrackers;
    }

    @Generated
    public CompatibilityManager getCompatibilityManager() {
        return this.compatibilityManager;
    }

    @Generated
    public ScreenManager getScreenManager() {
        return this.screenManager;
    }

    @Generated
    public SkinSplitter getSkinSplitter() {
        return this.skinSplitter;
    }

    @Generated
    public SkinGeneratorService getSkinGenerator() {
        return this.skinGenerator;
    }

    @Generated
    public static ModelEngineAPI getAPI() {
        return API;
    }
}

