/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  lombok.Generated
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Player
 *  org.bukkit.persistence.PersistentDataType
 */
package com.ticxo.modelengine.api.model;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.entity.BaseEntity;
import com.ticxo.modelengine.api.entity.data.IEntityData;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.ModeledEntity;
import com.ticxo.modelengine.api.model.render.ModelRenderer;
import com.ticxo.modelengine.api.nms.NMSHandler;
import com.ticxo.modelengine.api.nms.RenderParsers;
import com.ticxo.modelengine.api.nms.network.NetworkHandler;
import com.ticxo.modelengine.api.utils.Profiler;
import com.ticxo.modelengine.api.utils.config.ConfigProperty;
import com.ticxo.modelengine.api.utils.data.io.SavedData;
import com.ticxo.modelengine.api.utils.ticker.DualTicker;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import lombok.Generated;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

public class ModelUpdaters {
    private final ExecutorService executors = Executors.newWorkStealingPool(ConfigProperty.MAX_ENGINE_THREADS.getIntOrIfNeg(() -> Runtime.getRuntime().availableProcessors()));
    private final Map<UUID, ModeledEntity> uuidLookup = Maps.newConcurrentMap();
    private final Map<Integer, UUID> idToUUID = Maps.newConcurrentMap();
    private final RenderParsers parsers;
    private final NetworkHandler networkHandler;
    private final NMSHandler nmsHandler;
    private final Set<UUID> desyncMonitored = Sets.newConcurrentHashSet();
    private final AtomicReference<CompletableFuture<Void>> lastTickFuture = new AtomicReference<CompletableFuture<Object>>(CompletableFuture.completedFuture(null));
    private final Profiler profiler = new Profiler(200);

    public ModelUpdaters() {
        this.parsers = ModelEngineAPI.getNMSHandler().createParsers();
        this.networkHandler = ModelEngineAPI.getNetworkHandler();
        this.nmsHandler = ModelEngineAPI.getNMSHandler();
    }

    public void start() {
        DualTicker.queueRepeatingSyncTask(this::tick, 0, 0);
    }

    public void tick() {
        CompletableFuture<Void> previousTick = this.lastTickFuture.get();
        CompletionStage currentTick = ((CompletableFuture)previousTick.thenComposeAsync(ignored -> {
            this.profiler.startProfiling();
            if (this.uuidLookup.isEmpty()) {
                this.profiler.stopProfiling();
                return CompletableFuture.completedFuture(null);
            }
            ArrayList<CompletionStage> futures = new ArrayList<CompletionStage>();
            for (UUID uuid : this.uuidLookup.keySet()) {
                ModeledEntity entity = this.uuidLookup.get(uuid);
                BaseEntity<?> base = entity.getBase();
                CompletionStage future = CompletableFuture.runAsync(() -> {
                    try {
                        entity.runTickTasks(ModeledEntity.Phase.PRE_DATA_SYNC);
                        base.getData().asyncUpdate();
                        entity.runTickTasks(ModeledEntity.Phase.PRE_MODEL_TICK);
                        if (!entity.tick()) {
                            this.forceRemoveModeledEntity(entity);
                        } else {
                            entity.runTickTasks(ModeledEntity.Phase.PRE_MODEL_RENDER);
                            this.forRenderers(entity, modelRenderer -> modelRenderer.sendToClient(this.parsers));
                            entity.runTickTasks(ModeledEntity.Phase.POST_MODEL_RENDER);
                        }
                    }
                    catch (Exception e) {
                        throw new RuntimeException("Entity with UUID " + base.getUUID() + " has encountered an exception:", e);
                    }
                }, this.executors).handle((res, ex) -> {
                    if (ex != null) {
                        ex.printStackTrace();
                    }
                    return null;
                });
                futures.add(future);
            }
            return ((CompletableFuture)CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).thenRun(this.nmsHandler::sendPivotOverrides)).handle((res, ex) -> {
                this.profiler.stopProfiling();
                if (ex != null) {
                    ex.printStackTrace();
                }
                return null;
            });
        }, (Executor)this.executors)).handle((res, ex) -> {
            if (ex != null) {
                ex.printStackTrace();
            }
            return null;
        });
        this.lastTickFuture.set((CompletableFuture<Void>)currentTick);
        for (UUID uuid : this.desyncMonitored) {
            this.networkHandler.ping(uuid);
        }
    }

    public void startDesyncMonitor(UUID uuid) {
        this.desyncMonitored.add(uuid);
    }

    public void stopDesyncMonitor(UUID uuid) {
        this.desyncMonitored.remove(uuid);
    }

    private void forRenderers(ModeledEntity modeledEntity, Consumer<ModelRenderer> rendererConsumer) {
        IEntityData data = modeledEntity.getBase().getData();
        Map<String, ActiveModel> models = modeledEntity.getModels();
        if (models.isEmpty()) {
            return;
        }
        for (ActiveModel model : models.values()) {
            if (model.getModelRenderer().isInitialized()) continue;
            return;
        }
        for (ActiveModel model : models.values()) {
            rendererConsumer.accept(model.getModelRenderer());
        }
        data.cleanup();
    }

    public void end() {
        this.executors.shutdown();
        try {
            if (!this.executors.awaitTermination(10L, TimeUnit.SECONDS)) {
                this.executors.shutdownNow();
            }
        }
        catch (InterruptedException e) {
            this.executors.shutdownNow();
        }
    }

    public Optional<ModeledEntity> registerModeledEntity(BaseEntity<?> base, ModeledEntity modeledEntity) {
        this.idToUUID.put(base.getEntityId(), base.getUUID());
        return Optional.ofNullable(this.uuidLookup.put(base.getUUID(), modeledEntity));
    }

    public ModeledEntity getModeledEntity(int id) {
        return this.getModeledEntity(this.idToUUID.get(id));
    }

    public ModeledEntity getModeledEntity(UUID uuid) {
        return uuid == null ? null : this.uuidLookup.get(uuid);
    }

    public ModeledEntity removeModeledEntity(int id) {
        return this.removeModeledEntity(this.idToUUID.get(id));
    }

    public ModeledEntity removeModeledEntity(UUID uuid) {
        if (uuid == null) {
            return null;
        }
        ModeledEntity model = this.uuidLookup.get(uuid);
        if (model != null) {
            model.markRemoved();
        }
        return model;
    }

    public void forceRemoveModeledEntity(ModeledEntity model) {
        this.uuidLookup.remove(model.getBase().getUUID());
        this.idToUUID.remove(model.getBase().getEntityId());
        model.getBase().setForcedAlive(false);
        model.destroy();
    }

    public void saveAllModels() {
        for (ModeledEntity model : this.uuidLookup.values()) {
            Entity entity;
            Object obj;
            if (!model.shouldBeSaved() || !((obj = model.getBase().getOriginal()) instanceof Entity) || (entity = (Entity)obj) instanceof Player) continue;
            model.save().ifPresent(data -> entity.getPersistentDataContainer().set(SavedData.DATA_KEY, PersistentDataType.STRING, (Object)data.toString()));
        }
    }

    @Generated
    public Profiler getProfiler() {
        return this.profiler;
    }
}

