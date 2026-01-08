/*
 * Decompiled with CFR 0.152.
 */
package org.mineskin.data;

import java.util.Optional;
import org.mineskin.data.JobInfo;
import org.mineskin.data.JobReference;
import org.mineskin.data.SkinInfo;

public class NullJobReference
implements JobReference {
    private final JobInfo job;

    public NullJobReference(JobInfo job) {
        this.job = job;
    }

    @Override
    public JobInfo getJob() {
        return this.job;
    }

    @Override
    public Optional<SkinInfo> getSkin() {
        return Optional.empty();
    }

    public String toString() {
        return "NullJobReference{job=" + String.valueOf(this.job) + "}";
    }
}

