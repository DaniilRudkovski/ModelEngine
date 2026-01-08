/*
 * Decompiled with CFR 0.152.
 */
package org.mineskin.data;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import org.mineskin.MineSkinClient;
import org.mineskin.data.JobInfo;
import org.mineskin.data.SkinInfo;
import org.mineskin.response.SkinResponse;

public interface JobReference {
    public JobInfo getJob();

    public Optional<SkinInfo> getSkin();

    default public CompletableFuture<SkinInfo> getOrLoadSkin(MineSkinClient client) {
        if (this.getSkin().isPresent()) {
            return CompletableFuture.completedFuture(this.getSkin().get());
        }
        return this.getJob().getSkin(client).thenApply(SkinResponse::getSkin);
    }
}

