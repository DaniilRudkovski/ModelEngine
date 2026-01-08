/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 *  net.minecraft.server.level.PlayerChunkMap$EntityTracker
 *  net.minecraft.server.network.PlayerConnection
 *  net.minecraft.world.item.InstrumentItem
 *  net.minecraft.world.item.ItemStack
 */
package com.ticxo.modelengine.v1_20_R1;

import com.ticxo.modelengine.api.utils.ReflectionUtils;
import lombok.Generated;
import net.minecraft.server.level.PlayerChunkMap;
import net.minecraft.server.network.PlayerConnection;
import net.minecraft.world.item.InstrumentItem;
import net.minecraft.world.item.ItemStack;

public enum NMSMethods implements ReflectionUtils.MethodEnum
{
    TRACKED_ENTITY_getEffectiveRange(PlayerChunkMap.EntityTracker.class, "b", "getEffectiveRange", new Class[0]),
    SERVER_GAME_PACKET_LISTENER_IMPL_checkLimit(PlayerConnection.class, "checkLimit", "checkLimit", Long.TYPE),
    INSTRUMENT_ITEM_getInstrument(InstrumentItem.class, "d", "getInstrument", ItemStack.class);

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

