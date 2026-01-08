/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  org.bukkit.NamespacedKey
 *  org.jetbrains.annotations.Nullable
 */
package com.ticxo.modelengine.api.utils.meta;

import com.google.common.collect.Maps;
import java.util.Map;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.Nullable;

public class MetadataRegistry {
    private final Map<NamespacedKey, Object> metadata = Maps.newConcurrentMap();

    @Nullable
    public <T> T get(NamespacedKey key) {
        return (T)this.metadata.get(key);
    }

    @Nullable
    public <T> T get(String key) {
        return this.get(NamespacedKey.fromString((String)key));
    }

    public Object put(NamespacedKey key, Object data) {
        return this.metadata.put(key, data);
    }

    public Object put(String key, Object data) {
        return this.put(NamespacedKey.fromString((String)key), data);
    }

    public boolean has(NamespacedKey key) {
        return this.metadata.containsKey(key);
    }

    public boolean has(String key) {
        return this.has(NamespacedKey.fromString((String)key));
    }
}

