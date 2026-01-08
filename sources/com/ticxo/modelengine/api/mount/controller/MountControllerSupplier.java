/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Entity
 */
package com.ticxo.modelengine.api.mount.controller;

import com.ticxo.modelengine.api.model.bone.type.Mount;
import com.ticxo.modelengine.api.mount.controller.MountController;
import org.bukkit.entity.Entity;

@FunctionalInterface
public interface MountControllerSupplier {
    public MountController createController(Entity var1, Mount var2);
}

