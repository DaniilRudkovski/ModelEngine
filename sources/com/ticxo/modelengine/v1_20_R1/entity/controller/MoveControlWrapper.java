/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.entity.EntityInsentient
 *  net.minecraft.world.entity.EnumMoveType
 *  net.minecraft.world.entity.ai.attributes.GenericAttributes
 *  net.minecraft.world.entity.ai.control.ControllerMove
 *  net.minecraft.world.phys.Vec3D
 *  org.apache.logging.log4j.util.TriConsumer
 *  org.bukkit.craftbukkit.v1_20_R1.util.CraftVector
 *  org.bukkit.entity.Entity
 *  org.bukkit.util.Vector
 */
package com.ticxo.modelengine.v1_20_R1.entity.controller;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.animation.rootmotion.RootMotionDelta;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.ModeledEntity;
import com.ticxo.modelengine.api.model.bone.behavior.BoneBehavior;
import com.ticxo.modelengine.api.model.bone.manager.BehaviorManager;
import com.ticxo.modelengine.api.model.bone.manager.MountData;
import com.ticxo.modelengine.api.model.bone.manager.MountManager;
import com.ticxo.modelengine.api.model.bone.type.Mount;
import com.ticxo.modelengine.api.mount.controller.MountController;
import com.ticxo.modelengine.api.nms.entity.wrapper.MoveController;
import com.ticxo.modelengine.api.utils.ReflectionUtils;
import com.ticxo.modelengine.api.utils.math.TMath;
import com.ticxo.modelengine.v1_20_R1.NMSFields;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EnumMoveType;
import net.minecraft.world.entity.ai.attributes.GenericAttributes;
import net.minecraft.world.entity.ai.control.ControllerMove;
import net.minecraft.world.phys.Vec3D;
import org.apache.logging.log4j.util.TriConsumer;
import org.bukkit.craftbukkit.v1_20_R1.util.CraftVector;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

public class MoveControlWrapper
extends ControllerMove
implements MoveController {
    protected final ControllerMove original;
    protected final Queue<Runnable> runnables = new ConcurrentLinkedQueue<Runnable>();
    protected boolean isOnGround;

    public MoveControlWrapper(EntityInsentient mob, ControllerMove control) {
        super(mob);
        this.original = control;
    }

    public boolean b() {
        return this.original.b();
    }

    public double c() {
        return this.original.c();
    }

    public void a(double x, double y, double z, double speed) {
        this.original.a(x, y, z, speed);
    }

    public void a(float forward, float sideways) {
        this.original.a(forward, sideways);
    }

    public void a() {
        Vector velocity;
        BehaviorManager<? extends Mount> & MountManager mainMountManager;
        this.isOnGround = this.d.ay();
        ModeledEntity modeledEntity = ModelEngineAPI.getModeledEntity(this.d.ct());
        if (modeledEntity == null) {
            this.defaultTick();
            return;
        }
        Object mountData = modeledEntity.getMountData();
        BehaviorManager<? extends Mount> & MountManager t = mainMountManager = mountData == null ? null : (BehaviorManager<? extends Mount> & MountManager)((MountData)mountData).getMainMountManager();
        if (mainMountManager != null && ((MountManager)mainMountManager).isControlled()) {
            this.d.c(true);
            this.disableWaterJumping();
            this.driverTick(mainMountManager);
        } else {
            this.defaultTick();
        }
        this.passengerTick(modeledEntity, mainMountManager);
        while (!this.runnables.isEmpty()) {
            this.runnables.poll().run();
        }
        Vector mobMotion = this.toVector();
        RootMotionDelta rootMotion = modeledEntity.getRootMotionHandler().calculateRootMotion(mobMotion);
        this.fromVector(mobMotion);
        if (!(rootMotion == null || (velocity = rootMotion.delta()).isZero() && rootMotion.onGround())) {
            this.nullifyFallDistance();
            if (!TMath.isSimilar((float)velocity.getY(), 0.0f) || !rootMotion.onGround()) {
                this.d.f(this.d.dl().d(1.0, 0.0, 1.0));
            }
            this.d.a(EnumMoveType.a, new Vec3D(velocity.getX(), velocity.getY(), velocity.getZ()));
        }
    }

    public double d() {
        return this.original.d();
    }

    public double e() {
        return this.original.e();
    }

    public double f() {
        return this.original.f();
    }

    protected <T extends BehaviorManager<? extends Mount> & MountManager> void driverTick(T manager) {
        this.d.z(0.0f);
        this.d.B(0.0f);
        this.updateRider(((MountManager)manager).getDriver(), manager.getActiveModel(), (Mount)((MountManager)manager).getDriverBone(), (TriConsumer<MountController, MoveController, ActiveModel>)((TriConsumer)MountController::updateDriverMovement));
    }

    protected <T extends BehaviorManager<? extends Mount> & MountManager> void passengerTick(ModeledEntity modeledEntity, T manager) {
        for (ActiveModel activeModel : modeledEntity.getModels().values()) {
            activeModel.getMountManager().ifPresent(mountManager -> {
                for (BoneBehavior seat : ((MountManager)((Object)mountManager)).getSeats().values()) {
                    for (Entity passenger : ((Mount)((Object)seat)).getPassengers()) {
                        this.updateRider(passenger, activeModel, (Mount)((Object)seat), (TriConsumer<MountController, MoveController, ActiveModel>)((TriConsumer)MountController::updatePassengerMovement));
                    }
                }
                if (mountManager != manager && ((MountManager)((Object)mountManager)).isControlled()) {
                    this.updateRider(((MountManager)((Object)mountManager)).getDriver(), activeModel, (Mount)((MountManager)((Object)mountManager)).getDriverBone(), (TriConsumer<MountController, MoveController, ActiveModel>)((TriConsumer)MountController::updatePassengerMovement));
                }
            });
        }
    }

    private void updateRider(Entity entity, ActiveModel activeModel, Mount mountBone, TriConsumer<MountController, MoveController, ActiveModel> updateMethod) {
        MountController controller = this.getController(entity.getUniqueId());
        if (controller == null) {
            return;
        }
        if (controller.getInput() == null) {
            controller.setInput(new MountController.MountInput());
        }
        updateMethod.accept((Object)controller, (Object)this, (Object)activeModel);
    }

    protected void defaultTick() {
        this.original.a();
    }

    private void disableWaterJumping() {
        if (this.d.aV()) {
            ReflectionUtils.set(this.d, NMSFields.LIVING_ENTITY_noJumpDelay, 1);
        }
    }

    @Override
    public void move(float side, float up, float front, float speedModifier) {
        float speed = this.getSpeed();
        this.d.w(speed * speedModifier);
        this.d.z(front);
        this.d.A(up);
        this.d.B(side);
    }

    @Override
    public void globalMove(float x, float y, float z, float speedModifier) {
        float speed = this.getSpeed();
        this.d.w(speed * speedModifier);
        Vec3D vec = new Vec3D((double)x, (double)y, (double)z).b(-this.d.dy() * ((float)Math.PI / 180));
        this.d.B((float)vec.c);
        this.d.A((float)vec.d);
        this.d.z((float)vec.e);
    }

    private void fromVector(Vector vector) {
        Vec3D vec = new Vec3D(vector.getX(), vector.getY(), vector.getZ()).b(-this.d.dy() * ((float)Math.PI / 180));
        this.d.B((float)vec.c);
        this.d.A((float)vec.d);
        this.d.z((float)vec.e);
    }

    private Vector toVector() {
        return new Vector(Float.isNaN(this.d.bl) ? 0.0f : this.d.bl, Float.isNaN(this.d.bm) ? 0.0f : this.d.bm, Float.isNaN(this.d.bn) ? 0.0f : this.d.bn).rotateAroundY((double)(this.d.dy() * ((float)Math.PI / 180)));
    }

    @Override
    public void jump() {
        this.d.I().a();
    }

    @Override
    public void setVelocity(double x, double y, double z) {
        this.d.o(x, y, z);
    }

    @Override
    public void addVelocity(double x, double y, double z) {
        this.d.f(this.d.dl().b(x, y, z));
    }

    @Override
    public void nullifyFallDistance() {
        this.d.n();
    }

    @Override
    public boolean isOnGround() {
        return this.isOnGround;
    }

    @Override
    public boolean isInWater() {
        return this.d.aV();
    }

    @Override
    public float getSpeed() {
        return (float)this.d.b(GenericAttributes.d);
    }

    @Override
    public Vector getVelocity() {
        return CraftVector.toBukkit((Vec3D)this.d.dl());
    }

    @Override
    public void queuePostTick(Runnable runnable) {
        this.runnables.add(runnable);
    }

    private MountController getController(UUID uuid) {
        return ModelEngineAPI.getMountPairManager().getController(uuid);
    }
}

