/*
 * Decompiled with CFR 0.152.
 */
package com.ticxo.modelengine.api.entity;

import java.util.Locale;

public enum CullType {
    NO_CULL,
    MOVEMENT_ONLY,
    CULLED;


    public static CullType get(String value) {
        try {
            return CullType.valueOf(value.toUpperCase(Locale.ENGLISH));
        }
        catch (IllegalArgumentException ignored) {
            return NO_CULL;
        }
    }
}

