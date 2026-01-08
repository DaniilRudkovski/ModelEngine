/*
 * Decompiled with CFR 0.152.
 */
package com.ticxo.modelengine.api.generator;

public interface ModelGenerator {
    default public void importModels() {
        this.importModels(false);
    }

    public boolean isInitialized();

    public void importModels(boolean var1);

    public void generateAssets(boolean var1);

    public void zipResourcePack(boolean var1);

    public void updateConfig();

    public void queueTask(Phase var1, Runnable var2);

    public static enum Phase {
        PRE_IMPORT,
        POST_IMPORT,
        PRE_ASSETS,
        POST_ASSETS,
        PRE_ZIPPING,
        POST_ZIPPING,
        FINISHED;

    }
}

