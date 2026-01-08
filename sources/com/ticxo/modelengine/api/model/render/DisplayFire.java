/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.inventory.ItemStack
 *  org.joml.Vector3f
 */
package com.ticxo.modelengine.api.model.render;

import com.ticxo.modelengine.api.utils.data.tracker.DataTracker;
import java.util.UUID;
import org.bukkit.inventory.ItemStack;
import org.joml.Vector3f;

public interface DisplayFire {
    public int getId();

    public UUID getUuid();

    public DataTracker<Vector3f> getPosition();

    public DataTracker<Vector3f> getScale();

    public DataTracker<ItemStack> getFireModel();

    public DataTracker<Boolean> getVisible();

    public void clearDirty();

    default public boolean isDirty() {
        return this.getPosition().isDirty() || this.getScale().isDirty() || this.getFireModel().isDirty() || this.getVisible().isDirty();
    }
}

