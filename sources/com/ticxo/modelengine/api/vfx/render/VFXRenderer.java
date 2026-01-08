/*
 * Decompiled with CFR 0.152.
 */
package com.ticxo.modelengine.api.vfx.render;

import com.ticxo.modelengine.api.vfx.VFX;

public interface VFXRenderer {
    public VFX getVFX();

    public boolean isInitialized();

    public void initialize();

    public void readVFXData();

    public void sendToClient();

    public void destroy();
}

