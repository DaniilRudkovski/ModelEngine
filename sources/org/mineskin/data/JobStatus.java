/*
 * Decompiled with CFR 0.152.
 */
package org.mineskin.data;

public enum JobStatus {
    WAITING,
    PROCESSING,
    COMPLETED,
    FAILED,
    UNKNOWN;


    public boolean isPending() {
        return this == WAITING || this == PROCESSING;
    }

    public boolean isDone() {
        return this == COMPLETED || this == FAILED;
    }
}

