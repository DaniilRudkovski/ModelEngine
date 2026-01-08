/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
 *  lombok.Generated
 *  org.bukkit.Color
 *  org.bukkit.NamespacedKey
 *  org.bukkit.inventory.ItemStack
 *  org.jetbrains.annotations.Nullable
 */
package com.ticxo.modelengine.api.generator.assets;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.generator.BaseItemEnum;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import lombok.Generated;
import org.bukkit.Color;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public class ItemModelData {
    private final MultiModels multiModels = new MultiModels();
    private SingleComposite singleComposite;
    private boolean isTranslucent;

    public static Context.ContextBuilder context() {
        return Context.builder();
    }

    public Collection<ItemStack> createItemStack() {
        return this.createItemStack(Context.builder().color(Color.WHITE).build());
    }

    public Set<ItemStack> createItemStack(@Nullable Context context) {
        return ModelEngineAPI.getNMSHandler().createStack(this, context);
    }

    @Generated
    public MultiModels getMultiModels() {
        return this.multiModels;
    }

    @Generated
    public SingleComposite getSingleComposite() {
        return this.singleComposite;
    }

    @Generated
    public boolean isTranslucent() {
        return this.isTranslucent;
    }

    @Generated
    public void setSingleComposite(SingleComposite singleComposite) {
        this.singleComposite = singleComposite;
    }

    @Generated
    public void setTranslucent(boolean isTranslucent) {
        this.isTranslucent = isTranslucent;
    }

    public static class MultiModels {
        private final Map<String, SubModel> map = new Object2ObjectOpenHashMap();

        public void addSubModel(SubModel model) {
            this.map.put(model.id, model);
        }

        public SubModel getSubModel(String id) {
            return this.map.get(id);
        }

        public Collection<SubModel> getSubModels() {
            return this.map.values();
        }

        public Set<String> getKeys() {
            return this.map.keySet();
        }
    }

    public record Context(Color color) {
        @Generated
        public static ContextBuilder builder() {
            return new ContextBuilder();
        }

        @Generated
        public static class ContextBuilder {
            @Generated
            private Color color;

            @Generated
            ContextBuilder() {
            }

            @Generated
            public ContextBuilder color(Color color) {
                this.color = color;
                return this;
            }

            @Generated
            public Context build() {
                return new Context(this.color);
            }

            @Generated
            public String toString() {
                return "ItemModelData.Context.ContextBuilder(color=" + this.color + ")";
            }
        }
    }

    public record SingleComposite(NamespacedKey model) {
    }

    public static class SubModel {
        private final String id;
        private BaseItemEnum item;
        private int data;

        @Generated
        public SubModel(String id) {
            this.id = id;
        }

        @Generated
        public String getId() {
            return this.id;
        }

        @Generated
        public BaseItemEnum getItem() {
            return this.item;
        }

        @Generated
        public int getData() {
            return this.data;
        }

        @Generated
        public void setItem(BaseItemEnum item) {
            this.item = item;
        }

        @Generated
        public void setData(int data) {
            this.data = data;
        }
    }
}

