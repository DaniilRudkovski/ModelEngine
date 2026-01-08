/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonSyntaxException
 *  net.citizensnpcs.api.CitizensAPI
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.world.EntitiesLoadEvent
 *  org.bukkit.event.world.EntitiesUnloadEvent
 *  org.bukkit.event.world.WorldLoadEvent
 *  org.bukkit.event.world.WorldUnloadEvent
 *  org.bukkit.persistence.PersistentDataContainer
 *  org.bukkit.persistence.PersistentDataType
 */
package com.ticxo.modelengine.core.listener;

import com.google.gson.JsonSyntaxException;
import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.ServerInfo;
import com.ticxo.modelengine.api.generator.ModelGenerator;
import com.ticxo.modelengine.api.model.ModeledEntity;
import com.ticxo.modelengine.api.utils.config.ConfigProperty;
import com.ticxo.modelengine.api.utils.data.io.SavedData;
import com.ticxo.modelengine.api.utils.ticker.DualTicker;
import com.ticxo.modelengine.core.data.DataUpdater;
import java.util.List;
import net.citizensnpcs.api.CitizensAPI;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.EntitiesLoadEvent;
import org.bukkit.event.world.EntitiesUnloadEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class WorldListener
implements Listener {
    private final ModelGenerator generator = ModelEngineAPI.getAPI().getModelGenerator();
    private boolean onWorldLoad = false;
    private boolean onWorldUnload = false;

    public WorldListener() {
        ModelEngineAPI.getAPI().getConfigManager().registerReferenceUpdate(() -> {
            this.onWorldLoad = ConfigProperty.LOAD_ON_WORLD_LOAD.getBoolean();
            this.onWorldUnload = ConfigProperty.UNLOAD_ON_WORLD_UNLOAD.getBoolean();
        });
    }

    @EventHandler(priority=EventPriority.MONITOR)
    private void onWorldLoad(WorldLoadEvent event) {
        if (!this.onWorldLoad) {
            return;
        }
        if (this.generator.isInitialized()) {
            this.loadEntities(event.getWorld().getEntities());
        } else {
            this.generator.queueTask(ModelGenerator.Phase.POST_IMPORT, () -> DualTicker.queueSyncTask(() -> this.loadEntities(event.getWorld().getEntities())));
        }
    }

    @EventHandler(priority=EventPriority.MONITOR)
    private void onWorldUnload(WorldUnloadEvent event) {
        if (!this.onWorldUnload) {
            return;
        }
        if (this.generator.isInitialized()) {
            this.unloadEntities(event.getWorld().getEntities());
        } else {
            this.generator.queueTask(ModelGenerator.Phase.POST_IMPORT, () -> DualTicker.queueSyncTask(() -> this.unloadEntities(event.getWorld().getEntities())));
        }
    }

    @EventHandler(priority=EventPriority.MONITOR)
    private void onEntityLoad(EntitiesLoadEvent event) {
        if (this.onWorldLoad) {
            return;
        }
        if (this.generator.isInitialized()) {
            this.loadEntities(event.getEntities());
        } else {
            this.generator.queueTask(ModelGenerator.Phase.POST_IMPORT, () -> this.loadEntities(event.getEntities()));
        }
    }

    @EventHandler(priority=EventPriority.MONITOR)
    private void onEntityUnload(EntitiesUnloadEvent event) {
        if (this.onWorldUnload) {
            return;
        }
        if (this.generator.isInitialized()) {
            this.unloadEntities(event.getEntities());
        } else {
            this.generator.queueTask(ModelGenerator.Phase.POST_IMPORT, () -> this.unloadEntities(event.getEntities()));
        }
    }

    private void loadEntities(List<Entity> entities) {
        for (Entity entity : entities) {
            String jsonData;
            if (entity instanceof Player || ModelEngineAPI.getModeledEntity(entity.getUniqueId()) != null || (jsonData = (String)entity.getPersistentDataContainer().get(SavedData.DATA_KEY, PersistentDataType.STRING)) == null) continue;
            try {
                SavedData data = DataUpdater.convertToSavedData(entity.getLocation(), jsonData);
                if (!DataUpdater.tryUpdate(data)) continue;
                ModeledEntity model = ModelEngineAPI.createModeledEntity(entity);
                model.load(data);
            }
            catch (JsonSyntaxException e) {
                e.printStackTrace();
            }
        }
    }

    private void unloadEntities(List<Entity> entities) {
        for (Entity entity : entities) {
            if (entity instanceof Player || ServerInfo.HAS_CITIZENS && CitizensAPI.getNPCRegistry().isNPC(entity)) continue;
            PersistentDataContainer pdc = entity.getPersistentDataContainer();
            ModeledEntity model = ModelEngineAPI.getModeledEntity(entity.getUniqueId());
            if (model == null) {
                pdc.remove(SavedData.DATA_KEY);
                continue;
            }
            if (model.shouldBeSaved()) {
                try {
                    model.save().ifPresentOrElse(data -> pdc.set(SavedData.DATA_KEY, PersistentDataType.STRING, (Object)data.toString()), () -> pdc.remove(SavedData.DATA_KEY));
                }
                catch (Throwable ex) {
                    throw new IllegalStateException("Failed to save data for entity " + entity.getType() + " " + entity.getUniqueId() + " at " + entity.getLocation().getX() + "," + entity.getLocation().getY() + "," + entity.getLocation().getZ(), ex);
                }
            } else {
                entity.getPersistentDataContainer().remove(SavedData.DATA_KEY);
            }
            ModelEngineAPI.getAPI().getModelUpdaters().forceRemoveModeledEntity(model);
        }
    }
}

