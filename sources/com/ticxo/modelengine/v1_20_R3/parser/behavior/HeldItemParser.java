/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.PacketDataSerializer
 *  net.minecraft.network.protocol.Packet
 *  net.minecraft.network.protocol.game.PacketListenerPlayOut
 *  net.minecraft.network.protocol.game.PacketPlayOutEntityDestroy
 *  net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata
 *  net.minecraft.network.protocol.game.PacketPlayOutEntityTeleport
 *  net.minecraft.network.protocol.game.PacketPlayOutMount
 *  net.minecraft.network.protocol.game.PacketPlayOutSpawnEntity
 *  net.minecraft.network.syncher.DataWatcherRegistry
 *  net.minecraft.world.entity.EntityTypes
 *  net.minecraft.world.phys.Vec3D
 *  org.bukkit.Location
 *  org.bukkit.craftbukkit.v1_20_R3.inventory.CraftItemStack
 *  org.bukkit.inventory.ItemStack
 */
package com.ticxo.modelengine.v1_20_R3.parser.behavior;

import com.ticxo.modelengine.api.entity.data.IEntityData;
import com.ticxo.modelengine.api.model.bone.render.BehaviorRendererParser;
import com.ticxo.modelengine.api.model.bone.render.renderer.HeldItemRenderer;
import com.ticxo.modelengine.api.utils.data.tracker.CollectionDataTracker;
import com.ticxo.modelengine.v1_20_R3.entity.EntityUtils;
import com.ticxo.modelengine.v1_20_R3.network.utils.NetworkUtils;
import com.ticxo.modelengine.v1_20_R3.network.utils.Packets;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.PacketListenerPlayOut;
import net.minecraft.network.protocol.game.PacketPlayOutEntityDestroy;
import net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata;
import net.minecraft.network.protocol.game.PacketPlayOutEntityTeleport;
import net.minecraft.network.protocol.game.PacketPlayOutMount;
import net.minecraft.network.protocol.game.PacketPlayOutSpawnEntity;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

public class HeldItemParser
implements BehaviorRendererParser<HeldItemRenderer> {
    @Override
    public void sendToClients(HeldItemRenderer renderer) {
        IEntityData data = renderer.getModelRenderer().getActiveModel().getModeledEntity().getBase().getData();
        this.update(data.getTracking().keySet(), renderer);
        this.spawn(data.getStartTracking(), renderer);
        this.remove(data.getStopTracking(), renderer);
    }

    @Override
    public void destroy(HeldItemRenderer renderer) {
        IEntityData data = renderer.getActiveModel().getModeledEntity().getBase().getData();
        HashSet<UUID> inRange = new HashSet<UUID>(data.getStartTracking());
        inRange.addAll(data.getTracking().keySet());
        inRange.addAll(data.getStopTracking());
        this.remove(inRange, renderer);
    }

    private void spawn(Set<UUID> targets, HeldItemRenderer renderer) {
        if (targets.isEmpty()) {
            return;
        }
        Packets set = new Packets();
        Location location = renderer.getActiveModel().getModeledEntity().getBase().getLocation();
        set.add((Packet<PacketListenerPlayOut>)this.pivotSpawn(location, renderer));
        set.add((Packet<PacketListenerPlayOut>)this.pivotData(renderer));
        for (HeldItemRenderer.Item item : renderer.getRendered().values()) {
            set.add((Packet<PacketListenerPlayOut>)this.itemSpawn(location, item));
            set.add((Packet<PacketListenerPlayOut>)this.itemData(item, true));
        }
        for (HeldItemRenderer.Item item : renderer.getSpawnQueue().values()) {
            set.add((Packet<PacketListenerPlayOut>)this.itemSpawn(location, item));
            set.add((Packet<PacketListenerPlayOut>)this.itemData(item, true));
        }
        set.add((Packet<PacketListenerPlayOut>)this.mount(renderer));
        NetworkUtils.sendBundled(targets, set);
    }

    private void update(Set<UUID> targets, HeldItemRenderer renderer) {
        if (targets.isEmpty()) {
            return;
        }
        Packets set = new Packets();
        set.add((Packet<PacketListenerPlayOut>)this.teleport(renderer));
        for (Object item2 : renderer.getRendered().values()) {
            set.add((Packet<PacketListenerPlayOut>)this.itemData((HeldItemRenderer.Item)item2, false));
        }
        Location location = renderer.getActiveModel().getModeledEntity().getBase().getLocation();
        for (HeldItemRenderer.Item item3 : renderer.getSpawnQueue().values()) {
            set.add((Packet<PacketListenerPlayOut>)this.itemSpawn(location, item3));
            set.add((Packet<PacketListenerPlayOut>)this.itemData(item3, true));
        }
        Map destroy = renderer.getDestroyQueue();
        if (!destroy.isEmpty()) {
            PacketDataSerializer buf = NetworkUtils.createByteBuf();
            buf.c(destroy.size());
            destroy.forEach((s, item) -> buf.c(item.getId()));
            set.add((Packet<PacketListenerPlayOut>)new PacketPlayOutEntityDestroy(buf));
        }
        if (renderer.getPassengers().isDirty()) {
            set.add((Packet<PacketListenerPlayOut>)this.mount(renderer));
            renderer.getPassengers().clearDirty();
        }
        NetworkUtils.sendBundled(targets, set);
    }

    private void remove(Set<UUID> targets, HeldItemRenderer renderer) {
        if (targets.isEmpty()) {
            return;
        }
        PacketDataSerializer buf = NetworkUtils.createByteBuf();
        Map items = renderer.getRendered();
        buf.c(1 + items.size());
        buf.c(renderer.getId());
        items.forEach((s, item) -> buf.c(item.getId()));
        NetworkUtils.send(targets, (Packet<? super PacketListenerPlayOut>)new PacketPlayOutEntityDestroy(buf));
    }

    private PacketPlayOutSpawnEntity pivotSpawn(Location location, HeldItemRenderer renderer) {
        return new PacketPlayOutSpawnEntity(renderer.getId(), renderer.getUuid(), location.getX(), location.getY(), location.getZ(), 0.0f, 0.0f, EntityTypes.d, 0, Vec3D.b, 0.0);
    }

    private PacketPlayOutEntityMetadata pivotData(HeldItemRenderer renderer) {
        PacketDataSerializer buf = NetworkUtils.createByteBuf();
        buf.c(renderer.getId());
        EntityUtils.writeData(buf, 0, DataWatcherRegistry.a, (byte)32);
        EntityUtils.writeData(buf, 1, DataWatcherRegistry.b, Integer.MAX_VALUE);
        EntityUtils.writeData(buf, 15, DataWatcherRegistry.a, (byte)16);
        buf.k(255);
        return new PacketPlayOutEntityMetadata(buf);
    }

    private PacketPlayOutSpawnEntity itemSpawn(Location location, HeldItemRenderer.Item renderer) {
        return new PacketPlayOutSpawnEntity(renderer.getId(), renderer.getUuid(), location.getX(), location.getY(), location.getZ(), 0.0f, 0.0f, EntityTypes.af, 0, Vec3D.b, 0.0);
    }

    private PacketPlayOutEntityMetadata itemData(HeldItemRenderer.Item renderer, boolean spawn) {
        if (!spawn && !renderer.isDirty()) {
            return null;
        }
        PacketDataSerializer buf = NetworkUtils.createByteBuf();
        buf.c(renderer.getId());
        if (spawn) {
            EntityUtils.writeData(buf, 1, DataWatcherRegistry.b, Integer.MAX_VALUE);
            EntityUtils.writeData(buf, 8, DataWatcherRegistry.b, 0);
            EntityUtils.writeData(buf, 9, DataWatcherRegistry.b, 1);
            EntityUtils.writeData(buf, 17, DataWatcherRegistry.d, Float.valueOf(4096.0f));
        } else if (renderer.isTransformDirty()) {
            EntityUtils.writeData(buf, 8, DataWatcherRegistry.b, 0);
        }
        renderer.getGlowing().ifDirty(flag -> EntityUtils.writeData(buf, 0, DataWatcherRegistry.a, (byte)(flag != false ? 96 : 32)), spawn);
        renderer.getGlowColor().ifDirty(color -> EntityUtils.writeData(buf, 22, DataWatcherRegistry.b, color), spawn);
        renderer.getPosition().ifDirty(vector3f -> EntityUtils.writeData(buf, 11, DataWatcherRegistry.A, vector3f), spawn);
        renderer.getScale().ifDirty(vector3f -> EntityUtils.writeData(buf, 12, DataWatcherRegistry.A, vector3f), spawn);
        renderer.getRotation().ifDirty(quaternionf -> EntityUtils.writeData(buf, 13, DataWatcherRegistry.B, quaternionf), spawn);
        renderer.getModel().ifDirty(itemStack -> EntityUtils.writeData(buf, 23, DataWatcherRegistry.h, CraftItemStack.asNMSCopy((ItemStack)itemStack)), spawn);
        renderer.getDisplay().ifDirty(display -> EntityUtils.writeData(buf, 24, DataWatcherRegistry.a, display == null ? (byte)0 : (byte)display.ordinal()), spawn);
        buf.k(255);
        renderer.clearDirty();
        return new PacketPlayOutEntityMetadata(buf);
    }

    private PacketPlayOutEntityTeleport teleport(HeldItemRenderer renderer) {
        Location location = renderer.getActiveModel().getModeledEntity().getBase().getLocation();
        PacketDataSerializer buf = NetworkUtils.createByteBuf();
        buf.c(renderer.getId());
        buf.a(location.getX());
        buf.a(location.getY());
        buf.a(location.getZ());
        buf.k(0);
        buf.k(0);
        buf.a(false);
        return new PacketPlayOutEntityTeleport(buf);
    }

    private PacketPlayOutMount mount(HeldItemRenderer renderer) {
        PacketDataSerializer buf = NetworkUtils.createByteBuf();
        buf.c(renderer.getId());
        CollectionDataTracker<Integer> set = renderer.getPassengers();
        buf.c(set.size());
        set.forEach(arg_0 -> ((PacketDataSerializer)buf).c(arg_0));
        return new PacketPlayOutMount(buf);
    }
}

