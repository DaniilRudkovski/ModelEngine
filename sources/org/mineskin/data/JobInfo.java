/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package org.mineskin.data;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import org.mineskin.MineSkinClient;
import org.mineskin.data.JobReference;
import org.mineskin.data.JobStatus;
import org.mineskin.data.MutableBreadcrumbed;
import org.mineskin.exception.MineskinException;
import org.mineskin.response.SkinResponse;

public class JobInfo
implements MutableBreadcrumbed {
    private final String id;
    private final JobStatus status;
    private final long timestamp;
    private final long eta;
    private final String result;
    private String breadcrumb;

    public JobInfo(String id, JobStatus status, long timestamp, String result) {
        this(id, status, timestamp, 0L, result);
    }

    public JobInfo(String id, JobStatus status, long timestamp, long eta, String result) {
        this.id = id;
        this.status = status;
        this.timestamp = timestamp;
        this.eta = eta;
        this.result = result;
    }

    public String id() {
        return this.id;
    }

    public JobStatus status() {
        return this.status;
    }

    public long timestamp() {
        return this.timestamp;
    }

    public long eta() {
        return this.eta;
    }

    public Optional<String> result() {
        return Optional.ofNullable(this.result);
    }

    @Override
    @Nullable
    public String getBreadcrumb() {
        return this.breadcrumb;
    }

    @Override
    public void setBreadcrumb(String breadcrumb) {
        this.breadcrumb = breadcrumb;
    }

    public CompletableFuture<JobReference> waitForCompletion(MineSkinClient client) {
        return client.queue().waitForCompletion(this);
    }

    public CompletableFuture<SkinResponse> getSkin(MineSkinClient client) {
        if (this.result == null) {
            throw new MineskinException("Job is not completed yet").withBreadcrumb(this.getBreadcrumb());
        }
        return client.skins().get(this.result);
    }

    public String toString() {
        return "JobInfo{id='" + this.id + "', status=" + String.valueOf((Object)this.status) + ", timestamp=" + this.timestamp + ", eta=" + this.eta + ", result='" + this.result + "', breadcrumb='" + this.breadcrumb + "'}";
    }
}

