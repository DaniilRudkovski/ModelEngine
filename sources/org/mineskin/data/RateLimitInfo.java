/*
 * Decompiled with CFR 0.152.
 */
package org.mineskin.data;

import org.mineskin.data.DelayInfo;
import org.mineskin.data.LimitInfo;
import org.mineskin.data.NextRequest;

public record RateLimitInfo(NextRequest next, DelayInfo delay, LimitInfo limit) {
}

