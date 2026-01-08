/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.HumanEntity
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.inventory.InventoryClickEvent
 *  org.bukkit.event.inventory.InventoryCloseEvent
 *  org.bukkit.event.inventory.InventoryDragEvent
 *  org.bukkit.inventory.Inventory
 */
package com.ticxo.modelengine.core.listener;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.menu.AbstractScreen;
import com.ticxo.modelengine.api.menu.ScreenManager;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;

public class InventoryListener
implements Listener {
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        HumanEntity humanEntity;
        if (event.isCancelled()) {
            return;
        }
        Inventory inventory = event.getInventory();
        AbstractScreen screen = this.getScreenManager().getScreen(inventory);
        if (screen == null || !((humanEntity = event.getWhoClicked()) instanceof Player)) {
            return;
        }
        Player player = (Player)humanEntity;
        if (event.getClickedInventory() == inventory) {
            event.setCancelled(true);
            screen.onClick(player, event.getSlot(), event);
        } else {
            screen.onClickPlayer(player, event.getSlot(), event);
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (this.getScreenManager().isScreen(event.getInventory())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        this.getScreenManager().unregisterScreen(event.getInventory());
    }

    private ScreenManager getScreenManager() {
        return ModelEngineAPI.getAPI().getScreenManager();
    }
}

