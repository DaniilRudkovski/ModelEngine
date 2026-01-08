/*
 * Decompiled with CFR 0.152.
 */
package org.mineskin.request;

import org.mineskin.request.GenerateRequest;
import org.mineskin.request.source.UploadSource;

public interface UploadRequestBuilder
extends GenerateRequest {
    public UploadSource getUploadSource();
}

