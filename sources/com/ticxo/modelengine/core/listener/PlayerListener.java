/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.entity.PlayerDeathEvent
 *  org.bukkit.event.player.PlayerJoinEvent
 *  org.bukkit.event.player.PlayerMoveEvent
 *  org.bukkit.event.player.PlayerPortalEvent
 *  org.bukkit.event.player.PlayerQuitEvent
 *  org.bukkit.event.player.PlayerTeleportEvent
 *  org.bukkit.util.Vector
 */
package com.ticxo.modelengine.core.listener;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.ServerInfo;
import com.ticxo.modelengine.api.entity.BukkitPlayer;
import com.ticxo.modelengine.api.entity.data.IEntityData;
import com.ticxo.modelengine.api.lod.AnimationLODHandler;
import com.ticxo.modelengine.api.model.ModeledEntity;
import com.ticxo.modelengine.core.menu.SelectionManager;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.util.Vector;

public class PlayerListener
implements Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        ServerInfo.setOnline(player.getUniqueId(), true);
        ModelEngineAPI.getNetworkHandler().injectChannel(player);
        AnimationLODHandler.registerPlayer(player.getUniqueId());
        if (ModelEngineAPI.getConfigCache().isEagerGenerate()) {
            ModelEngineAPI.getUserLimbRegistry().generate(player);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        ServerInfo.setOnline(player.getUniqueId(), false);
        ModelEngineAPI.getNetworkHandler().ejectChannel(player);
        ModelEngineAPI.getMountPairManager().tryDismount((Entity)player);
        ModelEngineAPI.getInteractionTracker().removeDynamicHitbox(player.getUniqueId());
        ModelEngineAPI.getEntityHandler().setForcedInvisible((Entity)player, false);
        SelectionManager.removePlayer(event.getPlayer());
        AnimationLODHandler.setPlayerActive(event.getPlayer().getUniqueId(), false);
    }

    @EventHandler
    public void onPortal(PlayerPortalEvent event) {
        Player entity = event.getPlayer();
        if (ModelEngineAPI.getMountPairManager().get(entity.getUniqueId()) != null) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        if (event instanceof PlayerPortalEvent) {
            return;
        }
        ModelEngineAPI.getMountPairManager().tryDismount((Entity)event.getPlayer());
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        ModelEngineAPI.getMountPairManager().tryDismount((Entity)event.getPlayer());
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Vector dir = event.getTo().toVector().subtract(event.getFrom().toVector());
        if (dir.isZero()) {
            return;
        }
        Player player = event.getPlayer();
        ModeledEntity modeledEntity = ModelEngineAPI.getModeledEntity(player.getUniqueId());
        if (modeledEntity == null) {
            return;
        }
        IEntityData iEntityData = modeledEntity.getBase().getData();
        if (!(iEntityData instanceof BukkitPlayer.BukkitPlayerData)) {
            return;
        }
        BukkitPlayer.BukkitPlayerData playerData = (BukkitPlayer.BukkitPlayerData)iEntityData;
        if (dir.getX() != 0.0 || dir.getZ() != 0.0) {
            playerData.setWalkTick(3);
        }
        if (dir.getY() > 0.0) {
            playerData.setJumpTick(3);
        }
    }
}

