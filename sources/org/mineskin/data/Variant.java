/*
 * Decompiled with CFR 0.152.
 */
package org.mineskin.data;

public enum Variant {
    AUTO(""),
    CLASSIC("classic"),
    SLIM("slim");

    private final String name;

    private Variant(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}

