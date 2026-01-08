/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  it.unimi.dsi.fastutil.ints.IntArrayList
 *  it.unimi.dsi.fastutil.ints.IntList
 *  net.minecraft.network.protocol.Packet
 *  net.minecraft.network.protocol.game.ClientGamePacketListener
 *  net.minecraft.network.protocol.game.ClientboundAddEntityPacket
 *  net.minecraft.network.protocol.game.ClientboundMoveEntityPacket$Rot
 *  net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket
 *  net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket
 *  net.minecraft.network.protocol.game.ClientboundSetPassengersPacket
 *  net.minecraft.network.protocol.game.ClientboundUpdateAttributesPacket
 *  net.minecraft.network.syncher.EntityDataSerializers
 *  net.minecraft.network.syncher.SynchedEntityData$DataValue
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.ai.attributes.AttributeInstance
 *  net.minecraft.world.entity.ai.attributes.Attributes
 *  net.minecraft.world.phys.Vec3
 *  org.joml.Vector3f
 */
package com.ticxo.modelengine.v1_21_R5.parser.behavior;

import com.google.common.collect.Lists;
import com.ticxo.modelengine.api.entity.data.IEntityData;
import com.ticxo.modelengine.api.model.bone.render.BehaviorRendererParser;
import com.ticxo.modelengine.api.model.bone.render.renderer.MountRenderer;
import com.ticxo.modelengine.api.utils.math.TMath;
import com.ticxo.modelengine.v1_21_R5.entity.EntityContainer;
import com.ticxo.modelengine.v1_21_R5.entity.EntityUtils;
import com.ticxo.modelengine.v1_21_R5.network.utils.NetworkUtils;
import com.ticxo.modelengine.v1_21_R5.network.utils.Packets;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.IntStream;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundMoveEntityPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateAttributesPacket;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.Vec3;
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
            set.add((Packet<ClientGamePacketListener>)this.pivotData(mount));
            set.add((Packet<ClientGamePacketListener>)this.mountSpawn(mount));
            set.add((Packet<ClientGamePacketListener>)this.mountData(mount));
            set.add((Packet<ClientGamePacketListener>)this.pivotMount(mount));
            set.add((Packet<ClientGamePacketListener>)this.mount(mount));
        }
        for (MountRenderer.Mount mount : renderer.getSpawnQueue().values()) {
            set.add(this.pivotSpawn(mount));
            set.add((Packet<ClientGamePacketListener>)this.pivotData(mount));
            set.add((Packet<ClientGamePacketListener>)this.mountSpawn(mount));
            set.add((Packet<ClientGamePacketListener>)this.mountData(mount));
            set.add((Packet<ClientGamePacketListener>)this.pivotMount(mount));
            set.add((Packet<ClientGamePacketListener>)this.mount(mount));
        }
        NetworkUtils.sendBundled(targets, set);
    }

    private void update(Set<UUID> targets, MountRenderer renderer) {
        if (targets.isEmpty()) {
            return;
        }
        Packets set = new Packets();
        for (MountRenderer.Mount mount : renderer.getRendered().values()) {
            if (mount.getPosition().isDirty()) {
                set.add(this.pivotMove(mount));
                mount.getPosition().clearDirty();
            }
            if (mount.getYaw().isDirty()) {
                set.add((Packet<ClientGamePacketListener>)this.mountRotate(mount));
                mount.getYaw().clearDirty();
            }
            if (mount.getHealth().isDirty()) {
                set.add((Packet<ClientGamePacketListener>)this.mountHealth(mount));
                mount.getHealth().clearDirty();
            }
            if (mount.getMaxHealth().isDirty()) {
                set.add((Packet<ClientGamePacketListener>)this.mountMaxHealth(mount));
                mount.getMaxHealth().clearDirty();
            }
            if (!mount.getPassengers().isDirty()) continue;
            set.add((Packet<ClientGamePacketListener>)this.mount(mount));
            mount.getPassengers().clearDirty();
        }
        for (MountRenderer.Mount mount : renderer.getSpawnQueue().values()) {
            set.add(this.pivotSpawn(mount));
            set.add((Packet<ClientGamePacketListener>)this.pivotData(mount));
            set.add((Packet<ClientGamePacketListener>)this.mountSpawn(mount));
            set.add((Packet<ClientGamePacketListener>)this.mountData(mount));
            set.add((Packet<ClientGamePacketListener>)this.pivotMount(mount));
            set.add((Packet<ClientGamePacketListener>)this.mount(mount));
        }
        Map destroy = renderer.getDestroyQueue();
        if (!destroy.isEmpty()) {
            IntArrayList entityIds = new IntArrayList(destroy.size() * 2);
            entityIds.addAll((IntList)IntArrayList.toList((IntStream)destroy.values().stream().mapToInt(MountRenderer.Mount::getPivotId)));
            entityIds.addAll((IntList)IntArrayList.toList((IntStream)destroy.values().stream().mapToInt(MountRenderer.Mount::getMountId)));
            set.add((Packet<ClientGamePacketListener>)new ClientboundRemoveEntitiesPacket((IntList)entityIds));
        }
        NetworkUtils.sendBundled(targets, set);
    }

    private void remove(Set<UUID> targets, MountRenderer renderer) {
        if (targets.isEmpty()) {
            return;
        }
        Collection mounts = renderer.getRendered().values();
        IntArrayList entityIds = new IntArrayList(mounts.size() * 2);
        entityIds.addAll((IntList)IntArrayList.toList((IntStream)mounts.stream().mapToInt(MountRenderer.Mount::getPivotId)));
        entityIds.addAll((IntList)IntArrayList.toList((IntStream)mounts.stream().mapToInt(MountRenderer.Mount::getMountId)));
        NetworkUtils.send(targets, (Packet<? super ClientGamePacketListener>)new ClientboundRemoveEntitiesPacket((IntList)entityIds));
    }

    private Packets.PacketSupplier pivotSpawn(MountRenderer.Mount renderer) {
        return NetworkUtils.createDisplayPivotSpawn(renderer.getPivotId(), renderer.getPivotUuid(), renderer.getPosition().get());
    }

    private ClientboundSetEntityDataPacket pivotData(MountRenderer.Mount renderer) {
        return new ClientboundSetEntityDataPacket(renderer.getPivotId(), EntityUtils.DEFAULT_PIVOT_DISPLAY_DATA);
    }

    private ClientboundAddEntityPacket mountSpawn(MountRenderer.Mount renderer) {
        Vector3f pos = renderer.getPosition().get();
        return new ClientboundAddEntityPacket(renderer.getMountId(), renderer.getMountUuid(), (double)pos.x, (double)pos.y, (double)pos.z, 0.0f, TMath.byteToRot(renderer.getYaw().get()), EntityType.ARMOR_STAND, 0, Vec3.ZERO, 0.0);
    }

    private ClientboundSetEntityDataPacket mountData(MountRenderer.Mount renderer) {
        return new ClientboundSetEntityDataPacket(renderer.getMountId(), EntityUtils.DEFAULT_ARMOR_STAND_DATA);
    }

    private ClientboundSetPassengersPacket pivotMount(MountRenderer.Mount renderer) {
        return new ClientboundSetPassengersPacket(EntityContainer.of(renderer.getPivotId(), renderer.getMountId()));
    }

    private ClientboundSetPassengersPacket mount(MountRenderer.Mount renderer) {
        return new ClientboundSetPassengersPacket(EntityContainer.of(renderer.getMountId(), renderer.getPassengers()));
    }

    private Packets.PacketSupplier pivotMove(MountRenderer.Mount renderer) {
        return NetworkUtils.createDisplayPivotTeleport(renderer.getPivotId(), renderer.getPosition().get());
    }

    private ClientboundMoveEntityPacket.Rot mountRotate(MountRenderer.Mount renderer) {
        return new ClientboundMoveEntityPacket.Rot(renderer.getMountId(), renderer.getYaw().get().byteValue(), 0, false);
    }

    private ClientboundSetEntityDataPacket mountHealth(MountRenderer.Mount renderer) {
        return new ClientboundSetEntityDataPacket(renderer.getMountId(), List.of(new SynchedEntityData.DataValue(9, EntityDataSerializers.FLOAT, (Object)renderer.getHealth().get())));
    }

    private ClientboundUpdateAttributesPacket mountMaxHealth(MountRenderer.Mount renderer) {
        AttributeInstance attributeInstance = new AttributeInstance(Attributes.MAX_HEALTH, onDirty -> {});
        attributeInstance.setBaseValue((double)renderer.getMaxHealth().get().floatValue());
        return new ClientboundUpdateAttributesPacket(renderer.getMountId(), (Collection)Lists.newArrayList((Object[])new AttributeInstance[]{attributeInstance}));
    }
}

