/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.event.Event
 */
package com.ticxo.modelengine.api.events;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;

public abstract class AbstractEvent
extends Event {
    public AbstractEvent() {
        super(!Bukkit.isPrimaryThread());
    }
}

