/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  net.minecraft.core.Holder
 *  net.minecraft.network.protocol.Packet
 *  net.minecraft.network.protocol.game.ClientGamePacketListener
 *  net.minecraft.network.protocol.game.ClientboundAddEntityPacket
 *  net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket
 *  net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket
 *  net.minecraft.network.protocol.game.ClientboundSetPassengersPacket
 *  net.minecraft.network.protocol.game.ClientboundSoundEntityPacket
 *  net.minecraft.network.protocol.game.ServerGamePacketListener
 *  net.minecraft.network.protocol.game.ServerboundUseItemPacket
 *  net.minecraft.network.syncher.EntityDataSerializers
 *  net.minecraft.network.syncher.SynchedEntityData$DataValue
 *  net.minecraft.server.level.ChunkMap$TrackedEntity
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.server.network.ServerGamePacketListenerImpl
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.util.Mth
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.InteractionResult$Fail
 *  net.minecraft.world.InteractionResult$Pass
 *  net.minecraft.world.InteractionResult$Success
 *  net.minecraft.world.InteractionResult$TryEmptyHandInteraction
 *  net.minecraft.world.damagesource.DamageSource
 *  net.minecraft.world.entity.AreaEffectCloud
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityAttachments
 *  net.minecraft.world.entity.EntityDimensions
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.Mob
 *  net.minecraft.world.entity.MoverType
 *  net.minecraft.world.entity.Pose
 *  net.minecraft.world.entity.ai.attributes.Attributes
 *  net.minecraft.world.entity.ai.control.MoveControl
 *  net.minecraft.world.entity.ai.goal.Goal
 *  net.minecraft.world.entity.ai.goal.GoalSelector
 *  net.minecraft.world.entity.ai.goal.WrappedGoal
 *  net.minecraft.world.entity.ai.navigation.AmphibiousPathNavigation
 *  net.minecraft.world.entity.ai.navigation.FlyingPathNavigation
 *  net.minecraft.world.entity.ai.navigation.GroundPathNavigation
 *  net.minecraft.world.entity.ai.navigation.PathNavigation
 *  net.minecraft.world.entity.ai.navigation.WallClimberNavigation
 *  net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation
 *  net.minecraft.world.entity.decoration.ArmorStand
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.Instrument
 *  net.minecraft.world.item.InstrumentItem
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.ItemUseAnimation
 *  net.minecraft.world.level.ClipContext
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.HitResult$Type
 *  net.minecraft.world.phys.Vec3
 *  org.bukkit.Color
 *  org.bukkit.Location
 *  org.bukkit.Particle
 *  org.bukkit.Particle$DustOptions
 *  org.bukkit.craftbukkit.CraftWorld
 *  org.bukkit.craftbukkit.entity.CraftPlayer
 *  org.bukkit.craftbukkit.inventory.CraftItemStack
 *  org.bukkit.craftbukkit.util.CraftLocation
 *  org.bukkit.craftbukkit.util.CraftMagicNumbers
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.HumanEntity
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.EquipmentSlot
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.util.BoundingBox
 *  org.bukkit.util.Consumer
 *  org.bukkit.util.Vector
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Vector3f
 */
package com.ticxo.modelengine.v1_21_R6.entity;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.ServerInfo;
import com.ticxo.modelengine.api.entity.BaseEntity;
import com.ticxo.modelengine.api.entity.Hitbox;
import com.ticxo.modelengine.api.entity.data.BukkitEntityData;
import com.ticxo.modelengine.api.entity.data.IEntityData;
import com.ticxo.modelengine.api.interaction.DynamicHitbox;
import com.ticxo.modelengine.api.model.bone.ModelBone;
import com.ticxo.modelengine.api.model.bone.type.SubHitbox;
import com.ticxo.modelengine.api.nms.entity.EntityHandler;
import com.ticxo.modelengine.api.nms.entity.HitboxEntity;
import com.ticxo.modelengine.api.nms.entity.wrapper.BodyRotationController;
import com.ticxo.modelengine.api.nms.entity.wrapper.LookController;
import com.ticxo.modelengine.api.nms.entity.wrapper.MoveController;
import com.ticxo.modelengine.api.nms.entity.wrapper.TrackedEntity;
import com.ticxo.modelengine.api.nms.impl.TempTrackedEntity;
import com.ticxo.modelengine.api.utils.RaceConditionUtil;
import com.ticxo.modelengine.api.utils.ReflectionUtils;
import com.ticxo.modelengine.api.utils.config.ConfigProperty;
import com.ticxo.modelengine.api.utils.config.DebugToggle;
import com.ticxo.modelengine.api.utils.logger.TLogger;
import com.ticxo.modelengine.api.utils.math.TMath;
import com.ticxo.modelengine.api.utils.promise.Promise;
import com.ticxo.modelengine.v1_21_R6.NMSFields;
import com.ticxo.modelengine.v1_21_R6.NMSMethods;
import com.ticxo.modelengine.v1_21_R6.entity.EntityContainer;
import com.ticxo.modelengine.v1_21_R6.entity.EntityUtils;
import com.ticxo.modelengine.v1_21_R6.entity.OcclusionClipContext;
import com.ticxo.modelengine.v1_21_R6.entity.TrackedEntityImpl;
import com.ticxo.modelengine.v1_21_R6.entity.controller.BodyRotationControlWrapper;
import com.ticxo.modelengine.v1_21_R6.entity.controller.LookControlWrapper;
import com.ticxo.modelengine.v1_21_R6.entity.controller.MoveControlWrapper;
import com.ticxo.modelengine.v1_21_R6.entity.hitbox.HitboxEntityImpl;
import com.ticxo.modelengine.v1_21_R6.entity.navigation.AmphibiousNavigationWrapper;
import com.ticxo.modelengine.v1_21_R6.entity.navigation.FlyingNavigationWrapper;
import com.ticxo.modelengine.v1_21_R6.entity.navigation.GroundNavigationWrapper;
import com.ticxo.modelengine.v1_21_R6.entity.navigation.WallClimberNavigationWrapper;
import com.ticxo.modelengine.v1_21_R6.entity.navigation.WaterBoundNavigationWrapper;
import com.ticxo.modelengine.v1_21_R6.network.patch.PatchedServerGamePacketListener;
import com.ticxo.modelengine.v1_21_R6.network.utils.NetworkUtils;
import com.ticxo.modelengine.v1_21_R6.network.utils.Packets;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import java.lang.reflect.Field;
import java.lang.runtime.SwitchBootstraps;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.minecraft.core.Holder;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket;
import net.minecraft.network.protocol.game.ClientboundSoundEntityPacket;
import net.minecraft.network.protocol.game.ServerGamePacketListener;
import net.minecraft.network.protocol.game.ServerboundUseItemPacket;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.EntityAttachments;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.ai.navigation.AmphibiousPathNavigation;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WallClimberNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Instrument;
import net.minecraft.world.item.InstrumentItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.craftbukkit.util.CraftLocation;
import org.bukkit.craftbukkit.util.CraftMagicNumbers;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Consumer;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public class EntityHandlerImpl
implements EntityHandler {
    private static boolean usePaperClipMethod;
    private final Set<UUID> forceInvisible = new HashSet<UUID>();
    private double forceRenderWidth;
    private double forceRenderHeight;
    private ArmorStand dummyArmorStand;
    private AreaEffectCloud dummyCloud;

    public EntityHandlerImpl() {
        ModelEngineAPI.getAPI().getConfigManager().registerReferenceUpdate(this::updateConfig);
    }

    @Override
    public void updateConfig() {
        this.forceRenderWidth = ConfigProperty.BLOCK_CULL_IGNORE_SIZE_WIDTH.getDouble();
        this.forceRenderHeight = ConfigProperty.BLOCK_CULL_IGNORE_SIZE_HEIGHT.getDouble();
        usePaperClipMethod = ConfigProperty.BLOCK_CULL_USE_PAPER_CLIP.getBoolean();
    }

    @Override
    public int getNextEntityId() {
        return CraftMagicNumbers.INSTANCE.nextEntityId();
    }

    @Override
    public void setHitbox(Entity entity, @NotNull Hitbox hitbox) {
        float width = (float)hitbox.getMaxWidth();
        float height = (float)hitbox.getHeight();
        net.minecraft.world.entity.Entity nms = EntityUtils.nms(entity);
        EntityDimensions box = new EntityDimensions(width, height, (float)hitbox.getEyeHeight(), EntityAttachments.createDefault((float)width, (float)height), true);
        ReflectionUtils.set(nms, NMSFields.ENTITY_dimensions, box);
        ReflectionUtils.set(nms, NMSFields.ENTITY_eyeHeight, Float.valueOf((float)hitbox.getEyeHeight()));
        nms.setBoundingBox(box.makeBoundingBox(nms.position()));
    }

    @Override
    public void setStepHeight(Entity entity, double height) {
        net.minecraft.world.entity.Entity nms = EntityUtils.nms(entity);
        if (nms instanceof LivingEntity) {
            LivingEntity living = (LivingEntity)nms;
            living.getAttributes().registerAttribute(Attributes.STEP_HEIGHT);
            Objects.requireNonNull(living.getAttribute(Attributes.STEP_HEIGHT)).setBaseValue(height);
        }
    }

    @Override
    public double getStepHeight(Entity entity) {
        return EntityUtils.nms(entity).maxUpStep();
    }

    @Override
    public void setPosition(Entity entity, double x, double y, double z) {
        EntityUtils.nms(entity).setPos(x, y, z);
    }

    @Override
    public void movePassenger(Entity entity, double x, double y, double z) {
        net.minecraft.world.entity.Entity nmsEntity = EntityUtils.nms(entity);
        if (this.dummyArmorStand == null) {
            this.dummyArmorStand = new ArmorStand(EntityType.ARMOR_STAND, nmsEntity.level());
            this.dummyArmorStand.setMarker(true);
        }
        double seatY = y - nmsEntity.getVehicleAttachmentPoint((net.minecraft.world.entity.Entity)this.dummyArmorStand).y;
        nmsEntity.setPos(x, seatY, z);
        nmsEntity.setDeltaMovement(Vec3.ZERO);
        nmsEntity.resetFallDistance();
        if (nmsEntity instanceof ServerPlayer) {
            ServerPlayer player = (ServerPlayer)nmsEntity;
            ReflectionUtils.set(player.connection, NMSFields.SERVER_GAME_PACKET_LISTENER_IMPL_clientIsFloating, false);
        }
    }

    @Override
    public void forceSpawn(BaseEntity<?> entity, org.bukkit.entity.Player player) {
        if (player == null) {
            return;
        }
        IEntityData data = entity.getData();
        if (!(data instanceof BukkitEntityData)) {
            return;
        }
        BukkitEntityData bukkitEntityData = (BukkitEntityData)data;
        bukkitEntityData.getTracked().sendPairingData(player);
    }

    @Override
    public void forceDespawn(BaseEntity<?> entity, org.bukkit.entity.Player player) {
        if (player == null) {
            return;
        }
        NetworkUtils.send(player.getUniqueId(), (Packet<? super ClientGamePacketListener>)new ClientboundRemoveEntitiesPacket(new int[]{entity.getEntityId()}));
    }

    @Override
    public void setForcedInvisible(Entity entity, boolean flag) {
        if (this.isForcedInvisible(entity) == flag) {
            return;
        }
        net.minecraft.world.entity.Entity nms = EntityUtils.nms(entity);
        byte data = 0;
        for (int i = 0; i < 8; ++i) {
            data = TMath.setBit(data, i, nms.getSharedFlag(i));
        }
        if (flag) {
            this.forceInvisible.add(entity.getUniqueId());
            data = TMath.setBit(data, 5, true);
        } else {
            this.forceInvisible.remove(entity.getUniqueId());
        }
        ClientboundSetEntityDataPacket packet = new ClientboundSetEntityDataPacket(entity.getEntityId(), List.of(new SynchedEntityData.DataValue(0, EntityDataSerializers.BYTE, (Object)data)));
        NetworkUtils.send(ServerInfo.getOnlinePlayers(), (Packet<? super ClientGamePacketListener>)packet);
    }

    @Override
    public boolean isForcedInvisible(UUID uuid) {
        return this.forceInvisible.contains(uuid);
    }

    @Override
    public BodyRotationController wrapBodyRotationControl(Entity entity, Supplier<BodyRotationController> def) {
        net.minecraft.world.entity.Entity entity2 = EntityUtils.nms(entity);
        if (!(entity2 instanceof Mob)) {
            return def.get();
        }
        Mob mob = (Mob)entity2;
        BodyRotationControlWrapper controller = new BodyRotationControlWrapper(mob);
        if (ReflectionUtils.set(mob, NMSFields.MOB_bodyRotationControl, controller)) {
            return controller;
        }
        return def.get();
    }

    @Override
    public MoveController wrapMoveController(Entity entity, Supplier<MoveController> def) {
        MoveController controller;
        net.minecraft.world.entity.Entity entity2 = EntityUtils.nms(entity);
        if (!(entity2 instanceof Mob)) {
            return def.get();
        }
        Mob mob = (Mob)entity2;
        MoveControl moveControl = mob.getMoveControl();
        if (moveControl instanceof MoveController) {
            controller = (MoveController)moveControl;
            return controller;
        }
        controller = new MoveControlWrapper(mob, mob.getMoveControl());
        if (ReflectionUtils.set(mob, NMSFields.MOB_moveControl, controller)) {
            return controller;
        }
        return def.get();
    }

    @Override
    public LookController wrapLookController(Entity entity, Supplier<LookController> def) {
        net.minecraft.world.entity.Entity entity2 = EntityUtils.nms(entity);
        if (!(entity2 instanceof Mob)) {
            return def.get();
        }
        Mob mob = (Mob)entity2;
        LookControlWrapper controller = new LookControlWrapper(mob, mob.getLookControl());
        if (ReflectionUtils.set(mob, NMSFields.MOB_lookControl, controller)) {
            return controller;
        }
        return def.get();
    }

    @Override
    public void wrapNavigation(Entity entity) {
        net.minecraft.world.entity.Entity entity2 = EntityUtils.nms(entity);
        if (!(entity2 instanceof Mob)) {
            return;
        }
        Mob mob = (Mob)entity2;
        try {
            Object newNav;
            PathNavigation oldNav;
            Field navField = ReflectionUtils.getField(NMSFields.MOB_navigation);
            PathNavigation pathNavigation = oldNav = mob.getNavigation();
            Objects.requireNonNull(pathNavigation);
            PathNavigation pathNavigation2 = pathNavigation;
            int n = 0;
            switch (SwitchBootstraps.typeSwitch("typeSwitch", new Object[]{WallClimberNavigation.class, GroundPathNavigation.class, FlyingPathNavigation.class, WaterBoundPathNavigation.class, AmphibiousPathNavigation.class}, (Object)pathNavigation2, n)) {
                case 0: {
                    WallClimberNavigation wallClimberNavigation = (WallClimberNavigation)pathNavigation2;
                    newNav = new WallClimberNavigationWrapper(mob, wallClimberNavigation);
                    break;
                }
                case 1: {
                    GroundPathNavigation groundPathNavigation = (GroundPathNavigation)pathNavigation2;
                    newNav = new GroundNavigationWrapper(mob, groundPathNavigation);
                    break;
                }
                case 2: {
                    FlyingPathNavigation flyingPathNavigation = (FlyingPathNavigation)pathNavigation2;
                    newNav = new FlyingNavigationWrapper(mob, flyingPathNavigation);
                    break;
                }
                case 3: {
                    WaterBoundPathNavigation waterBoundPathNavigation = (WaterBoundPathNavigation)pathNavigation2;
                    newNav = new WaterBoundNavigationWrapper(mob);
                    break;
                }
                case 4: {
                    AmphibiousPathNavigation amphibiousPathNavigation = (AmphibiousPathNavigation)pathNavigation2;
                    newNav = new AmphibiousNavigationWrapper(mob, amphibiousPathNavigation);
                    break;
                }
                default: {
                    TLogger.warn("Failed to create custom navigation for " + String.valueOf(mob.getType()) + ": " + String.valueOf(mob.getUUID()));
                    TLogger.warn("Reason: Navigation class type is " + oldNav.getClass().getSimpleName() + ".");
                    return;
                }
            }
            navField.set(mob, newNav);
            GoalSelector goalSelector = (GoalSelector)ReflectionUtils.getField(NMSFields.MOB_goalSelector).get(mob);
            RaceConditionUtil.wrapConmod(() -> this.lambda$wrapNavigation$0(goalSelector, (PathNavigation)newNav));
        }
        catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void replaceNavigation(GoalSelector goalSelector, PathNavigation newNav) {
        try {
            for (WrappedGoal wrappedGoal : goalSelector.getAvailableGoals()) {
                Goal goal = wrappedGoal.getGoal();
                for (Field field : goal.getClass().getDeclaredFields()) {
                    field = ReflectionUtils.getField(goal.getClass(), field.getName());
                    Object f = field.get(goal);
                    if (!(f instanceof PathNavigation)) continue;
                    field.set(goal, newNav);
                }
            }
        }
        catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public HitboxEntity createHitbox(Location location, ModelBone bone, SubHitbox subHitbox) {
        ServerLevel level = ((CraftWorld)location.getWorld()).getHandle();
        HitboxEntityImpl entity = new HitboxEntityImpl((Level)level, bone, subHitbox);
        entity.queueLocation(new Vector3f().set(location.getX(), location.getY(), location.getZ()));
        entity.setPos(location.getX(), location.getY(), location.getZ());
        ModelEngineAPI.setRenderCanceled(entity.getId(), true);
        Promise.start((Entity)entity.getBukkitEntity()).thenRunSync(() -> {
            level.addFreshEntity((net.minecraft.world.entity.Entity)entity);
            ModelEngineAPI.getInteractionTracker().addHitbox(entity);
        });
        return entity;
    }

    @Override
    @Nullable
    public HitboxEntity castHitbox(Entity entity) {
        HitboxEntity hitbox;
        net.minecraft.world.entity.Entity entity2 = EntityUtils.nms(entity);
        return entity2 instanceof HitboxEntity ? (hitbox = (HitboxEntity)entity2) : null;
    }

    @Override
    public boolean hurt(Entity entity, Object source, float amount) {
        if (source instanceof DamageSource) {
            DamageSource damageSource = (DamageSource)source;
            net.minecraft.world.entity.Entity nmsEntity = EntityUtils.nms(entity);
            Level world = nmsEntity.level();
            if (world instanceof ServerLevel) {
                ServerLevel worldserver = (ServerLevel)world;
                return nmsEntity.hurtServer(worldserver, damageSource, amount);
            }
        }
        throw new RuntimeException("Passed in source is not an NMS DamageSource.");
    }

    @Override
    public EntityHandler.InteractionResult interact(Entity entity, HumanEntity player, EquipmentSlot hand) {
        InteractionResult result;
        net.minecraft.world.entity.Entity entity2 = EntityUtils.nms(entity);
        if (!(entity2 instanceof LivingEntity)) {
            return EntityHandler.InteractionResult.FAIL;
        }
        LivingEntity livingEntity = (LivingEntity)entity2;
        InteractionResult interactionResult = result = livingEntity.interact((Player)((CraftPlayer)player).getHandle(), hand == EquipmentSlot.HAND ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND);
        Objects.requireNonNull(interactionResult);
        InteractionResult interactionResult2 = interactionResult;
        int n = 0;
        return switch (SwitchBootstraps.typeSwitch("typeSwitch", new Object[]{InteractionResult.Success.class, InteractionResult.TryEmptyHandInteraction.class, InteractionResult.Pass.class, InteractionResult.Fail.class}, (Object)interactionResult2, n)) {
            default -> throw new MatchException(null, null);
            case 0 -> {
                InteractionResult.Success success = (InteractionResult.Success)interactionResult2;
                if (success.equals((Object)InteractionResult.SUCCESS_SERVER)) {
                    yield EntityHandler.InteractionResult.SUCCESS;
                }
                if (success.equals((Object)InteractionResult.SUCCESS)) {
                    yield EntityHandler.InteractionResult.CONSUME_PARTIAL;
                }
                yield EntityHandler.InteractionResult.CONSUME;
            }
            case 1 -> {
                InteractionResult.TryEmptyHandInteraction ignored = (InteractionResult.TryEmptyHandInteraction)interactionResult2;
                yield EntityHandler.InteractionResult.SUCCESS_NO_ITEM_USED;
            }
            case 2 -> {
                InteractionResult.Pass ignored = (InteractionResult.Pass)interactionResult2;
                yield EntityHandler.InteractionResult.PASS;
            }
            case 3 -> {
                InteractionResult.Fail ignored = (InteractionResult.Fail)interactionResult2;
                yield EntityHandler.InteractionResult.FAIL;
            }
        };
    }

    @Override
    public void spawnDynamicHitbox(DynamicHitbox hitbox) {
        Vector location = hitbox.getPositionTracker().get();
        final Packets.PacketSupplier pivotSpawn = NetworkUtils.createPivotSpawn(DynamicHitbox.getPivotId(), DynamicHitbox.getPivotUUID(), location.toVector3f().add(0.0f, -0.5202f, 0.0f));
        final ClientboundSetEntityDataPacket pivotData = new ClientboundSetEntityDataPacket(DynamicHitbox.getPivotId(), EntityUtils.DEFAULT_AREA_EFFECT_CLOUD_DATA);
        final ClientboundAddEntityPacket hitboxSpawn = new ClientboundAddEntityPacket(DynamicHitbox.getHitboxId(), DynamicHitbox.getHitboxUUID(), location.getX(), location.getY() - 0.5202, location.getZ(), 0.0f, 0.0f, EntityType.SLIME, 0, Vec3.ZERO, 0.0);
        final ClientboundSetEntityDataPacket hitboxData = new ClientboundSetEntityDataPacket(DynamicHitbox.getHitboxId(), EntityUtils.DEFAULT_SLIME_DATA);
        final ClientboundSetPassengersPacket mount = new ClientboundSetPassengersPacket(EntityContainer.of(DynamicHitbox.getPivotId(), DynamicHitbox.getHitboxId()));
        NetworkUtils.sendBundled(Set.of(hitbox.getPlayer().getUniqueId()), new Packets(){
            {
                this.add(pivotSpawn);
                this.add((Packet<ClientGamePacketListener>)pivotData);
                this.add((Packet<ClientGamePacketListener>)hitboxSpawn);
                this.add((Packet<ClientGamePacketListener>)hitboxData);
                this.add((Packet<ClientGamePacketListener>)mount);
            }
        });
    }

    @Override
    public void updateDynamicHitbox(DynamicHitbox hitbox) {
        Vector3f vector = hitbox.getPositionTracker().get().toVector3f().add(0.0f, -0.5202f, 0.0f);
        NetworkUtils.send(hitbox.getPlayer().getUniqueId(), NetworkUtils.createPivotTeleport(DynamicHitbox.getPivotId(), vector).supply(hitbox.getPlayer().getUniqueId()));
    }

    @Override
    public void destroyDynamicHitbox(DynamicHitbox hitbox) {
        ClientboundRemoveEntitiesPacket destroy = new ClientboundRemoveEntitiesPacket(new int[]{DynamicHitbox.getHitboxId(), DynamicHitbox.getPivotId()});
        NetworkUtils.send(hitbox.getPlayer().getUniqueId(), (Packet<? super ClientGamePacketListener>)destroy);
    }

    @Override
    public void forceUseItem(org.bukkit.entity.Player player, EquipmentSlot hand) {
        ItemStack stack = player.getEquipment().getItem(hand);
        net.minecraft.world.item.ItemStack nmsStack = ((CraftItemStack)stack).handle;
        ServerPlayer nmsPlayer = (ServerPlayer)EntityUtils.nms((Entity)player);
        ServerGamePacketListenerImpl connection = nmsPlayer.connection;
        ServerboundUseItemPacket useItemPacket = new ServerboundUseItemPacket(hand == EquipmentSlot.HAND ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND, 0, 0.0f, 0.0f);
        useItemPacket.timestamp = System.currentTimeMillis();
        PatchedServerGamePacketListener.handleUseItem(useItemPacket, (ServerGamePacketListener)connection, (Consumer<InteractionResult>)((Consumer)interactionResult -> {
            if (nmsStack.getUseAnimation() == ItemUseAnimation.NONE || interactionResult != InteractionResult.CONSUME) {
                return;
            }
            ClientboundSetEntityDataPacket packet = new ClientboundSetEntityDataPacket(player.getEntityId(), List.of(new SynchedEntityData.DataValue(8, EntityDataSerializers.BYTE, (Object)((byte)(hand == EquipmentSlot.HAND ? 1 : 3)))));
            NetworkUtils.send(player.getUniqueId(), (Packet<? super ClientGamePacketListener>)packet);
            Item patt0$temp = nmsStack.getItem();
            if (patt0$temp instanceof InstrumentItem) {
                InstrumentItem instrumentItem = (InstrumentItem)patt0$temp;
                Optional optional = (Optional)ReflectionUtils.call(instrumentItem, NMSMethods.INSTRUMENT_ITEM_getInstrument, nmsStack, nmsPlayer.registryAccess());
                optional.ifPresent(instrumentHolder -> {
                    Instrument instrument = (Instrument)instrumentHolder.value();
                    Holder soundEvent = instrument.soundEvent();
                    float f = instrument.range() / 16.0f;
                    RandomSource random = (RandomSource)ReflectionUtils.get(nmsPlayer.level(), NMSFields.LEVEL_threadSafeRandom);
                    ClientboundSoundEntityPacket soundPacket = new ClientboundSoundEntityPacket(soundEvent, SoundSource.RECORDS, (net.minecraft.world.entity.Entity)nmsPlayer, f, 1.0f, random.nextLong());
                    NetworkUtils.send(player.getUniqueId(), (Packet<? super ClientGamePacketListener>)soundPacket);
                });
            }
        }));
    }

    @Override
    public float getYRot(Entity entity) {
        return EntityUtils.nms(entity).getYRot();
    }

    @Override
    public float getYHeadRot(Entity entity) {
        net.minecraft.world.entity.Entity nms = EntityUtils.nms(entity);
        if (nms instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity)nms;
            return livingEntity.getYHeadRot();
        }
        return nms.getYRot();
    }

    @Override
    public float getXHeadRot(Entity entity) {
        return EntityUtils.nms(entity).getXRot();
    }

    @Override
    public float getYBodyRot(Entity entity) {
        net.minecraft.world.entity.Entity nms = EntityUtils.nms(entity);
        if (nms instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity)nms;
            return livingEntity.yBodyRot;
        }
        return nms.getYRot();
    }

    @Override
    public void setYRot(Entity entity, float angle) {
        EntityUtils.nms(entity).setYRot(angle);
    }

    @Override
    public void setYHeadRot(Entity entity, float angle) {
        net.minecraft.world.entity.Entity nms = EntityUtils.nms(entity);
        if (nms instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity)nms;
            livingEntity.setYHeadRot(angle);
        } else {
            nms.setYRot(angle);
        }
    }

    @Override
    public void setXHeadRot(Entity entity, float angle) {
        EntityUtils.nms(entity).setXRot(angle);
    }

    @Override
    public void setYBodyRot(Entity entity, float angle) {
        net.minecraft.world.entity.Entity nms = EntityUtils.nms(entity);
        if (nms instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity)nms;
            livingEntity.setYBodyRot(angle);
        } else {
            nms.setYRot(angle);
        }
    }

    @Override
    public void move(Entity entity, double x, double y, double z) {
        net.minecraft.world.entity.Entity nms = EntityUtils.nms(entity);
        nms.move(MoverType.SELF, new Vec3(x, y, z));
    }

    @Override
    public boolean isWalking(Entity entity) {
        double dZ;
        net.minecraft.world.entity.Entity nms = EntityUtils.nms(entity);
        if (nms.tickCount < 1) {
            return false;
        }
        double dX = nms.getX() - nms.xOld;
        return dX * dX + (dZ = nms.getZ() - nms.zOld) * dZ > 2.500000277905201E-7;
    }

    @Override
    public boolean isStrafing(Entity entity) {
        return false;
    }

    @Override
    public boolean isJumping(Entity entity) {
        net.minecraft.world.entity.Entity entity2 = EntityUtils.nms(entity);
        if (entity2 instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity)entity2;
            Boolean flag = ReflectionUtils.get(livingEntity, NMSFields.LIVING_ENTITY_jumping, false);
            return flag != null && flag != false;
        }
        return false;
    }

    @Override
    public boolean isFlying(Entity entity) {
        return false;
    }

    @Override
    public float getHealth(Entity entity) {
        net.minecraft.world.entity.Entity entity2 = EntityUtils.nms(entity);
        if (entity2 instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity)entity2;
            return livingEntity.getHealth();
        }
        return 20.0f;
    }

    @Override
    public float getMaxHealth(Entity entity) {
        net.minecraft.world.entity.Entity entity2 = EntityUtils.nms(entity);
        if (entity2 instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity)entity2;
            return livingEntity.getMaxHealth();
        }
        return 20.0f;
    }

    @Override
    public Vector3f getPivotOffset(Entity entity) {
        net.minecraft.world.entity.Entity nmsEntity = EntityUtils.nms(entity);
        if (this.dummyCloud == null) {
            this.dummyCloud = new AreaEffectCloud(EntityType.AREA_EFFECT_CLOUD, nmsEntity.level());
        }
        Vec3 offset = (Vec3)ReflectionUtils.call(nmsEntity, NMSMethods.ENTITY_getPassengerAttachmentPoint, this.dummyCloud, nmsEntity.getDimensions(Pose.STANDING), Float.valueOf(1.0f));
        Vec3 offset2 = this.dummyCloud.getPassengerRidingPosition((net.minecraft.world.entity.Entity)this.dummyCloud);
        return offset == null ? new Vector3f() : offset.add(offset2).toVector3f();
    }

    @Override
    public boolean isRemoved(Entity entity) {
        return EntityUtils.nms(entity).isRemoved();
    }

    @Override
    public int getGlowColor(Entity entity) {
        net.minecraft.world.entity.Entity nms = EntityUtils.nms(entity);
        return nms.getTeamColor();
    }

    @Override
    public void setDeathTick(Entity entity, int tick) {
        net.minecraft.world.entity.Entity nms = EntityUtils.nms(entity);
        if (nms instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity)nms;
            livingEntity.deathTime = tick;
        }
    }

    @Override
    public TrackedEntity wrapTrackedEntity(Entity entity) {
        ServerLevel level = ((CraftWorld)entity.getWorld()).getHandle();
        Int2ObjectMap map = level.getChunkSource().chunkMap.entityMap;
        ChunkMap.TrackedEntity tracker = (ChunkMap.TrackedEntity)map.get(entity.getEntityId());
        if (tracker == null) {
            return new TempTrackedEntity(entity);
        }
        return new TrackedEntityImpl(entity, () -> EntityHandlerImpl.lambda$wrapTrackedEntity$4((Map)map, entity), tracker);
    }

    @Override
    public boolean shouldCull(org.bukkit.entity.Player player, Location eyePosition, BoundingBox box) {
        CraftWorld world = (CraftWorld)player.getWorld();
        Vec3 start = CraftLocation.toVec3((Location)eyePosition);
        if (box.getWidthX() >= this.forceRenderWidth || box.getWidthZ() >= this.forceRenderWidth || box.getHeight() >= this.forceRenderHeight) {
            return false;
        }
        int minX = Mth.floor((double)box.getMinX());
        int minY = Mth.floor((double)box.getMinY());
        int minZ = Mth.floor((double)box.getMinZ());
        int maxX = Mth.ceil((double)box.getMaxX()) - 1;
        int maxY = Mth.ceil((double)box.getMaxY()) - 1;
        int maxZ = Mth.ceil((double)box.getMaxZ()) - 1;
        EntityHandler.BoxRelToCam relX = EntityHandler.BoxRelToCam.from(minX, maxX, Mth.floor((double)start.x));
        EntityHandler.BoxRelToCam relY = EntityHandler.BoxRelToCam.from(minY, maxY, Mth.floor((double)start.y));
        EntityHandler.BoxRelToCam relZ = EntityHandler.BoxRelToCam.from(minZ, maxZ, Mth.floor((double)start.z));
        if (relX == EntityHandler.BoxRelToCam.INSIDE && relY == EntityHandler.BoxRelToCam.INSIDE && relZ == EntityHandler.BoxRelToCam.INSIDE) {
            return false;
        }
        LinkedHashSet<Vec3> points = new LinkedHashSet<Vec3>();
        for (int x = minX; x <= maxX; ++x) {
            byte xVisibleFace = 0;
            xVisibleFace = (byte)(xVisibleFace | (x == minX && relX == EntityHandler.BoxRelToCam.POSITIVE ? 1 : 0));
            xVisibleFace = (byte)(xVisibleFace | (x == maxX && relX == EntityHandler.BoxRelToCam.NEGATIVE ? 2 : 0));
            for (int y = minY; y <= maxY; ++y) {
                byte yVisibleFace = xVisibleFace;
                yVisibleFace = (byte)(yVisibleFace | (y == minY && relY == EntityHandler.BoxRelToCam.POSITIVE ? 4 : 0));
                yVisibleFace = (byte)(yVisibleFace | (y == maxY && relY == EntityHandler.BoxRelToCam.NEGATIVE ? 8 : 0));
                for (int z = minZ; z <= maxZ; ++z) {
                    byte visibleFace = yVisibleFace;
                    visibleFace = (byte)(visibleFace | (z == minZ && relZ == EntityHandler.BoxRelToCam.POSITIVE ? 16 : 0));
                    if ((visibleFace = (byte)(visibleFace | (z == maxZ && relZ == EntityHandler.BoxRelToCam.NEGATIVE ? 32 : 0))) == 0) continue;
                    for (EntityHandler.Point point : EntityHandler.getPoints(visibleFace)) {
                        points.add(new Vec3((double)((float)x + point.x), (double)((float)y + point.y), (double)((float)z + point.z)));
                    }
                }
            }
        }
        if (DebugToggle.isDebugging(DebugToggle.SHOW_CULL_POINTS)) {
            for (Vec3 point : points) {
                world.spawnParticle(Particle.DUST, point.x, point.y, point.z, 1, (Object)new Particle.DustOptions(Color.RED, 0.2f));
            }
        }
        for (Vec3 point : points) {
            if (!EntityHandlerImpl.isVisible(world, start, point)) continue;
            return false;
        }
        return true;
    }

    private static boolean isVisible(CraftWorld world, Vec3 startPos, Vec3 endPos) {
        BlockHitResult nmsHitResult = usePaperClipMethod ? world.getHandle().clip((ClipContext)new OcclusionClipContext(startPos, endPos)) : world.getHandle().clip((ClipContext)new OcclusionClipContext(startPos, endPos), (Predicate)null);
        return nmsHitResult.getType() == HitResult.Type.MISS;
    }

    private static /* synthetic */ ChunkMap.TrackedEntity lambda$wrapTrackedEntity$4(Map map, Entity entity) {
        return (ChunkMap.TrackedEntity)map.get(entity.getEntityId());
    }

    private /* synthetic */ void lambda$wrapNavigation$0(GoalSelector goalSelector, PathNavigation newNav) {
        this.replaceNavigation(goalSelector, newNav);
    }
}

