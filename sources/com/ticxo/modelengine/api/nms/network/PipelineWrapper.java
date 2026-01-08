/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 *  net.kyori.adventure.text.Component
 *  org.bukkit.entity.Player
 */
package com.ticxo.modelengine.api.nms.network;

import com.ticxo.modelengine.api.nms.network.ClientDesyncMonitor;
import com.ticxo.modelengine.api.utils.config.DebugToggle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import lombok.Generated;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

public class PipelineWrapper {
    private static final ScheduledExecutorService SCHEDULED_SERVICE = Executors.newScheduledThreadPool(10);
    private final Player player;
    private final Consumer<Object> flushAndWrite;
    private final ClientDesyncMonitor desyncMonitor;
    private long delay;

    public PipelineWrapper(Player player, Consumer<Object> flushAndWrite) {
        this.player = player;
        this.flushAndWrite = flushAndWrite;
        this.desyncMonitor = new ClientDesyncMonitor(this);
    }

    public void setDelay(long delay) {
        this.delay = delay;
        if (DebugToggle.isDebugging(DebugToggle.NOTIFY_DESYNC)) {
            this.player.sendMessage((Component)Component.text((String)("Client delay: " + delay)));
        }
    }

    public void writeAndFlush(Object object) {
        if (this.delay <= 0L) {
            this.flushAndWrite.accept(object);
        } else {
            SCHEDULED_SERVICE.schedule(() -> this.flushAndWrite.accept(object), this.delay, TimeUnit.MILLISECONDS);
        }
    }

    public void writeAndFlushNow(Object object) {
        this.flushAndWrite.accept(object);
    }

    @Generated
    public Player getPlayer() {
        return this.player;
    }

    @Generated
    public Consumer<Object> getFlushAndWrite() {
        return this.flushAndWrite;
    }

    @Generated
    public ClientDesyncMonitor getDesyncMonitor() {
        return this.desyncMonitor;
    }

    @Generated
    public long getDelay() {
        return this.delay;
    }
}

