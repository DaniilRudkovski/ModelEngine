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
 *  org.joml.Vector3f
 */
package com.ticxo.modelengine.v1_21_R2.parser.behavior;

import com.ticxo.modelengine.api.entity.BaseEntity;
import com.ticxo.modelengine.api.entity.data.IEntityData;
import com.ticxo.modelengine.api.model.bone.render.BehaviorRendererParser;
import com.ticxo.modelengine.api.model.bone.render.renderer.SubHitboxRenderer;
import com.ticxo.modelengine.v1_21_R2.entity.EntityContainer;
import com.ticxo.modelengine.v1_21_R2.entity.EntityUtils;
import com.ticxo.modelengine.v1_21_R2.network.utils.NetworkUtils;
import com.ticxo.modelengine.v1_21_R2.network.utils.Packets;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.ArrayList;
import java.util.Collection;
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
import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public class SubHitboxParser
implements BehaviorRendererParser<SubHitboxRenderer> {
    @Override
    public void sendToClients(SubHitboxRenderer renderer) {
        IEntityData data = renderer.getModelRenderer().getActiveModel().getModeledEntity().getBase().getData();
        this.update(data.getTracking().keySet(), renderer);
        this.spawn(data.getStartTracking(), renderer);
        this.remove(data.getStopTracking(), renderer);
    }

    @Override
    public void destroy(SubHitboxRenderer renderer) {
        IEntityData data = renderer.getActiveModel().getModeledEntity().getBase().getData();
        HashSet<UUID> inRange = new HashSet<UUID>(data.getStartTracking());
        inRange.addAll(data.getTracking().keySet());
        inRange.addAll(data.getStopTracking());
        this.remove(inRange, renderer);
    }

    private void spawn(Set<UUID> targets, SubHitboxRenderer renderer) {
        if (targets.isEmpty()) {
            return;
        }
        Packets set = new Packets();
        for (SubHitboxRenderer.SubHitbox subHitbox : renderer.getRendered().values()) {
            set.add(this.pivotSpawn(subHitbox));
            set.add((Packet<ClientGamePacketListener>)this.pivotData(subHitbox));
            set.add((Packet<ClientGamePacketListener>)this.hitboxSpawn(subHitbox));
            set.add((Packet<ClientGamePacketListener>)this.hitboxData(subHitbox, true));
            set.add((Packet<ClientGamePacketListener>)this.pivotMount(subHitbox));
        }
        for (SubHitboxRenderer.SubHitbox subHitbox : renderer.getSpawnQueue().values()) {
            set.add(this.pivotSpawn(subHitbox));
            set.add((Packet<ClientGamePacketListener>)this.pivotData(subHitbox));
            set.add((Packet<ClientGamePacketListener>)this.hitboxSpawn(subHitbox));
            set.add((Packet<ClientGamePacketListener>)this.hitboxData(subHitbox, true));
            set.add((Packet<ClientGamePacketListener>)this.pivotMount(subHitbox));
        }
        BaseEntity<?> base = renderer.getActiveModel().getModeledEntity().getBase();
        NetworkUtils.sendBundled(targets, set, player -> player != base.getOriginal());
    }

    private void update(Set<UUID> targets, SubHitboxRenderer renderer) {
        if (targets.isEmpty()) {
            return;
        }
        Packets set = new Packets();
        for (SubHitboxRenderer.SubHitbox subHitbox : renderer.getRendered().values()) {
            set.add((Packet<ClientGamePacketListener>)this.hitboxData(subHitbox, false));
            if (!subHitbox.getPosition().isDirty()) continue;
            set.add(this.pivotMove(subHitbox));
            subHitbox.getPosition().clearDirty();
        }
        for (SubHitboxRenderer.SubHitbox subHitbox : renderer.getSpawnQueue().values()) {
            set.add(this.pivotSpawn(subHitbox));
            set.add((Packet<ClientGamePacketListener>)this.pivotData(subHitbox));
            set.add((Packet<ClientGamePacketListener>)this.hitboxSpawn(subHitbox));
            set.add((Packet<ClientGamePacketListener>)this.hitboxData(subHitbox, true));
            set.add((Packet<ClientGamePacketListener>)this.pivotMount(subHitbox));
        }
        Map destroy = renderer.getDestroyQueue();
        if (!destroy.isEmpty()) {
            IntArrayList entityIds = new IntArrayList(destroy.size() * 2);
            entityIds.addAll((IntList)IntArrayList.toList((IntStream)destroy.values().stream().mapToInt(SubHitboxRenderer.SubHitbox::getPivotId)));
            entityIds.addAll((IntList)IntArrayList.toList((IntStream)destroy.values().stream().mapToInt(SubHitboxRenderer.SubHitbox::getHitboxId)));
            set.add((Packet<ClientGamePacketListener>)new ClientboundRemoveEntitiesPacket((IntList)entityIds));
        }
        NetworkUtils.sendBundled(targets, set);
    }

    private void remove(Set<UUID> targets, SubHitboxRenderer renderer) {
        if (targets.isEmpty()) {
            return;
        }
        Collection subHitboxes = renderer.getRendered().values();
        IntArrayList entityIds = new IntArrayList(subHitboxes.size() * 2);
        entityIds.addAll((IntList)IntArrayList.toList((IntStream)subHitboxes.stream().mapToInt(SubHitboxRenderer.SubHitbox::getPivotId)));
        entityIds.addAll((IntList)IntArrayList.toList((IntStream)subHitboxes.stream().mapToInt(SubHitboxRenderer.SubHitbox::getHitboxId)));
        NetworkUtils.send(targets, (Packet<? super ClientGamePacketListener>)new ClientboundRemoveEntitiesPacket((IntList)entityIds));
    }

    private Packets.PacketSupplier pivotSpawn(SubHitboxRenderer.SubHitbox renderer) {
        return NetworkUtils.createPivotSpawn(renderer.getPivotId(), renderer.getPivotUuid(), renderer.getPosition().get());
    }

    private ClientboundSetEntityDataPacket pivotData(SubHitboxRenderer.SubHitbox renderer) {
        return new ClientboundSetEntityDataPacket(renderer.getPivotId(), EntityUtils.DEFAULT_AREA_EFFECT_CLOUD_DATA);
    }

    private Packets.PacketSupplier pivotMove(SubHitboxRenderer.SubHitbox renderer) {
        return NetworkUtils.createPivotTeleport(renderer.getPivotId(), renderer.getPosition().get());
    }

    private ClientboundSetPassengersPacket pivotMount(SubHitboxRenderer.SubHitbox renderer) {
        return new ClientboundSetPassengersPacket(EntityContainer.of(renderer.getPivotId(), renderer.getHitboxId()));
    }

    private ClientboundAddEntityPacket hitboxSpawn(SubHitboxRenderer.SubHitbox renderer) {
        Vector3f pos = renderer.getPosition().get();
        return new ClientboundAddEntityPacket(renderer.getHitboxId(), renderer.getHitboxUuid(), (double)pos.x, (double)pos.y, (double)pos.z, 0.0f, 0.0f, EntityType.INTERACTION, 0, Vec3.ZERO, 0.0);
    }

    private ClientboundSetEntityDataPacket hitboxData(SubHitboxRenderer.SubHitbox renderer, boolean spawn) {
        if (!spawn && !renderer.isDirty()) {
            return null;
        }
        ArrayList<SynchedEntityData.DataValue> entityData = new ArrayList<SynchedEntityData.DataValue>(4);
        if (spawn) {
            entityData.add(new SynchedEntityData.DataValue(1, EntityDataSerializers.INT, (Object)Integer.MAX_VALUE));
            entityData.add(new SynchedEntityData.DataValue(10, EntityDataSerializers.BOOLEAN, (Object)false));
        }
        renderer.getWidth().ifDirty(width -> entityData.add(new SynchedEntityData.DataValue(8, EntityDataSerializers.FLOAT, width)), spawn);
        renderer.getHeight().ifDirty(height -> entityData.add(new SynchedEntityData.DataValue(9, EntityDataSerializers.FLOAT, height)), spawn);
        renderer.clearDirty();
        return new ClientboundSetEntityDataPacket(renderer.getHitboxId(), entityData);
    }
}

