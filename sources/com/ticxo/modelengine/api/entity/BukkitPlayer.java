/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Player
 */
package com.ticxo.modelengine.api.entity;

import com.ticxo.modelengine.api.entity.BukkitEntity;
import com.ticxo.modelengine.api.entity.data.BukkitEntityData;
import lombok.Generated;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class BukkitPlayer
extends BukkitEntity {
    public BukkitPlayer(Player original) {
        super((Entity)original);
    }

    @Override
    protected BukkitEntityData createEntityData(Entity original) {
        return new BukkitPlayerData(original, this);
    }

    @Override
    public boolean isWalking() {
        return ((BukkitPlayerData)this.getData()).getWalkTick() > 0;
    }

    @Override
    public boolean isJumping() {
        return ((BukkitPlayerData)this.getData()).getJumpTick() > 0;
    }

    @Override
    public boolean isFlying() {
        return ((BukkitPlayerData)this.getData()).isFlying;
    }

    public static class BukkitPlayerData
    extends BukkitEntityData {
        private int walkTick;
        private int jumpTick;
        private boolean isFlying;

        public BukkitPlayerData(Entity entity, BukkitPlayer player) {
            super(entity, player);
        }

        @Override
        public void syncUpdate() {
            super.syncUpdate();
            if (this.walkTick > 0) {
                --this.walkTick;
            }
            if (this.jumpTick > 0 && this.entity.isOnGround()) {
                --this.jumpTick;
            }
            this.isFlying = ((Player)this.entity).isFlying();
        }

        @Generated
        public int getWalkTick() {
            return this.walkTick;
        }

        @Generated
        public void setWalkTick(int walkTick) {
            this.walkTick = walkTick;
        }

        @Generated
        public int getJumpTick() {
            return this.jumpTick;
        }

        @Generated
        public void setJumpTick(int jumpTick) {
            this.jumpTick = jumpTick;
        }
    }
}

