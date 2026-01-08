/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.ticxo.modelengine.api.animation.keyframe.type;

import com.ticxo.modelengine.api.animation.property.IAnimationProperty;
import lombok.Generated;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractKeyframe<T> {
    protected final float[] leftTime = new float[3];
    protected final float[] leftValue = new float[3];
    protected final float[] rightTime = new float[3];
    protected final float[] rightValue = new float[3];
    @NotNull
    protected String interpolation = "";

    public boolean isBezier() {
        return "bezier".equalsIgnoreCase(this.interpolation);
    }

    public void setBezierLeftTime(@Nullable Float x, @Nullable Float y, @Nullable Float z) {
        if (x != null) {
            this.leftTime[0] = x.floatValue();
        }
        if (y != null) {
            this.leftTime[1] = y.floatValue();
        }
        if (z != null) {
            this.leftTime[2] = z.floatValue();
        }
    }

    public void setBezierLeftValue(@Nullable Float x, @Nullable Float y, @Nullable Float z) {
        if (x != null) {
            this.leftValue[0] = x.floatValue();
        }
        if (y != null) {
            this.leftValue[1] = y.floatValue();
        }
        if (z != null) {
            this.leftValue[2] = z.floatValue();
        }
    }

    public void setBezierRightTime(@Nullable Float x, @Nullable Float y, @Nullable Float z) {
        if (x != null) {
            this.rightTime[0] = x.floatValue();
        }
        if (y != null) {
            this.rightTime[1] = y.floatValue();
        }
        if (z != null) {
            this.rightTime[2] = z.floatValue();
        }
    }

    public void setBezierRightValue(@Nullable Float x, @Nullable Float y, @Nullable Float z) {
        if (x != null) {
            this.rightValue[0] = x.floatValue();
        }
        if (y != null) {
            this.rightValue[1] = y.floatValue();
        }
        if (z != null) {
            this.rightValue[2] = z.floatValue();
        }
    }

    public abstract T getValue(int var1, IAnimationProperty var2);

    @Generated
    public float[] getLeftTime() {
        return this.leftTime;
    }

    @Generated
    public float[] getLeftValue() {
        return this.leftValue;
    }

    @Generated
    public float[] getRightTime() {
        return this.rightTime;
    }

    @Generated
    public float[] getRightValue() {
        return this.rightValue;
    }

    @NotNull
    @Generated
    public String getInterpolation() {
        return this.interpolation;
    }

    @Generated
    public void setInterpolation(@NotNull String interpolation) {
        if (interpolation == null) {
            throw new NullPointerException("interpolation is marked non-null but is null");
        }
        this.interpolation = interpolation;
    }
}

