/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.kyori.adventure.text.Component
 *  net.kyori.adventure.text.format.NamedTextColor
 *  net.kyori.adventure.text.format.Style
 *  net.kyori.adventure.text.format.TextColor
 *  net.kyori.adventure.text.format.TextDecoration
 *  org.bukkit.Material
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Player
 *  org.bukkit.event.inventory.InventoryClickEvent
 *  org.bukkit.inventory.ItemStack
 *  org.jetbrains.annotations.NotNull
 */
package com.ticxo.modelengine.core.menu.screen;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.menu.AbstractScreen;
import com.ticxo.modelengine.api.menu.Widget;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.ModeledEntity;
import com.ticxo.modelengine.api.utils.MiscUtils;
import com.ticxo.modelengine.api.utils.data.ComponentUtil;
import com.ticxo.modelengine.api.utils.data.ItemUtils;
import com.ticxo.modelengine.api.utils.ticker.DualTicker;
import com.ticxo.modelengine.core.listener.ChatListener;
import com.ticxo.modelengine.core.menu.screen.AddModelScreen;
import com.ticxo.modelengine.core.menu.screen.EditModelScreen;
import com.ticxo.modelengine.core.menu.widget.BorderWidget;
import com.ticxo.modelengine.core.menu.widget.CloseWidget;
import com.ticxo.modelengine.core.menu.widget.PaginatorWidget;
import com.ticxo.modelengine.core.menu.widget.TabWidget;
import com.ticxo.modelengine.core.menu.widget.page.AbstractModelButton;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class EditEntityScreen
extends AbstractScreen {
    private final Entity selected;
    private final TabWidget tab;

    public EditEntityScreen(AbstractScreen rootScreen, Player viewer, @NotNull Entity selected) {
        super(viewer, "Editing: " + selected.getName(), 6);
        this.selected = selected;
        this.addWidget(new BorderWidget());
        this.tab = new TabWidget();
        this.tab.addTab(new SettingsTab());
        this.tab.addTab(new ModelTab());
        this.addWidget(this.tab);
        this.addWidget(new EntityStatsWidget());
        this.addWidget(new CloseWidget(rootScreen));
    }

    @Override
    public void openScreen() {
        TabWidget.Tab tab = this.tab.getSelectedTab();
        if (tab instanceof ModelTab) {
            ModelTab modelTab = (ModelTab)tab;
            modelTab.updatePage();
        }
        super.openScreen();
    }

    class SettingsTab
    implements TabWidget.Tab {
        private final ItemStack stack = new ItemStack(Material.COMMAND_BLOCK);
        private final SettingsWidget widget;

        public SettingsTab() {
            ItemUtils.name(this.stack, (Component)Component.text((String)"Entity Settings", (Style)ComponentUtil.reset()));
            this.widget = new SettingsWidget();
        }

        @Override
        public ItemStack getItemStack() {
            return this.stack;
        }

        @Override
        public Widget getWidget() {
            return this.widget;
        }

        @Override
        public void onSelect() {
        }
    }

    class ModelTab
    implements TabWidget.Tab {
        private final ItemStack stack = new ItemStack(Material.ARMOR_STAND);
        private final PaginatorWidget page;

        public ModelTab() {
            ItemUtils.name(this.stack, (Component)Component.text((String)"Models", (Style)ComponentUtil.reset()));
            this.page = new PaginatorWidget();
        }

        @Override
        public ItemStack getItemStack() {
            return this.stack;
        }

        @Override
        public Widget getWidget() {
            return this.page;
        }

        @Override
        public void onSelect() {
            this.updatePage();
        }

        private void updatePage() {
            this.page.clearButtons();
            ModeledEntity modeledEntity = ModelEngineAPI.getModeledEntity(EditEntityScreen.this.selected);
            if (modeledEntity != null) {
                for (ActiveModel model : modeledEntity.getModels().values()) {
                    ModelButton button = new ModelButton(model);
                    this.page.addButton(button);
                }
            }
            this.page.addButton(new AddModelButton());
        }

        class ModelButton
        extends AbstractModelButton {
            private final ActiveModel model;

            public ModelButton(ActiveModel model) {
                super(model.getBlueprint());
                this.model = model;
            }

            @Override
            protected void updateItem() {
                ItemUtils.name(this.stack, (Component)Component.text((String)this.blueprint.getName(), (Style)ComponentUtil.reset()));
                ItemUtils.lore(this.stack, new Component[]{Component.empty(), Component.text((String)("Model ID: " + this.blueprint.getName()), (Style)ComponentUtil.reset()), Component.text((String)("Hitbox: " + this.blueprint.getMainHitbox().toSimpleString()), (Style)ComponentUtil.reset()), Component.text((String)("Eye Height: " + this.blueprint.getMainHitbox().toEyeHeightString()), (Style)ComponentUtil.reset()), Component.text((String)("Shadow: " + this.blueprint.getShadowRadius()), (Style)ComponentUtil.reset()), Component.text((String)("Bone Count: " + this.blueprint.getFlatMap().size()), (Style)ComponentUtil.reset()), Component.empty(), Component.text((String)"Shift-click to remove model.", (Style)ComponentUtil.color((TextColor)NamedTextColor.DARK_GRAY))});
            }

            @Override
            public void onClick(AbstractScreen screen, Player player, int slot, InventoryClickEvent event) {
                if (event.isShiftClick()) {
                    ModeledEntity modeledEntity = ModelEngineAPI.getModeledEntity(EditEntityScreen.this.selected);
                    if (modeledEntity == null) {
                        return;
                    }
                    modeledEntity.removeModel(this.model.getBlueprint().getName()).ifPresent(ActiveModel::destroy);
                    if (modeledEntity.getModels().isEmpty()) {
                        modeledEntity.setBaseEntityVisible(true);
                        ModelEngineAPI.removeModeledEntity(EditEntityScreen.this.selected);
                    }
                    ModelTab.this.updatePage();
                    EditEntityScreen.this.draw(true);
                } else {
                    new EditModelScreen(screen, player, this.model).openScreen();
                }
            }
        }

        class AddModelButton
        implements PaginatorWidget.PageButton {
            private final ItemStack stack = new ItemStack(Material.NETHER_STAR);

            public AddModelButton() {
                ItemUtils.name(this.stack, (Component)Component.text((String)"Add Model", (Style)ComponentUtil.reset()));
            }

            @Override
            public ItemStack getItemStack() {
                return this.stack;
            }

            @Override
            public void onClick(AbstractScreen screen, Player player, int slot, InventoryClickEvent event) {
                new AddModelScreen(screen, player, EditEntityScreen.this.selected).openScreen();
            }
        }
    }

    class EntityStatsWidget
    implements Widget {
        private static final int SLOT = 53;
        private final ItemStack stack = new ItemStack(Material.OAK_SIGN);

        public EntityStatsWidget() {
            this.updateStats();
        }

        @Override
        public ItemStack getItemForSlot(int size, int slot) {
            return slot == 53 ? this.stack : null;
        }

        @Override
        public void onClick(AbstractScreen screen, Player player, int slot, InventoryClickEvent event) {
            if (slot == 53) {
                if (event.isLeftClick()) {
                    this.updateStats();
                    EditEntityScreen.this.markSlotDirty(53);
                } else if (event.isRightClick()) {
                    player.teleport(EditEntityScreen.this.selected.getLocation());
                }
            }
        }

        private void updateStats() {
            String location = EditEntityScreen.this.selected.getLocation().getBlockX() + ", " + EditEntityScreen.this.selected.getLocation().getBlockY() + ", " + EditEntityScreen.this.selected.getLocation().getBlockZ();
            ItemUtils.name(this.stack, (Component)Component.text((String)"Stats", (Style)ComponentUtil.reset().decoration(TextDecoration.BOLD, true)));
            ItemUtils.lore(this.stack, new Component[]{Component.empty(), Component.text((String)"Type: ", (Style)ComponentUtil.reset()).append((Component)Component.translatable((String)EditEntityScreen.this.selected.getType().getTranslationKey())), Component.text((String)("UUID: " + EditEntityScreen.this.selected.getUniqueId()), (Style)ComponentUtil.reset()), Component.text((String)("Position: " + location), (Style)ComponentUtil.reset()), Component.text((String)("Status: " + (EditEntityScreen.this.selected.isDead() ? "Dead" : "Alive")), (Style)ComponentUtil.reset()), Component.empty(), Component.text((String)"Left click to refresh stats.", (Style)ComponentUtil.color((TextColor)NamedTextColor.DARK_GRAY)), Component.text((String)"Right click to teleport to entity.", (Style)ComponentUtil.color((TextColor)NamedTextColor.DARK_GRAY))});
        }
    }

    class SettingsWidget
    implements Widget {
        private static final int VISIBLE_SLOT = 29;
        private static final int SAVED_SLOT = 30;
        private static final int ROT_LOCK_SLOT = 31;
        private static final int RENDER_RADIUS_SLOT = 32;
        private static final int STEP_HEIGHT_SLOT = 33;
        private final ModeledEntity modeledEntity;
        private final ItemStack visible;
        private final ItemStack saved;
        private final ItemStack rotLock;
        private final ItemStack renderRadius;
        private final ItemStack stepHeight;

        public SettingsWidget() {
            this.modeledEntity = ModelEngineAPI.getOrCreateModeledEntity(EditEntityScreen.this.selected, me -> me.setBaseEntityVisible(false));
            this.visible = new ItemStack(Material.ENDER_EYE);
            ItemUtils.name(this.visible, (Component)Component.text((String)("Base Entity Visible: " + this.modeledEntity.isBaseEntityVisible()), (Style)ComponentUtil.color((TextColor)(this.modeledEntity.isBaseEntityVisible() ? NamedTextColor.GREEN : NamedTextColor.RED))));
            ItemUtils.lore(this.visible, new Component[]{Component.empty(), Component.text((String)"Is the actual entity visible.", (Style)ComponentUtil.reset())});
            this.saved = new ItemStack(Material.CHEST);
            ItemUtils.name(this.saved, (Component)Component.text((String)("Model Persistent: " + this.modeledEntity.shouldBeSaved()), (Style)ComponentUtil.color((TextColor)(this.modeledEntity.shouldBeSaved() ? NamedTextColor.GREEN : NamedTextColor.RED))));
            ItemUtils.lore(this.saved, new Component[]{Component.empty(), Component.text((String)"Should the models be saved on unload.", (Style)ComponentUtil.reset())});
            this.rotLock = new ItemStack(Material.COMPASS);
            ItemUtils.name(this.rotLock, (Component)Component.text((String)("Rotation Locked: " + this.modeledEntity.isModelRotationLocked()), (Style)ComponentUtil.color((TextColor)(this.modeledEntity.isModelRotationLocked() ? NamedTextColor.GREEN : NamedTextColor.RED))));
            ItemUtils.lore(this.rotLock, new Component[]{Component.empty(), Component.text((String)"Can the models rotate with the entity.", (Style)ComponentUtil.reset())});
            this.renderRadius = new ItemStack(Material.SPYGLASS);
            ItemUtils.name(this.renderRadius, (Component)Component.text((String)("Render Radius: " + this.modeledEntity.getBase().getRenderRadius()), (Style)ComponentUtil.reset()));
            ItemUtils.lore(this.renderRadius, new Component[]{Component.empty(), Component.text((String)"The render radius of the entity in blocks.", (Style)ComponentUtil.reset())});
            this.stepHeight = new ItemStack(Material.STONE_STAIRS);
            ItemUtils.name(this.stepHeight, (Component)Component.text((String)("Step Height: " + MiscUtils.FORMATTER.format(this.modeledEntity.getBase().getMaxStepHeight())), (Style)ComponentUtil.reset()));
            ItemUtils.lore(this.stepHeight, new Component[]{Component.empty(), Component.text((String)"The step height of the entity in blocks.", (Style)ComponentUtil.reset())});
        }

        @Override
        public ItemStack getItemForSlot(int size, int slot) {
            return switch (slot) {
                case 29 -> this.visible;
                case 30 -> this.saved;
                case 31 -> this.rotLock;
                case 32 -> this.renderRadius;
                case 33 -> this.stepHeight;
                default -> null;
            };
        }

        @Override
        public void onClick(AbstractScreen screen, Player player, int slot, InventoryClickEvent event) {
            switch (slot) {
                case 29: {
                    this.modeledEntity.setBaseEntityVisible(!this.modeledEntity.isBaseEntityVisible());
                    ItemUtils.name(this.visible, (Component)Component.text((String)("Base Entity Visible: " + this.modeledEntity.isBaseEntityVisible()), (Style)ComponentUtil.color((TextColor)(this.modeledEntity.isBaseEntityVisible() ? NamedTextColor.GREEN : NamedTextColor.RED))));
                    screen.markSlotDirty(29);
                    break;
                }
                case 30: {
                    this.modeledEntity.setSaved(!this.modeledEntity.shouldBeSaved());
                    ItemUtils.name(this.saved, (Component)Component.text((String)("Model Persistent: " + this.modeledEntity.shouldBeSaved()), (Style)ComponentUtil.color((TextColor)(this.modeledEntity.shouldBeSaved() ? NamedTextColor.GREEN : NamedTextColor.RED))));
                    screen.markSlotDirty(30);
                    break;
                }
                case 31: {
                    this.modeledEntity.setModelRotationLocked(!this.modeledEntity.isModelRotationLocked());
                    ItemUtils.name(this.rotLock, (Component)Component.text((String)("Rotation Locked: " + this.modeledEntity.isModelRotationLocked()), (Style)ComponentUtil.color((TextColor)(this.modeledEntity.isModelRotationLocked() ? NamedTextColor.GREEN : NamedTextColor.RED))));
                    screen.markSlotDirty(31);
                    break;
                }
                case 32: {
                    player.closeInventory();
                    ComponentUtil.sendMessage(player, (Component)Component.text((String)"[ModelEngine] Enter render radius:", (Style)ComponentUtil.color((TextColor)NamedTextColor.AQUA)));
                    ChatListener.fetch(player, s -> {
                        try {
                            this.modeledEntity.getBase().setRenderRadius(Integer.parseInt(s));
                            ComponentUtil.sendMessage(player, (Component)Component.text((String)("[ModelEngine] Set render radius of model to " + s + "."), (Style)ComponentUtil.color((TextColor)NamedTextColor.GREEN)));
                            screen.markSlotDirty(32);
                            DualTicker.queueSyncTask(EditEntityScreen.this::openScreen);
                            return true;
                        }
                        catch (NumberFormatException numberFormatException) {
                            ComponentUtil.sendMessage(player, (Component)Component.text((String)("[ModelEngine] \"" + s + "\" is not an integer. Try again."), (Style)ComponentUtil.color((TextColor)NamedTextColor.RED)));
                            return false;
                        }
                    });
                    break;
                }
                case 33: {
                    player.closeInventory();
                    ComponentUtil.sendMessage(player, (Component)Component.text((String)"[ModelEngine] Enter step height:", (Style)ComponentUtil.color((TextColor)NamedTextColor.AQUA)));
                    ChatListener.fetch(player, s -> {
                        try {
                            this.modeledEntity.getBase().setMaxStepHeight(Double.parseDouble(s));
                            ComponentUtil.sendMessage(player, (Component)Component.text((String)("[ModelEngine] Set step height of model to " + s + "."), (Style)ComponentUtil.color((TextColor)NamedTextColor.GREEN)));
                            screen.markSlotDirty(33);
                            DualTicker.queueSyncTask(EditEntityScreen.this::openScreen);
                            return true;
                        }
                        catch (NumberFormatException numberFormatException) {
                            ComponentUtil.sendMessage(player, (Component)Component.text((String)("[ModelEngine] \"" + s + "\" is not a number. Try again."), (Style)ComponentUtil.color((TextColor)NamedTextColor.RED)));
                            return false;
                        }
                    });
                }
            }
        }
    }
}

