/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Player
 */
package com.ticxo.modelengine.api.nms.impl;

import com.ticxo.modelengine.api.nms.entity.wrapper.TrackedEntity;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;
import lombok.Generated;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class TempTrackedEntity
implements TrackedEntity {
    private final Entity entity;
    private final Set<UUID> forcePaired = new HashSet<UUID>();
    private final Set<UUID> forceHidden = new HashSet<UUID>();
    private int baseRange = -1;
    private Predicate<Player> playerPredicate = DEFAULT_PREDICATE;

    @Override
    public int getEffectiveRange() {
        return this.baseRange;
    }

    @Override
    public Set<UUID> getTrackedPlayer() {
        return new HashSet<UUID>();
    }

    @Override
    public Set<UUID> getTrackedPlayer(Predicate<Player> predicate) {
        return new HashSet<UUID>();
    }

    @Override
    public void sendPairingData(Player player) {
    }

    @Override
    public void broadcastSpawn() {
    }

    @Override
    public void broadcastRemove() {
    }

    @Override
    public void addForcedPairing(UUID uuid) {
        this.forcePaired.add(uuid);
    }

    @Override
    public void removeForcedPairing(UUID uuid) {
        this.forcePaired.remove(uuid);
    }

    @Override
    public void addForcedHidden(UUID uuid) {
        this.forceHidden.add(uuid);
    }

    @Override
    public void removeForcedHidden(UUID uuid) {
        this.forceHidden.remove(uuid);
    }

    @Generated
    public TempTrackedEntity(Entity entity) {
        this.entity = entity;
    }

    @Override
    @Generated
    public Entity getEntity() {
        return this.entity;
    }

    @Generated
    public Set<UUID> getForcePaired() {
        return this.forcePaired;
    }

    @Generated
    public Set<UUID> getForceHidden() {
        return this.forceHidden;
    }

    @Override
    @Generated
    public int getBaseRange() {
        return this.baseRange;
    }

    @Override
    @Generated
    public void setBaseRange(int baseRange) {
        this.baseRange = baseRange;
    }

    @Override
    @Generated
    public Predicate<Player> getPlayerPredicate() {
        return this.playerPredicate;
    }

    @Override
    @Generated
    public void setPlayerPredicate(Predicate<Player> playerPredicate) {
        this.playerPredicate = playerPredicate;
    }
}

