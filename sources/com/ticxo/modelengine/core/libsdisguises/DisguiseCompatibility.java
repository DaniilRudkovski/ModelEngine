/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  me.libraryaddict.disguise.events.DisguiseEvent
 *  me.libraryaddict.disguise.events.UndisguiseEvent
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 */
package com.ticxo.modelengine.core.libsdisguises;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import me.libraryaddict.disguise.events.DisguiseEvent;
import me.libraryaddict.disguise.events.UndisguiseEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class DisguiseCompatibility
implements Listener {
    private final Map<UUID, UUID> disguiseToEntity = new HashMap<UUID, UUID>();

    @EventHandler(priority=EventPriority.MONITOR)
    public void onDisguise(DisguiseEvent event) {
        this.disguiseToEntity.put(event.getDisguise().getUUID(), event.getDisguised().getUniqueId());
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onUndisguise(UndisguiseEvent event) {
        this.disguiseToEntity.remove(event.getDisguise().getUUID());
    }

    public UUID getRelayOrDefault(UUID uuid) {
        return this.disguiseToEntity.getOrDefault(uuid, uuid);
    }
}

