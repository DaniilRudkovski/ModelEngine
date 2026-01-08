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
 *  net.minecraft.network.protocol.game.ClientboundSetEntityLinkPacket
 *  net.minecraft.network.protocol.game.ClientboundSetPassengersPacket
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.phys.Vec3
 *  org.joml.Vector3f
 */
package com.ticxo.modelengine.v1_21_R2.parser.behavior;

import com.ticxo.modelengine.api.entity.data.IEntityData;
import com.ticxo.modelengine.api.model.bone.render.BehaviorRendererParser;
import com.ticxo.modelengine.api.model.bone.render.renderer.LeashRenderer;
import com.ticxo.modelengine.v1_21_R2.entity.EntityContainer;
import com.ticxo.modelengine.v1_21_R2.entity.EntityUtils;
import com.ticxo.modelengine.v1_21_R2.network.utils.NetworkUtils;
import com.ticxo.modelengine.v1_21_R2.network.utils.Packets;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.IntStream;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityLinkPacket;
import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.Vec3;
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
            set.add((Packet<ClientGamePacketListener>)this.pivotData(leash));
            set.add((Packet<ClientGamePacketListener>)this.leashSpawn(leash));
            set.add((Packet<ClientGamePacketListener>)this.leashData(leash));
            set.add((Packet<ClientGamePacketListener>)this.mount(leash));
            set.add((Packet<ClientGamePacketListener>)this.link(leash, true));
        }
        for (LeashRenderer.Leash leash : renderer.getSpawnQueue().values()) {
            set.add(this.pivotSpawn(leash));
            set.add((Packet<ClientGamePacketListener>)this.pivotData(leash));
            set.add((Packet<ClientGamePacketListener>)this.leashSpawn(leash));
            set.add((Packet<ClientGamePacketListener>)this.leashData(leash));
            set.add((Packet<ClientGamePacketListener>)this.mount(leash));
            set.add((Packet<ClientGamePacketListener>)this.link(leash, true));
        }
        NetworkUtils.sendBundled(targets, set);
    }

    private void update(Set<UUID> targets, LeashRenderer renderer) {
        if (targets.isEmpty()) {
            return;
        }
        Packets set = new Packets();
        for (LeashRenderer.Leash leash2 : renderer.getRendered().values()) {
            set.add((Packet<ClientGamePacketListener>)this.link(leash2, false));
            set.add(this.move(leash2));
        }
        for (LeashRenderer.Leash leash2 : renderer.getSpawnQueue().values()) {
            set.add(this.pivotSpawn(leash2));
            set.add((Packet<ClientGamePacketListener>)this.pivotData(leash2));
            set.add((Packet<ClientGamePacketListener>)this.leashSpawn(leash2));
            set.add((Packet<ClientGamePacketListener>)this.leashData(leash2));
            set.add((Packet<ClientGamePacketListener>)this.mount(leash2));
            set.add((Packet<ClientGamePacketListener>)this.link(leash2, true));
        }
        Map destroy = renderer.getDestroyQueue();
        if (!destroy.isEmpty()) {
            set.add((Packet<ClientGamePacketListener>)new ClientboundRemoveEntitiesPacket((IntList)IntArrayList.toList((IntStream)destroy.values().stream().mapMultiToInt((leash, intConsumer) -> {
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
        Map leashes = renderer.getRendered();
        NetworkUtils.send(targets, (Packet<? super ClientGamePacketListener>)new ClientboundRemoveEntitiesPacket((IntList)IntArrayList.toList((IntStream)leashes.values().stream().mapMultiToInt((leash, intConsumer) -> {
            intConsumer.accept(leash.getLeashId());
            intConsumer.accept(leash.getPivotId());
        }))));
    }

    private Packets.PacketSupplier pivotSpawn(LeashRenderer.Leash renderer) {
        Vector3f pos = renderer.getPosition().get();
        return NetworkUtils.createPivotSpawn(renderer.getPivotId(), renderer.getPivotUUID(), pos.add(0.0f, -0.45f, -0.2f, new Vector3f()));
    }

    private ClientboundAddEntityPacket leashSpawn(LeashRenderer.Leash renderer) {
        Vector3f pos = renderer.getPosition().get();
        return new ClientboundAddEntityPacket(renderer.getLeashId(), renderer.getLeastUUID(), (double)pos.x, (double)pos.y - 0.45, (double)pos.z - 0.2, 0.0f, 0.0f, EntityType.BAT, 0, Vec3.ZERO, 0.0);
    }

    private ClientboundSetEntityDataPacket pivotData(LeashRenderer.Leash renderer) {
        return new ClientboundSetEntityDataPacket(renderer.getPivotId(), EntityUtils.DEFAULT_AREA_EFFECT_CLOUD_DATA);
    }

    private ClientboundSetEntityDataPacket leashData(LeashRenderer.Leash renderer) {
        return new ClientboundSetEntityDataPacket(renderer.getLeashId(), EntityUtils.DEFAULT_BAT_DATA);
    }

    private ClientboundSetPassengersPacket mount(LeashRenderer.Leash renderer) {
        ArrayList<Integer> passengers = new ArrayList<Integer>(1);
        passengers.add(renderer.getLeashId());
        return new ClientboundSetPassengersPacket(EntityContainer.of(renderer.getPivotId(), passengers));
    }

    private Packets.PacketSupplier move(LeashRenderer.Leash renderer) {
        if (!renderer.getPosition().isDirty()) {
            return null;
        }
        Vector3f pos = renderer.getPosition().get();
        return NetworkUtils.createPivotTeleport(renderer.getPivotId(), pos.add(0.0f, -0.45f, -0.2f, new Vector3f()));
    }

    private ClientboundSetEntityLinkPacket link(LeashRenderer.Leash renderer, boolean spawn) {
        if (!spawn && !renderer.getConnected().isDirty()) {
            return null;
        }
        renderer.getConnected().clearDirty();
        return new ClientboundSetEntityLinkPacket((Entity)EntityContainer.of(renderer.getLeashId()), (Entity)EntityContainer.of(renderer.getConnected().get()));
    }
}

