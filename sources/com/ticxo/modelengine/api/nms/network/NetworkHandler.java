/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 */
package com.ticxo.modelengine.api.nms.network;

import com.ticxo.modelengine.api.nms.network.PipelineWrapper;
import java.util.Optional;
import java.util.UUID;
import org.bukkit.entity.Player;

public interface NetworkHandler {
    public int getProtocolVersion();

    public Optional<PipelineWrapper> getPipeline(UUID var1);

    public void removePipeline(UUID var1);

    public void injectChannel(Player var1);

    public void ejectChannel(Player var1);

    public void ping(UUID var1);

    public void startBatch();

    public boolean isBatching();

    public void endBatch();
}

