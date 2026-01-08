/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.destroystokyo.paper.event.player.PlayerUseUnknownEntityEvent
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.logging.LogUtils
 *  javax.annotation.Nullable
 *  net.minecraft.advancements.CriteriaTriggers
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.network.PacketListener
 *  net.minecraft.network.protocol.Packet
 *  net.minecraft.network.protocol.PacketUtils
 *  net.minecraft.network.protocol.game.ClientboundBundlePacket
 *  net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket
 *  net.minecraft.network.protocol.game.ClientboundSetEntityLinkPacket
 *  net.minecraft.network.protocol.game.ClientboundSetEquipmentPacket
 *  net.minecraft.network.protocol.game.ServerGamePacketListener
 *  net.minecraft.network.protocol.game.ServerboundInteractPacket$Handler
 *  net.minecraft.network.protocol.game.ServerboundUseItemPacket
 *  net.minecraft.server.level.ServerEntity
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.server.network.ServerGamePacketListenerImpl
 *  net.minecraft.util.Mth
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EquipmentSlot
 *  net.minecraft.world.entity.ExperienceOrb
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.Mob
 *  net.minecraft.world.entity.animal.Bucketable
 *  net.minecraft.world.entity.animal.allay.Allay
 *  net.minecraft.world.entity.item.ItemEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.entity.projectile.AbstractArrow
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.level.ClipContext
 *  net.minecraft.world.level.ClipContext$Block
 *  net.minecraft.world.level.ClipContext$Fluid
 *  net.minecraft.world.level.GameType
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.HitResult$Type
 *  net.minecraft.world.phys.Vec3
 *  org.bukkit.craftbukkit.CraftServer
 *  org.bukkit.craftbukkit.entity.CraftPlayer
 *  org.bukkit.craftbukkit.event.CraftEventFactory
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
package com.ticxo.modelengine.v1_20_R4.network.patch;

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
import com.ticxo.modelengine.v1_20_R4.NMSMethods;
import com.ticxo.modelengine.v1_20_R4.network.patch.ServerboundInteractPacketWrapper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketUtils;
import net.minecraft.network.protocol.game.ClientboundBundlePacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityLinkPacket;
import net.minecraft.network.protocol.game.ClientboundSetEquipmentPacket;
import net.minecraft.network.protocol.game.ServerGamePacketListener;
import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import net.minecraft.network.protocol.game.ServerboundUseItemPacket;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Bucketable;
import net.minecraft.world.entity.animal.allay.Allay;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Consumer;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

public class PatchedServerGamePacketListener {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static void handleInteract(final ServerboundInteractPacketWrapper interactPacket, ServerGamePacketListener listener) {
        if (!(listener instanceof ServerGamePacketListenerImpl)) {
            return;
        }
        final ServerGamePacketListenerImpl connection = (ServerGamePacketListenerImpl)listener;
        PacketUtils.ensureRunningOnSameThread((Packet)interactPacket, (PacketListener)connection, (ServerLevel)connection.getPlayer().serverLevel());
        final ServerPlayer player = connection.getPlayer();
        if (player.isImmobile()) {
            return;
        }
        final CraftPlayer craftPlayer = player.getBukkitEntity();
        final ServerLevel serverLevel = player.serverLevel();
        final CraftServer craftServer = serverLevel.getServer().server;
        final net.minecraft.world.entity.Entity entity = interactPacket.getTarget(serverLevel);
        interactPacket.dispatch(new ServerboundInteractPacket.Handler(){

            public void onInteraction(InteractionHand hand) {
                if (!interactPacket.isFakeInteraction()) {
                    return;
                }
                EntityHandler entityHandler = ModelEngineAPI.getNMSHandler().getEntityHandler();
                entityHandler.forceUseItem((org.bukkit.entity.Player)craftPlayer, org.bukkit.inventory.EquipmentSlot.HAND);
                entityHandler.forceUseItem((org.bukkit.entity.Player)craftPlayer, org.bukkit.inventory.EquipmentSlot.OFF_HAND);
            }

            public void onInteraction(InteractionHand hand, Vec3 pos) {
            }

            public void onAttack() {
            }
        });
        if (entity == player && !player.isSpectator()) {
            return;
        }
        player.resetLastActionTime();
        player.setShiftKeyDown(interactPacket.isUsingSecondaryAction());
        final ActiveModel activeModel = ModelEngineAPI.getInteractionTracker().getModelRelay(interactPacket.getOriginalId());
        if (activeModel != null) {
            final BaseEntity<?> base = activeModel.getModeledEntity().getBase();
            interactPacket.dispatch(new ServerboundInteractPacket.Handler(){

                public void onInteraction(InteractionHand hand) {
                    craftServer.getPluginManager().callEvent((Event)new BaseEntityInteractEvent((org.bukkit.entity.Player)craftPlayer, base, activeModel, BaseEntityInteractEvent.Action.INTERACT, org.bukkit.inventory.EquipmentSlot.HAND, interactPacket.isUsingSecondaryAction(), craftPlayer.getInventory().getItem(org.bukkit.inventory.EquipmentSlot.HAND), null));
                    craftServer.getPluginManager().callEvent((Event)new BaseEntityInteractEvent((org.bukkit.entity.Player)craftPlayer, base, activeModel, BaseEntityInteractEvent.Action.INTERACT, org.bukkit.inventory.EquipmentSlot.OFF_HAND, interactPacket.isUsingSecondaryAction(), craftPlayer.getInventory().getItem(org.bukkit.inventory.EquipmentSlot.OFF_HAND), null));
                }

                public void onInteraction(InteractionHand hand, Vec3 pos) {
                    Vector position = new Vector(pos.x, pos.y, pos.z);
                    craftServer.getPluginManager().callEvent((Event)new BaseEntityInteractEvent((org.bukkit.entity.Player)craftPlayer, base, activeModel, BaseEntityInteractEvent.Action.INTERACT_ON, org.bukkit.inventory.EquipmentSlot.HAND, interactPacket.isUsingSecondaryAction(), craftPlayer.getInventory().getItem(org.bukkit.inventory.EquipmentSlot.HAND), position));
                    craftServer.getPluginManager().callEvent((Event)new BaseEntityInteractEvent((org.bukkit.entity.Player)craftPlayer, base, activeModel, BaseEntityInteractEvent.Action.INTERACT_ON, org.bukkit.inventory.EquipmentSlot.OFF_HAND, interactPacket.isUsingSecondaryAction(), craftPlayer.getInventory().getItem(org.bukkit.inventory.EquipmentSlot.OFF_HAND), position));
                }

                public void onAttack() {
                    craftServer.getPluginManager().callEvent((Event)new BaseEntityInteractEvent((org.bukkit.entity.Player)craftPlayer, base, activeModel, BaseEntityInteractEvent.Action.ATTACK, org.bukkit.inventory.EquipmentSlot.HAND, interactPacket.isUsingSecondaryAction(), craftPlayer.getInventory().getItem(org.bukkit.inventory.EquipmentSlot.HAND), null));
                }
            });
        }
        if (entity == null) {
            if (ServerInfo.IS_PAPER) {
                interactPacket.dispatch(new ServerboundInteractPacket.Handler(){

                    public void onInteraction(InteractionHand hand) {
                        PatchedServerGamePacketListener.callPlayerUseUnknownEntityEvent(craftServer, craftPlayer, interactPacket, hand, null);
                    }

                    public void onInteraction(InteractionHand hand, Vec3 pos) {
                        PatchedServerGamePacketListener.callPlayerUseUnknownEntityEvent(craftServer, craftPlayer, interactPacket, hand, pos);
                    }

                    public void onAttack() {
                        PatchedServerGamePacketListener.callPlayerUseUnknownEntityEvent(craftServer, craftPlayer, interactPacket, InteractionHand.MAIN_HAND, null);
                    }
                });
            }
            return;
        }
        if (!serverLevel.getWorldBorder().isWithinBounds(entity.blockPosition())) {
            return;
        }
        AABB boundingBox = entity.getBoundingBox();
        if (boundingBox.distanceToSqr(player.getEyePosition()) >= player.entityInteractionRange() * player.entityInteractionRange()) {
            return;
        }
        interactPacket.dispatch(new ServerboundInteractPacket.Handler(){

            private void performInteraction(InteractionHand enumhand, EntityInteraction entityInteraction, PlayerInteractEntityEvent event) {
                ItemStack itemstack = player.getItemInHand(enumhand);
                if (itemstack.isItemEnabled(serverLevel.enabledFeatures())) {
                    ItemStack itemstack1 = itemstack.copy();
                    ItemStack itemInHand = player.getItemInHand(enumhand);
                    boolean triggerLeashUpdate = itemInHand.getItem() == Items.LEAD && entity instanceof Mob;
                    Item origItem = player.getInventory().getSelected().getItem();
                    craftServer.getPluginManager().callEvent((Event)event);
                    if (entity instanceof Bucketable && entity instanceof LivingEntity && origItem.asItem() == Items.WATER_BUCKET && (event.isCancelled() || player.getInventory().getSelected().getItem() != origItem)) {
                        if (entity.tracker != null && player.getBukkitEntity().canSee((Entity)entity.getBukkitEntity())) {
                            ServerEntity serverEntity = entity.tracker.serverEntity;
                            ArrayList list = new ArrayList();
                            serverEntity.sendPairingData(player, list::add);
                            player.connection.send((Packet)new ClientboundBundlePacket(list));
                        }
                        player.containerMenu.sendAllDataToRemote();
                    }
                    if (triggerLeashUpdate && (event.isCancelled() || player.getInventory().getSelected().getItem() != origItem)) {
                        connection.send((Packet)new ClientboundSetEntityLinkPacket(entity, ((Mob)entity).getLeashHolder()));
                    }
                    if (event.isCancelled() || player.getInventory().getSelected().getItem() != origItem) {
                        List data = entity.getEntityData().packAll();
                        if (data != null && player.getBukkitEntity().canSee((Entity)entity.getBukkitEntity())) {
                            player.connection.send((Packet)new ClientboundSetEntityDataPacket(entity.getId(), data));
                        }
                        if (entity instanceof Allay) {
                            Allay allay = (Allay)entity;
                            List equipment = Arrays.stream(EquipmentSlot.values()).map(slot -> Pair.of((Object)slot, (Object)allay.getItemBySlot(slot))).collect(Collectors.toList());
                            connection.send((Packet)new ClientboundSetEquipmentPacket(entity.getId(), equipment));
                            player.containerMenu.sendAllDataToRemote();
                        }
                    }
                    if (event.isCancelled()) {
                        player.containerMenu.sendAllDataToRemote();
                        return;
                    }
                    InteractionResult enuminteractionresult = entityInteraction.run(player, entity, enumhand);
                    if (!itemInHand.isEmpty() && itemInHand.getCount() <= -1) {
                        player.containerMenu.sendAllDataToRemote();
                    }
                    if (enuminteractionresult.consumesAction()) {
                        CriteriaTriggers.PLAYER_INTERACTED_WITH_ENTITY.trigger(player, itemstack1, entity);
                        if (enuminteractionresult.shouldSwing()) {
                            player.swing(enumhand, true);
                        }
                    }
                }
            }

            public void onInteraction(@NotNull InteractionHand hand) {
                this.performInteraction(hand, Player::interactOn, new PlayerInteractEntityEvent((org.bukkit.entity.Player)connection.getCraftPlayer(), (Entity)entity.getBukkitEntity(), hand == InteractionHand.OFF_HAND ? org.bukkit.inventory.EquipmentSlot.OFF_HAND : org.bukkit.inventory.EquipmentSlot.HAND));
            }

            public void onInteraction(@NotNull InteractionHand hand, @NotNull Vec3 pos) {
                this.performInteraction(hand, (entityplayer, entity1, enumhand1) -> entity1.interactAt((Player)entityplayer, pos, enumhand1), (PlayerInteractEntityEvent)new PlayerInteractAtEntityEvent((org.bukkit.entity.Player)connection.getCraftPlayer(), (Entity)entity.getBukkitEntity(), new Vector(pos.x, pos.y, pos.z), hand == InteractionHand.OFF_HAND ? org.bukkit.inventory.EquipmentSlot.OFF_HAND : org.bukkit.inventory.EquipmentSlot.HAND));
            }

            public void onAttack() {
                ItemStack itemstack;
                if (!(entity instanceof ItemEntity || entity instanceof ExperienceOrb || entity instanceof AbstractArrow || entity == player && !player.isSpectator() || !(itemstack = player.getItemInHand(InteractionHand.MAIN_HAND)).isItemEnabled(serverLevel.enabledFeatures()))) {
                    player.attack(entity);
                    if (!itemstack.isEmpty() && itemstack.getCount() <= -1) {
                        player.containerMenu.sendAllDataToRemote();
                    }
                }
            }
        });
    }

    public static void handleUseItem(ServerboundUseItemPacket packet, ServerGamePacketListener listener, Consumer<InteractionResult> afterUse) {
        if (!(listener instanceof ServerGamePacketListenerImpl)) {
            return;
        }
        ServerGamePacketListenerImpl connection = (ServerGamePacketListenerImpl)listener;
        ServerPlayer player = connection.getPlayer();
        PacketUtils.ensureRunningOnSameThread((Packet)packet, (PacketListener)connection, (ServerLevel)player.serverLevel());
        if (player.isImmobile()) {
            return;
        }
        boolean shouldLimit = Boolean.TRUE.equals(ReflectionUtils.call(connection, NMSMethods.SERVER_GAME_PACKET_LISTENER_IMPL_checkLimit, packet.timestamp));
        if (!shouldLimit) {
            return;
        }
        connection.ackBlockChangesUpTo(packet.getSequence());
        ServerLevel worldserver = player.serverLevel();
        InteractionHand enumhand = packet.getHand();
        ItemStack itemstack = player.getItemInHand(enumhand);
        player.resetLastActionTime();
        if (!itemstack.isEmpty() && itemstack.isItemEnabled(worldserver.enabledFeatures())) {
            boolean cancelled;
            float f1 = player.getXRot();
            float f2 = player.getYRot();
            double d0 = player.getX();
            double d1 = player.getY() + (double)player.getEyeHeight();
            double d2 = player.getZ();
            Vec3 vec3d = new Vec3(d0, d1, d2);
            float f3 = Mth.cos((float)(-f2 * ((float)Math.PI / 180) - (float)Math.PI));
            float f4 = Mth.sin((float)(-f2 * ((float)Math.PI / 180) - (float)Math.PI));
            float f5 = -Mth.cos((float)(-f1 * ((float)Math.PI / 180)));
            float f6 = Mth.sin((float)(-f1 * ((float)Math.PI / 180)));
            float f7 = f4 * f5;
            float f8 = f3 * f5;
            double d3 = player.gameMode.getGameModeForPlayer() == GameType.CREATIVE ? 5.0 : 4.5;
            Vec3 vec3d1 = vec3d.add((double)f7 * d3, (double)f6 * d3, (double)f8 * d3);
            BlockHitResult movingobjectposition = player.level().clip(new ClipContext(vec3d, vec3d1, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, (net.minecraft.world.entity.Entity)player));
            if (movingobjectposition == null || movingobjectposition.getType() != HitResult.Type.BLOCK) {
                PlayerInteractEvent event = CraftEventFactory.callPlayerInteractEvent((Player)player, (Action)Action.RIGHT_CLICK_AIR, (ItemStack)itemstack, (InteractionHand)enumhand);
                cancelled = event.useItemInHand() == Event.Result.DENY;
            } else {
                PlayerInteractEvent event;
                BlockHitResult movingobjectpositionblock = movingobjectposition;
                cancelled = player.gameMode.firedInteract && player.gameMode.interactPosition.equals((Object)movingobjectpositionblock.getBlockPos()) && player.gameMode.interactHand == enumhand && ItemStack.isSameItemSameComponents((ItemStack)player.gameMode.interactItemStack, (ItemStack)itemstack) ? player.gameMode.interactResult : (event = CraftEventFactory.callPlayerInteractEvent((Player)player, (Action)Action.RIGHT_CLICK_BLOCK, (BlockPos)movingobjectpositionblock.getBlockPos(), (Direction)movingobjectpositionblock.getDirection(), (ItemStack)itemstack, (boolean)true, (InteractionHand)enumhand, (Vec3)movingobjectpositionblock.getLocation())).useItemInHand() == Event.Result.DENY;
                player.gameMode.firedInteract = false;
            }
            if (cancelled) {
                player.resyncUsingItem(player);
                player.getBukkitEntity().updateInventory();
                return;
            }
            itemstack = player.getItemInHand(enumhand);
            if (itemstack.isEmpty()) {
                return;
            }
            InteractionResult enuminteractionresult = player.gameMode.useItem(player, (Level)worldserver, itemstack, enumhand);
            afterUse.accept((Object)enuminteractionresult);
            if (enuminteractionresult.shouldSwing()) {
                player.swing(enumhand, true);
            }
        }
    }

    private static void callPlayerUseUnknownEntityEvent(CraftServer cserver, CraftPlayer craftPlayer, ServerboundInteractPacketWrapper packet, InteractionHand hand, @Nullable Vec3 vector) {
        cserver.getPluginManager().callEvent((Event)new PlayerUseUnknownEntityEvent((org.bukkit.entity.Player)craftPlayer, packet.getEntityId(), packet.isAttack(), hand == InteractionHand.MAIN_HAND ? org.bukkit.inventory.EquipmentSlot.HAND : org.bukkit.inventory.EquipmentSlot.OFF_HAND, vector != null ? new Vector(vector.x, vector.y, vector.z) : null));
    }

    @FunctionalInterface
    private static interface EntityInteraction {
        public InteractionResult run(ServerPlayer var1, net.minecraft.world.entity.Entity var2, InteractionHand var3);
    }
}

