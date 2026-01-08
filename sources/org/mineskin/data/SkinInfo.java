/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package org.mineskin.data;

import javax.annotation.Nullable;
import org.mineskin.data.GeneratorInfo;
import org.mineskin.data.MutableBreadcrumbed;
import org.mineskin.data.Skin;
import org.mineskin.data.TextureInfo;
import org.mineskin.data.Variant;
import org.mineskin.data.Visibility;

public class SkinInfo
implements Skin,
MutableBreadcrumbed {
    private final String uuid;
    private final String name;
    private final Variant variant;
    private final Visibility visibility;
    private final TextureInfo texture;
    private final GeneratorInfo generator;
    private final int views;
    private final boolean duplicate;
    private String breadcrumb;

    public SkinInfo(String uuid, String name, Variant variant, Visibility visibility, TextureInfo texture, GeneratorInfo generator, int views, boolean duplicate) {
        this.uuid = uuid;
        this.name = name;
        this.variant = variant;
        this.visibility = visibility;
        this.texture = texture;
        this.generator = generator;
        this.views = views;
        this.duplicate = duplicate;
    }

    @Override
    public String uuid() {
        return this.uuid;
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public Visibility visibility() {
        return this.visibility;
    }

    @Override
    public Variant variant() {
        return this.variant;
    }

    @Override
    public TextureInfo texture() {
        return this.texture;
    }

    public GeneratorInfo generator() {
        return this.generator;
    }

    @Override
    public int views() {
        return this.views;
    }

    @Override
    public boolean duplicate() {
        return this.duplicate;
    }

    @Override
    @Nullable
    public String getBreadcrumb() {
        return this.breadcrumb;
    }

    @Override
    public void setBreadcrumb(String breadcrumb) {
        this.breadcrumb = breadcrumb;
    }

    public String toString() {
        return "SkinInfo{uuid='" + this.uuid + "', name='" + this.name + "', variant=" + String.valueOf((Object)this.variant) + ", visibility=" + String.valueOf((Object)this.visibility) + ", texture=" + String.valueOf(this.texture) + ", generator=" + String.valueOf(this.generator) + ", views=" + this.views + ", duplicate=" + this.duplicate + ", breadcrumb='" + this.breadcrumb + "'}";
    }
}

