/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  io.lumine.mythic.api.config.MythicLineConfig
 *  io.lumine.mythic.api.skills.ISkillMechanic
 *  io.lumine.mythic.api.skills.conditions.ISkillCondition
 *  io.lumine.mythic.api.skills.targeters.ISkillTargeter
 *  io.lumine.mythic.bukkit.MythicBukkit
 *  io.lumine.mythic.bukkit.events.MythicConditionLoadEvent
 *  io.lumine.mythic.bukkit.events.MythicMechanicLoadEvent
 *  io.lumine.mythic.bukkit.events.MythicReloadedEvent
 *  io.lumine.mythic.bukkit.events.MythicTargeterLoadEvent
 *  io.lumine.mythic.bukkit.utils.Schedulers
 *  io.lumine.mythic.core.logging.MythicLogger
 *  io.lumine.mythic.core.skills.placeholders.Placeholder
 *  io.lumine.mythic.core.utils.annotations.AnnotationUtil
 *  io.lumine.mythic.core.utils.annotations.MythicPlaceholder
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.plugin.java.JavaPlugin
 */
package com.ticxo.modelengine.core21.mythic.utils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.core21.mythic.utils.MythicCondition;
import com.ticxo.modelengine.core21.mythic.utils.MythicMechanic;
import com.ticxo.modelengine.core21.mythic.utils.MythicTargeter;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ISkillMechanic;
import io.lumine.mythic.api.skills.conditions.ISkillCondition;
import io.lumine.mythic.api.skills.targeters.ISkillTargeter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.events.MythicConditionLoadEvent;
import io.lumine.mythic.bukkit.events.MythicMechanicLoadEvent;
import io.lumine.mythic.bukkit.events.MythicReloadedEvent;
import io.lumine.mythic.bukkit.events.MythicTargeterLoadEvent;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.placeholders.Placeholder;
import io.lumine.mythic.core.utils.annotations.AnnotationUtil;
import io.lumine.mythic.core.utils.annotations.MythicPlaceholder;
import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.Map;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class ModelEngineComponentRegistry
implements Listener {
    private final Map<String, Constructor<? extends ISkillMechanic>> mechanics = Maps.newConcurrentMap();
    private final Map<String, Constructor<? extends ISkillCondition>> conditions = Maps.newConcurrentMap();
    private final Map<String, Constructor<? extends ISkillTargeter>> targeters = Maps.newConcurrentMap();
    private final Map<String, Class<? extends Placeholder>> placeholders = Maps.newConcurrentMap();

    public ModelEngineComponentRegistry(JavaPlugin plugin, String packagePath) {
        this(plugin, Lists.newArrayList((Object[])new String[]{packagePath}));
    }

    public ModelEngineComponentRegistry(JavaPlugin plugin, Collection<String> packages) {
        for (String packagePath : packages) {
            String[] aliases;
            String name;
            Object clazz2;
            Collection mechanicsClasses = AnnotationUtil.getAnnotatedClasses((JavaPlugin)plugin, (String)packagePath, MythicMechanic.class);
            for (Object clazz2 : mechanicsClasses) {
                try {
                    Constructor constructor;
                    MythicMechanic annotation = ((Class)clazz2).getAnnotation(MythicMechanic.class);
                    String namespace = annotation.namespace();
                    String name2 = annotation.name();
                    String[] aliases2 = annotation.aliases();
                    if (!ISkillMechanic.class.isAssignableFrom((Class<?>)clazz2)) continue;
                    try {
                        constructor = ((Class)clazz2).getConstructor(MythicLineConfig.class);
                    }
                    catch (NoSuchMethodException ex) {
                        try {
                            constructor = ((Class)clazz2).getConstructor(MythicMechanicLoadEvent.class);
                        }
                        catch (NoSuchMethodException ex2) {
                            constructor = ((Class)clazz2).getConstructor(new Class[0]);
                        }
                    }
                    this.mechanics.put(name2.toUpperCase(), constructor);
                    this.mechanics.put(namespace.toUpperCase() + ":" + name2.toUpperCase(), constructor);
                    for (String alias : aliases2) {
                        this.mechanics.put(alias.toUpperCase(), constructor);
                        this.mechanics.put(namespace.toUpperCase() + ":" + alias.toUpperCase(), constructor);
                    }
                }
                catch (Throwable ex) {
                    MythicLogger.error((String)"Failed to register custom mechanic {0}", (Object[])new Object[]{((Class)clazz2).getCanonicalName()});
                    ex.printStackTrace();
                }
            }
            Collection conditionClasses = AnnotationUtil.getAnnotatedClasses((JavaPlugin)plugin, (String)packagePath, MythicCondition.class);
            clazz2 = conditionClasses.iterator();
            while (clazz2.hasNext()) {
                Class clazz3 = (Class)clazz2.next();
                try {
                    Constructor constructor;
                    MythicCondition annotation = clazz3.getAnnotation(MythicCondition.class);
                    String namespace = annotation.namespace();
                    String name3 = annotation.name();
                    String[] aliases3 = annotation.aliases();
                    if (!ISkillCondition.class.isAssignableFrom(clazz3)) continue;
                    try {
                        constructor = clazz3.getConstructor(ModelEngineAPI.class, MythicConditionLoadEvent.class);
                    }
                    catch (NoSuchMethodException ex) {
                        try {
                            constructor = clazz3.getConstructor(MythicLineConfig.class);
                        }
                        catch (NoSuchMethodException ex2) {
                            constructor = clazz3.getConstructor(new Class[0]);
                        }
                    }
                    this.conditions.put(name3.toUpperCase(), constructor);
                    this.conditions.put(namespace.toUpperCase() + ":" + name3.toUpperCase(), constructor);
                    for (String alias : aliases3) {
                        this.conditions.put(alias.toUpperCase(), constructor);
                        this.conditions.put(namespace.toUpperCase() + ":" + alias.toUpperCase(), constructor);
                    }
                }
                catch (Throwable ex) {
                    MythicLogger.error((String)"Failed to register custom condition {0}", (Object[])new Object[]{clazz3.getCanonicalName()});
                    ex.printStackTrace();
                }
            }
            Collection targeterClasses = AnnotationUtil.getAnnotatedClasses((JavaPlugin)plugin, (String)packagePath, MythicTargeter.class);
            for (Class clazz4 : targeterClasses) {
                try {
                    Constructor constructor;
                    MythicTargeter annotation = clazz4.getAnnotation(MythicTargeter.class);
                    String namespace = annotation.namespace();
                    name = annotation.name();
                    aliases = annotation.aliases();
                    if (!ISkillTargeter.class.isAssignableFrom(clazz4)) continue;
                    try {
                        constructor = clazz4.getConstructor(MythicLineConfig.class);
                    }
                    catch (NoSuchMethodException ex) {
                        constructor = clazz4.getConstructor(new Class[0]);
                    }
                    this.targeters.put(name.toUpperCase(), constructor);
                    this.targeters.put(namespace.toUpperCase() + ":" + name.toUpperCase(), constructor);
                    for (String alias : aliases) {
                        this.targeters.put(alias.toUpperCase(), constructor);
                        this.targeters.put(namespace.toUpperCase() + ":" + alias.toUpperCase(), constructor);
                    }
                }
                catch (Throwable ex) {
                    MythicLogger.error((String)"Failed to register custom targeter {0}", (Object[])new Object[]{clazz4.getCanonicalName()});
                    ex.printStackTrace();
                }
            }
            Collection placeholderClasses = AnnotationUtil.getAnnotatedClasses((JavaPlugin)plugin, (String)packagePath, MythicPlaceholder.class);
            for (Class clazz5 : placeholderClasses) {
                try {
                    MythicPlaceholder anno = clazz5.getAnnotation(MythicPlaceholder.class);
                    name = anno.placeholder();
                    aliases = anno.aliases();
                    if (!Placeholder.class.isAssignableFrom(clazz5)) continue;
                    this.placeholders.put(name, clazz5);
                    for (String alias : aliases) {
                        this.placeholders.put(alias, clazz5);
                    }
                }
                catch (Exception ex) {
                    MythicLogger.error((String)"Failed to load placeholder {0}", (Object[])new Object[]{clazz5.getCanonicalName()});
                }
            }
        }
        Schedulers.sync().runLater(() -> this.injectPlaceholders(), 20L);
    }

    @EventHandler
    public void onMythicMechanicLoad(MythicMechanicLoadEvent event) {
        String name = event.getMechanicName().toUpperCase();
        if (this.mechanics.containsKey(name)) {
            Constructor<? extends ISkillMechanic> constructor = this.mechanics.get(name);
            try {
                if (constructor.getParameterCount() == 1) {
                    if (constructor.getParameterTypes()[0] == MythicLineConfig.class) {
                        event.register(constructor.newInstance(event.getConfig()));
                    } else {
                        event.register(constructor.newInstance(event));
                    }
                } else {
                    event.register(constructor.newInstance(new Object[0]));
                }
            }
            catch (Exception e) {
                MythicLogger.error((String)"Failed to construct mechanic {0}", (Object[])new Object[]{name});
                e.printStackTrace();
            }
        }
    }

    @EventHandler
    public void onMythicConditionLoad(MythicConditionLoadEvent event) {
        String name = event.getConditionName().toUpperCase();
        if (this.conditions.containsKey(name)) {
            Constructor<? extends ISkillCondition> constructor = this.conditions.get(name);
            try {
                if (constructor.getParameterCount() == 2) {
                    event.register(constructor.newInstance(new Object[]{ModelEngineAPI.getAPI(), event}));
                } else if (constructor.getParameterCount() == 1) {
                    event.register(constructor.newInstance(event.getConfig()));
                } else {
                    event.register(constructor.newInstance(new Object[0]));
                }
            }
            catch (Exception e) {
                MythicLogger.error((String)"Failed to construct condition {0}", (Object[])new Object[]{name});
                e.printStackTrace();
            }
        }
    }

    @EventHandler
    public void onMythicTargeterLoad(MythicTargeterLoadEvent event) {
        String name = event.getTargeterName().toUpperCase();
        if (this.targeters.containsKey(name)) {
            Constructor<? extends ISkillTargeter> constructor = this.targeters.get(name);
            try {
                if (constructor.getParameterCount() == 1) {
                    event.register(constructor.newInstance(event.getConfig()));
                } else {
                    event.register(constructor.newInstance(new Object[0]));
                }
            }
            catch (Exception e) {
                MythicLogger.error((String)"Failed to construct targeter {0}", (Object[])new Object[]{name});
                e.printStackTrace();
            }
        }
    }

    @EventHandler
    public void onMythicReloaded(MythicReloadedEvent event) {
        this.injectPlaceholders();
    }

    private void injectPlaceholders() {
        for (Map.Entry<String, Class<? extends Placeholder>> entry : this.placeholders.entrySet()) {
            String name = entry.getKey();
            Class<? extends Placeholder> clazz = entry.getValue();
            MythicBukkit.inst().getPlaceholderManager().register(name, clazz);
        }
    }
}

