/*
 * Decompiled with CFR 0.152.
 */
package com.ticxo.modelengine.api.model.bone.render;

import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.render.ModelRenderer;
import com.ticxo.modelengine.api.nms.RenderParsers;
import com.ticxo.modelengine.api.utils.data.io.DataIO;
import com.ticxo.modelengine.api.utils.data.io.SavedData;

public interface BehaviorRenderer
extends DataIO {
    public ModelRenderer getModelRenderer();

    public void setModelRenderer(ModelRenderer var1);

    public ActiveModel getActiveModel();

    public void initialize();

    public void readBoneData();

    public void sendToClient(RenderParsers var1);

    public void destroy(RenderParsers var1);

    @Override
    default public void load(SavedData data) {
    }

    @Override
    default public void save(SavedData data) {
    }
}

