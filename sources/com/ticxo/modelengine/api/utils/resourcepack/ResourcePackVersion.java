/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 */
package com.ticxo.modelengine.api.utils.resourcepack;

import com.ticxo.modelengine.api.utils.resourcepack.ResourcePackFeature;
import java.util.Set;
import lombok.Generated;

public enum ResourcePackVersion {
    v1_19_4(13, ResourcePackFeature.COLORABLE_ITEM),
    v1_20(15, ResourcePackFeature.COLORABLE_ITEM),
    v1_20_1(15, ResourcePackFeature.COLORABLE_ITEM),
    v1_20_2(18, ResourcePackFeature.COLORABLE_ITEM),
    v1_20_3(22, ResourcePackFeature.COLORABLE_ITEM),
    v1_20_4(22, ResourcePackFeature.COLORABLE_ITEM),
    v1_20_5(32, ResourcePackFeature.COLORABLE_ITEM),
    v1_20_6(32, ResourcePackFeature.COLORABLE_ITEM),
    v1_21(34, ResourcePackFeature.COLORABLE_ITEM),
    v1_21_1(34, ResourcePackFeature.COLORABLE_ITEM),
    v1_21_2(42, ResourcePackFeature.COLORABLE_ITEM),
    v1_21_3(42, ResourcePackFeature.COLORABLE_ITEM),
    v1_21_4(46, ResourcePackFeature.COLORABLE_ITEM, ResourcePackFeature.DATA_DRIVEN, ResourcePackFeature.COMPOSITE_BYPASS);

    private final int version;
    private final Set<ResourcePackFeature> availableFeatures;

    private ResourcePackVersion(int version, ResourcePackFeature ... features) {
        this.version = version;
        this.availableFeatures = Set.of(features);
    }

    public static ResourcePackVersion getVersion(String value) {
        if (!((String)value).startsWith("1_")) {
            value = "1_" + (String)value;
        }
        if (!((String)value).startsWith("v")) {
            value = "v" + (String)value;
        }
        if (((String)(value = ((String)value).replaceAll("\\.", "_"))).endsWith("_0")) {
            value = ((String)value).substring(0, ((String)value).length() - 2);
        }
        return ResourcePackVersion.get((String)value);
    }

    public static ResourcePackVersion get(String value) {
        try {
            return ResourcePackVersion.valueOf(value);
        }
        catch (IllegalArgumentException e) {
            e.printStackTrace();
            return v1_19_4;
        }
    }

    @Generated
    public int getVersion() {
        return this.version;
    }

    @Generated
    public Set<ResourcePackFeature> getAvailableFeatures() {
        return this.availableFeatures;
    }
}

