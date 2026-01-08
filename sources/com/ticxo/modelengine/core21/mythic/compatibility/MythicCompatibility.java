/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.lumine.mythic.api.skills.SkillCaster
 *  io.lumine.mythic.api.skills.SkillMetadata
 *  io.lumine.mythic.bukkit.BukkitAdapter
 *  io.lumine.mythic.bukkit.MythicBukkit
 *  io.lumine.mythic.bukkit.utils.Events
 *  io.lumine.mythic.core.skills.EventExecutor
 *  io.lumine.mythic.core.skills.SkillTriggers
 *  io.lumine.mythic.core.skills.TriggeredSkill
 *  lombok.Generated
 *  org.bukkit.Bukkit
 *  org.bukkit.entity.Entity
 *  org.bukkit.event.Listener
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.java.JavaPlugin
 */
package com.ticxo.modelengine.core21.mythic.compatibility;

import com.ticxo.modelengine.api.events.ModelDismountEvent;
import com.ticxo.modelengine.api.utils.CompatibilityManager;
import com.ticxo.modelengine.core.ModelEngine;
import com.ticxo.modelengine.core21.mythic.animation.MythicScriptReader;
import com.ticxo.modelengine.core21.mythic.compatibility.ModelEngineSupportImpl;
import com.ticxo.modelengine.core21.mythic.utils.ModelEngineComponentRegistry;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.utils.Events;
import io.lumine.mythic.core.skills.EventExecutor;
import io.lumine.mythic.core.skills.SkillTriggers;
import io.lumine.mythic.core.skills.TriggeredSkill;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import lombok.Generated;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class MythicCompatibility
implements CompatibilityManager.CompatibilityConfiguration {
    private final ModelEngine plugin;
    private ModelEngineSupportImpl mythicSupport;
    private ModelEngineComponentRegistry customComponentRegistry;

    public MythicCompatibility(ModelEngine plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean tryApply(Plugin plugin) {
        this.customComponentRegistry = new ModelEngineComponentRegistry((JavaPlugin)this.plugin, (Collection<String>)new ArrayList<String>(){
            {
                this.add("com.ticxo.modelengine.core21.mythic.conditions");
                this.add("com.ticxo.modelengine.core21.mythic.mechanics");
                this.add("com.ticxo.modelengine.core21.mythic.targeters");
                this.add("com.ticxo.modelengine.core21.mythic.placeholders");
            }
        });
        this.plugin.getScriptReaderRegistry().register("mm", new MythicScriptReader());
        this.mythicSupport = new ModelEngineSupportImpl();
        MythicBukkit.inst().getCompatibility().setModelEngine(Optional.of(this.mythicSupport));
        Bukkit.getPluginManager().registerEvents((Listener)this.customComponentRegistry, plugin);
        this.plugin.getModelGenerator().importModels(true);
        Events.subscribe(ModelDismountEvent.class).handler(this::onDismounted);
        return true;
    }

    void onDismounted(ModelDismountEvent event) {
        MythicBukkit.inst().getMobManager().getActiveMob(event.getVehicle().getModeledEntity().getBase().getUUID()).ifPresent(mob -> {
            SkillMetadata data;
            EventExecutor eventBus = MythicBukkit.inst().getSkillManager().getEventBus();
            TriggeredSkill ts = eventBus.processTriggerMechanics(data = eventBus.buildSkillMetadata(SkillTriggers.DISMOUNTED, (SkillCaster)mob, BukkitAdapter.adapt((Entity)event.getPassenger()), mob.getLocation(), false));
            if (ts.getCancelled()) {
                event.setCancelled(true);
            }
        });
    }

    @Generated
    public ModelEngineSupportImpl getMythicSupport() {
        return this.mythicSupport;
    }

    @Generated
    public ModelEngineComponentRegistry getCustomComponentRegistry() {
        return this.customComponentRegistry;
    }
}

