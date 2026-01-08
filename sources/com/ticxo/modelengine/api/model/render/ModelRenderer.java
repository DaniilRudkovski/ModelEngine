/*
 * Decompiled with CFR 0.152.
 */
package com.ticxo.modelengine.api.model.render;

import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.PivotOverride;
import com.ticxo.modelengine.api.nms.RenderParsers;

public interface ModelRenderer {
    public ActiveModel getActiveModel();

    public void updatePivotOverride(PivotOverride var1);

    public boolean isInitialized();

    public void initialize();

    public void readModelData();

    public void sendToClient(RenderParsers var1);

    public void destroy(RenderParsers var1);

    public void createRealEntities();

    public boolean pollFirstSpawn();
}

