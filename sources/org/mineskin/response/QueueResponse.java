/*
 * Decompiled with CFR 0.152.
 */
package org.mineskin.response;

import org.mineskin.data.JobInfo;
import org.mineskin.data.RateLimitInfo;
import org.mineskin.data.UsageInfo;
import org.mineskin.response.MineSkinResponse;

public interface QueueResponse
extends MineSkinResponse<JobInfo> {
    public JobInfo getJob();

    public RateLimitInfo getRateLimit();

    public UsageInfo getUsage();
}

