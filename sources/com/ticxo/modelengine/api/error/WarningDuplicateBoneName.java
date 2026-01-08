/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 */
package com.ticxo.modelengine.api.error;

import com.ticxo.modelengine.api.error.IError;
import com.ticxo.modelengine.api.utils.logger.LogColor;
import java.util.UUID;
import lombok.Generated;

public class WarningDuplicateBoneName
extends IError.Warn {
    private final String bone;
    private final UUID uuid;

    @Override
    public String getErrorMessage() {
        return String.format("Warning: Model contains duplicate bone names %s. Naming bone with UUID %s", LogColor.BLUE + this.bone + LogColor.YELLOW, LogColor.BLUE + this.uuid.toString() + LogColor.YELLOW);
    }

    @Generated
    public WarningDuplicateBoneName(String bone, UUID uuid) {
        this.bone = bone;
        this.uuid = uuid;
    }
}

