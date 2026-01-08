/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.ticxo.modelengine.api.utils.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GSONUtils {
    public static void ifPresent(JsonElement element, String key, Consumer<JsonElement> consumer) {
        if (!(element instanceof JsonObject)) {
            return;
        }
        JsonObject object = (JsonObject)element;
        if (!object.has(key)) {
            return;
        }
        consumer.accept(object.get(key));
    }

    public static void ifArray(JsonElement element, String key, Consumer<JsonElement> forEach) {
        if (!(element instanceof JsonObject)) {
            return;
        }
        JsonObject object = (JsonObject)element;
        if (!object.has(key)) {
            return;
        }
        JsonElement value = object.get(key);
        if (value instanceof JsonArray) {
            JsonArray array = (JsonArray)value;
            array.forEach(forEach);
        }
    }

    public static void ifArray(JsonElement element, String key, BiConsumer<Integer, JsonElement> forEach) {
        if (!(element instanceof JsonObject)) {
            return;
        }
        JsonObject object = (JsonObject)element;
        if (!object.has(key)) {
            return;
        }
        JsonElement value = object.get(key);
        if (value instanceof JsonArray) {
            JsonArray array = (JsonArray)value;
            for (int i = 0; i < array.size(); ++i) {
                forEach.accept(i, array.get(i));
            }
        }
    }

    @Nullable
    public static <T> T get(JsonElement element, String key, Function<JsonElement, T> func) {
        if (!(element instanceof JsonObject)) {
            return null;
        }
        JsonObject object = (JsonObject)element;
        if (!object.has(key)) {
            return null;
        }
        JsonElement value = object.get(key);
        if (value.isJsonNull()) {
            return null;
        }
        try {
            return func.apply(value);
        }
        catch (Exception e) {
            return null;
        }
    }

    @NotNull
    public static <T> T get(JsonElement element, String key, Function<JsonElement, T> func, @NotNull T def) {
        T val = GSONUtils.get(element, key, func);
        return val == null ? def : val;
    }

    @NotNull
    public static <T> T get(JsonElement element, Function<JsonElement, T> func, @NotNull T def) {
        try {
            return func.apply(element);
        }
        catch (Exception ignored) {
            return def;
        }
    }

    public static Float[] getAsFloatArray(JsonElement jsonElement) {
        if (!jsonElement.isJsonArray()) {
            return null;
        }
        JsonArray array = jsonElement.getAsJsonArray();
        return (Float[])array.asList().stream().map(JsonElement::getAsFloat).toArray(Float[]::new);
    }

    public static UUID getAsUUID(JsonElement jsonElement) {
        return UUID.fromString(jsonElement.getAsString());
    }
}

