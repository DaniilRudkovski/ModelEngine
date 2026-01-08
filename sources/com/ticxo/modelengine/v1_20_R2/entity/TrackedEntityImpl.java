/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Sets
 *  lombok.Generated
 *  net.minecraft.network.protocol.Packet
 *  net.minecraft.network.protocol.game.PacketListenerPlayOut
 *  net.minecraft.server.level.EntityPlayer
 *  net.minecraft.server.level.PlayerChunkMap$EntityTracker
 *  net.minecraft.server.network.ServerPlayerConnection
 *  org.bukkit.Bukkit
 *  org.bukkit.craftbukkit.v1_20_R2.entity.CraftPlayer
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Player
 *  org.jetbrains.annotations.NotNull
 */
package com.ticxo.modelengine.v1_20_R2.entity;

import com.google.common.collect.Sets;
import com.ticxo.modelengine.api.nms.entity.wrapper.TrackedEntity;
import com.ticxo.modelengine.api.utils.ReflectionUtils;
import com.ticxo.modelengine.v1_20_R2.NMSFields;
import com.ticxo.modelengine.v1_20_R2.NMSMethods;
import com.ticxo.modelengine.v1_20_R2.network.utils.NetworkUtils;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.function.Supplier;
import lombok.Generated;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.PacketListenerPlayOut;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.PlayerChunkMap;
import net.minecraft.server.network.ServerPlayerConnection;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_20_R2.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TrackedEntityImpl
implements TrackedEntity {
    private final Entity entity;
    private final Supplier<PlayerChunkMap.EntityTracker> trackedEntitySupplier;
    private final Set<UUID> forcedPairing = Sets.newConcurrentHashSet();
    private final Set<UUID> forcedRemove = Sets.newConcurrentHashSet();
    private Predicate<Player> playerPredicate = DEFAULT_PREDICATE;
    @NotNull
    private PlayerChunkMap.EntityTracker lastTrackedEntity;

    public TrackedEntityImpl(Entity entity, Supplier<PlayerChunkMap.EntityTracker> trackedEntitySupplier, @NotNull PlayerChunkMap.EntityTracker lastTrackedEntity) {
        this.entity = entity;
        this.trackedEntitySupplier = trackedEntitySupplier;
        this.lastTrackedEntity = lastTrackedEntity;
    }

    @Override
    public int getBaseRange() {
        Integer range = (Integer)ReflectionUtils.get(this.getTrackedEntity(), NMSFields.TRACKED_ENTITY_range);
        if (range == null) {
            throw new NullPointerException(String.format("Unable to retrieve base range of entity with UUID %s.", this.entity.getUniqueId()));
        }
        return range;
    }

    @Override
    public void setBaseRange(int range) {
        ReflectionUtils.set(this.getTrackedEntity(), NMSFields.TRACKED_ENTITY_range, range);
    }

    @Override
    public int getEffectiveRange() {
        Integer range = (Integer)ReflectionUtils.call(this.getTrackedEntity(), NMSMethods.TRACKED_ENTITY_getEffectiveRange, new Object[0]);
        if (range == null) {
            throw new NullPointerException(String.format("Unable to retrieve range of entity with UUID %s.", this.entity.getUniqueId()));
        }
        return range;
    }

    @Override
    public Set<UUID> getTrackedPlayer() {
        HashSet<UUID> set = new HashSet<UUID>(this.forcedPairing);
        for (ServerPlayerConnection connection : this.getTrackedEntity().f) {
            Player player = Bukkit.getPlayer((UUID)connection.p().cv());
            if (player == null || !this.playerPredicate.test(player) || this.forcedRemove.contains(player.getUniqueId())) continue;
            set.add(player.getUniqueId());
        }
        return set;
    }

    @Override
    public Set<UUID> getTrackedPlayer(Predicate<Player> predicate) {
        HashSet<UUID> set = new HashSet<UUID>(this.forcedPairing);
        for (ServerPlayerConnection connection : this.getTrackedEntity().f) {
            Player player = Bukkit.getPlayer((UUID)connection.p().cv());
            if (player == null || !predicate.test(player) || !this.playerPredicate.test(player) || this.forcedRemove.contains(player.getUniqueId())) continue;
            set.add(player.getUniqueId());
        }
        return set;
    }

    @Override
    public void sendPairingData(Player player) {
        if (player == null) {
            return;
        }
        EntityPlayer nms = ((CraftPlayer)player).getHandle();
        this.getTrackedEntity().b.a(nms, packet -> NetworkUtils.send(player.getUniqueId(), (Packet<? super PacketListenerPlayOut>)packet));
    }

    @Override
    public void broadcastSpawn() {
        for (UUID player : this.getTrackedPlayer()) {
            this.sendPairingData(Bukkit.getPlayer((UUID)player));
        }
    }

    @Override
    public void broadcastRemove() {
        this.getTrackedEntity().a();
    }

    @Override
    public void addForcedPairing(UUID uuid) {
        this.forcedPairing.add(uuid);
        this.removeForcedHidden(uuid);
    }

    @Override
    public void removeForcedPairing(UUID uuid) {
        this.forcedPairing.remove(uuid);
    }

    @Override
    public void addForcedHidden(UUID uuid) {
        this.forcedRemove.add(uuid);
        this.removeForcedPairing(uuid);
    }

    @Override
    public void removeForcedHidden(UUID uuid) {
        this.forcedRemove.remove(uuid);
    }

    @NotNull
    private PlayerChunkMap.EntityTracker getTrackedEntity() {
        PlayerChunkMap.EntityTracker trackedEntity = this.trackedEntitySupplier.get();
        if (trackedEntity != null && this.lastTrackedEntity != trackedEntity) {
            this.syncInstance(trackedEntity);
            this.lastTrackedEntity = trackedEntity;
        }
        return this.lastTrackedEntity;
    }

    private void syncInstance(PlayerChunkMap.EntityTracker target) {
        Integer range = (Integer)ReflectionUtils.get(this.lastTrackedEntity, NMSFields.TRACKED_ENTITY_range);
        if (range != null) {
            ReflectionUtils.set(target, NMSFields.TRACKED_ENTITY_range, range);
        }
    }

    @Override
    @Generated
    public Entity getEntity() {
        return this.entity;
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

