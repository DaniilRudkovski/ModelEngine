/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 */
package com.ticxo.modelengine.api.error;

import com.ticxo.modelengine.api.error.IError;
import com.ticxo.modelengine.api.utils.logger.LogColor;
import lombok.Generated;

public class ErrorIncompatibleBone
extends IError.Error {
    private final boolean rendering;
    private final String boneName;
    private final String id;

    @Override
    public String getErrorMessage() {
        return String.format("Error: The %s bone behavior type %s is not compatible with %s %s.", this.rendering ? "ghost" : "renderer", LogColor.BLUE + this.id + LogColor.RED, this.rendering ? "renderer bone" : "ghost bone", LogColor.BLUE + this.boneName + LogColor.RED);
    }

    @Generated
    public ErrorIncompatibleBone(boolean rendering, String boneName, String id) {
        this.rendering = rendering;
        this.boneName = boneName;
        this.id = id;
    }
}

