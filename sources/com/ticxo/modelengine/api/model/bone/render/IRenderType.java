/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package com.ticxo.modelengine.api.model.bone.render;

import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.bone.render.BehaviorRenderer;
import org.jetbrains.annotations.Nullable;

public interface IRenderType {
    @Nullable
    public BehaviorRenderer createBehaviorRenderer(ActiveModel var1);
}

