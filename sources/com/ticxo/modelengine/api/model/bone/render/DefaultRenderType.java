/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package com.ticxo.modelengine.api.model.bone.render;

import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.bone.render.BehaviorRenderer;
import com.ticxo.modelengine.api.model.bone.render.IRenderType;
import org.jetbrains.annotations.Nullable;

public enum DefaultRenderType implements IRenderType
{
    ANY,
    NONE,
    MODEL;


    @Override
    @Nullable
    public BehaviorRenderer createBehaviorRenderer(ActiveModel activeModel) {
        return null;
    }
}

