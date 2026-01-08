/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 *  org.joml.Vector3d
 */
package com.ticxo.modelengine.api.utils.math;

import lombok.Generated;
import org.joml.Vector3d;

public enum Axis {
    X(new Vector3d(1.0, 0.0, 0.0)),
    Y(new Vector3d(0.0, 1.0, 0.0)),
    Z(new Vector3d(0.0, 0.0, 1.0));

    private final Vector3d vector;

    public Vector3d getVector() {
        return this.vector.get(new Vector3d());
    }

    @Generated
    private Axis(Vector3d vector) {
        this.vector = vector;
    }
}

