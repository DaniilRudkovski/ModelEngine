/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 */
package com.ticxo.modelengine.api.error;

import com.ticxo.modelengine.api.error.IError;
import com.ticxo.modelengine.api.utils.data.ResourceLocation;
import com.ticxo.modelengine.api.utils.logger.LogColor;
import lombok.Generated;

public class WarnDuplicateTexture
extends IError.Warn {
    private final ResourceLocation name;
    private final ResourceLocation correct;

    @Override
    public String getErrorMessage() {
        return String.format("Warn: Texture name %s already exists. Using alternative texture name %s.", LogColor.BLUE + this.name.toString() + LogColor.YELLOW, LogColor.BLUE + this.correct.toString() + LogColor.YELLOW);
    }

    @Generated
    public WarnDuplicateTexture(ResourceLocation name, ResourceLocation correct) {
        this.name = name;
        this.correct = correct;
    }
}

