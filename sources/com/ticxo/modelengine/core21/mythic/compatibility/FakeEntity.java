/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.lumine.mythic.bukkit.compatibility.AbstractModelEngineSupport$MEGAttachmentAnchor
 *  io.lumine.mythic.bukkit.utils.serialize.Orient
 *  org.bukkit.Location
 *  org.bukkit.entity.Entity
 *  org.bukkit.util.Vector
 *  org.jetbrains.annotations.NotNull
 */
package com.ticxo.modelengine.core21.mythic.compatibility;

import com.ticxo.modelengine.api.entity.BukkitEntity;
import com.ticxo.modelengine.api.nms.entity.wrapper.BodyRotationController;
import io.lumine.mythic.bukkit.compatibility.AbstractModelEngineSupport;
import io.lumine.mythic.bukkit.utils.serialize.Orient;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class FakeEntity
extends BukkitEntity {
    private final UUID uuid = UUID.randomUUID();
    private final Vector offset;
    private final double yaw;
    private final double pitch;
    private final AbstractModelEngineSupport.MEGAttachmentAnchor anchor;

    public FakeEntity(@NotNull Entity entity, Orient orient, AbstractModelEngineSupport.MEGAttachmentAnchor anchor) {
        super(entity);
        this.offset = orient.getLocus().toVector();
        this.yaw = Math.toRadians(orient.getDirection().getYaw());
        this.pitch = Math.toRadians(orient.getDirection().getPitch());
        this.anchor = anchor;
        this.getBodyRotationController().setMaxHeadAngle(45.0f);
        this.getBodyRotationController().setMaxBodyAngle(45.0f);
        this.getBodyRotationController().setStableAngle(5.0f);
    }

    @Override
    public UUID getUUID() {
        return this.uuid;
    }

    @Override
    public Location getLocation() {
        Vector offset;
        BodyRotationController controller = this.getBodyRotationController();
        Location location = this.getOriginal().getLocation();
        if (this.anchor == AbstractModelEngineSupport.MEGAttachmentAnchor.HEAD) {
            double pYaw = Math.toRadians(location.getYaw());
            double pPitch = Math.toRadians(location.getPitch());
            offset = FakeEntity.rotateYaw(FakeEntity.rotatePitch(this.offset.clone(), this.pitch + pPitch), this.yaw + pYaw);
        } else {
            double pYaw = Math.toRadians(controller == null ? (double)this.getYBodyRot() : (double)controller.getYBodyRot());
            offset = FakeEntity.rotateYaw(this.offset.clone(), this.yaw + pYaw);
        }
        return location.add(offset);
    }

    public static Vector rotatePitch(Vector vec, double pitch) {
        double sin = Math.sin(pitch);
        double cos = Math.cos(pitch);
        double y = vec.getY() * cos - vec.getZ() * sin;
        double z = vec.getY() * sin + vec.getZ() * cos;
        return vec.setY(y).setZ(z);
    }

    public static Vector rotateYaw(Vector vec, double yaw) {
        double sin = Math.sin(yaw);
        double cos = Math.cos(yaw);
        double x = vec.getX() * cos - vec.getZ() * sin;
        double z = vec.getX() * sin + vec.getZ() * cos;
        return vec.setX(x).setZ(z);
    }
}

