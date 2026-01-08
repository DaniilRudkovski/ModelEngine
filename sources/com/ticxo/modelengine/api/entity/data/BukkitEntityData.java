/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Maps
 *  lombok.Generated
 *  org.bukkit.Bukkit
 *  org.bukkit.Location
 *  org.bukkit.entity.Entity
 */
package com.ticxo.modelengine.api.entity.data;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.entity.BukkitEntity;
import com.ticxo.modelengine.api.entity.CullType;
import com.ticxo.modelengine.api.entity.cull.BasicCulling;
import com.ticxo.modelengine.api.entity.cull.ModelCuller;
import com.ticxo.modelengine.api.entity.data.AbstractEntityData;
import com.ticxo.modelengine.api.nms.entity.EntityHandler;
import com.ticxo.modelengine.api.nms.entity.wrapper.TrackedEntity;
import com.ticxo.modelengine.api.nms.impl.TempTrackedEntity;
import com.ticxo.modelengine.api.utils.data.QueuedAtomic;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import lombok.Generated;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

public class BukkitEntityData
extends AbstractEntityData {
    protected final EntityHandler entityHandler = ModelEngineAPI.getEntityHandler();
    protected final Entity entity;
    protected final BukkitEntity baseEntity;
    protected final ModelCuller culler;
    protected final QueuedAtomic<Boolean> isEntityValid = QueuedAtomic.builder().value(() -> false).build();
    protected final QueuedAtomic<Boolean> isDataValid = QueuedAtomic.builder().value(() -> false).build();
    protected final QueuedAtomic<Boolean> isForcedAlive = QueuedAtomic.builder().value(() -> true).build();
    protected final QueuedAtomic<Location> location = QueuedAtomic.builder().passer((a, b2) -> b2.set(((Location)a.get()).clone())).build();
    protected final QueuedAtomic<List<Entity>> passengers = QueuedAtomic.builder().value(ArrayList::new).setter((v, r) -> {
        ((List)r.get()).clear();
        ((List)r.get()).addAll(v);
    }).passer((a, b2) -> {
        ((List)b2.get()).clear();
        ((List)b2.get()).addAll((Collection)a.get());
    }).build();
    protected final QueuedAtomic<Integer> glowColor = QueuedAtomic.builder().value(() -> -1).build();
    protected final Set<UUID> syncTracking = new HashSet<UUID>();
    protected final Map<UUID, CullType> asyncTracking = Maps.newConcurrentMap();
    protected final Queue<Queue<UUID>> startTrackingQueue = new ConcurrentLinkedQueue<Queue<UUID>>();
    protected final Set<UUID> startTracking = new HashSet<UUID>();
    protected final Queue<Queue<UUID>> stopTrackingQueue = new ConcurrentLinkedQueue<Queue<UUID>>();
    protected final Set<UUID> stopTracking = new HashSet<UUID>();
    protected TrackedEntity tracked;
    protected int syncTick;
    protected boolean wasValid;

    public BukkitEntityData(Entity entity, BukkitEntity base) {
        this(entity, base, new BasicCulling());
    }

    public BukkitEntityData(Entity entity, BukkitEntity base, ModelCuller culler) {
        this.entity = entity;
        this.baseEntity = base;
        this.culler = culler;
        culler.setData(this);
        this.tracked = this.entityHandler.wrapTrackedEntity(entity);
        this.syncUpdate();
        this.asyncUpdate();
    }

    @Override
    public void asyncUpdate() {
        this.isEntityValid.pass();
        this.isDataValid.pass();
        this.isForcedAlive.pass();
        this.location.pass();
        this.passengers.pass();
        while (!this.startTrackingQueue.isEmpty() || !this.stopTrackingQueue.isEmpty()) {
            UUID player;
            Queue<UUID> playerQueue;
            if (!this.startTrackingQueue.isEmpty() && (playerQueue = this.startTrackingQueue.poll()) != null) {
                while (!playerQueue.isEmpty()) {
                    player = playerQueue.poll();
                    this.startTracking.add(player);
                    this.stopTracking.remove(player);
                    this.culler.put(player, CullType.NO_CULL);
                }
            }
            if (this.stopTrackingQueue.isEmpty() || (playerQueue = this.stopTrackingQueue.poll()) == null) continue;
            while (!playerQueue.isEmpty()) {
                player = playerQueue.poll();
                this.stopTracking.add(player);
                this.startTracking.remove(player);
                if (this.asyncTracking.get(player) == CullType.CULLED) continue;
                this.culler.remove(player);
            }
        }
    }

    @Override
    public void syncUpdate() {
        ++this.syncTick;
        boolean valid = this.entity.isValid();
        this.wasValid |= valid || this.syncTick > 20;
        this.isEntityValid.set(valid |= !this.wasValid);
        this.isDataValid.set(valid || !this.entityHandler.isRemoved(this.entity));
        if (this.isForcedAlive()) {
            this.entityHandler.setDeathTick(this.entity, 0);
        } else if (!this.isEntityValid()) {
            this.entityHandler.setDeathTick(this.entity, 20);
        }
        this.location.set(this.entity.getLocation());
        this.passengers.set(this.entity.getPassengers());
        if (Bukkit.isPrimaryThread()) {
            this.safeSyncUpdate();
        }
        Set<UUID> updatedTracking = this.getTracked().getTrackedPlayer(player -> this.asyncTracking.get(player.getUniqueId()) != CullType.CULLED);
        HashSet<UUID> all = new HashSet<UUID>(this.syncTracking);
        ConcurrentLinkedQueue<UUID> startTrack = new ConcurrentLinkedQueue<UUID>();
        ConcurrentLinkedQueue<UUID> stopTrack = new ConcurrentLinkedQueue<UUID>();
        all.addAll(updatedTracking);
        for (UUID player2 : all) {
            if (!this.syncTracking.contains(player2)) {
                startTrack.add(player2);
                continue;
            }
            if (updatedTracking.contains(player2)) continue;
            stopTrack.add(player2);
        }
        this.syncTracking.clear();
        this.syncTracking.addAll(updatedTracking);
        this.startTrackingQueue.add(startTrack);
        this.stopTrackingQueue.add(stopTrack);
    }

    private void safeSyncUpdate() {
        this.glowColor.set(this.entityHandler.getGlowColor(this.entity));
    }

    @Override
    public void cullUpdate() {
        this.culler.updateCulledPlayer();
    }

    @Override
    public void cleanup() {
        this.startTracking.clear();
        this.stopTracking.clear();
    }

    @Override
    public void destroy() {
        this.startTrackingQueue.forEach(Collection::clear);
        this.startTrackingQueue.clear();
        this.stopTrackingQueue.forEach(Collection::clear);
        this.stopTrackingQueue.clear();
    }

    @Override
    public boolean isDataValid() {
        return this.isDataValid.get();
    }

    public boolean isEntityValid() {
        return this.isEntityValid.get();
    }

    public boolean isForcedAlive() {
        return this.isForcedAlive.get();
    }

    public void setForcedAlive(boolean flag) {
        this.isForcedAlive.set(flag);
    }

    public int getGlowColor() {
        return this.glowColor.get();
    }

    @Override
    public Location getLocation() {
        return this.location.get().clone();
    }

    @Override
    public List<Entity> getPassengers() {
        return this.passengers.get();
    }

    @Override
    public Set<UUID> getStartTracking() {
        return ImmutableSet.copyOf(this.startTracking);
    }

    @Override
    public Map<UUID, CullType> getMutableTracking() {
        return this.asyncTracking;
    }

    @Override
    public Map<UUID, CullType> getTracking() {
        return ImmutableMap.copyOf(this.asyncTracking);
    }

    @Override
    public Set<UUID> getStopTracking() {
        return ImmutableSet.copyOf(this.stopTracking);
    }

    @Override
    public boolean hasTracking() {
        return !this.asyncTracking.isEmpty() && this.culler.getCulledCount() != this.asyncTracking.size();
    }

    @Override
    public boolean isBaseGlowing() {
        return this.entity.isGlowing();
    }

    public TrackedEntity getTracked() {
        TrackedEntity trackedEntity = this.tracked;
        if (trackedEntity instanceof TempTrackedEntity) {
            TempTrackedEntity tempTracked = (TempTrackedEntity)trackedEntity;
            TrackedEntity newTracked = this.entityHandler.wrapTrackedEntity(this.entity);
            if (!(newTracked instanceof TempTrackedEntity)) {
                if (tempTracked.getBaseRange() != -1) {
                    newTracked.setBaseRange(tempTracked.getBaseRange());
                }
                newTracked.setPlayerPredicate(tempTracked.getPlayerPredicate());
                for (UUID player : tempTracked.getForcePaired()) {
                    newTracked.addForcedPairing(player);
                }
                for (UUID player : tempTracked.getForceHidden()) {
                    newTracked.addForcedHidden(player);
                }
                this.tracked = newTracked;
            }
        }
        return this.tracked;
    }

    @Generated
    public BukkitEntity getBaseEntity() {
        return this.baseEntity;
    }
}

