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
 *  net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket
 *  net.minecraft.network.syncher.EntityDataSerializers
 *  net.minecraft.network.syncher.SynchedEntityData$DataValue
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.phys.Vec3
 *  org.bukkit.Location
 *  org.bukkit.craftbukkit.inventory.CraftItemStack
 *  org.bukkit.inventory.ItemStack
 */
package com.ticxo.modelengine.v1_21_R1.parser.behavior;

import com.ticxo.modelengine.api.entity.data.IEntityData;
import com.ticxo.modelengine.api.model.bone.render.BehaviorRendererParser;
import com.ticxo.modelengine.api.model.bone.render.renderer.HeldItemRenderer;
import com.ticxo.modelengine.v1_21_R1.entity.EntityContainer;
import com.ticxo.modelengine.v1_21_R1.entity.EntityUtils;
import com.ticxo.modelengine.v1_21_R1.network.utils.NetworkUtils;
import com.ticxo.modelengine.v1_21_R1.network.utils.Packets;
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
import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket;
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Location;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
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
        set.add((Packet<ClientGamePacketListener>)this.pivotSpawn(location, renderer));
        set.add((Packet<ClientGamePacketListener>)this.pivotData(renderer));
        for (HeldItemRenderer.Item item : renderer.getRendered().values()) {
            set.add((Packet<ClientGamePacketListener>)this.itemSpawn(location, item));
            set.add((Packet<ClientGamePacketListener>)this.itemData(item, true));
        }
        for (HeldItemRenderer.Item item : renderer.getSpawnQueue().values()) {
            set.add((Packet<ClientGamePacketListener>)this.itemSpawn(location, item));
            set.add((Packet<ClientGamePacketListener>)this.itemData(item, true));
        }
        set.add((Packet<ClientGamePacketListener>)this.mount(renderer));
        NetworkUtils.sendBundled(targets, set);
    }

    private void update(Set<UUID> targets, HeldItemRenderer renderer) {
        if (targets.isEmpty()) {
            return;
        }
        Packets set = new Packets();
        set.add((Packet<ClientGamePacketListener>)this.teleport(renderer));
        for (Object item : renderer.getRendered().values()) {
            set.add((Packet<ClientGamePacketListener>)this.itemData((HeldItemRenderer.Item)item, false));
        }
        Location location = renderer.getActiveModel().getModeledEntity().getBase().getLocation();
        for (HeldItemRenderer.Item item : renderer.getSpawnQueue().values()) {
            set.add((Packet<ClientGamePacketListener>)this.itemSpawn(location, item));
            set.add((Packet<ClientGamePacketListener>)this.itemData(item, true));
        }
        Map destroy = renderer.getDestroyQueue();
        if (!destroy.isEmpty()) {
            set.add((Packet<ClientGamePacketListener>)new ClientboundRemoveEntitiesPacket((IntList)IntArrayList.toList((IntStream)destroy.values().stream().mapToInt(HeldItemRenderer.Item::getId))));
        }
        if (renderer.getPassengers().isDirty()) {
            set.add((Packet<ClientGamePacketListener>)this.mount(renderer));
            renderer.getPassengers().clearDirty();
        }
        NetworkUtils.sendBundled(targets, set);
    }

    private void remove(Set<UUID> targets, HeldItemRenderer renderer) {
        if (targets.isEmpty()) {
            return;
        }
        Map items = renderer.getRendered();
        IntArrayList entityIds = IntArrayList.toList((IntStream)items.values().stream().mapToInt(HeldItemRenderer.Item::getId));
        entityIds.add(renderer.getId());
        NetworkUtils.send(targets, (Packet<? super ClientGamePacketListener>)new ClientboundRemoveEntitiesPacket((IntList)entityIds));
    }

    private ClientboundAddEntityPacket pivotSpawn(Location location, HeldItemRenderer renderer) {
        return new ClientboundAddEntityPacket(renderer.getId(), renderer.getUuid(), location.getX(), location.getY(), location.getZ(), 0.0f, 0.0f, EntityType.ARMOR_STAND, 0, Vec3.ZERO, 0.0);
    }

    private ClientboundSetEntityDataPacket pivotData(HeldItemRenderer renderer) {
        return new ClientboundSetEntityDataPacket(renderer.getId(), EntityUtils.DEFAULT_AREA_EFFECT_CLOUD_DATA);
    }

    private ClientboundAddEntityPacket itemSpawn(Location location, HeldItemRenderer.Item renderer) {
        return new ClientboundAddEntityPacket(renderer.getId(), renderer.getUuid(), location.getX(), location.getY(), location.getZ(), 0.0f, 0.0f, EntityType.ITEM_DISPLAY, 0, Vec3.ZERO, 0.0);
    }

    private ClientboundSetEntityDataPacket itemData(HeldItemRenderer.Item renderer, boolean spawn) {
        if (!spawn && !renderer.isDirty()) {
            return null;
        }
        ArrayList<SynchedEntityData.DataValue> entityData = new ArrayList<SynchedEntityData.DataValue>(11);
        if (spawn) {
            entityData.add(new SynchedEntityData.DataValue(1, EntityDataSerializers.INT, (Object)Integer.MAX_VALUE));
            entityData.add(new SynchedEntityData.DataValue(8, EntityDataSerializers.INT, (Object)0));
            entityData.add(new SynchedEntityData.DataValue(9, EntityDataSerializers.INT, (Object)1));
            entityData.add(new SynchedEntityData.DataValue(17, EntityDataSerializers.FLOAT, (Object)Float.valueOf(4096.0f)));
        } else if (renderer.isTransformDirty()) {
            entityData.add(new SynchedEntityData.DataValue(8, EntityDataSerializers.INT, (Object)0));
        }
        renderer.getGlowing().ifDirty(flag -> entityData.add(new SynchedEntityData.DataValue(0, EntityDataSerializers.BYTE, (Object)((byte)(flag != false ? 96 : 32)))), spawn);
        renderer.getGlowColor().ifDirty(color -> entityData.add(new SynchedEntityData.DataValue(22, EntityDataSerializers.INT, color)), spawn);
        renderer.getPosition().ifDirty(vector3f -> entityData.add(new SynchedEntityData.DataValue(11, EntityDataSerializers.VECTOR3, vector3f)), spawn);
        renderer.getScale().ifDirty(vector3f -> entityData.add(new SynchedEntityData.DataValue(12, EntityDataSerializers.VECTOR3, vector3f)), spawn);
        renderer.getRotation().ifDirty(quaternionf -> entityData.add(new SynchedEntityData.DataValue(13, EntityDataSerializers.QUATERNION, quaternionf)), spawn);
        renderer.getModel().ifDirty(itemStack -> entityData.add(new SynchedEntityData.DataValue(23, EntityDataSerializers.ITEM_STACK, (Object)CraftItemStack.asNMSCopy((ItemStack)itemStack))), spawn);
        renderer.getDisplay().ifDirty(display -> entityData.add(new SynchedEntityData.DataValue(24, EntityDataSerializers.BYTE, (Object)(display == null ? (byte)0 : (byte)display.ordinal()))), spawn);
        renderer.clearDirty();
        return new ClientboundSetEntityDataPacket(renderer.getId(), entityData);
    }

    private ClientboundTeleportEntityPacket teleport(HeldItemRenderer renderer) {
        Location loc = renderer.getActiveModel().getModeledEntity().getBase().getLocation();
        EntityContainer container = EntityContainer.of(renderer.getId());
        container.setPosRaw(loc.getX(), loc.getY(), loc.getZ());
        return new ClientboundTeleportEntityPacket((Entity)container);
    }

    private ClientboundSetPassengersPacket mount(HeldItemRenderer renderer) {
        return new ClientboundSetPassengersPacket(EntityContainer.of(renderer.getId(), renderer.getPassengers()));
    }
}

