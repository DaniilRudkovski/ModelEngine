/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 */
package com.ticxo.modelengine.api.nms.network;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.nms.network.PipelineWrapper;
import com.ticxo.modelengine.api.utils.config.ConfigProperty;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.Generated;

public class ClientDesyncMonitor {
    private static long CLIENT_SYNC_INTERVAL = 10000L;
    private final PipelineWrapper pipeline;
    private final LinkedList<Long> clientSyncTime = new LinkedList();
    private final AtomicInteger pingCounter = new AtomicInteger();
    private boolean testing;
    private boolean hasPinged;
    private long lastTestTime;
    private long clientSyncOrigin;
    private long pongReceiveTime;

    public ClientDesyncMonitor(PipelineWrapper pipeline) {
        this.pipeline = pipeline;
        this.startTest();
    }

    public void startTest() {
        this.testing = true;
        this.hasPinged = false;
        ModelEngineAPI.getAPI().getModelUpdaters().startDesyncMonitor(this.pipeline.getPlayer().getUniqueId());
    }

    public void stopTest() {
        this.testing = false;
        this.lastTestTime = System.currentTimeMillis();
        ModelEngineAPI.getAPI().getModelUpdaters().stopDesyncMonitor(this.pipeline.getPlayer().getUniqueId());
        if (this.clientSyncTime.isEmpty()) {
            return;
        }
        this.clientSyncOrigin = this.clientSyncTime.getLast();
        long delta = (this.clientSyncOrigin - this.pongReceiveTime) % 50L;
        long deviation = Math.abs(delta - 25L);
        if (deviation <= 15L) {
            this.pipeline.setDelay(0L);
            return;
        }
        if (delta > 25L) {
            this.pipeline.setDelay(delta - 25L);
        } else {
            this.pipeline.setDelay(delta + 25L);
        }
    }

    public void recordClientSyncTime(long time) {
        this.clientSyncTime.add(time);
        if (this.clientSyncTime.size() > 10) {
            this.clientSyncTime.poll();
        }
        if (time > this.pongReceiveTime && this.testing && this.hasPinged) {
            this.stopTest();
        }
    }

    public void recordPongTime(long time) {
        if (!this.testing) {
            return;
        }
        this.pongReceiveTime = time;
        if (this.clientSyncTime.isEmpty()) {
            return;
        }
        if (time > this.clientSyncTime.getLast()) {
            this.hasPinged = true;
        }
    }

    public boolean clientTickShifted() {
        if (this.clientSyncTime.isEmpty()) {
            return true;
        }
        long last = this.clientSyncTime.getLast();
        return (last - this.clientSyncOrigin) % 50L < 40L;
    }

    public boolean shouldRetest() {
        return System.currentTimeMillis() - this.lastTestTime > CLIENT_SYNC_INTERVAL;
    }

    @Generated
    public PipelineWrapper getPipeline() {
        return this.pipeline;
    }

    @Generated
    public LinkedList<Long> getClientSyncTime() {
        return this.clientSyncTime;
    }

    @Generated
    public AtomicInteger getPingCounter() {
        return this.pingCounter;
    }

    @Generated
    public boolean isTesting() {
        return this.testing;
    }

    @Generated
    public boolean isHasPinged() {
        return this.hasPinged;
    }

    @Generated
    public long getLastTestTime() {
        return this.lastTestTime;
    }

    @Generated
    public long getClientSyncOrigin() {
        return this.clientSyncOrigin;
    }

    @Generated
    public long getPongReceiveTime() {
        return this.pongReceiveTime;
    }

    @Generated
    public void setTesting(boolean testing) {
        this.testing = testing;
    }

    @Generated
    public void setHasPinged(boolean hasPinged) {
        this.hasPinged = hasPinged;
    }

    @Generated
    public void setLastTestTime(long lastTestTime) {
        this.lastTestTime = lastTestTime;
    }

    @Generated
    public void setClientSyncOrigin(long clientSyncOrigin) {
        this.clientSyncOrigin = clientSyncOrigin;
    }

    @Generated
    public void setPongReceiveTime(long pongReceiveTime) {
        this.pongReceiveTime = pongReceiveTime;
    }

    static {
        ModelEngineAPI.getAPI().getConfigManager().registerReferenceUpdate(() -> {
            CLIENT_SYNC_INTERVAL = ConfigProperty.CLIENT_SYNC_INTERVAL.getInt();
        });
    }
}

