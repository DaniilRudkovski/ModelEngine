/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 *  net.minecraft.network.protocol.Packet
 *  net.minecraft.network.protocol.PacketType
 *  net.minecraft.network.protocol.game.GamePacketTypes
 *  net.minecraft.network.protocol.game.ServerGamePacketListener
 *  net.minecraft.network.protocol.game.ServerboundInteractPacket
 *  net.minecraft.network.protocol.game.ServerboundInteractPacket$Handler
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.entity.Entity
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.ticxo.modelengine.v1_21_R4.network.patch;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.interaction.InteractionTracker;
import com.ticxo.modelengine.v1_21_R4.network.patch.PatchedServerGamePacketListener;
import lombok.Generated;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.network.protocol.game.GamePacketTypes;
import net.minecraft.network.protocol.game.ServerGamePacketListener;
import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ServerboundInteractPacketWrapper
implements Packet<ServerGamePacketListener> {
    private final int originalId;
    private final int relayedId;
    private final ServerboundInteractPacket original;
    private final boolean isAttack;

    public ServerboundInteractPacketWrapper(int originalId, int relayedId, int action, ServerboundInteractPacket original) {
        this.originalId = originalId;
        this.relayedId = relayedId;
        this.original = original;
        this.isAttack = action == 1;
    }

    @NotNull
    public PacketType<? extends Packet<ServerGamePacketListener>> type() {
        return GamePacketTypes.SERVERBOUND_INTERACT;
    }

    public int getEntityId() {
        return this.originalId;
    }

    public boolean isAttack() {
        return this.isAttack;
    }

    public void handle(ServerGamePacketListener var0) {
        PatchedServerGamePacketListener.handleInteract(this, var0);
    }

    @Nullable
    public Entity getTarget(ServerLevel var0) {
        return var0.getEntityOrPart(this.relayedId);
    }

    public boolean isFakeInteraction() {
        InteractionTracker tracker = ModelEngineAPI.getInteractionTracker();
        return tracker.getModelRelay(this.originalId) != null || tracker.getEntityRelay(this.originalId) != null;
    }

    public boolean isUsingSecondaryAction() {
        return this.original.isUsingSecondaryAction();
    }

    public void dispatch(ServerboundInteractPacket.Handler var0) {
        this.original.dispatch(var0);
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
    public ServerboundInteractPacket getOriginal() {
        return this.original;
    }
}

