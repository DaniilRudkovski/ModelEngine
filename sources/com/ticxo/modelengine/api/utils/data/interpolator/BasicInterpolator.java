/*
 * Decompiled with CFR 0.152.
 */
package com.ticxo.modelengine.api.utils.data.interpolator;

import com.ticxo.modelengine.api.utils.data.interpolator.Interpolator;

public class BasicInterpolator<IN>
extends Interpolator<IN, IN> {
    public BasicInterpolator() {
        this.setParseFunc(value -> value);
    }
}

