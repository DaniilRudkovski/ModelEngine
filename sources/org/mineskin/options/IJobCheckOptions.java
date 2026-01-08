/*
 * Decompiled with CFR 0.152.
 */
package org.mineskin.options;

import java.util.concurrent.ScheduledExecutorService;
import org.mineskin.request.backoff.RequestInterval;

public interface IJobCheckOptions {
    public ScheduledExecutorService scheduler();

    public RequestInterval interval();

    @Deprecated
    public int intervalMillis();

    public int initialDelayMillis();

    public int maxAttempts();

    public boolean useEta();
}

