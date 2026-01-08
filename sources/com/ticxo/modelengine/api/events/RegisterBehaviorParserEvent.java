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
import com.ticxo.modelengine.api.generator.parser.blockbench.BlockbenchBehaviorParser;
import java.util.Set;
import lombok.Generated;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class RegisterBehaviorParserEvent
extends AbstractEvent
implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private final Set<BlockbenchBehaviorParser> behaviorParsers;
    private boolean cancelled;

    @NotNull
    public static HandlerList getHandlerList() {
        return handlers;
    }

    @NotNull
    public HandlerList getHandlers() {
        return handlers;
    }

    public void register(BlockbenchBehaviorParser parser) {
        this.behaviorParsers.add(parser);
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
    public RegisterBehaviorParserEvent(Set<BlockbenchBehaviorParser> behaviorParsers) {
        this.behaviorParsers = behaviorParsers;
    }
}

