/*
 * Decompiled with CFR 0.152.
 */
package com.ticxo.modelengine.api.generator.skin;

import java.util.concurrent.CompletableFuture;

public interface SkinGeneratorService {
    public boolean isEnabled();

    public void onReload();

    public CompletableFuture<String> generate(byte[] var1);
}

