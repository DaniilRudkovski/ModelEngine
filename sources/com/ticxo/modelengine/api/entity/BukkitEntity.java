/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 *  org.bukkit.Bukkit
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.HumanEntity
 *  org.bukkit.entity.LivingEntity
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Event
 *  org.bukkit.event.player.PlayerInteractAtEntityEvent
 *  org.bukkit.inventory.EquipmentSlot
 *  org.bukkit.metadata.FixedMetadataValue
 *  org.bukkit.metadata.MetadataValue
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.util.BoundingBox
 *  org.bukkit.util.Vector
 *  org.jetbrains.annotations.Nullable
 */
package com.ticxo.modelengine.api.entity;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.entity.BaseEntity;
import com.ticxo.modelengine.api.entity.data.BukkitEntityData;
import com.ticxo.modelengine.api.nms.entity.EntityHandler;
import com.ticxo.modelengine.api.nms.entity.HitboxEntity;
import com.ticxo.modelengine.api.nms.entity.wrapper.BodyRotationController;
import com.ticxo.modelengine.api.nms.entity.wrapper.LookController;
import com.ticxo.modelengine.api.nms.entity.wrapper.MoveController;
import com.ticxo.modelengine.api.nms.impl.DefaultBodyRotationController;
import com.ticxo.modelengine.api.nms.impl.EmptyLookController;
import com.ticxo.modelengine.api.nms.impl.EmptyMoveController;
import com.ticxo.modelengine.api.utils.data.io.SavedData;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.Generated;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

public class BukkitEntity
implements BaseEntity<Entity> {
    protected final EntityHandler entityHandler = ModelEngineAPI.getNMSHandler().getEntityHandler();
    protected final Entity original;
    protected final BukkitEntityData data;
    protected final BodyRotationController bodyRotationController;
    protected final MoveController moveController;
    protected final LookController lookController;
    protected boolean isVisible = true;

    public BukkitEntity(Entity original) {
        this.original = original;
        this.data = this.createEntityData(original);
        this.bodyRotationController = this.entityHandler.wrapBodyRotationControl(original, () -> new DefaultBodyRotationController(this));
        this.moveController = this.entityHandler.wrapMoveController(original, EmptyMoveController::new);
        this.lookController = this.entityHandler.wrapLookController(original, EmptyLookController::new);
        this.entityHandler.wrapNavigation(original);
    }

    protected BukkitEntityData createEntityData(Entity original) {
        return new BukkitEntityData(original, this);
    }

    @Override
    public void registerData() {
        ModelEngineAPI.getAPI().getDataTrackers().execute(this.getUUID(), (uuid, tracker) -> tracker.putEntityData((UUID)uuid, this.data));
    }

    @Override
    public void setVisible(boolean flag) {
        block4: {
            block3: {
                if (this.isVisible == flag) {
                    return;
                }
                this.isVisible = flag;
                if (!this.isVisible) break block3;
                for (UUID player : this.data.getTracking().keySet()) {
                    this.entityHandler.forceSpawn(this, Bukkit.getPlayer((UUID)player));
                }
                break block4;
            }
            if (!ModelEngineAPI.getPivotOverrideRegistry().get(this.original.getUniqueId()).isEmpty()) break block4;
            for (UUID player : this.data.getTracking().keySet()) {
                if (player.equals(this.original.getUniqueId())) continue;
                this.entityHandler.forceDespawn(this, Bukkit.getPlayer((UUID)player));
            }
        }
    }

    @Override
    public boolean isRemoved() {
        return this.entityHandler.isRemoved(this.original);
    }

    @Override
    public boolean isAlive() {
        return this.data.isEntityValid();
    }

    @Override
    public boolean isForcedAlive() {
        return this.data.isForcedAlive();
    }

    @Override
    public void setForcedAlive(boolean flag) {
        this.data.setForcedAlive(flag);
    }

    @Override
    public int getEntityId() {
        return this.original.getEntityId();
    }

    @Override
    public UUID getUUID() {
        return this.original.getUniqueId();
    }

    @Override
    public double getMaxStepHeight() {
        return this.entityHandler.getStepHeight(this.original);
    }

    @Override
    public void setMaxStepHeight(double stepHeight) {
        this.entityHandler.setStepHeight(this.original, stepHeight);
    }

    @Override
    public int getRenderRadius() {
        return this.data.getTracked().getBaseRange();
    }

    @Override
    public void setRenderRadius(int radius) {
        this.data.getTracked().setBaseRange(radius);
    }

    @Override
    public void setCollidableWith(Entity entity, boolean flag) {
        Entity entity2 = this.original;
        if (!(entity2 instanceof LivingEntity)) {
            return;
        }
        LivingEntity livingEntity = (LivingEntity)entity2;
        Set set = livingEntity.getCollidableExemptions();
        if (flag) {
            set.remove(entity.getUniqueId());
        } else {
            set.add(entity.getUniqueId());
        }
    }

    @Override
    public boolean isGlowing() {
        return this.original.isGlowing();
    }

    @Override
    public int getGlowColor() {
        return this.data.getGlowColor();
    }

    @Override
    public boolean isOnFire() {
        return this.original.isVisualFire() || this.original.getFireTicks() > 0;
    }

    @Override
    public boolean hurt(@Nullable HumanEntity player, Object nmsDamageCause, float damage) {
        return this.entityHandler.hurt(this.original, nmsDamageCause, damage);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean hurt(HitboxEntity hitbox, @Nullable HumanEntity player, Object nmsDamageCause, float damage) {
        if (this.original.hasMetadata("skill-damage")) {
            List metadataList = this.original.getMetadata("skill-damage");
            MetadataValue metadata = (MetadataValue)metadataList.get(0);
            this.original.setMetadata("skill-damage", metadata);
            this.original.setMetadata("hitbox", (MetadataValue)new FixedMetadataValue((Plugin)ModelEngineAPI.getAPI(), (Object)hitbox.getBone().getBoneId()));
            try {
                boolean bl = this.entityHandler.hurt(this.original, nmsDamageCause, damage);
                return bl;
            }
            finally {
                this.original.removeMetadata("hitbox", (Plugin)ModelEngineAPI.getAPI());
                this.original.removeMetadata("skill-damage", metadata.getOwningPlugin());
            }
        }
        this.original.setMetadata("hitbox", (MetadataValue)new FixedMetadataValue((Plugin)ModelEngineAPI.getAPI(), (Object)hitbox.getBone().getBoneId()));
        try {
            boolean bl = this.entityHandler.hurt(this.original, nmsDamageCause, damage);
            return bl;
        }
        finally {
            this.original.removeMetadata("hitbox", (Plugin)ModelEngineAPI.getAPI());
        }
    }

    @Override
    public EntityHandler.InteractionResult interact(HumanEntity player, EquipmentSlot slot) {
        if (player instanceof Player) {
            Player serverPlayer = (Player)player;
            PlayerInteractAtEntityEvent event = new PlayerInteractAtEntityEvent(serverPlayer, this.original, new Vector(0, 0, 0), slot);
            Bukkit.getPluginManager().callEvent((Event)event);
            if (event.isCancelled()) {
                return EntityHandler.InteractionResult.FAIL;
            }
        }
        return this.entityHandler.interact(this.original, player, slot);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public EntityHandler.InteractionResult interact(HitboxEntity hitbox, HumanEntity player, EquipmentSlot slot) {
        this.original.setMetadata("hitbox", (MetadataValue)new FixedMetadataValue((Plugin)ModelEngineAPI.getAPI(), (Object)hitbox.getBone().getBoneId()));
        try {
            EntityHandler.InteractionResult interactionResult = this.interact(player, slot);
            return interactionResult;
        }
        finally {
            this.original.removeMetadata("hitbox", (Plugin)ModelEngineAPI.getAPI());
        }
    }

    @Override
    public float getYRot() {
        return this.entityHandler.getYRot(this.original);
    }

    @Override
    public float getYHeadRot() {
        return this.entityHandler.getYHeadRot(this.original);
    }

    @Override
    public float getXHeadRot() {
        return this.entityHandler.getXHeadRot(this.original);
    }

    @Override
    public float getYBodyRot() {
        return this.entityHandler.getYBodyRot(this.original);
    }

    @Override
    public boolean isWalking() {
        return this.entityHandler.isWalking(this.original);
    }

    @Override
    public boolean isStrafing() {
        return this.entityHandler.isStrafing(this.original);
    }

    @Override
    public boolean isJumping() {
        return this.entityHandler.isJumping(this.original);
    }

    @Override
    public boolean isFlying() {
        return this.entityHandler.isFlying(this.original);
    }

    @Override
    public float getHealth() {
        return this.entityHandler.getHealth(this.original);
    }

    @Override
    public float getMaxHealth() {
        return this.entityHandler.getMaxHealth(this.original);
    }

    @Override
    public BoundingBox getBoundingBox() {
        return this.original.getBoundingBox();
    }

    @Override
    public void save(SavedData data) {
        BaseEntity.super.save(data);
        Entity entity = this.original;
        if (entity instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity)entity;
            data.putList("collide_exemption", livingEntity.getCollidableExemptions());
        }
    }

    @Override
    public void load(SavedData data) {
        BaseEntity.super.load(data);
        Entity entity = this.original;
        if (entity instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity)entity;
            livingEntity.getCollidableExemptions().addAll(data.getList("collide_exemption"));
        }
    }

    @Override
    @Generated
    public Entity getOriginal() {
        return this.original;
    }

    @Override
    @Generated
    public BukkitEntityData getData() {
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

    @Override
    @Generated
    public boolean isVisible() {
        return this.isVisible;
    }
}

