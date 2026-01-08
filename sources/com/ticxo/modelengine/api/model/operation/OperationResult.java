/*
 * Decompiled with CFR 0.152.
 */
package com.ticxo.modelengine.api.model.operation;

import com.ticxo.modelengine.api.entity.BaseEntity;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.ModeledEntity;

public record OperationResult(BaseEntity<?> baseEntity, ModeledEntity modeledEntity, ActiveModel model, Throwable exception) {
    public boolean isSuccessful() {
        return this.exception == null;
    }
}

