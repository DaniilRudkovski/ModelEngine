/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Entity
 *  org.joml.Vector3f
 */
package com.ticxo.modelengine.api.model.bone.type;

import java.util.Collection;
import java.util.Set;
import org.bukkit.entity.Entity;
import org.joml.Vector3f;

public interface Mount {
    public boolean isDriver();

    public Vector3f getLocation();

    public Vector3f getGlobalLocation();

    public boolean addPassenger(Entity var1);

    public boolean addPassengers(Collection<Entity> var1);

    public void removePassenger(Entity var1);

    public void removePassengers(Collection<Entity> var1);

    public Set<Entity> clearPassengers();

    public Set<Entity> getPassengers();

    public boolean canMountMore();
}

