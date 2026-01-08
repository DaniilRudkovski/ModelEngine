/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
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
 *  net.minecraft.util.Brightness
 *  net.minecraft.world.entity.Display$BillboardConstraints
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.phys.Vec3
 *  org.bukkit.craftbukkit.inventory.CraftItemStack
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.ItemStack
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Vector3f
 */
package com.ticxo.modelengine.v1_21_R7.parser.model;

import com.google.common.collect.ImmutableSet;
import com.ticxo.modelengine.api.entity.BaseEntity;
import com.ticxo.modelengine.api.entity.BukkitEntity;
import com.ticxo.modelengine.api.entity.CullType;
import com.ticxo.modelengine.api.entity.data.IEntityData;
import com.ticxo.modelengine.api.lod.AnimationLODHandler;
import com.ticxo.modelengine.api.model.ModeledEntity;
import com.ticxo.modelengine.api.model.render.DisplayBone;
import com.ticxo.modelengine.api.model.render.DisplayFire;
import com.ticxo.modelengine.api.model.render.DisplayRenderer;
import com.ticxo.modelengine.api.model.render.ModelRendererParser;
import com.ticxo.modelengine.api.utils.data.PooledCollection;
import com.ticxo.modelengine.api.utils.data.UpdateScheme;
import com.ticxo.modelengine.api.utils.math.TMath;
import com.ticxo.modelengine.v1_21_R7.entity.EntityContainer;
import com.ticxo.modelengine.v1_21_R7.entity.EntityUtils;
import com.ticxo.modelengine.v1_21_R7.network.utils.NetworkUtils;
import com.ticxo.modelengine.v1_21_R7.network.utils.Packets;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Brightness;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.Vec3;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public class DisplayParser
implements ModelRendererParser<DisplayRenderer> {
    private final Map<String, Set<UUID>> players = new HashMap<String, Set<UUID>>();
    private final Set<Runnable> cleanupQueue = new HashSet<Runnable>();
    private ModeledEntity modeledEntity;

    @Override
    public void sendToClients(DisplayRenderer renderer) {
        IEntityData data = renderer.getActiveModel().getModeledEntity().getBase().getData();
        this.modeledEntity = renderer.getActiveModel().getModeledEntity();
        for (Map.Entry<UUID, CullType> entry : data.getTracking().entrySet()) {
            switch (entry.getValue()) {
                case NO_CULL: {
                    if (renderer.pollFullUpdate(entry.getKey())) {
                        this.players.computeIfAbsent("NO_CULL_FORCE", s -> new HashSet()).add(entry.getKey());
                        break;
                    }
                    this.players.computeIfAbsent(entry.getValue().name(), s -> new HashSet()).add(entry.getKey());
                    break;
                }
                case MOVEMENT_ONLY: {
                    renderer.pushFullUpdate(entry.getKey());
                    this.players.computeIfAbsent(entry.getValue().name(), s -> new HashSet()).add(entry.getKey());
                    break;
                }
                case CULLED: {
                    this.players.computeIfAbsent(entry.getValue().name(), s -> new HashSet()).add(entry.getKey());
                }
            }
        }
        if (renderer.pollFirstSpawn()) {
            HashSet<UUID> set = new HashSet<UUID>();
            set.addAll((Collection)this.players.getOrDefault("MOVEMENT_ONLY", (Set<UUID>)ImmutableSet.of()));
            set.addAll((Collection)this.players.getOrDefault("NO_CULL", (Set<UUID>)ImmutableSet.of()));
            set.addAll((Collection)this.players.getOrDefault("NO_CULL_FORCE", (Set<UUID>)ImmutableSet.of()));
            this.spawn(set, renderer);
        } else {
            this.spawn(data.getStartTracking(), renderer);
            this.updateRealtime(this.players.getOrDefault("MOVEMENT_ONLY", (Set<UUID>)ImmutableSet.of()), renderer, true, false);
            this.updateRealtime(this.players.getOrDefault("NO_CULL", (Set<UUID>)ImmutableSet.of()), renderer, false, false);
            this.updateRealtime(this.players.getOrDefault("NO_CULL_FORCE", (Set<UUID>)ImmutableSet.of()), renderer, false, true);
            this.updateCulled(this.players.getOrDefault("CULLED", (Set<UUID>)ImmutableSet.of()), renderer);
            this.remove(data.getStopTracking(), renderer);
        }
        this.players.forEach((cullType, players) -> players.clear());
        renderer.getPivot().clearDirty();
        renderer.getHitbox().clearDirty();
        this.cleanupQueue.forEach(Runnable::run);
        this.cleanupQueue.clear();
    }

    @Override
    public void destroy(DisplayRenderer renderer) {
        IEntityData data = renderer.getActiveModel().getModeledEntity().getBase().getData();
        HashSet<UUID> inRange = new HashSet<UUID>(data.getStartTracking());
        inRange.addAll(data.getTracking().keySet());
        inRange.addAll(data.getStopTracking());
        this.remove(inRange, renderer);
    }

    private void spawn(Set<UUID> targets, DisplayRenderer renderer) {
        Player owner;
        BukkitEntity bukkitEntity;
        Entity entity;
        if (targets.isEmpty()) {
            return;
        }
        Packets set = new Packets();
        DisplayRenderer.Pivot pivot = renderer.getPivot();
        set.add(this.pivotSpawn(pivot));
        set.add((Packet<ClientGamePacketListener>)this.pivotData(pivot));
        for (DisplayBone bone : renderer.getRendered().values()) {
            for (DisplayBone.BoneData boneData : bone.getModel().values()) {
                set.add((Packet<ClientGamePacketListener>)this.displaySpawn(pivot, boneData));
                set.add(this.displayData(boneData, true, false));
            }
        }
        for (DisplayBone bone : renderer.getSpawnQueue().values()) {
            for (DisplayBone.BoneData boneData : bone.getModel().values()) {
                set.add((Packet<ClientGamePacketListener>)this.displaySpawn(pivot, boneData));
                set.add(this.displayData(boneData, true, false));
            }
        }
        set.add((Packet<ClientGamePacketListener>)this.pivotMount(pivot));
        BaseEntity<?> base = renderer.getActiveModel().getModeledEntity().getBase();
        if (base instanceof BukkitEntity && (entity = (bukkitEntity = (BukkitEntity)base).getOriginal()) instanceof Player && targets.contains((owner = (Player)entity).getUniqueId())) {
            NetworkUtils.sendBundled(owner.getUniqueId(), set);
        }
        DisplayRenderer.Hitbox hitbox = renderer.getHitbox();
        set.add(this.hitboxSpawnPivot(hitbox));
        set.add((Packet<ClientGamePacketListener>)this.hitboxDataPivot(hitbox));
        set.add((Packet<ClientGamePacketListener>)this.hitboxSpawn(hitbox));
        set.add((Packet<ClientGamePacketListener>)this.hitboxData(hitbox, true));
        set.add((Packet<ClientGamePacketListener>)this.shadowSpawn(hitbox));
        set.add((Packet<ClientGamePacketListener>)this.shadowData(hitbox, true));
        if (hitbox.isFireVisible()) {
            hitbox.getFireDisplay().getInUse().forEach(displayFire -> {
                set.add((Packet<ClientGamePacketListener>)this.fireSpawn(hitbox, (DisplayFire)displayFire));
                set.add((Packet<ClientGamePacketListener>)this.fireData((DisplayFire)displayFire, true));
            });
        }
        set.add((Packet<ClientGamePacketListener>)this.hitboxMount(hitbox));
        NetworkUtils.sendBundled(targets, set, player -> !player.getUniqueId().equals(base.getUUID()));
    }

    private void updateRealtime(Set<UUID> targets, DisplayRenderer renderer, boolean movementOnly, boolean dynamicOnly) {
        if (targets.isEmpty()) {
            return;
        }
        Packets set = new Packets();
        HashSet<Integer> destroy = new HashSet<Integer>();
        DisplayRenderer.Pivot pivot = renderer.getPivot();
        set.add(this.pivotTeleport(pivot));
        if (!movementOnly) {
            for (DisplayBone bone : renderer.getRendered().values()) {
                scheme = bone.getModelUpdateScheme();
                for (DisplayBone.BoneData boneData2 : bone.getModel().values()) {
                    switch (((UpdateScheme)scheme).getUpdateMode(boneData2)) {
                        case NONE: 
                        case UPDATE: {
                            set.add(this.displayData(boneData2, false, dynamicOnly));
                        }
                    }
                }
                ((UpdateScheme)scheme).getAdded().forEach(boneData -> {
                    set.add((Packet<ClientGamePacketListener>)this.displaySpawn(pivot, (DisplayBone.BoneData)boneData));
                    set.add(this.displayData((DisplayBone.BoneData)boneData, true, false));
                });
                ((UpdateScheme)scheme).getRemoved().forEach(boneData -> destroy.add(boneData.getId()));
            }
        } else {
            for (DisplayBone bone : renderer.getRendered().values()) {
                scheme = bone.getModelUpdateScheme();
                for (DisplayBone.BoneData boneData2 : bone.getModel().values()) {
                    switch (((UpdateScheme)scheme).getUpdateMode(boneData2)) {
                        case NONE: 
                        case UPDATE: {
                            set.add((Packet<ClientGamePacketListener>)this.displayVisibleData(boneData2));
                        }
                    }
                }
                ((UpdateScheme)scheme).getAdded().forEach(boneData -> {
                    set.add((Packet<ClientGamePacketListener>)this.displaySpawn(pivot, (DisplayBone.BoneData)boneData));
                    set.add(this.displayData((DisplayBone.BoneData)boneData, true, false));
                });
                ((UpdateScheme)scheme).getRemoved().forEach(boneData -> destroy.add(boneData.getId()));
            }
        }
        for (DisplayBone bone : renderer.getSpawnQueue().values()) {
            for (DisplayBone.BoneData boneData3 : bone.getModel().values()) {
                set.add((Packet<ClientGamePacketListener>)this.displaySpawn(pivot, boneData3));
                set.add(this.displayData(boneData3, true, false));
            }
        }
        renderer.getDestroyQueue().forEach((s, bones) -> bones.getModel().forEach((uuid, boneData) -> destroy.add(boneData.getId())));
        if (pivot.getPassengers().isDirty()) {
            set.add((Packet<ClientGamePacketListener>)this.pivotMount(pivot));
        }
        DisplayRenderer.Hitbox hitbox = renderer.getHitbox();
        AtomicBoolean updateHitboxMount = new AtomicBoolean(false);
        set.add(this.hitboxTeleport(hitbox));
        if (hitbox.getHitboxVisible().isDirty()) {
            if (hitbox.isHitboxVisible()) {
                set.add((Packet<ClientGamePacketListener>)this.hitboxSpawn(hitbox));
                set.add((Packet<ClientGamePacketListener>)this.hitboxData(hitbox, true));
                updateHitboxMount.set(true);
            } else {
                destroy.add(hitbox.getHitboxId());
            }
        } else {
            set.add((Packet<ClientGamePacketListener>)this.hitboxData(hitbox, false));
        }
        if (hitbox.getShadowVisible().isDirty()) {
            if (hitbox.isShadowVisible()) {
                set.add((Packet<ClientGamePacketListener>)this.shadowSpawn(hitbox));
                set.add((Packet<ClientGamePacketListener>)this.shadowData(hitbox, true));
                updateHitboxMount.set(true);
            } else {
                destroy.add(hitbox.getShadowId());
            }
        } else {
            set.add((Packet<ClientGamePacketListener>)this.shadowData(hitbox, false));
        }
        PooledCollection<DisplayFire> fireDisplay = hitbox.getFireDisplay();
        if (hitbox.getFireVisible().isDirty()) {
            if (hitbox.isFireVisible()) {
                fireDisplay.getInUse().forEach(displayFire -> {
                    updateHitboxMount.set(true);
                    set.add((Packet<ClientGamePacketListener>)this.fireSpawn(hitbox, (DisplayFire)displayFire));
                    set.add((Packet<ClientGamePacketListener>)this.fireData((DisplayFire)displayFire, true));
                });
            } else {
                fireDisplay.getAll().forEach(displayFire -> destroy.add(displayFire.getId()));
            }
        } else {
            fireDisplay.processAll(displayFire -> {
                updateHitboxMount.set(true);
                set.add((Packet<ClientGamePacketListener>)this.fireSpawn(hitbox, (DisplayFire)displayFire));
                set.add((Packet<ClientGamePacketListener>)this.fireData((DisplayFire)displayFire, true));
            }, displayFire -> set.add((Packet<ClientGamePacketListener>)this.fireData((DisplayFire)displayFire, false)), displayFire -> destroy.add(displayFire.getId()));
        }
        fireDisplay.getCreated().clear();
        fireDisplay.getReleased().clear();
        if (updateHitboxMount.get()) {
            set.add((Packet<ClientGamePacketListener>)this.hitboxMount(hitbox));
        }
        if (!destroy.isEmpty()) {
            set.add((Packet<ClientGamePacketListener>)new ClientboundRemoveEntitiesPacket((IntList)new IntArrayList(destroy)));
        }
        NetworkUtils.sendBundled(targets, set);
    }

    private void updateCulled(Set<UUID> targets, DisplayRenderer renderer) {
        if (targets.isEmpty()) {
            return;
        }
        IntArrayList entityIds = new IntArrayList();
        Map destroy = renderer.getDestroyQueue();
        if (!destroy.isEmpty()) {
            destroy.values().stream().mapMultiToInt((bones, intConsumer) -> bones.getModel().forEach((uuid, boneData) -> intConsumer.accept(boneData.getId()))).forEach(arg_0 -> ((IntArrayList)entityIds).add(arg_0));
        }
        if (!entityIds.isEmpty()) {
            NetworkUtils.send(targets, (Packet<? super ClientGamePacketListener>)new ClientboundRemoveEntitiesPacket((IntList)entityIds));
        }
    }

    private void remove(Set<UUID> targets, DisplayRenderer renderer) {
        if (targets.isEmpty()) {
            return;
        }
        IntArrayList entityIds = new IntArrayList();
        renderer.getRendered().forEach((s, bone) -> {
            targets.forEach(uuid -> bone.getSnapshotHandler().remove((UUID)uuid));
            bone.getModel().forEach((integer, boneData) -> entityIds.add(boneData.getId()));
        });
        entityIds.add(renderer.getPivot().getId());
        entityIds.add(renderer.getHitbox().getPivotId());
        entityIds.add(renderer.getHitbox().getHitboxId());
        entityIds.add(renderer.getHitbox().getShadowId());
        renderer.getHitbox().getFireDisplay().getAll().forEach(displayFire -> entityIds.add(displayFire.getId()));
        NetworkUtils.send(targets, (Packet<? super ClientGamePacketListener>)new ClientboundRemoveEntitiesPacket((IntList)entityIds));
    }

    private Packets.PacketSupplier pivotSpawn(DisplayRenderer.Pivot pivot) {
        return NetworkUtils.createPivotSpawn(pivot.getId(), pivot.getUuid(), pivot.getPosition().get());
    }

    private ClientboundSetEntityDataPacket pivotData(DisplayRenderer.Pivot pivot) {
        return new ClientboundSetEntityDataPacket(pivot.getId(), EntityUtils.DEFAULT_AREA_EFFECT_CLOUD_DATA);
    }

    private ClientboundSetPassengersPacket pivotMount(DisplayRenderer.Pivot pivot) {
        return new ClientboundSetPassengersPacket(EntityContainer.of(pivot.getId(), pivot.getPassengers()));
    }

    private Packets.PacketSupplier pivotTeleport(DisplayRenderer.Pivot pivot) {
        if (pivot.isOverridden() || !pivot.getPosition().isDirty()) {
            return null;
        }
        return NetworkUtils.createPivotTeleport(pivot.getId(), pivot.getPosition().get());
    }

    private ClientboundAddEntityPacket displaySpawn(DisplayRenderer.Pivot pivot, DisplayBone.BoneData boneData) {
        Vector3f pos = pivot.getPosition().get();
        return new ClientboundAddEntityPacket(boneData.getId(), boneData.getUuid(), (double)pos.x, (double)pos.y, (double)pos.z, 0.0f, 0.0f, EntityType.ITEM_DISPLAY, 0, Vec3.ZERO, 0.0);
    }

    private Packets.PacketSupplier displayData(DisplayBone.BoneData boneData, boolean force, boolean dynamicOnly) {
        DisplayBone bone = boneData.getBone();
        if (!(force || dynamicOnly || bone.isDirty() || boneData.getModel().isDirty())) {
            return null;
        }
        this.cleanupQueue.add(bone::clearDirty);
        this.cleanupQueue.add(() -> boneData.getModel().clearDirty());
        return !force && bone.isSkippable() ? NetworkUtils.lodWrapper(this.modeledEntity, (uuid, tracker) -> this.displayData((UUID)uuid, (AnimationLODHandler.LODTracker)tracker, boneData, false, dynamicOnly)) : uuid -> this.displayData(uuid, null, boneData, force, dynamicOnly);
    }

    private ClientboundSetEntityDataPacket displayData(UUID uuid, @Nullable AnimationLODHandler.LODTracker tracker, DisplayBone.BoneData boneData, boolean force, boolean dynamicOnly) {
        DisplayBone bone = boneData.getBone();
        ArrayList<SynchedEntityData.DataValue> entityData = new ArrayList<SynchedEntityData.DataValue>(13);
        if (force) {
            entityData.add(new SynchedEntityData.DataValue(1, EntityDataSerializers.INT, (Object)Integer.MAX_VALUE));
            entityData.add(new SynchedEntityData.DataValue(8, EntityDataSerializers.INT, (Object)0));
        } else if (bone.isTransformDirty() || dynamicOnly) {
            entityData.add(new SynchedEntityData.DataValue(8, EntityDataSerializers.INT, (Object)0));
        }
        int lodTick = tracker == null ? 1 : tracker.getTickDuration().get();
        boolean lodDirty = tracker != null && tracker.getTickDuration().isDirty();
        byte update = bone.getSnapshotHandler().getUpdate(uuid);
        bone.getStep().ifDirty(flag -> entityData.add(new SynchedEntityData.DataValue(9, EntityDataSerializers.INT, (Object)(flag != false ? 0 : lodTick))), force || dynamicOnly || lodDirty);
        bone.getGlowing().ifDirty(flag -> entityData.add(new SynchedEntityData.DataValue(0, EntityDataSerializers.BYTE, (Object)((byte)(flag != false ? 96 : 32)))), force || dynamicOnly);
        bone.getGlowColor().ifDirty(color -> entityData.add(new SynchedEntityData.DataValue(22, EntityDataSerializers.INT, color)), force || dynamicOnly);
        bone.getBrightness().ifDirty(val -> entityData.add(new SynchedEntityData.DataValue(16, EntityDataSerializers.INT, val)), force || dynamicOnly);
        bone.getPosition().ifDirty(vector3f -> entityData.add(new SynchedEntityData.DataValue(11, EntityDataSerializers.VECTOR3, vector3f)), force || dynamicOnly || TMath.getBit(update, 0));
        bone.getScale().ifDirty(vector3f -> entityData.add(new SynchedEntityData.DataValue(12, EntityDataSerializers.VECTOR3, vector3f)), force || dynamicOnly || TMath.getBit(update, 2));
        bone.getLeftRotation().ifDirty(quaternionf -> entityData.add(new SynchedEntityData.DataValue(13, EntityDataSerializers.QUATERNION, quaternionf)), force || dynamicOnly || TMath.getBit(update, 1));
        bone.getRightRotation().ifDirty(quaternionf -> entityData.add(new SynchedEntityData.DataValue(14, EntityDataSerializers.QUATERNION, quaternionf)), force || dynamicOnly || TMath.getBit(update, 3));
        bone.getBillboard().ifDirty(billboard -> entityData.add(new SynchedEntityData.DataValue(15, EntityDataSerializers.BYTE, (Object)((byte)billboard.ordinal()))), force || dynamicOnly);
        bone.getVisibility().ifDirty(flag -> entityData.add(new SynchedEntityData.DataValue(17, EntityDataSerializers.FLOAT, (Object)Float.valueOf(flag != false ? 4096.0f : 0.0f))), force || dynamicOnly);
        boneData.getModel().ifDirty(itemStack -> entityData.add(new SynchedEntityData.DataValue(23, EntityDataSerializers.ITEM_STACK, (Object)CraftItemStack.asNMSCopy((ItemStack)itemStack))), force || dynamicOnly);
        bone.getDisplay().ifDirty(display -> entityData.add(new SynchedEntityData.DataValue(24, EntityDataSerializers.BYTE, (Object)(display == null ? (byte)0 : (byte)display.ordinal()))), force || dynamicOnly);
        return new ClientboundSetEntityDataPacket(boneData.getId(), entityData);
    }

    private ClientboundSetEntityDataPacket displayVisibleData(DisplayBone.BoneData boneData) {
        DisplayBone bone = boneData.getBone();
        if (!bone.isRenderDirty() && !boneData.getModel().isDirty()) {
            return null;
        }
        ArrayList entityData = new ArrayList(6);
        bone.getGlowing().ifDirty(flag -> entityData.add(new SynchedEntityData.DataValue(0, EntityDataSerializers.BYTE, (Object)((byte)(flag != false ? 96 : 32)))));
        bone.getGlowColor().ifDirty(color -> entityData.add(new SynchedEntityData.DataValue(22, EntityDataSerializers.INT, color)));
        bone.getBrightness().ifDirty(val -> entityData.add(new SynchedEntityData.DataValue(16, EntityDataSerializers.INT, val)));
        bone.getBillboard().ifDirty(billboard -> entityData.add(new SynchedEntityData.DataValue(15, EntityDataSerializers.BYTE, (Object)((byte)billboard.ordinal()))));
        bone.getVisibility().ifDirty(flag -> entityData.add(new SynchedEntityData.DataValue(17, EntityDataSerializers.FLOAT, (Object)Float.valueOf(flag != false ? 4096.0f : 0.0f))));
        boneData.getModel().ifDirty(itemStack -> entityData.add(new SynchedEntityData.DataValue(23, EntityDataSerializers.ITEM_STACK, (Object)CraftItemStack.asNMSCopy((ItemStack)itemStack))));
        bone.getDisplay().ifDirty(display -> entityData.add(new SynchedEntityData.DataValue(24, EntityDataSerializers.BYTE, (Object)(display == null ? (byte)0 : (byte)display.ordinal()))));
        this.cleanupQueue.add(bone::clearDirty);
        return new ClientboundSetEntityDataPacket(boneData.getId(), entityData);
    }

    private Packets.PacketSupplier hitboxSpawnPivot(DisplayRenderer.Hitbox hitbox) {
        return hitbox.isPivotVisible() ? NetworkUtils.createPivotSpawn(hitbox.getPivotId(), hitbox.getPivotUuid(), hitbox.getPosition().get()) : null;
    }

    private ClientboundSetEntityDataPacket hitboxDataPivot(DisplayRenderer.Hitbox hitbox) {
        if (!hitbox.isPivotVisible()) {
            return null;
        }
        return new ClientboundSetEntityDataPacket(hitbox.getPivotId(), EntityUtils.DEFAULT_AREA_EFFECT_CLOUD_DATA);
    }

    private ClientboundAddEntityPacket hitboxSpawn(DisplayRenderer.Hitbox hitbox) {
        if (!hitbox.isHitboxVisible()) {
            return null;
        }
        Vector3f pos = hitbox.getPosition().get();
        return new ClientboundAddEntityPacket(hitbox.getHitboxId(), hitbox.getHitboxUuid(), (double)pos.x, (double)pos.y, (double)pos.z, 0.0f, 0.0f, EntityType.INTERACTION, 0, Vec3.ZERO, 0.0);
    }

    private ClientboundSetEntityDataPacket hitboxData(DisplayRenderer.Hitbox hitbox, boolean spawn) {
        if (!hitbox.isHitboxVisible() || !hitbox.isHitboxDirty() && !spawn) {
            return null;
        }
        ArrayList<SynchedEntityData.DataValue> entityData = new ArrayList<SynchedEntityData.DataValue>(4);
        if (spawn) {
            entityData.add(new SynchedEntityData.DataValue(1, EntityDataSerializers.INT, (Object)Integer.MAX_VALUE));
            entityData.add(new SynchedEntityData.DataValue(10, EntityDataSerializers.BOOLEAN, (Object)false));
        }
        hitbox.getWidth().ifDirty(val -> entityData.add(new SynchedEntityData.DataValue(8, EntityDataSerializers.FLOAT, val)), spawn);
        hitbox.getHeight().ifDirty(val -> entityData.add(new SynchedEntityData.DataValue(9, EntityDataSerializers.FLOAT, val)), spawn);
        return new ClientboundSetEntityDataPacket(hitbox.getHitboxId(), entityData);
    }

    private ClientboundAddEntityPacket shadowSpawn(DisplayRenderer.Hitbox hitbox) {
        if (!hitbox.isShadowVisible()) {
            return null;
        }
        Vector3f pos = hitbox.getPosition().get();
        return new ClientboundAddEntityPacket(hitbox.getShadowId(), hitbox.getShadowUuid(), (double)pos.x, (double)pos.y, (double)pos.z, 0.0f, 0.0f, EntityType.ITEM_DISPLAY, 0, Vec3.ZERO, 0.0);
    }

    private ClientboundSetEntityDataPacket shadowData(DisplayRenderer.Hitbox hitbox, boolean spawn) {
        if (!hitbox.isShadowVisible() || !hitbox.getShadowRadius().isDirty() && !spawn) {
            return null;
        }
        ArrayList<SynchedEntityData.DataValue> entityData = new ArrayList<SynchedEntityData.DataValue>(2);
        if (spawn) {
            entityData.add(new SynchedEntityData.DataValue(1, EntityDataSerializers.INT, (Object)Integer.MAX_VALUE));
        }
        hitbox.getShadowRadius().ifDirty(val -> entityData.add(new SynchedEntityData.DataValue(18, EntityDataSerializers.FLOAT, val)), spawn);
        return new ClientboundSetEntityDataPacket(hitbox.getShadowId(), entityData);
    }

    private ClientboundAddEntityPacket fireSpawn(DisplayRenderer.Hitbox hitbox, DisplayFire fire) {
        Vector3f pos = hitbox.getPosition().get();
        return new ClientboundAddEntityPacket(fire.getId(), fire.getUuid(), (double)pos.x, (double)pos.y, (double)pos.z, 0.0f, 0.0f, EntityType.ITEM_DISPLAY, 0, Vec3.ZERO, 0.0);
    }

    private ClientboundSetEntityDataPacket fireData(DisplayFire fire, boolean spawn) {
        if (!fire.isDirty() && !spawn) {
            return null;
        }
        ArrayList<SynchedEntityData.DataValue> entityData = new ArrayList<SynchedEntityData.DataValue>(2);
        if (spawn) {
            entityData.add(new SynchedEntityData.DataValue(1, EntityDataSerializers.INT, (Object)Integer.MAX_VALUE));
            entityData.add(new SynchedEntityData.DataValue(15, EntityDataSerializers.BYTE, (Object)((byte)Display.BillboardConstraints.VERTICAL.ordinal())));
            entityData.add(new SynchedEntityData.DataValue(16, EntityDataSerializers.INT, (Object)Brightness.FULL_BRIGHT.pack()));
        }
        fire.getFireModel().ifDirty(stack -> entityData.add(new SynchedEntityData.DataValue(23, EntityDataSerializers.ITEM_STACK, (Object)CraftItemStack.asNMSCopy((ItemStack)stack))), spawn);
        fire.getPosition().ifDirty(vector3f -> entityData.add(new SynchedEntityData.DataValue(11, EntityDataSerializers.VECTOR3, vector3f)), spawn);
        fire.getScale().ifDirty(vector3f -> entityData.add(new SynchedEntityData.DataValue(12, EntityDataSerializers.VECTOR3, vector3f)), spawn);
        fire.getVisible().ifDirty(flag -> entityData.add(new SynchedEntityData.DataValue(17, EntityDataSerializers.FLOAT, (Object)Float.valueOf(flag != false ? 4096.0f : 0.0f))), spawn);
        return new ClientboundSetEntityDataPacket(fire.getId(), entityData);
    }

    private Packets.PacketSupplier hitboxTeleport(DisplayRenderer.Hitbox hitbox) {
        if (!hitbox.isPivotVisible() || !hitbox.getPosition().isDirty()) {
            return null;
        }
        this.cleanupQueue.add(() -> hitbox.getPosition().clearDirty());
        return NetworkUtils.createPivotTeleport(hitbox.getPivotId(), hitbox.getPosition().get());
    }

    private ClientboundSetPassengersPacket hitboxMount(DisplayRenderer.Hitbox hitbox) {
        if (!hitbox.isPivotVisible()) {
            return null;
        }
        boolean hitboxVisible = hitbox.isHitboxVisible();
        boolean shadowVisible = hitbox.isShadowVisible();
        ArrayList<Integer> passengers = new ArrayList<Integer>(2 + hitbox.getFireDisplay().getInUse().size());
        if (hitboxVisible) {
            passengers.add(hitbox.getHitboxId());
        }
        if (shadowVisible) {
            passengers.add(hitbox.getShadowId());
        }
        if (hitbox.isFireVisible()) {
            hitbox.getFireDisplay().getInUse().forEach(displayFire -> passengers.add(displayFire.getId()));
        }
        return new ClientboundSetPassengersPacket(EntityContainer.of(hitbox.getPivotId(), passengers));
    }
}

