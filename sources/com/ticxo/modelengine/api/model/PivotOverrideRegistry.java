/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  org.bukkit.entity.Entity
 */
package com.ticxo.modelengine.api.model;

import com.google.common.collect.Maps;
import com.ticxo.modelengine.api.model.PivotOverride;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.bukkit.entity.Entity;

public class PivotOverrideRegistry {
    private final Map<UUID, PivotOverride> overrides = Maps.newConcurrentMap();

    public Optional<PivotOverride> get(UUID uuid) {
        return Optional.ofNullable(uuid == null ? null : this.overrides.get(uuid));
    }

    public void register(PivotOverride override) {
        this.overrides.put(override.getUuid(), override);
    }

    public void remove(UUID uuid) {
        this.overrides.remove(uuid);
    }

    public PivotOverride getOrCreate(Entity entity) {
        return this.overrides.computeIfAbsent(entity.getUniqueId(), uuid -> PivotOverride.create(entity));
    }

    public Set<PivotOverride> collectDirty() {
        return this.overrides.values().stream().filter(override -> override.getPassengers().isDirty()).collect(Collectors.toSet());
    }
}

