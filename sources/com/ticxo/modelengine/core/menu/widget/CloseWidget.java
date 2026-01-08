/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.kyori.adventure.text.Component
 *  net.kyori.adventure.text.format.Style
 *  org.bukkit.Material
 *  org.bukkit.entity.Player
 *  org.bukkit.event.inventory.InventoryClickEvent
 *  org.bukkit.inventory.ItemStack
 *  org.jetbrains.annotations.Nullable
 */
package com.ticxo.modelengine.core.menu.widget;

import com.ticxo.modelengine.api.menu.AbstractScreen;
import com.ticxo.modelengine.api.menu.Widget;
import com.ticxo.modelengine.api.utils.data.ComponentUtil;
import com.ticxo.modelengine.api.utils.data.ItemUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public class CloseWidget
implements Widget {
    private final ItemStack close = new ItemStack(Material.BARRIER);
    private final AbstractScreen rootScreen;

    public CloseWidget(@Nullable AbstractScreen screen) {
        ItemUtils.name(this.close, (Component)Component.text((String)"Close", (Style)ComponentUtil.reset()));
        this.rootScreen = screen;
    }

    @Override
    public ItemStack getItemForSlot(int size, int slot) {
        return slot == size - 5 ? this.close : null;
    }

    @Override
    public void onClick(AbstractScreen screen, Player player, int slot, InventoryClickEvent event) {
        if (slot == screen.getInventory().getSize() - 5) {
            if (this.rootScreen == null) {
                player.closeInventory();
            } else {
                this.rootScreen.openScreen();
            }
        }
    }
}

