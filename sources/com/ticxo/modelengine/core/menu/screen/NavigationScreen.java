/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.kyori.adventure.text.Component
 *  net.kyori.adventure.text.TextComponent
 *  net.kyori.adventure.text.format.NamedTextColor
 *  net.kyori.adventure.text.format.Style
 *  net.kyori.adventure.text.format.TextColor
 *  net.kyori.adventure.text.format.TextDecoration
 *  org.bukkit.Material
 *  org.bukkit.enchantments.Enchantment
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Player
 *  org.bukkit.event.inventory.ClickType
 *  org.bukkit.event.inventory.InventoryClickEvent
 *  org.bukkit.inventory.ItemFlag
 *  org.bukkit.inventory.ItemStack
 */
package com.ticxo.modelengine.core.menu.screen;

import com.ticxo.modelengine.api.menu.AbstractScreen;
import com.ticxo.modelengine.api.menu.Widget;
import com.ticxo.modelengine.api.utils.data.ComponentUtil;
import com.ticxo.modelengine.api.utils.data.ItemUtils;
import com.ticxo.modelengine.core.menu.SelectionManager;
import com.ticxo.modelengine.core.menu.screen.EditEntityScreen;
import com.ticxo.modelengine.core.menu.screen.SpawnModelScreen;
import com.ticxo.modelengine.core.menu.widget.BorderWidget;
import com.ticxo.modelengine.core.menu.widget.CloseWidget;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

public class NavigationScreen
extends AbstractScreen {
    public NavigationScreen(Player player) {
        super(player, "Model Engine", 6);
        this.addWidget(new BorderWidget());
        this.addWidget(new Navigator());
        this.addWidget(new CloseWidget(null));
    }

    class Navigator
    implements Widget {
        private static final int SPAWN_SLOT = 29;
        private static final int SELECT_SLOT = 31;
        private static final int EDIT_SLOT = 33;
        private final ItemStack spawn;
        private final ItemStack select;
        private final ItemStack edit;
        private Entity selected;

        public Navigator() {
            if (SelectionManager.isSelecting(NavigationScreen.this.viewer)) {
                SelectionManager.setSelecting(NavigationScreen.this.viewer, false);
                NavigationScreen.this.viewer.sendActionBar(ComponentUtil.base(((TextComponent)Component.empty().style(ComponentUtil.reset())).append((Component)Component.text((String)"Canceled selection mode."))));
            }
            this.spawn = new ItemStack(Material.PIG_SPAWN_EGG);
            ItemUtils.name(this.spawn, (Component)Component.text((String)"Spawn Model", (Style)ComponentUtil.reset()));
            ItemUtils.lore(this.spawn, new Component[]{Component.empty(), Component.text((String)"Spawn a model at where you are standing.", (Style)ComponentUtil.reset())});
            this.selected = SelectionManager.getSelected(NavigationScreen.this.viewer);
            this.select = new ItemStack(Material.SPYGLASS);
            this.updateSelect();
            this.edit = new ItemStack(Material.WRITABLE_BOOK);
            ItemUtils.name(this.edit, (Component)Component.text((String)"Edit Entity", (Style)ComponentUtil.reset()));
            ItemUtils.lore(this.edit, new Component[]{Component.empty(), Component.text((String)"Add or remove models, states, and edit", (Style)ComponentUtil.reset()), Component.text((String)"bone behaviors of the selected entity.", (Style)ComponentUtil.reset())});
        }

        @Override
        public ItemStack getItemForSlot(int size, int slot) {
            return switch (slot) {
                case 29 -> this.spawn;
                case 31 -> this.select;
                case 33 -> this.edit;
                default -> null;
            };
        }

        @Override
        public void onClick(AbstractScreen screen, Player player, int slot, InventoryClickEvent event) {
            ClickType click = event.getClick();
            if (click.isKeyboardClick()) {
                return;
            }
            switch (slot) {
                case 29: {
                    new SpawnModelScreen(NavigationScreen.this, player).openScreen();
                    break;
                }
                case 31: {
                    switch (click) {
                        case RIGHT: {
                            SelectionManager.setSelected(player, (Entity)player);
                            player.sendActionBar(ComponentUtil.base((Component)Component.text((String)("Select " + player.getName() + "."), (Style)ComponentUtil.reset())));
                            this.selected = player;
                            this.updateSelect();
                            NavigationScreen.this.markSlotDirty(31);
                            break;
                        }
                        case LEFT: {
                            SelectionManager.setSelecting(player, true);
                            player.sendActionBar(ComponentUtil.base((Component)Component.text((String)"Select an entity by attacking the target.", (Style)ComponentUtil.reset())));
                            player.closeInventory();
                            break;
                        }
                        case SHIFT_LEFT: 
                        case SHIFT_RIGHT: {
                            SelectionManager.setSelected(player, null);
                            this.selected = null;
                            this.updateSelect();
                            NavigationScreen.this.markSlotDirty(31);
                        }
                    }
                    break;
                }
                case 33: {
                    Entity entity = SelectionManager.getSelected(player);
                    if (entity == null) break;
                    new EditEntityScreen(NavigationScreen.this, player, entity).openScreen();
                }
            }
        }

        private void updateSelect() {
            ItemUtils.name(this.select, (Component)Component.text((String)"Select Entity", (Style)ComponentUtil.reset()));
            ItemUtils.lore(this.select, new Component[]{Component.empty(), this.selected == null ? null : ((TextComponent)Component.empty().style(ComponentUtil.reset().decoration(TextDecoration.BOLD, true).color((TextColor)NamedTextColor.AQUA))).append((Component)Component.text((String)("Selected: " + this.selected.getName()))), this.selected == null ? null : Component.empty(), Component.text((String)"Left click to select entity.", (Style)ComponentUtil.reset()), Component.text((String)"Right click to select yourself.", (Style)ComponentUtil.reset()), Component.text((String)"Shift click to clear selection.", (Style)ComponentUtil.reset())});
            this.select.addItemFlags(new ItemFlag[]{ItemFlag.HIDE_ENCHANTS});
            if (this.selected != null) {
                this.select.addUnsafeEnchantment(Enchantment.VANISHING_CURSE, 1);
            } else {
                this.select.removeEnchantment(Enchantment.VANISHING_CURSE);
            }
        }
    }
}

