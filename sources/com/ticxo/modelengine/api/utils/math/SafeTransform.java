/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 */
package com.ticxo.modelengine.api.utils.math;

import com.ticxo.modelengine.api.utils.math.Transform;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class SafeTransform
extends Transform {
    private final Transform lastTransform = new Transform();
    private final AtomicReference<Transform> safe = new AtomicReference<Transform>(new Transform());

    public void recordLast() {
        this.lastTransform.position.set((Vector3fc)this.position);
        this.lastTransform.leftEuler.set((Vector3fc)this.leftEuler);
        this.lastTransform.scale.set((Vector3fc)this.scale);
        this.lastTransform.rightEuler.set((Vector3fc)this.rightEuler);
        this.lastTransform.leftQuaternion.set((Quaternionfc)this.leftQuaternion);
        this.lastTransform.rightQuaternion.set((Quaternionfc)this.rightQuaternion);
    }

    public void recordSafe() {
        this.safe.updateAndGet(transform -> {
            transform.position.set((Vector3fc)this.position);
            transform.leftEuler.set((Vector3fc)this.leftEuler);
            transform.scale.set((Vector3fc)this.scale);
            transform.rightEuler.set((Vector3fc)this.rightEuler);
            transform.leftQuaternion.set((Quaternionfc)this.leftQuaternion);
            transform.rightQuaternion.set((Quaternionfc)this.rightQuaternion);
            return transform;
        });
    }

    public Vector3f getSafePosition() {
        return this.safe.get().getPosition();
    }

    public Vector3f getSafeLeftEuler() {
        return this.safe.get().getLeftEuler();
    }

    public Vector3f getSafeScale() {
        return this.safe.get().getScale();
    }

    public Vector3f getSafeRightEuler() {
        return this.safe.get().getRightEuler();
    }

    public Quaternionf getSafeLeftQuaternion() {
        return this.safe.get().getLeftQuaternion();
    }

    public Quaternionf getSafeRightQuaternion() {
        return this.safe.get().getRightQuaternion();
    }

    public void mutateSafe(Consumer<Transform> consumer) {
        consumer.accept(this.safe.get());
    }

    public Vector3f getLastPosition() {
        return this.lastTransform.getPosition();
    }

    public Vector3f getLastLeftEuler() {
        return this.lastTransform.getLeftEuler();
    }

    public Vector3f getLastScale() {
        return this.lastTransform.getScale();
    }

    public Vector3f getLastRightEuler() {
        return this.lastTransform.getRightEuler();
    }

    public Quaternionf getLastLeftQuaternion() {
        return this.lastTransform.getLeftQuaternion();
    }

    public Quaternionf getLastRightQuaternion() {
        return this.lastTransform.getRightQuaternion();
    }
}

