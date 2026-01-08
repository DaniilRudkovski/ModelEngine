/*
 * Decompiled with CFR 0.152.
 */
package com.ticxo.modelengine.api.vfx.render;

import com.ticxo.modelengine.api.vfx.render.VFXRenderer;

public interface VFXRendererParser<T extends VFXRenderer> {
    public void sendToClients(T var1);

    public void destroy(T var1);
}

