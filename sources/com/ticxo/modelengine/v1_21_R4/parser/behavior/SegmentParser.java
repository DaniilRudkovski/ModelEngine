/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.IntArrayList
 *  it.unimi.dsi.fastutil.ints.IntList
 *  net.minecraft.network.protocol.Packet
 *  net.minecraft.network.protocol.game.ClientGamePacketListener
 *  net.minecraft.network.protocol.game.ClientboundAddEntityPacket
 *  net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket
 *  net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket
 *  net.minecraft.network.protocol.game.ClientboundSetPassengersPacket
 *  net.minecraft.network.syncher.EntityDataSerializers
 *  net.minecraft.network.syncher.SynchedEntityData$DataValue
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.phys.Vec3
 *  org.bukkit.craftbukkit.inventory.CraftItemStack
 *  org.bukkit.inventory.ItemStack
 *  org.joml.Vector3f
 */
package com.ticxo.modelengine.v1_21_R4.parser.behavior;

import com.ticxo.modelengine.api.entity.data.IEntityData;
import com.ticxo.modelengine.api.model.bone.render.BehaviorRendererParser;
import com.ticxo.modelengine.api.model.bone.render.renderer.SegmentRenderer;
import com.ticxo.modelengine.api.model.render.DisplayBone;
import com.ticxo.modelengine.api.utils.data.UpdateScheme;
import com.ticxo.modelengine.api.utils.math.TMath;
import com.ticxo.modelengine.v1_21_R4.entity.EntityContainer;
import com.ticxo.modelengine.v1_21_R4.entity.EntityUtils;
import com.ticxo.modelengine.v1_21_R4.network.utils.NetworkUtils;
import com.ticxo.modelengine.v1_21_R4.network.utils.Packets;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.Vec3;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
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
            set.add((Packet<ClientGamePacketListener>)this.pivotData(pivot));
            for (SegmentRenderer.SegmentDisplayBone bone : pivot.getRendered().values()) {
                for (DisplayBone.BoneData boneData : bone.getModel().values()) {
                    set.add((Packet<ClientGamePacketListener>)this.displaySpawn(pivot, boneData));
                    set.add(this.displayData(boneData, true));
                }
            }
            for (SegmentRenderer.SegmentDisplayBone bone : pivot.getSpawnQueue().values()) {
                for (DisplayBone.BoneData boneData : bone.getModel().values()) {
                    set.add((Packet<ClientGamePacketListener>)this.displaySpawn(pivot, boneData));
                    set.add(this.displayData(boneData, true));
                }
            }
            set.add((Packet<ClientGamePacketListener>)this.pivotMount(pivot));
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
        IntArrayList destroy = new IntArrayList();
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
                    set.add((Packet<ClientGamePacketListener>)this.displaySpawn(pivot2, (DisplayBone.BoneData)boneData));
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
            set.add((Packet<ClientGamePacketListener>)new ClientboundRemoveEntitiesPacket((IntList)destroy));
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
        NetworkUtils.send(targets, (Packet<? super ClientGamePacketListener>)new ClientboundRemoveEntitiesPacket((IntList)ids));
    }

    private Packets.PacketSupplier pivotSpawn(SegmentRenderer.Pivot pivot) {
        return NetworkUtils.createPivotSpawn(pivot.getId(), pivot.getUuid(), pivot.getPosition().get());
    }

    private ClientboundSetEntityDataPacket pivotData(SegmentRenderer.Pivot pivot) {
        return new ClientboundSetEntityDataPacket(pivot.getId(), EntityUtils.DEFAULT_AREA_EFFECT_CLOUD_DATA);
    }

    private ClientboundSetPassengersPacket pivotMount(SegmentRenderer.Pivot pivot) {
        return new ClientboundSetPassengersPacket(EntityContainer.of(pivot.getId(), pivot.getPassengers()));
    }

    private Packets.PacketSupplier pivotTeleport(SegmentRenderer.Pivot pivot) {
        if (!pivot.getPosition().isDirty()) {
            return null;
        }
        this.cleanupQueue.add(() -> pivot.getPosition().clearDirty());
        return NetworkUtils.createPivotTeleport(pivot.getId(), pivot.getPosition().get());
    }

    private ClientboundAddEntityPacket displaySpawn(SegmentRenderer.Pivot pivot, DisplayBone.BoneData boneData) {
        Vector3f pos = pivot.getPosition().get();
        return new ClientboundAddEntityPacket(boneData.getId(), boneData.getUuid(), (double)pos.x, (double)pos.y, (double)pos.z, 0.0f, 0.0f, EntityType.ITEM_DISPLAY, 0, Vec3.ZERO, 0.0);
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

    private ClientboundSetEntityDataPacket displayData(UUID uuid, DisplayBone.BoneData boneData, boolean force) {
        DisplayBone bone = boneData.getBone();
        ArrayList<SynchedEntityData.DataValue> entityData = new ArrayList<SynchedEntityData.DataValue>(13);
        if (force) {
            entityData.add(new SynchedEntityData.DataValue(1, EntityDataSerializers.INT, (Object)Integer.MAX_VALUE));
            entityData.add(new SynchedEntityData.DataValue(8, EntityDataSerializers.INT, (Object)0));
        } else if (bone.isTransformDirty()) {
            entityData.add(new SynchedEntityData.DataValue(8, EntityDataSerializers.INT, (Object)0));
        }
        byte update = bone.getSnapshotHandler().getUpdate(uuid);
        bone.getStep().ifDirty(flag -> entityData.add(new SynchedEntityData.DataValue(9, EntityDataSerializers.INT, (Object)(flag != false ? 0 : 1))), force);
        bone.getGlowing().ifDirty(flag -> entityData.add(new SynchedEntityData.DataValue(0, EntityDataSerializers.BYTE, (Object)((byte)(flag != false ? 96 : 32)))), force);
        bone.getGlowColor().ifDirty(color -> entityData.add(new SynchedEntityData.DataValue(22, EntityDataSerializers.INT, color)), force);
        bone.getBrightness().ifDirty(val -> entityData.add(new SynchedEntityData.DataValue(16, EntityDataSerializers.INT, val)), force);
        bone.getPosition().ifDirty(vector3f -> entityData.add(new SynchedEntityData.DataValue(11, EntityDataSerializers.VECTOR3, vector3f)), force || TMath.getBit(update, 0));
        bone.getScale().ifDirty(vector3f -> entityData.add(new SynchedEntityData.DataValue(12, EntityDataSerializers.VECTOR3, vector3f)), force || TMath.getBit(update, 2));
        bone.getLeftRotation().ifDirty(quaternionf -> entityData.add(new SynchedEntityData.DataValue(13, EntityDataSerializers.QUATERNION, quaternionf)), force || TMath.getBit(update, 1));
        bone.getRightRotation().ifDirty(quaternionf -> entityData.add(new SynchedEntityData.DataValue(14, EntityDataSerializers.QUATERNION, quaternionf)), force || TMath.getBit(update, 3));
        bone.getBillboard().ifDirty(billboard -> entityData.add(new SynchedEntityData.DataValue(15, EntityDataSerializers.BYTE, (Object)((byte)billboard.ordinal()))));
        bone.getVisibility().ifDirty(flag -> entityData.add(new SynchedEntityData.DataValue(17, EntityDataSerializers.FLOAT, (Object)Float.valueOf(flag != false ? 4096.0f : 0.0f))), force);
        boneData.getModel().ifDirty(itemStack -> entityData.add(new SynchedEntityData.DataValue(23, EntityDataSerializers.ITEM_STACK, (Object)CraftItemStack.asNMSCopy((ItemStack)itemStack))), force);
        bone.getDisplay().ifDirty(display -> entityData.add(new SynchedEntityData.DataValue(24, EntityDataSerializers.BYTE, (Object)(display == null ? (byte)0 : (byte)display.ordinal()))), force);
        return new ClientboundSetEntityDataPacket(boneData.getId(), entityData);
    }
}

