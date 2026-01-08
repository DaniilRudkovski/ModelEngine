/*
 * Decompiled with CFR 0.152.
 */
package com.ticxo.modelengine.api.error;

import com.ticxo.modelengine.api.error.IError;

public class WarnBadEyeHeight
extends IError.Warn {
    @Override
    public String getErrorMessage() {
        return "Warning: Eye height is below 0. Entity might suffocate.";
    }
}

