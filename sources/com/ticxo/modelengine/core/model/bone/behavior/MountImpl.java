/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Sets
 *  lombok.Generated
 *  org.bukkit.Location
 *  org.bukkit.entity.Entity
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 */
package com.ticxo.modelengine.core.model.bone.behavior;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.model.bone.ModelBone;
import com.ticxo.modelengine.api.model.bone.behavior.AbstractBoneBehavior;
import com.ticxo.modelengine.api.model.bone.behavior.BoneBehaviorData;
import com.ticxo.modelengine.api.model.bone.behavior.BoneBehaviorType;
import com.ticxo.modelengine.api.model.bone.manager.MountManager;
import com.ticxo.modelengine.api.model.bone.type.Mount;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import lombok.Generated;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class MountImpl
extends AbstractBoneBehavior<MountImpl>
implements Mount {
    private final boolean driver;
    private final Vector3f location = new Vector3f();
    private final Vector3f globalLocation = new Vector3f();
    private final Set<Entity> passengers = Sets.newConcurrentHashSet();

    public MountImpl(ModelBone bone, BoneBehaviorType<MountImpl> type, BoneBehaviorData data) {
        super(bone, type, data);
        this.driver = data.get("driver", false);
    }

    @Override
    public void onApply() {
        this.bone.getActiveModel().getMountManager().ifPresent(mountManager -> {
            if (this.driver) {
                ((MountManager)((Object)mountManager)).setDriverBone(this);
            } else {
                ((MountManager)((Object)mountManager)).registerSeat(this);
            }
        });
        this.bone.getBlueprintBone().getLocalPosition().rotateY(-this.bone.getYaw() * ((float)Math.PI / 180), this.location);
        Location baseLocation = this.bone.calculatePivotLocation();
        this.globalLocation.set((Vector3fc)this.location).add((float)baseLocation.getX(), (float)baseLocation.getY(), (float)baseLocation.getZ());
    }

    @Override
    public void onFinalize() {
        this.bone.getGlobalTransform().mutatePosition(pos -> pos.rotateY(-this.bone.getYaw() * ((float)Math.PI / 180), this.location));
        Location baseLocation = this.bone.calculatePivotLocation();
        this.globalLocation.set((Vector3fc)this.location).add((float)baseLocation.getX(), (float)baseLocation.getY(), (float)baseLocation.getZ());
        this.passengers.removeIf(entity -> ModelEngineAPI.getEntityHandler().isRemoved((Entity)entity));
    }

    @Override
    public boolean addPassenger(Entity entity) {
        if (this.canMountMore()) {
            this.passengers.add(entity);
            return true;
        }
        return false;
    }

    @Override
    public boolean addPassengers(Collection<Entity> entities) {
        for (Entity entity : entities) {
            if (!this.canMountMore()) {
                return false;
            }
            this.passengers.add(entity);
        }
        return true;
    }

    @Override
    public void removePassenger(Entity entity) {
        this.passengers.remove(entity);
    }

    @Override
    public void removePassengers(Collection<Entity> entities) {
        this.passengers.removeAll(entities);
    }

    @Override
    public Set<Entity> clearPassengers() {
        HashSet<Entity> set = new HashSet<Entity>(this.passengers);
        this.passengers.clear();
        return set;
    }

    @Override
    public Set<Entity> getPassengers() {
        return ImmutableSet.copyOf(this.passengers);
    }

    @Override
    public boolean canMountMore() {
        return !this.isDriver() || this.passengers.isEmpty();
    }

    @Override
    @Generated
    public boolean isDriver() {
        return this.driver;
    }

    @Override
    @Generated
    public Vector3f getLocation() {
        return this.location;
    }

    @Override
    @Generated
    public Vector3f getGlobalLocation() {
        return this.globalLocation;
    }
}

