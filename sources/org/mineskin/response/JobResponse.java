/*
 * Decompiled with CFR 0.152.
 */
package org.mineskin.response;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import org.mineskin.MineSkinClient;
import org.mineskin.data.JobInfo;
import org.mineskin.data.JobReference;
import org.mineskin.data.SkinInfo;
import org.mineskin.response.MineSkinResponse;

public interface JobResponse
extends MineSkinResponse<JobInfo>,
JobReference {
    @Override
    public JobInfo getJob();

    @Override
    public Optional<SkinInfo> getSkin();

    @Override
    public CompletableFuture<SkinInfo> getOrLoadSkin(MineSkinClient var1);
}

