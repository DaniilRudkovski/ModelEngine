/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 *  org.bukkit.NamespacedKey
 */
package com.ticxo.modelengine.api.generator.assets;

import com.ticxo.modelengine.api.generator.assets.TintSource;
import java.util.ArrayList;
import java.util.List;
import lombok.Generated;
import org.bukkit.NamespacedKey;

public abstract class ItemModel {
    protected final String type;

    public static Composite composite(ItemModel ... models) {
        Composite composite = new Composite();
        composite.models.addAll(List.of(models));
        return composite;
    }

    @Generated
    public ItemModel(String type) {
        this.type = type;
    }

    public static class Composite
    extends ItemModel {
        protected final List<ItemModel> models = new ArrayList<ItemModel>();

        public Composite() {
            super("minecraft:composite");
        }

        @Generated
        public List<ItemModel> getModels() {
            return this.models;
        }
    }

    public static class RangeDispatch
    extends ItemModel {
        protected final List<Entry> entries = new ArrayList<Entry>();
        protected ItemModel fallback;
        protected final String property;

        public RangeDispatch() {
            super("minecraft:range_dispatch");
            this.property = "minecraft:custom_model_data";
        }

        @Generated
        public List<Entry> getEntries() {
            return this.entries;
        }

        @Generated
        public ItemModel getFallback() {
            return this.fallback;
        }

        @Generated
        public String getProperty() {
            return this.property;
        }

        @Generated
        public void setFallback(ItemModel fallback) {
            this.fallback = fallback;
        }

        public record Entry(ItemModel model, int threshold) {
        }
    }

    public static class Model
    extends ItemModel {
        protected final String model;
        protected final List<TintSource> tints = new ArrayList<TintSource>();

        public Model(NamespacedKey model) {
            super("minecraft:model");
            this.model = model.asString();
            this.tints.add(new TintSource.CustomModelData(0));
        }

        public Model(String model) {
            super("minecraft:model");
            this.model = model;
        }

        @Generated
        public String getModel() {
            return this.model;
        }

        @Generated
        public List<TintSource> getTints() {
            return this.tints;
        }
    }
}

