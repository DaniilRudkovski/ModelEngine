/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Display$Billboard
 *  org.bukkit.entity.ItemDisplay$ItemDisplayTransform
 *  org.bukkit.inventory.ItemStack
 *  org.joml.Quaternionf
 *  org.joml.Vector3f
 */
package com.ticxo.modelengine.api.model.render;

import com.ticxo.modelengine.api.lod.BoneSnapshotHandler;
import com.ticxo.modelengine.api.utils.data.UpdateScheme;
import com.ticxo.modelengine.api.utils.data.tracker.DataTracker;
import java.util.Map;
import java.util.UUID;
import org.bukkit.entity.Display;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.ItemStack;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public interface DisplayBone {
    default public boolean isDirty() {
        return this.isTransformDirty() || this.isRenderDirty() || this.isModelDirty();
    }

    default public boolean isSkippable() {
        return this.isTransformDirty() && !this.isRenderDirty() && !this.isModelDirty();
    }

    default public boolean isTransformDirty() {
        return this.getPosition().isDirty() || this.getLeftRotation().isDirty() || this.getScale().isDirty() || this.getRightRotation().isDirty();
    }

    default public boolean isRenderDirty() {
        return this.getBillboard().isDirty() || this.getDisplay().isDirty() || this.getVisibility().isDirty() || this.getGlowing().isDirty() || this.getGlowColor().isDirty() || this.getBrightness().isDirty() || this.getStep().isDirty();
    }

    default public boolean isModelDirty() {
        return this.getModelUpdateScheme().hasUpdate();
    }

    default public void clearDirty() {
        this.getStep().clearDirty();
        this.getPosition().clearDirty();
        this.getLeftRotation().clearDirty();
        this.getScale().clearDirty();
        this.getRightRotation().clearDirty();
        this.getBillboard().clearDirty();
        this.getModelUpdateScheme().reset();
        this.getDisplay().clearDirty();
        this.getVisibility().clearDirty();
        this.getGlowing().clearDirty();
        this.getGlowColor().clearDirty();
        this.getBrightness().clearDirty();
    }

    public BoneSnapshotHandler getSnapshotHandler();

    public DataTracker<Boolean> getRender();

    public DataTracker<Boolean> getStep();

    public DataTracker<Vector3f> getPosition();

    public DataTracker<Quaternionf> getLeftRotation();

    public DataTracker<Vector3f> getScale();

    public DataTracker<Quaternionf> getRightRotation();

    public DataTracker<Display.Billboard> getBillboard();

    public Map<Integer, BoneData> getModel();

    public UpdateScheme<BoneData> getModelUpdateScheme();

    public DataTracker<ItemDisplay.ItemDisplayTransform> getDisplay();

    public DataTracker<Boolean> getVisibility();

    public DataTracker<Boolean> getGlowing();

    public DataTracker<Integer> getGlowColor();

    public DataTracker<Integer> getBrightness();

    public static interface BoneData {
        public int getId();

        public UUID getUuid();

        public DisplayBone getBone();

        public DataTracker<ItemStack> getModel();

        public int getModelHash();
    }
}

