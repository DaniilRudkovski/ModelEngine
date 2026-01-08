/*
 * Decompiled with CFR 0.152.
 */
package com.ticxo.modelengine.api.nms.impl;

import com.ticxo.modelengine.api.nms.entity.wrapper.BodyRotationController;

public class EmptyBodyRotationController
implements BodyRotationController {
    @Override
    public float getYHeadRot() {
        return 0.0f;
    }

    @Override
    public void setYHeadRot(float rot) {
    }

    @Override
    public float getXHeadRot() {
        return 0.0f;
    }

    @Override
    public void setXHeadRot(float rot) {
    }

    @Override
    public float getYBodyRot() {
        return 0.0f;
    }

    @Override
    public void setYBodyRot(float rot) {
    }

    @Override
    public boolean isHeadClampUneven() {
        return false;
    }

    @Override
    public void setHeadClampUneven(boolean flag) {
    }

    @Override
    public boolean isBodyClampUneven() {
        return false;
    }

    @Override
    public void setBodyClampUneven(boolean flag) {
    }

    @Override
    public float getMaxHeadAngle() {
        return 0.0f;
    }

    @Override
    public void setMaxHeadAngle(float angle) {
    }

    @Override
    public float getMaxBodyAngle() {
        return 0.0f;
    }

    @Override
    public void setMaxBodyAngle(float angle) {
    }

    @Override
    public float getMinHeadAngle() {
        return 0.0f;
    }

    @Override
    public void setMinHeadAngle(float angle) {
    }

    @Override
    public float getMinBodyAngle() {
        return 0.0f;
    }

    @Override
    public void setMinBodyAngle(float angle) {
    }

    @Override
    public float getStableAngle() {
        return 0.0f;
    }

    @Override
    public void setStableAngle(float angle) {
    }

    @Override
    public boolean isPlayerMode() {
        return false;
    }

    @Override
    public void setPlayerMode(boolean flag) {
    }

    @Override
    public int getRotationDelay() {
        return 0;
    }

    @Override
    public void setRotationDelay(int delay) {
    }

    @Override
    public int getRotationDuration() {
        return 0;
    }

    @Override
    public void setRotationDuration(int duration) {
    }
}

