/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 *  org.bukkit.entity.Entity
 *  org.joml.Vector3f
 */
package com.ticxo.modelengine.api.mount.controller;

import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.bone.type.Mount;
import com.ticxo.modelengine.api.nms.entity.wrapper.LookController;
import com.ticxo.modelengine.api.nms.entity.wrapper.MoveController;
import lombok.Generated;
import org.bukkit.entity.Entity;
import org.joml.Vector3f;

public interface MountController {
    public Entity getEntity();

    public MountInput getInput();

    public void setInput(MountInput var1);

    public Mount getMount();

    public void setCanDamageMount(boolean var1);

    public boolean canDamageMount();

    public void setCanInteractMount(boolean var1);

    public boolean canInteractMount();

    public void updateDriverMovement(MoveController var1, ActiveModel var2);

    public void updatePassengerMovement(MoveController var1, ActiveModel var2);

    default public void updateRiderPosition(MoveController controller) {
        Vector3f pos = this.getMount().getGlobalLocation();
        controller.movePassenger(this.getEntity(), pos.x, pos.y, pos.z);
    }

    public void updateDirection(LookController var1, ActiveModel var2);

    public static class MountInput {
        private Float side;
        private Float front;
        private boolean forward;
        private boolean backward;
        private boolean left;
        private boolean right;
        private boolean jump;
        private boolean sneak;
        private boolean sprint;
        private boolean updated;

        public MountInput() {
            this(false, false, false, false, false, false, false);
        }

        public MountInput(float side, float front, boolean jump, boolean sneak) {
            this.side = Float.valueOf(side);
            this.front = Float.valueOf(front);
            this.jump = jump;
            this.sneak = sneak;
        }

        public MountInput(boolean forward, boolean backward, boolean left, boolean right, boolean jump, boolean sneak, boolean sprint) {
            this.forward = forward;
            this.backward = backward;
            this.left = left;
            this.right = right;
            this.jump = jump;
            this.sneak = sneak;
            this.sprint = sprint;
        }

        public float getFront() {
            if (this.front != null) {
                return this.front.floatValue();
            }
            return this.forward ? 1.0f : (this.backward ? -1.0f : 0.0f);
        }

        public float getSide() {
            if (this.side != null) {
                return this.side.floatValue();
            }
            return this.left ? 1.0f : (this.right ? -1.0f : 0.0f);
        }

        @Generated
        public boolean isForward() {
            return this.forward;
        }

        @Generated
        public boolean isBackward() {
            return this.backward;
        }

        @Generated
        public boolean isLeft() {
            return this.left;
        }

        @Generated
        public boolean isRight() {
            return this.right;
        }

        @Generated
        public boolean isJump() {
            return this.jump;
        }

        @Generated
        public boolean isSneak() {
            return this.sneak;
        }

        @Generated
        public boolean isSprint() {
            return this.sprint;
        }

        @Generated
        public boolean isUpdated() {
            return this.updated;
        }

        @Generated
        public void setSide(Float side) {
            this.side = side;
        }

        @Generated
        public void setFront(Float front) {
            this.front = front;
        }

        @Generated
        public void setForward(boolean forward) {
            this.forward = forward;
        }

        @Generated
        public void setBackward(boolean backward) {
            this.backward = backward;
        }

        @Generated
        public void setLeft(boolean left) {
            this.left = left;
        }

        @Generated
        public void setRight(boolean right) {
            this.right = right;
        }

        @Generated
        public void setJump(boolean jump) {
            this.jump = jump;
        }

        @Generated
        public void setSneak(boolean sneak) {
            this.sneak = sneak;
        }

        @Generated
        public void setSprint(boolean sprint) {
            this.sprint = sprint;
        }

        @Generated
        public void setUpdated(boolean updated) {
            this.updated = updated;
        }
    }
}

