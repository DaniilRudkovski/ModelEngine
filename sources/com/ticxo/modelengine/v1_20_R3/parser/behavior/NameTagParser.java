/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.PacketDataSerializer
 *  net.minecraft.network.chat.IChatBaseComponent
 *  net.minecraft.network.chat.IChatBaseComponent$ChatSerializer
 *  net.minecraft.network.protocol.Packet
 *  net.minecraft.network.protocol.game.PacketListenerPlayOut
 *  net.minecraft.network.protocol.game.PacketPlayOutEntityDestroy
 *  net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata
 *  net.minecraft.network.protocol.game.PacketPlayOutMount
 *  net.minecraft.network.protocol.game.PacketPlayOutSpawnEntity
 *  net.minecraft.network.syncher.DataWatcherRegistry
 *  net.minecraft.world.entity.EntityTypes
 *  net.minecraft.world.phys.Vec3D
 *  org.bukkit.entity.Display$Billboard
 *  org.joml.Vector3f
 */
package com.ticxo.modelengine.v1_20_R3.parser.behavior;

import com.ticxo.modelengine.api.entity.data.IEntityData;
import com.ticxo.modelengine.api.model.bone.render.BehaviorRendererParser;
import com.ticxo.modelengine.api.model.bone.render.renderer.NameTagRenderer;
import com.ticxo.modelengine.v1_20_R3.entity.EntityUtils;
import com.ticxo.modelengine.v1_20_R3.network.utils.NetworkUtils;
import com.ticxo.modelengine.v1_20_R3.network.utils.Packets;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.PacketListenerPlayOut;
import net.minecraft.network.protocol.game.PacketPlayOutEntityDestroy;
import net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata;
import net.minecraft.network.protocol.game.PacketPlayOutMount;
import net.minecraft.network.protocol.game.PacketPlayOutSpawnEntity;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.entity.Display;
import org.joml.Vector3f;

public class NameTagParser
implements BehaviorRendererParser<NameTagRenderer> {
    @Override
    public void sendToClients(NameTagRenderer renderer) {
        IEntityData data = renderer.getModelRenderer().getActiveModel().getModeledEntity().getBase().getData();
        this.update(data.getTracking().keySet(), renderer);
        this.spawn(data.getStartTracking(), renderer);
        this.remove(data.getStopTracking(), renderer);
    }

    @Override
    public void destroy(NameTagRenderer renderer) {
        IEntityData data = renderer.getActiveModel().getModeledEntity().getBase().getData();
        HashSet<UUID> inRange = new HashSet<UUID>(data.getStartTracking());
        inRange.addAll(data.getTracking().keySet());
        inRange.addAll(data.getStopTracking());
        this.remove(inRange, renderer);
    }

    private void spawn(Set<UUID> targets, NameTagRenderer renderer) {
        if (targets.isEmpty()) {
            return;
        }
        Packets set = new Packets();
        for (NameTagRenderer.NameTag nameTag : renderer.getRendered().values()) {
            set.add(this.pivotSpawn(nameTag));
            set.add((Packet<PacketListenerPlayOut>)this.pivotData(nameTag, true));
            set.add((Packet<PacketListenerPlayOut>)this.tagSpawn(nameTag));
            set.add((Packet<PacketListenerPlayOut>)this.tagData(nameTag, true));
            set.add((Packet<PacketListenerPlayOut>)this.pivotMount(nameTag));
        }
        for (NameTagRenderer.NameTag nameTag : renderer.getSpawnQueue().values()) {
            set.add(this.pivotSpawn(nameTag));
            set.add((Packet<PacketListenerPlayOut>)this.pivotData(nameTag, true));
            set.add((Packet<PacketListenerPlayOut>)this.tagSpawn(nameTag));
            set.add((Packet<PacketListenerPlayOut>)this.tagData(nameTag, true));
            set.add((Packet<PacketListenerPlayOut>)this.pivotMount(nameTag));
        }
        NetworkUtils.sendBundled(targets, set);
    }

    private void update(Set<UUID> targets, NameTagRenderer renderer) {
        if (targets.isEmpty()) {
            return;
        }
        Packets set = new Packets();
        for (NameTagRenderer.NameTag nameTag : renderer.getRendered().values()) {
            set.add((Packet<PacketListenerPlayOut>)this.tagData(nameTag, false));
            if (!nameTag.getPosition().isDirty()) continue;
            set.add(this.pivotMove(nameTag));
            nameTag.getPosition().clearDirty();
        }
        for (NameTagRenderer.NameTag nameTag : renderer.getSpawnQueue().values()) {
            set.add(this.pivotSpawn(nameTag));
            set.add((Packet<PacketListenerPlayOut>)this.pivotData(nameTag, true));
            set.add((Packet<PacketListenerPlayOut>)this.tagSpawn(nameTag));
            set.add((Packet<PacketListenerPlayOut>)this.tagData(nameTag, true));
            set.add((Packet<PacketListenerPlayOut>)this.pivotMount(nameTag));
        }
        Map destroy = renderer.getDestroyQueue();
        if (!destroy.isEmpty()) {
            PacketDataSerializer buf = NetworkUtils.createByteBuf();
            buf.c(destroy.size() * 2);
            destroy.forEach((s, mount) -> {
                buf.c(mount.getPivotId());
                buf.c(mount.getTagId());
            });
            set.add((Packet<PacketListenerPlayOut>)new PacketPlayOutEntityDestroy(buf));
        }
        NetworkUtils.sendBundled(targets, set);
    }

    private void remove(Set<UUID> targets, NameTagRenderer renderer) {
        if (targets.isEmpty()) {
            return;
        }
        PacketDataSerializer buf = NetworkUtils.createByteBuf();
        Collection<NameTagRenderer.NameTag> nameTags = renderer.getRendered().values();
        buf.c(nameTags.size() * 2);
        nameTags.forEach(nameTag -> {
            buf.c(nameTag.getPivotId());
            buf.c(nameTag.getTagId());
        });
        NetworkUtils.send(targets, (Packet<? super PacketListenerPlayOut>)new PacketPlayOutEntityDestroy(buf));
    }

    private Packets.PacketSupplier pivotSpawn(NameTagRenderer.NameTag renderer) {
        return NetworkUtils.createPivotSpawn(renderer.getPivotId(), renderer.getPivotUuid(), renderer.getPosition().get());
    }

    private PacketPlayOutEntityMetadata pivotData(NameTagRenderer.NameTag renderer, boolean spawn) {
        if (!spawn && !renderer.isDirty()) {
            return null;
        }
        PacketDataSerializer buf = NetworkUtils.createByteBuf();
        buf.c(renderer.getPivotId());
        if (spawn) {
            EntityUtils.writeData(buf, 0, DataWatcherRegistry.a, (byte)32);
            EntityUtils.writeData(buf, 1, DataWatcherRegistry.b, Integer.MAX_VALUE);
            EntityUtils.writeData(buf, 8, DataWatcherRegistry.d, Float.valueOf(0.0f));
        }
        buf.k(255);
        renderer.clearDirty();
        return new PacketPlayOutEntityMetadata(buf);
    }

    private PacketPlayOutSpawnEntity tagSpawn(NameTagRenderer.NameTag renderer) {
        Vector3f pos = renderer.getPosition().get();
        return new PacketPlayOutSpawnEntity(renderer.getTagId(), renderer.getTagUuid(), (double)pos.x, (double)pos.y, (double)pos.z, 0.0f, 0.0f, EntityTypes.aY, 0, Vec3D.b, 0.0);
    }

    private PacketPlayOutEntityMetadata tagData(NameTagRenderer.NameTag renderer, boolean spawn) {
        if (!spawn && !renderer.isDirty()) {
            return null;
        }
        PacketDataSerializer buf = NetworkUtils.createByteBuf();
        buf.c(renderer.getTagId());
        renderer.getVisibility().ifDirty(flag -> EntityUtils.writeData(buf, 17, DataWatcherRegistry.d, Float.valueOf(flag != false ? 4096.0f : 0.0f)), spawn);
        renderer.getTextOpacity().ifDirty(flag -> EntityUtils.writeData(buf, 26, DataWatcherRegistry.a, flag), spawn);
        renderer.getBackgroundColor().ifDirty(flag -> EntityUtils.writeData(buf, 25, DataWatcherRegistry.b, flag), spawn);
        renderer.getStyle().ifDirty(flags -> EntityUtils.writeData(buf, 27, DataWatcherRegistry.a, flags), spawn);
        renderer.getBillboard().ifDirty(flag -> {
            byte value = switch (flag) {
                default -> throw new IncompatibleClassChangeError();
                case Display.Billboard.FIXED -> 0;
                case Display.Billboard.VERTICAL -> 1;
                case Display.Billboard.HORIZONTAL -> 2;
                case Display.Billboard.CENTER -> 3;
            };
            EntityUtils.writeData(buf, 15, DataWatcherRegistry.a, value);
        }, spawn);
        renderer.getJsonString().ifDirty(jsonString -> {
            if (jsonString == null) {
                EntityUtils.writeData(buf, 23, DataWatcherRegistry.f, IChatBaseComponent.i());
            } else {
                EntityUtils.writeData(buf, 23, DataWatcherRegistry.f, IChatBaseComponent.ChatSerializer.a((String)jsonString));
            }
        }, spawn);
        buf.k(255);
        renderer.clearDirty();
        return new PacketPlayOutEntityMetadata(buf);
    }

    private Packets.PacketSupplier pivotMove(NameTagRenderer.NameTag renderer) {
        return NetworkUtils.createPivotTeleport(renderer.getPivotId(), renderer.getPosition().get());
    }

    private PacketPlayOutMount pivotMount(NameTagRenderer.NameTag renderer) {
        PacketDataSerializer buf = NetworkUtils.createByteBuf();
        buf.c(renderer.getPivotId());
        buf.c(1);
        buf.c(renderer.getTagId());
        return new PacketPlayOutMount(buf);
    }
}

