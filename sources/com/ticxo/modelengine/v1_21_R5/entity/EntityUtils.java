/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.syncher.EntityDataSerializers
 *  net.minecraft.network.syncher.SynchedEntityData$DataValue
 *  net.minecraft.world.entity.Entity
 *  org.bukkit.craftbukkit.entity.CraftEntity
 *  org.bukkit.entity.Entity
 */
package com.ticxo.modelengine.v1_21_R5.entity;

import java.util.List;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.entity.Entity;

public class EntityUtils {
    public static final List<SynchedEntityData.DataValue<?>> DEFAULT_AREA_EFFECT_CLOUD_DATA = List.of(new SynchedEntityData.DataValue(0, EntityDataSerializers.BYTE, (Object)32), new SynchedEntityData.DataValue(1, EntityDataSerializers.INT, (Object)Integer.MAX_VALUE), new SynchedEntityData.DataValue(8, EntityDataSerializers.FLOAT, (Object)Float.valueOf(0.0f)));
    public static final List<SynchedEntityData.DataValue<?>> DEFAULT_PIVOT_DISPLAY_DATA = List.of(new SynchedEntityData.DataValue(0, EntityDataSerializers.BYTE, (Object)32), new SynchedEntityData.DataValue(1, EntityDataSerializers.INT, (Object)Integer.MAX_VALUE), new SynchedEntityData.DataValue(10, EntityDataSerializers.INT, (Object)1));
    public static final List<SynchedEntityData.DataValue<?>> DEFAULT_SLIME_DATA = List.of(new SynchedEntityData.DataValue(0, EntityDataSerializers.BYTE, (Object)32), new SynchedEntityData.DataValue(1, EntityDataSerializers.INT, (Object)Integer.MAX_VALUE), new SynchedEntityData.DataValue(16, EntityDataSerializers.INT, (Object)2));
    public static final List<SynchedEntityData.DataValue<?>> DEFAULT_BAT_DATA = List.of(new SynchedEntityData.DataValue(0, EntityDataSerializers.BYTE, (Object)32), new SynchedEntityData.DataValue(1, EntityDataSerializers.INT, (Object)Integer.MAX_VALUE));
    public static final List<SynchedEntityData.DataValue<?>> DEFAULT_ARMOR_STAND_DATA = List.of(new SynchedEntityData.DataValue(0, EntityDataSerializers.BYTE, (Object)32), new SynchedEntityData.DataValue(1, EntityDataSerializers.INT, (Object)Integer.MAX_VALUE), new SynchedEntityData.DataValue(15, EntityDataSerializers.BYTE, (Object)16));

    public static net.minecraft.world.entity.Entity nms(Entity entity) {
        return ((CraftEntity)entity).getHandle();
    }
}

