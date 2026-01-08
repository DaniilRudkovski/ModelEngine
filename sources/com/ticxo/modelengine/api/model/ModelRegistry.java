/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Ordering
 */
package com.ticxo.modelengine.api.model;

import com.google.common.collect.Ordering;
import com.ticxo.modelengine.api.generator.blueprint.ModelBlueprint;
import com.ticxo.modelengine.api.utils.registry.TUnaryRegistry;
import java.util.Collection;
import java.util.PriorityQueue;

public class ModelRegistry
extends TUnaryRegistry<ModelBlueprint> {
    private final PriorityQueue<String> orderedId = new PriorityQueue(Ordering.natural());

    public void registerBlueprint(ModelBlueprint blueprint) {
        this.orderedId.add(blueprint.getName());
        this.register(blueprint.getName(), blueprint);
    }

    public void clearRegistry() {
        this.orderedId.clear();
        this.registry.clear();
    }

    public Collection<String> getOrderedId() {
        return this.orderedId;
    }
}

