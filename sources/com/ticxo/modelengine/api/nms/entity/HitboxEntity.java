/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Vector3f
 */
package com.ticxo.modelengine.api.nms.entity;

import com.ticxo.modelengine.api.model.bone.ModelBone;
import com.ticxo.modelengine.api.model.bone.type.SubHitbox;
import com.ticxo.modelengine.api.utils.math.OrientedBoundingBox;
import java.util.UUID;
import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public interface HitboxEntity {
    public ModelBone getBone();

    public SubHitbox getSubHitbox();

    public int getEntityId();

    public UUID getUniqueId();

    public void queueLocation(Vector3f var1);

    public Location getLocation();

    @Nullable
    public OrientedBoundingBox getOrientedBoundingBox();

    public void markRemoved();
}

