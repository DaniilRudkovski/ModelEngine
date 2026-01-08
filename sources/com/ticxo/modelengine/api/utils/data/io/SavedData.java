/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.reflect.TypeToken
 *  org.bukkit.NamespacedKey
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.plugin.Plugin
 *  org.jetbrains.annotations.Nullable
 */
package com.ticxo.modelengine.api.utils.data.io;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.utils.data.ItemUtils;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

public class SavedData
extends HashMap<String, Object> {
    public static final NamespacedKey DATA_KEY = new NamespacedKey((Plugin)ModelEngineAPI.getAPI(), "model_data");

    public static SavedData parse(String json) {
        return (SavedData)SavedData.gson().fromJson(json, SavedData.class);
    }

    public void putBoolean(String key, Boolean value) {
        this.put(key, value);
    }

    public Boolean getBoolean(String key, Boolean def) {
        String val = this.getAsString(key);
        if (val == null) {
            return def;
        }
        return Boolean.parseBoolean(val);
    }

    public Boolean getBoolean(String key) {
        return this.getBoolean(key, null);
    }

    public void putByte(String key, Byte value) {
        this.put(key, value);
    }

    public Byte getByte(String key, Byte def) {
        try {
            String val = this.getAsString(key);
            if (val == null) {
                return def;
            }
            return Byte.parseByte(val);
        }
        catch (NumberFormatException e) {
            this.wrapException(key, e);
            return def;
        }
    }

    public Byte getByte(String key) {
        return this.getByte(key, null);
    }

    public void putInt(String key, Integer value) {
        this.put(key, value);
    }

    public Integer getInt(String key, Integer def) {
        try {
            String val = this.getAsString(key);
            if (val == null) {
                return def;
            }
            return (int)Double.parseDouble(val);
        }
        catch (NumberFormatException e) {
            this.wrapException(key, e);
            return def;
        }
    }

    public Integer getInt(String key) {
        return this.getInt(key, null);
    }

    public void putFloat(String key, Float value) {
        if (value.isNaN() || value.isInfinite()) {
            value = Float.valueOf(0.0f);
        }
        this.put(key, value);
    }

    public Float getFloat(String key, Float def) {
        try {
            String val = this.getAsString(key);
            if (val == null) {
                return def;
            }
            return Float.valueOf(Float.parseFloat(val));
        }
        catch (NumberFormatException e) {
            this.wrapException(key, e);
            return def;
        }
    }

    public Float getFloat(String key) {
        return this.getFloat(key, null);
    }

    public void putDouble(String key, Double value) {
        if (value.isNaN() || value.isInfinite()) {
            value = 0.0;
        }
        this.put(key, value);
    }

    public Double getDouble(String key, Double def) {
        try {
            String val = this.getAsString(key);
            if (val == null) {
                return def;
            }
            return Double.parseDouble(val);
        }
        catch (NumberFormatException e) {
            this.wrapException(key, e);
            return def;
        }
    }

    public Double getDouble(String key) {
        return this.getDouble(key, null);
    }

    public void putString(String key, String value) {
        this.put(key, value);
    }

    public String getString(String key, String def) {
        return this.getOrDefaultAsString(key, def);
    }

    public String getString(String key) {
        return this.getString(key, null);
    }

    public void putUUID(String key, UUID value) {
        this.put(key, value);
    }

    public UUID getUUID(String key, UUID def) {
        String value = this.getAsString(key);
        if (value == null) {
            return def;
        }
        return UUID.fromString(value);
    }

    public UUID getUUID(String key) {
        return this.getUUID(key, null);
    }

    public void putList(String key, Collection<?> collection) {
        this.put(key, collection);
    }

    public <T> List<T> getList(String key) {
        String value = this.getAsString(key);
        if (value == null) {
            return List.of();
        }
        return (List)SavedData.gson().fromJson(value, new TypeToken<Object>(){}.getType());
    }

    public <T> List<T> getList(String key, Class<T> clazz) {
        String value = SavedData.gson().toJson(this.get(key));
        if (value == null) {
            return List.of();
        }
        return (List)SavedData.gson().fromJson(value, TypeToken.getParameterized(List.class, (Type[])new Type[]{clazz}));
    }

    public void putData(String key, SavedData value) {
        if (this == value) {
            throw new RuntimeException("Cannot add data: Attempting to add self to self.");
        }
        this.put(key, value);
    }

    public Optional<SavedData> getData(String key) {
        Object value = this.get(key);
        if (value instanceof Map) {
            SavedData data = new SavedData();
            data.putAll((Map)value);
            return Optional.of(data);
        }
        return Optional.empty();
    }

    public void putItemStack(String key, @Nullable ItemStack stack) {
        if (stack == null) {
            return;
        }
        this.putString(key, ItemUtils.encodeItemStackToString(stack));
    }

    public ItemStack getItemStack(String key) {
        return this.getItemStack(key, null);
    }

    public ItemStack getItemStack(String key, ItemStack def) {
        String value = this.getAsString(key);
        if (value == null) {
            return def;
        }
        try {
            return ItemUtils.decodeItemStack(value);
        }
        catch (Throwable throwable) {
            throwable.printStackTrace();
            return def;
        }
    }

    public <T> void saveIfExist(String key, Supplier<T> supplier, DataSaver<T> saveConsumer) {
        T val = supplier.get();
        if (val != null) {
            saveConsumer.save(this, key, val);
        }
    }

    public <T> void loadIfExist(String key, DataLoader<T> getter, Consumer<T> consumer) {
        T val = getter.load(this, key);
        if (val != null) {
            consumer.accept(val);
        }
    }

    @Override
    public String toString() {
        return SavedData.gson().toJson((Object)this);
    }

    private void wrapException(String key, Exception e) {
        throw new RuntimeException("An error occurred while reading the value of " + key, e);
    }

    private String getAsString(String key) {
        return this.getOrDefaultAsString(key, null);
    }

    private String getOrDefaultAsString(String key, String def) {
        Object obj = this.get(key);
        return obj == null ? def : obj.toString();
    }

    private static Gson gson() {
        return ModelEngineAPI.getAPI().getGson();
    }

    @FunctionalInterface
    public static interface DataSaver<S> {
        public void save(SavedData var1, String var2, S var3);
    }

    @FunctionalInterface
    public static interface DataLoader<S> {
        public S load(SavedData var1, String var2);
    }
}

