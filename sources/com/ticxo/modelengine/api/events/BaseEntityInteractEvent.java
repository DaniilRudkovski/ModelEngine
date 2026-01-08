/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 *  org.bukkit.entity.Player
 *  org.bukkit.event.HandlerList
 *  org.bukkit.inventory.EquipmentSlot
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.util.Vector
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.ticxo.modelengine.api.events;

import com.ticxo.modelengine.api.entity.BaseEntity;
import com.ticxo.modelengine.api.events.AbstractEvent;
import com.ticxo.modelengine.api.model.ActiveModel;
import lombok.Generated;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BaseEntityInteractEvent
extends AbstractEvent {
    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private final BaseEntity<?> baseEntity;
    private final ActiveModel model;
    private final Action action;
    private final EquipmentSlot slot;
    private final boolean isSecondary;
    private final ItemStack item;
    @Nullable
    private final Vector clickedPosition;

    @NotNull
    public static HandlerList getHandlerList() {
        return handlers;
    }

    @NotNull
    public HandlerList getHandlers() {
        return handlers;
    }

    @Generated
    public BaseEntityInteractEvent(Player player, BaseEntity<?> baseEntity, ActiveModel model, Action action, EquipmentSlot slot, boolean isSecondary, ItemStack item, @Nullable Vector clickedPosition) {
        this.player = player;
        this.baseEntity = baseEntity;
        this.model = model;
        this.action = action;
        this.slot = slot;
        this.isSecondary = isSecondary;
        this.item = item;
        this.clickedPosition = clickedPosition;
    }

    @Generated
    public String toString() {
        return "BaseEntityInteractEvent(player=" + this.getPlayer() + ", baseEntity=" + this.getBaseEntity() + ", model=" + this.getModel() + ", action=" + this.getAction() + ", slot=" + this.getSlot() + ", isSecondary=" + this.isSecondary() + ", item=" + this.getItem() + ", clickedPosition=" + this.getClickedPosition() + ")";
    }

    @Generated
    public Player getPlayer() {
        return this.player;
    }

    @Generated
    public BaseEntity<?> getBaseEntity() {
        return this.baseEntity;
    }

    @Generated
    public ActiveModel getModel() {
        return this.model;
    }

    @Generated
    public Action getAction() {
        return this.action;
    }

    @Generated
    public EquipmentSlot getSlot() {
        return this.slot;
    }

    @Generated
    public boolean isSecondary() {
        return this.isSecondary;
    }

    @Generated
    public ItemStack getItem() {
        return this.item;
    }

    @Nullable
    @Generated
    public Vector getClickedPosition() {
        return this.clickedPosition;
    }

    public static enum Action {
        ATTACK,
        INTERACT,
        INTERACT_ON;

    }
}

