/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.JsonObject
 */
package org.mineskin.response;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import org.mineskin.MineSkinClient;
import org.mineskin.data.JobInfo;
import org.mineskin.data.SkinInfo;
import org.mineskin.response.AbstractMineSkinResponse;
import org.mineskin.response.JobResponse;

public class JobResponseImpl
extends AbstractMineSkinResponse<JobInfo>
implements JobResponse {
    private SkinInfo skin;

    public JobResponseImpl(int status, Map<String, String> headers, JsonObject rawBody, Gson gson, Class<JobInfo> clazz) {
        super(status, headers, rawBody, gson, "job", clazz);
        this.skin = (SkinInfo)gson.fromJson(rawBody.get("skin"), SkinInfo.class);
    }

    @Override
    public JobInfo getJob() {
        return (JobInfo)this.getBody();
    }

    @Override
    public Optional<SkinInfo> getSkin() {
        return Optional.ofNullable(this.skin);
    }

    @Override
    public CompletableFuture<SkinInfo> getOrLoadSkin(MineSkinClient client) {
        if (this.skin != null) {
            this.skin.setBreadcrumb(this.getBreadcrumb());
            return CompletableFuture.completedFuture(this.skin);
        }
        return this.getJob().getSkin(client).thenApply(skin -> {
            this.skin = skin.getSkin();
            return this.skin;
        });
    }
}

