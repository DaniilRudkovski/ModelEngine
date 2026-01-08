/*
 * Decompiled with CFR 0.152.
 */
package com.ticxo.modelengine.api.error;

import com.ticxo.modelengine.api.error.IError;

public class WarnNoHitbox
extends IError.Warn {
    @Override
    public String getErrorMessage() {
        return "Warning: Missing hitbox.";
    }
}

