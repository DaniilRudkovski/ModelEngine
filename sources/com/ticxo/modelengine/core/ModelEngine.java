/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.GsonBuilder
 *  com.viaversion.viaversion.api.Via
 *  net.citizensnpcs.api.CitizensAPI
 *  net.citizensnpcs.api.trait.TraitInfo
 *  org.bukkit.Bukkit
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.command.PluginCommand
 *  org.bukkit.entity.ItemDisplay$ItemDisplayTransform
 *  org.bukkit.event.Listener
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.PluginManager
 *  org.joml.Vector3f
 */
package com.ticxo.modelengine.core;

import com.google.gson.GsonBuilder;
import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.ServerInfo;
import com.ticxo.modelengine.api.animation.AnimationHandlerRegistry;
import com.ticxo.modelengine.api.animation.AnimationPropertyRegistry;
import com.ticxo.modelengine.api.animation.ModelState;
import com.ticxo.modelengine.api.animation.handler.AnimationHandler;
import com.ticxo.modelengine.api.animation.keyframe.KeyframeTypeRegistry;
import com.ticxo.modelengine.api.animation.keyframe.KeyframeTypes;
import com.ticxo.modelengine.api.animation.keyframe.data.KeyframeReaderRegistry;
import com.ticxo.modelengine.api.animation.property.SimpleProperty;
import com.ticxo.modelengine.api.animation.script.ScriptReaderRegistry;
import com.ticxo.modelengine.api.command.AbstractCommand;
import com.ticxo.modelengine.api.entity.BaseEntity;
import com.ticxo.modelengine.api.entity.EntityDataTrackers;
import com.ticxo.modelengine.api.entity.Hitbox;
import com.ticxo.modelengine.api.entity.data.AbstractEntityData;
import com.ticxo.modelengine.api.generator.blueprint.ModelBlueprint;
import com.ticxo.modelengine.api.interaction.InteractionTracker;
import com.ticxo.modelengine.api.lod.AnimationLODHandler;
import com.ticxo.modelengine.api.menu.ScreenManager;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.ModelRegistry;
import com.ticxo.modelengine.api.model.ModelUpdaters;
import com.ticxo.modelengine.api.model.ModeledEntity;
import com.ticxo.modelengine.api.model.PivotOverrideRegistry;
import com.ticxo.modelengine.api.model.bone.BoneBehaviorTypes;
import com.ticxo.modelengine.api.model.bone.behavior.BoneBehaviorRegistry;
import com.ticxo.modelengine.api.model.bone.behavior.BoneBehaviorType;
import com.ticxo.modelengine.api.model.bone.render.DefaultRenderType;
import com.ticxo.modelengine.api.model.bone.type.PlayerLimb;
import com.ticxo.modelengine.api.model.limb.UserLimbRegistry;
import com.ticxo.modelengine.api.model.render.ModelRenderer;
import com.ticxo.modelengine.api.mount.MountControllerTypeRegistry;
import com.ticxo.modelengine.api.mount.MountPairManager;
import com.ticxo.modelengine.api.mount.controller.MountControllerTypes;
import com.ticxo.modelengine.api.nms.NMSHandler;
import com.ticxo.modelengine.api.utils.CompatibilityManager;
import com.ticxo.modelengine.api.utils.TFile;
import com.ticxo.modelengine.api.utils.config.ConfigCache;
import com.ticxo.modelengine.api.utils.config.ConfigManager;
import com.ticxo.modelengine.api.utils.config.ConfigProperty;
import com.ticxo.modelengine.api.utils.config.Property;
import com.ticxo.modelengine.api.utils.logger.TLogger;
import com.ticxo.modelengine.api.utils.scheduling.BukkitPlatformScheduler;
import com.ticxo.modelengine.api.utils.scheduling.FoliaPlatformScheduler;
import com.ticxo.modelengine.api.utils.ticker.DualTicker;
import com.ticxo.modelengine.api.vfx.VFX;
import com.ticxo.modelengine.api.vfx.VFXUpdater;
import com.ticxo.modelengine.api.vfx.render.VFXRenderer;
import com.ticxo.modelengine.core.animation.handler.PriorityHandler;
import com.ticxo.modelengine.core.animation.handler.StateMachineHandler;
import com.ticxo.modelengine.core.animation.script.ModelEngineScriptReader;
import com.ticxo.modelengine.core.citizens.CitizensCommand;
import com.ticxo.modelengine.core.citizens.ModelTrait;
import com.ticxo.modelengine.core.command.MECommand;
import com.ticxo.modelengine.core.generator.ModelGeneratorImpl;
import com.ticxo.modelengine.core.generator.skin.MineSkinGeneratorService;
import com.ticxo.modelengine.core.generator.skin.SkinSplitterImpl;
import com.ticxo.modelengine.core.java21.Java21Access;
import com.ticxo.modelengine.core.libsdisguises.DisguiseCompatibility;
import com.ticxo.modelengine.core.listener.ChatListener;
import com.ticxo.modelengine.core.listener.EntityListener;
import com.ticxo.modelengine.core.listener.InventoryListener;
import com.ticxo.modelengine.core.listener.PlayerListener;
import com.ticxo.modelengine.core.listener.WorldListener;
import com.ticxo.modelengine.core.model.ActiveModelImpl;
import com.ticxo.modelengine.core.model.ModeledEntityImpl;
import com.ticxo.modelengine.core.model.bone.behavior.GhostImpl;
import com.ticxo.modelengine.core.model.bone.behavior.HeadForcedImpl;
import com.ticxo.modelengine.core.model.bone.behavior.HeadImpl;
import com.ticxo.modelengine.core.model.bone.behavior.HeldItemImpl;
import com.ticxo.modelengine.core.model.bone.behavior.LeashImpl;
import com.ticxo.modelengine.core.model.bone.behavior.MountImpl;
import com.ticxo.modelengine.core.model.bone.behavior.NameTagImpl;
import com.ticxo.modelengine.core.model.bone.behavior.PlayerLimbImpl;
import com.ticxo.modelengine.core.model.bone.behavior.SegmentImpl;
import com.ticxo.modelengine.core.model.bone.behavior.SubHitboxImpl;
import com.ticxo.modelengine.core.model.bone.behavior.TailImpl;
import com.ticxo.modelengine.core.model.bone.behavior.UserLimbImpl;
import com.ticxo.modelengine.core.model.bone.manager.LeashManagerImpl;
import com.ticxo.modelengine.core.model.bone.manager.MountManagerImpl;
import com.ticxo.modelengine.core.model.bone.render.HeldItemRendererImpl;
import com.ticxo.modelengine.core.model.bone.render.LeashRendererImpl;
import com.ticxo.modelengine.core.model.bone.render.MountRendererImpl;
import com.ticxo.modelengine.core.model.bone.render.NameTagRendererImpl;
import com.ticxo.modelengine.core.model.bone.render.SegmentRendererImpl;
import com.ticxo.modelengine.core.model.bone.render.SubHitboxRendererImpl;
import com.ticxo.modelengine.core.vfx.VFXImpl;
import com.ticxo.modelengine.v1_19_R3.NMSHandler_v1_19_R3;
import com.ticxo.modelengine.v1_20_R1.NMSHandler_v1_20_R1;
import com.ticxo.modelengine.v1_20_R2.NMSHandler_v1_20_R2;
import com.ticxo.modelengine.v1_20_R3.NMSHandler_v1_20_R3;
import com.viaversion.viaversion.api.Via;
import java.io.File;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.trait.TraitInfo;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.joml.Vector3f;

public class ModelEngine
extends ModelEngineAPI {
    public static ModelEngine CORE;
    private AbstractCommand commandRoot;
    private PluginManager pluginManager;
    private DisguiseCompatibility disguiseCompatibility;

    public void onLoad() {
        ModelEngineAPI.API = this;
        CORE = this;
        TLogger.logger = this.getLogger();
        this.pluginManager = Bukkit.getPluginManager();
    }

    public void onEnable() {
        this.configureConfig();
        this.configureNMSHandler();
        this.gson = new GsonBuilder().create();
        this.scheduler = ServerInfo.IS_FOLIA ? new FoliaPlatformScheduler() : new BukkitPlatformScheduler();
        this.modelRegistry = new ModelRegistry();
        this.modelGenerator = new ModelGeneratorImpl(this);
        this.configureKeyframeTypeRegistry();
        this.keyframeReaderRegistry = new KeyframeReaderRegistry();
        this.configureScriptReaderRegistry();
        this.configureBoneBehaviorRegistry();
        this.configureTicker();
        this.configureMountControllerTypeRegistry();
        this.configureAnimationHandlerRegistry();
        this.configureAnimationPropertyRegistry();
        this.configureCommands();
        this.configureCompatibility();
        this.configureSkinSplitter();
        this.configureSkinGenerator();
        this.configureUserLimbRegistry();
        this.pivotOverrideRegistry = new PivotOverrideRegistry();
        this.pluginManager.registerEvents((Listener)new PlayerListener(), (Plugin)this);
        this.pluginManager.registerEvents((Listener)new EntityListener(), (Plugin)this);
        this.pluginManager.registerEvents((Listener)new WorldListener(), (Plugin)this);
        this.pluginManager.registerEvents((Listener)new InventoryListener(), (Plugin)this);
        this.pluginManager.registerEvents((Listener)new ChatListener(), (Plugin)this);
        if (Bukkit.getPluginManager().getPlugin("MythicMobs") == null) {
            this.modelGenerator.importModels(true);
        }
    }

    public void onDisable() {
        if (this.ticker != null) {
            this.ticker.stop();
        }
        if (this.modelUpdaters != null) {
            this.modelUpdaters.saveAllModels();
        }
    }

    private void configureConfig() {
        this.configManager = new ConfigManager(this);
        this.configCache = new ConfigCache();
        for (ConfigProperty configProperty : ConfigProperty.values()) {
            this.configManager.register(configProperty);
        }
        for (Enum enum_ : ModelState.values()) {
            this.configManager.register((Property)((Object)enum_));
        }
        this.configManager.save();
        this.configManager.registerReferenceUpdate(AbstractEntityData::updateConfig);
        this.configManager.registerReferenceUpdate(AnimationLODHandler::updateConfig);
        this.configManager.registerReferenceUpdate(this.configCache::updateConfig);
    }

    private void configureKeyframeTypeRegistry() {
        this.keyframeTypeRegistry = new KeyframeTypeRegistry();
        this.keyframeTypeRegistry.registerKeyframeType(KeyframeTypes.POSITION);
        this.keyframeTypeRegistry.registerKeyframeType(KeyframeTypes.ROTATION);
        this.keyframeTypeRegistry.registerKeyframeType(KeyframeTypes.SCALE);
        this.keyframeTypeRegistry.registerKeyframeType(KeyframeTypes.SCRIPT);
    }

    private void configureScriptReaderRegistry() {
        this.scriptReaderRegistry = new ScriptReaderRegistry();
        this.scriptReaderRegistry.registerAndDefault("meg", new ModelEngineScriptReader());
    }

    private void configureBoneBehaviorRegistry() {
        this.boneBehaviorRegistry = new BoneBehaviorRegistry();
        BoneBehaviorTypes.HEAD = BoneBehaviorType.Builder.of(HeadImpl::new, null, "head").optional("local", Boolean.class).optional("inherited", Boolean.class).predicate(BoneBehaviorType::noProcedural).forced(HeadForcedImpl::new).build();
        BoneBehaviorTypes.GHOST = BoneBehaviorType.Builder.of(GhostImpl::new, null, "ghost").renderType(DefaultRenderType.NONE).build();
        BoneBehaviorTypes.MOUNT = BoneBehaviorType.Builder.of(MountImpl::new, MountManagerImpl::new, "mount").required("driver", Boolean.class).renderType(MountRendererImpl::new).build();
        BoneBehaviorTypes.SUB_HITBOX = BoneBehaviorType.Builder.of(SubHitboxImpl::new, null, "sub_hitbox").required("dimension", Hitbox.class).optional("obb", Boolean.class).optional("origin", Vector3f.class).optional("rotation", Vector3f.class).optional("cube_origin", Vector3f.class).renderType(SubHitboxRendererImpl::new).ignoreCubes().build();
        BoneBehaviorTypes.NAMETAG = BoneBehaviorType.Builder.of(NameTagImpl::new, null, "nametag").renderType(NameTagRendererImpl::new).build();
        BoneBehaviorTypes.ITEM = BoneBehaviorType.Builder.of(HeldItemImpl::new, null, "item").required("display", ItemDisplay.ItemDisplayTransform.class).renderType(HeldItemRendererImpl::new).build();
        BoneBehaviorTypes.SEGMENT = BoneBehaviorType.Builder.of(SegmentImpl::new, null, "segment").required("back_pivot", Boolean.class).optional("bounded", Boolean.class).optional("min_x", Float.class).optional("max_x", Float.class).optional("min_y", Float.class).optional("max_y", Float.class).optional("min_z", Float.class).optional("max_z", Float.class).optional("extend_rate", Float.class).optional("elasticity", Float.class).renderType(SegmentRendererImpl::new).pivot().build();
        BoneBehaviorTypes.TAIL = BoneBehaviorType.Builder.of(TailImpl::new, null, "tail").required("back_pivot", Boolean.class).optional("bounded", Boolean.class).optional("min_x", Float.class).optional("max_x", Float.class).optional("min_y", Float.class).optional("max_y", Float.class).optional("min_z", Float.class).optional("max_z", Float.class).optional("extend_rate", Float.class).optional("elasticity", Float.class).build();
        BoneBehaviorTypes.LEASH = BoneBehaviorType.Builder.of(LeashImpl::new, LeashManagerImpl::new, "leash").optional("main", Boolean.class).renderType(LeashRendererImpl::new).build();
        BoneBehaviorTypes.PLAYER_LIMB = BoneBehaviorType.Builder.of(PlayerLimbImpl::new, null, "player_limb").required("limb", PlayerLimb.Limb.class).renderType(DefaultRenderType.NONE).ignoreCubes().build();
        BoneBehaviorTypes.USER_LIMB = BoneBehaviorType.Builder.of(UserLimbImpl::new, null, "user_limb").required("type", String.class).renderType(DefaultRenderType.NONE).ignoreCubes().build();
        this.boneBehaviorRegistry.register(BoneBehaviorTypes.HEAD);
        this.boneBehaviorRegistry.register(BoneBehaviorTypes.GHOST);
        this.boneBehaviorRegistry.register(BoneBehaviorTypes.MOUNT);
        this.boneBehaviorRegistry.register(BoneBehaviorTypes.SUB_HITBOX);
        this.boneBehaviorRegistry.register(BoneBehaviorTypes.NAMETAG);
        this.boneBehaviorRegistry.register(BoneBehaviorTypes.ITEM);
        this.boneBehaviorRegistry.register(BoneBehaviorTypes.SEGMENT);
        this.boneBehaviorRegistry.register(BoneBehaviorTypes.TAIL);
        this.boneBehaviorRegistry.register(BoneBehaviorTypes.LEASH);
        this.boneBehaviorRegistry.register(BoneBehaviorTypes.PLAYER_LIMB);
        this.boneBehaviorRegistry.register(BoneBehaviorTypes.USER_LIMB);
    }

    private void configureAnimationHandlerRegistry() {
        this.animationHandlerRegistry = new AnimationHandlerRegistry();
        this.animationHandlerRegistry.register("priority", PriorityHandler::create);
        this.animationHandlerRegistry.register("state_machine", StateMachineHandler::create);
    }

    private void configureAnimationPropertyRegistry() {
        this.animationPropertyRegistry = new AnimationPropertyRegistry();
        this.animationPropertyRegistry.register("simple", SimpleProperty::create);
    }

    private void configureTicker() {
        this.ticker = new DualTicker(this, this.getScheduler());
        this.mountPairManager = new MountPairManager();
        this.dataTrackers = new EntityDataTrackers(this, this.getScheduler());
        this.modelUpdaters = new ModelUpdaters();
        this.vfxUpdater = new VFXUpdater();
        this.interactionTracker = new InteractionTracker();
        this.screenManager = new ScreenManager();
        DualTicker.queueRepeatingSyncTask(this.mountPairManager::updatePassengerPosition, 0, 0);
        DualTicker.queueRepeatingAsyncTask(this.vfxUpdater::updateAllVFXs, 0, 0);
        DualTicker.queueRepeatingAsyncTask(this.interactionTracker::raytraceHitboxes, 0, 0);
        DualTicker.queueRepeatingSyncTask(this.screenManager::updateAllScreens, 0, 0);
        this.ticker.start();
    }

    private void configureMountControllerTypeRegistry() {
        this.mountControllerTypeRegistry = new MountControllerTypeRegistry();
        this.mountControllerTypeRegistry.registerAndDefault("walking", MountControllerTypes.WALKING);
        this.mountControllerTypeRegistry.register("force_walking", MountControllerTypes.WALKING_FORCE);
        this.mountControllerTypeRegistry.register("flying", MountControllerTypes.FLYING);
        this.mountControllerTypeRegistry.register("force_flying", MountControllerTypes.FLYING_FORCE);
    }

    private void configureNMSHandler() {
        switch (ServerInfo.NMS_VERSION) {
            case "1.19.4": {
                this.nmsHandler = new NMSHandler_v1_19_R3();
                break;
            }
            case "1.20": 
            case "1.20.1": {
                this.nmsHandler = new NMSHandler_v1_20_R1();
                break;
            }
            case "1.20.2": 
            case "1.20.3": {
                this.nmsHandler = new NMSHandler_v1_20_R2();
                break;
            }
            case "1.20.4": {
                this.nmsHandler = new NMSHandler_v1_20_R3();
                break;
            }
            default: {
                this.nmsHandler = (NMSHandler)Java21Access.createNMSHandler.call(ServerInfo.NMS_VERSION);
            }
        }
    }

    private void configureCommands() {
        PluginCommand command = this.getCommand("meg");
        if (command != null) {
            this.commandRoot = new MECommand(this);
            command.setExecutor((CommandExecutor)this.commandRoot);
        }
    }

    private void configureCompatibility() {
        this.compatibilityManager = new CompatibilityManager(this);
        this.compatibilityManager.registerSupport("Citizens", this::configureCitizensSupport);
        this.compatibilityManager.registerSupport("LibsDisguises", this::configureLibsDisguises);
        this.compatibilityManager.registerSupport("ViaVersion", plugin -> {
            ServerInfo.HAS_VIAVERSION = true;
            return true;
        });
        Java21Access.registerCompatibility.call(new Object[]{this.compatibilityManager, this});
    }

    private void configureSkinSplitter() {
        File internalSkinFolder = TFile.createDirectory(this.getDataFolder(), "internals", "skins");
        SkinSplitterImpl splitter = new SkinSplitterImpl(TFile.createFile(internalSkinFolder, "steve_mapped.bbmodel"), TFile.createFile(internalSkinFolder, "alex_mapped.bbmodel"));
        splitter.copyMapped(this);
        splitter.reloadMapping();
        this.skinSplitter = splitter;
    }

    private void configureSkinGenerator() {
        this.skinGenerator = new MineSkinGeneratorService();
        this.configManager.registerReferenceUpdate(this.skinGenerator::onReload);
    }

    private void configureUserLimbRegistry() {
        this.userLimbRegistry = new UserLimbRegistry(this);
        this.userLimbRegistry.generateDefaults();
    }

    private boolean configureCitizensSupport(Plugin plugin) {
        ServerInfo.HAS_CITIZENS = true;
        CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(ModelTrait.class));
        this.commandRoot.addSubCommands(new CitizensCommand(this.commandRoot));
        return true;
    }

    private boolean configureLibsDisguises(Plugin plugin) {
        this.disguiseCompatibility = new DisguiseCompatibility();
        this.pluginManager.registerEvents((Listener)this.disguiseCompatibility, (Plugin)this);
        return true;
    }

    @Override
    public ModeledEntity createModeledEntityImpl(BaseEntity<?> base, Consumer<ModeledEntity> consumer) {
        return new ModeledEntityImpl(base, consumer);
    }

    @Override
    public ActiveModel createActiveModelImpl(ModelBlueprint blueprint, Function<ActiveModel, ModelRenderer> rendererSupplier, Function<ActiveModel, AnimationHandler> handlerSupplier) {
        return new ActiveModelImpl(blueprint, rendererSupplier, handlerSupplier);
    }

    @Override
    public VFX createVFXImpl(BaseEntity<?> base, Function<VFX, VFXRenderer> rendererSupplier, Consumer<VFX> consumer) {
        return new VFXImpl(base, rendererSupplier, consumer);
    }

    @Override
    public UUID getDisguiseRelayOrDefault(UUID uuid) {
        return this.disguiseCompatibility == null ? uuid : this.disguiseCompatibility.getRelayOrDefault(uuid);
    }

    @Override
    public AnimationHandler getPriorityHandler(ActiveModel activeModel) {
        return new PriorityHandler(activeModel);
    }

    @Override
    public AnimationHandler getStateMachineHandler(ActiveModel activeModel) {
        return new StateMachineHandler(activeModel);
    }

    @Override
    public int playerProtocolVersion(UUID uuid) {
        return ServerInfo.HAS_VIAVERSION ? Via.getAPI().getPlayerVersion(uuid) : ModelEngine.getNetworkHandler().getProtocolVersion();
    }
}

