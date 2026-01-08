/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Sets
 *  org.bukkit.configuration.ConfigurationSection
 *  org.bukkit.configuration.file.FileConfiguration
 *  org.bukkit.plugin.java.JavaPlugin
 *  org.jetbrains.annotations.NotNull
 */
package com.ticxo.modelengine.api.utils.config;

import com.google.common.collect.Sets;
import com.ticxo.modelengine.api.utils.config.Property;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class ConfigManager {
    private final JavaPlugin plugin;
    private final Map<String, Object> config = new LinkedHashMap<String, Object>();
    private final Set<Runnable> updater = Sets.newConcurrentHashSet();
    private FileConfiguration file;

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.file = plugin.getConfig();
        plugin.saveDefaultConfig();
    }

    public void register(Property property) {
        if (property.getDef() == null) {
            return;
        }
        this.register(property.getPath(), property.getDef());
    }

    public void register(String path, @NotNull Object def) {
        this.config.put(path, this.file.get(path, def));
    }

    public void registerReferenceUpdate(Runnable runnable) {
        this.updater.add(runnable);
        runnable.run();
    }

    public void reload() {
        this.plugin.reloadConfig();
        this.file = this.plugin.getConfig();
        this.config.replaceAll((p, v) -> this.file.get(p, this.config.get(p)));
    }

    public void updateReferences() {
        this.updater.forEach(Runnable::run);
    }

    public <T> T get(String path) {
        try {
            return (T)this.config.get(path);
        }
        catch (ClassCastException ignored) {
            return null;
        }
    }

    public <T> T get(Property property) {
        return this.get(property.getPath());
    }

    public int getInt(Property property) {
        return (Integer)this.get(property);
    }

    public double getDouble(Property property) {
        Object o = this.get(property);
        if (o instanceof Number) {
            Number number = (Number)o;
            return number.doubleValue();
        }
        return 0.0;
    }

    public String getString(Property property) {
        return (String)this.get(property);
    }

    public boolean getBoolean(Property property) {
        return (Boolean)this.get(property);
    }

    public List<String> getStringList(Property property) {
        return (List)this.get(property);
    }

    public ConfigurationSection getConfigSection(String path) {
        return this.file.getConfigurationSection(path);
    }

    public void save() {
        this.config.forEach((arg_0, arg_1) -> ((FileConfiguration)this.file).set(arg_0, arg_1));
        this.plugin.saveConfig();
    }
}

