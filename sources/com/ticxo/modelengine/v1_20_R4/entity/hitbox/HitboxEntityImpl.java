/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 *  net.minecraft.core.NonNullList
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.damagesource.DamageSource
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.Entity$RemovalReason
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.EquipmentSlot
 *  net.minecraft.world.entity.EquipmentSlot$Type
 *  net.minecraft.world.entity.HumanoidArm
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 *  org.bukkit.Bukkit
 *  org.bukkit.Location
 *  org.bukkit.World
 *  org.bukkit.craftbukkit.entity.CraftEntity
 *  org.bukkit.craftbukkit.entity.CraftHumanEntity
 *  org.bukkit.craftbukkit.entity.CraftLivingEntity
 *  org.bukkit.craftbukkit.util.CraftLocation
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.HumanEntity
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Event
 *  org.bukkit.event.player.PlayerInteractAtEntityEvent
 *  org.bukkit.inventory.EquipmentSlot
 *  org.bukkit.util.Vector
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Quaternionf
 *  org.joml.Vector3f
 */
package com.ticxo.modelengine.v1_20_R4.entity.hitbox;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.model.bone.ModelBone;
import com.ticxo.modelengine.api.model.bone.type.SubHitbox;
import com.ticxo.modelengine.api.nms.entity.EntityHandler;
import com.ticxo.modelengine.api.nms.entity.HitboxEntity;
import com.ticxo.modelengine.api.utils.ReflectionUtils;
import com.ticxo.modelengine.api.utils.math.OrientedBoundingBox;
import com.ticxo.modelengine.v1_20_R4.NMSFields;
import com.ticxo.modelengine.v1_20_R4.entity.EntityUtils;
import com.ticxo.modelengine.v1_20_R4.entity.hitbox.OBB;
import java.util.UUID;
import lombok.Generated;
import net.minecraft.core.NonNullList;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.util.CraftLocation;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class HitboxEntityImpl
extends LivingEntity
implements HitboxEntity {
    private final NonNullList<ItemStack> handItems = NonNullList.withSize((int)2, (Object)ItemStack.EMPTY);
    private final NonNullList<ItemStack> armorItems = NonNullList.withSize((int)4, (Object)ItemStack.EMPTY);
    private final ModelBone bone;
    private final SubHitbox subHitbox;
    private OBB obb;
    private Vector3f location;
    private boolean markRemoved;

    public HitboxEntityImpl(Level world, @NotNull ModelBone bone, @NotNull SubHitbox subHitbox) {
        super(EntityType.SILVERFISH, world);
        this.bone = bone;
        this.subHitbox = subHitbox;
        this.setInvulnerable(true);
        this.setNoGravity(true);
        this.noPhysics = true;
    }

    public HumanoidArm getMainArm() {
        return HumanoidArm.RIGHT;
    }

    public Iterable<ItemStack> getArmorSlots() {
        return this.armorItems;
    }

    public ItemStack getItemBySlot(EquipmentSlot slot) {
        return switch (slot.getType()) {
            default -> throw new MatchException(null, null);
            case EquipmentSlot.Type.HAND -> (ItemStack)this.handItems.get(slot.getIndex());
            case EquipmentSlot.Type.ARMOR -> (ItemStack)this.armorItems.get(slot.getIndex());
            case EquipmentSlot.Type.BODY -> ItemStack.EMPTY;
        };
    }

    public void setItemSlot(EquipmentSlot slot, ItemStack stack) {
        this.verifyEquippedItem(stack);
        switch (slot.getType()) {
            case HAND: {
                this.onEquipItem(slot, (ItemStack)this.handItems.set(slot.getIndex(), (Object)stack), stack);
                break;
            }
            case ARMOR: {
                this.onEquipItem(slot, (ItemStack)this.armorItems.set(slot.getIndex(), (Object)stack), stack);
            }
        }
    }

    public boolean isInvisible() {
        return true;
    }

    public void push(net.minecraft.world.entity.Entity entity) {
    }

    @NotNull
    protected AABB makeBoundingBox() {
        if (this.subHitbox == null) {
            return super.makeBoundingBox();
        }
        Vector3f pos = this.subHitbox.getLocation();
        Vector3f dim = this.subHitbox.getDimension();
        if (this.subHitbox.isOBB()) {
            float halfX = dim.x * 0.5f;
            float halfY = dim.y * 0.5f;
            float halfZ = dim.z * 0.5f;
            Quaternionf rot = this.subHitbox.getRotation();
            float yaw = this.subHitbox.getYaw();
            this.obb = new OBB(pos.x - halfX, pos.y - halfY, pos.z - halfZ, pos.x + halfX, pos.y + halfY, pos.z + halfZ, rot, yaw);
            return this.obb;
        }
        float halfX = dim.x * 0.5f;
        float halfZ = dim.z * 0.5f;
        return new AABB((double)(pos.x - halfX), (double)pos.y, (double)(pos.z - halfZ), (double)(pos.x + halfX), (double)(pos.y + dim.y), (double)(pos.z + halfZ));
    }

    public void tick() {
        if (this.markRemoved) {
            this.discard();
            return;
        }
        super.tick();
        if (this.bone == null || this.subHitbox == null) {
            this.discard();
            return;
        }
        if (!this.bone.getActiveModel().getModeledEntity().getBase().isAlive()) {
            this.discard();
            return;
        }
        if (!this.location.isFinite()) {
            return;
        }
        Vec3 vec = new Vec3(this.location);
        this.setPos(vec);
        for (Entity entity : this.subHitbox.getBoundEntities().values()) {
            EntityUtils.nms(entity).setPos(vec);
        }
    }

    public boolean fireImmune() {
        return true;
    }

    protected void pushEntities() {
    }

    protected void doPush(net.minecraft.world.entity.Entity entity) {
    }

    public boolean isPushable() {
        return false;
    }

    public boolean hurt(DamageSource source, float amount) {
        CraftHumanEntity craftHumanEntity;
        for (Entity entity : this.subHitbox.getBoundEntities().values()) {
            EntityUtils.nms(entity).hurt(source, amount);
        }
        if (this.subHitbox.getDamageMultiplier() <= 1.0E-5f) {
            return false;
        }
        net.minecraft.world.entity.Entity entity = source.getEntity();
        if (entity instanceof Player) {
            Player player = (Player)entity;
            craftHumanEntity = player.getBukkitEntity();
        } else {
            craftHumanEntity = null;
        }
        CraftHumanEntity cause = craftHumanEntity;
        return this.bone.getActiveModel().getModeledEntity().getBase().hurt(this, (HumanEntity)cause, source, amount * this.subHitbox.getDamageMultiplier());
    }

    @NotNull
    public InteractionResult interact(Player player, InteractionHand hand) {
        CraftHumanEntity craftHumanEntity = player.getBukkitEntity();
        if (craftHumanEntity instanceof org.bukkit.entity.Player) {
            org.bukkit.entity.Player craftPlayer = (org.bukkit.entity.Player)craftHumanEntity;
            for (Entity entity : this.subHitbox.getBoundEntities().values()) {
                PlayerInteractAtEntityEvent event = new PlayerInteractAtEntityEvent(craftPlayer, entity, new Vector(0, 0, 0), hand == InteractionHand.OFF_HAND ? org.bukkit.inventory.EquipmentSlot.OFF_HAND : org.bukkit.inventory.EquipmentSlot.HAND);
                Bukkit.getPluginManager().callEvent((Event)event);
                if (event.isCancelled()) continue;
                EntityUtils.nms(entity).interact(player, hand);
            }
        }
        if (this.subHitbox.getDamageMultiplier() <= 1.0E-5f) {
            return InteractionResult.PASS;
        }
        EntityHandler.InteractionResult result = this.bone.getActiveModel().getModeledEntity().getBase().interact(this, (HumanEntity)player.getBukkitEntity(), hand == InteractionHand.MAIN_HAND ? org.bukkit.inventory.EquipmentSlot.HAND : org.bukkit.inventory.EquipmentSlot.OFF_HAND);
        return switch (result) {
            default -> throw new MatchException(null, null);
            case EntityHandler.InteractionResult.SUCCESS -> InteractionResult.SUCCESS;
            case EntityHandler.InteractionResult.SUCCESS_NO_ITEM_USED -> InteractionResult.SUCCESS_NO_ITEM_USED;
            case EntityHandler.InteractionResult.CONSUME -> InteractionResult.CONSUME;
            case EntityHandler.InteractionResult.CONSUME_PARTIAL -> InteractionResult.CONSUME_PARTIAL;
            case EntityHandler.InteractionResult.PASS -> InteractionResult.PASS;
            case EntityHandler.InteractionResult.FAIL -> InteractionResult.FAIL;
        };
    }

    public boolean shouldBeSaved() {
        return false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public CraftEntity getBukkitEntity() {
        CraftEntity bukkitEntity = this.getBukkitEntityR();
        if (bukkitEntity == null) {
            HitboxEntityImpl hitboxEntityImpl = this;
            synchronized (hitboxEntityImpl) {
                bukkitEntity = new CraftLivingEntity(this.level().getCraftServer(), (LivingEntity)this);
                this.setBukkitEntityR(bukkitEntity);
                return bukkitEntity;
            }
        }
        return bukkitEntity;
    }

    private void setBukkitEntityR(CraftEntity craftEntity) {
        ReflectionUtils.set(this, NMSFields.ENTITY_bukkitEntity, craftEntity);
    }

    private CraftEntity getBukkitEntityR() {
        return (CraftEntity)ReflectionUtils.get(this, NMSFields.ENTITY_bukkitEntity);
    }

    protected int decreaseAirSupply(int air) {
        return air;
    }

    protected int increaseAirSupply(int air) {
        return air;
    }

    public void remove(Entity.RemovalReason reason) {
        super.remove(reason);
        ModelEngineAPI.setRenderCanceled(this.getId(), false);
        ModelEngineAPI.getInteractionTracker().removeHitbox(this.getEntityId());
    }

    @Override
    public int getEntityId() {
        return this.getId();
    }

    @Override
    public UUID getUniqueId() {
        return this.getUUID();
    }

    @Override
    public void queueLocation(Vector3f location) {
        this.location = location;
    }

    @Override
    public Location getLocation() {
        return CraftLocation.toBukkit((Vec3)this.position(), (World)this.level().getWorld(), (float)this.getBukkitYaw(), (float)this.getXRot());
    }

    @Override
    @Nullable
    public OrientedBoundingBox getOrientedBoundingBox() {
        return this.obb == null ? null : this.obb.getBukkitOBB();
    }

    @Override
    public void markRemoved() {
        this.markRemoved = true;
        ModelEngineAPI.setRenderCanceled(this.getId(), false);
        ModelEngineAPI.getInteractionTracker().removeHitbox(this.getEntityId());
    }

    @Override
    @Generated
    public ModelBone getBone() {
        return this.bone;
    }

    @Override
    @Generated
    public SubHitbox getSubHitbox() {
        return this.subHitbox;
    }
}

