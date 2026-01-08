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

public class WarnBadAngle
extends IError.Warn {
    private final String bone;
    private final String cube;
    private final double angle;

    @Override
    public String getErrorMessage() {
        return String.format("Warning: The cube %s in bone %s has illegal rotations. Cube rotation can only be -45, -22.5, 0, 22.5 and 45. [ %s ]", LogColor.BLUE + this.cube + LogColor.YELLOW, LogColor.BLUE + this.bone + LogColor.YELLOW, this.angle);
    }

    @Generated
    public WarnBadAngle(String bone, String cube, double angle) {
        this.bone = bone;
        this.cube = cube;
        this.angle = angle;
    }
}

