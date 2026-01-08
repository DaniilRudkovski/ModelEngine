/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.protocol.Packet
 *  net.minecraft.network.protocol.game.ClientboundBundlePacket
 *  net.minecraft.network.protocol.game.PacketListenerPlayOut
 */
package com.ticxo.modelengine.v1_20_R2.network.utils;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.nms.network.ProtectedPacket;
import com.ticxo.modelengine.api.utils.config.ConfigProperty;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundBundlePacket;
import net.minecraft.network.protocol.game.PacketListenerPlayOut;

public class Bundler {
    private static int BUNDLE_SIZE;
    private final List<List<Packet<PacketListenerPlayOut>>> bundles = new ArrayList<List<Packet<PacketListenerPlayOut>>>();

    public void appendPacket(Packet<PacketListenerPlayOut> packet) {
        List<Packet<PacketListenerPlayOut>> bundleList;
        if (this.bundles.isEmpty()) {
            this.bundles.add(new ArrayList());
        }
        if ((bundleList = this.bundles.get(this.bundles.size() - 1)).size() == BUNDLE_SIZE) {
            bundleList = new ArrayList<Packet<PacketListenerPlayOut>>();
            this.bundles.add(bundleList);
        }
        bundleList.add(packet);
    }

    public void appendPacket(Collection<? extends Packet> packets) {
        List<Packet<PacketListenerPlayOut>> bundleList;
        if (this.bundles.isEmpty()) {
            this.bundles.add(new ArrayList());
        }
        if ((bundleList = this.bundles.get(this.bundles.size() - 1)).size() + packets.size() > BUNDLE_SIZE) {
            bundleList = new ArrayList<Packet<PacketListenerPlayOut>>();
            this.bundles.add(bundleList);
        }
        for (Packet packet : packets) {
            bundleList.add((Packet<PacketListenerPlayOut>)packet);
        }
    }

    public void clear() {
        this.bundles.clear();
    }

    public void bundle(Consumer<Object> send) {
        for (List<Packet<PacketListenerPlayOut>> list : this.bundles) {
            ProtectedPacket wrapped = new ProtectedPacket(new ClientboundBundlePacket(list));
            send.accept(wrapped);
        }
    }

    static {
        ModelEngineAPI.getAPI().getConfigManager().registerReferenceUpdate(() -> {
            BUNDLE_SIZE = Math.min(ConfigProperty.BUNDLE_SIZE.getInt(), 4096);
        });
    }
}

