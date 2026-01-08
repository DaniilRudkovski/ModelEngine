/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 *  org.bukkit.entity.Entity
 */
package com.ticxo.modelengine.api.mount.controller;

import com.ticxo.modelengine.api.model.bone.type.Mount;
import com.ticxo.modelengine.api.mount.controller.MountController;
import com.ticxo.modelengine.api.mount.controller.MountControllerSupplier;
import java.util.function.BiFunction;
import lombok.Generated;
import org.bukkit.entity.Entity;

public class MountControllerType
implements MountControllerSupplier {
    private final BiFunction<Entity, Mount, MountController> controllerConstructor;

    @Override
    public MountController createController(Entity entity, Mount mount) {
        return this.controllerConstructor.apply(entity, mount);
    }

    @Generated
    public MountControllerType(BiFunction<Entity, Mount, MountController> controllerConstructor) {
        this.controllerConstructor = controllerConstructor;
    }
}

