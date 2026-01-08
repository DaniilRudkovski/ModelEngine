/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 *  net.minecraft.network.PacketDataSerializer
 *  net.minecraft.network.protocol.game.PacketListenerPlayIn
 *  net.minecraft.network.protocol.game.PacketPlayInUseEntity
 *  net.minecraft.network.protocol.game.PacketPlayInUseEntity$c
 *  net.minecraft.server.level.WorldServer
 *  net.minecraft.world.entity.Entity
 *  org.jetbrains.annotations.Nullable
 */
package com.ticxo.modelengine.v1_20_R3.network.patch;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.interaction.InteractionTracker;
import com.ticxo.modelengine.v1_20_R3.network.patch.PatchedServerGamePacketListener;
import com.ticxo.modelengine.v1_20_R3.network.utils.NetworkUtils;
import lombok.Generated;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.game.PacketListenerPlayIn;
import net.minecraft.network.protocol.game.PacketPlayInUseEntity;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

public class ServerboundInteractPacketWrapper
extends PacketPlayInUseEntity {
    private final int originalId;
    private final int relayedId;
    private final PacketPlayInUseEntity original;

    public ServerboundInteractPacketWrapper(int originalId, int relayedId, PacketPlayInUseEntity original) {
        super(NetworkUtils.readData(original));
        this.originalId = originalId;
        this.relayedId = relayedId;
        this.original = original;
    }

    public void a(PacketDataSerializer var0) {
        this.original.a(var0);
    }

    public void a(PacketListenerPlayIn var0) {
        PatchedServerGamePacketListener.handleInteract(this, var0);
    }

    @Nullable
    public Entity a(WorldServer var0) {
        return var0.b(this.relayedId);
    }

    public boolean isFakeInteraction() {
        InteractionTracker tracker = ModelEngineAPI.getInteractionTracker();
        return tracker.getModelRelay(this.originalId) != null || tracker.getEntityRelay(this.originalId) != null;
    }

    public boolean a() {
        return this.original.a();
    }

    public void a(PacketPlayInUseEntity.c var0) {
        this.original.a(var0);
    }

    @Generated
    public int getOriginalId() {
        return this.originalId;
    }

    @Generated
    public int getRelayedId() {
        return this.relayedId;
    }

    @Generated
    public PacketPlayInUseEntity getOriginal() {
        return this.original;
    }
}

