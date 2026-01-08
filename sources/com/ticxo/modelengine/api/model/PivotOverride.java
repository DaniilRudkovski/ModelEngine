/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 *  org.bukkit.entity.Entity
 *  org.jetbrains.annotations.NotNull
 *  org.joml.Vector3f
 */
package com.ticxo.modelengine.api.model;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.utils.data.tracker.CollectionDataTracker;
import java.util.HashSet;
import java.util.UUID;
import java.util.function.Supplier;
import lombok.Generated;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public class PivotOverride {
    private final UUID uuid;
    private final int id;
    private final Supplier<Vector3f> mountOffsetSupplier;
    private final CollectionDataTracker<Integer> passengers = new CollectionDataTracker(new HashSet());

    public void addPassenger(int id) {
        this.passengers.add(id);
    }

    public void removePassenger(int id) {
        this.passengers.remove(id);
    }

    public Vector3f getMountOffset() {
        return this.mountOffsetSupplier.get();
    }

    public static PivotOverride create(@NotNull Entity entity) {
        return new PivotOverride(entity.getUniqueId(), entity.getEntityId(), () -> ModelEngineAPI.getEntityHandler().getPivotOffset(entity));
    }

    @Generated
    public UUID getUuid() {
        return this.uuid;
    }

    @Generated
    public int getId() {
        return this.id;
    }

    @Generated
    public Supplier<Vector3f> getMountOffsetSupplier() {
        return this.mountOffsetSupplier;
    }

    @Generated
    public CollectionDataTracker<Integer> getPassengers() {
        return this.passengers;
    }

    @Generated
    public PivotOverride(UUID uuid, int id, Supplier<Vector3f> mountOffsetSupplier) {
        this.uuid = uuid;
        this.id = id;
        this.mountOffsetSupplier = mountOffsetSupplier;
    }
}

