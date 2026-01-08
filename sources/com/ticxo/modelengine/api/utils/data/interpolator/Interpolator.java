/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 *  org.jetbrains.annotations.Nullable
 */
package com.ticxo.modelengine.api.utils.data.interpolator;

import java.util.TreeMap;
import lombok.Generated;
import org.jetbrains.annotations.Nullable;

public class Interpolator<IN, OUT>
extends TreeMap<Float, IN> {
    protected Interpolation<IN> interpolateFunc;
    protected Parse<IN, OUT> parseFunc;
    protected OUT defaultValue = null;

    public Interpolator<IN, OUT> setInterpolateFunc(Interpolation<IN> interpolateFunc) {
        this.interpolateFunc = interpolateFunc;
        return this;
    }

    public Interpolator<IN, OUT> setParseFunc(Parse<IN, OUT> parseFunc) {
        this.parseFunc = parseFunc;
        return this;
    }

    public Interpolator<IN, OUT> setDefaultValue(OUT value) {
        this.defaultValue = value;
        return this;
    }

    @Nullable
    public OUT interpolate(float key) {
        float lastKey;
        if (this.isEmpty()) {
            return this.defaultValue;
        }
        if (this.containsKey(Float.valueOf(key))) {
            return this.parseFunc.parse(this.get(Float.valueOf(key)));
        }
        float nextKey = this.getHigherKey(key);
        if (nextKey == (lastKey = this.getLowerKey(key))) {
            return this.parseFunc.parse(this.get(Float.valueOf(lastKey)));
        }
        float t = (key - lastKey) / (nextKey - lastKey);
        Object next = this.get(Float.valueOf(nextKey));
        Object prev = this.get(Float.valueOf(lastKey));
        return this.parseFunc.parse(this.interpolateFunc.interpolate(new Context(lastKey, nextKey), prev, next, t));
    }

    public float getHigherKey(float time) {
        Float high = this.higherKey(Float.valueOf(time));
        if (high == null) {
            return ((Float)this.lastKey()).floatValue();
        }
        return high.floatValue();
    }

    public float getLowerKey(float time) {
        Float low = this.lowerKey(Float.valueOf(time));
        if (low == null) {
            return ((Float)this.firstKey()).floatValue();
        }
        return low.floatValue();
    }

    @FunctionalInterface
    public static interface Interpolation<IN> {
        public IN interpolate(Context var1, IN var2, IN var3, float var4);
    }

    @FunctionalInterface
    public static interface Parse<IN, OUT> {
        public OUT parse(IN var1);
    }

    public static class Context {
        public final float prevKey;
        public final float nextKey;

        @Generated
        public Context(float prevKey, float nextKey) {
            this.prevKey = prevKey;
            this.nextKey = nextKey;
        }
    }
}

