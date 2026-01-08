/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 *  org.bukkit.Location
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.HumanEntity
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.EquipmentSlot
 *  org.bukkit.util.BoundingBox
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Vector3f
 */
package com.ticxo.modelengine.api.nms.entity;

import com.ticxo.modelengine.api.entity.BaseEntity;
import com.ticxo.modelengine.api.entity.Hitbox;
import com.ticxo.modelengine.api.interaction.DynamicHitbox;
import com.ticxo.modelengine.api.model.bone.ModelBone;
import com.ticxo.modelengine.api.model.bone.type.SubHitbox;
import com.ticxo.modelengine.api.nms.entity.HitboxEntity;
import com.ticxo.modelengine.api.nms.entity.wrapper.BodyRotationController;
import com.ticxo.modelengine.api.nms.entity.wrapper.LookController;
import com.ticxo.modelengine.api.nms.entity.wrapper.MoveController;
import com.ticxo.modelengine.api.nms.entity.wrapper.TrackedEntity;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;
import lombok.Generated;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public interface EntityHandler {
    public static final int ON_MIN_X = 1;
    public static final int ON_MAX_X = 2;
    public static final int ON_MIN_Y = 4;
    public static final int ON_MAX_Y = 8;
    public static final int ON_MIN_Z = 16;
    public static final int ON_MAX_Z = 32;

    public static Set<Point> getPoints(byte face) {
        HashSet<Point> set = new HashSet<Point>();
        if ((face & 1) == 1) {
            set.add(Point.CORNER_A);
            set.add(Point.CORNER_E);
            set.add(Point.CORNER_C);
            set.add(Point.CORNER_G);
        }
        if ((face & 4) == 4) {
            set.add(Point.CORNER_A);
            set.add(Point.CORNER_E);
            set.add(Point.CORNER_B);
            set.add(Point.CORNER_F);
        }
        if ((face & 0x10) == 16) {
            set.add(Point.CORNER_A);
            set.add(Point.CORNER_C);
            set.add(Point.CORNER_B);
            set.add(Point.CORNER_D);
        }
        if ((face & 2) == 2) {
            set.add(Point.CORNER_B);
            set.add(Point.CORNER_F);
            set.add(Point.CORNER_D);
            set.add(Point.CORNER_H);
        }
        if ((face & 8) == 8) {
            set.add(Point.CORNER_C);
            set.add(Point.CORNER_G);
            set.add(Point.CORNER_D);
            set.add(Point.CORNER_H);
        }
        if ((face & 0x20) == 32) {
            set.add(Point.CORNER_E);
            set.add(Point.CORNER_G);
            set.add(Point.CORNER_F);
            set.add(Point.CORNER_H);
        }
        return set;
    }

    public void updateConfig();

    public int getNextEntityId();

    public void setHitbox(Entity var1, Hitbox var2);

    public void setStepHeight(Entity var1, double var2);

    public double getStepHeight(Entity var1);

    public void setPosition(Entity var1, double var2, double var4, double var6);

    public void movePassenger(Entity var1, double var2, double var4, double var6);

    public void forceSpawn(BaseEntity<?> var1, Player var2);

    default public void forceSpawn(Entity entity) {
        TrackedEntity tracked = this.wrapTrackedEntity(entity);
        if (tracked != null) {
            tracked.broadcastSpawn();
        }
    }

    public void forceDespawn(BaseEntity<?> var1, Player var2);

    default public void forceDespawn(Entity entity) {
        TrackedEntity tracked = this.wrapTrackedEntity(entity);
        if (tracked != null) {
            tracked.broadcastRemove();
        }
    }

    public void setForcedInvisible(Entity var1, boolean var2);

    default public boolean isForcedInvisible(Entity entity) {
        return this.isForcedInvisible(entity.getUniqueId());
    }

    public boolean isForcedInvisible(UUID var1);

    public BodyRotationController wrapBodyRotationControl(Entity var1, Supplier<BodyRotationController> var2);

    public MoveController wrapMoveController(Entity var1, Supplier<MoveController> var2);

    public LookController wrapLookController(Entity var1, Supplier<LookController> var2);

    public void wrapNavigation(Entity var1);

    public HitboxEntity createHitbox(Location var1, ModelBone var2, SubHitbox var3);

    @Nullable
    public HitboxEntity castHitbox(Entity var1);

    public boolean hurt(Entity var1, Object var2, float var3);

    public InteractionResult interact(Entity var1, HumanEntity var2, EquipmentSlot var3);

    public void spawnDynamicHitbox(DynamicHitbox var1);

    public void updateDynamicHitbox(DynamicHitbox var1);

    public void destroyDynamicHitbox(DynamicHitbox var1);

    public void forceUseItem(Player var1, EquipmentSlot var2);

    public float getYRot(Entity var1);

    public float getYHeadRot(Entity var1);

    public float getXHeadRot(Entity var1);

    public float getYBodyRot(Entity var1);

    public void setYRot(Entity var1, float var2);

    public void setYHeadRot(Entity var1, float var2);

    public void setXHeadRot(Entity var1, float var2);

    public void setYBodyRot(Entity var1, float var2);

    public void move(Entity var1, double var2, double var4, double var6);

    public boolean isWalking(Entity var1);

    public boolean isStrafing(Entity var1);

    public boolean isJumping(Entity var1);

    public boolean isFlying(Entity var1);

    public float getHealth(Entity var1);

    public float getMaxHealth(Entity var1);

    default public Vector3f getPivotOffset(Entity entity) {
        return null;
    }

    public boolean isRemoved(Entity var1);

    public int getGlowColor(Entity var1);

    public void setDeathTick(Entity var1, int var2);

    public TrackedEntity wrapTrackedEntity(Entity var1);

    public boolean shouldCull(Player var1, Location var2, BoundingBox var3);

    public static enum Point {
        CORNER_A(0.0f, 0.0f, 0.0f),
        CORNER_B(1.0f, 0.0f, 0.0f),
        CORNER_C(0.0f, 1.0f, 0.0f),
        CORNER_D(1.0f, 1.0f, 0.0f),
        CORNER_E(0.0f, 0.0f, 1.0f),
        CORNER_F(1.0f, 0.0f, 1.0f),
        CORNER_G(0.0f, 1.0f, 1.0f),
        CORNER_H(1.0f, 1.0f, 1.0f),
        FACE_NX(0.0f, 0.5f, 0.5f),
        FACE_PX(1.0f, 0.5f, 0.5f),
        FACE_NY(0.5f, 0.0f, 0.5f),
        FACE_PY(0.5f, 1.0f, 0.5f),
        FACE_NZ(0.5f, 0.5f, 0.0f),
        FACE_PZ(0.5f, 0.5f, 1.0f);

        public final float x;
        public final float y;
        public final float z;

        @Generated
        private Point(float x, float y, float z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }

    public static enum InteractionResult {
        SUCCESS,
        SUCCESS_NO_ITEM_USED,
        CONSUME,
        CONSUME_PARTIAL,
        PASS,
        FAIL;

    }

    public static enum BoxRelToCam {
        INSIDE,
        POSITIVE,
        NEGATIVE;


        public static BoxRelToCam from(int min, int max, int pos) {
            if (max > pos && min > pos) {
                return POSITIVE;
            }
            if (min < pos && max < pos) {
                return NEGATIVE;
            }
            return INSIDE;
        }
    }
}

