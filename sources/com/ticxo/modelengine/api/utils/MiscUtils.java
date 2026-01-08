/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.Pair
 *  org.bukkit.Color
 *  org.bukkit.Location
 *  org.bukkit.Particle
 *  org.bukkit.Particle$DustOptions
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.ticxo.modelengine.api.utils;

import it.unimi.dsi.fastutil.Pair;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.UUID;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MiscUtils {
    public static final DecimalFormat FORMATTER = new DecimalFormat(){
        {
            this.setMaximumFractionDigits(1);
            this.setMinimumFractionDigits(1);
        }
    };

    @SafeVarargs
    @NotNull
    public static <T> T or(T ... values) {
        for (T v : values) {
            if (v == null) continue;
            return v;
        }
        throw new RuntimeException("All values are null");
    }

    @SafeVarargs
    @NotNull
    public static <T> T orDef(@NotNull T def, T ... values) {
        for (T v : values) {
            if (v == null) continue;
            return v;
        }
        return def;
    }

    public static boolean isJava21OrHigher() {
        try {
            int version = Runtime.version().feature();
            return version >= 21;
        }
        catch (Exception e) {
            int version = Integer.parseInt(System.getProperty("java.version"));
            return version >= 21;
        }
    }

    public static UUID generateUUIDFromString(String input) {
        return UUID.nameUUIDFromBytes(input.getBytes(StandardCharsets.UTF_8));
    }

    public static Pair<String, String> getAnimationRef(String animation) {
        String[] split;
        if (animation.startsWith("ref[") && animation.endsWith("]") && (split = (animation = animation.substring(4, animation.length() - 1)).split("\\.", 2)).length >= 2) {
            return Pair.of((Object)split[0], (Object)split[1]);
        }
        return Pair.of(null, (Object)animation);
    }

    public static String createAnimationRef(String model, String animation) {
        return model == null ? animation : "ref[" + model + "." + animation + "]";
    }

    public static <T> T handle(T val, @Nullable Throwable throwable) {
        if (throwable != null) {
            throwable.printStackTrace();
        }
        return null;
    }

    public static void pfx(Location location, Color color, float size) {
        location.getWorld().spawnParticle(Particle.REDSTONE, location, 1, (Object)new Particle.DustOptions(color, size));
    }
}

