/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.Unpooled
 *  net.minecraft.core.RegistryAccess
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.network.ProtocolInfo
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.network.protocol.Packet
 *  net.minecraft.network.protocol.game.ClientGamePacketListener
 *  net.minecraft.network.protocol.game.ClientboundAddEntityPacket
 *  net.minecraft.network.protocol.game.ClientboundBundlePacket
 *  net.minecraft.network.protocol.game.ClientboundEntityPositionSyncPacket
 *  net.minecraft.network.protocol.game.GameProtocols
 *  net.minecraft.network.protocol.game.ServerGamePacketListener
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.PositionMoveRotation
 *  net.minecraft.world.phys.Vec3
 *  org.bukkit.Bukkit
 *  org.bukkit.entity.Player
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Vector3f
 */
package com.ticxo.modelengine.v1_21_R6.network.utils;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.lod.AnimationLODHandler;
import com.ticxo.modelengine.api.model.ModeledEntity;
import com.ticxo.modelengine.api.nms.network.ProtectedPacket;
import com.ticxo.modelengine.v1_21_R6.entity.EntityContainer;
import com.ticxo.modelengine.v1_21_R6.network.NetworkHandlerImpl;
import com.ticxo.modelengine.v1_21_R6.network.utils.Packets;
import io.netty.buffer.Unpooled;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.ProtocolInfo;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundBundlePacket;
import net.minecraft.network.protocol.game.ClientboundEntityPositionSyncPacket;
import net.minecraft.network.protocol.game.GameProtocols;
import net.minecraft.network.protocol.game.ServerGamePacketListener;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PositionMoveRotation;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public class NetworkUtils {
    public static final ProtocolInfo<ClientGamePacketListener> CLIENTBOUND_CODEC = GameProtocols.CLIENTBOUND_TEMPLATE.bind(RegistryFriendlyByteBuf.decorator((RegistryAccess)MinecraftServer.getServer().registryAccess()));
    public static final ProtocolInfo<ServerGamePacketListener> SERVERBOUND_CODEC = GameProtocols.SERVERBOUND_TEMPLATE.bind(RegistryFriendlyByteBuf.decorator((RegistryAccess)MinecraftServer.getServer().registryAccess()), () -> false);
    private static final Map<Class<?>, Constructor<?>> PACKET_CONSTRUCTOR = new HashMap();

    public static RegistryFriendlyByteBuf createByteBuf() {
        return new RegistryFriendlyByteBuf(Unpooled.buffer(), (RegistryAccess)MinecraftServer.getServer().registryAccess());
    }

    public static RegistryFriendlyByteBuf createByteBuf(Consumer<RegistryFriendlyByteBuf> consumer) {
        RegistryFriendlyByteBuf buf = NetworkUtils.createByteBuf();
        consumer.accept(buf);
        return buf;
    }

    public static <T extends Packet<ClientGamePacketListener>> T create(Class<T> clazz, RegistryFriendlyByteBuf buf) {
        Constructor constructor = PACKET_CONSTRUCTOR.computeIfAbsent(clazz, aClass -> {
            try {
                Constructor c2 = aClass.getDeclaredConstructor(RegistryFriendlyByteBuf.class);
                c2.setAccessible(true);
                return c2;
            }
            catch (NoSuchMethodException ignored) {
                try {
                    Constructor c3 = aClass.getDeclaredConstructor(FriendlyByteBuf.class);
                    c3.setAccessible(true);
                    return c3;
                }
                catch (NoSuchMethodException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        try {
            return (T)((Packet)constructor.newInstance(buf));
        }
        catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public static FriendlyByteBuf readServerbound(Packet<? super ServerGamePacketListener> packet) {
        RegistryFriendlyByteBuf buf = NetworkUtils.createByteBuf();
        SERVERBOUND_CODEC.codec().encode((Object)buf, packet);
        buf.readVarInt();
        return buf;
    }

    public static FriendlyByteBuf readClientbound(Packet<? super ClientGamePacketListener> packet) {
        RegistryFriendlyByteBuf buf = NetworkUtils.createByteBuf();
        CLIENTBOUND_CODEC.codec().encode((Object)buf, packet);
        buf.readVarInt();
        return buf;
    }

    public static Packets.PacketSupplier createPivotSpawn(int id, UUID uuid, Vector3f pos) {
        return playerUUID -> ModelEngineAPI.getPlayerProtocolVersion(playerUUID) >= 764 ? new ClientboundAddEntityPacket(id, uuid, (double)pos.x, (double)pos.y - 0.5, (double)pos.z, 0.0f, 0.0f, EntityType.AREA_EFFECT_CLOUD, 0, Vec3.ZERO, 0.0) : new ClientboundAddEntityPacket(id, uuid, (double)pos.x, (double)pos.y - 0.375, (double)pos.z, 0.0f, 0.0f, EntityType.AREA_EFFECT_CLOUD, 0, Vec3.ZERO, 0.0);
    }

    public static Packets.PacketSupplier createDisplayPivotSpawn(int id, UUID uuid, Vector3f pos) {
        return playerUUID -> new ClientboundAddEntityPacket(id, uuid, (double)pos.x, (double)pos.y - 0.375, (double)pos.z, 0.0f, 0.0f, EntityType.ITEM_DISPLAY, 0, Vec3.ZERO, 0.0);
    }

    public static Packets.PacketSupplier createPivotTeleport(int id, Vector3f pos) {
        EntityContainer container = EntityContainer.of(id);
        container.setPosRaw(pos.x, (double)pos.y - 0.375, pos.z);
        ClientboundEntityPositionSyncPacket tpHigher = new ClientboundEntityPositionSyncPacket(container.getId(), PositionMoveRotation.of((Entity)container), container.onGround());
        container = EntityContainer.of(id);
        container.setId(id);
        container.setPosRaw(pos.x, (double)pos.y - 0.5, pos.z);
        ClientboundEntityPositionSyncPacket tpLower = new ClientboundEntityPositionSyncPacket(container.getId(), PositionMoveRotation.of((Entity)container), container.onGround());
        return playerUUID -> ModelEngineAPI.getPlayerProtocolVersion(playerUUID) >= 764 ? tpLower : tpHigher;
    }

    public static Packets.PacketSupplier createDisplayPivotTeleport(int id, Vector3f pos) {
        EntityContainer container = EntityContainer.of(id);
        container.setPosRaw(pos.x, pos.y, pos.z);
        return playerUUID -> new ClientboundEntityPositionSyncPacket(container.getId(), PositionMoveRotation.of((Entity)container), container.onGround());
    }

    public static Packets.PacketSupplier lodWrapper(ModeledEntity modeledEntity, BiFunction<UUID, AnimationLODHandler.LODTracker, Packet<ClientGamePacketListener>> packetFunction) {
        return uuid -> {
            AnimationLODHandler.LODTracker tracker = modeledEntity.getAnimationLodHandler().tick(uuid);
            if (tracker.isCanSkip()) {
                return null;
            }
            return (Packet)packetFunction.apply(uuid, tracker);
        };
    }

    public static void send(UUID target, @Nullable Packet<? super ClientGamePacketListener> packet) {
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

    public static void send(Set<UUID> targets, @Nullable Packet<? super ClientGamePacketListener> packet) {
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

    public static void send(Set<UUID> targets, @Nullable Packet<? super ClientGamePacketListener> packet, Predicate<Player> predicate) {
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

    public static void sendRaw(UUID target, @Nullable Packet<? super ClientGamePacketListener> packet) {
        if (packet == null) {
            return;
        }
        ModelEngineAPI.getNetworkHandler().getPipeline(target).ifPresent(pipeline -> pipeline.writeAndFlush(packet));
    }

    public static void sendRaw(Set<UUID> targets, @Nullable Packet<? super ClientGamePacketListener> packet) {
        if (packet == null) {
            return;
        }
        for (UUID player : targets) {
            ModelEngineAPI.getNetworkHandler().getPipeline(player).ifPresent(pipeline -> pipeline.writeAndFlush(packet));
        }
    }

    public static void sendRaw(Set<UUID> targets, @Nullable Packet<? super ClientGamePacketListener> packet, Predicate<Player> predicate) {
        if (packet == null) {
            return;
        }
        for (UUID player : targets) {
            Player p = Bukkit.getPlayer((UUID)player);
            if (p == null || !predicate.test(p)) continue;
            ModelEngineAPI.getNetworkHandler().getPipeline(player).ifPresent(pipeline -> pipeline.writeAndFlush(packet));
        }
    }

    public static void sendNow(UUID target, @Nullable Packet<? super ClientGamePacketListener> packet) {
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
        collection.compile(target, packets -> {
            NetworkHandlerImpl handler = NetworkHandlerImpl.instance;
            if (handler.isBatching()) {
                handler.appendPackets(target, (Collection<Packet<? super ClientGamePacketListener>>)packets);
            } else {
                ProtectedPacket wrapped = new ProtectedPacket(new ClientboundBundlePacket((Iterable)packets));
                handler.getPipeline(target).ifPresent(pipeline -> pipeline.writeAndFlush(wrapped));
            }
        });
    }

    public static void sendBundled(Set<UUID> targets, Packets collection) {
        if (collection.isEmpty()) {
            return;
        }
        NetworkHandlerImpl handler = NetworkHandlerImpl.instance;
        if (handler.isBatching()) {
            for (UUID player : targets) {
                collection.compile(player, packets -> handler.appendPackets(player, (Collection<Packet<? super ClientGamePacketListener>>)packets));
            }
        } else {
            for (UUID player : targets) {
                ModelEngineAPI.getNetworkHandler().getPipeline(player).ifPresent(pipeline -> collection.compile(player, packets -> {
                    ProtectedPacket wrapped = new ProtectedPacket(new ClientboundBundlePacket((Iterable)packets));
                    pipeline.writeAndFlush(wrapped);
                }));
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
                collection.compile(player, packets -> handler.appendPackets(player, (Collection<Packet<? super ClientGamePacketListener>>)packets));
            }
        } else {
            for (UUID player : targets) {
                Player p = Bukkit.getPlayer((UUID)player);
                if (p == null || !predicate.test(p)) continue;
                ModelEngineAPI.getNetworkHandler().getPipeline(player).ifPresent(pipeline -> collection.compile(player, packets -> {
                    ProtectedPacket wrapped = new ProtectedPacket(new ClientboundBundlePacket((Iterable)packets));
                    pipeline.writeAndFlush(wrapped);
                }));
            }
        }
    }

    public static void sendBundledRaw(UUID target, Packets collection) {
        if (collection.isEmpty()) {
            return;
        }
        collection.compile(target, packets -> {
            ClientboundBundlePacket packet = new ClientboundBundlePacket((Iterable)packets);
            ModelEngineAPI.getNetworkHandler().getPipeline(target).ifPresent(pipeline -> pipeline.writeAndFlush(packet));
        });
    }

    public static void sendBundledRaw(Set<UUID> targets, Packets collection) {
        if (collection.isEmpty()) {
            return;
        }
        for (UUID player : targets) {
            ModelEngineAPI.getNetworkHandler().getPipeline(player).ifPresent(pipeline -> collection.compile(player, packets -> {
                ClientboundBundlePacket packet = new ClientboundBundlePacket((Iterable)packets);
                pipeline.writeAndFlush(packet);
            }));
        }
    }

    public static void sendBundledRaw(Set<UUID> targets, Packets collection, Predicate<Player> predicate) {
        if (collection.isEmpty()) {
            return;
        }
        for (UUID player : targets) {
            Player p = Bukkit.getPlayer((UUID)player);
            if (p == null || !predicate.test(p)) continue;
            ModelEngineAPI.getNetworkHandler().getPipeline(player).ifPresent(pipeline -> collection.compile(player, packets -> {
                ClientboundBundlePacket packet = new ClientboundBundlePacket((Iterable)packets);
                pipeline.writeAndFlush(packet);
            }));
        }
    }
}

