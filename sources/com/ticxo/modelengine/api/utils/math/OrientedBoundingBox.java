/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 *  org.bukkit.Color
 *  org.bukkit.Location
 *  org.bukkit.Particle
 *  org.bukkit.Particle$DustOptions
 *  org.bukkit.World
 *  org.bukkit.entity.ItemDisplay
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.util.BoundingBox
 *  org.bukkit.util.Consumer
 *  org.bukkit.util.RayTraceResult
 *  org.bukkit.util.Transformation
 *  org.bukkit.util.Vector
 *  org.jetbrains.annotations.NotNull
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 */
package com.ticxo.modelengine.api.utils.math;

import com.ticxo.modelengine.api.utils.math.TMath;
import lombok.Generated;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Consumer;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class OrientedBoundingBox {
    private static final Vector3f GLOBAL_RIGHT = new Vector3f(1.0f, 0.0f, 0.0f);
    private static final Vector3f GLOBAL_UP = new Vector3f(0.0f, 1.0f, 0.0f);
    private static final Vector3f GLOBAL_FORWARD = new Vector3f(0.0f, 0.0f, 1.0f);
    private final Vector3f origin;
    private final Quaternionf rotation;
    private final Vector3f right;
    private final Vector3f up;
    private final Vector3f forward;
    private final float halfX;
    private final float halfY;
    private final float halfZ;

    public OrientedBoundingBox(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
        this.origin = new Vector3f((maxX + minX) * 0.5f, (maxY + minY) * 0.5f, (maxZ + minZ) * 0.5f);
        this.rotation = new Quaternionf();
        this.right = new Vector3f((Vector3fc)GLOBAL_RIGHT);
        this.up = new Vector3f((Vector3fc)GLOBAL_UP);
        this.forward = new Vector3f((Vector3fc)GLOBAL_FORWARD);
        this.halfX = (maxX - minX) * 0.5f;
        this.halfY = (maxY - minY) * 0.5f;
        this.halfZ = (maxZ - minZ) * 0.5f;
    }

    public OrientedBoundingBox(Vector3f origin, Vector3f dimension, Vector3f rotation, float yaw) {
        this(origin, dimension, new Quaternionf().rotationXYZ(rotation.x, rotation.y, rotation.z), yaw);
    }

    public OrientedBoundingBox(Vector3f origin, Vector3f dimension, Quaternionf rotation, float yaw) {
        this.origin = new Vector3f((Vector3fc)origin);
        this.rotation = rotation.rotateLocalY(yaw * ((float)Math.PI / 180), new Quaternionf());
        this.right = GLOBAL_RIGHT.rotate((Quaternionfc)this.rotation, new Vector3f());
        this.up = GLOBAL_UP.rotate((Quaternionfc)this.rotation, new Vector3f());
        this.forward = GLOBAL_FORWARD.rotate((Quaternionfc)this.rotation, new Vector3f());
        this.halfX = dimension.x * 0.5f;
        this.halfY = dimension.y * 0.5f;
        this.halfZ = dimension.z * 0.5f;
    }

    public boolean intersects(BoundingBox aabb) {
        return this.intersects(new OrientedBoundingBox((float)aabb.getMinX(), (float)aabb.getMinY(), (float)aabb.getMinZ(), (float)aabb.getMaxX(), (float)aabb.getMaxY(), (float)aabb.getMaxZ()));
    }

    public boolean intersects(OrientedBoundingBox obb) {
        Vector3f traversed = new Vector3f(obb.origin.x - this.origin.x, obb.origin.y - this.origin.y, obb.origin.z - this.origin.z);
        for (int i = 0; i < 15; ++i) {
            double proj;
            Vector3f check = this.getL(i, obb);
            double t = this.projectionOnAxis(traversed, check);
            if (!(t > (proj = this.projectionOnAxis(new Vector3f((Vector3fc)this.right).mul(this.halfX), check) + this.projectionOnAxis(new Vector3f((Vector3fc)this.up).mul(this.halfY), check) + this.projectionOnAxis(new Vector3f((Vector3fc)this.forward).mul(this.halfZ), check) + this.projectionOnAxis(new Vector3f((Vector3fc)obb.right).mul(obb.halfX), check) + this.projectionOnAxis(new Vector3f((Vector3fc)obb.up).mul(obb.halfY), check) + this.projectionOnAxis(new Vector3f((Vector3fc)obb.forward).mul(obb.halfZ), check)))) continue;
            return false;
        }
        return true;
    }

    public RayTraceResult rayTrace(@NotNull Vector3f start, @NotNull Vector3f direction, double maxDistance, Consumer<BoundingBox> consumer) {
        RayTraceResult result;
        if (!this.origin.isFinite()) {
            return new RayTraceResult(new Vector((double)start.x + (double)direction.x * maxDistance, (double)start.y + (double)direction.y * maxDistance, (double)start.z + (double)direction.z * maxDistance));
        }
        Quaternionf inverse = this.rotation.conjugate(new Quaternionf());
        Vector3f relativeStart = start.sub((Vector3fc)this.origin, new Vector3f()).rotate((Quaternionfc)inverse).add((Vector3fc)this.origin);
        Vector3f relativeDirection = new Vector3f((Vector3fc)direction).rotate((Quaternionfc)inverse);
        BoundingBox bb = BoundingBox.of((Vector)new Vector(this.origin.x, this.origin.y, this.origin.z), (double)this.halfX, (double)this.halfY, (double)this.halfZ);
        if (consumer != null) {
            consumer.accept((Object)bb);
        }
        if ((result = bb.rayTrace(new Vector(relativeStart.x, relativeStart.y, relativeStart.z), new Vector(relativeDirection.x, relativeDirection.y, relativeDirection.z), maxDistance)) == null) {
            return null;
        }
        Vector3f hitPosition = result.getHitPosition().toVector3f().sub((Vector3fc)this.origin).rotate((Quaternionfc)this.rotation).add((Vector3fc)this.origin);
        return new RayTraceResult(new Vector(hitPosition.x, hitPosition.y, hitPosition.z), result.getHitBlockFace());
    }

    public double distanceSquared(@NotNull Vector3f vector) {
        if (!this.origin.isFinite()) {
            return Double.NaN;
        }
        Quaternionf inverse = this.rotation.conjugate(new Quaternionf());
        Vector3f relativeVector = vector.sub((Vector3fc)this.origin, new Vector3f()).rotate((Quaternionfc)inverse).add((Vector3fc)this.origin);
        BoundingBox bb = BoundingBox.of((Vector)new Vector(this.origin.x, this.origin.y, this.origin.z), (double)this.halfX, (double)this.halfY, (double)this.halfZ);
        return TMath.distanceSquaredToBoundingBox(Vector.fromJOML((Vector3f)relativeVector), bb);
    }

    public boolean contains(@NotNull Vector3f vector) {
        Quaternionf inverse = this.rotation.conjugate(new Quaternionf());
        Vector3f relativeVector = vector.sub((Vector3fc)this.origin, new Vector3f()).rotate((Quaternionfc)inverse).add((Vector3fc)this.origin);
        BoundingBox bb = BoundingBox.of((Vector)new Vector(this.origin.x, this.origin.y, this.origin.z), (double)this.halfX, (double)this.halfY, (double)this.halfZ);
        return bb.contains(Vector.fromJOML((Vector3f)relativeVector));
    }

    private double projectionOnAxis(Vector3f vector, Vector3f axis) {
        return Math.abs(vector.dot((Vector3fc)axis));
    }

    private Vector3f getL(int i, OrientedBoundingBox other) {
        return switch (i) {
            case 0 -> this.right;
            case 1 -> this.up;
            case 2 -> this.forward;
            case 3 -> other.right;
            case 4 -> other.up;
            case 5 -> other.forward;
            case 6 -> this.right.cross((Vector3fc)other.right, new Vector3f());
            case 7 -> this.right.cross((Vector3fc)other.up, new Vector3f());
            case 8 -> this.right.cross((Vector3fc)other.forward, new Vector3f());
            case 9 -> this.up.cross((Vector3fc)other.right, new Vector3f());
            case 10 -> this.up.cross((Vector3fc)other.up, new Vector3f());
            case 11 -> this.up.cross((Vector3fc)other.forward, new Vector3f());
            case 12 -> this.forward.cross((Vector3fc)other.right, new Vector3f());
            case 13 -> this.forward.cross((Vector3fc)other.up, new Vector3f());
            case 14 -> this.forward.cross((Vector3fc)other.forward, new Vector3f());
            default -> throw new IllegalStateException("Unexpected value: " + i);
        };
    }

    public ItemDisplay visualize(World world, ItemStack stack) {
        Location location = new Location(world, (double)this.origin.x, (double)this.origin.y, (double)this.origin.z);
        return (ItemDisplay)world.spawn(location, ItemDisplay.class, itemDisplay -> {
            itemDisplay.setItemStack(stack);
            itemDisplay.setTransformation(new Transformation(new Vector3f(), this.rotation, new Vector3f(2.0f * this.halfX, 2.0f * this.halfY, 2.0f * this.halfZ), new Quaternionf()));
        });
    }

    public void visualize(World world) {
        Vector3f offsetX = new Vector3f((Vector3fc)this.right).mul(this.halfX);
        Vector3f offsetY = new Vector3f((Vector3fc)this.up).mul(this.halfY);
        Vector3f offsetZ = new Vector3f((Vector3fc)this.forward).mul(this.halfZ);
        Vector3f offsetXN = new Vector3f((Vector3fc)offsetX).mul(-1.0f);
        Vector3f offsetYN = new Vector3f((Vector3fc)offsetY).mul(-1.0f);
        Vector3f offsetZN = new Vector3f((Vector3fc)offsetZ).mul(-1.0f);
        this.drawLine(offsetX, offsetY, offsetZ, world, Color.ORANGE, this.halfZ * 2.0f);
        this.drawLine(offsetX, offsetYN, offsetZ, world, Color.ORANGE, this.halfZ * 2.0f);
        this.drawLine(offsetXN, offsetY, offsetZ, world, Color.ORANGE, this.halfZ * 2.0f);
        this.drawLine(offsetXN, offsetYN, offsetZ, world, Color.ORANGE, this.halfZ * 2.0f);
        this.drawLine(offsetY, offsetZ, offsetX, world, Color.GREEN, this.halfX * 2.0f);
        this.drawLine(offsetY, offsetZN, offsetX, world, Color.GREEN, this.halfX * 2.0f);
        this.drawLine(offsetYN, offsetZ, offsetX, world, Color.GREEN, this.halfX * 2.0f);
        this.drawLine(offsetYN, offsetZN, offsetX, world, Color.GREEN, this.halfX * 2.0f);
        this.drawLine(offsetZ, offsetX, offsetY, world, Color.AQUA, this.halfY * 2.0f);
        this.drawLine(offsetZ, offsetXN, offsetY, world, Color.AQUA, this.halfY * 2.0f);
        this.drawLine(offsetZN, offsetX, offsetY, world, Color.AQUA, this.halfY * 2.0f);
        this.drawLine(offsetZN, offsetXN, offsetY, world, Color.AQUA, this.halfY * 2.0f);
    }

    private void drawLine(Vector3f offset1, Vector3f offset2, Vector3f line, World world, Color color, double dist) {
        Vector3f offset = new Vector3f((Vector3fc)offset1).add((Vector3fc)offset2);
        Vector3f pointA = new Vector3f((Vector3fc)offset).add((Vector3fc)line);
        Vector3f pointB = new Vector3f((Vector3fc)offset).sub((Vector3fc)line);
        double ratio = 1.0 / dist;
        for (double i = 0.0; i < dist; i += 0.1) {
            Vector3f point = TMath.lerp(pointA, pointB, i * ratio);
            world.spawnParticle(Particle.REDSTONE, (double)(this.origin.x + point.x), (double)(this.origin.y + point.y), (double)(this.origin.z + point.z), 1, (Object)new Particle.DustOptions(color, 0.2f));
        }
    }

    @Generated
    public String toString() {
        return "OrientedBoundingBox(origin=" + this.origin + ", rotation=" + this.rotation + ", right=" + this.right + ", up=" + this.up + ", forward=" + this.forward + ", halfX=" + this.halfX + ", halfY=" + this.halfY + ", halfZ=" + this.halfZ + ")";
    }
}

