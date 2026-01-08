/*
 * Decompiled with CFR 0.152.
 */
package com.ticxo.modelengine.api.nms.entity.wrapper;

import com.ticxo.modelengine.api.model.ModeledEntity;
import com.ticxo.modelengine.api.nms.entity.wrapper.LookController;
import com.ticxo.modelengine.api.nms.entity.wrapper.MoveController;

public interface MovementOverride {
    public void updateMovement(MoveController var1, ModeledEntity var2);

    public void updateDirection(LookController var1, ModeledEntity var2);
}

