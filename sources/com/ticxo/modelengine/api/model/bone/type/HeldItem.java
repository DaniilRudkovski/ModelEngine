/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 *  org.bukkit.Bukkit
 *  org.bukkit.Material
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.ItemDisplay$ItemDisplayTransform
 *  org.bukkit.entity.LivingEntity
 *  org.bukkit.inventory.EntityEquipment
 *  org.bukkit.inventory.EquipmentSlot
 *  org.bukkit.inventory.ItemStack
 *  org.joml.Quaternionf
 *  org.joml.Vector3f
 */
package com.ticxo.modelengine.api.model.bone.type;

import com.ticxo.modelengine.api.utils.data.io.DataIO;
import com.ticxo.modelengine.api.utils.data.io.SavedData;
import com.ticxo.modelengine.api.utils.logger.TLogger;
import java.util.UUID;
import java.util.function.Supplier;
import lombok.Generated;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public interface HeldItem {
    public static final ItemStack EMPTY = new ItemStack(Material.AIR);

    public Vector3f getLocation();

    public Quaternionf getRotation();

    public ItemDisplay.ItemDisplayTransform getDisplay();

    public void setDisplay(ItemDisplay.ItemDisplayTransform var1);

    public ItemStackSupplier getItemProvider();

    public void setItemProvider(Supplier<ItemStack> var1);

    public void setItemProvider(ItemStackSupplier var1);

    public void clearItemProvider();

    public ItemStack getItem();

    public static class EquipmentSupplier
    implements ItemStackSupplier {
        private LivingEntity target;
        private EquipmentSlot slot;

        public EquipmentSupplier() {
        }

        @Override
        public ItemStack supply() {
            if (this.target == null || this.slot == null) {
                return EMPTY;
            }
            EntityEquipment eq = this.target.getEquipment();
            return eq == null ? EMPTY : eq.getItem(this.slot);
        }

        @Override
        public void save(SavedData data) {
            data.putString("type", "equipment");
            data.putUUID("target", this.target.getUniqueId());
            data.put("slot", this.slot.name());
        }

        @Override
        public void load(SavedData data) {
            UUID uuid = data.getUUID("target");
            String slotString = data.getString("slot");
            if (uuid == null || slotString == null) {
                return;
            }
            Entity entity = Bukkit.getEntity((UUID)uuid);
            if (!(entity instanceof LivingEntity)) {
                TLogger.error(1, "Failed to load EquipmentSupplier: Target entity does not exist or is not LivingEntity.");
                return;
            }
            LivingEntity livingEntity = (LivingEntity)entity;
            try {
                this.slot = EquipmentSlot.valueOf((String)slotString);
            }
            catch (Throwable throwable) {
                TLogger.error(1, "Failed to load EquipmentSupplier: Invalid slot " + slotString + ".");
                return;
            }
            this.target = livingEntity;
        }

        @Generated
        public EquipmentSupplier(LivingEntity target, EquipmentSlot slot) {
            this.target = target;
            this.slot = slot;
        }
    }

    public static class StaticItemStackSupplier
    implements ItemStackSupplier {
        private ItemStack itemStack;

        public StaticItemStackSupplier() {
        }

        @Override
        public ItemStack supply() {
            return this.itemStack;
        }

        @Override
        public void save(SavedData data) {
            data.putString("type", "static");
            data.putItemStack("item", this.itemStack);
        }

        @Override
        public void load(SavedData data) {
            this.itemStack = data.getItemStack("item", EMPTY);
        }

        @Generated
        public StaticItemStackSupplier(ItemStack itemStack) {
            this.itemStack = itemStack;
        }
    }

    public static class TemporaryItemStackSupplier
    implements ItemStackSupplier {
        private final Supplier<ItemStack> stackSupplier;

        @Override
        public ItemStack supply() {
            return this.stackSupplier.get();
        }

        @Override
        public void save(SavedData data) {
        }

        @Override
        public void load(SavedData data) {
        }

        @Generated
        public TemporaryItemStackSupplier(Supplier<ItemStack> stackSupplier) {
            this.stackSupplier = stackSupplier;
        }
    }

    public static interface ItemStackSupplier
    extends DataIO {
        public ItemStack supply();
    }
}

