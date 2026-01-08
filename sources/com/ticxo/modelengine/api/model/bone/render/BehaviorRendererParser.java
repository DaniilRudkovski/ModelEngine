/*
 * Decompiled with CFR 0.152.
 */
package com.ticxo.modelengine.api.model.bone.render;

import com.ticxo.modelengine.api.model.bone.render.BehaviorRenderer;

public interface BehaviorRendererParser<T extends BehaviorRenderer> {
    public void sendToClients(T var1);

    public void destroy(T var1);
}

