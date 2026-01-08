/*
 * Decompiled with CFR 0.152.
 */
package org.mineskin;

import java.util.concurrent.CompletableFuture;
import org.mineskin.request.GenerateRequest;
import org.mineskin.response.GenerateResponse;

public interface GenerateClient {
    public CompletableFuture<GenerateResponse> submitAndWait(GenerateRequest var1);
}

