/*
 * Decompiled with CFR 0.152.
 */
package com.ticxo.modelengine.api.utils;

public enum OffsetMode {
    LOCAL,
    MODEL,
    GLOBAL;


    public static OffsetMode get(String name) {
        try {
            return OffsetMode.valueOf(name);
        }
        catch (IllegalArgumentException ignored) {
            return LOCAL;
        }
    }
}

