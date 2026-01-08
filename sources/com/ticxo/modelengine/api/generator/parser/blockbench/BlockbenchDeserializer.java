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
package com.ticxo.modelengine.api.generator.parser.blockbench;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.ticxo.modelengine.api.generator.parser.blockbench.BlockbenchModel;
import com.ticxo.modelengine.api.generator.parser.blockbench.LegacyDeserializer;
import com.ticxo.modelengine.api.generator.parser.blockbench.v5_0_Deserializer;
import com.ticxo.modelengine.api.utils.math.TMath;
import java.lang.reflect.Type;

public class BlockbenchDeserializer
implements JsonDeserializer<BlockbenchModel> {
    public BlockbenchModel deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject root = jsonElement.getAsJsonObject();
        String version = root.getAsJsonObject("meta").get("format_version").getAsString();
        String[] versionSplit = version.split("\\.");
        int major = TMath.tryParse(versionSplit[0], 0);
        int minor = TMath.tryParse(versionSplit[1], 0);
        return switch (major) {
            case 5 -> v5_0_Deserializer.deserialize(root);
            default -> LegacyDeserializer.deserialize(root);
        };
    }
}

