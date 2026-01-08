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

public class ErrorWrongBoneBehaviorDataType
extends IError.Error {
    private final String bone;
    private final BoneBehaviorType<?> boneBehaviorType;
    private final String key;
    private final Class<?> expects;
    private final Class<?> provided;

    @Override
    public String getErrorMessage() {
        return String.format("Error: The bone behavior %s of %s was given the wrong data type for %s. Expected %s, provided %s. Removing behavior.", LogColor.BLUE + this.boneBehaviorType.getId() + LogColor.RED, LogColor.BLUE + this.bone + LogColor.RED, LogColor.BLUE + this.key + LogColor.RED, LogColor.BLUE + this.expects.getSimpleName() + LogColor.RED, LogColor.BLUE + this.provided.getSimpleName() + LogColor.RED);
    }

    @Generated
    public ErrorWrongBoneBehaviorDataType(String bone, BoneBehaviorType<?> boneBehaviorType, String key, Class<?> expects, Class<?> provided) {
        this.bone = bone;
        this.boneBehaviorType = boneBehaviorType;
        this.key = key;
        this.expects = expects;
        this.provided = provided;
    }
}

