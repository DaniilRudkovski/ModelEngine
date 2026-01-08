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
import java.util.ArrayList;
import java.util.List;
import lombok.Generated;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class TabWidget
implements Widget {
    private final List<Tab> tabs = new ArrayList<Tab>();
    private Tab selectedTab;

    @Override
    public ItemStack getItemForSlot(int size, int slot) {
        if (slot == 0 || slot >= 8) {
            return this.selectedTab == null ? null : this.selectedTab.getWidget().getItemForSlot(size, slot);
        }
        if (this.tabs.size() < slot) {
            return null;
        }
        return this.tabs.get(slot - 1).getItemStack();
    }

    @Override
    public void onClick(AbstractScreen screen, Player player, int slot, InventoryClickEvent event) {
        Tab t;
        if (slot == 0 || slot >= 8) {
            if (this.selectedTab != null) {
                this.selectedTab.getWidget().onClick(screen, player, slot, event);
            }
            return;
        }
        if (this.tabs.size() >= slot && this.selectedTab != (t = this.tabs.get(slot - 1))) {
            this.selectedTab = t;
            this.selectedTab.onSelect();
            screen.draw(true);
        }
    }

    public void addTab(Tab tab) {
        this.tabs.add(tab);
        if (this.selectedTab == null) {
            this.selectedTab = tab;
            this.selectedTab.onSelect();
        }
    }

    public void clearTab() {
        this.tabs.clear();
    }

    @Generated
    public Tab getSelectedTab() {
        return this.selectedTab;
    }

    public static interface Tab {
        public ItemStack getItemStack();

        public Widget getWidget();

        public void onSelect();
    }
}

