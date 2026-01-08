/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPosition
 *  net.minecraft.core.IPosition
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityInsentient
 *  net.minecraft.world.entity.ai.navigation.AmphibiousPathNavigation
 *  net.minecraft.world.level.pathfinder.PathEntity
 *  net.minecraft.world.phys.Vec3D
 *  org.jetbrains.annotations.Nullable
 */
package com.ticxo.modelengine.v1_20_R2.entity.navigation;

import net.minecraft.core.BlockPosition;
import net.minecraft.core.IPosition;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.ai.navigation.AmphibiousPathNavigation;
import net.minecraft.world.level.pathfinder.PathEntity;
import net.minecraft.world.phys.Vec3D;
import org.jetbrains.annotations.Nullable;

public class AmphibiousNavigationWrapper
extends AmphibiousPathNavigation {
    private final AmphibiousPathNavigation oldNav;

    public AmphibiousNavigationWrapper(EntityInsentient mob, AmphibiousPathNavigation oldNav) {
        super(mob, mob.dL());
        this.oldNav = oldNav;
        this.a(oldNav.p());
    }

    public boolean a(@Nullable PathEntity newPath, double speed) {
        if (newPath != null && !newPath.a(this.c)) {
            int closestNode = newPath.f();
            double distanceSqr = this.getNodeDistanceSquared(newPath.a((Entity)this.a));
            for (int i = closestNode + 1; i < newPath.e(); ++i) {
                double temp = this.getNodeDistanceSquared(newPath.a((Entity)this.a, i));
                if (!(temp < distanceSqr)) continue;
                distanceSqr = temp;
                closestNode = i;
            }
            newPath.c(closestNode);
        }
        return super.a(newPath, speed);
    }

    private double getNodeDistanceSquared(Vec3D pos) {
        double x = pos.c - this.a.dq();
        double y = pos.d - this.a.ds();
        double z = pos.e - this.a.dw();
        return x * x + y * y + z * z;
    }

    protected void k() {
        boolean canAdvance;
        Vec3D mobPosition = this.b();
        this.l = this.a.df() > 0.75f ? this.a.df() / 2.0f : 0.75f - this.a.df() / 2.0f;
        Vec3D targetNodePosition = this.c.a((Entity)this.a);
        double distX = Math.abs(this.a.dq() - targetNodePosition.c);
        double distY = Math.abs(this.a.ds() - targetNodePosition.d);
        double distZ = Math.abs(this.a.dw() - targetNodePosition.e);
        boolean hasArrivedNode = distX < (double)this.l && distZ < (double)this.l && distY < 1.0;
        boolean bl = canAdvance = hasArrivedNode || this.b(this.c.h().l) && this.shouldTargetNextNodeInDirection(mobPosition);
        if (canAdvance) {
            this.c.a();
        }
        this.b(mobPosition);
    }

    private boolean shouldTargetNextNodeInDirection(Vec3D mobPosition) {
        Vec3D mobDir;
        if (this.c.f() + 1 >= this.c.e()) {
            return false;
        }
        Vec3D targetNodePosition = this.c.a((Entity)this.a);
        if (!mobPosition.a((IPosition)targetNodePosition, 2.0)) {
            return false;
        }
        if (this.a(mobPosition, this.c.a((Entity)this.a))) {
            return true;
        }
        Vec3D nextNodePosition = this.c.a((Entity)this.a, this.c.f() + 1);
        Vec3D nextNodeDir = nextNodePosition.d(targetNodePosition);
        return nextNodeDir.b(mobDir = mobPosition.d(targetNodePosition)) > 0.0;
    }

    public boolean a(BlockPosition var0) {
        return this.oldNav.a(var0);
    }
}

