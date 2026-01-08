/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 *  net.kyori.adventure.text.Component
 *  org.bukkit.Bukkit
 *  org.bukkit.entity.Player
 *  org.bukkit.event.inventory.InventoryClickEvent
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.ItemStack
 */
package com.ticxo.modelengine.api.menu;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.menu.Widget;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.Generated;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public abstract class AbstractScreen {
    protected final Player viewer;
    protected final Inventory inventory;
    protected final List<Widget> widgets = new ArrayList<Widget>();
    protected final Map<Integer, Widget> widgetReference = new HashMap<Integer, Widget>();
    protected final Set<Integer> dirtySlots = new HashSet<Integer>();

    public AbstractScreen(Player viewer, String title, int rows) {
        this(viewer, (Component)Component.text((String)title), rows);
    }

    public AbstractScreen(Player viewer, Component component, int rows) {
        this.viewer = viewer;
        this.inventory = Bukkit.createInventory(null, (int)(rows * 9), (Component)component);
    }

    public void openScreen() {
        this.draw(true);
        this.viewer.openInventory(this.inventory);
        ModelEngineAPI.getAPI().getScreenManager().registerScreen(this);
    }

    public void onTick() {
        if (!this.dirtySlots.isEmpty()) {
            this.draw(false);
        }
    }

    public void onClick(Player player, int slot, InventoryClickEvent event) {
        Widget widget = this.widgetReference.get(slot);
        if (widget != null) {
            widget.onClick(this, player, slot, event);
        }
    }

    public void onClickPlayer(Player player, int slot, InventoryClickEvent event) {
        if (event.isShiftClick()) {
            event.setCancelled(true);
        }
    }

    public void addWidget(Widget widget) {
        this.widgets.add(widget);
    }

    public void markSlotDirty(int slot) {
        this.dirtySlots.add(slot);
    }

    public void markSlotsDirty(int ... slots) {
        for (int slot : slots) {
            this.dirtySlots.add(slot);
        }
    }

    public void draw(boolean all) {
        int size = this.inventory.getSize();
        if (all) {
            this.inventory.clear();
            this.widgetReference.clear();
            block0: for (int i = 0; i < size; ++i) {
                for (int j = this.widgets.size() - 1; j >= 0; --j) {
                    Widget widget = this.widgets.get(j);
                    ItemStack item = widget.getItemForSlot(size, i);
                    if (item == null) continue;
                    this.inventory.setItem(i, item);
                    this.widgetReference.put(i, widget);
                    continue block0;
                }
            }
        } else {
            if (this.dirtySlots.isEmpty()) {
                return;
            }
            block2: for (int slot : this.dirtySlots) {
                for (int j = this.widgets.size() - 1; j >= 0; --j) {
                    Widget widget = this.widgets.get(j);
                    ItemStack item = widget.getItemForSlot(size, slot);
                    if (item == null) continue;
                    this.inventory.setItem(slot, item);
                    this.widgetReference.put(slot, widget);
                    continue block2;
                }
                this.inventory.setItem(slot, null);
                this.widgetReference.remove(slot);
            }
            this.dirtySlots.clear();
        }
    }

    @Generated
    public Player getViewer() {
        return this.viewer;
    }

    @Generated
    public Inventory getInventory() {
        return this.inventory;
    }
}

