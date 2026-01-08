/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 */
package com.ticxo.modelengine.core.generator.java;

import com.ticxo.modelengine.api.generator.BaseItemEnum;
import com.ticxo.modelengine.core.generator.ModelIdCache;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.Generated;

public class BaseItem {
    private final List<JavaOverride> overrides = new ArrayList<JavaOverride>();
    private transient String name;
    private transient BaseItemEnum baseItemEnum;
    private String parent;
    private Map<String, String> textures;

    public void clearOverrides() {
        this.overrides.clear();
    }

    public void addModels(String namespace, ModelIdCache cache) {
        cache.sortedIterate((model, id) -> this.overrides.add(new JavaOverride(namespace + ":" + model, (int)id)));
    }

    @Generated
    public List<JavaOverride> getOverrides() {
        return this.overrides;
    }

    @Generated
    public String getName() {
        return this.name;
    }

    @Generated
    public void setName(String name) {
        this.name = name;
    }

    @Generated
    public BaseItemEnum getBaseItemEnum() {
        return this.baseItemEnum;
    }

    @Generated
    public void setBaseItemEnum(BaseItemEnum baseItemEnum) {
        this.baseItemEnum = baseItemEnum;
    }

    static class JavaOverride {
        private final JavaPredicate predicate;
        private final String model;

        public JavaOverride(String model, int id) {
            this.model = model;
            this.predicate = new JavaPredicate(id);
        }

        @Generated
        public JavaPredicate getPredicate() {
            return this.predicate;
        }

        @Generated
        public String getModel() {
            return this.model;
        }
    }

    record JavaPredicate(int custom_model_data) {
    }
}

