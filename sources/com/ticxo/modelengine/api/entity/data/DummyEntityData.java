/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Maps
 *  lombok.Generated
 *  org.bukkit.Location
 *  org.bukkit.entity.Entity
 */
package com.ticxo.modelengine.api.entity.data;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.ticxo.modelengine.api.entity.BaseEntity;
import com.ticxo.modelengine.api.entity.CullType;
import com.ticxo.modelengine.api.entity.Dummy;
import com.ticxo.modelengine.api.entity.cull.BasicCulling;
import com.ticxo.modelengine.api.entity.cull.ModelCuller;
import com.ticxo.modelengine.api.entity.data.AbstractEntityData;
import com.ticxo.modelengine.api.nms.impl.DummyTrackedEntity;
import com.ticxo.modelengine.api.utils.callback.Callback;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import lombok.Generated;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

public class DummyEntityData<T>
extends AbstractEntityData {
    protected final Dummy<T> dummy;
    protected final DummyTrackedEntity tracked;
    protected final ModelCuller culler;
    protected final Set<UUID> syncTracking = new HashSet<UUID>();
    protected final Map<UUID, CullType> asyncTracking = Maps.newConcurrentMap();
    protected final Queue<UUID> startTrackingQueue = new ConcurrentLinkedQueue<UUID>();
    protected final Set<UUID> startTracking = new HashSet<UUID>();
    protected final Queue<UUID> stopTrackingQueue = new ConcurrentLinkedQueue<UUID>();
    protected final Set<UUID> stopTracking = new HashSet<UUID>();
    protected final Callback<Runnable> syncUpdateCallback = new Callback<Runnable>(runnables -> () -> runnables.forEach(Runnable::run));
    protected final Callback<Runnable> asyncUpdateCallback = new Callback<Runnable>(runnables -> () -> runnables.forEach(Runnable::run));
    protected Location location;
    protected boolean baseGlowing;

    public DummyEntityData(Dummy<T> dummy) {
        this(dummy, new BasicCulling());
    }

    public DummyEntityData(Dummy<T> dummy, ModelCuller culler) {
        this.dummy = dummy;
        this.culler = culler;
        culler.setData(this);
        this.tracked = new DummyTrackedEntity();
        this.syncUpdate();
        this.asyncUpdate();
    }

    @Override
    public void asyncUpdate() {
        UUID player;
        while (!this.startTrackingQueue.isEmpty()) {
            player = this.startTrackingQueue.poll();
            this.startTracking.add(player);
            this.asyncTracking.put(player, CullType.NO_CULL);
        }
        while (!this.stopTrackingQueue.isEmpty()) {
            player = this.stopTrackingQueue.poll();
            this.stopTracking.add(player);
            if (this.asyncTracking.get(player) == CullType.CULLED) continue;
            this.asyncTracking.remove(player);
        }
        this.asyncUpdateCallback.invoker().run();
    }

    @Override
    public void syncUpdate() {
        if (this.dummy.isDetectingPlayers() && this.location != null) {
            this.tracked.detectPlayers(this.location);
        }
        Set<UUID> updatedTracking = this.tracked.getTrackedPlayer(player -> this.asyncTracking.get(player.getUniqueId()) != CullType.CULLED);
        HashSet<UUID> all = new HashSet<UUID>(this.syncTracking);
        all.addAll(updatedTracking);
        for (UUID player2 : all) {
            if (!this.syncTracking.contains(player2)) {
                this.startTrackingQueue.add(player2);
                continue;
            }
            if (updatedTracking.contains(player2)) continue;
            this.stopTrackingQueue.add(player2);
        }
        this.syncTracking.clear();
        this.syncTracking.addAll(updatedTracking);
        this.syncUpdateCallback.invoker().run();
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
    }

    @Override
    public BaseEntity<?> getBaseEntity() {
        return this.dummy;
    }

    @Override
    public boolean isDataValid() {
        return this.dummy.isAlive();
    }

    @Override
    public List<Entity> getPassengers() {
        return List.of();
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
        return !this.asyncTracking.isEmpty();
    }

    public int getRenderRadius() {
        return this.tracked.getBaseRange();
    }

    public void setRenderRadius(int radius) {
        this.tracked.setBaseRange(radius);
    }

    @Generated
    public DummyTrackedEntity getTracked() {
        return this.tracked;
    }

    @Generated
    public Callback<Runnable> getSyncUpdateCallback() {
        return this.syncUpdateCallback;
    }

    @Generated
    public Callback<Runnable> getAsyncUpdateCallback() {
        return this.asyncUpdateCallback;
    }

    @Override
    @Generated
    public Location getLocation() {
        return this.location;
    }

    @Generated
    public void setLocation(Location location) {
        this.location = location;
    }

    @Override
    @Generated
    public boolean isBaseGlowing() {
        return this.baseGlowing;
    }

    @Generated
    public void setBaseGlowing(boolean baseGlowing) {
        this.baseGlowing = baseGlowing;
    }
}

