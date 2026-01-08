/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.destroystokyo.paper.event.player.PlayerUseUnknownEntityEvent
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.logging.LogUtils
 *  javax.annotation.Nullable
 *  net.minecraft.advancements.CriterionTriggers
 *  net.minecraft.core.BlockPosition
 *  net.minecraft.core.EnumDirection
 *  net.minecraft.network.PacketListener
 *  net.minecraft.network.protocol.Packet
 *  net.minecraft.network.protocol.PlayerConnectionUtils
 *  net.minecraft.network.protocol.game.PacketListenerPlayIn
 *  net.minecraft.network.protocol.game.PacketPlayInBlockPlace
 *  net.minecraft.network.protocol.game.PacketPlayInUseEntity
 *  net.minecraft.network.protocol.game.PacketPlayInUseEntity$c
 *  net.minecraft.network.protocol.game.PacketPlayOutAttachEntity
 *  net.minecraft.network.protocol.game.PacketPlayOutEntityEquipment
 *  net.minecraft.server.level.EntityPlayer
 *  net.minecraft.server.level.WorldServer
 *  net.minecraft.server.network.PlayerConnection
 *  net.minecraft.util.MathHelper
 *  net.minecraft.world.EnumHand
 *  net.minecraft.world.EnumInteractionResult
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityExperienceOrb
 *  net.minecraft.world.entity.EntityInsentient
 *  net.minecraft.world.entity.EntityLiving
 *  net.minecraft.world.entity.EnumItemSlot
 *  net.minecraft.world.entity.animal.Bucketable
 *  net.minecraft.world.entity.animal.allay.Allay
 *  net.minecraft.world.entity.item.EntityItem
 *  net.minecraft.world.entity.player.EntityHuman
 *  net.minecraft.world.entity.projectile.EntityArrow
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.level.EnumGamemode
 *  net.minecraft.world.level.RayTrace
 *  net.minecraft.world.level.RayTrace$BlockCollisionOption
 *  net.minecraft.world.level.RayTrace$FluidCollisionOption
 *  net.minecraft.world.level.World
 *  net.minecraft.world.phys.AxisAlignedBB
 *  net.minecraft.world.phys.MovingObjectPosition$EnumMovingObjectType
 *  net.minecraft.world.phys.MovingObjectPositionBlock
 *  net.minecraft.world.phys.Vec3D
 *  org.bukkit.craftbukkit.v1_20_R1.CraftServer
 *  org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer
 *  org.bukkit.craftbukkit.v1_20_R1.event.CraftEventFactory
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Event
 *  org.bukkit.event.Event$Result
 *  org.bukkit.event.block.Action
 *  org.bukkit.event.player.PlayerInteractAtEntityEvent
 *  org.bukkit.event.player.PlayerInteractEntityEvent
 *  org.bukkit.event.player.PlayerInteractEvent
 *  org.bukkit.inventory.EquipmentSlot
 *  org.bukkit.util.Consumer
 *  org.bukkit.util.Vector
 *  org.jetbrains.annotations.NotNull
 *  org.slf4j.Logger
 */
package com.ticxo.modelengine.v1_20_R1.network.patch;

import com.destroystokyo.paper.event.player.PlayerUseUnknownEntityEvent;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.ServerInfo;
import com.ticxo.modelengine.api.entity.BaseEntity;
import com.ticxo.modelengine.api.events.BaseEntityInteractEvent;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.nms.entity.EntityHandler;
import com.ticxo.modelengine.api.utils.ReflectionUtils;
import com.ticxo.modelengine.v1_20_R1.NMSMethods;
import com.ticxo.modelengine.v1_20_R1.network.patch.ServerboundInteractPacketWrapper;
import java.util.Arrays;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriterionTriggers;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PlayerConnectionUtils;
import net.minecraft.network.protocol.game.PacketListenerPlayIn;
import net.minecraft.network.protocol.game.PacketPlayInBlockPlace;
import net.minecraft.network.protocol.game.PacketPlayInUseEntity;
import net.minecraft.network.protocol.game.PacketPlayOutAttachEntity;
import net.minecraft.network.protocol.game.PacketPlayOutEntityEquipment;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.server.network.PlayerConnection;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumHand;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.entity.EntityExperienceOrb;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.entity.animal.Bucketable;
import net.minecraft.world.entity.animal.allay.Allay;
import net.minecraft.world.entity.item.EntityItem;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.entity.projectile.EntityArrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.EnumGamemode;
import net.minecraft.world.level.RayTrace;
import net.minecraft.world.level.World;
import net.minecraft.world.phys.AxisAlignedBB;
import net.minecraft.world.phys.MovingObjectPosition;
import net.minecraft.world.phys.MovingObjectPositionBlock;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.craftbukkit.v1_20_R1.CraftServer;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_20_R1.event.CraftEventFactory;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.util.Consumer;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

public class PatchedServerGamePacketListener {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static void handleInteract(final ServerboundInteractPacketWrapper interactPacket, PacketListenerPlayIn listener) {
        if (!(listener instanceof PlayerConnection)) {
            return;
        }
        final PlayerConnection connection = (PlayerConnection)listener;
        PlayerConnectionUtils.a((Packet)interactPacket, (PacketListener)connection, (WorldServer)connection.f().x());
        final EntityPlayer player = connection.f();
        if (player.eT()) {
            return;
        }
        final CraftPlayer craftPlayer = player.getBukkitEntity();
        final WorldServer serverLevel = player.x();
        final CraftServer craftServer = serverLevel.n().server;
        final net.minecraft.world.entity.Entity entity = interactPacket.a(serverLevel);
        interactPacket.a(new PacketPlayInUseEntity.c(){

            public void a(EnumHand hand) {
                if (!interactPacket.isFakeInteraction()) {
                    return;
                }
                EntityHandler entityHandler = ModelEngineAPI.getNMSHandler().getEntityHandler();
                entityHandler.forceUseItem((Player)craftPlayer, EquipmentSlot.HAND);
                entityHandler.forceUseItem((Player)craftPlayer, EquipmentSlot.OFF_HAND);
            }

            public void a(EnumHand hand, Vec3D pos) {
            }

            public void a() {
            }
        });
        if (entity == player && !player.G_()) {
            return;
        }
        player.C();
        player.f(interactPacket.a());
        final ActiveModel activeModel = ModelEngineAPI.getInteractionTracker().getModelRelay(interactPacket.getOriginalId());
        if (activeModel != null) {
            final BaseEntity<?> base = activeModel.getModeledEntity().getBase();
            interactPacket.a(new PacketPlayInUseEntity.c(){

                public void a(EnumHand hand) {
                    craftServer.getPluginManager().callEvent((Event)new BaseEntityInteractEvent((Player)craftPlayer, base, activeModel, BaseEntityInteractEvent.Action.INTERACT, EquipmentSlot.HAND, interactPacket.a(), craftPlayer.getInventory().getItem(EquipmentSlot.HAND), null));
                    craftServer.getPluginManager().callEvent((Event)new BaseEntityInteractEvent((Player)craftPlayer, base, activeModel, BaseEntityInteractEvent.Action.INTERACT, EquipmentSlot.OFF_HAND, interactPacket.a(), craftPlayer.getInventory().getItem(EquipmentSlot.OFF_HAND), null));
                }

                public void a(EnumHand hand, Vec3D pos) {
                    Vector position = new Vector(pos.c, pos.d, pos.e);
                    craftServer.getPluginManager().callEvent((Event)new BaseEntityInteractEvent((Player)craftPlayer, base, activeModel, BaseEntityInteractEvent.Action.INTERACT_ON, EquipmentSlot.HAND, interactPacket.a(), craftPlayer.getInventory().getItem(EquipmentSlot.HAND), position));
                    craftServer.getPluginManager().callEvent((Event)new BaseEntityInteractEvent((Player)craftPlayer, base, activeModel, BaseEntityInteractEvent.Action.INTERACT_ON, EquipmentSlot.OFF_HAND, interactPacket.a(), craftPlayer.getInventory().getItem(EquipmentSlot.OFF_HAND), position));
                }

                public void a() {
                    craftServer.getPluginManager().callEvent((Event)new BaseEntityInteractEvent((Player)craftPlayer, base, activeModel, BaseEntityInteractEvent.Action.ATTACK, EquipmentSlot.HAND, interactPacket.a(), craftPlayer.getInventory().getItem(EquipmentSlot.HAND), null));
                }
            });
        }
        if (entity == null) {
            if (ServerInfo.IS_PAPER) {
                interactPacket.a(new PacketPlayInUseEntity.c(){

                    public void a(EnumHand hand) {
                        PatchedServerGamePacketListener.callPlayerUseUnknownEntityEvent(craftServer, craftPlayer, interactPacket, hand, null);
                    }

                    public void a(EnumHand hand, Vec3D pos) {
                        PatchedServerGamePacketListener.callPlayerUseUnknownEntityEvent(craftServer, craftPlayer, interactPacket, hand, pos);
                    }

                    public void a() {
                        PatchedServerGamePacketListener.callPlayerUseUnknownEntityEvent(craftServer, craftPlayer, interactPacket, EnumHand.a, null);
                    }
                });
            }
            return;
        }
        if (!serverLevel.w_().a(entity.di())) {
            return;
        }
        AxisAlignedBB boundingBox = entity.cE();
        if (boundingBox.e(player.bm()) >= PlayerConnection.a) {
            return;
        }
        interactPacket.a(new PacketPlayInUseEntity.c(){

            private void performInteraction(EnumHand enumhand, EntityInteraction entityInteraction, PlayerInteractEntityEvent event) {
                ItemStack itemstack = player.b(enumhand);
                if (itemstack.a(serverLevel.G())) {
                    ItemStack itemstack1 = itemstack.p();
                    ItemStack itemInHand = player.b(enumhand);
                    boolean triggerLeashUpdate = itemInHand.d() == Items.tQ && entity instanceof EntityInsentient;
                    Item origItem = player.fN().f().d();
                    craftServer.getPluginManager().callEvent((Event)event);
                    if (entity instanceof Bucketable && entity instanceof EntityLiving && origItem.k() == Items.pL && (event.isCancelled() || player.fN().f().d() != origItem)) {
                        entity.aj().resendPossiblyDesyncedEntity(player);
                        player.bR.b();
                    }
                    if (triggerLeashUpdate && (event.isCancelled() || player.fN().f().d() != origItem)) {
                        connection.a((Packet)new PacketPlayOutAttachEntity(entity, ((EntityInsentient)entity).fP()));
                    }
                    if (event.isCancelled() || player.fN().f().d() != origItem) {
                        entity.aj().refresh(player);
                        if (entity instanceof Allay) {
                            Allay allay = (Allay)entity;
                            connection.a((Packet)new PacketPlayOutEntityEquipment(entity.af(), Arrays.stream(EnumItemSlot.values()).map(slot -> Pair.of((Object)slot, (Object)allay.stripMeta(allay.c(slot), true))).collect(Collectors.toList())));
                            player.bR.b();
                        }
                    }
                    if (event.isCancelled()) {
                        player.bR.b();
                        return;
                    }
                    EnumInteractionResult enuminteractionresult = entityInteraction.run(player, entity, enumhand);
                    if (!itemInHand.b() && itemInHand.L() <= -1) {
                        player.bR.b();
                    }
                    if (enuminteractionresult.a()) {
                        CriterionTriggers.Q.a(player, itemstack1, entity);
                        if (enuminteractionresult.b()) {
                            player.a(enumhand, true);
                        }
                    }
                }
            }

            public void a(@NotNull EnumHand hand) {
                this.performInteraction(hand, EntityHuman::a, new PlayerInteractEntityEvent((Player)connection.getCraftPlayer(), (Entity)entity.getBukkitEntity(), hand == EnumHand.b ? EquipmentSlot.OFF_HAND : EquipmentSlot.HAND));
            }

            public void a(@NotNull EnumHand hand, @NotNull Vec3D pos) {
                this.performInteraction(hand, (entityplayer, entity1, enumhand1) -> entity1.a((EntityHuman)entityplayer, pos, enumhand1), (PlayerInteractEntityEvent)new PlayerInteractAtEntityEvent((Player)connection.getCraftPlayer(), (Entity)entity.getBukkitEntity(), new Vector(pos.c, pos.d, pos.e), hand == EnumHand.b ? EquipmentSlot.OFF_HAND : EquipmentSlot.HAND));
            }

            public void a() {
                ItemStack itemstack;
                if (!(entity instanceof EntityItem || entity instanceof EntityExperienceOrb || entity instanceof EntityArrow || entity == player && !player.G_() || !(itemstack = player.b(EnumHand.a)).a(serverLevel.G()))) {
                    player.d(entity);
                    if (!itemstack.b() && itemstack.L() <= -1) {
                        player.bR.b();
                    }
                }
            }
        });
    }

    public static void handleUseItem(PacketPlayInBlockPlace packet, PacketListenerPlayIn listener, Consumer<EnumInteractionResult> afterUse) {
        if (!(listener instanceof PlayerConnection)) {
            return;
        }
        PlayerConnection connection = (PlayerConnection)listener;
        EntityPlayer player = connection.f();
        PlayerConnectionUtils.a((Packet)packet, (PacketListener)connection, (WorldServer)player.x());
        if (player.eT()) {
            return;
        }
        boolean shouldLimit = Boolean.TRUE.equals(ReflectionUtils.call(connection, NMSMethods.SERVER_GAME_PACKET_LISTENER_IMPL_checkLimit, packet.timestamp));
        if (!shouldLimit) {
            return;
        }
        connection.a(packet.c());
        WorldServer worldserver = player.x();
        EnumHand enumhand = packet.a();
        ItemStack itemstack = player.b(enumhand);
        player.C();
        if (!itemstack.b() && itemstack.a(worldserver.G())) {
            boolean cancelled;
            float f1 = player.dA();
            float f2 = player.dy();
            double d0 = player.dn();
            double d1 = player.dp() + (double)player.cF();
            double d2 = player.dt();
            Vec3D vec3d = new Vec3D(d0, d1, d2);
            float f3 = MathHelper.b((float)(-f2 * ((float)Math.PI / 180) - (float)Math.PI));
            float f4 = MathHelper.a((float)(-f2 * ((float)Math.PI / 180) - (float)Math.PI));
            float f5 = -MathHelper.b((float)(-f1 * ((float)Math.PI / 180)));
            float f6 = MathHelper.a((float)(-f1 * ((float)Math.PI / 180)));
            float f7 = f4 * f5;
            float f8 = f3 * f5;
            double d3 = player.e.b() == EnumGamemode.b ? 5.0 : 4.5;
            Vec3D vec3d1 = vec3d.b((double)f7 * d3, (double)f6 * d3, (double)f8 * d3);
            MovingObjectPositionBlock movingobjectposition = player.dI().a(new RayTrace(vec3d, vec3d1, RayTrace.BlockCollisionOption.b, RayTrace.FluidCollisionOption.a, (net.minecraft.world.entity.Entity)player));
            if (movingobjectposition == null || movingobjectposition.c() != MovingObjectPosition.EnumMovingObjectType.b) {
                PlayerInteractEvent event = CraftEventFactory.callPlayerInteractEvent((EntityHuman)player, (Action)Action.RIGHT_CLICK_AIR, (ItemStack)itemstack, (EnumHand)enumhand);
                cancelled = event.useItemInHand() == Event.Result.DENY;
            } else {
                PlayerInteractEvent event;
                MovingObjectPositionBlock movingobjectpositionblock = movingobjectposition;
                cancelled = player.e.firedInteract && player.e.interactPosition.equals((Object)movingobjectpositionblock.a()) && player.e.interactHand == enumhand && ItemStack.c((ItemStack)player.e.interactItemStack, (ItemStack)itemstack) ? player.e.interactResult : (event = CraftEventFactory.callPlayerInteractEvent((EntityHuman)player, (Action)Action.RIGHT_CLICK_BLOCK, (BlockPosition)movingobjectpositionblock.a(), (EnumDirection)movingobjectpositionblock.b(), (ItemStack)itemstack, (boolean)true, (EnumHand)enumhand, (Vec3D)movingobjectpositionblock.e())).useItemInHand() == Event.Result.DENY;
                player.e.firedInteract = false;
            }
            if (cancelled) {
                player.resyncUsingItem(player);
                player.getBukkitEntity().updateInventory();
                return;
            }
            itemstack = player.b(enumhand);
            if (itemstack.b()) {
                return;
            }
            EnumInteractionResult enuminteractionresult = player.e.a(player, (World)worldserver, itemstack, enumhand);
            afterUse.accept((Object)enuminteractionresult);
            if (enuminteractionresult.b()) {
                player.a(enumhand, true);
            }
        }
    }

    private static void callPlayerUseUnknownEntityEvent(CraftServer cserver, CraftPlayer craftPlayer, PacketPlayInUseEntity packet, EnumHand hand, @Nullable Vec3D vector) {
        cserver.getPluginManager().callEvent((Event)new PlayerUseUnknownEntityEvent((Player)craftPlayer, packet.getEntityId(), packet.isAttack(), hand == EnumHand.a ? EquipmentSlot.HAND : EquipmentSlot.OFF_HAND, vector != null ? new Vector(vector.c, vector.d, vector.e) : null));
    }

    @FunctionalInterface
    private static interface EntityInteraction {
        public EnumInteractionResult run(EntityPlayer var1, net.minecraft.world.entity.Entity var2, EnumHand var3);
    }
}

