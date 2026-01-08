/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.IntArrayList
 *  it.unimi.dsi.fastutil.ints.IntList
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.Component$Serializer
 *  net.minecraft.network.protocol.Packet
 *  net.minecraft.network.protocol.game.ClientGamePacketListener
 *  net.minecraft.network.protocol.game.ClientboundAddEntityPacket
 *  net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket
 *  net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket
 *  net.minecraft.network.protocol.game.ClientboundSetPassengersPacket
 *  net.minecraft.network.syncher.EntityDataSerializers
 *  net.minecraft.network.syncher.SynchedEntityData$DataValue
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.phys.Vec3
 *  org.bukkit.entity.Display$Billboard
 *  org.joml.Vector3f
 */
package com.ticxo.modelengine.v1_21_R4.parser.behavior;

import com.ticxo.modelengine.api.entity.data.IEntityData;
import com.ticxo.modelengine.api.model.bone.render.BehaviorRendererParser;
import com.ticxo.modelengine.api.model.bone.render.renderer.NameTagRenderer;
import com.ticxo.modelengine.v1_21_R4.entity.EntityContainer;
import com.ticxo.modelengine.v1_21_R4.entity.EntityUtils;
import com.ticxo.modelengine.v1_21_R4.network.utils.NetworkUtils;
import com.ticxo.modelengine.v1_21_R4.network.utils.Packets;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.IntStream;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.Vec3;
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
            set.add((Packet<ClientGamePacketListener>)this.pivotData(nameTag, true));
            set.add((Packet<ClientGamePacketListener>)this.tagSpawn(nameTag));
            set.add((Packet<ClientGamePacketListener>)this.tagData(nameTag, true));
            set.add((Packet<ClientGamePacketListener>)this.pivotMount(nameTag));
        }
        for (NameTagRenderer.NameTag nameTag : renderer.getSpawnQueue().values()) {
            set.add(this.pivotSpawn(nameTag));
            set.add((Packet<ClientGamePacketListener>)this.pivotData(nameTag, true));
            set.add((Packet<ClientGamePacketListener>)this.tagSpawn(nameTag));
            set.add((Packet<ClientGamePacketListener>)this.tagData(nameTag, true));
            set.add((Packet<ClientGamePacketListener>)this.pivotMount(nameTag));
        }
        NetworkUtils.sendBundled(targets, set);
    }

    private void update(Set<UUID> targets, NameTagRenderer renderer) {
        if (targets.isEmpty()) {
            return;
        }
        Packets set = new Packets();
        for (NameTagRenderer.NameTag nameTag : renderer.getRendered().values()) {
            set.add((Packet<ClientGamePacketListener>)this.tagData(nameTag, false));
            if (!nameTag.getPosition().isDirty()) continue;
            set.add(this.pivotMove(nameTag));
            nameTag.getPosition().clearDirty();
        }
        for (NameTagRenderer.NameTag nameTag : renderer.getSpawnQueue().values()) {
            set.add(this.pivotSpawn(nameTag));
            set.add((Packet<ClientGamePacketListener>)this.pivotData(nameTag, true));
            set.add((Packet<ClientGamePacketListener>)this.tagSpawn(nameTag));
            set.add((Packet<ClientGamePacketListener>)this.tagData(nameTag, true));
            set.add((Packet<ClientGamePacketListener>)this.pivotMount(nameTag));
        }
        Map destroy = renderer.getDestroyQueue();
        if (!destroy.isEmpty()) {
            IntArrayList entityIds = new IntArrayList(destroy.size() * 2);
            entityIds.addAll((IntList)IntArrayList.toList((IntStream)destroy.values().stream().mapToInt(NameTagRenderer.NameTag::getPivotId)));
            entityIds.addAll((IntList)IntArrayList.toList((IntStream)destroy.values().stream().mapToInt(NameTagRenderer.NameTag::getTagId)));
            set.add((Packet<ClientGamePacketListener>)new ClientboundRemoveEntitiesPacket((IntList)entityIds));
        }
        NetworkUtils.sendBundled(targets, set);
    }

    private void remove(Set<UUID> targets, NameTagRenderer renderer) {
        if (targets.isEmpty()) {
            return;
        }
        Collection nameTags = renderer.getRendered().values();
        IntArrayList entityIds = new IntArrayList(nameTags.size() * 2);
        entityIds.addAll((IntList)IntArrayList.toList((IntStream)nameTags.stream().mapToInt(NameTagRenderer.NameTag::getPivotId)));
        entityIds.addAll((IntList)IntArrayList.toList((IntStream)nameTags.stream().mapToInt(NameTagRenderer.NameTag::getTagId)));
        NetworkUtils.send(targets, (Packet<? super ClientGamePacketListener>)new ClientboundRemoveEntitiesPacket((IntList)entityIds));
    }

    private Packets.PacketSupplier pivotSpawn(NameTagRenderer.NameTag renderer) {
        return NetworkUtils.createPivotSpawn(renderer.getPivotId(), renderer.getPivotUuid(), renderer.getPosition().get());
    }

    private ClientboundSetEntityDataPacket pivotData(NameTagRenderer.NameTag renderer, boolean spawn) {
        if (!spawn && !renderer.isDirty()) {
            return null;
        }
        List<Object> entityData = List.of();
        if (spawn) {
            entityData = EntityUtils.DEFAULT_AREA_EFFECT_CLOUD_DATA;
        }
        renderer.clearDirty();
        return new ClientboundSetEntityDataPacket(renderer.getPivotId(), entityData);
    }

    private ClientboundAddEntityPacket tagSpawn(NameTagRenderer.NameTag renderer) {
        Vector3f pos = renderer.getPosition().get();
        return new ClientboundAddEntityPacket(renderer.getTagId(), renderer.getTagUuid(), (double)pos.x, (double)pos.y, (double)pos.z, 0.0f, 0.0f, EntityType.TEXT_DISPLAY, 0, Vec3.ZERO, 0.0);
    }

    private ClientboundSetEntityDataPacket tagData(NameTagRenderer.NameTag renderer, boolean spawn) {
        if (!spawn && !renderer.isDirty()) {
            return null;
        }
        ArrayList entityData = new ArrayList(7);
        renderer.getVisibility().ifDirty(flag -> entityData.add(new SynchedEntityData.DataValue(17, EntityDataSerializers.FLOAT, (Object)Float.valueOf(flag != false ? 4096.0f : 0.0f))), spawn);
        renderer.getScale().ifDirty(flag -> entityData.add(new SynchedEntityData.DataValue(12, EntityDataSerializers.VECTOR3, flag)), spawn);
        renderer.getTextOpacity().ifDirty(flag -> entityData.add(new SynchedEntityData.DataValue(26, EntityDataSerializers.BYTE, flag)), spawn);
        renderer.getBackgroundColor().ifDirty(flag -> entityData.add(new SynchedEntityData.DataValue(25, EntityDataSerializers.INT, flag)), spawn);
        renderer.getStyle().ifDirty(flags -> entityData.add(new SynchedEntityData.DataValue(27, EntityDataSerializers.BYTE, flags)), spawn);
        renderer.getBillboard().ifDirty(flag -> {
            byte value = switch (flag) {
                default -> throw new MatchException(null, null);
                case Display.Billboard.FIXED -> 0;
                case Display.Billboard.VERTICAL -> 1;
                case Display.Billboard.HORIZONTAL -> 2;
                case Display.Billboard.CENTER -> 3;
            };
            entityData.add(new SynchedEntityData.DataValue(15, EntityDataSerializers.BYTE, (Object)value));
        }, spawn);
        renderer.getJsonString().ifDirty(jsonString -> {
            if (jsonString == null) {
                entityData.add(new SynchedEntityData.DataValue(23, EntityDataSerializers.COMPONENT, (Object)Component.empty()));
            } else {
                entityData.add(new SynchedEntityData.DataValue(23, EntityDataSerializers.COMPONENT, (Object)Component.Serializer.fromJson((String)jsonString, (HolderLookup.Provider)MinecraftServer.getServer().registryAccess())));
            }
        }, spawn);
        renderer.clearDirty();
        return new ClientboundSetEntityDataPacket(renderer.getTagId(), entityData);
    }

    private Packets.PacketSupplier pivotMove(NameTagRenderer.NameTag renderer) {
        return NetworkUtils.createPivotTeleport(renderer.getPivotId(), renderer.getPosition().get());
    }

    private ClientboundSetPassengersPacket pivotMount(NameTagRenderer.NameTag renderer) {
        return new ClientboundSetPassengersPacket(EntityContainer.of(renderer.getPivotId(), renderer.getTagId()));
    }
}

