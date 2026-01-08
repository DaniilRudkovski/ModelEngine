/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.kyori.adventure.text.Component
 *  net.kyori.adventure.text.TextComponent
 *  net.kyori.adventure.text.format.NamedTextColor
 *  net.kyori.adventure.text.format.Style
 *  net.kyori.adventure.text.format.TextColor
 *  org.bukkit.Color
 *  org.bukkit.Material
 *  org.bukkit.enchantments.Enchantment
 *  org.bukkit.entity.Player
 *  org.bukkit.event.inventory.InventoryClickEvent
 *  org.bukkit.inventory.ItemFlag
 *  org.bukkit.inventory.ItemStack
 */
package com.ticxo.modelengine.core.menu.screen;

import com.ticxo.modelengine.api.animation.BlueprintAnimation;
import com.ticxo.modelengine.api.animation.handler.AnimationHandler;
import com.ticxo.modelengine.api.animation.property.IAnimationProperty;
import com.ticxo.modelengine.api.animation.property.SimpleProperty;
import com.ticxo.modelengine.api.generator.blueprint.BlueprintBone;
import com.ticxo.modelengine.api.generator.blueprint.ModelBlueprint;
import com.ticxo.modelengine.api.menu.AbstractScreen;
import com.ticxo.modelengine.api.menu.Widget;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.bone.ModelBone;
import com.ticxo.modelengine.api.model.bone.behavior.BoneBehavior;
import com.ticxo.modelengine.api.model.bone.behavior.BoneBehaviorType;
import com.ticxo.modelengine.api.utils.MiscUtils;
import com.ticxo.modelengine.api.utils.data.ComponentUtil;
import com.ticxo.modelengine.api.utils.data.ItemUtils;
import com.ticxo.modelengine.api.utils.ticker.DualTicker;
import com.ticxo.modelengine.core.animation.handler.StateMachineHandler;
import com.ticxo.modelengine.core.listener.ChatListener;
import com.ticxo.modelengine.core.menu.widget.BorderWidget;
import com.ticxo.modelengine.core.menu.widget.CloseWidget;
import com.ticxo.modelengine.core.menu.widget.CompositeWidget;
import com.ticxo.modelengine.core.menu.widget.PaginatorWidget;
import com.ticxo.modelengine.core.menu.widget.TabWidget;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

public class EditModelScreen
extends AbstractScreen {
    private final ActiveModel model;
    private final TabWidget tab;

    public EditModelScreen(AbstractScreen rootScreen, Player viewer, ActiveModel model) {
        super(viewer, model.getBlueprint().getName(), 6);
        this.model = model;
        this.addWidget(new BorderWidget());
        this.tab = new TabWidget();
        this.tab.addTab(new SettingsTab());
        this.tab.addTab(new BoneTab());
        this.tab.addTab(new AnimationTab());
        this.addWidget(this.tab);
        this.addWidget(new CloseWidget(rootScreen));
    }

    @Override
    public void onTick() {
        TabWidget.Tab tab = this.tab.getSelectedTab();
        if (tab instanceof AnimationTab) {
            AnimationTab animationTab = (AnimationTab)tab;
            animationTab.onTick();
        }
        super.onTick();
    }

    class SettingsTab
    implements TabWidget.Tab {
        private final ItemStack stack = new ItemStack(Material.COMMAND_BLOCK);
        private final SettingsWidget widget;

        public SettingsTab() {
            ItemUtils.name(this.stack, (Component)Component.text((String)"Model Settings", (Style)ComponentUtil.reset()));
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

    class BoneTab
    implements TabWidget.Tab {
        private final ItemStack stack = new ItemStack(Material.BONE);
        private final PaginatorWidget page;

        public BoneTab() {
            ItemUtils.name(this.stack, (Component)Component.text((String)"Bones", (Style)ComponentUtil.reset()));
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
            this.page.clearButtons();
            for (ModelBone bone : EditModelScreen.this.model.getBones().values()) {
                this.page.addButton(new BoneButton(bone));
            }
        }
    }

    class AnimationTab
    implements TabWidget.Tab {
        private final ItemStack stack = new ItemStack(Material.ARMOR_STAND);
        private final CompositeWidget composite;
        private final PaginatorWidget page;
        private final PriorityButton priority;

        public AnimationTab() {
            ItemUtils.name(this.stack, (Component)Component.text((String)"Animations", (Style)ComponentUtil.reset()));
            this.page = new PaginatorWidget();
            if (EditModelScreen.this.model.getAnimationHandler() instanceof StateMachineHandler) {
                this.priority = new PriorityButton();
                this.composite = new CompositeWidget(this.page, this.priority);
            } else {
                this.priority = null;
                this.composite = null;
            }
        }

        @Override
        public ItemStack getItemStack() {
            return this.stack;
        }

        @Override
        public Widget getWidget() {
            return this.composite != null ? this.composite : this.page;
        }

        @Override
        public void onSelect() {
            this.page.clearButtons();
            ModelBlueprint blueprint = EditModelScreen.this.model.getBlueprint();
            Map<String, BlueprintAnimation> pairs = blueprint.getAnimations();
            for (Map.Entry<String, BlueprintAnimation> entry : pairs.entrySet()) {
                this.page.addButton(new AnimationButton(entry.getValue()));
            }
        }

        protected void onTick() {
            block3: for (int i = 19; i <= 43; ++i) {
                switch (i % 9) {
                    case 0: 
                    case 8: {
                        continue block3;
                    }
                    default: {
                        AnimationButton button;
                        PaginatorWidget.PageButton pageButton = this.page.getButton(i);
                        if (!(pageButton instanceof AnimationButton) || !(button = (AnimationButton)pageButton).update(false)) continue block3;
                        EditModelScreen.this.markSlotDirty(i);
                    }
                }
            }
        }

        class PriorityButton
        implements Widget {
            private static final int SLOT = 51;
            private final ItemStack stack = new ItemStack(Material.COMPARATOR);
            private int priority = 0;

            public PriorityButton() {
                ItemUtils.name(this.stack, (Component)Component.text((String)("Priority: " + this.priority), (Style)ComponentUtil.reset()));
                ItemUtils.lore(this.stack, ((TextComponent)((TextComponent)((TextComponent)Component.empty().style(ComponentUtil.reset())).append((Component)Component.text((String)"<<< Left Click"))).append((Component)Component.text((String)" | "))).append((Component)Component.text((String)"Right Click >>>")));
            }

            @Override
            public ItemStack getItemForSlot(int size, int slot) {
                return 51 == slot ? this.stack : null;
            }

            @Override
            public void onClick(AbstractScreen screen, Player player, int slot, InventoryClickEvent event) {
                if (slot != 51) {
                    return;
                }
                if (event.getClick().isLeftClick()) {
                    --this.priority;
                } else if (event.getClick().isRightClick()) {
                    ++this.priority;
                }
                AnimationTab.this.page.refreshPage(screen);
                ItemUtils.name(this.stack, (Component)Component.text((String)("Priority: " + this.priority), (Style)ComponentUtil.reset()));
                screen.markSlotsDirty(51);
            }
        }

        class AnimationButton
        implements PaginatorWidget.PageButton {
            private final BlueprintAnimation animation;
            private ItemStack stack;
            private IAnimationProperty trackedProperty;
            private int lastHash;

            public AnimationButton(BlueprintAnimation animation) {
                this.animation = animation;
                this.update(true);
            }

            @Override
            public ItemStack getItemStack() {
                return this.stack;
            }

            @Override
            public void onClick(AbstractScreen screen, Player player, int slot, InventoryClickEvent event) {
                AnimationHandler handler = EditModelScreen.this.model.getAnimationHandler();
                SimpleProperty prop = new SimpleProperty(EditModelScreen.this.model, this.animation);
                if (prop.getLoopMode() == BlueprintAnimation.LoopMode.LOOP) {
                    prop.setSkipLastFrame(true);
                }
                if (handler instanceof StateMachineHandler) {
                    StateMachineHandler stateMachineHandler = (StateMachineHandler)handler;
                    if (stateMachineHandler.isPlayingAnimation(AnimationTab.this.priority.priority, this.animation.getName())) {
                        stateMachineHandler.stopAnimation(AnimationTab.this.priority.priority, this.animation.getName());
                    } else {
                        stateMachineHandler.playAnimation(AnimationTab.this.priority.priority, prop, true);
                    }
                } else if (handler.isPlayingAnimation(this.animation.getName())) {
                    handler.stopAnimation(this.animation.getName());
                } else {
                    handler.playAnimation(prop, true);
                }
            }

            public boolean update(boolean force) {
                IAnimationProperty property;
                AnimationHandler handler = EditModelScreen.this.model.getAnimationHandler();
                if (handler instanceof StateMachineHandler) {
                    StateMachineHandler stateMachineHandler = (StateMachineHandler)handler;
                    v0 = stateMachineHandler.getAnimation(AnimationTab.this.priority.priority, this.animation.getName());
                } else {
                    v0 = property = handler.getAnimation(this.animation.getName());
                }
                if (property == null) {
                    if (!force && this.trackedProperty == null) {
                        return false;
                    }
                    this.trackedProperty = null;
                    this.stack = new ItemStack(Material.RED_CONCRETE);
                    ItemUtils.name(this.stack, (Component)Component.text((String)this.animation.getName(), (Style)ComponentUtil.color((TextColor)NamedTextColor.RED)));
                    ItemUtils.lore(this.stack, new Component[]{Component.empty(), Component.text((String)"Animation:", (Style)ComponentUtil.reset()), Component.text((String)("- Animation ID: " + this.animation.getName()), (Style)ComponentUtil.reset()), Component.text((String)("- Loop Mode: " + this.animation.getLoopMode()), (Style)ComponentUtil.reset()), Component.text((String)("- Override: " + this.animation.isOverride()), (Style)ComponentUtil.reset()), Component.text((String)("- Length: " + MiscUtils.FORMATTER.format(this.animation.getLength()) + "s"), (Style)ComponentUtil.reset())});
                } else {
                    if (!force && this.trackedProperty != null) {
                        int hash = this.trackedProperty.hashCode();
                        if (hash == this.lastHash) {
                            return false;
                        }
                        this.lastHash = hash;
                    }
                    this.trackedProperty = property;
                    this.stack = new ItemStack(Material.LIME_CONCRETE);
                    ItemUtils.name(this.stack, (Component)Component.text((String)this.animation.getName(), (Style)ComponentUtil.color((TextColor)NamedTextColor.GREEN)));
                    ItemUtils.lore(this.stack, new Component[]{Component.empty(), Component.text((String)"Animation:", (Style)ComponentUtil.reset()), Component.text((String)("- Animation ID: " + this.animation.getName()), (Style)ComponentUtil.reset()), Component.text((String)("- Loop Mode: " + this.animation.getLoopMode()), (Style)ComponentUtil.reset()), Component.text((String)("- Override: " + this.animation.isOverride()), (Style)ComponentUtil.reset()), Component.text((String)("- Length: " + MiscUtils.FORMATTER.format(this.animation.getLength()) + "s"), (Style)ComponentUtil.reset()), Component.empty(), Component.text((String)"Property:", (Style)ComponentUtil.reset()), Component.text((String)("- Phase: " + property.getPhase() + (String)(property.getPhase() == IAnimationProperty.Phase.PLAY ? " (" + MiscUtils.FORMATTER.format(property.getTime()) + "s)" : "")), (Style)ComponentUtil.reset()), Component.text((String)("- Forced Loop Mode: " + property.getForceLoopMode()), (Style)ComponentUtil.reset()), Component.text((String)("- Forced Override: " + property.getForceOverride()), (Style)ComponentUtil.reset()), Component.text((String)("- Speed: " + MiscUtils.FORMATTER.format(property.getSpeed()) + "x"), (Style)ComponentUtil.reset()), Component.text((String)("- Lerp-In: " + MiscUtils.FORMATTER.format(property.getLerpIn()) + "s"), (Style)ComponentUtil.reset()), Component.text((String)("- Lerp-Out: " + MiscUtils.FORMATTER.format(property.getLerpOut()) + "s"), (Style)ComponentUtil.reset())});
                }
                return true;
            }
        }
    }

    static class BoneButton
    implements PaginatorWidget.PageButton {
        private final ModelBone bone;
        private ItemStack stack;

        public BoneButton(ModelBone bone) {
            this.bone = bone;
            this.updateItem();
        }

        @Override
        public ItemStack getItemStack() {
            return this.stack;
        }

        @Override
        public void onClick(AbstractScreen screen, Player player, int slot, InventoryClickEvent event) {
            this.bone.setVisible(!this.bone.isVisible());
            this.updateItem();
            screen.markSlotDirty(slot);
        }

        private void updateItem() {
            Collection<ItemStack> items;
            BlueprintBone blueprintBone = this.bone.getBlueprintBone();
            if (blueprintBone.isRenderer() && !(items = blueprintBone.getModelData().createItemStack()).isEmpty()) {
                this.stack = items.iterator().next();
                this.stack.addItemFlags(new ItemFlag[]{ItemFlag.HIDE_DYE});
            }
            if (this.stack == null) {
                this.stack = new ItemStack(Material.BONE);
            }
            if (this.bone.isEnchanted()) {
                this.stack.addUnsafeEnchantment(Enchantment.VANISHING_CURSE, 1);
                this.stack.addItemFlags(new ItemFlag[]{ItemFlag.HIDE_ENCHANTS});
            }
            ItemUtils.name(this.stack, (Component)Component.text((String)this.bone.getUniqueBoneId(), (Style)ComponentUtil.reset()));
            ArrayList<Component> lore = new ArrayList<Component>();
            lore.add((Component)Component.empty());
            lore.add((Component)Component.text((String)("Bone ID: " + this.bone.getBoneId()), (Style)ComponentUtil.reset()));
            if (this.bone.getParent() != null) {
                lore.add((Component)Component.text((String)("Parent: " + this.bone.getParent().getBoneId()), (Style)ComponentUtil.reset()));
            }
            lore.add((Component)Component.text((String)("Visible: " + this.bone.isVisible()), (Style)ComponentUtil.reset()));
            int defColor = this.bone.getDefaultTint().asRGB();
            int dmgColor = this.bone.getDamageTint().asRGB();
            lore.add(Component.text((String)"Default Color: ", (Style)ComponentUtil.reset()).append((Component)Component.text((String)("#" + Integer.toString(defColor, 16).toUpperCase(Locale.ENGLISH)), (Style)Style.style((TextColor)TextColor.color((int)defColor)))));
            lore.add(Component.text((String)"Damage Color: ", (Style)ComponentUtil.reset()).append((Component)Component.text((String)("#" + Integer.toString(dmgColor, 16).toUpperCase(Locale.ENGLISH)), (Style)Style.style((TextColor)TextColor.color((int)dmgColor)))));
            lore.add((Component)Component.text((String)("Enchanted: " + this.bone.isEnchanted()), (Style)ComponentUtil.reset()));
            boolean hasBehavior = false;
            for (Map.Entry<BoneBehaviorType<?>, BoneBehavior> behavior : this.bone.getImmutableBoneBehaviors().entrySet()) {
                if (behavior.getValue().isHidden()) continue;
                if (!hasBehavior) {
                    hasBehavior = true;
                    lore.add((Component)Component.text((String)"Behavior: ", (Style)ComponentUtil.reset()));
                }
                lore.add((Component)Component.text((String)("- " + behavior.getKey().getId()), (Style)ComponentUtil.reset()));
            }
            ItemUtils.lore(this.stack, lore);
        }
    }

    class SettingsWidget
    implements Widget {
        private static final int RENDER_SCALE_SLOT = 21;
        private static final int HITBOX_SCALE_SLOT = 22;
        private static final int HITBOX_VISIBLE_SLOT = 23;
        private static final int SHADOW_VISIBLE_SLOT = 30;
        private static final int DEFAULT_TINT_SLOT = 31;
        private static final int DAMAGE_TINT_SLOT = 32;
        private static final int DAMAGE_TINT_VISIBLE_SLOT = 39;
        private static final int PITCH_LOCK_SLOT = 40;
        private static final int YAW_LOCK_SLOT = 41;
        private final ItemStack renderScale = new ItemStack(Material.SLIME_BALL);
        private final ItemStack hitboxScale = new ItemStack(Material.MAGMA_CREAM);
        private final ItemStack hitboxVisible = new ItemStack(Material.SHULKER_SHELL);
        private final ItemStack shadowVisible = new ItemStack(Material.BLACK_DYE);
        private final ItemStack defaultTint = new ItemStack(Material.WHITE_DYE);
        private final ItemStack damageTint = new ItemStack(Material.RED_DYE);
        private final ItemStack damageTintVisible = new ItemStack(Material.DIAMOND_SWORD);
        private final ItemStack pitchLock;
        private final ItemStack yawLock;

        public SettingsWidget() {
            ItemUtils.meta(this.damageTintVisible, meta -> meta.addItemFlags(new ItemFlag[]{ItemFlag.HIDE_ATTRIBUTES}));
            this.pitchLock = new ItemStack(Material.CLOCK);
            this.yawLock = new ItemStack(Material.COMPASS);
            this.update();
        }

        @Override
        public ItemStack getItemForSlot(int size, int slot) {
            return switch (slot) {
                case 21 -> this.renderScale;
                case 22 -> this.hitboxScale;
                case 23 -> this.hitboxVisible;
                case 30 -> this.shadowVisible;
                case 31 -> this.defaultTint;
                case 32 -> this.damageTint;
                case 39 -> this.damageTintVisible;
                case 40 -> this.pitchLock;
                case 41 -> this.yawLock;
                default -> null;
            };
        }

        @Override
        public void onClick(AbstractScreen screen, Player player, int slot, InventoryClickEvent event) {
            switch (slot) {
                case 21: {
                    player.closeInventory();
                    ComponentUtil.sendMessage(player, (Component)Component.text((String)"[ModelEngine] Enter render scale:", (Style)ComponentUtil.color((TextColor)NamedTextColor.AQUA)));
                    ChatListener.fetch(player, s -> {
                        try {
                            EditModelScreen.this.model.setScale(Double.parseDouble(s));
                            ComponentUtil.sendMessage(player, (Component)Component.text((String)("[ModelEngine] Set render scale of model to " + s + "."), (Style)ComponentUtil.color((TextColor)NamedTextColor.GREEN)));
                            this.update(slot);
                            DualTicker.queueSyncTask(EditModelScreen.this::openScreen);
                            return true;
                        }
                        catch (NumberFormatException numberFormatException) {
                            ComponentUtil.sendMessage(player, (Component)Component.text((String)("[ModelEngine] \"" + s + "\" is not a number. Try again."), (Style)ComponentUtil.color((TextColor)NamedTextColor.RED)));
                            return false;
                        }
                    });
                    break;
                }
                case 22: {
                    player.closeInventory();
                    ComponentUtil.sendMessage(player, (Component)Component.text((String)"[ModelEngine] Enter hitbox scale:", (Style)ComponentUtil.color((TextColor)NamedTextColor.AQUA)));
                    ChatListener.fetch(player, s -> {
                        try {
                            EditModelScreen.this.model.setHitboxScale(Double.parseDouble(s));
                            ComponentUtil.sendMessage(player, (Component)Component.text((String)("[ModelEngine] Set hitbox scale of model to " + s + "."), (Style)ComponentUtil.color((TextColor)NamedTextColor.GREEN)));
                            this.update(slot);
                            DualTicker.queueSyncTask(EditModelScreen.this::openScreen);
                            return true;
                        }
                        catch (NumberFormatException numberFormatException) {
                            ComponentUtil.sendMessage(player, (Component)Component.text((String)("[ModelEngine] \"" + s + "\" is not a number. Try again."), (Style)ComponentUtil.color((TextColor)NamedTextColor.RED)));
                            return false;
                        }
                    });
                    break;
                }
                case 23: {
                    EditModelScreen.this.model.setHitboxVisible(!EditModelScreen.this.model.isHitboxVisible());
                    break;
                }
                case 30: {
                    EditModelScreen.this.model.setShadowVisible(!EditModelScreen.this.model.isShadowVisible());
                    break;
                }
                case 31: {
                    player.closeInventory();
                    ComponentUtil.sendMessage(player, (Component)Component.text((String)"[ModelEngine] Enter default color in hex:", (Style)ComponentUtil.color((TextColor)NamedTextColor.AQUA)));
                    ChatListener.fetch(player, s -> {
                        try {
                            if (s.startsWith("#")) {
                                s = s.substring(1);
                            }
                            int color = Integer.parseInt(s, 16);
                            EditModelScreen.this.model.setDefaultTint(Color.fromRGB((int)color));
                            ComponentUtil.sendMessage(player, ((TextComponent)Component.text((String)"[ModelEngine] Set default color of model to ", (Style)ComponentUtil.color((TextColor)NamedTextColor.GREEN)).append((Component)Component.text((String)("#" + s.toUpperCase(Locale.ENGLISH)), (Style)ComponentUtil.color(TextColor.color((int)color))))).append((Component)Component.text((String)".")));
                            this.update(slot);
                            DualTicker.queueSyncTask(EditModelScreen.this::openScreen);
                            return true;
                        }
                        catch (NumberFormatException numberFormatException) {
                            ComponentUtil.sendMessage(player, (Component)Component.text((String)("[ModelEngine] \"" + s + "\" is not a valid color. Try again."), (Style)ComponentUtil.color((TextColor)NamedTextColor.RED)));
                            return false;
                        }
                    });
                    break;
                }
                case 32: {
                    player.closeInventory();
                    ComponentUtil.sendMessage(player, (Component)Component.text((String)"[ModelEngine] Enter damage color in hex:", (Style)ComponentUtil.color((TextColor)NamedTextColor.AQUA)));
                    ChatListener.fetch(player, s -> {
                        try {
                            if (s.startsWith("#")) {
                                s = s.substring(1);
                            }
                            int color = Integer.parseInt(s, 16);
                            EditModelScreen.this.model.setDamageTint(Color.fromRGB((int)color));
                            ComponentUtil.sendMessage(player, ((TextComponent)Component.text((String)"[ModelEngine] Set damage color of model to ", (Style)ComponentUtil.color((TextColor)NamedTextColor.GREEN)).append((Component)Component.text((String)("#" + s.toUpperCase(Locale.ENGLISH)), (Style)ComponentUtil.color(TextColor.color((int)color))))).append((Component)Component.text((String)".")));
                            this.update(slot);
                            DualTicker.queueSyncTask(EditModelScreen.this::openScreen);
                            return true;
                        }
                        catch (NumberFormatException numberFormatException) {
                            ComponentUtil.sendMessage(player, (Component)Component.text((String)("[ModelEngine] \"" + s + "\" is not a valid color. Try again."), (Style)ComponentUtil.color((TextColor)NamedTextColor.RED)));
                            return false;
                        }
                    });
                    break;
                }
                case 39: {
                    EditModelScreen.this.model.setCanHurt(!EditModelScreen.this.model.canHurt());
                    break;
                }
                case 40: {
                    EditModelScreen.this.model.setLockPitch(!EditModelScreen.this.model.isLockPitch());
                    break;
                }
                case 41: {
                    EditModelScreen.this.model.setLockYaw(!EditModelScreen.this.model.isLockYaw());
                }
            }
            this.update(slot);
            EditModelScreen.this.markSlotDirty(slot);
        }

        public void update() {
            this.update(21);
            this.update(22);
            this.update(23);
            this.update(30);
            this.update(31);
            this.update(32);
            this.update(39);
            this.update(40);
            this.update(41);
        }

        public void update(int slot) {
            switch (slot) {
                case 21: {
                    ItemUtils.name(this.renderScale, (Component)Component.text((String)("Render Scale: " + MiscUtils.FORMATTER.format(EditModelScreen.this.model.getScale().x())), (Style)ComponentUtil.reset()));
                    break;
                }
                case 22: {
                    ItemUtils.name(this.hitboxScale, (Component)Component.text((String)("Hitbox Scale: " + MiscUtils.FORMATTER.format(EditModelScreen.this.model.getHitboxScale().x())), (Style)ComponentUtil.reset()));
                    break;
                }
                case 23: {
                    ItemUtils.name(this.hitboxVisible, (Component)Component.text((String)("Hitbox Visible: " + EditModelScreen.this.model.isHitboxVisible()), (Style)ComponentUtil.color((TextColor)(EditModelScreen.this.model.isHitboxVisible() ? NamedTextColor.GREEN : NamedTextColor.RED))));
                    break;
                }
                case 30: {
                    ItemUtils.name(this.shadowVisible, (Component)Component.text((String)("Shadow Visible: " + EditModelScreen.this.model.isShadowVisible()), (Style)ComponentUtil.color((TextColor)(EditModelScreen.this.model.isShadowVisible() ? NamedTextColor.GREEN : NamedTextColor.RED))));
                    break;
                }
                case 31: {
                    ItemUtils.name(this.defaultTint, (Component)Component.text((String)("Default Tint: #" + Integer.toHexString(EditModelScreen.this.model.getDefaultTint().asARGB()).toUpperCase(Locale.ENGLISH).substring(2)), (Style)ComponentUtil.color(TextColor.color((int)EditModelScreen.this.model.getDefaultTint().asRGB()))));
                    break;
                }
                case 32: {
                    ItemUtils.name(this.damageTint, (Component)Component.text((String)("Damage Tint: #" + Integer.toHexString(EditModelScreen.this.model.getDamageTint().asARGB()).toUpperCase(Locale.ENGLISH).substring(2)), (Style)ComponentUtil.color(TextColor.color((int)EditModelScreen.this.model.getDamageTint().asRGB()))));
                    break;
                }
                case 39: {
                    ItemUtils.name(this.damageTintVisible, (Component)Component.text((String)("Damage Tint Visible: " + EditModelScreen.this.model.canHurt()), (Style)ComponentUtil.color((TextColor)(EditModelScreen.this.model.canHurt() ? NamedTextColor.GREEN : NamedTextColor.RED))));
                    break;
                }
                case 40: {
                    ItemUtils.name(this.pitchLock, (Component)Component.text((String)("Pitch Lock: " + EditModelScreen.this.model.isLockPitch()), (Style)ComponentUtil.color((TextColor)(EditModelScreen.this.model.isLockPitch() ? NamedTextColor.GREEN : NamedTextColor.RED))));
                    break;
                }
                case 41: {
                    ItemUtils.name(this.yawLock, (Component)Component.text((String)("Yaw Lock: " + EditModelScreen.this.model.isLockYaw()), (Style)ComponentUtil.color((TextColor)(EditModelScreen.this.model.isLockYaw() ? NamedTextColor.GREEN : NamedTextColor.RED))));
                }
            }
        }
    }
}

