/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 *  org.bukkit.event.HandlerList
 *  org.jetbrains.annotations.NotNull
 */
package com.ticxo.modelengine.api.events;

import com.ticxo.modelengine.api.animation.property.IAnimationProperty;
import com.ticxo.modelengine.api.events.AbstractEvent;
import com.ticxo.modelengine.api.model.ActiveModel;
import lombok.Generated;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class AnimationEndEvent
extends AbstractEvent {
    private static final HandlerList handlers = new HandlerList();
    private final ActiveModel model;
    private final IAnimationProperty property;

    @NotNull
    public static HandlerList getHandlerList() {
        return handlers;
    }

    @NotNull
    public HandlerList getHandlers() {
        return handlers;
    }

    @Generated
    public ActiveModel getModel() {
        return this.model;
    }

    @Generated
    public IAnimationProperty getProperty() {
        return this.property;
    }

    @Generated
    public AnimationEndEvent(ActiveModel model, IAnimationProperty property) {
        this.model = model;
        this.property = property;
    }
}

