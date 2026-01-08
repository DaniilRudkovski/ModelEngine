/*
 * Decompiled with CFR 0.152.
 */
package com.ticxo.modelengine.api.animation;

import com.ticxo.modelengine.api.animation.handler.AnimationHandler;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.utils.data.io.SavedData;
import com.ticxo.modelengine.api.utils.registry.TUnaryRegistry;
import java.util.function.BiFunction;

public class AnimationHandlerRegistry
extends TUnaryRegistry<BiFunction<ActiveModel, SavedData, AnimationHandler>> {
    public AnimationHandler createHandler(ActiveModel model, SavedData data) {
        String id = data.getString("id");
        if (id != null) {
            return (AnimationHandler)((BiFunction)this.get(id)).apply(model, data);
        }
        return null;
    }
}

