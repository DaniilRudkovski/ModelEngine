/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  io.netty.channel.Channel
 *  io.netty.channel.ChannelHandler
 *  io.netty.channel.ChannelPipeline
 *  lombok.Generated
 *  net.minecraft.SharedConstants
 *  net.minecraft.network.Connection
 *  net.minecraft.network.protocol.Packet
 *  net.minecraft.network.protocol.common.ClientboundPingPacket
 *  net.minecraft.network.protocol.game.ClientGamePacketListener
 *  net.minecraft.server.network.ServerGamePacketListenerImpl
 *  org.bukkit.craftbukkit.entity.CraftPlayer
 *  org.bukkit.entity.Player
 */
package com.ticxo.modelengine.v1_21_R6.network;

import com.google.common.collect.Maps;
import com.ticxo.modelengine.api.nms.network.NetworkHandler;
import com.ticxo.modelengine.api.nms.network.PipelineWrapper;
import com.ticxo.modelengine.api.nms.network.ProtectedPacketUnpacker;
import com.ticxo.modelengine.api.utils.ReflectionUtils;
import com.ticxo.modelengine.v1_21_R6.NMSFields;
import com.ticxo.modelengine.v1_21_R6.network.ModelEngineChannelHandler;
import com.ticxo.modelengine.v1_21_R6.network.utils.Bundler;
import com.ticxo.modelengine.v1_21_R6.network.utils.NetworkUtils;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelPipeline;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.Generated;
import net.minecraft.SharedConstants;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientboundPingPacket;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class NetworkHandlerImpl
implements NetworkHandler {
    public static NetworkHandlerImpl instance;
    private final Map<UUID, PipelineWrapper> pipelines = Maps.newConcurrentMap();
    private final Map<UUID, Bundler> bundles = Maps.newConcurrentMap();
    private boolean isBatching;

    public NetworkHandlerImpl() {
        if (instance != null) {
            throw new IllegalStateException("Network handler already initialized");
        }
        instance = this;
    }

    @Override
    public int getProtocolVersion() {
        return SharedConstants.getProtocolVersion();
    }

    @Override
    public Optional<PipelineWrapper> getPipeline(UUID uuid) {
        return Optional.ofNullable(this.pipelines.get(uuid));
    }

    @Override
    public void removePipeline(UUID uuid) {
        this.pipelines.remove(uuid);
    }

    @Override
    public void injectChannel(Player player) {
        ServerGamePacketListenerImpl listener = ((CraftPlayer)player).getHandle().connection;
        Connection connection = (Connection)ReflectionUtils.get(listener, NMSFields.SERVER_COMMON_PACKET_LISTENER_IMPL_connection);
        ChannelPipeline pipeline = connection.channel.pipeline();
        PipelineWrapper wrapper = new PipelineWrapper(player, arg_0 -> ((ChannelPipeline)pipeline).writeAndFlush(arg_0));
        ModelEngineChannelHandler handler = new ModelEngineChannelHandler(player, wrapper);
        this.pipelines.put(player.getUniqueId(), wrapper);
        this.bundles.put(player.getUniqueId(), new Bundler());
        for (String name : pipeline.toMap().keySet()) {
            if (!(pipeline.get(name) instanceof Connection)) continue;
            pipeline.addBefore(name, "protected_packet_unpacker", (ChannelHandler)new ProtectedPacketUnpacker());
            pipeline.addBefore(name, "model_engine_packet_handler", (ChannelHandler)handler);
            break;
        }
    }

    @Override
    public void ejectChannel(Player player) {
        ServerGamePacketListenerImpl listener = ((CraftPlayer)player).getHandle().connection;
        Connection connection = (Connection)ReflectionUtils.get(listener, NMSFields.SERVER_COMMON_PACKET_LISTENER_IMPL_connection);
        Channel channel = connection.channel;
        channel.eventLoop().submit(() -> {
            channel.pipeline().remove("protected_packet_unpacker");
            channel.pipeline().remove("model_engine_packet_handler");
            return null;
        });
        this.removePipeline(player.getUniqueId());
        this.bundles.remove(player.getUniqueId());
    }

    @Override
    public void ping(UUID uuid) {
        this.getPipeline(uuid).ifPresent(wrapper -> {
            int id = wrapper.getDesyncMonitor().getPingCounter().getAndIncrement();
            NetworkUtils.sendNow(uuid, (Packet<? super ClientGamePacketListener>)new ClientboundPingPacket(id));
        });
    }

    @Override
    public void startBatch() {
        this.isBatching = true;
    }

    @Override
    public void endBatch() {
        for (Map.Entry<UUID, Bundler> entry : this.bundles.entrySet()) {
            PipelineWrapper pipeline = this.pipelines.get(entry.getKey());
            if (pipeline == null) continue;
            Bundler bundler = entry.getValue();
            bundler.bundle(pipeline::writeAndFlush);
            bundler.clear();
        }
        this.isBatching = false;
    }

    public void appendPacket(UUID uuid, Packet<? super ClientGamePacketListener> packet) {
        Bundler bundler = this.bundles.get(uuid);
        if (bundler != null) {
            bundler.appendPacket(packet);
        }
    }

    public void appendPackets(UUID uuid, Collection<Packet<? super ClientGamePacketListener>> collection) {
        Bundler bundler = this.bundles.get(uuid);
        if (bundler != null) {
            bundler.appendPacket(collection);
        }
    }

    @Override
    @Generated
    public boolean isBatching() {
        return this.isBatching;
    }
}

