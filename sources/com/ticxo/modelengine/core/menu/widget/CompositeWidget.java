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

public class CompositeWidget
implements Widget {
    private final Widget first;
    private final Widget second;

    @Override
    public ItemStack getItemForSlot(int size, int slot) {
        ItemStack sItem = this.second.getItemForSlot(size, slot);
        return sItem != null ? sItem : this.first.getItemForSlot(size, slot);
    }

    @Override
    public void onClick(AbstractScreen screen, Player player, int slot, InventoryClickEvent event) {
        this.second.onClick(screen, player, slot, event);
        this.first.onClick(screen, player, slot, event);
    }

    @Generated
    public CompositeWidget(Widget first, Widget second) {
        this.first = first;
        this.second = second;
    }
}

