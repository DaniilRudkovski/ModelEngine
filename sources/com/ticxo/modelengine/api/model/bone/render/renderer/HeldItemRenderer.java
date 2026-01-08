/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.ItemDisplay$ItemDisplayTransform
 *  org.bukkit.inventory.ItemStack
 *  org.joml.Quaternionf
 *  org.joml.Vector3f
 */
package com.ticxo.modelengine.api.model.bone.render.renderer;

import com.ticxo.modelengine.api.model.bone.render.BehaviorRenderer;
import com.ticxo.modelengine.api.model.bone.render.renderer.RenderQueues;
import com.ticxo.modelengine.api.utils.data.tracker.CollectionDataTracker;
import com.ticxo.modelengine.api.utils.data.tracker.DataTracker;
import java.util.UUID;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.ItemStack;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public interface HeldItemRenderer
extends BehaviorRenderer,
RenderQueues<Item> {
    public int getId();

    public UUID getUuid();

    public CollectionDataTracker<Integer> getPassengers();

    public static interface Item {
        public int getId();

        public UUID getUuid();

        public DataTracker<Vector3f> getPosition();

        public DataTracker<Vector3f> getScale();

        public DataTracker<Quaternionf> getRotation();

        public DataTracker<ItemStack> getModel();

        public DataTracker<ItemDisplay.ItemDisplayTransform> getDisplay();

        public DataTracker<Boolean> getGlowing();

        public DataTracker<Integer> getGlowColor();

        default public boolean isTransformDirty() {
            return this.getPosition().isDirty() || this.getScale().isDirty() || this.getRotation().isDirty();
        }

        default public boolean isRenderDirty() {
            return this.getModel().isDirty() || this.getDisplay().isDirty() || this.getGlowing().isDirty() || this.getGlowColor().isDirty();
        }

        default public boolean isDirty() {
            return this.isTransformDirty() || this.isRenderDirty();
        }

        default public void clearDirty() {
            this.getPosition().clearDirty();
            this.getScale().clearDirty();
            this.getRotation().clearDirty();
            this.getModel().clearDirty();
            this.getDisplay().clearDirty();
            this.getGlowing().clearDirty();
            this.getGlowColor().clearDirty();
        }
    }
}

