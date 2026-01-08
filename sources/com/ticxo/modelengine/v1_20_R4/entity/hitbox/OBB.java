/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 *  org.jetbrains.annotations.NotNull
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 *  org.joml.Vector3f
 */
package com.ticxo.modelengine.v1_20_R4.entity.hitbox;

import com.ticxo.modelengine.api.utils.math.OrientedBoundingBox;
import java.util.Optional;
import lombok.Generated;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;

public class OBB
extends AABB {
    private final Quaternionf rotation;
    private final float yaw;
    private final OrientedBoundingBox bukkitOBB;

    public OBB(Vec3 cornerA, Vec3 cornerB, Quaternionf rotation, float yaw) {
        this(cornerA.x, cornerA.y, cornerA.z, cornerB.x, cornerB.y, cornerB.z, rotation, yaw);
    }

    public OBB(double minX, double minY, double minZ, double maxX, double maxY, double maxZ, Quaternionf rotation, float yaw) {
        super(minX, minY, minZ, maxX, maxY, maxZ);
        this.rotation = rotation;
        this.yaw = yaw;
        this.bukkitOBB = new OrientedBoundingBox(this.getCenter().toVector3f(), new Vector3f((float)this.getXsize(), (float)this.getYsize(), (float)this.getZsize()), rotation, yaw);
    }

    public OBB makeOBBInstance(Vec3 position, Quaternionf rotation, float yaw) {
        return new OBB(position.add(this.minX, this.minY, this.minZ), position.add(this.maxX, this.maxY, this.maxZ), rotation, yaw);
    }

    @NotNull
    public AABB inflate(double xInflate, double yInflate, double zInflate) {
        double minX = this.minX - xInflate;
        double minY = this.minY - yInflate;
        double minZ = this.minZ - zInflate;
        double maxX = this.maxX + xInflate;
        double maxY = this.maxY + yInflate;
        double maxZ = this.maxZ + zInflate;
        return new OBB(minX, minY, minZ, maxX, maxY, maxZ, this.rotation, this.yaw);
    }

    public boolean intersects(@NotNull AABB aabb) {
        boolean bl;
        if (aabb instanceof OBB) {
            OBB obb = (OBB)aabb;
            bl = this.intersects(obb);
        } else {
            bl = super.intersects(aabb);
        }
        return bl;
    }

    public boolean intersects(OBB obb) {
        return this.bukkitOBB.intersects(obb.bukkitOBB);
    }

    public boolean intersects(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        OrientedBoundingBox obbB = new OrientedBoundingBox((float)minX, (float)minY, (float)minZ, (float)maxX, (float)maxY, (float)maxZ);
        return this.bukkitOBB.intersects(obbB);
    }

    public Optional<Vec3> clip(Vec3 from, Vec3 to) {
        Vec3 rTo;
        Quaternionf inverse = this.rotation.conjugate(new Quaternionf());
        Vec3 center = this.getCenter();
        float yaw = this.yaw * ((float)Math.PI / 180);
        Vec3 rFrom = new Vec3(from.subtract(center).toVector3f().rotateY(-yaw).rotate((Quaternionfc)inverse)).add(center);
        Optional result = super.clip(rFrom, rTo = new Vec3(to.subtract(center).toVector3f().rotateY(-yaw).rotate((Quaternionfc)inverse)).add(center));
        if (result.isEmpty()) {
            return result;
        }
        Vec3 clip = new Vec3(((Vec3)result.get()).subtract(center).toVector3f().rotate((Quaternionfc)this.rotation).rotateY(yaw)).add(center);
        return Optional.of(clip);
    }

    @Generated
    public String toString() {
        return "OBB(rotation=" + String.valueOf(this.getRotation()) + ", yaw=" + this.getYaw() + ", bukkitOBB=" + String.valueOf(this.getBukkitOBB()) + ")";
    }

    @Generated
    public Quaternionf getRotation() {
        return this.rotation;
    }

    @Generated
    public float getYaw() {
        return this.yaw;
    }

    @Generated
    public OrientedBoundingBox getBukkitOBB() {
        return this.bukkitOBB;
    }
}

