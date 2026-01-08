/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joml.Matrix4f
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 */
package com.ticxo.modelengine.api.utils.math;

import com.ticxo.modelengine.api.utils.math.TMath;
import java.util.function.Consumer;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class Transform {
    protected final Vector3f position = new Vector3f();
    protected final Vector3f leftEuler = new Vector3f();
    protected final Vector3f scale = new Vector3f(1.0f);
    protected final Vector3f rightEuler = new Vector3f();
    protected final Quaternionf leftQuaternion = new Quaternionf();
    protected final Quaternionf rightQuaternion = new Quaternionf();

    public void identity() {
        this.position.set(0.0f);
        this.leftEuler.set(0.0f);
        this.scale.set(1.0f);
        this.rightEuler.set(0.0f);
        this.leftQuaternion.identity();
        this.rightQuaternion.identity();
    }

    public Vector3f getPosition() {
        return new Vector3f((Vector3fc)this.position);
    }

    public Vector3f getLeftEuler() {
        return new Vector3f((Vector3fc)this.leftEuler);
    }

    public Vector3f getScale() {
        return new Vector3f((Vector3fc)this.scale);
    }

    public Vector3f getRightEuler() {
        return new Vector3f((Vector3fc)this.rightEuler);
    }

    public Quaternionf getLeftQuaternion() {
        return new Quaternionf((Quaternionfc)this.leftQuaternion);
    }

    public Quaternionf getRightQuaternion() {
        return new Quaternionf((Quaternionfc)this.rightQuaternion);
    }

    public Vector3f setPosition(Vector3f value) {
        return this.position.set((Vector3fc)value);
    }

    public Vector3f setLeftEuler(Vector3f value) {
        this.leftEuler.set((Vector3fc)value);
        TMath.toQuaternion(this.leftEuler, this.leftQuaternion);
        return this.leftEuler;
    }

    public Vector3f setScale(Vector3f value) {
        return this.scale.set((Vector3fc)value);
    }

    public Vector3f setRightEuler(Vector3f value) {
        this.rightEuler.set((Vector3fc)value);
        TMath.toQuaternion(this.rightEuler, this.rightQuaternion);
        return this.rightEuler;
    }

    public Quaternionf setLeftQuaternion(Quaternionf value) {
        this.leftQuaternion.set((Quaternionfc)value);
        TMath.getEulerAnglesZYX(this.leftQuaternion, this.leftEuler);
        return this.leftQuaternion;
    }

    public Quaternionf setRightQuaternion(Quaternionf value) {
        this.rightQuaternion.set((Quaternionfc)value);
        TMath.getEulerAnglesZYX(this.rightQuaternion, this.rightEuler);
        return this.rightQuaternion;
    }

    public void mutatePosition(Consumer<Vector3f> mutator) {
        mutator.accept(this.position);
    }

    public void mutateLeftEuler(Consumer<Vector3f> mutator) {
        mutator.accept(this.leftEuler);
        TMath.toQuaternion(this.leftEuler, this.leftQuaternion);
    }

    public void mutateScale(Consumer<Vector3f> mutator) {
        mutator.accept(this.scale);
    }

    public void mutateRightEuler(Consumer<Vector3f> mutator) {
        mutator.accept(this.rightEuler);
        TMath.toQuaternion(this.rightEuler, this.rightQuaternion);
    }

    public void mutateLeftQuaternion(Consumer<Quaternionf> mutator) {
        mutator.accept(this.leftQuaternion);
        TMath.toEulerZYX(this.leftQuaternion, this.leftEuler);
    }

    public void mutateRightQuaternion(Consumer<Quaternionf> mutator) {
        mutator.accept(this.rightQuaternion);
        TMath.toEulerZYX(this.rightQuaternion, this.rightEuler);
    }

    public Matrix4f createMatrix() {
        return new Matrix4f().translate((Vector3fc)this.position).rotate((Quaternionfc)this.leftQuaternion).scale((Vector3fc)this.scale).rotate((Quaternionfc)this.rightQuaternion);
    }
}

