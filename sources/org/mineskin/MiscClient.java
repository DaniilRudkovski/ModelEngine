/*
 * Decompiled with CFR 0.152.
 */
package org.mineskin;

import java.util.concurrent.CompletableFuture;
import org.mineskin.response.UserResponse;

public interface MiscClient {
    public CompletableFuture<UserResponse> getUser();
}

