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

public class ErrorUnknownBoneBehavior
extends IError.Error {
    private final String boneName;
    private final String id;

    @Override
    public String getErrorMessage() {
        return String.format("Error: Unknown bone behavior %s on bone %s.", LogColor.BLUE + this.id + LogColor.RED, LogColor.BLUE + this.boneName + LogColor.RED);
    }

    @Generated
    public ErrorUnknownBoneBehavior(String boneName, String id) {
        this.boneName = boneName;
        this.id = id;
    }
}

