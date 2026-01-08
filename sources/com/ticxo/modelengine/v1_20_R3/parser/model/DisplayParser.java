/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  it.unimi.dsi.fastutil.ints.IntArrayList
 *  it.unimi.dsi.fastutil.ints.IntList
 *  net.minecraft.network.PacketDataSerializer
 *  net.minecraft.network.protocol.Packet
 *  net.minecraft.network.protocol.game.PacketListenerPlayOut
 *  net.minecraft.network.protocol.game.PacketPlayOutEntityDestroy
 *  net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata
 *  net.minecraft.network.protocol.game.PacketPlayOutMount
 *  net.minecraft.network.protocol.game.PacketPlayOutSpawnEntity
 *  net.minecraft.network.syncher.DataWatcher$b
 *  net.minecraft.network.syncher.DataWatcherRegistry
 *  net.minecraft.util.Brightness
 *  net.minecraft.world.entity.Display$BillboardConstraints
 *  net.minecraft.world.entity.EntityTypes
 *  net.minecraft.world.phys.Vec3D
 *  org.bukkit.craftbukkit.v1_20_R3.inventory.CraftItemStack
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.ItemStack
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Vector3f
 */
package com.ticxo.modelengine.v1_20_R3.parser.model;

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
import com.ticxo.modelengine.v1_20_R3.entity.EntityUtils;
import com.ticxo.modelengine.v1_20_R3.network.utils.NetworkUtils;
import com.ticxo.modelengine.v1_20_R3.network.utils.Packets;
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
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.PacketListenerPlayOut;
import net.minecraft.network.protocol.game.PacketPlayOutEntityDestroy;
import net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata;
import net.minecraft.network.protocol.game.PacketPlayOutMount;
import net.minecraft.network.protocol.game.PacketPlayOutSpawnEntity;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.util.Brightness;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftItemStack;
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
        set.add((Packet<PacketListenerPlayOut>)this.pivotData(pivot));
        for (DisplayBone bone : renderer.getRendered().values()) {
            for (DisplayBone.BoneData boneData : bone.getModel().values()) {
                set.add((Packet<PacketListenerPlayOut>)this.displaySpawn(pivot, boneData));
                set.add(this.displayData(boneData, true, false));
            }
        }
        for (DisplayBone bone : renderer.getSpawnQueue().values()) {
            for (DisplayBone.BoneData boneData : bone.getModel().values()) {
                set.add((Packet<PacketListenerPlayOut>)this.displaySpawn(pivot, boneData));
                set.add(this.displayData(boneData, true, false));
            }
        }
        set.add((Packet<PacketListenerPlayOut>)this.pivotMount(pivot));
        BaseEntity<?> base = renderer.getActiveModel().getModeledEntity().getBase();
        if (base instanceof BukkitEntity && (entity = (bukkitEntity = (BukkitEntity)base).getOriginal()) instanceof Player && targets.contains((owner = (Player)entity).getUniqueId())) {
            NetworkUtils.sendBundled(owner.getUniqueId(), set);
        }
        DisplayRenderer.Hitbox hitbox = renderer.getHitbox();
        set.add(this.hitboxSpawnPivot(hitbox));
        set.add((Packet<PacketListenerPlayOut>)this.hitboxDataPivot(hitbox));
        set.add((Packet<PacketListenerPlayOut>)this.hitboxSpawn(hitbox));
        set.add((Packet<PacketListenerPlayOut>)this.hitboxData(hitbox, true));
        set.add((Packet<PacketListenerPlayOut>)this.shadowSpawn(hitbox));
        set.add((Packet<PacketListenerPlayOut>)this.shadowData(hitbox, true));
        if (hitbox.isFireVisible()) {
            hitbox.getFireDisplay().getInUse().forEach(displayFire -> {
                set.add((Packet<PacketListenerPlayOut>)this.fireSpawn(hitbox, (DisplayFire)displayFire));
                set.add((Packet<PacketListenerPlayOut>)this.fireData((DisplayFire)displayFire, true));
            });
        }
        set.add((Packet<PacketListenerPlayOut>)this.hitboxMount(hitbox));
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
                    set.add((Packet<PacketListenerPlayOut>)this.displaySpawn(pivot, (DisplayBone.BoneData)boneData));
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
                            set.add((Packet<PacketListenerPlayOut>)this.displayVisibleData(boneData2));
                        }
                    }
                }
                ((UpdateScheme)scheme).getAdded().forEach(boneData -> {
                    set.add((Packet<PacketListenerPlayOut>)this.displaySpawn(pivot, (DisplayBone.BoneData)boneData));
                    set.add(this.displayData((DisplayBone.BoneData)boneData, true, false));
                });
                ((UpdateScheme)scheme).getRemoved().forEach(boneData -> destroy.add(boneData.getId()));
            }
        }
        for (DisplayBone bone : renderer.getSpawnQueue().values()) {
            for (DisplayBone.BoneData boneData3 : bone.getModel().values()) {
                set.add((Packet<PacketListenerPlayOut>)this.displaySpawn(pivot, boneData3));
                set.add(this.displayData(boneData3, true, false));
            }
        }
        renderer.getDestroyQueue().forEach((s, bones) -> bones.getModel().forEach((uuid, boneData) -> destroy.add(boneData.getId())));
        if (pivot.getPassengers().isDirty()) {
            set.add((Packet<PacketListenerPlayOut>)this.pivotMount(pivot));
        }
        DisplayRenderer.Hitbox hitbox = renderer.getHitbox();
        AtomicBoolean updateHitboxMount = new AtomicBoolean(false);
        set.add(this.hitboxTeleport(hitbox));
        if (hitbox.getHitboxVisible().isDirty()) {
            if (hitbox.isHitboxVisible()) {
                set.add((Packet<PacketListenerPlayOut>)this.hitboxSpawn(hitbox));
                set.add((Packet<PacketListenerPlayOut>)this.hitboxData(hitbox, true));
                updateHitboxMount.set(true);
            } else {
                destroy.add(hitbox.getHitboxId());
            }
        } else {
            set.add((Packet<PacketListenerPlayOut>)this.hitboxData(hitbox, false));
        }
        if (hitbox.getShadowVisible().isDirty()) {
            if (hitbox.isShadowVisible()) {
                set.add((Packet<PacketListenerPlayOut>)this.shadowSpawn(hitbox));
                set.add((Packet<PacketListenerPlayOut>)this.shadowData(hitbox, true));
                updateHitboxMount.set(true);
            } else {
                destroy.add(hitbox.getShadowId());
            }
        } else {
            set.add((Packet<PacketListenerPlayOut>)this.shadowData(hitbox, false));
        }
        PooledCollection<DisplayFire> fireDisplay = hitbox.getFireDisplay();
        if (hitbox.getFireVisible().isDirty()) {
            if (hitbox.isFireVisible()) {
                fireDisplay.getInUse().forEach(displayFire -> {
                    updateHitboxMount.set(true);
                    set.add((Packet<PacketListenerPlayOut>)this.fireSpawn(hitbox, (DisplayFire)displayFire));
                    set.add((Packet<PacketListenerPlayOut>)this.fireData((DisplayFire)displayFire, true));
                });
            } else {
                fireDisplay.getAll().forEach(displayFire -> destroy.add(displayFire.getId()));
            }
        } else {
            fireDisplay.processAll(displayFire -> {
                updateHitboxMount.set(true);
                set.add((Packet<PacketListenerPlayOut>)this.fireSpawn(hitbox, (DisplayFire)displayFire));
                set.add((Packet<PacketListenerPlayOut>)this.fireData((DisplayFire)displayFire, true));
            }, displayFire -> set.add((Packet<PacketListenerPlayOut>)this.fireData((DisplayFire)displayFire, false)), displayFire -> destroy.add(displayFire.getId()));
        }
        fireDisplay.getCreated().clear();
        fireDisplay.getReleased().clear();
        if (updateHitboxMount.get()) {
            set.add((Packet<PacketListenerPlayOut>)this.hitboxMount(hitbox));
        }
        if (!destroy.isEmpty()) {
            PacketDataSerializer buf = NetworkUtils.createByteBuf();
            buf.c(destroy.size());
            destroy.forEach(arg_0 -> ((PacketDataSerializer)buf).c(arg_0));
            set.add((Packet<PacketListenerPlayOut>)new PacketPlayOutEntityDestroy(buf));
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
            NetworkUtils.send(targets, (Packet<? super PacketListenerPlayOut>)new PacketPlayOutEntityDestroy((IntList)entityIds));
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
        NetworkUtils.send(targets, (Packet<? super PacketListenerPlayOut>)new PacketPlayOutEntityDestroy((IntList)entityIds));
    }

    private Packets.PacketSupplier pivotSpawn(DisplayRenderer.Pivot pivot) {
        if (pivot.isOverridden()) {
            return null;
        }
        return NetworkUtils.createPivotSpawn(pivot.getId(), pivot.getUuid(), pivot.getPosition().get());
    }

    private PacketPlayOutEntityMetadata pivotData(DisplayRenderer.Pivot pivot) {
        if (pivot.isOverridden()) {
            return null;
        }
        PacketDataSerializer buf = NetworkUtils.createByteBuf();
        buf.c(pivot.getId());
        EntityUtils.writeData(buf, 0, DataWatcherRegistry.a, (byte)32);
        EntityUtils.writeData(buf, 1, DataWatcherRegistry.b, Integer.MAX_VALUE);
        EntityUtils.writeData(buf, 8, DataWatcherRegistry.d, Float.valueOf(0.0f));
        buf.k(255);
        return new PacketPlayOutEntityMetadata(buf);
    }

    private PacketPlayOutMount pivotMount(DisplayRenderer.Pivot pivot) {
        PacketDataSerializer buf = NetworkUtils.createByteBuf();
        buf.c(pivot.getId());
        buf.c(pivot.getPassengers().size());
        pivot.getPassengers().forEach(arg_0 -> ((PacketDataSerializer)buf).c(arg_0));
        return new PacketPlayOutMount(buf);
    }

    private Packets.PacketSupplier pivotTeleport(DisplayRenderer.Pivot pivot) {
        if (pivot.isOverridden() || !pivot.getPosition().isDirty()) {
            return null;
        }
        return NetworkUtils.createPivotTeleport(pivot.getId(), pivot.getPosition().get());
    }

    private PacketPlayOutSpawnEntity displaySpawn(DisplayRenderer.Pivot pivot, DisplayBone.BoneData boneData) {
        Vector3f pos = pivot.getPosition().get();
        return new PacketPlayOutSpawnEntity(boneData.getId(), boneData.getUuid(), (double)pos.x, (double)pos.y, (double)pos.z, 0.0f, 0.0f, EntityTypes.af, 0, Vec3D.b, 0.0);
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

    private PacketPlayOutEntityMetadata displayData(UUID uuid, @Nullable AnimationLODHandler.LODTracker tracker, DisplayBone.BoneData boneData, boolean force, boolean dynamicOnly) {
        DisplayBone bone = boneData.getBone();
        PacketDataSerializer buf = NetworkUtils.createByteBuf();
        buf.c(boneData.getId());
        if (force) {
            EntityUtils.writeData(buf, 1, DataWatcherRegistry.b, Integer.MAX_VALUE);
            EntityUtils.writeData(buf, 8, DataWatcherRegistry.b, 0);
        } else if (bone.isTransformDirty() || dynamicOnly) {
            EntityUtils.writeData(buf, 8, DataWatcherRegistry.b, 0);
        }
        int lodTick = tracker == null ? 1 : tracker.getTickDuration().get();
        boolean lodDirty = tracker != null && tracker.getTickDuration().isDirty();
        byte update = bone.getSnapshotHandler().getUpdate(uuid);
        bone.getStep().ifDirty(flag -> EntityUtils.writeData(buf, 9, DataWatcherRegistry.b, flag != false ? 0 : lodTick), force || dynamicOnly || lodDirty);
        bone.getGlowing().ifDirty(flag -> EntityUtils.writeData(buf, 0, DataWatcherRegistry.a, (byte)(flag != false ? 96 : 32)), force || dynamicOnly);
        bone.getGlowColor().ifDirty(color -> EntityUtils.writeData(buf, 22, DataWatcherRegistry.b, color), force || dynamicOnly);
        bone.getBrightness().ifDirty(val -> EntityUtils.writeData(buf, 16, DataWatcherRegistry.b, val), force || dynamicOnly);
        bone.getPosition().ifDirty(vector3f -> EntityUtils.writeData(buf, 11, DataWatcherRegistry.A, vector3f), force || dynamicOnly || TMath.getBit(update, 0));
        bone.getScale().ifDirty(vector3f -> EntityUtils.writeData(buf, 12, DataWatcherRegistry.A, vector3f), force || dynamicOnly || TMath.getBit(update, 2));
        bone.getLeftRotation().ifDirty(quaternionf -> EntityUtils.writeData(buf, 13, DataWatcherRegistry.B, quaternionf), force || dynamicOnly || TMath.getBit(update, 1));
        bone.getRightRotation().ifDirty(quaternionf -> EntityUtils.writeData(buf, 14, DataWatcherRegistry.B, quaternionf), force || dynamicOnly || TMath.getBit(update, 3));
        bone.getBillboard().ifDirty(billboard -> EntityUtils.writeData(buf, 15, DataWatcherRegistry.a, (byte)billboard.ordinal()), force || dynamicOnly);
        bone.getVisibility().ifDirty(flag -> EntityUtils.writeData(buf, 17, DataWatcherRegistry.d, Float.valueOf(flag != false ? 4096.0f : 0.0f)), force || dynamicOnly);
        boneData.getModel().ifDirty(itemStack -> EntityUtils.writeData(buf, 23, DataWatcherRegistry.h, CraftItemStack.asNMSCopy((ItemStack)itemStack)), force || dynamicOnly);
        bone.getDisplay().ifDirty(display -> EntityUtils.writeData(buf, 24, DataWatcherRegistry.a, display == null ? (byte)0 : (byte)display.ordinal()), force || dynamicOnly);
        buf.k(255);
        this.cleanupQueue.add(bone::clearDirty);
        this.cleanupQueue.add(() -> boneData.getModel().clearDirty());
        return new PacketPlayOutEntityMetadata(buf);
    }

    private PacketPlayOutEntityMetadata displayVisibleData(DisplayBone.BoneData boneData) {
        DisplayBone bone = boneData.getBone();
        if (!bone.isRenderDirty()) {
            return null;
        }
        PacketDataSerializer buf = NetworkUtils.createByteBuf();
        buf.c(boneData.getId());
        bone.getGlowing().ifDirty(flag -> EntityUtils.writeData(buf, 0, DataWatcherRegistry.a, (byte)(flag != false ? 96 : 32)));
        bone.getGlowColor().ifDirty(color -> EntityUtils.writeData(buf, 22, DataWatcherRegistry.b, color));
        bone.getBrightness().ifDirty(val -> EntityUtils.writeData(buf, 16, DataWatcherRegistry.b, val));
        bone.getBillboard().ifDirty(billboard -> EntityUtils.writeData(buf, 15, DataWatcherRegistry.a, (byte)billboard.ordinal()));
        bone.getVisibility().ifDirty(flag -> EntityUtils.writeData(buf, 17, DataWatcherRegistry.d, Float.valueOf(flag != false ? 4096.0f : 0.0f)));
        boneData.getModel().ifDirty(itemStack -> EntityUtils.writeData(buf, 23, DataWatcherRegistry.h, CraftItemStack.asNMSCopy((ItemStack)itemStack)));
        bone.getDisplay().ifDirty(display -> EntityUtils.writeData(buf, 24, DataWatcherRegistry.a, display == null ? (byte)0 : (byte)display.ordinal()));
        buf.k(255);
        this.cleanupQueue.add(bone::clearDirty);
        return new PacketPlayOutEntityMetadata(buf);
    }

    private Packets.PacketSupplier hitboxSpawnPivot(DisplayRenderer.Hitbox hitbox) {
        return hitbox.isPivotVisible() ? NetworkUtils.createPivotSpawn(hitbox.getPivotId(), hitbox.getPivotUuid(), hitbox.getPosition().get()) : null;
    }

    private PacketPlayOutEntityMetadata hitboxDataPivot(DisplayRenderer.Hitbox hitbox) {
        if (!hitbox.isPivotVisible()) {
            return null;
        }
        PacketDataSerializer buf = NetworkUtils.createByteBuf();
        buf.c(hitbox.getPivotId());
        EntityUtils.writeData(buf, 0, DataWatcherRegistry.a, (byte)32);
        EntityUtils.writeData(buf, 1, DataWatcherRegistry.b, Integer.MAX_VALUE);
        EntityUtils.writeData(buf, 8, DataWatcherRegistry.d, Float.valueOf(0.0f));
        buf.k(255);
        return new PacketPlayOutEntityMetadata(buf);
    }

    private PacketPlayOutSpawnEntity hitboxSpawn(DisplayRenderer.Hitbox hitbox) {
        if (!hitbox.isHitboxVisible()) {
            return null;
        }
        Vector3f pos = hitbox.getPosition().get();
        return new PacketPlayOutSpawnEntity(hitbox.getHitboxId(), hitbox.getHitboxUuid(), (double)pos.x, (double)pos.y, (double)pos.z, 0.0f, 0.0f, EntityTypes.ac, 0, Vec3D.b, 0.0);
    }

    private PacketPlayOutEntityMetadata hitboxData(DisplayRenderer.Hitbox hitbox, boolean spawn) {
        if (!hitbox.isHitboxVisible() || !hitbox.isHitboxDirty() && !spawn) {
            return null;
        }
        PacketDataSerializer buf = NetworkUtils.createByteBuf();
        buf.c(hitbox.getHitboxId());
        if (spawn) {
            EntityUtils.writeData(buf, 1, DataWatcherRegistry.b, Integer.MAX_VALUE);
            EntityUtils.writeData(buf, 10, DataWatcherRegistry.k, false);
        }
        hitbox.getWidth().ifDirty(val -> EntityUtils.writeData(buf, 8, DataWatcherRegistry.d, val), spawn);
        hitbox.getHeight().ifDirty(val -> EntityUtils.writeData(buf, 9, DataWatcherRegistry.d, val), spawn);
        buf.k(255);
        return new PacketPlayOutEntityMetadata(buf);
    }

    private PacketPlayOutSpawnEntity shadowSpawn(DisplayRenderer.Hitbox hitbox) {
        if (!hitbox.isShadowVisible()) {
            return null;
        }
        Vector3f pos = hitbox.getPosition().get();
        return new PacketPlayOutSpawnEntity(hitbox.getShadowId(), hitbox.getShadowUuid(), (double)pos.x, (double)pos.y, (double)pos.z, 0.0f, 0.0f, EntityTypes.af, 0, Vec3D.b, 0.0);
    }

    private PacketPlayOutEntityMetadata shadowData(DisplayRenderer.Hitbox hitbox, boolean spawn) {
        if (!hitbox.isShadowVisible() || !hitbox.getShadowRadius().isDirty() && !spawn) {
            return null;
        }
        PacketDataSerializer buf = NetworkUtils.createByteBuf();
        buf.c(hitbox.getShadowId());
        if (spawn) {
            EntityUtils.writeData(buf, 1, DataWatcherRegistry.b, Integer.MAX_VALUE);
        }
        hitbox.getShadowRadius().ifDirty(val -> EntityUtils.writeData(buf, 18, DataWatcherRegistry.d, val), spawn);
        buf.k(255);
        return new PacketPlayOutEntityMetadata(buf);
    }

    private PacketPlayOutSpawnEntity fireSpawn(DisplayRenderer.Hitbox hitbox, DisplayFire fire) {
        Vector3f pos = hitbox.getPosition().get();
        return new PacketPlayOutSpawnEntity(fire.getId(), fire.getUuid(), (double)pos.x, (double)pos.y, (double)pos.z, 0.0f, 0.0f, EntityTypes.af, 0, Vec3D.b, 0.0);
    }

    private PacketPlayOutEntityMetadata fireData(DisplayFire fire, boolean spawn) {
        if (!fire.isDirty() && !spawn) {
            return null;
        }
        ArrayList<DataWatcher.b> entityData = new ArrayList<DataWatcher.b>(2);
        if (spawn) {
            entityData.add(new DataWatcher.b(1, DataWatcherRegistry.b, (Object)Integer.MAX_VALUE));
            entityData.add(new DataWatcher.b(15, DataWatcherRegistry.a, (Object)((byte)Display.BillboardConstraints.b.ordinal())));
            entityData.add(new DataWatcher.b(16, DataWatcherRegistry.b, (Object)Brightness.c.a()));
        }
        fire.getFireModel().ifDirty(stack -> entityData.add(new DataWatcher.b(23, DataWatcherRegistry.h, (Object)CraftItemStack.asNMSCopy((ItemStack)stack))), spawn);
        fire.getPosition().ifDirty(vector3f -> entityData.add(new DataWatcher.b(11, DataWatcherRegistry.A, vector3f)), spawn);
        fire.getScale().ifDirty(vector3f -> entityData.add(new DataWatcher.b(12, DataWatcherRegistry.A, vector3f)), spawn);
        fire.getVisible().ifDirty(flag -> entityData.add(new DataWatcher.b(17, DataWatcherRegistry.d, (Object)Float.valueOf(flag != false ? 4096.0f : 0.0f))), spawn);
        return new PacketPlayOutEntityMetadata(fire.getId(), entityData);
    }

    private Packets.PacketSupplier hitboxTeleport(DisplayRenderer.Hitbox hitbox) {
        if (!hitbox.isPivotVisible() || !hitbox.getPosition().isDirty()) {
            return null;
        }
        this.cleanupQueue.add(() -> hitbox.getPosition().clearDirty());
        return NetworkUtils.createPivotTeleport(hitbox.getPivotId(), hitbox.getPosition().get());
    }

    private PacketPlayOutMount hitboxMount(DisplayRenderer.Hitbox hitbox) {
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
        PacketDataSerializer buf = NetworkUtils.createByteBuf();
        buf.c(hitbox.getPivotId());
        buf.c(passengers.size());
        passengers.forEach(arg_0 -> ((PacketDataSerializer)buf).c(arg_0));
        return new PacketPlayOutMount(buf);
    }
}

