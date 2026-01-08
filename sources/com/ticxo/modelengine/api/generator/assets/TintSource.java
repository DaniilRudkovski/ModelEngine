/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.annotations.SerializedName
 *  lombok.Generated
 */
package com.ticxo.modelengine.api.generator.assets;

import com.google.gson.annotations.SerializedName;
import lombok.Generated;

public abstract class TintSource {
    protected final String type;

    @Generated
    public TintSource(String type) {
        this.type = type;
    }

    public static class CustomModelData
    extends TintSource {
        protected final int index;
        @SerializedName(value="default")
        protected final int def = 0xFFFFFF;

        public CustomModelData(int index) {
            super("minecraft:custom_model_data");
            this.index = index;
        }
    }

    public static class Potion
    extends TintSource {
        @SerializedName(value="default")
        protected final int def;

        public Potion() {
            this(-13083194);
        }

        public Potion(int def) {
            super("minecraft:potion");
            this.def = def;
        }
    }

    public static class MapColor
    extends TintSource {
        @SerializedName(value="default")
        protected final int def;

        public MapColor() {
            this(4603950);
        }

        public MapColor(int def) {
            super("minecraft:map_color");
            this.def = def;
        }
    }

    public static class Dye
    extends TintSource {
        @SerializedName(value="default")
        protected final int def;

        public Dye() {
            this(-6265536);
        }

        public Dye(int def) {
            super("minecraft:dye");
            this.def = def;
        }
    }

    public static class Constant
    extends TintSource {
        protected final int value;

        public Constant(int value) {
            super("minecraft:constant");
            this.value = value;
        }
    }
}

