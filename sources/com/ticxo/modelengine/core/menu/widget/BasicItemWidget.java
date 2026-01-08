/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 *  org.bukkit.entity.Player
 *  org.bukkit.event.inventory.InventoryClickEvent
 *  org.bukkit.inventory.ItemStack
 */
package com.ticxo.modelengine.core.menu.widget;

import com.ticxo.modelengine.api.menu.AbstractScreen;
import com.ticxo.modelengine.api.menu.Widget;
import lombok.Generated;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class BasicItemWidget
implements Widget {
    private final int slot;
    private final ItemStack stack;

    @Override
    public ItemStack getItemForSlot(int size, int slot) {
        return this.slot == slot ? this.stack : null;
    }

    @Override
    public void onClick(AbstractScreen screen, Player player, int slot, InventoryClickEvent event) {
    }

    @Generated
    public BasicItemWidget(int slot, ItemStack stack) {
        this.slot = slot;
        this.stack = stack;
    }
}

