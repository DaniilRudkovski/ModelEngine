/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 *  org.bukkit.event.Cancellable
 *  org.bukkit.event.HandlerList
 *  org.jetbrains.annotations.NotNull
 */
package com.ticxo.modelengine.api.events;

import com.ticxo.modelengine.api.animation.property.IAnimationProperty;
import com.ticxo.modelengine.api.events.AbstractEvent;
import com.ticxo.modelengine.api.model.ActiveModel;
import lombok.Generated;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class AnimationPlayEvent
extends AbstractEvent
implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private final ActiveModel model;
    private final IAnimationProperty property;
    private boolean cancelled;

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
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Generated
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Generated
    public AnimationPlayEvent(ActiveModel model, IAnimationProperty property) {
        this.model = model;
        this.property = property;
    }
}

