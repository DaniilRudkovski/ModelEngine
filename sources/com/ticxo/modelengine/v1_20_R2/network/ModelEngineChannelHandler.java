/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.channel.ChannelDuplexHandler
 *  io.netty.channel.ChannelHandlerContext
 *  io.netty.channel.ChannelPromise
 *  it.unimi.dsi.fastutil.Pair
 *  net.minecraft.network.PacketDataSerializer
 *  net.minecraft.network.PacketListener
 *  net.minecraft.network.protocol.Packet
 *  net.minecraft.network.protocol.common.ClientboundKeepAlivePacket
 *  net.minecraft.network.protocol.common.ServerboundPongPacket
 *  net.minecraft.network.protocol.game.ClientboundBundlePacket
 *  net.minecraft.network.protocol.game.PacketPlayInFlying
 *  net.minecraft.network.protocol.game.PacketPlayInFlying$PacketPlayInPosition
 *  net.minecraft.network.protocol.game.PacketPlayInFlying$PacketPlayInPositionLook
 *  net.minecraft.network.protocol.game.PacketPlayInSteerVehicle
 *  net.minecraft.network.protocol.game.PacketPlayInUseEntity
 *  net.minecraft.network.protocol.game.PacketPlayOutAnimation
 *  net.minecraft.network.protocol.game.PacketPlayOutEntity$PacketPlayOutEntityLook
 *  net.minecraft.network.protocol.game.PacketPlayOutEntity$PacketPlayOutRelEntityMove
 *  net.minecraft.network.protocol.game.PacketPlayOutEntity$PacketPlayOutRelEntityMoveLook
 *  net.minecraft.network.protocol.game.PacketPlayOutEntityDestroy
 *  net.minecraft.network.protocol.game.PacketPlayOutEntityEffect
 *  net.minecraft.network.protocol.game.PacketPlayOutEntityEquipment
 *  net.minecraft.network.protocol.game.PacketPlayOutEntityHeadRotation
 *  net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata
 *  net.minecraft.network.protocol.game.PacketPlayOutEntityStatus
 *  net.minecraft.network.protocol.game.PacketPlayOutEntityTeleport
 *  net.minecraft.network.protocol.game.PacketPlayOutEntityVelocity
 *  net.minecraft.network.protocol.game.PacketPlayOutMount
 *  net.minecraft.network.protocol.game.PacketPlayOutRemoveEntityEffect
 *  net.minecraft.network.protocol.game.PacketPlayOutSpawnEntity
 *  net.minecraft.network.syncher.DataWatcher$b
 *  net.minecraft.network.syncher.DataWatcherRegistry
 *  net.minecraft.server.level.EntityPlayer
 *  org.bukkit.craftbukkit.v1_20_R2.entity.CraftPlayer
 *  org.bukkit.entity.Player
 *  org.jetbrains.annotations.NotNull
 */
package com.ticxo.modelengine.v1_20_R2.network;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.interaction.DynamicHitbox;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.ModelUpdaters;
import com.ticxo.modelengine.api.model.ModeledEntity;
import com.ticxo.modelengine.api.model.bone.BoneBehaviorTypes;
import com.ticxo.modelengine.api.model.bone.behavior.BoneBehavior;
import com.ticxo.modelengine.api.model.bone.render.renderer.MountRenderer;
import com.ticxo.modelengine.api.model.bone.type.Mount;
import com.ticxo.modelengine.api.mount.controller.MountController;
import com.ticxo.modelengine.api.nms.entity.EntityHandler;
import com.ticxo.modelengine.api.nms.network.CancelType;
import com.ticxo.modelengine.api.nms.network.ClientDesyncMonitor;
import com.ticxo.modelengine.api.nms.network.PipelineWrapper;
import com.ticxo.modelengine.api.utils.data.tracker.CollectionDataTracker;
import com.ticxo.modelengine.api.utils.math.TMath;
import com.ticxo.modelengine.v1_20_R2.network.patch.ServerboundInteractPacketWrapper;
import com.ticxo.modelengine.v1_20_R2.network.utils.NetworkUtils;
import com.ticxo.modelengine.v1_20_R2.network.utils.PacketInterceptor;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import it.unimi.dsi.fastutil.Pair;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientboundKeepAlivePacket;
import net.minecraft.network.protocol.common.ServerboundPongPacket;
import net.minecraft.network.protocol.game.ClientboundBundlePacket;
import net.minecraft.network.protocol.game.PacketPlayInFlying;
import net.minecraft.network.protocol.game.PacketPlayInSteerVehicle;
import net.minecraft.network.protocol.game.PacketPlayInUseEntity;
import net.minecraft.network.protocol.game.PacketPlayOutAnimation;
import net.minecraft.network.protocol.game.PacketPlayOutEntity;
import net.minecraft.network.protocol.game.PacketPlayOutEntityDestroy;
import net.minecraft.network.protocol.game.PacketPlayOutEntityEffect;
import net.minecraft.network.protocol.game.PacketPlayOutEntityEquipment;
import net.minecraft.network.protocol.game.PacketPlayOutEntityHeadRotation;
import net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata;
import net.minecraft.network.protocol.game.PacketPlayOutEntityStatus;
import net.minecraft.network.protocol.game.PacketPlayOutEntityTeleport;
import net.minecraft.network.protocol.game.PacketPlayOutEntityVelocity;
import net.minecraft.network.protocol.game.PacketPlayOutMount;
import net.minecraft.network.protocol.game.PacketPlayOutRemoveEntityEffect;
import net.minecraft.network.protocol.game.PacketPlayOutSpawnEntity;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.server.level.EntityPlayer;
import org.bukkit.craftbukkit.v1_20_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ModelEngineChannelHandler
extends ChannelDuplexHandler {
    private final Player player;
    private final EntityPlayer serverPlayer;
    private final ModelUpdaters updaters;
    private final EntityHandler entityHandler;
    private final ClientDesyncMonitor desyncMonitor;
    private final PacketInterceptor writeInterceptors;
    private final PacketInterceptor readInterceptors;

    public ModelEngineChannelHandler(Player player, PipelineWrapper pipeline) {
        this.player = player;
        this.serverPlayer = ((CraftPlayer)player).getHandle();
        this.updaters = ModelEngineAPI.getAPI().getModelUpdaters();
        this.entityHandler = ModelEngineAPI.getEntityHandler();
        this.desyncMonitor = pipeline.getDesyncMonitor();
        this.writeInterceptors = new PacketInterceptor();
        this.writeInterceptors.register(PacketPlayOutSpawnEntity.class, this::handleAddEntity).register(PacketPlayOutEntityDestroy.class, this::handleRemoveEntities).register(PacketPlayOutEntity.PacketPlayOutRelEntityMove.class, this::handleEntityId).register(PacketPlayOutEntity.PacketPlayOutEntityLook.class, this::handleEntityId).register(PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook.class, this::handleEntityId).register(PacketPlayOutEntityHeadRotation.class, this::handleEntityId).register(PacketPlayOutEntityStatus.class, this::handleEntityId).register(PacketPlayOutEntityVelocity.class, this::handleEntityMotion).register(PacketPlayOutEntityTeleport.class, this::handleTeleportEntity).register(PacketPlayOutAnimation.class, this::handleAnimate).register(PacketPlayOutEntityMetadata.class, this::handleEntityData).register(PacketPlayOutEntityEquipment.class, this::handleSetEquipment).register(PacketPlayOutRemoveEntityEffect.class, this::handleRemoveMobEffect).register(PacketPlayOutEntityEffect.class, this::handleUpdateMobEffect).register(ClientboundKeepAlivePacket.class, this::handleKeepAlive).registerPost(PacketPlayOutSpawnEntity.class, this::handleAddEntityPost);
        this.readInterceptors = new PacketInterceptor();
        this.readInterceptors.register(PacketPlayInUseEntity.class, this::handleInteract).register(PacketPlayInSteerVehicle.class, this::handlePlayerInput).register(ServerboundPongPacket.class, this::handlePong).register(PacketPlayInFlying.PacketPlayInPosition.class, this::handlePlayerMove).register(PacketPlayInFlying.PacketPlayInPositionLook.class, this::handlePlayerMove);
    }

    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (!(msg instanceof Packet)) {
            super.write(ctx, msg, promise);
            return;
        }
        Packet packet = (Packet)msg;
        try {
            if (packet instanceof ClientboundBundlePacket) {
                ClientboundBundlePacket bundle = (ClientboundBundlePacket)packet;
                ArrayList<Packet> list = new ArrayList<Packet>();
                for (Packet subPacket : bundle.a()) {
                    Packet result = this.writeInterceptors.accept(subPacket);
                    if (result == null) continue;
                    list.add(result);
                    for (Packet p : this.writeInterceptors.acceptPost(result)) {
                        list.add(p);
                    }
                }
                if (!list.isEmpty()) {
                    packet = new ClientboundBundlePacket(list);
                    super.write(ctx, (Object)packet, promise);
                }
            } else {
                if ((packet = this.writeInterceptors.accept(packet)) == null) {
                    return;
                }
                ArrayList<Packet> list = new ArrayList<Packet>();
                list.add(packet);
                for (Packet p : this.writeInterceptors.acceptPost(packet)) {
                    list.add(p);
                }
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
        Packet packet = this.readInterceptors.accept((Packet)msg);
        if (packet == null) {
            return;
        }
        super.channelRead(ctx, (Object)packet);
        this.readInterceptors.acceptPost(packet);
    }

    private PacketPlayOutSpawnEntity handleAddEntity(PacketPlayOutSpawnEntity packet) {
        return ModelEngineAPI.shouldShow(this.player, packet.a()) != CancelType.HIDE ? packet : null;
    }

    private List<Packet> handleAddEntityPost(PacketPlayOutSpawnEntity packet) {
        return this.handleMount(packet.d());
    }

    private List<Packet> handleMount(UUID uuid) {
        Pair<ActiveModel, MountController> pair = ModelEngineAPI.getMountPairManager().get(uuid);
        if (pair == null) {
            return null;
        }
        ActiveModel model = (ActiveModel)pair.left();
        ArrayList<Packet> list = new ArrayList<Packet>();
        model.getBehaviorRenderer(BoneBehaviorTypes.MOUNT).ifPresent(behaviorRenderer -> {
            if (behaviorRenderer instanceof MountRenderer) {
                MountRenderer renderer = (MountRenderer)behaviorRenderer;
                MountController controller = (MountController)pair.right();
                Mount patt6903$temp = controller.getMount();
                if (patt6903$temp instanceof BoneBehavior) {
                    BoneBehavior behavior = (BoneBehavior)((Object)patt6903$temp);
                    MountRenderer.Mount mount = (MountRenderer.Mount)renderer.getRendered().get(behavior.getBone().getBoneId());
                    CollectionDataTracker<Integer> ids = mount.getPassengers();
                    PacketDataSerializer buf = NetworkUtils.createByteBuf();
                    buf.c(mount.getMountId());
                    buf.c(ids.size());
                    ids.forEach(arg_0 -> ((PacketDataSerializer)buf).c(arg_0));
                    list.add((Packet)new PacketPlayOutMount(buf));
                }
            }
        });
        return list;
    }

    private PacketPlayOutEntityDestroy handleRemoveEntities(PacketPlayOutEntityDestroy packet) {
        int id;
        PacketDataSerializer buf = NetworkUtils.readData(packet);
        int size = buf.m();
        HashSet<Integer> set = new HashSet<Integer>(size);
        for (int i = 0; i < size; ++i) {
            id = buf.m();
            if (ModelEngineAPI.shouldShow(this.player, id) == CancelType.HIDE) continue;
            set.add(id);
        }
        if (set.size() == size) {
            return packet;
        }
        buf = NetworkUtils.createByteBuf();
        buf.c(set.size());
        Iterator iterator = set.iterator();
        while (iterator.hasNext()) {
            id = (Integer)iterator.next();
            buf.c(id);
        }
        return new PacketPlayOutEntityDestroy(buf);
    }

    private <U extends PacketListener, T extends Packet<U>> Packet<U> handleEntityId(T packet) {
        PacketDataSerializer buf = NetworkUtils.readData(packet);
        int id = buf.m();
        return ModelEngineAPI.shouldShow(this.player, id) != CancelType.HIDE ? packet : null;
    }

    private PacketPlayOutEntityVelocity handleEntityMotion(PacketPlayOutEntityVelocity packet) {
        return ModelEngineAPI.shouldShow(this.player, packet.a()) != CancelType.HIDE ? packet : null;
    }

    private PacketPlayOutEntityTeleport handleTeleportEntity(PacketPlayOutEntityTeleport packet) {
        return ModelEngineAPI.shouldShow(this.player, packet.a()) != CancelType.HIDE ? packet : null;
    }

    private PacketPlayOutAnimation handleAnimate(PacketPlayOutAnimation packet) {
        return ModelEngineAPI.shouldShow(this.player, packet.a()) != CancelType.HIDE ? packet : null;
    }

    private PacketPlayOutEntityMetadata handleEntityData(PacketPlayOutEntityMetadata packet) {
        CancelType type = ModelEngineAPI.shouldShow(this.player, packet.a());
        if (type == CancelType.HIDE) {
            return null;
        }
        if (packet.a() != this.player.getEntityId() && type == CancelType.SHOW) {
            return packet;
        }
        ModeledEntity modeledEntity = this.updaters.getModeledEntity(packet.a());
        if (modeledEntity == null) {
            return packet;
        }
        if (this.entityHandler.isForcedInvisible(modeledEntity.getBase().getUUID())) {
            ArrayList<DataWatcher.b> entityData = new ArrayList<DataWatcher.b>();
            PacketDataSerializer buf = NetworkUtils.createByteBuf();
            buf.c(packet.a());
            for (DataWatcher.b item : packet.d()) {
                if (item.a() == 0) {
                    byte data = (Byte)item.c();
                    data = TMath.setBit(data, 5, true);
                    entityData.add(new DataWatcher.b(0, DataWatcherRegistry.a, (Object)data));
                    continue;
                }
                entityData.add(item);
            }
            buf.k(255);
            packet = new PacketPlayOutEntityMetadata(packet.a(), entityData);
        }
        return packet;
    }

    private PacketPlayOutEntityEquipment handleSetEquipment(PacketPlayOutEntityEquipment packet) {
        return ModelEngineAPI.shouldShow(this.player, packet.a()) == CancelType.SHOW ? packet : null;
    }

    private PacketPlayOutRemoveEntityEffect handleRemoveMobEffect(PacketPlayOutRemoveEntityEffect packet) {
        PacketDataSerializer buf = NetworkUtils.readData(packet);
        int id = buf.m();
        return ModelEngineAPI.shouldShow(this.player, id) != CancelType.HIDE ? packet : null;
    }

    private PacketPlayOutEntityEffect handleUpdateMobEffect(PacketPlayOutEntityEffect packet) {
        return ModelEngineAPI.shouldShow(this.player, packet.a()) != CancelType.HIDE ? packet : null;
    }

    private ClientboundKeepAlivePacket handleKeepAlive(ClientboundKeepAlivePacket packet) {
        if (this.desyncMonitor.clientTickShifted() || this.desyncMonitor.shouldRetest()) {
            this.desyncMonitor.startTest();
        }
        return packet;
    }

    private PacketPlayInUseEntity handleInteract(PacketPlayInUseEntity packet) {
        DynamicHitbox hitbox;
        PacketDataSerializer buf = NetworkUtils.readData(packet);
        int entityId = buf.m();
        if (entityId == DynamicHitbox.getHitboxId() && (hitbox = ModelEngineAPI.getInteractionTracker().getDynamicHitbox(this.player.getUniqueId())) != null) {
            return new ServerboundInteractPacketWrapper(entityId, hitbox.getTarget(), packet);
        }
        ActiveModel activeModel = ModelEngineAPI.getInteractionTracker().getModelRelay(entityId);
        if (activeModel != null) {
            ModeledEntity modeledEntity = activeModel.getModeledEntity();
            if (modeledEntity == null) {
                return packet;
            }
            int id = modeledEntity.getBase().getEntityId();
            return new ServerboundInteractPacketWrapper(entityId, id, packet);
        }
        Integer relayed = ModelEngineAPI.getInteractionTracker().getEntityRelay(entityId);
        if (relayed != null) {
            return new ServerboundInteractPacketWrapper(entityId, relayed, packet);
        }
        return packet;
    }

    private PacketPlayInSteerVehicle handlePlayerInput(PacketPlayInSteerVehicle inputPacket) {
        MountController controller = ModelEngineAPI.getMountPairManager().getController(this.player.getUniqueId());
        if (controller != null) {
            MountController.MountInput input = controller.getInput();
            if (input == null) {
                controller.setInput(new MountController.MountInput(inputPacket.a(), inputPacket.d(), inputPacket.e(), inputPacket.f()));
            } else {
                input.setSide(Float.valueOf(inputPacket.a()));
                input.setFront(Float.valueOf(inputPacket.d()));
                input.setJump(inputPacket.e());
                input.setSneak(inputPacket.f());
            }
        }
        return inputPacket;
    }

    private ServerboundPongPacket handlePong(ServerboundPongPacket packet) {
        this.desyncMonitor.recordPongTime(System.currentTimeMillis());
        return null;
    }

    private PacketPlayInFlying handlePlayerMove(PacketPlayInFlying packet) {
        this.desyncMonitor.recordClientSyncTime(System.currentTimeMillis());
        return packet;
    }
}

