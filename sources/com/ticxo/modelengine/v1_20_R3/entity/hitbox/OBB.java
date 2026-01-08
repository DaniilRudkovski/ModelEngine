/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 *  net.minecraft.world.phys.AxisAlignedBB
 *  net.minecraft.world.phys.Vec3D
 *  org.jetbrains.annotations.NotNull
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 *  org.joml.Vector3f
 */
package com.ticxo.modelengine.v1_20_R3.entity.hitbox;

import com.ticxo.modelengine.api.utils.math.OrientedBoundingBox;
import java.util.Optional;
import lombok.Generated;
import net.minecraft.world.phys.AxisAlignedBB;
import net.minecraft.world.phys.Vec3D;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;

public class OBB
extends AxisAlignedBB {
    private final Quaternionf rotation;
    private final float yaw;
    private final OrientedBoundingBox bukkitOBB;

    public OBB(Vec3D cornerA, Vec3D cornerB, Quaternionf rotation, float yaw) {
        this(cornerA.c, cornerA.d, cornerA.e, cornerB.c, cornerB.d, cornerB.e, rotation, yaw);
    }

    public OBB(double minX, double minY, double minZ, double maxX, double maxY, double maxZ, Quaternionf rotation, float yaw) {
        super(minX, minY, minZ, maxX, maxY, maxZ);
        this.rotation = rotation;
        this.yaw = yaw;
        this.bukkitOBB = new OrientedBoundingBox(this.f().j(), new Vector3f((float)this.b(), (float)this.c(), (float)this.d()), rotation, yaw);
    }

    public OBB makeOBBInstance(Vec3D position, Quaternionf rotation, float yaw) {
        return new OBB(position.b(this.a, this.b, this.c), position.b(this.d, this.e, this.f), rotation, yaw);
    }

    @NotNull
    public AxisAlignedBB c(double xInflate, double yInflate, double zInflate) {
        double minX = this.a - xInflate;
        double minY = this.b - yInflate;
        double minZ = this.c - zInflate;
        double maxX = this.d + xInflate;
        double maxY = this.e + yInflate;
        double maxZ = this.f + zInflate;
        return new OBB(minX, minY, minZ, maxX, maxY, maxZ, this.rotation, this.yaw);
    }

    public boolean c(@NotNull AxisAlignedBB aabb) {
        boolean bl;
        if (aabb instanceof OBB) {
            OBB obb = (OBB)aabb;
            bl = this.intersects(obb);
        } else {
            bl = super.c(aabb);
        }
        return bl;
    }

    public boolean intersects(OBB obb) {
        return this.bukkitOBB.intersects(obb.bukkitOBB);
    }

    public boolean a(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        OrientedBoundingBox obbB = new OrientedBoundingBox((float)minX, (float)minY, (float)minZ, (float)maxX, (float)maxY, (float)maxZ);
        return this.bukkitOBB.intersects(obbB);
    }

    public Optional<Vec3D> b(Vec3D from, Vec3D to) {
        Vec3D rTo;
        Quaternionf inverse = this.rotation.conjugate(new Quaternionf());
        Vec3D center = this.f();
        float yaw = this.yaw * ((float)Math.PI / 180);
        Vec3D rFrom = new Vec3D(from.d(center).j().rotateY(-yaw).rotate((Quaternionfc)inverse)).e(center);
        Optional result = super.b(rFrom, rTo = new Vec3D(to.d(center).j().rotateY(-yaw).rotate((Quaternionfc)inverse)).e(center));
        if (result.isEmpty()) {
            return result;
        }
        Vec3D clip = new Vec3D(((Vec3D)result.get()).d(center).j().rotate((Quaternionfc)this.rotation).rotateY(yaw)).e(center);
        return Optional.of(clip);
    }

    @Generated
    public String toString() {
        return "OBB(rotation=" + this.getRotation() + ", yaw=" + this.getYaw() + ", bukkitOBB=" + this.getBukkitOBB() + ")";
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

