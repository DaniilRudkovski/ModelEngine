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
 */
package com.ticxo.modelengine.api.entity;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.entity.BaseEntity;
import com.ticxo.modelengine.api.entity.Hitbox;
import com.ticxo.modelengine.api.entity.data.DummyEntityData;
import com.ticxo.modelengine.api.nms.entity.EntityHandler;
import com.ticxo.modelengine.api.nms.entity.HitboxEntity;
import com.ticxo.modelengine.api.nms.entity.wrapper.BodyRotationController;
import com.ticxo.modelengine.api.nms.entity.wrapper.LookController;
import com.ticxo.modelengine.api.nms.entity.wrapper.MoveController;
import com.ticxo.modelengine.api.nms.impl.DefaultBodyRotationController;
import com.ticxo.modelengine.api.nms.impl.EmptyLookController;
import com.ticxo.modelengine.api.nms.impl.EmptyMoveController;
import java.util.UUID;
import lombok.Generated;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.Nullable;

public class Dummy<T>
implements BaseEntity<T> {
    protected final int entityId;
    protected final UUID uuid;
    protected final T original;
    protected final DummyEntityData<T> data;
    protected final BodyRotationController bodyRotationController;
    protected final MoveController moveController;
    protected final LookController lookController;
    protected boolean detectingPlayers = true;
    protected boolean isRemoved;
    protected boolean isWalking;
    protected boolean isStrafing;
    protected boolean isJumping;
    protected boolean isFlying;
    protected boolean isGlowing;
    protected int glowColor;
    protected boolean isOnFire;
    protected float yHeadRot;
    protected float xHeadRot;
    protected float yBodyRot;
    protected Hitbox hitbox = new Hitbox(0.0, 0.0, 0.0, 0.0);
    protected BoundingBox boundingBox = new BoundingBox();

    public Dummy() {
        this(null);
    }

    public Dummy(T original) {
        this(UUID.randomUUID(), original);
    }

    public Dummy(UUID uuid, T original) {
        this(ModelEngineAPI.getEntityHandler().getNextEntityId(), uuid, original);
    }

    public Dummy(int id, UUID uuid, T original) {
        this.entityId = id;
        this.uuid = uuid;
        this.original = original;
        this.data = new DummyEntityData(this);
        this.bodyRotationController = new DefaultBodyRotationController(this);
        this.moveController = new EmptyMoveController();
        this.lookController = new EmptyLookController();
    }

    @Override
    public boolean isAlive() {
        return !this.isRemoved;
    }

    @Override
    public UUID getUUID() {
        return this.uuid;
    }

    @Override
    public int getRenderRadius() {
        return this.data.getRenderRadius();
    }

    @Override
    public void setRenderRadius(int radius) {
        this.data.setRenderRadius(radius);
    }

    @Override
    public float getYRot() {
        return this.yHeadRot;
    }

    @Override
    public float getHealth() {
        return 20.0f;
    }

    @Override
    public float getMaxHealth() {
        return 20.0f;
    }

    public void setLocation(Location location) {
        this.data.setLocation(location);
        this.boundingBox = this.hitbox.createBoundingBox(location.toVector());
    }

    public void syncLocation(Location location) {
        this.setLocation(location);
        this.setYBodyRot(location.getYaw());
        this.setYHeadRot(location.getYaw());
        this.setXHeadRot(location.getPitch());
    }

    public void setForceViewing(Player player, boolean flag) {
        if (flag) {
            this.setForceHidden(player, false);
            this.data.getTracked().addForcedPairing(player.getUniqueId());
        } else {
            this.data.getTracked().removeForcedPairing(player.getUniqueId());
        }
    }

    public void setForceHidden(Player player, boolean flag) {
        if (flag) {
            this.setForceViewing(player, false);
            this.data.getTracked().addForcedHidden(player.getUniqueId());
        } else {
            this.data.getTracked().removeForcedHidden(player.getUniqueId());
        }
    }

    @Override
    public void registerData() {
        ModelEngineAPI.getAPI().getDataTrackers().execute(this.getUUID(), (uuid, tracker) -> tracker.putEntityData((UUID)uuid, this.data));
    }

    @Override
    public boolean isVisible() {
        return true;
    }

    @Override
    public void setVisible(boolean flag) {
    }

    @Override
    public boolean isForcedAlive() {
        return false;
    }

    @Override
    public void setForcedAlive(boolean flag) {
    }

    @Override
    public double getMaxStepHeight() {
        return 0.0;
    }

    @Override
    public void setMaxStepHeight(double stepHeight) {
    }

    @Override
    public void setCollidableWith(Entity entity, boolean flag) {
    }

    @Override
    public boolean hurt(@Nullable HumanEntity player, Object nmsDamageCause, float damage) {
        return false;
    }

    @Override
    public boolean hurt(HitboxEntity hitbox, @Nullable HumanEntity player, Object nmsDamageCause, float damage) {
        return false;
    }

    @Override
    public EntityHandler.InteractionResult interact(HumanEntity player, EquipmentSlot slot) {
        return EntityHandler.InteractionResult.SUCCESS_NO_ITEM_USED;
    }

    @Override
    public EntityHandler.InteractionResult interact(HitboxEntity hitbox, HumanEntity player, EquipmentSlot slot) {
        return EntityHandler.InteractionResult.SUCCESS_NO_ITEM_USED;
    }

    @Override
    @Generated
    public int getEntityId() {
        return this.entityId;
    }

    @Override
    @Generated
    public T getOriginal() {
        return this.original;
    }

    @Override
    @Generated
    public DummyEntityData<T> getData() {
        return this.data;
    }

    @Override
    @Generated
    public BodyRotationController getBodyRotationController() {
        return this.bodyRotationController;
    }

    @Override
    @Generated
    public MoveController getMoveController() {
        return this.moveController;
    }

    @Override
    @Generated
    public LookController getLookController() {
        return this.lookController;
    }

    @Generated
    public boolean isDetectingPlayers() {
        return this.detectingPlayers;
    }

    @Override
    @Generated
    public boolean isRemoved() {
        return this.isRemoved;
    }

    @Override
    @Generated
    public boolean isWalking() {
        return this.isWalking;
    }

    @Override
    @Generated
    public boolean isStrafing() {
        return this.isStrafing;
    }

    @Override
    @Generated
    public boolean isJumping() {
        return this.isJumping;
    }

    @Override
    @Generated
    public boolean isFlying() {
        return this.isFlying;
    }

    @Override
    @Generated
    public boolean isGlowing() {
        return this.isGlowing;
    }

    @Override
    @Generated
    public int getGlowColor() {
        return this.glowColor;
    }

    @Override
    @Generated
    public boolean isOnFire() {
        return this.isOnFire;
    }

    @Override
    @Generated
    public float getYHeadRot() {
        return this.yHeadRot;
    }

    @Override
    @Generated
    public float getXHeadRot() {
        return this.xHeadRot;
    }

    @Override
    @Generated
    public float getYBodyRot() {
        return this.yBodyRot;
    }

    @Generated
    public Hitbox getHitbox() {
        return this.hitbox;
    }

    @Override
    @Generated
    public BoundingBox getBoundingBox() {
        return this.boundingBox;
    }

    @Generated
    public void setDetectingPlayers(boolean detectingPlayers) {
        this.detectingPlayers = detectingPlayers;
    }

    @Generated
    public void setRemoved(boolean isRemoved) {
        this.isRemoved = isRemoved;
    }

    @Generated
    public void setWalking(boolean isWalking) {
        this.isWalking = isWalking;
    }

    @Generated
    public void setStrafing(boolean isStrafing) {
        this.isStrafing = isStrafing;
    }

    @Generated
    public void setJumping(boolean isJumping) {
        this.isJumping = isJumping;
    }

    @Generated
    public void setFlying(boolean isFlying) {
        this.isFlying = isFlying;
    }

    @Generated
    public void setGlowing(boolean isGlowing) {
        this.isGlowing = isGlowing;
    }

    @Generated
    public void setGlowColor(int glowColor) {
        this.glowColor = glowColor;
    }

    @Generated
    public void setOnFire(boolean isOnFire) {
        this.isOnFire = isOnFire;
    }

    @Generated
    public void setYHeadRot(float yHeadRot) {
        this.yHeadRot = yHeadRot;
    }

    @Generated
    public void setXHeadRot(float xHeadRot) {
        this.xHeadRot = xHeadRot;
    }

    @Generated
    public void setYBodyRot(float yBodyRot) {
        this.yBodyRot = yBodyRot;
    }

    @Generated
    public void setHitbox(Hitbox hitbox) {
        this.hitbox = hitbox;
    }

    @Generated
    public void setBoundingBox(BoundingBox boundingBox) {
        this.boundingBox = boundingBox;
    }
}

