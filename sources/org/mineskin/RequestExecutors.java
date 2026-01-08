/*
 * Decompiled with CFR 0.152.
 */
package org.mineskin;

import java.util.concurrent.Executor;
import org.mineskin.options.IJobCheckOptions;
import org.mineskin.options.IQueueOptions;

public record RequestExecutors(Executor getExecutor, Executor generateExecutor, IQueueOptions generateQueueOptions, IQueueOptions getQueueOptions, IJobCheckOptions jobCheckOptions) {
}

