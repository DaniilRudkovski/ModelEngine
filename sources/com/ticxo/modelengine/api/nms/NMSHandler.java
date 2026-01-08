/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Color
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.meta.ItemMeta
 *  org.bukkit.util.Transformation
 *  org.joml.Matrix4f
 */
package com.ticxo.modelengine.api.nms;

import com.ticxo.modelengine.api.generator.BaseItemEnum;
import com.ticxo.modelengine.api.generator.assets.ItemModelData;
import com.ticxo.modelengine.api.model.bone.render.BehaviorRenderer;
import com.ticxo.modelengine.api.model.bone.render.BehaviorRendererParser;
import com.ticxo.modelengine.api.model.render.ModelRenderer;
import com.ticxo.modelengine.api.model.render.ModelRendererParser;
import com.ticxo.modelengine.api.nms.RenderParsers;
import com.ticxo.modelengine.api.nms.entity.EntityHandler;
import com.ticxo.modelengine.api.nms.network.NetworkHandler;
import com.ticxo.modelengine.api.nms.ui.AnvilHandler;
import com.ticxo.modelengine.api.vfx.render.VFXRenderer;
import com.ticxo.modelengine.api.vfx.render.VFXRendererParser;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import org.bukkit.Color;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Transformation;
import org.joml.Matrix4f;

public interface NMSHandler {
    public EntityHandler getEntityHandler();

    public NetworkHandler getNetworkHandler();

    default public AnvilHandler getAnvilHandler() {
        return null;
    }

    public RenderParsers getGlobalParsers();

    public RenderParsers createParsers();

    public void sendPivotOverrides();

    default public Set<ItemStack> createStack(ItemModelData data, ItemModelData.Context context) {
        Collection<ItemModelData.SubModel> subModels = data.getMultiModels().getSubModels();
        return subModels.stream().map(subModel -> subModel.getItem().create(context.color(), subModel.getData())).collect(Collectors.toSet());
    }

    default public boolean colorStack(ItemStack stack, Color color) {
        BaseItemEnum base = BaseItemEnum.fromMaterial(stack.getType());
        if (base == null) {
            return false;
        }
        ItemMeta meta = stack.getItemMeta();
        base.color(meta, color);
        stack.setItemMeta(meta);
        return true;
    }

    public <T extends ModelRenderer> ModelRendererParser<T> getModelRendererParser(T var1);

    public <T extends BehaviorRenderer> BehaviorRendererParser<T> getBehaviorRendererParser(T var1);

    public <T extends VFXRenderer> VFXRendererParser<T> getVFXRendererParser(T var1);

    public Transformation decompose(Matrix4f var1);
}

