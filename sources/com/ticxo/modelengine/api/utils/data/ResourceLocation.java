/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 */
package com.ticxo.modelengine.api.utils.data;

import java.util.Locale;
import lombok.Generated;

public class ResourceLocation {
    private final String namespace;
    private final String path;

    public ResourceLocation(String full) {
        full = full.toLowerCase(Locale.ENGLISH);
        String[] split = full.split(":", 2);
        if (split.length <= 1) {
            this.namespace = "minecraft";
            this.path = full;
        } else {
            this.namespace = split[0];
            this.path = split[1];
        }
    }

    public String toString() {
        return this.namespace.isBlank() || "minecraft".equals(this.namespace) ? this.path : this.namespace + ":" + this.path;
    }

    public boolean isValid() {
        return ResourceLocation.isValidNamespace(this.namespace) && ResourceLocation.isValidPath(this.path);
    }

    public static boolean isValidPath(String path) {
        for (int i = 0; i < path.length(); ++i) {
            if (ResourceLocation.validPathChar(path.charAt(i))) continue;
            return false;
        }
        return true;
    }

    public static boolean isValidNamespace(String namespace) {
        for (int i = 0; i < namespace.length(); ++i) {
            if (ResourceLocation.validNamespaceChar(namespace.charAt(i))) continue;
            return false;
        }
        return true;
    }

    private static boolean validPathChar(char character) {
        return character == '_' || character == '-' || character >= 'a' && character <= 'z' || character >= '0' && character <= '9' || character == '/' || character == '.';
    }

    private static boolean validNamespaceChar(char character) {
        return character == '_' || character == '-' || character >= 'a' && character <= 'z' || character >= '0' && character <= '9' || character == '.';
    }

    @Generated
    public String getNamespace() {
        return this.namespace;
    }

    @Generated
    public String getPath() {
        return this.path;
    }

    @Generated
    public ResourceLocation(String namespace, String path) {
        this.namespace = namespace;
        this.path = path;
    }
}

