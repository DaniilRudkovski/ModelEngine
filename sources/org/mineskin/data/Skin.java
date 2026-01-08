/*
 * Decompiled with CFR 0.152.
 */
package org.mineskin.data;

import org.mineskin.data.TextureInfo;
import org.mineskin.data.Variant;
import org.mineskin.data.Visibility;

public interface Skin {
    public String uuid();

    public String name();

    public Variant variant();

    public Visibility visibility();

    public TextureInfo texture();

    public int views();

    public boolean duplicate();
}

