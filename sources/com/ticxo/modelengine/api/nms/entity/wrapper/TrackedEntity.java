/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Player
 *  org.jetbrains.annotations.NotNull
 */
package com.ticxo.modelengine.api.nms.entity.wrapper;

import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface TrackedEntity {
    public static final Predicate<Player> DEFAULT_PREDICATE = player -> true;

    public Entity getEntity();

    public int getBaseRange();

    public void setBaseRange(int var1);

    public int getEffectiveRange();

    public Set<UUID> getTrackedPlayer();

    public Set<UUID> getTrackedPlayer(Predicate<Player> var1);

    public void sendPairingData(Player var1);

    public void broadcastSpawn();

    public void broadcastRemove();

    public void addForcedPairing(UUID var1);

    public void removeForcedPairing(UUID var1);

    public void addForcedHidden(UUID var1);

    public void removeForcedHidden(UUID var1);

    public void setPlayerPredicate(@NotNull Predicate<Player> var1);

    @NotNull
    public Predicate<Player> getPlayerPredicate();
}

