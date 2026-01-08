/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 *  net.minecraft.core.NonNullList
 *  net.minecraft.world.EnumHand
 *  net.minecraft.world.EnumInteractionResult
 *  net.minecraft.world.damagesource.DamageSource
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.Entity$RemovalReason
 *  net.minecraft.world.entity.EntityLiving
 *  net.minecraft.world.entity.EntityTypes
 *  net.minecraft.world.entity.EnumItemSlot
 *  net.minecraft.world.entity.EnumItemSlot$Function
 *  net.minecraft.world.entity.EnumMainHand
 *  net.minecraft.world.entity.player.EntityHuman
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.World
 *  net.minecraft.world.phys.AxisAlignedBB
 *  net.minecraft.world.phys.Vec3D
 *  org.bukkit.Bukkit
 *  org.bukkit.Location
 *  org.bukkit.World
 *  org.bukkit.craftbukkit.v1_20_R2.entity.CraftHumanEntity
 *  org.bukkit.craftbukkit.v1_20_R2.util.CraftLocation
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
package com.ticxo.modelengine.v1_20_R2.entity.hitbox;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.model.bone.ModelBone;
import com.ticxo.modelengine.api.model.bone.type.SubHitbox;
import com.ticxo.modelengine.api.nms.entity.EntityHandler;
import com.ticxo.modelengine.api.nms.entity.HitboxEntity;
import com.ticxo.modelengine.api.utils.math.OrientedBoundingBox;
import com.ticxo.modelengine.v1_20_R2.entity.EntityUtils;
import com.ticxo.modelengine.v1_20_R2.entity.hitbox.OBB;
import java.util.UUID;
import lombok.Generated;
import net.minecraft.core.NonNullList;
import net.minecraft.world.EnumHand;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.entity.EnumMainHand;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.World;
import net.minecraft.world.phys.AxisAlignedBB;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_20_R2.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.v1_20_R2.util.CraftLocation;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class HitboxEntityImpl
extends EntityLiving
implements HitboxEntity {
    private final NonNullList<ItemStack> handItems = NonNullList.a((int)2, (Object)ItemStack.b);
    private final NonNullList<ItemStack> armorItems = NonNullList.a((int)4, (Object)ItemStack.b);
    private final ModelBone bone;
    private final SubHitbox subHitbox;
    private OBB obb;
    private Vector3f location;
    private boolean markRemoved;

    public HitboxEntityImpl(World world, @NotNull ModelBone bone, @NotNull SubHitbox subHitbox) {
        super(EntityTypes.aI, world);
        this.bone = bone;
        this.subHitbox = subHitbox;
        this.m(true);
        this.e(true);
        this.af = true;
    }

    public EnumMainHand fl() {
        return EnumMainHand.b;
    }

    public Iterable<ItemStack> bK() {
        return this.armorItems;
    }

    public ItemStack c(EnumItemSlot slot) {
        return switch (slot.a()) {
            default -> throw new IncompatibleClassChangeError();
            case EnumItemSlot.Function.a -> (ItemStack)this.handItems.get(slot.b());
            case EnumItemSlot.Function.b -> (ItemStack)this.armorItems.get(slot.b());
        };
    }

    public void a(EnumItemSlot slot, ItemStack stack) {
        this.e(stack);
        switch (slot.a()) {
            case a: {
                this.a(slot, (ItemStack)this.handItems.set(slot.b(), (Object)stack), stack);
                break;
            }
            case b: {
                this.a(slot, (ItemStack)this.armorItems.set(slot.b(), (Object)stack), stack);
            }
        }
    }

    public boolean cd() {
        return true;
    }

    public void g(net.minecraft.world.entity.Entity entity) {
    }

    @NotNull
    protected AxisAlignedBB ao() {
        if (this.subHitbox == null) {
            return super.ao();
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
        return new AxisAlignedBB((double)(pos.x - halfX), (double)pos.y, (double)(pos.z - halfZ), (double)(pos.x + halfX), (double)(pos.y + dim.y), (double)(pos.z + halfZ));
    }

    public void l() {
        if (this.markRemoved) {
            this.ak();
            return;
        }
        super.l();
        if (this.bone == null || this.subHitbox == null) {
            this.ak();
            return;
        }
        if (!this.bone.getActiveModel().getModeledEntity().getBase().isAlive()) {
            this.ak();
            return;
        }
        if (!this.location.isFinite()) {
            return;
        }
        Vec3D vec = new Vec3D(this.location);
        this.a(vec);
        for (Entity entity : this.subHitbox.getBoundEntities().values()) {
            EntityUtils.nms(entity).a(vec);
        }
    }

    public boolean aW() {
        return true;
    }

    protected void fh() {
    }

    protected void D(net.minecraft.world.entity.Entity entity) {
    }

    public boolean bs() {
        return false;
    }

    public boolean a(DamageSource source, float amount) {
        CraftHumanEntity craftHumanEntity;
        for (Entity entity : this.subHitbox.getBoundEntities().values()) {
            EntityUtils.nms(entity).a(source, amount);
        }
        if (this.subHitbox.getDamageMultiplier() <= 1.0E-5f) {
            return false;
        }
        net.minecraft.world.entity.Entity entity = source.d();
        if (entity instanceof EntityHuman) {
            EntityHuman player = (EntityHuman)entity;
            craftHumanEntity = player.getBukkitEntity();
        } else {
            craftHumanEntity = null;
        }
        CraftHumanEntity cause = craftHumanEntity;
        return this.bone.getActiveModel().getModeledEntity().getBase().hurt(this, (HumanEntity)cause, source, amount * this.subHitbox.getDamageMultiplier());
    }

    @NotNull
    public EnumInteractionResult a(EntityHuman player, EnumHand hand) {
        CraftHumanEntity craftHumanEntity = player.getBukkitEntity();
        if (craftHumanEntity instanceof Player) {
            Player craftPlayer = (Player)craftHumanEntity;
            for (Entity entity : this.subHitbox.getBoundEntities().values()) {
                PlayerInteractAtEntityEvent event = new PlayerInteractAtEntityEvent(craftPlayer, entity, new Vector(0, 0, 0), hand == EnumHand.b ? EquipmentSlot.OFF_HAND : EquipmentSlot.HAND);
                Bukkit.getPluginManager().callEvent((Event)event);
                if (event.isCancelled()) continue;
                EntityUtils.nms(entity).a(player, hand);
            }
        }
        if (this.subHitbox.getDamageMultiplier() <= 1.0E-5f) {
            return EnumInteractionResult.d;
        }
        EntityHandler.InteractionResult result = this.bone.getActiveModel().getModeledEntity().getBase().interact(this, (HumanEntity)player.getBukkitEntity(), hand == EnumHand.a ? EquipmentSlot.HAND : EquipmentSlot.OFF_HAND);
        return switch (result) {
            default -> throw new IncompatibleClassChangeError();
            case EntityHandler.InteractionResult.SUCCESS, EntityHandler.InteractionResult.SUCCESS_NO_ITEM_USED -> EnumInteractionResult.a;
            case EntityHandler.InteractionResult.CONSUME -> EnumInteractionResult.b;
            case EntityHandler.InteractionResult.CONSUME_PARTIAL -> EnumInteractionResult.c;
            case EntityHandler.InteractionResult.PASS -> EnumInteractionResult.d;
            case EntityHandler.InteractionResult.FAIL -> EnumInteractionResult.e;
        };
    }

    public boolean dJ() {
        return false;
    }

    protected int m(int air) {
        return air;
    }

    protected int n(int air) {
        return air;
    }

    public void a(Entity.RemovalReason reason) {
        super.a(reason);
        ModelEngineAPI.setRenderCanceled(this.ah(), false);
        ModelEngineAPI.getInteractionTracker().removeHitbox(this.getEntityId());
    }

    @Override
    public int getEntityId() {
        return this.ah();
    }

    @Override
    public UUID getUniqueId() {
        return this.cv();
    }

    @Override
    public void queueLocation(Vector3f location) {
        this.location = location;
    }

    @Override
    public Location getLocation() {
        return CraftLocation.toBukkit((Vec3D)this.dj(), (org.bukkit.World)this.dL().getWorld(), (float)this.getBukkitYaw(), (float)this.dD());
    }

    @Override
    @Nullable
    public OrientedBoundingBox getOrientedBoundingBox() {
        return this.obb == null ? null : this.obb.getBukkitOBB();
    }

    @Override
    public void markRemoved() {
        this.markRemoved = true;
        ModelEngineAPI.setRenderCanceled(this.ah(), false);
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

