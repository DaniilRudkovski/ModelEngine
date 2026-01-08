/*
 * Decompiled with CFR 0.152.
 */
package com.ticxo.modelengine.api.generator.skin;

import com.ticxo.modelengine.api.generator.skin.LimbData;
import java.util.Map;

public interface SkinSplitter {
    public void reloadMapping();

    public LimbData getLimb(String var1, boolean var2);

    public Map<String, byte[]> splitSkin(byte[] var1, boolean var2);
}

