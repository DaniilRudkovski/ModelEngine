/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 */
package com.ticxo.modelengine.api.utils.data;

import lombok.Generated;

public class Triple<A, B, C> {
    private A first;
    private B second;
    private C third;

    @Generated
    public A getFirst() {
        return this.first;
    }

    @Generated
    public B getSecond() {
        return this.second;
    }

    @Generated
    public C getThird() {
        return this.third;
    }

    @Generated
    public void setFirst(A first) {
        this.first = first;
    }

    @Generated
    public void setSecond(B second) {
        this.second = second;
    }

    @Generated
    public void setThird(C third) {
        this.third = third;
    }

    @Generated
    public Triple(A first, B second, C third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }
}

