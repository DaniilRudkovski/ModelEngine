/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 *  org.bukkit.Location
 *  org.bukkit.entity.Entity
 */
package com.ticxo.modelengine.api.mount.controller.impl;

import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.bone.type.Mount;
import com.ticxo.modelengine.api.mount.controller.MountController;
import com.ticxo.modelengine.api.nms.entity.wrapper.LookController;
import lombok.Generated;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

public abstract class AbstractMountController
implements MountController {
    protected final Entity entity;
    protected final Mount mount;
    protected MountController.MountInput input;
    protected boolean canDamageMount;
    protected boolean canInteractMount;

    @Override
    public boolean canDamageMount() {
        return this.canDamageMount;
    }

    @Override
    public boolean canInteractMount() {
        return this.canInteractMount;
    }

    @Override
    public void updateDirection(LookController controller, ActiveModel model) {
        Location location = this.getEntity().getLocation();
        controller.setHeadYaw(location.getYaw());
        controller.setPitch(location.getPitch() * 0.5f);
    }

    @Generated
    public AbstractMountController(Entity entity, Mount mount) {
        this.entity = entity;
        this.mount = mount;
    }

    @Override
    @Generated
    public Entity getEntity() {
        return this.entity;
    }

    @Override
    @Generated
    public Mount getMount() {
        return this.mount;
    }

    @Override
    @Generated
    public MountController.MountInput getInput() {
        return this.input;
    }

    @Generated
    public boolean isCanDamageMount() {
        return this.canDamageMount;
    }

    @Generated
    public boolean isCanInteractMount() {
        return this.canInteractMount;
    }

    @Override
    @Generated
    public void setInput(MountController.MountInput input) {
        this.input = input;
    }

    @Override
    @Generated
    public void setCanDamageMount(boolean canDamageMount) {
        this.canDamageMount = canDamageMount;
    }

    @Override
    @Generated
    public void setCanInteractMount(boolean canInteractMount) {
        this.canInteractMount = canInteractMount;
    }
}

