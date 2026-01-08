/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Entity
 */
package com.ticxo.modelengine.api.mount.controller;

import com.ticxo.modelengine.api.model.bone.type.Mount;
import com.ticxo.modelengine.api.mount.controller.MountControllerType;
import com.ticxo.modelengine.api.mount.controller.impl.FlyingMountController;
import com.ticxo.modelengine.api.mount.controller.impl.WalkingMountController;
import org.bukkit.entity.Entity;

public class MountControllerTypes {
    public static final MountControllerType WALKING = new MountControllerType((entity, mount) -> new WalkingMountController((Entity)entity, (Mount)mount, false));
    public static final MountControllerType WALKING_FORCE = new MountControllerType((entity, mount) -> new WalkingMountController((Entity)entity, (Mount)mount, true));
    public static final MountControllerType FLYING = new MountControllerType((entity, mount) -> new FlyingMountController((Entity)entity, (Mount)mount, false));
    public static final MountControllerType FLYING_FORCE = new MountControllerType((entity, mount) -> new FlyingMountController((Entity)entity, (Mount)mount, true));
}

