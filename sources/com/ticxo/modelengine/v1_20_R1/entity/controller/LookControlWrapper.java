/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityInsentient
 *  net.minecraft.world.entity.ai.control.ControllerLook
 *  net.minecraft.world.phys.Vec3D
 *  org.bukkit.entity.Entity
 *  org.spigotmc.ActivationRange
 */
package com.ticxo.modelengine.v1_20_R1.entity.controller;

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
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.ai.control.ControllerLook;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.entity.Entity;
import org.spigotmc.ActivationRange;

public class LookControlWrapper
extends ControllerLook
implements LookController {
    private final ControllerLook original;
    private ModeledEntity modeledEntity;

    public LookControlWrapper(EntityInsentient mob, ControllerLook control) {
        super(mob);
        this.original = control;
    }

    public void a(Vec3D var0) {
        this.original.a(var0);
    }

    public void a(net.minecraft.world.entity.Entity var0) {
        this.original.a(var0);
    }

    public void a(net.minecraft.world.entity.Entity var0, float var1, float var2) {
        this.original.a(var0, var1, var2);
    }

    public void a(double var0, double var2, double var4) {
        this.original.a(var0, var2, var4);
    }

    public void a(double var0, double var2, double var4, float var6, float var7) {
        this.original.a(var0, var2, var4, var6, var7);
    }

    public void a() {
        Object mountManager;
        if (this.modeledEntity == null) {
            this.modeledEntity = ModelEngineAPI.getModeledEntity(this.a.ct());
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
            this.original.a();
        }
    }

    public boolean d() {
        return this.original.d();
    }

    public double e() {
        return this.original.e();
    }

    public double f() {
        return this.original.f();
    }

    public double g() {
        return this.original.g();
    }

    @Override
    public void lookAt(double x, double y, double z) {
        double dX = x - this.a.dn();
        double dY = y - this.a.dr();
        double dZ = z - this.a.dt();
        double hMagnitude = Math.sqrt(dX * dX + dZ * dZ);
        float pitch = (float)Math.toDegrees(Math.atan2(-dY, hMagnitude));
        float yaw = (float)Math.toDegrees(Math.atan2(-dX, dZ));
        this.setPitch(pitch);
        this.setHeadYaw(yaw);
    }

    @Override
    public void setPitch(float pitch) {
        this.a.b_(pitch);
    }

    @Override
    public void setHeadYaw(float yaw) {
        this.a.a_(yaw);
        this.a.aX = yaw;
    }

    @Override
    public void setBodyYaw(float yaw) {
        this.a.aV = yaw;
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
        return ActivationRange.checkIfActive((net.minecraft.world.entity.Entity)this.a);
    }
}

