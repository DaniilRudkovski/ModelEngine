/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 */
package com.ticxo.modelengine.api.error;

import com.ticxo.modelengine.api.error.IError;
import com.ticxo.modelengine.api.model.bone.behavior.BoneBehaviorType;
import com.ticxo.modelengine.api.utils.logger.LogColor;
import lombok.Generated;

public class ErrorMissingBoneBehaviorData
extends IError.Error {
    private final String bone;
    private final BoneBehaviorType<?> boneBehaviorType;
    private final String key;

    @Override
    public String getErrorMessage() {
        return String.format("Error: The bone behavior %s of %s is missing required data %s. Removing behavior.", LogColor.BLUE + this.boneBehaviorType.getId() + LogColor.RED, LogColor.BLUE + this.bone + LogColor.RED, LogColor.BLUE + this.key + LogColor.RED);
    }

    @Generated
    public ErrorMissingBoneBehaviorData(String bone, BoneBehaviorType<?> boneBehaviorType, String key) {
        this.bone = bone;
        this.boneBehaviorType = boneBehaviorType;
        this.key = key;
    }
}

