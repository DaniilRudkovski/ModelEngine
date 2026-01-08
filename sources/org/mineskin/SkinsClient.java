/*
 * Decompiled with CFR 0.152.
 */
package org.mineskin;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.mineskin.response.SkinResponse;

public interface SkinsClient {
    public CompletableFuture<SkinResponse> get(UUID var1);

    public CompletableFuture<SkinResponse> get(String var1);
}

