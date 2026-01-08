/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.network.PacketDataSerializer
 *  net.minecraft.network.protocol.Packet
 *  net.minecraft.network.protocol.game.PacketListenerPlayOut
 *  net.minecraft.network.protocol.game.PacketPlayOutEntity$PacketPlayOutEntityLook
 *  net.minecraft.network.protocol.game.PacketPlayOutEntityDestroy
 *  net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata
 *  net.minecraft.network.protocol.game.PacketPlayOutMount
 *  net.minecraft.network.protocol.game.PacketPlayOutSpawnEntity
 *  net.minecraft.network.protocol.game.PacketPlayOutUpdateAttributes
 *  net.minecraft.network.protocol.game.PacketPlayOutUpdateAttributes$AttributeSnapshot
 *  net.minecraft.network.syncher.DataWatcherRegistry
 *  net.minecraft.world.entity.EntityTypes
 *  net.minecraft.world.entity.ai.attributes.GenericAttributes
 *  net.minecraft.world.phys.Vec3D
 *  org.joml.Vector3f
 */
package com.ticxo.modelengine.v1_19_R3.parser.behavior;

import com.google.common.collect.Lists;
import com.ticxo.modelengine.api.entity.data.IEntityData;
import com.ticxo.modelengine.api.model.bone.render.BehaviorRendererParser;
import com.ticxo.modelengine.api.model.bone.render.renderer.MountRenderer;
import com.ticxo.modelengine.api.utils.data.tracker.CollectionDataTracker;
import com.ticxo.modelengine.api.utils.math.TMath;
import com.ticxo.modelengine.v1_19_R3.entity.EntityUtils;
import com.ticxo.modelengine.v1_19_R3.network.utils.NetworkUtils;
import com.ticxo.modelengine.v1_19_R3.network.utils.Packets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.PacketListenerPlayOut;
import net.minecraft.network.protocol.game.PacketPlayOutEntity;
import net.minecraft.network.protocol.game.PacketPlayOutEntityDestroy;
import net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata;
import net.minecraft.network.protocol.game.PacketPlayOutMount;
import net.minecraft.network.protocol.game.PacketPlayOutSpawnEntity;
import net.minecraft.network.protocol.game.PacketPlayOutUpdateAttributes;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.ai.attributes.GenericAttributes;
import net.minecraft.world.phys.Vec3D;
import org.joml.Vector3f;

public class MountParser
implements BehaviorRendererParser<MountRenderer> {
    @Override
    public void sendToClients(MountRenderer renderer) {
        IEntityData data = renderer.getModelRenderer().getActiveModel().getModeledEntity().getBase().getData();
        this.update(data.getTracking().keySet(), renderer);
        this.spawn(data.getStartTracking(), renderer);
        this.remove(data.getStopTracking(), renderer);
    }

    @Override
    public void destroy(MountRenderer renderer) {
        IEntityData data = renderer.getActiveModel().getModeledEntity().getBase().getData();
        HashSet<UUID> inRange = new HashSet<UUID>(data.getStartTracking());
        inRange.addAll(data.getTracking().keySet());
        inRange.addAll(data.getStopTracking());
        this.remove(inRange, renderer);
    }

    private void spawn(Set<UUID> targets, MountRenderer renderer) {
        if (targets.isEmpty()) {
            return;
        }
        Packets set = new Packets();
        for (MountRenderer.Mount mount : renderer.getRendered().values()) {
            set.add(this.pivotSpawn(mount));
            set.add((Packet<PacketListenerPlayOut>)this.pivotData(mount));
            set.add((Packet<PacketListenerPlayOut>)this.mountSpawn(mount));
            set.add((Packet<PacketListenerPlayOut>)this.mountData(mount));
            set.add((Packet<PacketListenerPlayOut>)this.pivotMount(mount));
            set.add((Packet<PacketListenerPlayOut>)this.mount(mount));
        }
        for (MountRenderer.Mount mount : renderer.getSpawnQueue().values()) {
            set.add(this.pivotSpawn(mount));
            set.add((Packet<PacketListenerPlayOut>)this.pivotData(mount));
            set.add((Packet<PacketListenerPlayOut>)this.mountSpawn(mount));
            set.add((Packet<PacketListenerPlayOut>)this.mountData(mount));
            set.add((Packet<PacketListenerPlayOut>)this.pivotMount(mount));
            set.add((Packet<PacketListenerPlayOut>)this.mount(mount));
        }
        NetworkUtils.sendBundled(targets, set);
    }

    private void update(Set<UUID> targets, MountRenderer renderer) {
        if (targets.isEmpty()) {
            return;
        }
        Packets set = new Packets();
        for (MountRenderer.Mount mount2 : renderer.getRendered().values()) {
            if (mount2.getPosition().isDirty()) {
                set.add(this.pivotMove(mount2));
                mount2.getPosition().clearDirty();
            }
            if (mount2.getYaw().isDirty()) {
                set.add((Packet<PacketListenerPlayOut>)this.mountRotate(mount2));
                mount2.getYaw().clearDirty();
            }
            if (mount2.getHealth().isDirty()) {
                set.add((Packet<PacketListenerPlayOut>)this.mountHealth(mount2));
                mount2.getHealth().clearDirty();
            }
            if (mount2.getMaxHealth().isDirty()) {
                set.add((Packet<PacketListenerPlayOut>)this.mountMaxHealth(mount2));
                mount2.getMaxHealth().clearDirty();
            }
            if (!mount2.getPassengers().isDirty()) continue;
            set.add((Packet<PacketListenerPlayOut>)this.mount(mount2));
            mount2.getPassengers().clearDirty();
        }
        for (MountRenderer.Mount mount2 : renderer.getSpawnQueue().values()) {
            set.add(this.pivotSpawn(mount2));
            set.add((Packet<PacketListenerPlayOut>)this.pivotData(mount2));
            set.add((Packet<PacketListenerPlayOut>)this.mountSpawn(mount2));
            set.add((Packet<PacketListenerPlayOut>)this.mountData(mount2));
            set.add((Packet<PacketListenerPlayOut>)this.pivotMount(mount2));
            set.add((Packet<PacketListenerPlayOut>)this.mount(mount2));
        }
        Map destroy = renderer.getDestroyQueue();
        if (!destroy.isEmpty()) {
            PacketDataSerializer buf = NetworkUtils.createByteBuf();
            buf.d(destroy.size() * 2);
            destroy.forEach((s, mount) -> {
                buf.d(mount.getPivotId());
                buf.d(mount.getMountId());
            });
            set.add((Packet<PacketListenerPlayOut>)new PacketPlayOutEntityDestroy(buf));
        }
        NetworkUtils.sendBundled(targets, set);
    }

    private void remove(Set<UUID> targets, MountRenderer renderer) {
        if (targets.isEmpty()) {
            return;
        }
        PacketDataSerializer buf = NetworkUtils.createByteBuf();
        Collection<MountRenderer.Mount> mounts = renderer.getRendered().values();
        buf.d(mounts.size() * 2);
        mounts.forEach(mount -> {
            buf.d(mount.getPivotId());
            buf.d(mount.getMountId());
        });
        NetworkUtils.send(targets, (Packet<? super PacketListenerPlayOut>)new PacketPlayOutEntityDestroy(buf));
    }

    private Packets.PacketSupplier pivotSpawn(MountRenderer.Mount renderer) {
        return NetworkUtils.createPivotSpawn(renderer.getPivotId(), renderer.getPivotUuid(), renderer.getPosition().get());
    }

    private PacketPlayOutEntityMetadata pivotData(MountRenderer.Mount renderer) {
        PacketDataSerializer buf = NetworkUtils.createByteBuf();
        buf.d(renderer.getPivotId());
        EntityUtils.writeData(buf, 0, DataWatcherRegistry.a, (byte)32);
        EntityUtils.writeData(buf, 1, DataWatcherRegistry.b, Integer.MAX_VALUE);
        EntityUtils.writeData(buf, 8, DataWatcherRegistry.d, Float.valueOf(0.0f));
        buf.writeByte(255);
        return new PacketPlayOutEntityMetadata(buf);
    }

    private PacketPlayOutSpawnEntity mountSpawn(MountRenderer.Mount renderer) {
        Vector3f pos = renderer.getPosition().get();
        return new PacketPlayOutSpawnEntity(renderer.getMountId(), renderer.getMountUuid(), (double)pos.x, (double)pos.y, (double)pos.z, 0.0f, TMath.byteToRot(renderer.getYaw().get()), EntityTypes.d, 0, Vec3D.b, 0.0);
    }

    private PacketPlayOutEntityMetadata mountData(MountRenderer.Mount renderer) {
        PacketDataSerializer buf = NetworkUtils.createByteBuf();
        buf.d(renderer.getMountId());
        EntityUtils.writeData(buf, 0, DataWatcherRegistry.a, (byte)32);
        EntityUtils.writeData(buf, 1, DataWatcherRegistry.b, Integer.MAX_VALUE);
        EntityUtils.writeData(buf, 15, DataWatcherRegistry.a, (byte)16);
        buf.writeByte(255);
        return new PacketPlayOutEntityMetadata(buf);
    }

    private PacketPlayOutMount pivotMount(MountRenderer.Mount renderer) {
        PacketDataSerializer buf = NetworkUtils.createByteBuf();
        buf.d(renderer.getPivotId());
        buf.d(1);
        buf.d(renderer.getMountId());
        return new PacketPlayOutMount(buf);
    }

    private PacketPlayOutMount mount(MountRenderer.Mount renderer) {
        CollectionDataTracker<Integer> ids = renderer.getPassengers();
        PacketDataSerializer buf = NetworkUtils.createByteBuf();
        buf.d(renderer.getMountId());
        buf.d(ids.size());
        ids.forEach(arg_0 -> ((PacketDataSerializer)buf).d(arg_0));
        return new PacketPlayOutMount(buf);
    }

    private Packets.PacketSupplier pivotMove(MountRenderer.Mount renderer) {
        return NetworkUtils.createPivotTeleport(renderer.getPivotId(), renderer.getPosition().get());
    }

    private PacketPlayOutEntity.PacketPlayOutEntityLook mountRotate(MountRenderer.Mount renderer) {
        PacketDataSerializer rot = NetworkUtils.createByteBuf();
        rot.d(renderer.getMountId());
        rot.writeByte((int)renderer.getYaw().get().byteValue());
        rot.writeByte(0);
        rot.writeBoolean(false);
        return PacketPlayOutEntity.PacketPlayOutEntityLook.b((PacketDataSerializer)rot);
    }

    private PacketPlayOutEntityMetadata mountHealth(MountRenderer.Mount renderer) {
        PacketDataSerializer buf = NetworkUtils.createByteBuf();
        buf.d(renderer.getMountId());
        EntityUtils.writeData(buf, 9, DataWatcherRegistry.d, renderer.getHealth().get());
        buf.writeByte(255);
        return new PacketPlayOutEntityMetadata(buf);
    }

    private PacketPlayOutUpdateAttributes mountMaxHealth(MountRenderer.Mount renderer) {
        PacketDataSerializer buf = NetworkUtils.createByteBuf();
        ArrayList attributes = Lists.newArrayList();
        attributes.add(new PacketPlayOutUpdateAttributes.AttributeSnapshot(GenericAttributes.a, (double)renderer.getMaxHealth().get().floatValue(), (Collection)Lists.newArrayList()));
        buf.d(renderer.getMountId());
        buf.a((Collection)attributes, (buf2, attribute) -> {
            buf2.a(BuiltInRegistries.u.b((Object)attribute.a()));
            buf2.writeDouble(attribute.b());
            buf2.a(attribute.c(), (buf3, modifier) -> {
                buf3.a(modifier.a());
                buf3.writeDouble(modifier.d());
                buf3.writeByte(modifier.c().a());
            });
        });
        return new PacketPlayOutUpdateAttributes(buf);
    }
}

