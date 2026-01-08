/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  org.bukkit.Location
 *  org.jetbrains.annotations.Nullable
 */
package com.ticxo.modelengine.core.data;

import com.google.gson.Gson;
import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.utils.data.io.SavedData;
import com.ticxo.modelengine.core.data.V3Data;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;

public class DataUpdater {
    private static final Map<String, Consumer<SavedData>> VERSION_CHAIN = new HashMap<String, Consumer<SavedData>>(){
        {
            this.put("R4.0.0", data -> {
                data.putString("version", "R4.0.3");
                List<SavedData> list = data.getList("models", SavedData.class);
                for (SavedData modelData : list) {
                    modelData.putBoolean("hitbox_visible", true);
                    modelData.putBoolean("shadow_visible", true);
                    modelData.putBoolean("main_hitbox", true);
                }
                data.putList("models", list);
            });
        }
    };

    @Nullable
    public static SavedData convertToSavedData(@Nullable Location location, String json) {
        if (json == null || json.isEmpty() || json.isBlank()) {
            return null;
        }
        Gson gson = ModelEngineAPI.getAPI().getGson();
        SavedData data = (SavedData)gson.fromJson(json, SavedData.class);
        if (data == null) {
            return null;
        }
        if (data.getString("version") == null) {
            V3Data oldData = (V3Data)gson.fromJson(json, V3Data.class);
            data = oldData.convert(location);
        }
        return data;
    }

    public static boolean tryUpdate(@Nullable SavedData data) {
        if (data == null) {
            return false;
        }
        if (data.getString("version") == null) {
            return false;
        }
        Consumer<SavedData> updater;
        while ((updater = VERSION_CHAIN.get(data.getString("version"))) != null) {
            updater.accept(data);
        }
        return true;
    }
}

