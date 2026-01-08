/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 */
package com.ticxo.modelengine.api.generator.assets;

import com.ticxo.modelengine.api.utils.math.TMath;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Predicate;
import lombok.Generated;

public enum JavaDisplay {
    THIRDPERSON_RIGHTHAND,
    THIRDPERSON_LEFTHAND,
    FIRSTPERSON_RIGHTHAND,
    FIRSTPERSON_LEFTHAND,
    GROUND,
    GUI,
    HEAD,
    FIXED;


    public String toString() {
        return super.toString().toLowerCase(Locale.ENGLISH);
    }

    public static enum Transform {
        ROTATION(vals -> {
            vals[0] = vals[0] % 360.0f;
            vals[1] = vals[1] % 360.0f;
            vals[2] = vals[2] % 360.0f;
        }, vals -> TMath.isSimilar(vals[0], 0.0f) && TMath.isSimilar(vals[1], 0.0f) && TMath.isSimilar(vals[2], 0.0f)),
        TRANSLATION(vals -> {
            vals[0] = TMath.clamp(vals[0], -80.0f, 80.0f);
            vals[1] = TMath.clamp(vals[1], -80.0f, 80.0f);
            vals[2] = TMath.clamp(vals[2], -80.0f, 80.0f);
        }, vals -> TMath.isSimilar(vals[0], 0.0f) && TMath.isSimilar(vals[1], 0.0f) && TMath.isSimilar(vals[2], 0.0f)),
        SCALE(vals -> {
            vals[0] = TMath.clamp(vals[0], 0.0f, 4.0f);
            vals[1] = TMath.clamp(vals[1], 0.0f, 4.0f);
            vals[2] = TMath.clamp(vals[2], 0.0f, 4.0f);
        }, vals -> TMath.isSimilar(vals[0], 1.0f) && TMath.isSimilar(vals[1], 1.0f) && TMath.isSimilar(vals[2], 1.0f));

        private final Consumer<float[]> sanitizer;
        private final Predicate<float[]> isDirty;

        public void sanitize(float[] val) {
            this.sanitizer.accept(val);
        }

        public boolean isDefault(float[] val) {
            return this.isDirty.test(val);
        }

        public String toString() {
            return super.toString().toLowerCase(Locale.ENGLISH);
        }

        @Generated
        private Transform(Consumer<float[]> sanitizer, Predicate<float[]> isDirty) {
            this.sanitizer = sanitizer;
            this.isDirty = isDirty;
        }
    }
}

