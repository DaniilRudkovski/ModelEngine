/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 *  org.bukkit.entity.Entity
 *  org.bukkit.event.Cancellable
 *  org.bukkit.event.HandlerList
 *  org.jetbrains.annotations.NotNull
 */
package com.ticxo.modelengine.api.events;

import com.ticxo.modelengine.api.events.AbstractEvent;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.bone.type.Mount;
import lombok.Generated;
import org.bukkit.entity.Entity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class ModelMountEvent
extends AbstractEvent
implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private final ActiveModel vehicle;
    private final Entity passenger;
    private final boolean isDriver;
    private final Mount seat;
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
    public ActiveModel getVehicle() {
        return this.vehicle;
    }

    @Generated
    public Entity getPassenger() {
        return this.passenger;
    }

    @Generated
    public boolean isDriver() {
        return this.isDriver;
    }

    @Generated
    public Mount getSeat() {
        return this.seat;
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
    public ModelMountEvent(ActiveModel vehicle, Entity passenger, boolean isDriver, Mount seat) {
        this.vehicle = vehicle;
        this.passenger = passenger;
        this.isDriver = isDriver;
        this.seat = seat;
    }
}

