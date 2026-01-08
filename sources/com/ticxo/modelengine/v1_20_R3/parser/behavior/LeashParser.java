/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.IntArrayList
 *  it.unimi.dsi.fastutil.ints.IntList
 *  net.minecraft.network.PacketDataSerializer
 *  net.minecraft.network.protocol.Packet
 *  net.minecraft.network.protocol.game.PacketListenerPlayOut
 *  net.minecraft.network.protocol.game.PacketPlayOutAttachEntity
 *  net.minecraft.network.protocol.game.PacketPlayOutEntityDestroy
 *  net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata
 *  net.minecraft.network.protocol.game.PacketPlayOutMount
 *  net.minecraft.network.protocol.game.PacketPlayOutSpawnEntity
 *  net.minecraft.network.syncher.DataWatcherRegistry
 *  net.minecraft.world.entity.EntityTypes
 *  net.minecraft.world.phys.Vec3D
 *  org.joml.Vector3f
 */
package com.ticxo.modelengine.v1_20_R3.parser.behavior;

import com.ticxo.modelengine.api.entity.data.IEntityData;
import com.ticxo.modelengine.api.model.bone.render.BehaviorRendererParser;
import com.ticxo.modelengine.api.model.bone.render.renderer.LeashRenderer;
import com.ticxo.modelengine.v1_20_R3.entity.EntityUtils;
import com.ticxo.modelengine.v1_20_R3.network.utils.NetworkUtils;
import com.ticxo.modelengine.v1_20_R3.network.utils.Packets;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.IntStream;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.PacketListenerPlayOut;
import net.minecraft.network.protocol.game.PacketPlayOutAttachEntity;
import net.minecraft.network.protocol.game.PacketPlayOutEntityDestroy;
import net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata;
import net.minecraft.network.protocol.game.PacketPlayOutMount;
import net.minecraft.network.protocol.game.PacketPlayOutSpawnEntity;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.phys.Vec3D;
import org.joml.Vector3f;

public class LeashParser
implements BehaviorRendererParser<LeashRenderer> {
    @Override
    public void sendToClients(LeashRenderer renderer) {
        IEntityData data = renderer.getModelRenderer().getActiveModel().getModeledEntity().getBase().getData();
        this.update(data.getTracking().keySet(), renderer);
        this.spawn(data.getStartTracking(), renderer);
        HashSet<UUID> stop = new HashSet<UUID>(data.getStopTracking());
        stop.removeAll(data.getTracking().keySet());
        this.remove(stop, renderer);
    }

    @Override
    public void destroy(LeashRenderer renderer) {
        IEntityData data = renderer.getActiveModel().getModeledEntity().getBase().getData();
        HashSet<UUID> inRange = new HashSet<UUID>(data.getStartTracking());
        inRange.addAll(data.getTracking().keySet());
        inRange.addAll(data.getStopTracking());
        this.remove(inRange, renderer);
    }

    private void spawn(Set<UUID> targets, LeashRenderer renderer) {
        if (targets.isEmpty()) {
            return;
        }
        Packets set = new Packets();
        for (LeashRenderer.Leash leash : renderer.getRendered().values()) {
            set.add(this.pivotSpawn(leash));
            set.add((Packet<PacketListenerPlayOut>)this.pivotData(leash));
            set.add((Packet<PacketListenerPlayOut>)this.leashSpawn(leash));
            set.add((Packet<PacketListenerPlayOut>)this.leashData(leash));
            set.add((Packet<PacketListenerPlayOut>)this.mount(leash));
            set.add((Packet<PacketListenerPlayOut>)this.link(leash, true));
        }
        for (LeashRenderer.Leash leash : renderer.getSpawnQueue().values()) {
            set.add(this.pivotSpawn(leash));
            set.add((Packet<PacketListenerPlayOut>)this.pivotData(leash));
            set.add((Packet<PacketListenerPlayOut>)this.leashSpawn(leash));
            set.add((Packet<PacketListenerPlayOut>)this.leashData(leash));
            set.add((Packet<PacketListenerPlayOut>)this.mount(leash));
            set.add((Packet<PacketListenerPlayOut>)this.link(leash, true));
        }
        NetworkUtils.sendBundled(targets, set);
    }

    private void update(Set<UUID> targets, LeashRenderer renderer) {
        if (targets.isEmpty()) {
            return;
        }
        Packets set = new Packets();
        for (LeashRenderer.Leash leash2 : renderer.getRendered().values()) {
            set.add((Packet<PacketListenerPlayOut>)this.link(leash2, false));
            set.add(this.move(leash2));
        }
        for (LeashRenderer.Leash leash2 : renderer.getSpawnQueue().values()) {
            set.add(this.pivotSpawn(leash2));
            set.add((Packet<PacketListenerPlayOut>)this.pivotData(leash2));
            set.add((Packet<PacketListenerPlayOut>)this.leashSpawn(leash2));
            set.add((Packet<PacketListenerPlayOut>)this.leashData(leash2));
            set.add((Packet<PacketListenerPlayOut>)this.mount(leash2));
            set.add((Packet<PacketListenerPlayOut>)this.link(leash2, true));
        }
        Map destroy = renderer.getDestroyQueue();
        if (!destroy.isEmpty()) {
            set.add((Packet<PacketListenerPlayOut>)new PacketPlayOutEntityDestroy((IntList)IntArrayList.toList((IntStream)destroy.values().stream().mapMultiToInt((leash, intConsumer) -> {
                intConsumer.accept(leash.getLeashId());
                intConsumer.accept(leash.getPivotId());
            }))));
        }
        NetworkUtils.sendBundled(targets, set);
    }

    private void remove(Set<UUID> targets, LeashRenderer renderer) {
        if (targets.isEmpty()) {
            return;
        }
        PacketDataSerializer buf = NetworkUtils.createByteBuf();
        Map leashes = renderer.getRendered();
        buf.c(leashes.size());
        leashes.forEach((s, leash) -> buf.c(leash.getLeashId()));
        NetworkUtils.send(targets, (Packet<? super PacketListenerPlayOut>)new PacketPlayOutEntityDestroy(buf));
    }

    private Packets.PacketSupplier pivotSpawn(LeashRenderer.Leash renderer) {
        Vector3f pos = renderer.getPosition().get();
        return NetworkUtils.createPivotSpawn(renderer.getPivotId(), renderer.getPivotUUID(), pos);
    }

    private PacketPlayOutSpawnEntity leashSpawn(LeashRenderer.Leash renderer) {
        Vector3f pos = renderer.getPosition().get();
        return new PacketPlayOutSpawnEntity(renderer.getLeashId(), renderer.getLeastUUID(), (double)pos.x, (double)pos.y, (double)pos.z, 0.0f, 0.0f, EntityTypes.aM, 0, Vec3D.b, 0.0);
    }

    private PacketPlayOutEntityMetadata pivotData(LeashRenderer.Leash renderer) {
        PacketDataSerializer buf = NetworkUtils.createByteBuf();
        buf.c(renderer.getLeashId());
        EntityUtils.writeData(buf, 0, DataWatcherRegistry.a, (byte)32);
        EntityUtils.writeData(buf, 1, DataWatcherRegistry.b, Integer.MAX_VALUE);
        EntityUtils.writeData(buf, 8, DataWatcherRegistry.d, Float.valueOf(0.0f));
        buf.k(255);
        return new PacketPlayOutEntityMetadata(buf);
    }

    private PacketPlayOutEntityMetadata leashData(LeashRenderer.Leash renderer) {
        PacketDataSerializer buf = NetworkUtils.createByteBuf();
        buf.c(renderer.getLeashId());
        EntityUtils.writeData(buf, 0, DataWatcherRegistry.a, (byte)32);
        EntityUtils.writeData(buf, 1, DataWatcherRegistry.b, Integer.MAX_VALUE);
        EntityUtils.writeData(buf, 16, DataWatcherRegistry.b, 0);
        buf.k(255);
        return new PacketPlayOutEntityMetadata(buf);
    }

    private PacketPlayOutMount mount(LeashRenderer.Leash renderer) {
        PacketDataSerializer buf = NetworkUtils.createByteBuf();
        buf.c(renderer.getPivotId());
        buf.c(1);
        buf.c(renderer.getLeashId());
        return new PacketPlayOutMount(buf);
    }

    private Packets.PacketSupplier move(LeashRenderer.Leash renderer) {
        if (!renderer.getPosition().isDirty()) {
            return null;
        }
        Vector3f pos = renderer.getPosition().get();
        return NetworkUtils.createPivotTeleport(renderer.getPivotId(), pos);
    }

    private PacketPlayOutAttachEntity link(LeashRenderer.Leash renderer, boolean spawn) {
        if (!spawn && !renderer.getConnected().isDirty()) {
            return null;
        }
        PacketDataSerializer buf = NetworkUtils.createByteBuf();
        buf.p(renderer.getLeashId());
        buf.p(renderer.getConnected().get().intValue());
        renderer.getConnected().clearDirty();
        return new PacketPlayOutAttachEntity(buf);
    }
}

