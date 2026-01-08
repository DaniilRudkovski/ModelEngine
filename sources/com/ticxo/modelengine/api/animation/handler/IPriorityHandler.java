/*
 * Decompiled with CFR 0.152.
 */
package com.ticxo.modelengine.api.animation.handler;

import com.ticxo.modelengine.api.animation.ModelState;
import com.ticxo.modelengine.api.animation.handler.AnimationHandler;
import com.ticxo.modelengine.api.animation.property.IAnimationProperty;
import java.util.function.BiConsumer;

public interface IPriorityHandler
extends AnimationHandler {
    public void forEachProperty(BiConsumer<String, IAnimationProperty> var1);

    public void playState(ModelState var1);

    @Override
    default public String getId() {
        return "priority";
    }
}

