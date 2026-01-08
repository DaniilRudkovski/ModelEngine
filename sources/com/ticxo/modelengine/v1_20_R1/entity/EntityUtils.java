/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.PacketDataSerializer
 *  net.minecraft.network.syncher.DataWatcherRegistry
 *  net.minecraft.network.syncher.DataWatcherSerializer
 *  net.minecraft.world.entity.Entity
 *  org.bukkit.craftbukkit.v1_20_R1.entity.CraftEntity
 *  org.bukkit.entity.Entity
 */
package com.ticxo.modelengine.v1_20_R1.entity;

import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.network.syncher.DataWatcherSerializer;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftEntity;
import org.bukkit.entity.Entity;

public class EntityUtils {
    public static net.minecraft.world.entity.Entity nms(Entity entity) {
        return ((CraftEntity)entity).getHandle();
    }

    public static <T> void writeData(PacketDataSerializer byteBuf, int id, DataWatcherSerializer<T> serializer, T val) {
        int serializedId = DataWatcherRegistry.b(serializer);
        byteBuf.writeByte(id);
        byteBuf.d(serializedId);
        serializer.a(byteBuf, val);
    }

    public static void writeDataUnsafe(PacketDataSerializer byteBuf, int id, DataWatcherSerializer serializer, Object val) {
        int serializedId = DataWatcherRegistry.b((DataWatcherSerializer)serializer);
        byteBuf.writeByte(id);
        byteBuf.d(serializedId);
        serializer.a(byteBuf, val);
    }
}

