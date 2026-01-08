/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 */
package com.ticxo.modelengine.api.utils.math;

import com.ticxo.modelengine.api.utils.math.TMath;
import lombok.Generated;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public enum Direction {
    NORTH(new Vector3d(0.0, 0.0, -1.0), new Vector3d(0.0, 1.0, 0.0).normalize()),
    EAST(new Vector3d(1.0, 0.0, 0.0), new Vector3d(0.0, 1.0, 0.0).normalize()),
    SOUTH(new Vector3d(0.0, 0.0, 1.0), new Vector3d(0.0, 1.0, 0.0).normalize()),
    WEST(new Vector3d(-1.0, 0.0, 0.0), new Vector3d(0.0, 1.0, 0.0).normalize()),
    UP(new Vector3d(0.0, 1.0, 0.0), new Vector3d(0.0, 0.0, -1.0).normalize()),
    DOWN(new Vector3d(0.0, -1.0, 0.0), new Vector3d(0.0, 0.0, 1.0).normalize());

    private final Vector3d normal;
    private final Vector3d uvUp;

    public Vector3d getNormal() {
        return new Vector3d((Vector3dc)this.normal);
    }

    public Vector3d getUvUp() {
        return new Vector3d((Vector3dc)this.uvUp);
    }

    public static Direction fromNormal(Vector3d normal) {
        for (Direction value : Direction.values()) {
            if (!TMath.isSimilar(value.normal.dot((Vector3dc)normal), 1.0)) continue;
            return value;
        }
        return null;
    }

    @Generated
    private Direction(Vector3d normal, Vector3d uvUp) {
        this.normal = normal;
        this.uvUp = uvUp;
    }
}

