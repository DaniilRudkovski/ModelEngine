/*
 * Decompiled with CFR 0.152.
 */
package org.mineskin.data;

public enum Visibility {
    PUBLIC("public"),
    UNLISTED("unlisted"),
    PRIVATE("private");

    private final String name;

    private Visibility(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}

