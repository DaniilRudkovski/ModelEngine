/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
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
 *  org.joml.Vector3f
 */
package com.ticxo.modelengine.v1_20_R1.parser.behavior;

import com.ticxo.modelengine.api.entity.BaseEntity;
import com.ticxo.modelengine.api.entity.data.IEntityData;
import com.ticxo.modelengine.api.model.bone.render.BehaviorRendererParser;
import com.ticxo.modelengine.api.model.bone.render.renderer.SubHitboxRenderer;
import com.ticxo.modelengine.v1_20_R1.entity.EntityUtils;
import com.ticxo.modelengine.v1_20_R1.network.utils.NetworkUtils;
import com.ticxo.modelengine.v1_20_R1.network.utils.Packets;
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
            set.add((Packet<PacketListenerPlayOut>)this.pivotData(subHitbox));
            set.add((Packet<PacketListenerPlayOut>)this.hitboxSpawn(subHitbox));
            set.add((Packet<PacketListenerPlayOut>)this.hitboxData(subHitbox, true));
            set.add((Packet<PacketListenerPlayOut>)this.pivotMount(subHitbox));
        }
        for (SubHitboxRenderer.SubHitbox subHitbox : renderer.getSpawnQueue().values()) {
            set.add(this.pivotSpawn(subHitbox));
            set.add((Packet<PacketListenerPlayOut>)this.pivotData(subHitbox));
            set.add((Packet<PacketListenerPlayOut>)this.hitboxSpawn(subHitbox));
            set.add((Packet<PacketListenerPlayOut>)this.hitboxData(subHitbox, true));
            set.add((Packet<PacketListenerPlayOut>)this.pivotMount(subHitbox));
        }
        BaseEntity<?> base = renderer.getActiveModel().getModeledEntity().getBase();
        NetworkUtils.sendBundled(targets, set, player -> player != base.getOriginal());
    }

    private void update(Set<UUID> targets, SubHitboxRenderer renderer) {
        if (targets.isEmpty()) {
            return;
        }
        Packets set = new Packets();
        for (SubHitboxRenderer.SubHitbox subHitbox2 : renderer.getRendered().values()) {
            set.add((Packet<PacketListenerPlayOut>)this.hitboxData(subHitbox2, false));
            if (!subHitbox2.getPosition().isDirty()) continue;
            set.add(this.pivotMove(subHitbox2));
            subHitbox2.getPosition().clearDirty();
        }
        for (SubHitboxRenderer.SubHitbox subHitbox2 : renderer.getSpawnQueue().values()) {
            set.add(this.pivotSpawn(subHitbox2));
            set.add((Packet<PacketListenerPlayOut>)this.pivotData(subHitbox2));
            set.add((Packet<PacketListenerPlayOut>)this.hitboxSpawn(subHitbox2));
            set.add((Packet<PacketListenerPlayOut>)this.hitboxData(subHitbox2, true));
            set.add((Packet<PacketListenerPlayOut>)this.pivotMount(subHitbox2));
        }
        Map destroy = renderer.getDestroyQueue();
        if (!destroy.isEmpty()) {
            PacketDataSerializer buf = NetworkUtils.createByteBuf();
            buf.d(destroy.size() * 2);
            destroy.forEach((s, subHitbox) -> {
                buf.d(subHitbox.getPivotId());
                buf.d(subHitbox.getHitboxId());
            });
            set.add((Packet<PacketListenerPlayOut>)new PacketPlayOutEntityDestroy(buf));
        }
        NetworkUtils.sendBundled(targets, set);
    }

    private void remove(Set<UUID> targets, SubHitboxRenderer renderer) {
        if (targets.isEmpty()) {
            return;
        }
        PacketDataSerializer buf = NetworkUtils.createByteBuf();
        Collection<SubHitboxRenderer.SubHitbox> subHitboxes = renderer.getRendered().values();
        buf.d(subHitboxes.size() * 2);
        subHitboxes.forEach(subHitbox -> {
            buf.d(subHitbox.getPivotId());
            buf.d(subHitbox.getHitboxId());
        });
        NetworkUtils.send(targets, (Packet<? super PacketListenerPlayOut>)new PacketPlayOutEntityDestroy(buf));
    }

    private Packets.PacketSupplier pivotSpawn(SubHitboxRenderer.SubHitbox renderer) {
        return NetworkUtils.createPivotSpawn(renderer.getPivotId(), renderer.getPivotUuid(), renderer.getPosition().get());
    }

    private PacketPlayOutEntityMetadata pivotData(SubHitboxRenderer.SubHitbox renderer) {
        PacketDataSerializer buf = NetworkUtils.createByteBuf();
        buf.d(renderer.getPivotId());
        EntityUtils.writeData(buf, 0, DataWatcherRegistry.a, (byte)32);
        EntityUtils.writeData(buf, 1, DataWatcherRegistry.b, Integer.MAX_VALUE);
        EntityUtils.writeData(buf, 8, DataWatcherRegistry.d, Float.valueOf(0.0f));
        buf.writeByte(255);
        return new PacketPlayOutEntityMetadata(buf);
    }

    private Packets.PacketSupplier pivotMove(SubHitboxRenderer.SubHitbox renderer) {
        return NetworkUtils.createPivotTeleport(renderer.getPivotId(), renderer.getPosition().get());
    }

    private PacketPlayOutMount pivotMount(SubHitboxRenderer.SubHitbox renderer) {
        PacketDataSerializer buf = NetworkUtils.createByteBuf();
        buf.d(renderer.getPivotId());
        buf.d(1);
        buf.d(renderer.getHitboxId());
        return new PacketPlayOutMount(buf);
    }

    private PacketPlayOutSpawnEntity hitboxSpawn(SubHitboxRenderer.SubHitbox renderer) {
        Vector3f pos = renderer.getPosition().get();
        return new PacketPlayOutSpawnEntity(renderer.getHitboxId(), renderer.getHitboxUuid(), (double)pos.x, (double)pos.y, (double)pos.z, 0.0f, 0.0f, EntityTypes.ab, 0, Vec3D.b, 0.0);
    }

    private PacketPlayOutEntityMetadata hitboxData(SubHitboxRenderer.SubHitbox renderer, boolean spawn) {
        if (!spawn && !renderer.isDirty()) {
            return null;
        }
        PacketDataSerializer buf = NetworkUtils.createByteBuf();
        buf.d(renderer.getHitboxId());
        if (spawn) {
            EntityUtils.writeData(buf, 1, DataWatcherRegistry.b, Integer.MAX_VALUE);
            EntityUtils.writeData(buf, 10, DataWatcherRegistry.k, false);
        }
        renderer.getWidth().ifDirty(width -> EntityUtils.writeData(buf, 8, DataWatcherRegistry.d, width), spawn);
        renderer.getHeight().ifDirty(height -> EntityUtils.writeData(buf, 9, DataWatcherRegistry.d, height), spawn);
        buf.writeByte(255);
        renderer.clearDirty();
        return new PacketPlayOutEntityMetadata(buf);
    }
}

