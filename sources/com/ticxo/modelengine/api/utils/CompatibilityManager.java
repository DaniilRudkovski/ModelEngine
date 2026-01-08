/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  org.bukkit.Bukkit
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.server.PluginEnableEvent
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.PluginManager
 *  org.bukkit.plugin.java.JavaPlugin
 */
package com.ticxo.modelengine.api.utils;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.ticxo.modelengine.api.utils.logger.TLogger;
import java.util.Map;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class CompatibilityManager
implements Listener {
    private final PluginManager pluginManager;
    private final Set<String> allPlugins = Sets.newConcurrentHashSet();
    private final Map<String, CompatibilityConfiguration> compatibilities = Maps.newConcurrentMap();

    public CompatibilityManager(JavaPlugin plugin) {
        this.pluginManager = Bukkit.getPluginManager();
        for (Plugin pluginManagerPlugin : this.pluginManager.getPlugins()) {
            this.allPlugins.add(pluginManagerPlugin.getName());
        }
        this.pluginManager.registerEvents((Listener)this, (Plugin)plugin);
    }

    public void registerSupport(String pluginName, CompatibilityConfiguration configuration) {
        if (!this.allPlugins.contains(pluginName)) {
            return;
        }
        Plugin plugin = this.pluginManager.getPlugin(pluginName);
        if (plugin == null || !plugin.isEnabled() || !configuration.tryApply(plugin)) {
            this.compatibilities.put(pluginName, configuration);
        } else {
            TLogger.log("Compatibility applied: " + plugin.getName());
        }
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onPluginLoad(PluginEnableEvent event) {
        Plugin plugin = event.getPlugin();
        if (!plugin.isEnabled()) {
            return;
        }
        CompatibilityConfiguration config = this.compatibilities.get(plugin.getName());
        if (config != null) {
            if (!config.tryApply(plugin)) {
                TLogger.error("Failed to apply compatibility support for " + plugin.getName() + ".");
            } else {
                TLogger.log("Compatibility applied: " + plugin.getName());
            }
        }
    }

    @FunctionalInterface
    public static interface CompatibilityConfiguration {
        public boolean tryApply(Plugin var1);
    }
}

