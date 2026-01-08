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

import com.ticxo.modelengine.api.events.AbstractEvent;
import com.ticxo.modelengine.api.generator.parser.ModelParser;
import java.util.List;
import lombok.Generated;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class RegisterParserEvent
extends AbstractEvent
implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private final List<ModelParser> parsers;
    private boolean cancelled;

    @NotNull
    public static HandlerList getHandlerList() {
        return handlers;
    }

    @NotNull
    public HandlerList getHandlers() {
        return handlers;
    }

    public void register(ModelParser parser) {
        this.parsers.add(parser);
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
    public RegisterParserEvent(List<ModelParser> parsers) {
        this.parsers = parsers;
    }
}

