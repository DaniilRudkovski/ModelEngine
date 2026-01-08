/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.kyori.adventure.text.Component
 *  net.kyori.adventure.text.format.Style
 *  org.bukkit.Material
 *  org.bukkit.inventory.ItemFlag
 *  org.bukkit.inventory.ItemStack
 */
package com.ticxo.modelengine.core.menu.widget.page;

import com.ticxo.modelengine.api.generator.blueprint.BlueprintBone;
import com.ticxo.modelengine.api.generator.blueprint.ModelBlueprint;
import com.ticxo.modelengine.api.utils.data.ComponentUtil;
import com.ticxo.modelengine.api.utils.data.ItemUtils;
import com.ticxo.modelengine.core.menu.widget.PaginatorWidget;
import java.util.Collection;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

public abstract class AbstractModelButton
implements PaginatorWidget.PageButton {
    protected final ModelBlueprint blueprint;
    protected final ItemStack stack;

    public AbstractModelButton(ModelBlueprint blueprint) {
        Collection<ItemStack> items;
        this.blueprint = blueprint;
        ItemStack bone = null;
        BlueprintBone headBone = blueprint.getFlatMap().get("head");
        if (headBone != null && headBone.isRenderer() && !(items = headBone.getModelData().createItemStack()).isEmpty()) {
            bone = items.iterator().next();
            bone.addItemFlags(new ItemFlag[]{ItemFlag.HIDE_DYE});
        }
        if (bone == null) {
            bone = new ItemStack(Material.ARMOR_STAND);
        }
        this.stack = bone;
        this.updateItem();
    }

    protected void updateItem() {
        ItemUtils.name(this.stack, (Component)Component.text((String)this.blueprint.getName(), (Style)ComponentUtil.reset()));
        ItemUtils.lore(this.stack, new Component[]{Component.empty(), Component.text((String)("Model ID: " + this.blueprint.getName()), (Style)ComponentUtil.reset()), Component.text((String)("Hitbox: " + this.blueprint.getMainHitbox().toSimpleString()), (Style)ComponentUtil.reset()), Component.text((String)("Eye Height: " + this.blueprint.getMainHitbox().toEyeHeightString()), (Style)ComponentUtil.reset()), Component.text((String)("Shadow: " + this.blueprint.getShadowRadius()), (Style)ComponentUtil.reset()), Component.text((String)("Bone Count: " + this.blueprint.getFlatMap().size()), (Style)ComponentUtil.reset())});
    }

    @Override
    public ItemStack getItemStack() {
        return this.stack;
    }
}

