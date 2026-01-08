/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Entity
 *  org.bukkit.util.Vector
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
import org.bukkit.util.Vector;

public class FlyingMountController
extends AbstractMountController {
    private final boolean force;

    public FlyingMountController(Entity entity, Mount mount, boolean force) {
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
        controller.nullifyFallDistance();
        Vector original = controller.getVelocity();
        controller.setVelocity(original.getX(), 0.0, original.getZ());
        if (this.input.isSneak()) {
            if (!controller.isOnGround()) {
                controller.addVelocity(0.0, -controller.getSpeed(), 0.0);
            } else if (!this.force) {
                ((MountManager)((Object)manager)).dismountDriver();
                controller.move(0.0f, 0.0f, 0.0f, 0.0f);
                return;
            }
        }
        if (this.input.isJump()) {
            controller.addVelocity(0.0, controller.getSpeed(), 0.0);
        }
        controller.move(this.input.getSide(), 0.0f, this.input.getFront(), 1.0f);
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

