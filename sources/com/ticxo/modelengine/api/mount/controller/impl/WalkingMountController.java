/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Entity
 */
package com.ticxo.modelengine.api.mount.controller.impl;

import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.bone.manager.BehaviorManager;
import com.ticxo.modelengine.api.model.bone.manager.MountManager;
import com.ticxo.modelengine.api.model.bone.type.Mount;
import com.ticxo.modelengine.api.mount.controller.impl.AbstractMountController;
import com.ticxo.modelengine.api.nms.entity.wrapper.MoveController;
import java.util.Optional;
import org.bukkit.entity.Entity;

public class WalkingMountController
extends AbstractMountController {
    private final boolean force;

    public WalkingMountController(Entity entity, Mount mount, boolean force) {
        super(entity, mount);
        this.force = force;
    }

    @Override
    public void updateDriverMovement(MoveController controller, ActiveModel model) {
        Optional maybeMount = model.getMountManager();
        if (maybeMount.isEmpty()) {
            return;
        }
        BehaviorManager manager = (BehaviorManager)maybeMount.get();
        if (!this.force && this.input.isSneak()) {
            ((MountManager)((Object)manager)).dismountDriver();
            controller.move(0.0f, 0.0f, 0.0f, 0.0f);
            return;
        }
        controller.move(this.input.getSide(), 0.0f, this.input.getFront(), 1.0f);
        if (this.input.isJump() && (controller.isOnGround() || controller.isInWater())) {
            controller.jump();
        }
    }

    @Override
    public void updatePassengerMovement(MoveController controller, ActiveModel model) {
        Optional maybeMount = model.getMountManager();
        if (maybeMount.isEmpty()) {
            return;
        }
        BehaviorManager manager = (BehaviorManager)maybeMount.get();
        if (!this.force && this.input.isSneak()) {
            ((MountManager)((Object)manager)).dismountRider(this.entity);
        }
    }
}

