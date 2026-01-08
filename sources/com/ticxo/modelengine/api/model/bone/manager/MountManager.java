/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Entity
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.ticxo.modelengine.api.model.bone.manager;

import com.ticxo.modelengine.api.model.bone.behavior.BoneBehavior;
import com.ticxo.modelengine.api.model.bone.type.Mount;
import com.ticxo.modelengine.api.mount.controller.MountController;
import com.ticxo.modelengine.api.mount.controller.MountControllerSupplier;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface MountManager {
    public void setCanDrive(boolean var1);

    public boolean canDrive();

    public void setCanRide(boolean var1);

    public boolean canRide();

    public Entity getDriver();

    public boolean isControlled();

    public boolean hasPassengers();

    public boolean hasRiders();

    @Nullable
    public <T extends Mount & BoneBehavior> T getDriverBone();

    public <T extends Mount & BoneBehavior> void setDriverBone(@Nullable T var1);

    public <T extends Mount & BoneBehavior> void registerSeat(T var1);

    public <T extends Mount & BoneBehavior> Map<String, T> getSeats();

    public <T extends Mount & BoneBehavior> Optional<T> getSeat(String var1);

    public <T extends Mount & BoneBehavior> Optional<T> getMount(Entity var1);

    public Map<Entity, Mount> getPassengerSeatMap();

    public boolean mountDriver(Entity var1, MountControllerSupplier var2);

    public boolean mountDriver(Entity var1, MountControllerSupplier var2, @Nullable Consumer<MountController> var3);

    public boolean mountPassenger(String var1, Entity var2, MountControllerSupplier var3);

    public boolean mountPassenger(String var1, Entity var2, MountControllerSupplier var3, @Nullable Consumer<MountController> var4);

    public boolean mountPassenger(Mount var1, Entity var2, MountControllerSupplier var3);

    public boolean mountPassenger(Mount var1, Entity var2, MountControllerSupplier var3, @Nullable Consumer<MountController> var4);

    public boolean mountAvailable(Entity var1, MountControllerSupplier var2);

    public boolean mountAvailable(Entity var1, MountControllerSupplier var2, @Nullable Consumer<MountController> var3);

    public Set<Entity> mountAvailable(Collection<Entity> var1, MountControllerSupplier var2);

    public Set<Entity> mountAvailable(Collection<Entity> var1, MountControllerSupplier var2, @Nullable Consumer<MountController> var3);

    public boolean mountAvailable(Entity var1, Collection<String> var2, MountControllerSupplier var3);

    public boolean mountAvailable(Entity var1, Collection<String> var2, MountControllerSupplier var3, @Nullable Consumer<MountController> var4);

    public Set<Entity> mountAvailable(Collection<Entity> var1, Collection<String> var2, MountControllerSupplier var3);

    public Set<Entity> mountAvailable(Collection<Entity> var1, Collection<String> var2, MountControllerSupplier var3, @Nullable Consumer<MountController> var4);

    public boolean mountLeastOccupied(Entity var1, MountControllerSupplier var2);

    public boolean mountLeastOccupied(Entity var1, MountControllerSupplier var2, @Nullable Consumer<MountController> var3);

    public Set<Entity> mountLeastOccupied(Collection<Entity> var1, MountControllerSupplier var2);

    public Set<Entity> mountLeastOccupied(Collection<Entity> var1, MountControllerSupplier var2, @Nullable Consumer<MountController> var3);

    public boolean mountLeastOccupied(Entity var1, Collection<String> var2, MountControllerSupplier var3);

    public boolean mountLeastOccupied(Entity var1, Collection<String> var2, MountControllerSupplier var3, @Nullable Consumer<MountController> var4);

    public Set<Entity> mountLeastOccupied(Collection<Entity> var1, Collection<String> var2, MountControllerSupplier var3);

    public Set<Entity> mountLeastOccupied(Collection<Entity> var1, Collection<String> var2, MountControllerSupplier var3, @Nullable Consumer<MountController> var4);

    public Entity dismountDriver();

    public void dismountPassenger(@NotNull Entity var1);

    public void dismountRider(@NotNull Entity var1);

    public Set<Entity> dismountPassengers(String var1);

    public Set<Entity> dismountAll();
}

