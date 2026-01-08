/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 *  org.bukkit.Color
 *  org.bukkit.Material
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.meta.ItemMeta
 *  org.bukkit.inventory.meta.LeatherArmorMeta
 *  org.bukkit.inventory.meta.MapMeta
 *  org.bukkit.inventory.meta.PotionMeta
 */
package com.ticxo.modelengine.api.generator;

import java.util.function.BiConsumer;
import lombok.Generated;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.inventory.meta.PotionMeta;

public enum BaseItemEnum {
    FILLED_MAP(Cons.MAP_CONSUMER, Material.FILLED_MAP),
    LEATHER_BOOTS(Cons.LEATHER_CONSUMER, Material.LEATHER_BOOTS),
    LEATHER_CHESTPLATE(Cons.LEATHER_CONSUMER, Material.LEATHER_CHESTPLATE),
    LEATHER_HORSE_ARMOR(Cons.LEATHER_CONSUMER, Material.LEATHER_HORSE_ARMOR),
    LEATHER_LEGGINGS(Cons.LEATHER_CONSUMER, Material.LEATHER_LEGGINGS),
    LINGERING_POTION(Cons.POTION_CONSUMER, Material.LINGERING_POTION),
    POTION(Cons.POTION_CONSUMER, Material.POTION),
    SPLASH_POTION(Cons.POTION_CONSUMER, Material.SPLASH_POTION),
    TIPPED_ARROW(Cons.POTION_CONSUMER, Material.TIPPED_ARROW);

    private final BiConsumer<ItemMeta, Color> metaConsumer;
    private final Material material;

    public static BaseItemEnum get(String value) {
        try {
            return BaseItemEnum.valueOf(value);
        }
        catch (IllegalArgumentException ignored) {
            return LEATHER_HORSE_ARMOR;
        }
    }

    public static BaseItemEnum fromMaterial(Material material) {
        return switch (material) {
            case Material.FILLED_MAP -> FILLED_MAP;
            case Material.LEATHER_BOOTS -> LEATHER_BOOTS;
            case Material.LEATHER_CHESTPLATE -> LEATHER_CHESTPLATE;
            case Material.LEATHER_HORSE_ARMOR -> LEATHER_HORSE_ARMOR;
            case Material.LEATHER_LEGGINGS -> LEATHER_LEGGINGS;
            case Material.LINGERING_POTION -> LINGERING_POTION;
            case Material.POTION -> POTION;
            case Material.SPLASH_POTION -> SPLASH_POTION;
            case Material.TIPPED_ARROW -> TIPPED_ARROW;
            default -> null;
        };
    }

    public void color(ItemMeta meta, Color color) {
        this.metaConsumer.accept(meta, color);
    }

    public ItemStack create() {
        return new ItemStack(this.material);
    }

    public ItemStack create(Color color, int data) {
        ItemStack stack = this.create();
        ItemMeta meta = stack.getItemMeta();
        meta.setCustomModelData(Integer.valueOf(data));
        this.color(meta, color);
        stack.setItemMeta(meta);
        return stack;
    }

    @Generated
    private BaseItemEnum(BiConsumer<ItemMeta, Color> metaConsumer, Material material) {
        this.metaConsumer = metaConsumer;
        this.material = material;
    }

    @Generated
    public Material getMaterial() {
        return this.material;
    }

    private static class Cons {
        private static final BiConsumer<ItemMeta, Color> MAP_CONSUMER = (meta, color) -> {
            MapMeta map = (MapMeta)meta;
            map.setColor(color);
        };
        private static final BiConsumer<ItemMeta, Color> LEATHER_CONSUMER = (meta, color) -> {
            LeatherArmorMeta leather = (LeatherArmorMeta)meta;
            leather.setColor(color);
        };
        private static final BiConsumer<ItemMeta, Color> POTION_CONSUMER = (meta, color) -> {
            PotionMeta potion = (PotionMeta)meta;
            potion.setColor(color);
        };

        private Cons() {
        }
    }
}

