/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  net.kyori.adventure.text.Component
 *  net.kyori.adventure.text.TextComponent
 *  net.kyori.adventure.text.format.NamedTextColor
 *  net.kyori.adventure.text.format.Style
 *  net.kyori.adventure.text.format.TextColor
 *  org.bukkit.Material
 *  org.bukkit.entity.Player
 *  org.bukkit.event.inventory.InventoryClickEvent
 *  org.bukkit.inventory.ItemStack
 */
package com.ticxo.modelengine.core.menu.widget;

import com.google.common.collect.ImmutableMap;
import com.ticxo.modelengine.api.menu.AbstractScreen;
import com.ticxo.modelengine.api.menu.Widget;
import com.ticxo.modelengine.api.utils.data.ComponentUtil;
import com.ticxo.modelengine.api.utils.data.ItemUtils;
import com.ticxo.modelengine.api.utils.math.TMath;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class PaginatorWidget
implements Widget {
    private static final int PAGE_SLOT = 52;
    private static final int MAX_ITEM = 21;
    private static final Map<Integer, Integer> PAGE_PAIR;
    private final ItemStack pageSwitch;
    private final List<PageButton> buttons = new ArrayList<PageButton>();
    private int currentPage;

    public PaginatorWidget() {
        this.pageSwitch = new ItemStack(Material.PAPER);
        ItemUtils.name(this.pageSwitch, (Component)Component.text((String)"Turn Page", (Style)ComponentUtil.reset()));
    }

    @Override
    public ItemStack getItemForSlot(int size, int slot) {
        if (slot == 52) {
            ItemUtils.lore(this.pageSwitch, ((TextComponent)((TextComponent)((TextComponent)Component.empty().style(ComponentUtil.reset())).append(Component.text((String)"<<< Left Click").color((TextColor)(this.currentPage == 0 ? NamedTextColor.DARK_GRAY : NamedTextColor.WHITE)))).append((Component)Component.text((String)" | "))).append(Component.text((String)"Right Click >>>").color((TextColor)(this.currentPage == this.getLastPage() ? NamedTextColor.DARK_GRAY : NamedTextColor.WHITE))));
            return this.pageSwitch;
        }
        PageButton button = this.getButton(slot);
        return button == null ? null : button.getItemStack();
    }

    @Override
    public void onClick(AbstractScreen screen, Player player, int slot, InventoryClickEvent event) {
        if (slot == 52) {
            if (event.getClick().isLeftClick()) {
                --this.currentPage;
            } else if (event.getClick().isRightClick()) {
                ++this.currentPage;
            }
            this.currentPage = TMath.clamp(this.currentPage, 0, this.getLastPage());
            screen.markSlotsDirty(52);
            this.refreshPage(screen);
        } else {
            PageButton button = this.getButton(slot);
            if (button != null) {
                button.onClick(screen, player, slot, event);
            }
        }
    }

    public void addButton(PageButton button) {
        this.buttons.add(button);
    }

    public void clearButtons() {
        this.buttons.clear();
    }

    public PageButton getButton(int slot) {
        Integer id = PAGE_PAIR.get(slot);
        if (id == null) {
            return null;
        }
        return (id = Integer.valueOf(this.currentPage * 21 + id)) < this.buttons.size() ? this.buttons.get(id) : null;
    }

    public void refreshPage(AbstractScreen screen) {
        PAGE_PAIR.forEach((invSlot, id) -> screen.markSlotDirty((int)invSlot));
    }

    private int getLastPage() {
        return Math.max(TMath.ceil((float)this.buttons.size() / 21.0f) - 1, 0);
    }

    static {
        HashMap<Integer, Integer> pair = new HashMap<Integer, Integer>();
        int id = 0;
        block3: for (int i = 19; i <= 43; ++i) {
            switch (i % 9) {
                case 0: 
                case 8: {
                    continue block3;
                }
                default: {
                    pair.put(i, id++);
                }
            }
        }
        PAGE_PAIR = ImmutableMap.copyOf(pair);
    }

    public static interface PageButton {
        public ItemStack getItemStack();

        public void onClick(AbstractScreen var1, Player var2, int var3, InventoryClickEvent var4);
    }
}

