/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.Unpooled
 *  net.minecraft.network.PacketDataSerializer
 *  net.minecraft.network.protocol.Packet
 *  net.minecraft.network.protocol.game.ClientboundBundlePacket
 *  net.minecraft.network.protocol.game.PacketListenerPlayOut
 *  net.minecraft.network.protocol.game.PacketPlayOutEntityTeleport
 *  net.minecraft.network.protocol.game.PacketPlayOutSpawnEntity
 *  net.minecraft.world.entity.EntityTypes
 *  net.minecraft.world.phys.Vec3D
 *  org.bukkit.Bukkit
 *  org.bukkit.entity.Player
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Vector3f
 */
package com.ticxo.modelengine.v1_19_R3.network.utils;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.lod.AnimationLODHandler;
import com.ticxo.modelengine.api.model.ModeledEntity;
import com.ticxo.modelengine.api.nms.network.ProtectedPacket;
import com.ticxo.modelengine.v1_19_R3.network.NetworkHandlerImpl;
import com.ticxo.modelengine.v1_19_R3.network.utils.Packets;
import io.netty.buffer.Unpooled;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundBundlePacket;
import net.minecraft.network.protocol.game.PacketListenerPlayOut;
import net.minecraft.network.protocol.game.PacketPlayOutEntityTeleport;
import net.minecraft.network.protocol.game.PacketPlayOutSpawnEntity;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public class NetworkUtils {
    public static PacketDataSerializer createByteBuf() {
        return new PacketDataSerializer(Unpooled.buffer());
    }

    public static PacketDataSerializer createByteBuf(Consumer<PacketDataSerializer> consumer) {
        PacketDataSerializer buf = NetworkUtils.createByteBuf();
        consumer.accept(buf);
        return buf;
    }

    public static PacketDataSerializer readData(Packet<?> packet) {
        PacketDataSerializer buf = NetworkUtils.createByteBuf();
        packet.a(buf);
        return buf;
    }

    public static Packets.PacketSupplier createPivotSpawn(int id, UUID uuid, Vector3f pos) {
        return playerUUID -> ModelEngineAPI.getPlayerProtocolVersion(playerUUID) >= 764 ? new PacketPlayOutSpawnEntity(id, uuid, (double)pos.x, (double)pos.y - 0.5, (double)pos.z, 0.0f, 0.0f, EntityTypes.c, 0, Vec3D.b, 0.0) : new PacketPlayOutSpawnEntity(id, uuid, (double)pos.x, (double)pos.y - 0.375, (double)pos.z, 0.0f, 0.0f, EntityTypes.c, 0, Vec3D.b, 0.0);
    }

    public static Packets.PacketSupplier createPivotTeleport(int id, Vector3f pos) {
        PacketDataSerializer buf = NetworkUtils.createByteBuf();
        buf.d(id);
        buf.writeDouble((double)pos.x);
        buf.writeDouble((double)pos.y - 0.375);
        buf.writeDouble((double)pos.z);
        buf.writeByte(0);
        buf.writeByte(0);
        buf.writeBoolean(false);
        PacketPlayOutEntityTeleport tpHigher = new PacketPlayOutEntityTeleport(buf);
        buf = NetworkUtils.createByteBuf();
        buf.d(id);
        buf.writeDouble((double)pos.x);
        buf.writeDouble((double)pos.y - 0.5);
        buf.writeDouble((double)pos.z);
        buf.writeByte(0);
        buf.writeByte(0);
        buf.writeBoolean(false);
        PacketPlayOutEntityTeleport tpLower = new PacketPlayOutEntityTeleport(buf);
        return playerUUID -> ModelEngineAPI.getPlayerProtocolVersion(playerUUID) >= 764 ? tpLower : tpHigher;
    }

    public static Packets.PacketSupplier lodWrapper(ModeledEntity modeledEntity, BiFunction<UUID, AnimationLODHandler.LODTracker, Packet<PacketListenerPlayOut>> packetFunction) {
        return uuid -> {
            AnimationLODHandler.LODTracker tracker = modeledEntity.getAnimationLodHandler().tick(uuid);
            if (tracker.isCanSkip()) {
                return null;
            }
            return (Packet)packetFunction.apply(uuid, tracker);
        };
    }

    public static void send(UUID target, @Nullable Packet<? super PacketListenerPlayOut> packet) {
        if (packet == null) {
            return;
        }
        NetworkHandlerImpl handler = NetworkHandlerImpl.instance;
        if (handler.isBatching()) {
            handler.appendPacket(target, packet);
        } else {
            ProtectedPacket wrapped = new ProtectedPacket(packet);
            ModelEngineAPI.getNetworkHandler().getPipeline(target).ifPresent(pipeline -> pipeline.writeAndFlush(wrapped));
        }
    }

    public static void send(Set<UUID> targets, @Nullable Packet<? super PacketListenerPlayOut> packet) {
        if (packet == null) {
            return;
        }
        NetworkHandlerImpl handler = NetworkHandlerImpl.instance;
        if (handler.isBatching()) {
            for (UUID player : targets) {
                handler.appendPacket(player, packet);
            }
        } else {
            ProtectedPacket wrapped = new ProtectedPacket(packet);
            for (UUID player : targets) {
                ModelEngineAPI.getNetworkHandler().getPipeline(player).ifPresent(pipeline -> pipeline.writeAndFlush(wrapped));
            }
        }
    }

    public static void send(Set<UUID> targets, @Nullable Packet<? super PacketListenerPlayOut> packet, Predicate<Player> predicate) {
        if (packet == null) {
            return;
        }
        NetworkHandlerImpl handler = NetworkHandlerImpl.instance;
        if (handler.isBatching()) {
            for (UUID player : targets) {
                Player p = Bukkit.getPlayer((UUID)player);
                if (p == null || !predicate.test(p)) continue;
                handler.appendPacket(player, packet);
            }
        } else {
            ProtectedPacket wrapped = new ProtectedPacket(packet);
            for (UUID player : targets) {
                Player p = Bukkit.getPlayer((UUID)player);
                if (p == null || !predicate.test(p)) continue;
                ModelEngineAPI.getNetworkHandler().getPipeline(player).ifPresent(pipeline -> pipeline.writeAndFlush(wrapped));
            }
        }
    }

    public static void sendRaw(UUID target, @Nullable Packet<? super PacketListenerPlayOut> packet) {
        if (packet == null) {
            return;
        }
        ModelEngineAPI.getNetworkHandler().getPipeline(target).ifPresent(pipeline -> pipeline.writeAndFlush(packet));
    }

    public static void sendRaw(Set<UUID> targets, @Nullable Packet<? super PacketListenerPlayOut> packet) {
        if (packet == null) {
            return;
        }
        for (UUID player : targets) {
            ModelEngineAPI.getNetworkHandler().getPipeline(player).ifPresent(pipeline -> pipeline.writeAndFlush(packet));
        }
    }

    public static void sendRaw(Set<UUID> targets, @Nullable Packet<? super PacketListenerPlayOut> packet, Predicate<Player> predicate) {
        if (packet == null) {
            return;
        }
        for (UUID player : targets) {
            Player p = Bukkit.getPlayer((UUID)player);
            if (p == null || !predicate.test(p)) continue;
            ModelEngineAPI.getNetworkHandler().getPipeline(player).ifPresent(pipeline -> pipeline.writeAndFlush(packet));
        }
    }

    public static void sendNow(UUID target, @Nullable Packet<? super PacketListenerPlayOut> packet) {
        if (packet == null) {
            return;
        }
        ProtectedPacket wrapped = new ProtectedPacket(packet);
        ModelEngineAPI.getNetworkHandler().getPipeline(target).ifPresent(pipeline -> pipeline.writeAndFlushNow(wrapped));
    }

    public static void sendBundled(UUID target, Packets collection) {
        if (collection.isEmpty()) {
            return;
        }
        Collection<Packet<PacketListenerPlayOut>> packets = collection.compile(target);
        NetworkHandlerImpl handler = NetworkHandlerImpl.instance;
        if (handler.isBatching()) {
            handler.appendPackets(target, packets);
        } else {
            ProtectedPacket wrapped = new ProtectedPacket(new ClientboundBundlePacket(packets));
            handler.getPipeline(target).ifPresent(pipeline -> pipeline.writeAndFlush(wrapped));
        }
    }

    public static void sendBundled(Set<UUID> targets, Packets collection) {
        if (collection.isEmpty()) {
            return;
        }
        NetworkHandlerImpl handler = NetworkHandlerImpl.instance;
        if (handler.isBatching()) {
            for (UUID player : targets) {
                handler.appendPackets(player, collection.compile(player));
            }
        } else {
            for (UUID player : targets) {
                ModelEngineAPI.getNetworkHandler().getPipeline(player).ifPresent(pipeline -> {
                    ProtectedPacket wrapped = new ProtectedPacket(new ClientboundBundlePacket(collection.compile(player)));
                    pipeline.writeAndFlush(wrapped);
                });
            }
        }
    }

    public static void sendBundled(Set<UUID> targets, Packets collection, Predicate<Player> predicate) {
        if (collection.isEmpty()) {
            return;
        }
        NetworkHandlerImpl handler = NetworkHandlerImpl.instance;
        if (handler.isBatching()) {
            for (UUID player : targets) {
                Player p = Bukkit.getPlayer((UUID)player);
                if (p == null || !predicate.test(p)) continue;
                handler.appendPackets(player, collection.compile(player));
            }
        } else {
            for (UUID player : targets) {
                Player p = Bukkit.getPlayer((UUID)player);
                if (p == null || !predicate.test(p)) continue;
                ModelEngineAPI.getNetworkHandler().getPipeline(player).ifPresent(pipeline -> {
                    ProtectedPacket wrapped = new ProtectedPacket(new ClientboundBundlePacket(collection.compile(player)));
                    pipeline.writeAndFlush(wrapped);
                });
            }
        }
    }

    public static void sendBundledRaw(UUID target, Packets collection) {
        if (collection.isEmpty()) {
            return;
        }
        ClientboundBundlePacket packet = new ClientboundBundlePacket(collection.compile(target));
        ModelEngineAPI.getNetworkHandler().getPipeline(target).ifPresent(pipeline -> pipeline.writeAndFlush(packet));
    }

    public static void sendBundledRaw(Set<UUID> targets, Packets collection) {
        if (collection.isEmpty()) {
            return;
        }
        for (UUID player : targets) {
            ModelEngineAPI.getNetworkHandler().getPipeline(player).ifPresent(pipeline -> {
                ClientboundBundlePacket packet = new ClientboundBundlePacket(collection.compile(player));
                pipeline.writeAndFlush(packet);
            });
        }
    }

    public static void sendBundledRaw(Set<UUID> targets, Packets collection, Predicate<Player> predicate) {
        if (collection.isEmpty()) {
            return;
        }
        for (UUID player : targets) {
            Player p = Bukkit.getPlayer((UUID)player);
            if (p == null || !predicate.test(p)) continue;
            ModelEngineAPI.getNetworkHandler().getPipeline(player).ifPresent(pipeline -> {
                ClientboundBundlePacket packet = new ClientboundBundlePacket(collection.compile(player));
                pipeline.writeAndFlush(packet);
            });
        }
    }
}

