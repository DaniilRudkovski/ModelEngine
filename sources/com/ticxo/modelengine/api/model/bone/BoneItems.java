/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  lombok.Generated
 *  org.bukkit.Color
 *  org.bukkit.Material
 *  org.bukkit.inventory.ItemStack
 */
package com.ticxo.modelengine.api.model.bone;

import com.google.common.collect.Maps;
import com.ticxo.modelengine.api.generator.assets.ItemModelData;
import com.ticxo.modelengine.api.generator.blueprint.BlueprintBone;
import com.ticxo.modelengine.api.utils.data.tracker.DataTracker;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import lombok.Generated;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class BoneItems {
    private final Map<Integer, ItemStack> stacks = Maps.newConcurrentMap();
    private final Map<ItemStack, Integer> toHash = Maps.newConcurrentMap();
    private final DataTracker<BoneItems> tracker = new DataTracker<BoneItems>(this);

    public ItemStack getFirst() {
        this.prepStacks();
        return this.stacks.entrySet().iterator().next().getValue();
    }

    public Map<Integer, ItemStack> getItems() {
        return new HashMap<Integer, ItemStack>(this.stacks);
    }

    public void forEach(BiConsumer<Integer, ItemStack> consumer) {
        this.stacks.forEach(consumer);
    }

    public void forEach(Consumer<ItemStack> consumer) {
        this.forEach(consumer, false);
    }

    public void forEach(Consumer<ItemStack> consumer, boolean markDirty) {
        this.forEach(consumer, () -> markDirty);
    }

    public void forEach(Consumer<ItemStack> consumer, Supplier<Boolean> markDirty) {
        this.stacks.forEach((? super K uuid, ? super V stack) -> consumer.accept((ItemStack)stack));
        if (markDirty.get().booleanValue()) {
            this.toHash.clear();
            this.stacks.forEach((? super K integer, ? super V stack) -> this.toHash.put((ItemStack)stack, stack.hashCode()));
            this.stacks.clear();
            this.toHash.forEach((? super K stack, ? super V integer) -> this.stacks.put(stack.hashCode(), (ItemStack)stack));
            this.markDirty();
        }
    }

    public void update(BlueprintBone bone, Color color) {
        HashSet<ItemStack> items = new HashSet<ItemStack>(bone.getModelData().createItemStack(ItemModelData.context().color(color).build()));
        if (items.equals(this.stacks.keySet())) {
            return;
        }
        this.stacks.clear();
        this.toHash.clear();
        items.forEach(this::add);
    }

    public void clear() {
        if (this.stacks.isEmpty()) {
            return;
        }
        this.stacks.clear();
        this.toHash.clear();
        this.markDirty();
    }

    public void add(ItemStack stack) {
        if (this.toHash.containsKey(stack)) {
            return;
        }
        if (stack == null) {
            Thread.dumpStack();
            stack = ItemStack.empty();
        }
        this.stacks.put(stack.hashCode(), stack);
        this.toHash.put(stack, stack.hashCode());
        this.markDirty();
    }

    public void remove(ItemStack stack) {
        if (!this.toHash.containsKey(stack)) {
            return;
        }
        this.stacks.remove(this.toHash.remove(stack));
        this.markDirty();
    }

    public void markDirty() {
        this.tracker.markDirty();
    }

    public void clearDirty() {
        this.tracker.clearDirty();
    }

    public boolean isDirty() {
        return this.tracker.isDirty();
    }

    public boolean isEqual(Set<ItemStack> stackSet) {
        return stackSet.equals(this.toHash.keySet());
    }

    private void prepStacks() {
        if (this.stacks.isEmpty()) {
            this.add(new ItemStack(Material.AIR));
        }
    }

    @Generated
    public DataTracker<BoneItems> getTracker() {
        return this.tracker;
    }
}

