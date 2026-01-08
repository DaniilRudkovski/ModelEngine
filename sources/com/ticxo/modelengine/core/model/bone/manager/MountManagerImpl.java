/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  lombok.Generated
 *  org.bukkit.entity.Entity
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.ticxo.modelengine.core.model.bone.manager;

import com.google.common.collect.ImmutableMap;
import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.events.ModelDismountEvent;
import com.ticxo.modelengine.api.events.ModelMountEvent;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.bone.behavior.BoneBehavior;
import com.ticxo.modelengine.api.model.bone.behavior.BoneBehaviorType;
import com.ticxo.modelengine.api.model.bone.manager.AbstractBehaviorManager;
import com.ticxo.modelengine.api.model.bone.manager.MountManager;
import com.ticxo.modelengine.api.model.bone.type.Mount;
import com.ticxo.modelengine.api.mount.controller.MountController;
import com.ticxo.modelengine.api.mount.controller.MountControllerSupplier;
import com.ticxo.modelengine.api.utils.data.io.SavedData;
import com.ticxo.modelengine.core.model.bone.behavior.MountImpl;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import lombok.Generated;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MountManagerImpl
extends AbstractBehaviorManager<MountImpl>
implements MountManager {
    private final Map<String, ?> seats = new LinkedHashMap();
    private final Map<Entity, Mount> passengerSeatMap = new HashMap<Entity, Mount>();
    boolean canDrive;
    boolean canRide;
    private Entity driver;
    private Mount driverBone;

    public MountManagerImpl(ActiveModel activeModel, BoneBehaviorType<MountImpl> supplier) {
        super(activeModel, supplier);
    }

    @Override
    public void onDestroy() {
        this.dismountDriver();
        this.dismountAll();
    }

    @Override
    public void load(SavedData data) {
        this.canDrive = data.getBoolean("drive");
        this.canRide = data.getBoolean("ride");
    }

    @Override
    public void save(SavedData data) {
        data.putBoolean("drive", this.canDrive);
        data.putBoolean("ride", this.canRide);
    }

    @Override
    public boolean canDrive() {
        return this.canDrive;
    }

    @Override
    public boolean canRide() {
        return this.canRide;
    }

    @Override
    public boolean isControlled() {
        return this.driver != null && this.driverBone != null;
    }

    @Override
    public boolean hasPassengers() {
        return !this.passengerSeatMap.isEmpty();
    }

    @Override
    public boolean hasRiders() {
        return this.driver != null || !this.passengerSeatMap.isEmpty();
    }

    @Override
    @Nullable
    public <T extends Mount & BoneBehavior> T getDriverBone() {
        return (T)this.driverBone;
    }

    @Override
    public <T extends Mount & BoneBehavior> void setDriverBone(@Nullable T mount) {
        this.driverBone = mount;
    }

    @Override
    public <T extends Mount & BoneBehavior> void registerSeat(T mount) {
        this.getSeats().put(((BoneBehavior)mount).getBone().getUniqueBoneId(), mount);
    }

    @Override
    public <T extends Mount & BoneBehavior> Map<String, T> getSeats() {
        return this.seats;
    }

    @Override
    public <T extends Mount & BoneBehavior> Optional<T> getSeat(String boneId) {
        return Optional.ofNullable((Mount)this.getSeats().get(boneId));
    }

    @Override
    public <T extends Mount & BoneBehavior> Optional<T> getMount(Entity entity) {
        if (entity == this.driver) {
            return Optional.ofNullable(this.getDriverBone());
        }
        Mount mount = this.passengerSeatMap.get(entity);
        return Optional.ofNullable(mount);
    }

    @Override
    public Map<Entity, Mount> getPassengerSeatMap() {
        return ImmutableMap.copyOf(this.passengerSeatMap);
    }

    @Override
    public boolean mountDriver(Entity entity, MountControllerSupplier supplier) {
        return this.mountDriver(entity, supplier, null);
    }

    @Override
    public boolean mountDriver(Entity entity, MountControllerSupplier supplier, @Nullable Consumer<MountController> consumer) {
        if (this.driverBone == null || !this.driverBone.getPassengers().isEmpty() || !this.canDrive()) {
            return false;
        }
        ModelMountEvent event = new ModelMountEvent(this.activeModel, entity, true, this.driverBone);
        ModelEngineAPI.callEvent(event);
        if (event.isCancelled()) {
            return false;
        }
        boolean success = this.driverBone.addPassenger(entity);
        if (success) {
            this.driver = entity;
            this.registerMountPair(entity, this.driverBone, supplier, consumer);
        }
        return success;
    }

    @Override
    public boolean mountPassenger(String boneId, Entity entity, MountControllerSupplier supplier) {
        return this.mountPassenger(boneId, entity, supplier, null);
    }

    @Override
    public boolean mountPassenger(String boneId, Entity entity, MountControllerSupplier supplier, @Nullable Consumer<MountController> consumer) {
        return this.canRide() && this.getSeat(boneId).map(mount -> this.mountPassenger((Mount)((Object)mount), entity, supplier, consumer)).orElse(false) != false;
    }

    @Override
    public boolean mountPassenger(Mount mount, Entity entity, MountControllerSupplier supplier) {
        return this.mountPassenger(mount, entity, supplier, null);
    }

    @Override
    public boolean mountPassenger(Mount mount, Entity entity, MountControllerSupplier supplier, @Nullable Consumer<MountController> consumer) {
        ModelMountEvent event = new ModelMountEvent(this.activeModel, entity, false, mount);
        ModelEngineAPI.callEvent(event);
        if (event.isCancelled()) {
            return false;
        }
        boolean success = mount.addPassenger(entity);
        if (success) {
            this.passengerSeatMap.put(entity, mount);
            this.registerMountPair(entity, mount, supplier, consumer);
        }
        return success;
    }

    @Override
    public boolean mountAvailable(Entity entity, MountControllerSupplier supplier) {
        return this.mountAvailable(entity, supplier, null);
    }

    @Override
    public boolean mountAvailable(Entity entity, MountControllerSupplier supplier, @Nullable Consumer<MountController> consumer) {
        return this.mountAvailable(entity, this.seats.keySet(), supplier, consumer);
    }

    @Override
    public Set<Entity> mountAvailable(Collection<Entity> entities, MountControllerSupplier supplier) {
        return this.mountAvailable(entities, supplier, null);
    }

    @Override
    public Set<Entity> mountAvailable(Collection<Entity> entities, MountControllerSupplier supplier, @Nullable Consumer<MountController> consumer) {
        return this.mountAvailable(entities, this.seats.keySet(), supplier, consumer);
    }

    @Override
    public boolean mountAvailable(Entity entity, Collection<String> seats, MountControllerSupplier supplier) {
        return this.mountAvailable(entity, seats, supplier, null);
    }

    @Override
    public boolean mountAvailable(Entity entity, Collection<String> seats, MountControllerSupplier supplier, @Nullable Consumer<MountController> consumer) {
        for (String seatId : seats) {
            BoneBehavior seat;
            Optional maybeSeat = this.getSeat(seatId);
            if (maybeSeat.isEmpty() || !((Mount)((Object)(seat = (BoneBehavior)maybeSeat.get()))).getPassengers().isEmpty()) continue;
            this.mountPassenger((Mount)((Object)seat), entity, supplier, consumer);
            return true;
        }
        return false;
    }

    @Override
    public Set<Entity> mountAvailable(Collection<Entity> entities, Collection<String> seats, MountControllerSupplier supplier) {
        return this.mountAvailable(entities, seats, supplier, null);
    }

    @Override
    public Set<Entity> mountAvailable(Collection<Entity> entities, Collection<String> seats, MountControllerSupplier supplier, @Nullable Consumer<MountController> consumer) {
        HashSet<Entity> unseated = new HashSet<Entity>();
        boolean noSeats = false;
        block0: for (Entity entity : entities) {
            if (!noSeats) {
                noSeats = true;
                for (String seatId : seats) {
                    BoneBehavior seat;
                    Optional maybeSeat = this.getSeat(seatId);
                    if (maybeSeat.isEmpty() || !((Mount)((Object)(seat = (BoneBehavior)maybeSeat.get()))).getPassengers().isEmpty()) continue;
                    this.mountPassenger((Mount)((Object)seat), entity, supplier, consumer);
                    noSeats = false;
                    continue block0;
                }
            }
            unseated.add(entity);
        }
        return unseated;
    }

    @Override
    public boolean mountLeastOccupied(Entity entity, MountControllerSupplier supplier) {
        return this.mountLeastOccupied(entity, supplier, null);
    }

    @Override
    public boolean mountLeastOccupied(Entity entity, MountControllerSupplier supplier, @Nullable Consumer<MountController> consumer) {
        return this.mountLeastOccupied(entity, this.seats.keySet(), supplier, consumer);
    }

    @Override
    public Set<Entity> mountLeastOccupied(Collection<Entity> entities, MountControllerSupplier supplier) {
        return this.mountLeastOccupied(entities, supplier, null);
    }

    @Override
    public Set<Entity> mountLeastOccupied(Collection<Entity> entities, MountControllerSupplier supplier, @Nullable Consumer<MountController> consumer) {
        return this.mountLeastOccupied(entities, this.seats.keySet(), supplier, consumer);
    }

    @Override
    public boolean mountLeastOccupied(Entity entity, Collection<String> seats, MountControllerSupplier supplier) {
        return this.mountLeastOccupied(entity, seats, supplier, null);
    }

    @Override
    public boolean mountLeastOccupied(Entity entity, Collection<String> seats, MountControllerSupplier supplier, @Nullable Consumer<MountController> consumer) {
        int leastCount = Integer.MAX_VALUE;
        Mount leastOccupied = null;
        for (String seatId : seats) {
            BoneBehavior seat;
            Optional maybeSeat = this.getSeat(seatId);
            if (maybeSeat.isEmpty() || !((Mount)((Object)(seat = (BoneBehavior)maybeSeat.get()))).canMountMore()) continue;
            int count = ((Mount)((Object)seat)).getPassengers().size();
            if (count == 0) {
                return this.mountPassenger((Mount)((Object)seat), entity, supplier, consumer);
            }
            if (leastCount <= count) continue;
            leastCount = count;
            leastOccupied = (Mount)((Object)seat);
        }
        if (leastOccupied != null) {
            return this.mountPassenger(leastOccupied, entity, supplier, consumer);
        }
        return false;
    }

    @Override
    public Set<Entity> mountLeastOccupied(Collection<Entity> entities, Collection<String> seats, MountControllerSupplier supplier) {
        return this.mountLeastOccupied(entities, seats, supplier, null);
    }

    @Override
    public Set<Entity> mountLeastOccupied(Collection<Entity> entities, Collection<String> seats, MountControllerSupplier supplier, @Nullable Consumer<MountController> consumer) {
        HashSet<Entity> set = new HashSet<Entity>();
        for (Entity entity : entities) {
            if (this.mountLeastOccupied(entity, seats, supplier, consumer)) continue;
            set.add(entity);
        }
        return set;
    }

    @Override
    public Entity dismountDriver() {
        if (this.driverBone != null && this.driver != null) {
            ModelDismountEvent event = new ModelDismountEvent(this.activeModel, this.driver, true, this.driverBone);
            ModelEngineAPI.callEvent(event);
            if (event.isCancelled()) {
                return null;
            }
            this.driverBone.removePassenger(this.driver);
            this.removeMountPair(this.driver);
            Entity t = this.driver;
            this.driver = null;
            return t;
        }
        return null;
    }

    @Override
    public void dismountPassenger(@NotNull Entity entity) {
        Mount mount = this.passengerSeatMap.remove(entity);
        if (mount != null) {
            ModelDismountEvent event = new ModelDismountEvent(this.activeModel, entity, false, mount);
            ModelEngineAPI.callEvent(event);
            if (event.isCancelled()) {
                return;
            }
            mount.removePassenger(entity);
            this.removeMountPair(entity);
        }
    }

    @Override
    public void dismountRider(@NotNull Entity entity) {
        if (entity == this.driver) {
            this.dismountDriver();
        } else {
            this.dismountPassenger(entity);
        }
    }

    @Override
    public Set<Entity> dismountPassengers(String boneId) {
        HashSet<Entity> removed = new HashSet<Entity>();
        this.getSeat(boneId).ifPresent(mount -> {
            Set<Entity> passengers = ((Mount)((Object)mount)).getPassengers();
            for (Entity entity : passengers) {
                ModelDismountEvent event = new ModelDismountEvent(this.activeModel, entity, false, (Mount)((Object)mount));
                ModelEngineAPI.callEvent(event);
                if (event.isCancelled()) continue;
                ((Mount)((Object)mount)).removePassenger(entity);
                this.removeMountPair(entity);
                removed.add(entity);
            }
        });
        return removed;
    }

    @Override
    public Set<Entity> dismountAll() {
        HashSet<Entity> set = new HashSet<Entity>();
        for (BoneBehavior val : this.getSeats().values()) {
            Set<Entity> passengers = ((Mount)((Object)val)).clearPassengers();
            for (Entity entity : passengers) {
                ModelDismountEvent event = new ModelDismountEvent(this.activeModel, entity, false, (Mount)((Object)val));
                ModelEngineAPI.callEvent(event);
                if (event.isCancelled()) continue;
                this.passengerSeatMap.remove(entity);
                this.removeMountPair(entity);
                set.add(entity);
            }
        }
        return set;
    }

    private void registerMountPair(Entity entity, Mount mount, MountControllerSupplier supplier, @Nullable Consumer<MountController> consumer) {
        MountController controller = supplier.createController(entity, mount);
        if (consumer != null) {
            consumer.accept(controller);
        }
        ModelEngineAPI.getMountPairManager().registerMountedPair(entity, this.activeModel, controller);
        this.activeModel.getModeledEntity().getBase().setCollidableWith(entity, false);
    }

    private void removeMountPair(Entity entity) {
        ModelEngineAPI.getMountPairManager().unregisterMountedPair(entity.getUniqueId());
        this.activeModel.getModeledEntity().getBase().setCollidableWith(entity, true);
    }

    @Override
    @Generated
    public void setCanDrive(boolean canDrive) {
        this.canDrive = canDrive;
    }

    @Override
    @Generated
    public void setCanRide(boolean canRide) {
        this.canRide = canRide;
    }

    @Override
    @Generated
    public Entity getDriver() {
        return this.driver;
    }
}

