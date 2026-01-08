/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.util.BoundingBox
 *  org.bukkit.util.Vector
 *  org.jetbrains.annotations.NotNull
 */
package com.ticxo.modelengine.api.utils.math;

import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public interface Box {
    @NotNull
    public BoundingBox createBoundingBox(Vector var1);
}

