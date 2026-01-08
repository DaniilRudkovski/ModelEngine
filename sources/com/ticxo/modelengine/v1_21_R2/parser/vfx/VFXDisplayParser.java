/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.FriendlyByteBuf
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
 *  org.bukkit.entity.ItemDisplay$ItemDisplayTransform
 *  org.bukkit.inventory.ItemStack
 *  org.joml.Vector3f
 */
package com.ticxo.modelengine.v1_21_R2.parser.vfx;

import com.ticxo.modelengine.api.entity.data.IEntityData;
import com.ticxo.modelengine.api.vfx.render.VFXDisplayRenderer;
import com.ticxo.modelengine.api.vfx.render.VFXRendererParser;
import com.ticxo.modelengine.v1_21_R2.entity.EntityContainer;
import com.ticxo.modelengine.v1_21_R2.entity.EntityUtils;
import com.ticxo.modelengine.v1_21_R2.network.utils.NetworkUtils;
import com.ticxo.modelengine.v1_21_R2.network.utils.Packets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import net.minecraft.network.FriendlyByteBuf;
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
        set.add((Packet<ClientGamePacketListener>)this.pivotData(vfx));
        set.add((Packet<ClientGamePacketListener>)this.vfxSpawn(vfx));
        set.add((Packet<ClientGamePacketListener>)this.vfxData(vfx, true));
        set.add((Packet<ClientGamePacketListener>)this.mount(vfx));
        NetworkUtils.sendBundled(targets, set);
    }

    public void update(Set<UUID> targets, VFXDisplayRenderer.VFXModel vfx) {
        if (targets.isEmpty()) {
            return;
        }
        Packets set = new Packets();
        set.add(this.teleport(vfx));
        set.add((Packet<ClientGamePacketListener>)this.vfxData(vfx, false));
        NetworkUtils.sendBundled(targets, set);
    }

    public void remove(Set<UUID> targets, VFXDisplayRenderer.VFXModel vfx) {
        if (targets.isEmpty()) {
            return;
        }
        NetworkUtils.send(targets, (Packet<? super ClientGamePacketListener>)new ClientboundRemoveEntitiesPacket(new int[]{vfx.getPivotId(), vfx.getModelId()}));
    }

    private Packets.PacketSupplier pivotSpawn(VFXDisplayRenderer.VFXModel vfx) {
        return NetworkUtils.createPivotSpawn(vfx.getPivotId(), vfx.getPivotUuid(), vfx.getOrigin().get());
    }

    private ClientboundSetEntityDataPacket pivotData(VFXDisplayRenderer.VFXModel vfx) {
        return new ClientboundSetEntityDataPacket(vfx.getPivotId(), EntityUtils.DEFAULT_AREA_EFFECT_CLOUD_DATA);
    }

    private ClientboundAddEntityPacket vfxSpawn(VFXDisplayRenderer.VFXModel vfx) {
        Vector3f location = vfx.getOrigin().get();
        return new ClientboundAddEntityPacket(vfx.getModelId(), vfx.getModelUuid(), (double)location.x, (double)location.y, (double)location.z, 0.0f, 0.0f, EntityType.ITEM_DISPLAY, 0, Vec3.ZERO, 0.0);
    }

    private ClientboundSetEntityDataPacket vfxData(VFXDisplayRenderer.VFXModel vfx, boolean spawn) {
        if (!spawn && !vfx.isModelDirty()) {
            return null;
        }
        ArrayList<SynchedEntityData.DataValue> entityData = new ArrayList<SynchedEntityData.DataValue>(9);
        FriendlyByteBuf buf = NetworkUtils.createByteBuf();
        buf.writeVarInt(vfx.getModelId());
        if (spawn) {
            entityData.add(new SynchedEntityData.DataValue(0, EntityDataSerializers.BYTE, (Object)32));
            entityData.add(new SynchedEntityData.DataValue(1, EntityDataSerializers.INT, (Object)Integer.MAX_VALUE));
            entityData.add(new SynchedEntityData.DataValue(8, EntityDataSerializers.INT, (Object)0));
            entityData.add(new SynchedEntityData.DataValue(9, EntityDataSerializers.INT, (Object)1));
            entityData.add(new SynchedEntityData.DataValue(17, EntityDataSerializers.FLOAT, (Object)Float.valueOf(4096.0f)));
            entityData.add(new SynchedEntityData.DataValue(24, EntityDataSerializers.BYTE, (Object)((byte)ItemDisplay.ItemDisplayTransform.HEAD.ordinal())));
        } else if (vfx.isModelDirty()) {
            entityData.add(new SynchedEntityData.DataValue(8, EntityDataSerializers.INT, (Object)0));
        }
        vfx.getPosition().ifDirty(vector3f -> entityData.add(new SynchedEntityData.DataValue(11, EntityDataSerializers.VECTOR3, vector3f)), spawn);
        vfx.getScale().ifDirty(vector3f -> entityData.add(new SynchedEntityData.DataValue(12, EntityDataSerializers.VECTOR3, vector3f)), spawn);
        vfx.getLeftRotation().ifDirty(quaternionf -> entityData.add(new SynchedEntityData.DataValue(13, EntityDataSerializers.QUATERNION, quaternionf)), spawn);
        vfx.getModel().ifDirty(itemStack -> entityData.add(new SynchedEntityData.DataValue(23, EntityDataSerializers.ITEM_STACK, (Object)CraftItemStack.asNMSCopy((ItemStack)itemStack))), spawn);
        vfx.clearModelDirty();
        return new ClientboundSetEntityDataPacket(vfx.getModelId(), entityData);
    }

    private Packets.PacketSupplier teleport(VFXDisplayRenderer.VFXModel vfx) {
        if (!vfx.getOrigin().isDirty()) {
            return null;
        }
        return NetworkUtils.createPivotTeleport(vfx.getPivotId(), vfx.getOrigin().get());
    }

    private ClientboundSetPassengersPacket mount(VFXDisplayRenderer.VFXModel vfx) {
        FriendlyByteBuf buf = NetworkUtils.createByteBuf();
        buf.writeVarInt(vfx.getPivotId());
        buf.writeVarInt(1);
        buf.writeVarInt(vfx.getModelId());
        return new ClientboundSetPassengersPacket(EntityContainer.of(vfx.getPivotId(), vfx.getModelId()));
    }
}

