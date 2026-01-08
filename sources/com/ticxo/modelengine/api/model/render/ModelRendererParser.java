/*
 * Decompiled with CFR 0.152.
 */
package com.ticxo.modelengine.api.model.render;

import com.ticxo.modelengine.api.model.render.ModelRenderer;

public interface ModelRendererParser<T extends ModelRenderer> {
    public void sendToClients(T var1);

    public void destroy(T var1);
}

