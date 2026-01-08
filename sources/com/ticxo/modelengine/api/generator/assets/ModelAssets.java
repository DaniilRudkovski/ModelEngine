/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 */
package com.ticxo.modelengine.api.generator.assets;

import com.ticxo.modelengine.api.generator.assets.BlueprintTexture;
import com.ticxo.modelengine.api.generator.assets.JavaItemModel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Generated;

public class ModelAssets {
    private final List<BlueprintTexture> textures = new ArrayList<BlueprintTexture>();
    private final Map<String, Collection<JavaItemModel>> models = new HashMap<String, Collection<JavaItemModel>>();
    private String name;

    @Generated
    public List<BlueprintTexture> getTextures() {
        return this.textures;
    }

    @Generated
    public Map<String, Collection<JavaItemModel>> getModels() {
        return this.models;
    }

    @Generated
    public String getName() {
        return this.name;
    }

    @Generated
    public void setName(String name) {
        this.name = name;
    }
}

