/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 */
package com.ticxo.modelengine.core.generator.skin;

import com.ticxo.modelengine.api.generator.skin.SkinGeneratorService;
import com.ticxo.modelengine.api.utils.config.ConfigProperty;
import com.ticxo.modelengine.api.utils.logger.TLogger;
import java.io.ByteArrayInputStream;
import java.util.concurrent.CompletableFuture;
import lombok.Generated;
import org.mineskin.JsoupRequestHandler;
import org.mineskin.MineSkinClient;
import org.mineskin.data.Visibility;
import org.mineskin.options.GenerateQueueOptions;
import org.mineskin.request.GenerateRequest;

public class MineSkinGeneratorService
implements SkinGeneratorService {
    private boolean enabled;
    private MineSkinClient client;

    @Override
    public void onReload() {
        String key = ConfigProperty.MINESKIN_API_KEY.getString();
        if (key == null || key.isBlank()) {
            TLogger.warn("Unable to activate MineSkin skin generation: Empty API Key");
            this.enabled = false;
            return;
        }
        this.client = MineSkinClient.builder().requestHandler(JsoupRequestHandler::new).userAgent("ModelEngine/R4.0.9").apiKey(key).generateQueueOptions(GenerateQueueOptions.createAuto()).build();
        this.enabled = true;
    }

    @Override
    public CompletableFuture<String> generate(byte[] rawImage) {
        GenerateRequest request = GenerateRequest.upload(new ByteArrayInputStream(rawImage)).visibility(Visibility.UNLISTED);
        return ((CompletableFuture)((CompletableFuture)this.client.queue().submit(request).thenCompose(queueResponse -> queueResponse.getJob().waitForCompletion(this.client))).thenCompose(jobResponse -> jobResponse.getOrLoadSkin(this.client))).thenApply(skinInfo -> skinInfo.texture().url().skin());
    }

    @Override
    @Generated
    public boolean isEnabled() {
        return this.enabled;
    }
}

