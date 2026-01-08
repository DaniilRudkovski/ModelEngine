/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.util.Vector
 *  org.joml.Vector3f
 */
package com.ticxo.modelengine.api.animation.rootmotion;

import org.bukkit.util.Vector;
import org.joml.Vector3f;

public record RootMotion(Vector3f velocity, float yaw, boolean boneOnGround) {
    public Vector delta() {
        return Vector.fromJOML((Vector3f)this.velocity).rotateAroundY((double)(-this.yaw * ((float)Math.PI / 180)));
    }
}

