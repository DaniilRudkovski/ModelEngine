/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 */
package org.mineskin.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Optional;

public class Grants {
    private final JsonObject raw;

    public Grants(JsonObject raw) {
        this.raw = raw;
    }

    public Optional<JsonElement> getRaw(String key) {
        if (this.raw.has(key) && !this.raw.get(key).isJsonNull()) {
            return Optional.of(this.raw.get(key));
        }
        return Optional.empty();
    }

    public Optional<Boolean> getBoolean(String key) {
        return this.getRaw(key).map(JsonElement::getAsBoolean);
    }

    public Optional<Integer> getInt(String key) {
        return this.getRaw(key).map(JsonElement::getAsInt);
    }

    public Optional<Double> getDouble(String key) {
        return this.getRaw(key).map(JsonElement::getAsDouble);
    }

    public Optional<String> getString(String key) {
        return this.getRaw(key).map(JsonElement::getAsString);
    }

    public Optional<Integer> perMinute() {
        return this.getInt("per_minute");
    }

    public Optional<Integer> concurrency() {
        return this.getInt("concurrency");
    }

    public String toString() {
        return "Grants" + String.valueOf(this.raw);
    }
}

