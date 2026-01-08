/*
 * Decompiled with CFR 0.152.
 */
package com.ticxo.modelengine.api.utils;

import com.ticxo.modelengine.api.utils.math.TMath;

public enum StepFlag {
    POSITION,
    ROTATION,
    SCALE;


    public byte setStep(byte data, boolean flag) {
        return TMath.setBit(data, this.ordinal(), flag);
    }

    public boolean isStepping(byte data) {
        return TMath.getBit(data, this.ordinal());
    }
}

