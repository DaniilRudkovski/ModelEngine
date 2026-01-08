/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  lombok.Generated
 *  org.bukkit.plugin.java.JavaPlugin
 */
package com.ticxo.modelengine.api.entity;

import com.google.common.collect.Maps;
import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.entity.data.IEntityData;
import com.ticxo.modelengine.api.utils.config.ConfigProperty;
import com.ticxo.modelengine.api.utils.logger.TLogger;
import com.ticxo.modelengine.api.utils.scheduling.PlatformScheduler;
import com.ticxo.modelengine.api.utils.ticker.AbstractLoadBalancer;
import com.ticxo.modelengine.api.utils.ticker.DualTicker;
import com.ticxo.modelengine.api.utils.ticker.LoadBalancer;
import com.ticxo.modelengine.api.utils.ticker.PseudoThread;
import com.ticxo.modelengine.api.utils.ticker.Task;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.Generated;
import org.bukkit.plugin.java.JavaPlugin;

public class EntityDataTrackers
extends AbstractLoadBalancer<UUID, Tracker> {
    private final Map<Tracker, PseudoThread> threads = new HashMap<Tracker, PseudoThread>();
    private final JavaPlugin plugin;
    private final PlatformScheduler scheduler;
    private final int maximumThreads;
    private int trackerCount = 0;
    private boolean started;

    public EntityDataTrackers(JavaPlugin plugin, PlatformScheduler scheduler) {
        super(ConfigProperty.CULLING_THREADS.getInt());
        this.plugin = plugin;
        this.scheduler = scheduler;
        this.maximumThreads = Math.max(ConfigProperty.MAX_CULLING_THREADS.getInt(), this.available.size());
        this.available.forEach(this::setup);
    }

    @Override
    public Tracker supply() {
        return new Tracker();
    }

    public void start() {
        this.started = true;
        for (Map.Entry<Tracker, PseudoThread> entry : this.threads.entrySet()) {
            entry.getValue().start();
        }
    }

    public void end() {
        for (Map.Entry<Tracker, PseudoThread> entry : this.threads.entrySet()) {
            entry.getValue().end();
        }
    }

    private void setup(Tracker tracker) {
        tracker.setId("data_tracker_" + this.trackerCount);
        PseudoThread thread = new PseudoThread(tracker.getId(), this.scheduler, this.plugin, true, 0, 0, true, false);
        thread.registerOverloadCallback(skipped -> {
            TLogger.debug(tracker.getId() + " is overloaded with " + tracker.getLoad() + " targets.");
            if (this.available.size() < this.maximumThreads) {
                this.growAndBalance(tracker, (int)skipped);
            }
        });
        thread.queueTask(new Task(task -> tracker.asyncFetchEntityData(), 0, 0, true));
        DualTicker.queueRepeatingSyncTask(tracker::fetchEntityData, 0, 0);
        this.threads.put(tracker, thread);
        if (this.started) {
            thread.start();
        }
        ++this.trackerCount;
    }

    private void growAndBalance(Tracker request, int skipped) {
        TLogger.debug(request.id + " has skipped " + skipped + " ticks. Requested a new server.");
        Tracker handle = this.supply();
        int moved = request.dataTrackers.size() / 2;
        for (Map.Entry<UUID, IEntityData> entry : request.dataTrackers.entrySet()) {
            if (moved <= 0) break;
            handle.putEntityData(entry.getKey(), entry.getValue());
            this.reference.put(entry.getKey(), handle);
            --moved;
        }
        handle.dataTrackers.keySet().forEach(request.dataTrackers::remove);
        this.available.add(handle);
        this.setup(handle);
        TLogger.debug("- Created " + handle.id + ".");
    }

    public class Tracker
    implements LoadBalancer.Server {
        private final Map<UUID, IEntityData> dataTrackers = Maps.newConcurrentMap();
        private String id = "Unknown";
        private int lastSize = 0;
        private long timings;

        public void fetchEntityData() {
            for (Map.Entry<UUID, IEntityData> entry : this.dataTrackers.entrySet()) {
                IEntityData data = entry.getValue();
                if (!data.isDataValid() || !ModelEngineAPI.isModeledEntity(entry.getKey()) && !ModelEngineAPI.isVFX(entry.getKey())) {
                    data.destroy();
                    this.dataTrackers.remove(entry.getKey());
                    EntityDataTrackers.this.unregister(entry.getKey());
                    continue;
                }
                data.syncUpdate();
            }
        }

        public void asyncFetchEntityData() {
            long time = System.currentTimeMillis();
            for (Map.Entry<UUID, IEntityData> entry : this.dataTrackers.entrySet()) {
                IEntityData data = entry.getValue();
                data.cullUpdate();
            }
            this.timings = System.currentTimeMillis() - time;
            if (this.lastSize != this.dataTrackers.size()) {
                this.lastSize = this.dataTrackers.size();
                TLogger.debug(this.id + ": " + this.lastSize + " - " + this.timings);
            }
        }

        public void putEntityData(UUID uuid, IEntityData dataTracker) {
            this.dataTrackers.put(uuid, dataTracker);
        }

        public IEntityData getEntityData(UUID uuid) {
            return this.dataTrackers.get(uuid);
        }

        public IEntityData removeEntityData(UUID uuid) {
            EntityDataTrackers.this.unregister(uuid);
            return this.dataTrackers.remove(uuid);
        }

        @Override
        public int getLoad() {
            return this.lastSize;
        }

        @Generated
        public Map<UUID, IEntityData> getDataTrackers() {
            return this.dataTrackers;
        }

        @Generated
        public String getId() {
            return this.id;
        }

        @Generated
        public void setId(String id) {
            this.id = id;
        }

        @Generated
        public long getTimings() {
            return this.timings;
        }
    }
}

