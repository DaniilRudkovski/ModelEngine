/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.inventory.ItemStack
 *  org.joml.Quaternionf
 *  org.joml.Vector3f
 */
package com.ticxo.modelengine.api.vfx.render;

import com.ticxo.modelengine.api.utils.data.tracker.DataTracker;
import com.ticxo.modelengine.api.vfx.render.VFXRenderer;
import java.util.UUID;
import org.bukkit.inventory.ItemStack;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public interface VFXDisplayRenderer
extends VFXRenderer {
    public boolean isRespawnRequired();

    public void setRespawnRequired(boolean var1);

    public VFXModel getVFXModel();

    public static interface VFXModel {
        public int getPivotId();

        public UUID getPivotUuid();

        public int getModelId();

        public UUID getModelUuid();

        default public void clearModelDirty() {
            this.getPosition().clearDirty();
            this.getLeftRotation().clearDirty();
            this.getScale().clearDirty();
            this.getModel().clearDirty();
        }

        default public boolean isModelDirty() {
            return this.getPosition().isDirty() || this.getLeftRotation().isDirty() || this.getScale().isDirty() || this.getModel().isDirty();
        }

        public DataTracker<Vector3f> getOrigin();

        public DataTracker<Vector3f> getPosition();

        public DataTracker<Quaternionf> getLeftRotation();

        public DataTracker<Vector3f> getScale();

        public DataTracker<ItemStack> getModel();
    }
}

