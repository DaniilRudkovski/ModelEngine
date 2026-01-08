/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Entity
 *  org.joml.Quaternionf
 *  org.joml.Vector3f
 */
package com.ticxo.modelengine.api.model.bone.type;

import com.ticxo.modelengine.api.nms.entity.HitboxEntity;
import java.util.Map;
import java.util.UUID;
import org.bukkit.entity.Entity;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public interface SubHitbox {
    public boolean isOBB();

    public Vector3f getFixedDimension();

    public Vector3f getOrigin();

    public float getDamageMultiplier();

    public void setDamageMultiplier(float var1);

    public boolean isCanBeHitByProjectile();

    public void setCanBeHitByProjectile(boolean var1);

    public void addBoundEntity(Entity var1);

    public void removeBoundEntity(Entity var1);

    public Map<UUID, Entity> getBoundEntities();

    public int getHitboxId();

    public Vector3f getDimension();

    public Vector3f getLocation();

    public Quaternionf getRotation();

    public float getYaw();

    public HitboxEntity getHitboxEntity();
}

