/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 *  org.bukkit.event.inventory.InventoryClickEvent
 *  org.bukkit.inventory.ItemStack
 */
package com.ticxo.modelengine.api.menu;

import com.ticxo.modelengine.api.menu.AbstractScreen;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public interface Widget {
    public static int toSlot(int x, int y) {
        return y * 9 + x;
    }

    public ItemStack getItemForSlot(int var1, int var2);

    public void onClick(AbstractScreen var1, Player var2, int var3, InventoryClickEvent var4);
}

