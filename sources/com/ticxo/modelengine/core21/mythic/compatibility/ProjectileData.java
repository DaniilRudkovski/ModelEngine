/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Maps
 *  io.lumine.mythic.api.adapters.AbstractLocation
 *  io.lumine.mythic.api.skills.IParentSkill
 *  io.lumine.mythic.bukkit.BukkitAdapter
 *  org.bukkit.Bukkit
 *  org.bukkit.Location
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Player
 */
package com.ticxo.modelengine.core21.mythic.compatibility;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.ticxo.modelengine.api.entity.BaseEntity;
import com.ticxo.modelengine.api.entity.CullType;
import com.ticxo.modelengine.api.entity.cull.BasicCulling;
import com.ticxo.modelengine.api.entity.cull.ModelCuller;
import com.ticxo.modelengine.api.entity.data.AbstractEntityData;
import com.ticxo.modelengine.core21.mythic.compatibility.ProjectileEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.skills.IParentSkill;
import io.lumine.mythic.bukkit.BukkitAdapter;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class ProjectileData
extends AbstractEntityData {
    private final ProjectileEntity<?> projectileEntity;
    protected final ModelCuller culler;
    private final AtomicBoolean isAlive = new AtomicBoolean(true);
    private final AtomicReference<Location> location = new AtomicReference();
    private final Set<UUID> syncTracking = new HashSet<UUID>();
    private final Map<UUID, CullType> asyncTracking = Maps.newConcurrentMap();
    private final Queue<UUID> startTrackingQueue = new ConcurrentLinkedQueue<UUID>();
    private final Set<UUID> startTracking = new HashSet<UUID>();
    private final Queue<UUID> stopTrackingQueue = new ConcurrentLinkedQueue<UUID>();
    private final Set<UUID> stopTracking = new HashSet<UUID>();

    public ProjectileData(ProjectileEntity<?> projectileEntity) {
        this(projectileEntity, new BasicCulling());
    }

    public ProjectileData(ProjectileEntity<?> projectileEntity, ModelCuller culler) {
        this.projectileEntity = projectileEntity;
        this.culler = culler;
        culler.setData(this);
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
    }

    @Override
    public void syncUpdate() {
        Location location = BukkitAdapter.adapt((AbstractLocation)this.projectileEntity.getOriginal().getCurrentLocation());
        this.location.set(location);
        this.isAlive.set(!((IParentSkill)this.projectileEntity.getOriginal()).getCancelled());
        if (!this.isDataValid()) {
            this.projectileEntity.removeSelf();
        }
        HashSet<UUID> updatedTracking = new HashSet<UUID>();
        int radiusSqr = this.projectileEntity.getRenderRadius() * this.projectileEntity.getRenderRadius();
        for (Player player : Bukkit.getOnlinePlayers()) {
            Location playerLocation = player.getLocation();
            if (!this.getLocation().getWorld().equals((Object)playerLocation.getWorld()) || !(playerLocation.distanceSquared(location) <= (double)radiusSqr)) continue;
            updatedTracking.add(player.getUniqueId());
        }
        HashSet<UUID> all = new HashSet<UUID>(this.syncTracking);
        all.addAll(updatedTracking);
        for (UUID player : all) {
            if (!this.syncTracking.contains(player)) {
                this.startTrackingQueue.add(player);
                continue;
            }
            if (updatedTracking.contains(player)) continue;
            this.stopTrackingQueue.add(player);
        }
        this.syncTracking.clear();
        this.syncTracking.addAll(updatedTracking);
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
        return this.projectileEntity;
    }

    @Override
    public boolean isDataValid() {
        return this.isAlive.get();
    }

    @Override
    public Location getLocation() {
        return this.location.get();
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

    @Override
    public boolean isBaseGlowing() {
        return false;
    }
}

