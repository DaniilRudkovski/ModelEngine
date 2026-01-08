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

public class WarnMultipleAngle
extends IError.Warn {
    private final String bone;
    private final String cube;

    @Override
    public String getErrorMessage() {
        return String.format("Warning: The cube %s in bone %s is rotated in multiple axis. Choosing one axis.", LogColor.BLUE + this.cube + LogColor.YELLOW, LogColor.BLUE + this.bone + LogColor.YELLOW);
    }

    @Generated
    public WarnMultipleAngle(String bone, String cube) {
        this.bone = bone;
        this.cube = cube;
    }
}

