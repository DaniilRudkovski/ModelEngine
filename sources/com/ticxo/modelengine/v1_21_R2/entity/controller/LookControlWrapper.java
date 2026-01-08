/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.Mob
 *  net.minecraft.world.entity.ai.control.LookControl
 *  net.minecraft.world.phys.Vec3
 *  org.bukkit.entity.Entity
 *  org.spigotmc.ActivationRange
 */
package com.ticxo.modelengine.v1_21_R2.entity.controller;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.model.ModeledEntity;
import com.ticxo.modelengine.api.model.bone.BoneBehaviorTypes;
import com.ticxo.modelengine.api.model.bone.behavior.GlobalBehaviorData;
import com.ticxo.modelengine.api.model.bone.manager.BehaviorManager;
import com.ticxo.modelengine.api.model.bone.manager.MountData;
import com.ticxo.modelengine.api.model.bone.manager.MountManager;
import com.ticxo.modelengine.api.model.bone.type.Mount;
import com.ticxo.modelengine.api.mount.controller.MountController;
import com.ticxo.modelengine.api.nms.entity.wrapper.LookController;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.phys.Vec3;
import org.bukkit.entity.Entity;
import org.spigotmc.ActivationRange;

public class LookControlWrapper
extends LookControl
implements LookController {
    private final LookControl original;
    private ModeledEntity modeledEntity;

    public LookControlWrapper(Mob mob, LookControl control) {
        super(mob);
        this.original = control;
    }

    public void setLookAt(Vec3 var0) {
        this.original.setLookAt(var0);
    }

    public void setLookAt(net.minecraft.world.entity.Entity var0) {
        this.original.setLookAt(var0);
    }

    public void setLookAt(net.minecraft.world.entity.Entity var0, float var1, float var2) {
        this.original.setLookAt(var0, var1, var2);
    }

    public void setLookAt(double var0, double var2, double var4) {
        this.original.setLookAt(var0, var2, var4);
    }

    public void setLookAt(double var0, double var2, double var4, float var6, float var7) {
        this.original.setLookAt(var0, var2, var4, var6, var7);
    }

    public void tick() {
        Object mountManager;
        if (this.modeledEntity == null) {
            this.modeledEntity = ModelEngineAPI.getModeledEntity(this.mob.getUUID());
        }
        if ((mountManager = this.getMainManager()) != null && ((MountManager)mountManager).isControlled()) {
            this.controlledTick(mountManager);
        } else {
            this.defaultTick();
        }
    }

    protected <T extends BehaviorManager<? extends Mount> & MountManager> void controlledTick(T manager) {
        MountController controller;
        Entity driver = ((MountManager)manager).getDriver();
        if (driver != null && (controller = ModelEngineAPI.getMountPairManager().getController(driver.getUniqueId())) != null) {
            controller.updateDirection(this, manager.getActiveModel());
            return;
        }
        this.defaultTick();
    }

    protected void defaultTick() {
        if (this.isActive()) {
            this.original.tick();
        }
    }

    public boolean isLookingAtTarget() {
        return this.original.isLookingAtTarget();
    }

    public double getWantedX() {
        return this.original.getWantedX();
    }

    public double getWantedY() {
        return this.original.getWantedY();
    }

    public double getWantedZ() {
        return this.original.getWantedZ();
    }

    @Override
    public void lookAt(double x, double y, double z) {
        double dX = x - this.mob.getX();
        double dY = y - this.mob.getEyeY();
        double dZ = z - this.mob.getZ();
        double hMagnitude = Math.sqrt(dX * dX + dZ * dZ);
        float pitch = (float)Math.toDegrees(Math.atan2(-dY, hMagnitude));
        float yaw = (float)Math.toDegrees(Math.atan2(-dX, dZ));
        this.setPitch(pitch);
        this.setHeadYaw(yaw);
    }

    @Override
    public void setPitch(float pitch) {
        this.mob.setXRot(pitch);
    }

    @Override
    public void setHeadYaw(float yaw) {
        this.mob.setYRot(yaw);
        this.mob.yHeadRot = yaw;
    }

    @Override
    public void setBodyYaw(float yaw) {
        this.mob.yBodyRot = yaw;
    }

    private <T extends BehaviorManager<? extends Mount> & MountManager> T getMainManager() {
        if (this.modeledEntity == null) {
            return null;
        }
        GlobalBehaviorData data = this.modeledEntity.getGlobalBehaviorData(BoneBehaviorTypes.MOUNT);
        if (data instanceof MountData) {
            MountData mountData = (MountData)((Object)data);
            return mountData.getMainMountManager();
        }
        return null;
    }

    private boolean isActive() {
        return ActivationRange.checkIfActive((net.minecraft.world.entity.Entity)this.mob);
    }
}

