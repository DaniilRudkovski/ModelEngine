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
import org.mineskin.data.JobInfo;
import org.mineskin.data.RateLimitInfo;
import org.mineskin.data.UsageInfo;
import org.mineskin.response.AbstractMineSkinResponse;
import org.mineskin.response.QueueResponse;

public class QueueResponseImpl
extends AbstractMineSkinResponse<JobInfo>
implements QueueResponse {
    private final RateLimitInfo rateLimit;
    private final UsageInfo usage;

    public QueueResponseImpl(int status, Map<String, String> headers, JsonObject rawBody, Gson gson, Class<JobInfo> clazz) {
        super(status, headers, rawBody, gson, "job", clazz);
        this.rateLimit = (RateLimitInfo)gson.fromJson(rawBody.get("rateLimit"), RateLimitInfo.class);
        this.usage = (UsageInfo)gson.fromJson(rawBody.get("usage"), UsageInfo.class);
    }

    @Override
    public JobInfo getJob() {
        return (JobInfo)this.getBody();
    }

    @Override
    public RateLimitInfo getRateLimit() {
        return this.rateLimit;
    }

    @Override
    public UsageInfo getUsage() {
        return this.usage;
    }
}

