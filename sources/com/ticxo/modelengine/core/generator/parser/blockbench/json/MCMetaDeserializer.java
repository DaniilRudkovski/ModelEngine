/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonDeserializer
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 */
package com.ticxo.modelengine.core.generator.parser.blockbench.json;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.ticxo.modelengine.api.generator.assets.BlueprintTexture;
import com.ticxo.modelengine.api.utils.data.GSONUtils;
import java.lang.reflect.Type;

public class MCMetaDeserializer
implements JsonDeserializer<BlueprintTexture.MCMeta> {
    public BlueprintTexture.MCMeta deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject animation = GSONUtils.get(jsonElement, "animation", JsonElement::getAsJsonObject);
        if (animation == null) {
            return null;
        }
        BlueprintTexture.MCMeta meta = new BlueprintTexture.MCMeta();
        meta.setInterpolate(GSONUtils.get((JsonElement)animation, "interpolate", JsonElement::getAsBoolean));
        meta.setWidth(GSONUtils.get((JsonElement)animation, "width", JsonElement::getAsInt));
        meta.setHeight(GSONUtils.get((JsonElement)animation, "height", JsonElement::getAsInt));
        meta.setFrametime(GSONUtils.get((JsonElement)animation, "frametime", JsonElement::getAsInt));
        GSONUtils.ifArray((JsonElement)animation, "frames", element -> {
            if (element.isJsonPrimitive()) {
                meta.addFrame(element.getAsInt());
            } else if (element.isJsonObject()) {
                JsonObject frameObject = element.getAsJsonObject();
                Integer index = GSONUtils.get((JsonElement)frameObject, "index", JsonElement::getAsInt);
                Integer time = GSONUtils.get((JsonElement)frameObject, "time", JsonElement::getAsInt);
                if (index != null && time != null) {
                    meta.addFrame(index, time);
                }
            }
        });
        return meta;
    }
}

