/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 *  net.minecraft.server.level.ChunkMap$TrackedEntity
 *  net.minecraft.server.network.ServerGamePacketListenerImpl
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityDimensions
 *  net.minecraft.world.item.InstrumentItem
 *  net.minecraft.world.item.ItemStack
 */
package com.ticxo.modelengine.v1_20_R4;

import com.ticxo.modelengine.api.utils.ReflectionUtils;
import lombok.Generated;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.item.InstrumentItem;
import net.minecraft.world.item.ItemStack;

public enum NMSMethods implements ReflectionUtils.MethodEnum
{
    TRACKED_ENTITY_getEffectiveRange(ChunkMap.TrackedEntity.class, "b", "getEffectiveRange", new Class[0]),
    SERVER_GAME_PACKET_LISTENER_IMPL_checkLimit(ServerGamePacketListenerImpl.class, "checkLimit", "checkLimit", Long.TYPE),
    INSTRUMENT_ITEM_getInstrument(InstrumentItem.class, "d", "getInstrument", ItemStack.class),
    ENTITY_getPassengerAttachmentPoint(Entity.class, "a", "getPassengerAttachmentPoint", Entity.class, EntityDimensions.class, Float.TYPE);

    private final Class<?> target;
    private final String obfuscated;
    private final String mapped;
    private final Class<?>[] parameterClasses;

    private NMSMethods(Class<?> target, String obfuscated, String mapped, Class<?> ... parameterClasses) {
        this.target = target;
        this.obfuscated = obfuscated;
        this.mapped = mapped;
        this.parameterClasses = parameterClasses;
    }

    @Override
    @Generated
    public Class<?> getTarget() {
        return this.target;
    }

    @Override
    @Generated
    public String getObfuscated() {
        return this.obfuscated;
    }

    @Override
    @Generated
    public String getMapped() {
        return this.mapped;
    }

    @Override
    @Generated
    public Class<?>[] getParameterClasses() {
        return this.parameterClasses;
    }
}

