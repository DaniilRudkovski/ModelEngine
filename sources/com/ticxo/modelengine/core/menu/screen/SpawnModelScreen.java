/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.kyori.adventure.text.Component
 *  net.kyori.adventure.text.format.Style
 *  net.kyori.adventure.text.format.TextDecoration
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Pig
 *  org.bukkit.entity.Player
 *  org.bukkit.event.inventory.InventoryClickEvent
 *  org.bukkit.inventory.ItemStack
 */
package com.ticxo.modelengine.core.menu.screen;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.entity.BukkitEntity;
import com.ticxo.modelengine.api.generator.blueprint.ModelBlueprint;
import com.ticxo.modelengine.api.menu.AbstractScreen;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.ModelRegistry;
import com.ticxo.modelengine.api.model.ModeledEntity;
import com.ticxo.modelengine.api.model.bone.BoneBehaviorTypes;
import com.ticxo.modelengine.api.model.bone.type.PlayerLimb;
import com.ticxo.modelengine.api.model.bone.type.UserLimb;
import com.ticxo.modelengine.api.utils.data.ComponentUtil;
import com.ticxo.modelengine.api.utils.data.ItemUtils;
import com.ticxo.modelengine.core.menu.widget.BasicItemWidget;
import com.ticxo.modelengine.core.menu.widget.BorderWidget;
import com.ticxo.modelengine.core.menu.widget.CloseWidget;
import com.ticxo.modelengine.core.menu.widget.PaginatorWidget;
import com.ticxo.modelengine.core.menu.widget.page.AbstractModelButton;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class SpawnModelScreen
extends AbstractScreen {
    public SpawnModelScreen(AbstractScreen rootScreen, Player viewer) {
        super(viewer, "Spawn Model", 6);
        this.addWidget(new BorderWidget());
        PaginatorWidget page = new PaginatorWidget();
        ModelRegistry registry = ModelEngineAPI.getAPI().getModelRegistry();
        for (String modelId : registry.getOrderedId()) {
            ModelBlueprint blueprint = (ModelBlueprint)registry.get(modelId);
            ModelButton button = new ModelButton(blueprint);
            page.addButton(button);
        }
        this.addWidget(page);
        ItemStack statSign = new ItemStack(Material.OAK_SIGN);
        ItemUtils.name(statSign, (Component)Component.text((String)"Stats", (Style)ComponentUtil.reset().decoration(TextDecoration.BOLD, true)));
        ItemUtils.lore(statSign, new Component[]{Component.empty(), Component.text((String)("Models: " + registry.getKeys().size()), (Style)ComponentUtil.reset())});
        this.addWidget(new BasicItemWidget(53, statSign));
        this.addWidget(new CloseWidget(rootScreen));
    }

    static class ModelButton
    extends AbstractModelButton {
        public ModelButton(ModelBlueprint blueprint) {
            super(blueprint);
        }

        @Override
        public void onClick(AbstractScreen screen, Player player, int slot, InventoryClickEvent event) {
            if (!event.isLeftClick() && !event.isRightClick()) {
                return;
            }
            Location location = player.getLocation();
            player.getWorld().spawn(location, Pig.class, entity -> {
                BukkitEntity base = new BukkitEntity((Entity)entity);
                base.getBodyRotationController().setYBodyRot(location.getYaw());
                ModeledEntity modeledEntity = ModelEngineAPI.createModeledEntity(base);
                modeledEntity.setBaseEntityVisible(false);
                ActiveModel activeModel = ModelEngineAPI.createActiveModel(this.blueprint);
                activeModel.setAutoRendererInitialization(false);
                modeledEntity.addModel(activeModel, true).ifPresent(ActiveModel::destroy);
                activeModel.getBones().values().forEach(modelBone -> {
                    modelBone.getBoneBehavior(BoneBehaviorTypes.PLAYER_LIMB).ifPresent(playerLimb -> ((PlayerLimb)((Object)playerLimb)).setTexture(player));
                    modelBone.getBoneBehavior(BoneBehaviorTypes.USER_LIMB).ifPresent(playerLimb -> ((UserLimb)((Object)playerLimb)).setTexture(player));
                });
                activeModel.initializeRenderer();
            });
        }
    }
}

