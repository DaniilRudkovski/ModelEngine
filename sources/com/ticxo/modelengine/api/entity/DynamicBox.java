/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.util.BoundingBox
 *  org.bukkit.util.Vector
 *  org.jetbrains.annotations.NotNull
 */
package com.ticxo.modelengine.api.entity;

import com.ticxo.modelengine.api.utils.math.Box;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class DynamicBox
implements Box {
    private final BoundingBox box = new BoundingBox();

    public void reset() {
        this.box.resize(0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
    }

    public void expand(@NotNull BoundingBox other) {
        this.box.union(other);
    }

    @Override
    @NotNull
    public BoundingBox createBoundingBox(Vector vector) {
        return this.box.clone().shift(vector);
    }
}

