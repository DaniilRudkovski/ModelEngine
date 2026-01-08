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
 *  org.bukkit.craftbukkit.v1_20_R1.inventory.CraftItemStack
 *  org.bukkit.entity.ItemDisplay$ItemDisplayTransform
 *  org.bukkit.inventory.ItemStack
 *  org.joml.Vector3f
 */
package com.ticxo.modelengine.v1_20_R1.parser.vfx;

import com.ticxo.modelengine.api.entity.data.IEntityData;
import com.ticxo.modelengine.api.vfx.render.VFXDisplayRenderer;
import com.ticxo.modelengine.api.vfx.render.VFXRendererParser;
import com.ticxo.modelengine.v1_20_R1.entity.EntityUtils;
import com.ticxo.modelengine.v1_20_R1.network.utils.NetworkUtils;
import com.ticxo.modelengine.v1_20_R1.network.utils.Packets;
import java.util.HashSet;
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
import org.bukkit.craftbukkit.v1_20_R1.inventory.CraftItemStack;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.ItemStack;
import org.joml.Vector3f;

public class VFXDisplayParser
implements VFXRendererParser<VFXDisplayRenderer> {
    @Override
    public void sendToClients(VFXDisplayRenderer renderer) {
        IEntityData data = renderer.getVFX().getBase().getData();
        if (renderer.isRespawnRequired()) {
            this.spawn(data.getTracking().keySet(), renderer.getVFXModel());
            renderer.setRespawnRequired(false);
        } else {
            this.update(data.getTracking().keySet(), renderer.getVFXModel());
            this.spawn(data.getStartTracking(), renderer.getVFXModel());
            this.remove(data.getStopTracking(), renderer.getVFXModel());
        }
    }

    @Override
    public void destroy(VFXDisplayRenderer renderer) {
        IEntityData data = renderer.getVFX().getBase().getData();
        HashSet<UUID> inRange = new HashSet<UUID>(data.getStartTracking());
        inRange.addAll(data.getTracking().keySet());
        inRange.addAll(data.getStopTracking());
        this.remove(inRange, renderer.getVFXModel());
    }

    public void spawn(Set<UUID> targets, VFXDisplayRenderer.VFXModel vfx) {
        if (targets.isEmpty()) {
            return;
        }
        Packets set = new Packets();
        set.add(this.pivotSpawn(vfx));
        set.add((Packet<PacketListenerPlayOut>)this.pivotData(vfx));
        set.add((Packet<PacketListenerPlayOut>)this.vfxSpawn(vfx));
        set.add((Packet<PacketListenerPlayOut>)this.vfxData(vfx, true));
        set.add((Packet<PacketListenerPlayOut>)this.mount(vfx));
        NetworkUtils.sendBundled(targets, set);
    }

    public void update(Set<UUID> targets, VFXDisplayRenderer.VFXModel vfx) {
        if (targets.isEmpty()) {
            return;
        }
        Packets set = new Packets();
        set.add(this.teleport(vfx));
        set.add((Packet<PacketListenerPlayOut>)this.vfxData(vfx, false));
        NetworkUtils.sendBundled(targets, set);
    }

    public void remove(Set<UUID> targets, VFXDisplayRenderer.VFXModel vfx) {
        if (targets.isEmpty()) {
            return;
        }
        PacketDataSerializer buf = NetworkUtils.createByteBuf();
        buf.d(2);
        buf.d(vfx.getPivotId());
        buf.d(vfx.getModelId());
        NetworkUtils.send(targets, (Packet<? super PacketListenerPlayOut>)new PacketPlayOutEntityDestroy(buf));
    }

    private Packets.PacketSupplier pivotSpawn(VFXDisplayRenderer.VFXModel vfx) {
        return NetworkUtils.createPivotSpawn(vfx.getPivotId(), vfx.getPivotUuid(), vfx.getOrigin().get());
    }

    private PacketPlayOutEntityMetadata pivotData(VFXDisplayRenderer.VFXModel vfx) {
        PacketDataSerializer buf = NetworkUtils.createByteBuf();
        buf.d(vfx.getPivotId());
        EntityUtils.writeData(buf, 0, DataWatcherRegistry.a, (byte)32);
        EntityUtils.writeData(buf, 1, DataWatcherRegistry.b, Integer.MAX_VALUE);
        EntityUtils.writeData(buf, 8, DataWatcherRegistry.d, Float.valueOf(0.0f));
        buf.writeByte(255);
        return new PacketPlayOutEntityMetadata(buf);
    }

    private PacketPlayOutSpawnEntity vfxSpawn(VFXDisplayRenderer.VFXModel vfx) {
        Vector3f location = vfx.getOrigin().get();
        return new PacketPlayOutSpawnEntity(vfx.getModelId(), vfx.getModelUuid(), (double)location.x, (double)location.y, (double)location.z, 0.0f, 0.0f, EntityTypes.ae, 0, Vec3D.b, 0.0);
    }

    private PacketPlayOutEntityMetadata vfxData(VFXDisplayRenderer.VFXModel vfx, boolean spawn) {
        if (!spawn && !vfx.isModelDirty()) {
            return null;
        }
        PacketDataSerializer buf = NetworkUtils.createByteBuf();
        buf.d(vfx.getModelId());
        if (spawn) {
            EntityUtils.writeData(buf, 0, DataWatcherRegistry.a, (byte)32);
            EntityUtils.writeData(buf, 1, DataWatcherRegistry.b, Integer.MAX_VALUE);
            EntityUtils.writeData(buf, 8, DataWatcherRegistry.b, 0);
            EntityUtils.writeData(buf, 9, DataWatcherRegistry.b, 1);
            EntityUtils.writeData(buf, 16, DataWatcherRegistry.d, Float.valueOf(4096.0f));
            EntityUtils.writeData(buf, 23, DataWatcherRegistry.a, (byte)ItemDisplay.ItemDisplayTransform.HEAD.ordinal());
        } else if (vfx.isModelDirty()) {
            EntityUtils.writeData(buf, 8, DataWatcherRegistry.b, 0);
        }
        vfx.getPosition().ifDirty(vector3f -> EntityUtils.writeData(buf, 10, DataWatcherRegistry.A, vector3f), spawn);
        vfx.getScale().ifDirty(vector3f -> EntityUtils.writeData(buf, 11, DataWatcherRegistry.A, vector3f), spawn);
        vfx.getLeftRotation().ifDirty(quaternionf -> EntityUtils.writeData(buf, 12, DataWatcherRegistry.B, quaternionf), spawn);
        vfx.getModel().ifDirty(itemStack -> EntityUtils.writeData(buf, 22, DataWatcherRegistry.h, CraftItemStack.asNMSCopy((ItemStack)itemStack)), spawn);
        buf.writeByte(255);
        vfx.clearModelDirty();
        return new PacketPlayOutEntityMetadata(buf);
    }

    private Packets.PacketSupplier teleport(VFXDisplayRenderer.VFXModel vfx) {
        if (!vfx.getOrigin().isDirty()) {
            return null;
        }
        return NetworkUtils.createPivotTeleport(vfx.getPivotId(), vfx.getOrigin().get());
    }

    private PacketPlayOutMount mount(VFXDisplayRenderer.VFXModel vfx) {
        PacketDataSerializer buf = NetworkUtils.createByteBuf();
        buf.d(vfx.getPivotId());
        buf.d(1);
        buf.d(vfx.getModelId());
        return new PacketPlayOutMount(buf);
    }
}

