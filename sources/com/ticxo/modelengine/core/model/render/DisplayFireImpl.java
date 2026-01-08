/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 *  org.bukkit.Material
 *  org.bukkit.inventory.ItemStack
 *  org.joml.Vector3f
 */
package com.ticxo.modelengine.core.model.render;

import com.ticxo.modelengine.api.model.render.DisplayFire;
import com.ticxo.modelengine.api.utils.data.tracker.DataTracker;
import com.ticxo.modelengine.api.utils.data.tracker.UpdateDataTracker;
import java.util.UUID;
import lombok.Generated;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.joml.Vector3f;

public class DisplayFireImpl
implements DisplayFire {
    private final int id;
    private final UUID uuid = UUID.randomUUID();
    private final DataTracker<Vector3f> position = new UpdateDataTracker<Vector3f>(new Vector3f(), Vector3f::set, Vector3f::new);
    private final DataTracker<Vector3f> scale = new UpdateDataTracker<Vector3f>(new Vector3f(), Vector3f::set, Vector3f::new);
    private final DataTracker<ItemStack> fireModel = new DataTracker<ItemStack>(new ItemStack(Material.AIR), ItemStack::isSimilar);
    private final DataTracker<Boolean> visible = new DataTracker<Boolean>(false);

    @Override
    public void clearDirty() {
        this.position.clearDirty();
        this.scale.clearDirty();
        this.fireModel.clearDirty();
        this.visible.clearDirty();
    }

    @Override
    @Generated
    public int getId() {
        return this.id;
    }

    @Override
    @Generated
    public UUID getUuid() {
        return this.uuid;
    }

    @Override
    @Generated
    public DataTracker<Vector3f> getPosition() {
        return this.position;
    }

    @Override
    @Generated
    public DataTracker<Vector3f> getScale() {
        return this.scale;
    }

    @Override
    @Generated
    public DataTracker<ItemStack> getFireModel() {
        return this.fireModel;
    }

    @Override
    @Generated
    public DataTracker<Boolean> getVisible() {
        return this.visible;
    }

    @Generated
    public DisplayFireImpl(int id) {
        this.id = id;
    }
}

