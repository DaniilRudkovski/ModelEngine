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
import com.ticxo.modelengine.api.generator.ModelGenerator;
import lombok.Generated;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class ModelRegistrationEvent
extends AbstractEvent {
    private static final HandlerList handlers = new HandlerList();
    private final ModelGenerator.Phase phase;

    @NotNull
    public static HandlerList getHandlerList() {
        return handlers;
    }

    @NotNull
    public HandlerList getHandlers() {
        return handlers;
    }

    @Generated
    public ModelGenerator.Phase getPhase() {
        return this.phase;
    }

    @Generated
    public ModelRegistrationEvent(ModelGenerator.Phase phase) {
        this.phase = phase;
    }
}

