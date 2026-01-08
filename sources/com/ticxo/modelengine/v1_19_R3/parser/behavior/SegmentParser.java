/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.IntArrayList
 *  it.unimi.dsi.fastutil.ints.IntList
 *  net.minecraft.network.PacketDataSerializer
 *  net.minecraft.network.protocol.Packet
 *  net.minecraft.network.protocol.game.PacketListenerPlayOut
 *  net.minecraft.network.protocol.game.PacketPlayOutEntityDestroy
 *  net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata
 *  net.minecraft.network.protocol.game.PacketPlayOutMount
 *  net.minecraft.network.protocol.game.PacketPlayOutSpawnEntity
 *  net.minecraft.network.syncher.DataWatcherRegistry
 *  net.minecraft.world.entity.EntityTypes
 *  net.minecraft.world.phys.Vec3D
 *  org.bukkit.craftbukkit.v1_19_R3.inventory.CraftItemStack
 *  org.bukkit.inventory.ItemStack
 *  org.joml.Quaternionf
 *  org.joml.Vector3f
 */
package com.ticxo.modelengine.v1_19_R3.parser.behavior;

import com.ticxo.modelengine.api.entity.data.IEntityData;
import com.ticxo.modelengine.api.model.bone.render.BehaviorRendererParser;
import com.ticxo.modelengine.api.model.bone.render.renderer.SegmentRenderer;
import com.ticxo.modelengine.api.model.render.DisplayBone;
import com.ticxo.modelengine.api.utils.data.UpdateScheme;
import com.ticxo.modelengine.api.utils.math.TMath;
import com.ticxo.modelengine.v1_19_R3.entity.EntityUtils;
import com.ticxo.modelengine.v1_19_R3.network.utils.NetworkUtils;
import com.ticxo.modelengine.v1_19_R3.network.utils.Packets;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.PacketListenerPlayOut;
import net.minecraft.network.protocol.game.PacketPlayOutEntityDestroy;
import net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata;
import net.minecraft.network.protocol.game.PacketPlayOutMount;
import net.minecraft.network.protocol.game.PacketPlayOutSpawnEntity;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.craftbukkit.v1_19_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class SegmentParser
implements BehaviorRendererParser<SegmentRenderer> {
    private final Set<Runnable> cleanupQueue = new HashSet<Runnable>();

    @Override
    public void sendToClients(SegmentRenderer renderer) {
        IEntityData data = renderer.getModelRenderer().getActiveModel().getModeledEntity().getBase().getData();
        this.update(data.getTracking().keySet(), renderer);
        this.spawn(data.getStartTracking(), renderer);
        this.remove(data.getStopTracking(), renderer);
        this.cleanupQueue.forEach(Runnable::run);
        this.cleanupQueue.clear();
    }

    @Override
    public void destroy(SegmentRenderer renderer) {
        IEntityData data = renderer.getActiveModel().getModeledEntity().getBase().getData();
        HashSet<UUID> inRange = new HashSet<UUID>(data.getStartTracking());
        inRange.addAll(data.getTracking().keySet());
        inRange.addAll(data.getStopTracking());
        this.remove(inRange, renderer);
    }

    private void spawn(Packets set, Map<String, SegmentRenderer.Pivot> map) {
        for (SegmentRenderer.Pivot pivot : map.values()) {
            set.add(this.pivotSpawn(pivot));
            set.add((Packet<PacketListenerPlayOut>)this.pivotData(pivot));
            for (SegmentRenderer.SegmentDisplayBone bone : pivot.getRendered().values()) {
                for (DisplayBone.BoneData boneData : bone.getModel().values()) {
                    set.add((Packet<PacketListenerPlayOut>)this.displaySpawn(pivot, boneData));
                    set.add(this.displayData(boneData, true));
                }
            }
            for (SegmentRenderer.SegmentDisplayBone bone : pivot.getSpawnQueue().values()) {
                for (DisplayBone.BoneData boneData : bone.getModel().values()) {
                    set.add((Packet<PacketListenerPlayOut>)this.displaySpawn(pivot, boneData));
                    set.add(this.displayData(boneData, true));
                }
            }
            set.add((Packet<PacketListenerPlayOut>)this.pivotMount(pivot));
            this.cleanupQueue.add(pivot::clearDirty);
        }
    }

    private void spawn(Set<UUID> targets, SegmentRenderer renderer) {
        if (targets.isEmpty()) {
            return;
        }
        Packets set = new Packets();
        this.spawn(set, renderer.getRendered());
        this.spawn(set, renderer.getSpawnQueue());
        NetworkUtils.sendBundled(targets, set);
    }

    private void update(Set<UUID> targets, SegmentRenderer renderer) {
        if (targets.isEmpty()) {
            return;
        }
        Packets set = new Packets();
        HashSet destroy = new HashSet();
        for (SegmentRenderer.Pivot pivot2 : renderer.getRendered().values()) {
            set.add(this.pivotTeleport(pivot2));
            pivot2.getRendered().values().forEach(bone -> {
                UpdateScheme<DisplayBone.BoneData> scheme = bone.getModelUpdateScheme();
                for (DisplayBone.BoneData boneData2 : bone.getModel().values()) {
                    switch (scheme.getUpdateMode(boneData2)) {
                        case NONE: 
                        case UPDATE: {
                            set.add(this.displayData(boneData2, false));
                        }
                    }
                }
                scheme.getAdded().forEach(boneData -> {
                    set.add((Packet<PacketListenerPlayOut>)this.displaySpawn(pivot2, (DisplayBone.BoneData)boneData));
                    set.add(this.displayData((DisplayBone.BoneData)boneData, true));
                });
                scheme.getRemoved().forEach(boneData -> destroy.add(boneData.getId()));
            });
            this.cleanupQueue.add(pivot2::clearDirty);
        }
        this.spawn(set, renderer.getSpawnQueue());
        renderer.getDestroyQueue().forEach((s, pivot) -> {
            destroy.add(pivot.getId());
            destroy.addAll(pivot.getPassengers());
        });
        if (!destroy.isEmpty()) {
            PacketDataSerializer buf = NetworkUtils.createByteBuf();
            buf.a(destroy, PacketDataSerializer::d);
            set.add((Packet<PacketListenerPlayOut>)new PacketPlayOutEntityDestroy(buf));
        }
        NetworkUtils.sendBundled(targets, set);
    }

    private void remove(Set<UUID> targets, SegmentRenderer renderer) {
        if (targets.isEmpty()) {
            return;
        }
        Collection<SegmentRenderer.Pivot> pivots = renderer.getRendered().values();
        IntArrayList ids = new IntArrayList();
        pivots.forEach(pivot -> {
            ids.add(pivot.getId());
            ids.addAll(pivot.getPassengers());
        });
        NetworkUtils.send(targets, (Packet<? super PacketListenerPlayOut>)new PacketPlayOutEntityDestroy((IntList)ids));
    }

    private Packets.PacketSupplier pivotSpawn(SegmentRenderer.Pivot pivot) {
        return NetworkUtils.createPivotSpawn(pivot.getId(), pivot.getUuid(), pivot.getPosition().get());
    }

    private PacketPlayOutEntityMetadata pivotData(SegmentRenderer.Pivot pivot) {
        PacketDataSerializer buf = NetworkUtils.createByteBuf();
        buf.d(pivot.getId());
        EntityUtils.writeData(buf, 0, DataWatcherRegistry.a, (byte)32);
        EntityUtils.writeData(buf, 1, DataWatcherRegistry.b, Integer.MAX_VALUE);
        EntityUtils.writeData(buf, 8, DataWatcherRegistry.d, Float.valueOf(0.0f));
        buf.writeByte(255);
        return new PacketPlayOutEntityMetadata(buf);
    }

    private PacketPlayOutMount pivotMount(SegmentRenderer.Pivot pivot) {
        PacketDataSerializer buf = NetworkUtils.createByteBuf();
        buf.d(pivot.getId());
        buf.a(pivot.getPassengers(), PacketDataSerializer::d);
        return new PacketPlayOutMount(buf);
    }

    private Packets.PacketSupplier pivotTeleport(SegmentRenderer.Pivot pivot) {
        if (!pivot.getPosition().isDirty()) {
            return null;
        }
        this.cleanupQueue.add(() -> pivot.getPosition().clearDirty());
        return NetworkUtils.createPivotTeleport(pivot.getId(), pivot.getPosition().get());
    }

    private PacketPlayOutSpawnEntity displaySpawn(SegmentRenderer.Pivot pivot, DisplayBone.BoneData boneData) {
        Vector3f pos = pivot.getPosition().get();
        return new PacketPlayOutSpawnEntity(boneData.getId(), boneData.getUuid(), (double)pos.x, (double)pos.y, (double)pos.z, 0.0f, 0.0f, EntityTypes.ae, 0, Vec3D.b, 0.0);
    }

    private Packets.PacketSupplier displayData(DisplayBone.BoneData boneData, boolean force) {
        DisplayBone bone = boneData.getBone();
        if (!(force || bone.isDirty() || boneData.getModel().isDirty())) {
            return null;
        }
        this.cleanupQueue.add(bone::clearDirty);
        this.cleanupQueue.add(() -> boneData.getModel().clearDirty());
        return uuid -> this.displayData(uuid, boneData, force);
    }

    private PacketPlayOutEntityMetadata displayData(UUID uuid, DisplayBone.BoneData boneData, boolean force) {
        DisplayBone bone = boneData.getBone();
        PacketDataSerializer buf = NetworkUtils.createByteBuf();
        buf.d(boneData.getId());
        if (force) {
            EntityUtils.writeData(buf, 1, DataWatcherRegistry.b, Integer.MAX_VALUE);
            EntityUtils.writeData(buf, 8, DataWatcherRegistry.b, 0);
        } else if (bone.isTransformDirty()) {
            EntityUtils.writeData(buf, 8, DataWatcherRegistry.b, 0);
        }
        byte update = bone.getSnapshotHandler().getUpdate(uuid);
        bone.getStep().ifDirty(flag -> EntityUtils.writeData(buf, 9, DataWatcherRegistry.b, flag != false ? 0 : 1), force);
        bone.getGlowing().ifDirty(flag -> EntityUtils.writeData(buf, 0, DataWatcherRegistry.a, (byte)(flag != false ? 96 : 32)), force);
        bone.getGlowColor().ifDirty(color -> EntityUtils.writeData(buf, 21, DataWatcherRegistry.b, color), force);
        bone.getBrightness().ifDirty(val -> EntityUtils.writeData(buf, 15, DataWatcherRegistry.b, val), force);
        bone.getPosition().ifDirty(vector3f -> EntityUtils.writeData(buf, 10, DataWatcherRegistry.A, vector3f), force || TMath.getBit(update, 0));
        bone.getScale().ifDirty(vector3f -> EntityUtils.writeData(buf, 11, DataWatcherRegistry.A, vector3f), force || TMath.getBit(update, 2));
        bone.getLeftRotation().ifDirty(quaternionf -> EntityUtils.writeData(buf, 12, DataWatcherRegistry.B, quaternionf.rotateY((float)Math.PI, new Quaternionf())), force || TMath.getBit(update, 1));
        bone.getRightRotation().ifDirty(quaternionf -> EntityUtils.writeData(buf, 13, DataWatcherRegistry.B, quaternionf), force || TMath.getBit(update, 3));
        bone.getBillboard().ifDirty(billboard -> EntityUtils.writeData(buf, 14, DataWatcherRegistry.a, (byte)billboard.ordinal()));
        bone.getVisibility().ifDirty(flag -> EntityUtils.writeData(buf, 16, DataWatcherRegistry.d, Float.valueOf(flag != false ? 4096.0f : 0.0f)), force);
        boneData.getModel().ifDirty(itemStack -> EntityUtils.writeData(buf, 22, DataWatcherRegistry.h, CraftItemStack.asNMSCopy((ItemStack)itemStack)), force);
        bone.getDisplay().ifDirty(display -> EntityUtils.writeData(buf, 23, DataWatcherRegistry.a, display == null ? (byte)0 : (byte)display.ordinal()), force);
        buf.writeByte(255);
        this.cleanupQueue.add(bone::clearDirty);
        return new PacketPlayOutEntityMetadata(buf);
    }
}

