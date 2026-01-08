/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonPrimitive
 */
package com.ticxo.modelengine.core.generator.java;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.ticxo.modelengine.api.generator.BaseItemEnum;
import com.ticxo.modelengine.api.generator.assets.ItemModel;
import com.ticxo.modelengine.api.generator.assets.TintSource;
import com.ticxo.modelengine.core.generator.java.BaseItem;

public class BaseItemTransformer {
    public static ItemModel to1214Format(BaseItem item) {
        ItemModel.RangeDispatch model = new ItemModel.RangeDispatch();
        TintSource tint = switch (item.getBaseItemEnum()) {
            default -> throw new IncompatibleClassChangeError();
            case BaseItemEnum.FILLED_MAP -> new TintSource.MapColor(-1);
            case BaseItemEnum.LEATHER_BOOTS, BaseItemEnum.LEATHER_CHESTPLATE, BaseItemEnum.LEATHER_HORSE_ARMOR, BaseItemEnum.LEATHER_LEGGINGS -> new TintSource.Dye(-1);
            case BaseItemEnum.LINGERING_POTION, BaseItemEnum.POTION, BaseItemEnum.SPLASH_POTION, BaseItemEnum.TIPPED_ARROW -> new TintSource.Potion(-1);
        };
        for (BaseItem.JavaOverride overrides : item.getOverrides()) {
            int id = overrides.getPredicate().custom_model_data();
            ItemModel.Model idModel = new ItemModel.Model(overrides.getModel());
            idModel.getTints().add(tint);
            model.getEntries().add(new ItemModel.RangeDispatch.Entry(idModel, id));
        }
        ItemModel.Model fallback = new ItemModel.Model("minecraft:item/" + item.getName());
        switch (item.getBaseItemEnum()) {
            case FILLED_MAP: {
                fallback.getTints().add(new TintSource.Constant(-1));
                fallback.getTints().add(new TintSource.MapColor());
                break;
            }
            case LEATHER_BOOTS: 
            case LEATHER_CHESTPLATE: 
            case LEATHER_HORSE_ARMOR: 
            case LEATHER_LEGGINGS: {
                fallback.getTints().add(new TintSource.Dye());
                break;
            }
            case LINGERING_POTION: 
            case POTION: 
            case SPLASH_POTION: 
            case TIPPED_ARROW: {
                fallback.getTints().add(new TintSource.Potion());
            }
        }
        model.setFallback(fallback);
        return model;
    }

    public static JsonElement wrapModel(Gson gson, ItemModel model, boolean oversized) {
        JsonObject obj = new JsonObject();
        obj.add("model", gson.toJsonTree((Object)model));
        if (oversized) {
            obj.add("oversized_in_gui", (JsonElement)new JsonPrimitive(Boolean.valueOf(true)));
        }
        return obj;
    }
}

