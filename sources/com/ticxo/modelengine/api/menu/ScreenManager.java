/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  lombok.Generated
 *  org.bukkit.inventory.Inventory
 */
package com.ticxo.modelengine.api.menu;

import com.google.common.collect.Maps;
import com.ticxo.modelengine.api.menu.AbstractScreen;
import java.util.Map;
import lombok.Generated;
import org.bukkit.inventory.Inventory;

public class ScreenManager {
    private final Map<Inventory, AbstractScreen> screens = Maps.newConcurrentMap();

    public void registerScreen(AbstractScreen screen) {
        this.screens.put(screen.inventory, screen);
    }

    public void unregisterScreen(Inventory inventory) {
        this.screens.remove(inventory);
    }

    public void unregisterScreen(AbstractScreen screen) {
        this.screens.remove(screen.inventory);
    }

    public AbstractScreen getScreen(Inventory inventory) {
        return this.screens.get(inventory);
    }

    public boolean isScreen(Inventory inventory) {
        return this.screens.containsKey(inventory);
    }

    public void updateAllScreens() {
        for (AbstractScreen screen : this.screens.values()) {
            screen.onTick();
        }
    }

    @Generated
    public Map<Inventory, AbstractScreen> getScreens() {
        return this.screens;
    }
}

