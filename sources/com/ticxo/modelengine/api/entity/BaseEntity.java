/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.HumanEntity
 *  org.bukkit.inventory.EquipmentSlot
 *  org.bukkit.util.BoundingBox
 *  org.jetbrains.annotations.Nullable
 */
package com.ticxo.modelengine.api.entity;

import com.ticxo.modelengine.api.entity.data.IEntityData;
import com.ticxo.modelengine.api.nms.entity.EntityHandler;
import com.ticxo.modelengine.api.nms.entity.HitboxEntity;
import com.ticxo.modelengine.api.nms.entity.wrapper.BodyRotationController;
import com.ticxo.modelengine.api.nms.entity.wrapper.LookController;
import com.ticxo.modelengine.api.nms.entity.wrapper.MoveController;
import com.ticxo.modelengine.api.utils.data.io.DataIO;
import com.ticxo.modelengine.api.utils.data.io.SavedData;
import java.util.List;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.Nullable;

public interface BaseEntity<T>
extends DataIO {
    public T getOriginal();

    public IEntityData getData();

    public void registerData();

    public boolean isVisible();

    public void setVisible(boolean var1);

    public boolean isRemoved();

    public boolean isAlive();

    public boolean isForcedAlive();

    public void setForcedAlive(boolean var1);

    public int getEntityId();

    public UUID getUUID();

    default public Location getLocation() {
        return this.getData().getLocation();
    }

    default public List<Entity> getPassengers() {
        return this.getData().getPassengers();
    }

    public double getMaxStepHeight();

    public void setMaxStepHeight(double var1);

    public int getRenderRadius();

    public void setRenderRadius(int var1);

    public void setCollidableWith(Entity var1, boolean var2);

    public BodyRotationController getBodyRotationController();

    public MoveController getMoveController();

    public LookController getLookController();

    public boolean isGlowing();

    public int getGlowColor();

    public boolean isOnFire();

    public boolean hurt(@Nullable HumanEntity var1, Object var2, float var3);

    public boolean hurt(HitboxEntity var1, @Nullable HumanEntity var2, Object var3, float var4);

    public EntityHandler.InteractionResult interact(HumanEntity var1, EquipmentSlot var2);

    public EntityHandler.InteractionResult interact(HitboxEntity var1, HumanEntity var2, EquipmentSlot var3);

    public float getYRot();

    public float getYHeadRot();

    public float getXHeadRot();

    public float getYBodyRot();

    public boolean isWalking();

    public boolean isStrafing();

    public boolean isJumping();

    public boolean isFlying();

    public float getHealth();

    public float getMaxHealth();

    public BoundingBox getBoundingBox();

    @Override
    default public void save(SavedData data) {
        data.putInt("render_radius", this.getRenderRadius());
        data.putDouble("step", this.getMaxStepHeight());
        this.getData().save().ifPresent(entityData -> data.putData("entity_data", (SavedData)entityData));
        this.getBodyRotationController().save().ifPresent(controllerData -> data.putData("body_rotation", (SavedData)controllerData));
    }

    @Override
    default public void load(SavedData data) {
        this.setRenderRadius(data.getInt("render_radius"));
        this.setMaxStepHeight(data.getDouble("step"));
        data.getData("entity_data").ifPresent(entityData -> this.getData().load((SavedData)entityData));
        data.getData("body_rotation").ifPresent(controllerData -> this.getBodyRotationController().load((SavedData)controllerData));
    }
}

