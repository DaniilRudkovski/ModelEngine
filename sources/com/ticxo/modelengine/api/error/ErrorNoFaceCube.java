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

public class ErrorNoFaceCube
extends IError.Error {
    private final String bone;
    private final String cube;

    @Override
    public String getErrorMessage() {
        return String.format("Error: The cube %s in bone %s has no faces. This might be caused by all faces having UV size of 0. Excluding cube from bone.", LogColor.BLUE + this.cube + LogColor.RED, LogColor.BLUE + this.bone + LogColor.RED);
    }

    @Generated
    public ErrorNoFaceCube(String bone, String cube) {
        this.bone = bone;
        this.cube = cube;
    }
}

