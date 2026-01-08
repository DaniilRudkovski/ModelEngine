/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  net.minecraft.core.Holder
 *  net.minecraft.network.PacketDataSerializer
 *  net.minecraft.network.protocol.Packet
 *  net.minecraft.network.protocol.game.PacketListenerPlayIn
 *  net.minecraft.network.protocol.game.PacketListenerPlayOut
 *  net.minecraft.network.protocol.game.PacketPlayInBlockPlace
 *  net.minecraft.network.protocol.game.PacketPlayOutEntityDestroy
 *  net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata
 *  net.minecraft.network.protocol.game.PacketPlayOutEntitySound
 *  net.minecraft.network.protocol.game.PacketPlayOutMount
 *  net.minecraft.network.protocol.game.PacketPlayOutSpawnEntity
 *  net.minecraft.network.syncher.DataWatcherRegistry
 *  net.minecraft.server.level.EntityPlayer
 *  net.minecraft.server.level.PlayerChunkMap$EntityTracker
 *  net.minecraft.server.level.WorldServer
 *  net.minecraft.server.network.PlayerConnection
 *  net.minecraft.sounds.SoundCategory
 *  net.minecraft.util.MathHelper
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.EnumHand
 *  net.minecraft.world.EnumInteractionResult
 *  net.minecraft.world.damagesource.DamageSource
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityAreaEffectCloud
 *  net.minecraft.world.entity.EntityInsentient
 *  net.minecraft.world.entity.EntityLiving
 *  net.minecraft.world.entity.EntityPose
 *  net.minecraft.world.entity.EntitySize
 *  net.minecraft.world.entity.EntityTypes
 *  net.minecraft.world.entity.EnumMoveType
 *  net.minecraft.world.entity.ai.control.ControllerMove
 *  net.minecraft.world.entity.ai.goal.PathfinderGoal
 *  net.minecraft.world.entity.ai.goal.PathfinderGoalSelector
 *  net.minecraft.world.entity.ai.goal.PathfinderGoalWrapped
 *  net.minecraft.world.entity.ai.navigation.AmphibiousPathNavigation
 *  net.minecraft.world.entity.ai.navigation.Navigation
 *  net.minecraft.world.entity.ai.navigation.NavigationAbstract
 *  net.minecraft.world.entity.ai.navigation.NavigationFlying
 *  net.minecraft.world.entity.ai.navigation.NavigationGuardian
 *  net.minecraft.world.entity.ai.navigation.NavigationSpider
 *  net.minecraft.world.entity.decoration.EntityArmorStand
 *  net.minecraft.world.entity.player.EntityHuman
 *  net.minecraft.world.item.EnumAnimation
 *  net.minecraft.world.item.Instrument
 *  net.minecraft.world.item.InstrumentItem
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.RayTrace
 *  net.minecraft.world.level.World
 *  net.minecraft.world.phys.MovingObjectPosition$EnumMovingObjectType
 *  net.minecraft.world.phys.MovingObjectPositionBlock
 *  net.minecraft.world.phys.Vec3D
 *  org.bukkit.Color
 *  org.bukkit.Location
 *  org.bukkit.Particle
 *  org.bukkit.Particle$DustOptions
 *  org.bukkit.craftbukkit.v1_20_R2.CraftWorld
 *  org.bukkit.craftbukkit.v1_20_R2.entity.CraftPlayer
 *  org.bukkit.craftbukkit.v1_20_R2.inventory.CraftItemStack
 *  org.bukkit.craftbukkit.v1_20_R2.util.CraftLocation
 *  org.bukkit.craftbukkit.v1_20_R2.util.CraftMagicNumbers
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
package com.ticxo.modelengine.v1_20_R2.entity;

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
import com.ticxo.modelengine.v1_20_R2.NMSFields;
import com.ticxo.modelengine.v1_20_R2.NMSMethods;
import com.ticxo.modelengine.v1_20_R2.entity.EntityUtils;
import com.ticxo.modelengine.v1_20_R2.entity.OcclusionClipContext;
import com.ticxo.modelengine.v1_20_R2.entity.TrackedEntityImpl;
import com.ticxo.modelengine.v1_20_R2.entity.controller.BodyRotationControlWrapper;
import com.ticxo.modelengine.v1_20_R2.entity.controller.LookControlWrapper;
import com.ticxo.modelengine.v1_20_R2.entity.controller.MoveControlWrapper;
import com.ticxo.modelengine.v1_20_R2.entity.hitbox.HitboxEntityImpl;
import com.ticxo.modelengine.v1_20_R2.entity.navigation.AmphibiousNavigationWrapper;
import com.ticxo.modelengine.v1_20_R2.entity.navigation.FlyingNavigationWrapper;
import com.ticxo.modelengine.v1_20_R2.entity.navigation.GroundNavigationWrapper;
import com.ticxo.modelengine.v1_20_R2.entity.navigation.WallClimberNavigationWrapper;
import com.ticxo.modelengine.v1_20_R2.entity.navigation.WaterBoundNavigationWrapper;
import com.ticxo.modelengine.v1_20_R2.network.patch.PatchedServerGamePacketListener;
import com.ticxo.modelengine.v1_20_R2.network.utils.NetworkUtils;
import com.ticxo.modelengine.v1_20_R2.network.utils.Packets;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.minecraft.core.Holder;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.PacketListenerPlayIn;
import net.minecraft.network.protocol.game.PacketListenerPlayOut;
import net.minecraft.network.protocol.game.PacketPlayInBlockPlace;
import net.minecraft.network.protocol.game.PacketPlayOutEntityDestroy;
import net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata;
import net.minecraft.network.protocol.game.PacketPlayOutEntitySound;
import net.minecraft.network.protocol.game.PacketPlayOutMount;
import net.minecraft.network.protocol.game.PacketPlayOutSpawnEntity;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.PlayerChunkMap;
import net.minecraft.server.level.WorldServer;
import net.minecraft.server.network.PlayerConnection;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.util.MathHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.EnumHand;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityAreaEffectCloud;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityPose;
import net.minecraft.world.entity.EntitySize;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumMoveType;
import net.minecraft.world.entity.ai.control.ControllerMove;
import net.minecraft.world.entity.ai.goal.PathfinderGoal;
import net.minecraft.world.entity.ai.goal.PathfinderGoalSelector;
import net.minecraft.world.entity.ai.goal.PathfinderGoalWrapped;
import net.minecraft.world.entity.ai.navigation.AmphibiousPathNavigation;
import net.minecraft.world.entity.ai.navigation.Navigation;
import net.minecraft.world.entity.ai.navigation.NavigationAbstract;
import net.minecraft.world.entity.ai.navigation.NavigationFlying;
import net.minecraft.world.entity.ai.navigation.NavigationGuardian;
import net.minecraft.world.entity.ai.navigation.NavigationSpider;
import net.minecraft.world.entity.decoration.EntityArmorStand;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.EnumAnimation;
import net.minecraft.world.item.Instrument;
import net.minecraft.world.item.InstrumentItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.RayTrace;
import net.minecraft.world.level.World;
import net.minecraft.world.phys.MovingObjectPosition;
import net.minecraft.world.phys.MovingObjectPositionBlock;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.craftbukkit.v1_20_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_20_R2.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_20_R2.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_20_R2.util.CraftLocation;
import org.bukkit.craftbukkit.v1_20_R2.util.CraftMagicNumbers;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
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
    private EntityArmorStand dummyArmorStand;
    private EntityAreaEffectCloud dummyCloud;

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
        net.minecraft.world.entity.Entity nms = EntityUtils.nms(entity);
        EntitySize box = new EntitySize((float)hitbox.getMaxWidth(), (float)hitbox.getHeight(), true);
        ReflectionUtils.set(nms, NMSFields.ENTITY_dimensions, box);
        ReflectionUtils.set(nms, NMSFields.ENTITY_eyeHeight, Float.valueOf((float)hitbox.getEyeHeight()));
        nms.a(box.a(nms.dj()));
    }

    @Override
    public void setStepHeight(Entity entity, double height) {
        EntityUtils.nms(entity).t((float)height);
    }

    @Override
    public double getStepHeight(Entity entity) {
        return EntityUtils.nms(entity).dF();
    }

    @Override
    public void setPosition(Entity entity, double x, double y, double z) {
        EntityUtils.nms(entity).e(x, y, z);
    }

    @Override
    public void movePassenger(Entity entity, double x, double y, double z) {
        net.minecraft.world.entity.Entity nmsEntity = EntityUtils.nms(entity);
        if (this.dummyArmorStand == null) {
            this.dummyArmorStand = new EntityArmorStand(EntityTypes.d, nmsEntity.dL());
            this.dummyArmorStand.u(true);
        }
        double seatY = y + (double)nmsEntity.k((net.minecraft.world.entity.Entity)this.dummyArmorStand);
        nmsEntity.e(x, seatY, z);
        nmsEntity.f(Vec3D.b);
        nmsEntity.n();
        if (nmsEntity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer)nmsEntity;
            ReflectionUtils.set(player.c, NMSFields.SERVER_GAME_PACKET_LISTENER_IMPL_clientIsFloating, false);
        }
    }

    @Override
    public void forceSpawn(BaseEntity<?> entity, Player player) {
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
    public void forceDespawn(BaseEntity<?> entity, Player player) {
        if (player == null) {
            return;
        }
        NetworkUtils.send(player.getUniqueId(), (Packet<? super PacketListenerPlayOut>)new PacketPlayOutEntityDestroy(new int[]{entity.getEntityId()}));
    }

    @Override
    public void setForcedInvisible(Entity entity, boolean flag) {
        if (this.isForcedInvisible(entity) == flag) {
            return;
        }
        net.minecraft.world.entity.Entity nms = EntityUtils.nms(entity);
        byte data = 0;
        for (int i = 0; i < 8; ++i) {
            data = TMath.setBit(data, i, nms.i(i));
        }
        if (flag) {
            this.forceInvisible.add(entity.getUniqueId());
            data = TMath.setBit(data, 5, true);
        } else {
            this.forceInvisible.remove(entity.getUniqueId());
        }
        PacketDataSerializer buf = NetworkUtils.createByteBuf();
        buf.c(entity.getEntityId());
        EntityUtils.writeData(buf, 0, DataWatcherRegistry.a, data);
        buf.k(255);
        PacketPlayOutEntityMetadata packet = new PacketPlayOutEntityMetadata(buf);
        NetworkUtils.send(ServerInfo.getOnlinePlayers(), (Packet<? super PacketListenerPlayOut>)packet);
    }

    @Override
    public boolean isForcedInvisible(UUID uuid) {
        return this.forceInvisible.contains(uuid);
    }

    @Override
    public BodyRotationController wrapBodyRotationControl(Entity entity, Supplier<BodyRotationController> def) {
        net.minecraft.world.entity.Entity entity2 = EntityUtils.nms(entity);
        if (!(entity2 instanceof EntityInsentient)) {
            return def.get();
        }
        EntityInsentient mob = (EntityInsentient)entity2;
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
        if (!(entity2 instanceof EntityInsentient)) {
            return def.get();
        }
        EntityInsentient mob = (EntityInsentient)entity2;
        ControllerMove controllerMove = mob.I();
        if (controllerMove instanceof MoveController) {
            controller = (MoveController)controllerMove;
            return controller;
        }
        controller = new MoveControlWrapper(mob, mob.I());
        if (ReflectionUtils.set(mob, NMSFields.MOB_moveControl, controller)) {
            return controller;
        }
        return def.get();
    }

    @Override
    public LookController wrapLookController(Entity entity, Supplier<LookController> def) {
        net.minecraft.world.entity.Entity entity2 = EntityUtils.nms(entity);
        if (!(entity2 instanceof EntityInsentient)) {
            return def.get();
        }
        EntityInsentient mob = (EntityInsentient)entity2;
        LookControlWrapper controller = new LookControlWrapper(mob, mob.G());
        if (ReflectionUtils.set(mob, NMSFields.MOB_lookControl, controller)) {
            return controller;
        }
        return def.get();
    }

    @Override
    public void wrapNavigation(Entity entity) {
        net.minecraft.world.entity.Entity entity2 = EntityUtils.nms(entity);
        if (!(entity2 instanceof EntityInsentient)) {
            return;
        }
        EntityInsentient mob = (EntityInsentient)entity2;
        try {
            Object newNav;
            Field navField = ReflectionUtils.getField(NMSFields.MOB_navigation);
            NavigationAbstract oldNav = mob.L();
            if (oldNav instanceof NavigationSpider) {
                NavigationSpider wallClimberNavigation = (NavigationSpider)oldNav;
                newNav = new WallClimberNavigationWrapper(mob, wallClimberNavigation);
            } else if (oldNav instanceof Navigation) {
                Navigation groundPathNavigation = (Navigation)oldNav;
                newNav = new GroundNavigationWrapper(mob, groundPathNavigation);
            } else if (oldNav instanceof NavigationFlying) {
                NavigationFlying flyingPathNavigation = (NavigationFlying)oldNav;
                newNav = new FlyingNavigationWrapper(mob, flyingPathNavigation);
            } else if (oldNav instanceof NavigationGuardian) {
                newNav = new WaterBoundNavigationWrapper(mob);
            } else if (oldNav instanceof AmphibiousPathNavigation) {
                AmphibiousPathNavigation amphibiousPathNavigation = (AmphibiousPathNavigation)oldNav;
                newNav = new AmphibiousNavigationWrapper(mob, amphibiousPathNavigation);
            } else {
                TLogger.warn("Failed to create custom navigation for " + mob.ag() + ": " + mob.cv());
                TLogger.warn("Reason: Navigation class type is " + oldNav.getClass().getSimpleName() + ".");
                return;
            }
            navField.set(mob, newNav);
            PathfinderGoalSelector goalSelector = (PathfinderGoalSelector)ReflectionUtils.getField(NMSFields.MOB_goalSelector).get(mob);
            RaceConditionUtil.wrapConmod(() -> this.lambda$wrapNavigation$0(goalSelector, (NavigationAbstract)newNav));
        }
        catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void replaceNavigation(PathfinderGoalSelector goalSelector, NavigationAbstract newNav) {
        try {
            for (PathfinderGoalWrapped wrappedGoal : goalSelector.b()) {
                PathfinderGoal goal = wrappedGoal.k();
                for (Field field : goal.getClass().getDeclaredFields()) {
                    field = ReflectionUtils.getField(goal.getClass(), field.getName());
                    Object f = field.get(goal);
                    if (!(f instanceof NavigationAbstract)) continue;
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
        WorldServer level = ((CraftWorld)location.getWorld()).getHandle();
        HitboxEntityImpl entity = new HitboxEntityImpl((World)level, bone, subHitbox);
        entity.queueLocation(new Vector3f().set(location.getX(), location.getY(), location.getZ()));
        entity.e(location.getX(), location.getY(), location.getZ());
        ModelEngineAPI.setRenderCanceled(entity.ah(), true);
        Promise.start((Entity)entity.getBukkitEntity()).thenRunSync(() -> {
            level.b((net.minecraft.world.entity.Entity)entity);
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
            return EntityUtils.nms(entity).a(damageSource, amount);
        }
        throw new RuntimeException("Passed in source is not an NMS DamageSource.");
    }

    @Override
    public EntityHandler.InteractionResult interact(Entity entity, HumanEntity player, EquipmentSlot hand) {
        net.minecraft.world.entity.Entity entity2 = EntityUtils.nms(entity);
        if (!(entity2 instanceof EntityLiving)) {
            return EntityHandler.InteractionResult.FAIL;
        }
        EntityLiving livingEntity = (EntityLiving)entity2;
        EnumInteractionResult result = livingEntity.a((EntityHuman)((CraftPlayer)player).getHandle(), hand == EquipmentSlot.HAND ? EnumHand.a : EnumHand.b);
        return switch (result) {
            default -> throw new IncompatibleClassChangeError();
            case EnumInteractionResult.a -> EntityHandler.InteractionResult.SUCCESS;
            case EnumInteractionResult.b -> EntityHandler.InteractionResult.CONSUME;
            case EnumInteractionResult.c -> EntityHandler.InteractionResult.CONSUME_PARTIAL;
            case EnumInteractionResult.d -> EntityHandler.InteractionResult.PASS;
            case EnumInteractionResult.e -> EntityHandler.InteractionResult.FAIL;
        };
    }

    @Override
    public void spawnDynamicHitbox(DynamicHitbox hitbox) {
        Vector location = hitbox.getPositionTracker().get();
        final Packets.PacketSupplier pivotSpawn = NetworkUtils.createPivotSpawn(DynamicHitbox.getPivotId(), DynamicHitbox.getPivotUUID(), location.toVector3f().add(0.0f, -0.5202f, 0.0f));
        PacketDataSerializer pivotDataBuf = NetworkUtils.createByteBuf();
        pivotDataBuf.c(DynamicHitbox.getPivotId());
        EntityUtils.writeData(pivotDataBuf, 0, DataWatcherRegistry.a, (byte)32);
        EntityUtils.writeData(pivotDataBuf, 1, DataWatcherRegistry.b, Integer.MAX_VALUE);
        EntityUtils.writeData(pivotDataBuf, 8, DataWatcherRegistry.d, Float.valueOf(0.0f));
        pivotDataBuf.k(255);
        final PacketPlayOutEntityMetadata pivotData = new PacketPlayOutEntityMetadata(pivotDataBuf);
        final PacketPlayOutSpawnEntity hitboxSpawn = new PacketPlayOutSpawnEntity(DynamicHitbox.getHitboxId(), DynamicHitbox.getHitboxUUID(), location.getX(), location.getY() - 0.5202, location.getZ(), 0.0f, 0.0f, EntityTypes.aL, 0, Vec3D.b, 0.0);
        PacketDataSerializer hitboxDataBuf = NetworkUtils.createByteBuf();
        hitboxDataBuf.c(DynamicHitbox.getHitboxId());
        EntityUtils.writeData(hitboxDataBuf, 0, DataWatcherRegistry.a, (byte)32);
        EntityUtils.writeData(hitboxDataBuf, 1, DataWatcherRegistry.b, Integer.MAX_VALUE);
        EntityUtils.writeData(hitboxDataBuf, 16, DataWatcherRegistry.b, 2);
        hitboxDataBuf.k(255);
        final PacketPlayOutEntityMetadata hitboxData = new PacketPlayOutEntityMetadata(hitboxDataBuf);
        PacketDataSerializer mountBuf = NetworkUtils.createByteBuf();
        mountBuf.c(DynamicHitbox.getPivotId());
        mountBuf.c(1);
        mountBuf.c(DynamicHitbox.getHitboxId());
        final PacketPlayOutMount mount = new PacketPlayOutMount(mountBuf);
        NetworkUtils.sendBundled(Set.of(hitbox.getPlayer().getUniqueId()), new Packets(){
            {
                this.add(pivotSpawn);
                this.add((Packet<PacketListenerPlayOut>)pivotData);
                this.add((Packet<PacketListenerPlayOut>)hitboxSpawn);
                this.add((Packet<PacketListenerPlayOut>)hitboxData);
                this.add((Packet<PacketListenerPlayOut>)mount);
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
        PacketPlayOutEntityDestroy destroy = new PacketPlayOutEntityDestroy(new int[]{DynamicHitbox.getHitboxId(), DynamicHitbox.getPivotId()});
        NetworkUtils.send(hitbox.getPlayer().getUniqueId(), (Packet<? super PacketListenerPlayOut>)destroy);
    }

    @Override
    public void forceUseItem(Player player, EquipmentSlot hand) {
        ItemStack stack = player.getEquipment().getItem(hand);
        net.minecraft.world.item.ItemStack nmsStack = ((CraftItemStack)stack).handle;
        EntityPlayer nmsPlayer = (EntityPlayer)EntityUtils.nms((Entity)player);
        PlayerConnection connection = nmsPlayer.c;
        PacketPlayInBlockPlace useItemPacket = new PacketPlayInBlockPlace(hand == EquipmentSlot.HAND ? EnumHand.a : EnumHand.b, 0);
        useItemPacket.timestamp = System.currentTimeMillis();
        PatchedServerGamePacketListener.handleUseItem(useItemPacket, (PacketListenerPlayIn)connection, (Consumer<EnumInteractionResult>)((Consumer)interactionResult -> {
            if (nmsStack.s() == EnumAnimation.a || interactionResult != EnumInteractionResult.b) {
                return;
            }
            PacketDataSerializer buf = NetworkUtils.createByteBuf();
            buf.c(player.getEntityId());
            EntityUtils.writeData(buf, 8, DataWatcherRegistry.a, (byte)(hand == EquipmentSlot.HAND ? 1 : 3));
            buf.k(255);
            PacketPlayOutEntityMetadata packet = new PacketPlayOutEntityMetadata(buf);
            NetworkUtils.send(player.getUniqueId(), (Packet<? super PacketListenerPlayOut>)packet);
            Item patt16355$temp = nmsStack.d();
            if (patt16355$temp instanceof InstrumentItem) {
                InstrumentItem instrumentItem = (InstrumentItem)patt16355$temp;
                Optional optional = (Optional)ReflectionUtils.call(instrumentItem, NMSMethods.INSTRUMENT_ITEM_getInstrument, nmsStack);
                optional.ifPresent(instrumentHolder -> {
                    Instrument instrument = (Instrument)instrumentHolder.a();
                    Holder soundEvent = instrument.a();
                    float f = instrument.c() / 16.0f;
                    RandomSource random = (RandomSource)ReflectionUtils.get(nmsPlayer.dL(), NMSFields.LEVEL_threadSafeRandom);
                    PacketPlayOutEntitySound soundPacket = new PacketPlayOutEntitySound(soundEvent, SoundCategory.c, (net.minecraft.world.entity.Entity)nmsPlayer, f, 1.0f, random.g());
                    NetworkUtils.send(player.getUniqueId(), (Packet<? super PacketListenerPlayOut>)soundPacket);
                });
            }
        }));
    }

    @Override
    public float getYRot(Entity entity) {
        return EntityUtils.nms(entity).dB();
    }

    @Override
    public float getYHeadRot(Entity entity) {
        net.minecraft.world.entity.Entity nms = EntityUtils.nms(entity);
        if (nms instanceof EntityLiving) {
            EntityLiving livingEntity = (EntityLiving)nms;
            return livingEntity.co();
        }
        return nms.dB();
    }

    @Override
    public float getXHeadRot(Entity entity) {
        return EntityUtils.nms(entity).dD();
    }

    @Override
    public float getYBodyRot(Entity entity) {
        net.minecraft.world.entity.Entity nms = EntityUtils.nms(entity);
        if (nms instanceof EntityLiving) {
            EntityLiving livingEntity = (EntityLiving)nms;
            return livingEntity.aU;
        }
        return nms.dB();
    }

    @Override
    public void setYRot(Entity entity, float angle) {
        EntityUtils.nms(entity).r(angle);
    }

    @Override
    public void setYHeadRot(Entity entity, float angle) {
        net.minecraft.world.entity.Entity nms = EntityUtils.nms(entity);
        if (nms instanceof EntityLiving) {
            EntityLiving livingEntity = (EntityLiving)nms;
            livingEntity.n(angle);
        } else {
            nms.r(angle);
        }
    }

    @Override
    public void setXHeadRot(Entity entity, float angle) {
        EntityUtils.nms(entity).s(angle);
    }

    @Override
    public void setYBodyRot(Entity entity, float angle) {
        net.minecraft.world.entity.Entity nms = EntityUtils.nms(entity);
        if (nms instanceof EntityLiving) {
            EntityLiving livingEntity = (EntityLiving)nms;
            livingEntity.o(angle);
        } else {
            nms.r(angle);
        }
    }

    @Override
    public void move(Entity entity, double x, double y, double z) {
        net.minecraft.world.entity.Entity nms = EntityUtils.nms(entity);
        nms.a(EnumMoveType.a, new Vec3D(x, y, z));
    }

    @Override
    public boolean isWalking(Entity entity) {
        double dZ;
        net.minecraft.world.entity.Entity nms = EntityUtils.nms(entity);
        if (nms.ah < 1) {
            return false;
        }
        double dX = nms.dq() - nms.ac;
        return dX * dX + (dZ = nms.dw() - nms.ae) * dZ > 2.500000277905201E-7;
    }

    @Override
    public boolean isStrafing(Entity entity) {
        return false;
    }

    @Override
    public boolean isJumping(Entity entity) {
        net.minecraft.world.entity.Entity entity2 = EntityUtils.nms(entity);
        if (entity2 instanceof EntityLiving) {
            EntityLiving livingEntity = (EntityLiving)entity2;
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
        if (entity2 instanceof EntityLiving) {
            EntityLiving livingEntity = (EntityLiving)entity2;
            return livingEntity.eu();
        }
        return 20.0f;
    }

    @Override
    public float getMaxHealth(Entity entity) {
        net.minecraft.world.entity.Entity entity2 = EntityUtils.nms(entity);
        if (entity2 instanceof EntityLiving) {
            EntityLiving livingEntity = (EntityLiving)entity2;
            return livingEntity.eL();
        }
        return 20.0f;
    }

    @Override
    public Vector3f getPivotOffset(Entity entity) {
        net.minecraft.world.entity.Entity nmsEntity = EntityUtils.nms(entity);
        if (this.dummyCloud == null) {
            this.dummyCloud = new EntityAreaEffectCloud(EntityTypes.c, nmsEntity.dL());
        }
        Vec3D offset = (Vec3D)ReflectionUtils.call(nmsEntity, NMSMethods.ENTITY_getPassengerAttachmentPoint, this.dummyCloud, nmsEntity.a(EntityPose.a), Float.valueOf(1.0f));
        Vec3D offset2 = this.dummyCloud.m((net.minecraft.world.entity.Entity)this.dummyCloud);
        return offset == null ? new Vector3f() : offset.e(offset2).j();
    }

    @Override
    public boolean isRemoved(Entity entity) {
        return EntityUtils.nms(entity).dG();
    }

    @Override
    public int getGlowColor(Entity entity) {
        net.minecraft.world.entity.Entity nms = EntityUtils.nms(entity);
        return nms.h_();
    }

    @Override
    public void setDeathTick(Entity entity, int tick) {
        net.minecraft.world.entity.Entity nms = EntityUtils.nms(entity);
        if (nms instanceof EntityLiving) {
            EntityLiving livingEntity = (EntityLiving)nms;
            livingEntity.aM = tick;
        }
    }

    @Override
    public TrackedEntity wrapTrackedEntity(Entity entity) {
        WorldServer level = ((CraftWorld)entity.getWorld()).getHandle();
        Int2ObjectMap map = level.k().a.K;
        PlayerChunkMap.EntityTracker tracker = (PlayerChunkMap.EntityTracker)map.get(entity.getEntityId());
        if (tracker == null) {
            return new TempTrackedEntity(entity);
        }
        return new TrackedEntityImpl(entity, () -> EntityHandlerImpl.lambda$wrapTrackedEntity$4((Map)map, entity), tracker);
    }

    @Override
    public boolean shouldCull(Player player, Location eyePosition, BoundingBox box) {
        CraftWorld world = (CraftWorld)player.getWorld();
        Vec3D start = CraftLocation.toVec3D((Location)eyePosition);
        if (box.getWidthX() >= this.forceRenderWidth || box.getWidthZ() >= this.forceRenderWidth || box.getHeight() >= this.forceRenderHeight) {
            return false;
        }
        int minX = MathHelper.a((double)box.getMinX());
        int minY = MathHelper.a((double)box.getMinY());
        int minZ = MathHelper.a((double)box.getMinZ());
        int maxX = MathHelper.c((double)box.getMaxX()) - 1;
        int maxY = MathHelper.c((double)box.getMaxY()) - 1;
        int maxZ = MathHelper.c((double)box.getMaxZ()) - 1;
        EntityHandler.BoxRelToCam relX = EntityHandler.BoxRelToCam.from(minX, maxX, MathHelper.a((double)start.c));
        EntityHandler.BoxRelToCam relY = EntityHandler.BoxRelToCam.from(minY, maxY, MathHelper.a((double)start.d));
        EntityHandler.BoxRelToCam relZ = EntityHandler.BoxRelToCam.from(minZ, maxZ, MathHelper.a((double)start.e));
        if (relX == EntityHandler.BoxRelToCam.INSIDE && relY == EntityHandler.BoxRelToCam.INSIDE && relZ == EntityHandler.BoxRelToCam.INSIDE) {
            return false;
        }
        LinkedHashSet<Vec3D> points = new LinkedHashSet<Vec3D>();
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
                        points.add(new Vec3D((double)((float)x + point.x), (double)((float)y + point.y), (double)((float)z + point.z)));
                    }
                }
            }
        }
        if (DebugToggle.isDebugging(DebugToggle.SHOW_CULL_POINTS)) {
            for (Vec3D point : points) {
                world.spawnParticle(Particle.REDSTONE, point.c, point.d, point.e, 1, (Object)new Particle.DustOptions(Color.RED, 0.2f));
            }
        }
        for (Vec3D point : points) {
            if (!EntityHandlerImpl.isVisible(world, start, point)) continue;
            return false;
        }
        return true;
    }

    private static boolean isVisible(CraftWorld world, Vec3D startPos, Vec3D endPos) {
        MovingObjectPositionBlock nmsHitResult = usePaperClipMethod ? world.getHandle().a((RayTrace)new OcclusionClipContext(startPos, endPos)) : world.getHandle().clip((RayTrace)new OcclusionClipContext(startPos, endPos), (Predicate)null);
        return nmsHitResult.c() == MovingObjectPosition.EnumMovingObjectType.a;
    }

    private static /* synthetic */ PlayerChunkMap.EntityTracker lambda$wrapTrackedEntity$4(Map map, Entity entity) {
        return (PlayerChunkMap.EntityTracker)map.get(entity.getEntityId());
    }

    private /* synthetic */ void lambda$wrapNavigation$0(PathfinderGoalSelector goalSelector, NavigationAbstract newNav) {
        this.replaceNavigation(goalSelector, newNav);
    }
}

