/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Position
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.Mob
 *  net.minecraft.world.entity.ai.navigation.FlyingPathNavigation
 *  net.minecraft.world.level.pathfinder.Path
 *  net.minecraft.world.phys.Vec3
 *  org.jetbrains.annotations.Nullable
 */
package com.ticxo.modelengine.v1_21_R7.entity.navigation;

import net.minecraft.core.Position;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class FlyingNavigationWrapper
extends FlyingPathNavigation {
    public FlyingNavigationWrapper(Mob mob, FlyingPathNavigation oldNav) {
        super(mob, mob.level());
        this.setCanFloat(oldNav.canFloat());
        oldNav.getNodeEvaluator().setCanOpenDoors(oldNav.getNodeEvaluator().canOpenDoors());
        oldNav.getNodeEvaluator().setCanPassDoors(oldNav.getNodeEvaluator().canPassDoors());
    }

    public boolean moveTo(@Nullable Path newPath, double speed) {
        if (newPath != null && !newPath.sameAs(this.path)) {
            int closestNode = newPath.getNextNodeIndex();
            double distanceSqr = this.getNodeDistanceSquared(newPath.getNextEntityPos((Entity)this.mob));
            for (int i = closestNode + 1; i < newPath.getNodeCount(); ++i) {
                double temp = this.getNodeDistanceSquared(newPath.getEntityPosAtNode((Entity)this.mob, i));
                if (!(temp < distanceSqr)) continue;
                distanceSqr = temp;
                closestNode = i;
            }
            newPath.setNextNodeIndex(closestNode);
        }
        return super.moveTo(newPath, speed);
    }

    private double getNodeDistanceSquared(Vec3 pos) {
        double x = pos.x - this.mob.getX();
        double y = pos.y - this.mob.getY();
        double z = pos.z - this.mob.getZ();
        return x * x + y * y + z * z;
    }

    protected void followThePath() {
        boolean canAdvance;
        Vec3 mobPosition = this.getTempMobPos();
        this.maxDistanceToWaypoint = this.mob.getBbWidth() > 0.75f ? this.mob.getBbWidth() / 2.0f : 0.75f - this.mob.getBbWidth() / 2.0f;
        Vec3 targetNodePosition = this.path.getNextEntityPos((Entity)this.mob);
        double distX = Math.abs(this.mob.getX() - targetNodePosition.x);
        double distY = Math.abs(this.mob.getY() - targetNodePosition.y);
        double distZ = Math.abs(this.mob.getZ() - targetNodePosition.z);
        boolean hasArrivedNode = distX < (double)this.maxDistanceToWaypoint && distZ < (double)this.maxDistanceToWaypoint && distY < 1.0;
        boolean bl = canAdvance = hasArrivedNode || this.canCutCorner(this.path.getNextNode().type) && this.shouldTargetNextNodeInDirection(mobPosition);
        if (canAdvance) {
            this.path.advance();
        }
        this.doStuckDetection(mobPosition);
    }

    private boolean shouldTargetNextNodeInDirection(Vec3 mobPosition) {
        Vec3 mobDir;
        if (this.path.getNextNodeIndex() + 1 >= this.path.getNodeCount()) {
            return false;
        }
        Vec3 targetNodePosition = this.path.getNextEntityPos((Entity)this.mob);
        if (!mobPosition.closerThan((Position)targetNodePosition, 2.0)) {
            return false;
        }
        if (this.canMoveDirectly(mobPosition, this.path.getNextEntityPos((Entity)this.mob))) {
            return true;
        }
        Vec3 nextNodePosition = this.path.getEntityPosAtNode((Entity)this.mob, this.path.getNextNodeIndex() + 1);
        Vec3 nextNodeDir = nextNodePosition.subtract(targetNodePosition);
        return nextNodeDir.dot(mobDir = mobPosition.subtract(targetNodePosition)) > 0.0;
    }
}

