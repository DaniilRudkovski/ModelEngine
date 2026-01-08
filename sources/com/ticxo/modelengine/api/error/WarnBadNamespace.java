/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 */
package com.ticxo.modelengine.api.error;

import com.ticxo.modelengine.api.error.IError;
import lombok.Generated;

public class WarnBadNamespace
extends IError.Warn {
    private final String namespace;

    @Override
    public String getErrorMessage() {
        return "Warning: Namespace contains characters other than a-z_0-9. Using default namespace.[" + this.namespace + "]";
    }

    @Generated
    public WarnBadNamespace(String namespace) {
        this.namespace = namespace;
    }
}

