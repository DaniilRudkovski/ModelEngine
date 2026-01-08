/*
 * Decompiled with CFR 0.152.
 */
package com.ticxo.modelengine.api.error;

import com.ticxo.modelengine.api.error.IError;

public class ErrorUnknownFormat
extends IError.Error {
    @Override
    public String getErrorMessage() {
        return "Error: Unknown format.";
    }
}

