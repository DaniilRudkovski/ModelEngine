/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 *  net.minecraft.server.level.ChunkMap$TrackedEntity
 *  net.minecraft.server.network.ServerCommonPacketListenerImpl
 *  net.minecraft.server.network.ServerGamePacketListenerImpl
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.Mob
 *  net.minecraft.world.entity.ai.control.MoveControl
 *  net.minecraft.world.level.Level
 */
package com.ticxo.modelengine.v1_21_R6;

import com.ticxo.modelengine.api.utils.ReflectionUtils;
import lombok.Generated;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.network.ServerCommonPacketListenerImpl;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.level.Level;

public enum NMSFields implements ReflectionUtils.ReflectionEnum
{
    ENTITY_dimensions(Entity.class, "bz", "dimensions"),
    ENTITY_eyeHeight(Entity.class, "bA", "eyeHeight"),
    ENTITY_bukkitEntity(Entity.class, "bukkitEntity", "bukkitEntity"),
    LIVING_ENTITY_noJumpDelay(LivingEntity.class, "cu", "noJumpDelay"),
    LIVING_ENTITY_jumping(LivingEntity.class, "bL", "jumping"),
    MOB_lookControl(Mob.class, "cm", "lookControl"),
    MOB_moveControl(Mob.class, "cn", "moveControl"),
    MOB_navigation(Mob.class, "cp", "navigation"),
    MOB_goalSelector(Mob.class, "cq", "goalSelector"),
    MOB_bodyRotationControl(Mob.class, "cy", "bodyRotationControl"),
    SERVER_GAME_PACKET_LISTENER_IMPL_clientIsFloating(ServerGamePacketListenerImpl.class, "I", "clientIsFloating"),
    SERVER_COMMON_PACKET_LISTENER_IMPL_connection(ServerCommonPacketListenerImpl.class, "e", "connection"),
    MOVE_CONTROL_operation(MoveControl.class, "k", "operation"),
    TRACKED_ENTITY_serverEntity(ChunkMap.TrackedEntity.class, "b", "serverEntity"),
    TRACKED_ENTITY_range(ChunkMap.TrackedEntity.class, "d", "range"),
    LEVEL_threadSafeRandom(Level.class, "C", "threadSafeRandom");

    private final Class<?> target;
    private final String obfuscated;
    private final String mapped;

    @Generated
    private NMSFields(Class<?> target, String obfuscated, String mapped) {
        this.target = target;
        this.obfuscated = obfuscated;
        this.mapped = mapped;
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
}

