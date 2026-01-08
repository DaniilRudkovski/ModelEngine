/*
 * Decompiled with CFR 0.152.
 */
package org.mineskin.response;

import org.mineskin.data.RateLimitInfo;
import org.mineskin.data.SkinInfo;
import org.mineskin.data.UsageInfo;
import org.mineskin.response.MineSkinResponse;

public interface GenerateResponse
extends MineSkinResponse<SkinInfo> {
    public SkinInfo getSkin();

    public RateLimitInfo getRateLimit();

    public UsageInfo getUsage();
}

