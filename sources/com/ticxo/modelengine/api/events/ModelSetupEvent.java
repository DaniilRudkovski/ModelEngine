/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 *  org.bukkit.event.HandlerList
 *  org.jetbrains.annotations.NotNull
 */
package com.ticxo.modelengine.api.events;

import com.ticxo.modelengine.api.events.AbstractEvent;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.ModeledEntity;
import lombok.Generated;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class ModelSetupEvent
extends AbstractEvent {
    private static final HandlerList handlers = new HandlerList();
    private final ModeledEntity modeledEntity;
    private final ActiveModel activeModel;

    @NotNull
    public static HandlerList getHandlerList() {
        return handlers;
    }

    @NotNull
    public HandlerList getHandlers() {
        return handlers;
    }

    @Generated
    public ModeledEntity getModeledEntity() {
        return this.modeledEntity;
    }

    @Generated
    public ActiveModel getActiveModel() {
        return this.activeModel;
    }

    @Generated
    public ModelSetupEvent(ModeledEntity modeledEntity, ActiveModel activeModel) {
        this.modeledEntity = modeledEntity;
        this.activeModel = activeModel;
    }
}

