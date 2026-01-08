/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.channel.ChannelDuplexHandler
 *  io.netty.channel.ChannelHandlerContext
 *  io.netty.channel.ChannelPromise
 *  it.unimi.dsi.fastutil.Pair
 *  it.unimi.dsi.fastutil.ints.IntArrayList
 *  it.unimi.dsi.fastutil.ints.IntList
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.network.protocol.Packet
 *  net.minecraft.network.protocol.common.ClientboundKeepAlivePacket
 *  net.minecraft.network.protocol.common.ServerboundPongPacket
 *  net.minecraft.network.protocol.game.ClientGamePacketListener
 *  net.minecraft.network.protocol.game.ClientboundAddEntityPacket
 *  net.minecraft.network.protocol.game.ClientboundAnimatePacket
 *  net.minecraft.network.protocol.game.ClientboundBundlePacket
 *  net.minecraft.network.protocol.game.ClientboundEntityEventPacket
 *  net.minecraft.network.protocol.game.ClientboundMoveEntityPacket$Pos
 *  net.minecraft.network.protocol.game.ClientboundMoveEntityPacket$PosRot
 *  net.minecraft.network.protocol.game.ClientboundMoveEntityPacket$Rot
 *  net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket
 *  net.minecraft.network.protocol.game.ClientboundRemoveMobEffectPacket
 *  net.minecraft.network.protocol.game.ClientboundRotateHeadPacket
 *  net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket
 *  net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket
 *  net.minecraft.network.protocol.game.ClientboundSetEquipmentPacket
 *  net.minecraft.network.protocol.game.ClientboundSetPassengersPacket
 *  net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket
 *  net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket
 *  net.minecraft.network.protocol.game.ServerGamePacketListener
 *  net.minecraft.network.protocol.game.ServerboundClientTickEndPacket
 *  net.minecraft.network.protocol.game.ServerboundInteractPacket
 *  net.minecraft.network.protocol.game.ServerboundMovePlayerPacket
 *  net.minecraft.network.protocol.game.ServerboundMovePlayerPacket$Pos
 *  net.minecraft.network.protocol.game.ServerboundMovePlayerPacket$PosRot
 *  net.minecraft.network.protocol.game.ServerboundPlayerInputPacket
 *  net.minecraft.network.syncher.EntityDataSerializers
 *  net.minecraft.network.syncher.SynchedEntityData$DataValue
 *  net.minecraft.server.level.ServerPlayer
 *  org.bukkit.craftbukkit.entity.CraftPlayer
 *  org.bukkit.entity.Player
 *  org.jetbrains.annotations.NotNull
 */
package com.ticxo.modelengine.v1_21_R4.network;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.interaction.DynamicHitbox;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.ModelUpdaters;
import com.ticxo.modelengine.api.model.ModeledEntity;
import com.ticxo.modelengine.api.model.bone.BoneBehaviorTypes;
import com.ticxo.modelengine.api.model.bone.behavior.BoneBehavior;
import com.ticxo.modelengine.api.model.bone.render.BehaviorRenderer;
import com.ticxo.modelengine.api.model.bone.render.renderer.MountRenderer;
import com.ticxo.modelengine.api.model.bone.type.Mount;
import com.ticxo.modelengine.api.mount.controller.MountController;
import com.ticxo.modelengine.api.nms.entity.EntityHandler;
import com.ticxo.modelengine.api.nms.network.CancelType;
import com.ticxo.modelengine.api.nms.network.ClientDesyncMonitor;
import com.ticxo.modelengine.api.nms.network.PipelineWrapper;
import com.ticxo.modelengine.api.utils.config.ConfigProperty;
import com.ticxo.modelengine.api.utils.data.tracker.CollectionDataTracker;
import com.ticxo.modelengine.api.utils.math.TMath;
import com.ticxo.modelengine.v1_21_R4.entity.EntityContainer;
import com.ticxo.modelengine.v1_21_R4.network.patch.ServerboundInteractPacketWrapper;
import com.ticxo.modelengine.v1_21_R4.network.utils.NetworkUtils;
import com.ticxo.modelengine.v1_21_R4.network.utils.PacketInterceptor;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientboundKeepAlivePacket;
import net.minecraft.network.protocol.common.ServerboundPongPacket;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundAnimatePacket;
import net.minecraft.network.protocol.game.ClientboundBundlePacket;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import net.minecraft.network.protocol.game.ClientboundMoveEntityPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveMobEffectPacket;
import net.minecraft.network.protocol.game.ClientboundRotateHeadPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.network.protocol.game.ClientboundSetEquipmentPacket;
import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket;
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket;
import net.minecraft.network.protocol.game.ServerGamePacketListener;
import net.minecraft.network.protocol.game.ServerboundClientTickEndPacket;
import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerInputPacket;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ModelEngineChannelHandler
extends ChannelDuplexHandler {
    private final Player player;
    private final ServerPlayer serverPlayer;
    private final ModelUpdaters updaters;
    private final EntityHandler entityHandler;
    private final ClientDesyncMonitor desyncMonitor;
    private final PacketInterceptor<ClientGamePacketListener> writeInterceptors;
    private final PacketInterceptor<ServerGamePacketListener> readInterceptors;

    public ModelEngineChannelHandler(Player player, PipelineWrapper pipeline) {
        this.player = player;
        this.serverPlayer = ((CraftPlayer)player).getHandle();
        this.updaters = ModelEngineAPI.getAPI().getModelUpdaters();
        this.entityHandler = ModelEngineAPI.getEntityHandler();
        this.desyncMonitor = pipeline.getDesyncMonitor();
        this.writeInterceptors = new PacketInterceptor();
        this.writeInterceptors.register(ClientboundAddEntityPacket.class, this::handleAddEntity).register(ClientboundRemoveEntitiesPacket.class, this::handleRemoveEntities).register(ClientboundMoveEntityPacket.Pos.class, this::handleEntityId).register(ClientboundMoveEntityPacket.Rot.class, this::handleEntityId).register(ClientboundMoveEntityPacket.PosRot.class, this::handleEntityId).register(ClientboundRotateHeadPacket.class, this::handleEntityId).register(ClientboundEntityEventPacket.class, this::handleEntityId).register(ClientboundSetEntityMotionPacket.class, this::handleEntityMotion).register(ClientboundTeleportEntityPacket.class, this::handleTeleportEntity).register(ClientboundAnimatePacket.class, this::handleAnimate).register(ClientboundSetEntityDataPacket.class, this::handleEntityData).register(ClientboundSetEquipmentPacket.class, this::handleSetEquipment).register(ClientboundRemoveMobEffectPacket.class, this::handleRemoveMobEffect).register(ClientboundUpdateMobEffectPacket.class, this::handleUpdateMobEffect).register(ClientboundKeepAlivePacket.class, this::handleKeepAlive).registerPost(ClientboundAddEntityPacket.class, this::handleAddEntityPost);
        this.readInterceptors = new PacketInterceptor();
        this.readInterceptors.register(ServerboundInteractPacket.class, this::handleInteract).register(ServerboundPlayerInputPacket.class, this::handlePlayerInput).register(ServerboundPongPacket.class, this::handlePong).register(ServerboundMovePlayerPacket.Pos.class, this::handlePlayerMove).register(ServerboundMovePlayerPacket.PosRot.class, this::handlePlayerMove).register(ServerboundClientTickEndPacket.class, this::handleClientTickEnd);
    }

    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (!(msg instanceof Packet)) {
            super.write(ctx, msg, promise);
            return;
        }
        ClientboundBundlePacket packet = (ClientboundBundlePacket)msg;
        try {
            if (packet instanceof ClientboundBundlePacket) {
                ClientboundBundlePacket bundle = packet;
                ArrayList<Packet<ClientGamePacketListener>> list = new ArrayList<Packet<ClientGamePacketListener>>();
                for (Packet subPacket : bundle.subPackets()) {
                    Packet<ClientGamePacketListener> result = this.writeInterceptors.accept((Packet<ClientGamePacketListener>)subPacket);
                    if (result == null) continue;
                    list.add(result);
                    list.addAll(this.writeInterceptors.acceptPost(result));
                }
                if (!list.isEmpty()) {
                    packet = new ClientboundBundlePacket(list);
                    super.write(ctx, (Object)packet, promise);
                }
            } else {
                if ((packet = this.writeInterceptors.accept((Packet<ClientGamePacketListener>)packet)) == null) {
                    return;
                }
                ArrayList<Object> list = new ArrayList<Object>();
                list.add(packet);
                list.addAll(this.writeInterceptors.acceptPost((Packet<ClientGamePacketListener>)packet));
                if (list.size() == 1) {
                    super.write(ctx, (Object)packet, promise);
                } else {
                    packet = new ClientboundBundlePacket(list);
                    super.write(ctx, (Object)packet, promise);
                }
            }
        }
        catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public void channelRead(@NotNull ChannelHandlerContext ctx, @NotNull Object msg) throws Exception {
        if (!(msg instanceof Packet)) {
            super.channelRead(ctx, msg);
            return;
        }
        Packet<ServerGamePacketListener> packet = this.readInterceptors.accept((Packet<ServerGamePacketListener>)((Packet)msg));
        if (packet == null) {
            return;
        }
        super.channelRead(ctx, packet);
        this.readInterceptors.acceptPost(packet);
    }

    private ClientboundAddEntityPacket handleAddEntity(ClientboundAddEntityPacket packet) {
        return ModelEngineAPI.shouldShow(this.player, packet.getId()) != CancelType.HIDE ? packet : null;
    }

    private List<Packet<? super ClientGamePacketListener>> handleAddEntityPost(ClientboundAddEntityPacket packet) {
        return this.handleMount(packet.getUUID());
    }

    private List<Packet<? super ClientGamePacketListener>> handleMount(UUID uuid) {
        BehaviorRenderer behaviorRenderer;
        Pair<ActiveModel, MountController> pair = ModelEngineAPI.getMountPairManager().get(uuid);
        if (pair == null) {
            return null;
        }
        ActiveModel model = (ActiveModel)pair.left();
        ArrayList<ClientboundSetPassengersPacket> list = null;
        Optional<BehaviorRenderer> maybeMount = model.getBehaviorRenderer(BoneBehaviorTypes.MOUNT);
        if (maybeMount.isPresent() && (behaviorRenderer = maybeMount.get()) instanceof MountRenderer) {
            MountRenderer renderer = (MountRenderer)behaviorRenderer;
            MountController controller = (MountController)pair.right();
            Mount mount = controller.getMount();
            if (mount instanceof BoneBehavior) {
                BoneBehavior behavior = (BoneBehavior)((Object)mount);
                MountRenderer.Mount mount2 = (MountRenderer.Mount)renderer.getRendered().get(behavior.getBone().getBoneId());
                if (mount2 == null) {
                    return null;
                }
                CollectionDataTracker<Integer> ids = mount2.getPassengers();
                list = new ArrayList<ClientboundSetPassengersPacket>();
                list.add(new ClientboundSetPassengersPacket(EntityContainer.of(mount2.getMountId(), ids)));
            }
        }
        return list;
    }

    private ClientboundRemoveEntitiesPacket handleRemoveEntities(ClientboundRemoveEntitiesPacket packet) {
        int[] set = packet.getEntityIds().intStream().filter(id -> ModelEngineAPI.shouldShow(this.player, id) != CancelType.HIDE).toArray();
        if (set.length == packet.getEntityIds().size()) {
            return packet;
        }
        return new ClientboundRemoveEntitiesPacket((IntList)IntArrayList.wrap((int[])set));
    }

    private <T extends Packet<? super ClientGamePacketListener>> T handleEntityId(T packet) {
        FriendlyByteBuf buf = NetworkUtils.readClientbound(packet);
        int id = buf.readVarInt();
        return (T)(ModelEngineAPI.shouldShow(this.player, id) != CancelType.HIDE ? packet : null);
    }

    private ClientboundSetEntityMotionPacket handleEntityMotion(ClientboundSetEntityMotionPacket packet) {
        return ModelEngineAPI.shouldShow(this.player, packet.getId()) != CancelType.HIDE ? packet : null;
    }

    private ClientboundTeleportEntityPacket handleTeleportEntity(ClientboundTeleportEntityPacket packet) {
        return ModelEngineAPI.shouldShow(this.player, packet.id()) != CancelType.HIDE ? packet : null;
    }

    private ClientboundAnimatePacket handleAnimate(ClientboundAnimatePacket packet) {
        return ModelEngineAPI.shouldShow(this.player, packet.getId()) != CancelType.HIDE ? packet : null;
    }

    private ClientboundSetEntityDataPacket handleEntityData(ClientboundSetEntityDataPacket packet) {
        CancelType type = ModelEngineAPI.shouldShow(this.player, packet.id());
        if (type == CancelType.HIDE) {
            return null;
        }
        if (packet.id() != this.player.getEntityId() && type == CancelType.SHOW) {
            return packet;
        }
        ModeledEntity modeledEntity = this.updaters.getModeledEntity(packet.id());
        if (modeledEntity == null) {
            return packet;
        }
        if (this.entityHandler.isForcedInvisible(modeledEntity.getBase().getUUID())) {
            ArrayList<SynchedEntityData.DataValue> entityData = new ArrayList<SynchedEntityData.DataValue>();
            RegistryFriendlyByteBuf buf = NetworkUtils.createByteBuf();
            buf.writeVarInt(packet.id());
            for (SynchedEntityData.DataValue item : packet.packedItems()) {
                if (item.id() == 0) {
                    byte data = (Byte)item.value();
                    data = TMath.setBit(data, 5, true);
                    entityData.add(new SynchedEntityData.DataValue(0, EntityDataSerializers.BYTE, (Object)data));
                    continue;
                }
                entityData.add(item);
            }
            buf.writeByte(255);
            packet = new ClientboundSetEntityDataPacket(packet.id(), entityData);
        }
        return packet;
    }

    private ClientboundSetEquipmentPacket handleSetEquipment(ClientboundSetEquipmentPacket packet) {
        return ModelEngineAPI.shouldShow(this.player, packet.getEntity()) == CancelType.SHOW ? packet : null;
    }

    private ClientboundRemoveMobEffectPacket handleRemoveMobEffect(ClientboundRemoveMobEffectPacket packet) {
        int id = packet.entityId();
        return ModelEngineAPI.shouldShow(this.player, id) != CancelType.HIDE ? packet : null;
    }

    private ClientboundUpdateMobEffectPacket handleUpdateMobEffect(ClientboundUpdateMobEffectPacket packet) {
        return ModelEngineAPI.shouldShow(this.player, packet.getEntityId()) != CancelType.HIDE ? packet : null;
    }

    private ClientboundKeepAlivePacket handleKeepAlive(ClientboundKeepAlivePacket packet) {
        if (this.desyncMonitor.clientTickShifted() || this.desyncMonitor.shouldRetest()) {
            this.desyncMonitor.startTest();
        }
        return packet;
    }

    private Packet<? super ServerGamePacketListener> handleInteract(ServerboundInteractPacket packet) {
        DynamicHitbox hitbox;
        FriendlyByteBuf buf = NetworkUtils.readServerbound((Packet<? super ServerGamePacketListener>)packet);
        int entityId = buf.readVarInt();
        int action = buf.readVarInt();
        if (entityId == DynamicHitbox.getHitboxId() && (hitbox = ModelEngineAPI.getInteractionTracker().getDynamicHitbox(this.player.getUniqueId())) != null) {
            return new ServerboundInteractPacketWrapper(entityId, hitbox.getTarget(), action, packet);
        }
        ActiveModel activeModel = ModelEngineAPI.getInteractionTracker().getModelRelay(entityId);
        if (activeModel != null) {
            ModeledEntity modeledEntity = activeModel.getModeledEntity();
            if (modeledEntity == null) {
                return packet;
            }
            int id = modeledEntity.getBase().getEntityId();
            return new ServerboundInteractPacketWrapper(entityId, id, action, packet);
        }
        Integer relayed = ModelEngineAPI.getInteractionTracker().getEntityRelay(entityId);
        if (relayed != null) {
            return new ServerboundInteractPacketWrapper(entityId, relayed, action, packet);
        }
        return packet;
    }

    private ServerboundPlayerInputPacket handlePlayerInput(ServerboundPlayerInputPacket inputPacket) {
        MountController controller = ModelEngineAPI.getMountPairManager().getController(this.player.getUniqueId());
        if (controller != null) {
            MountController.MountInput input = controller.getInput();
            if (input == null) {
                controller.setInput(new MountController.MountInput(inputPacket.input().forward(), inputPacket.input().backward(), inputPacket.input().left(), inputPacket.input().right(), inputPacket.input().jump(), inputPacket.input().shift(), inputPacket.input().sprint()));
            } else {
                input.setForward(inputPacket.input().forward());
                input.setBackward(inputPacket.input().backward());
                input.setLeft(inputPacket.input().left());
                input.setRight(inputPacket.input().right());
                input.setJump(inputPacket.input().jump());
                input.setSneak(inputPacket.input().shift());
                input.setSprint(inputPacket.input().sprint());
            }
        }
        return inputPacket;
    }

    private ServerboundPongPacket handlePong(ServerboundPongPacket packet) {
        this.desyncMonitor.recordPongTime(System.currentTimeMillis());
        return null;
    }

    private ServerboundMovePlayerPacket handlePlayerMove(ServerboundMovePlayerPacket packet) {
        if (!ConfigProperty.SYNC_CLIENT_TICK_END.getBoolean()) {
            this.desyncMonitor.recordClientSyncTime(System.currentTimeMillis());
        }
        return packet;
    }

    private ServerboundClientTickEndPacket handleClientTickEnd(ServerboundClientTickEndPacket packet) {
        if (ConfigProperty.SYNC_CLIENT_TICK_END.getBoolean()) {
            this.desyncMonitor.recordClientSyncTime(System.currentTimeMillis());
        }
        return packet;
    }
}

