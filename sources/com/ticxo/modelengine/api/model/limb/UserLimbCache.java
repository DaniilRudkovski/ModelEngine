/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  lombok.Generated
 */
package com.ticxo.modelengine.api.model.limb;

import com.google.common.collect.Maps;
import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.generator.skin.SkinGeneratorService;
import com.ticxo.modelengine.api.generator.skin.SkinSplitter;
import com.ticxo.modelengine.api.utils.TFile;
import com.ticxo.modelengine.api.utils.logger.LogColor;
import com.ticxo.modelengine.api.utils.logger.TLogger;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import lombok.Generated;

public class UserLimbCache {
    private final String name;
    private final String source;
    private final Map<String, String> classic = new HashMap<String, String>();
    private final Map<String, String> slim = new HashMap<String, String>();
    private transient Map<String, CompletableFuture<String>> requested = Maps.newConcurrentMap();
    private boolean classicGenerated;
    private boolean slimGenerated;

    public void initialize() {
        if (this.requested == null) {
            this.requested = Maps.newConcurrentMap();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void generate(SkinGeneratorService service, boolean slim) {
        if (!service.isEnabled()) {
            TLogger.error(2, "Unable to generate user limb cache for " + this.name + ": Service is disabled!.");
            return;
        }
        UserLimbCache userLimbCache = this;
        synchronized (userLimbCache) {
            if (this.isGenerated(slim)) {
                return;
            }
            this.setGenerated(slim, true);
            Map<String, String> map = this.getMap(slim);
            map.clear();
            byte[] imageBytes = Base64.getDecoder().decode(this.source);
            SkinSplitter splitter = ModelEngineAPI.getAPI().getSkinSplitter();
            Map<String, byte[]> skins = splitter.splitSkin(imageBytes, slim);
            if (skins.isEmpty()) {
                return;
            }
            ArrayList<CompletionStage> futures = new ArrayList<CompletionStage>();
            for (Map.Entry<String, byte[]> entry : skins.entrySet()) {
                futures.add(((CompletableFuture)service.generate(entry.getValue()).thenAccept(s -> {
                    map.put((String)entry.getKey(), (String)s);
                    CompletableFuture<String> future = this.requested.remove(entry.getKey());
                    if (future != null) {
                        future.complete((String)s);
                    }
                })).handle((s, throwable) -> {
                    if (throwable != null) {
                        throwable.printStackTrace();
                    }
                    return null;
                }));
            }
            ((CompletableFuture)CompletableFuture.allOf((CompletableFuture[])futures.toArray(CompletableFuture[]::new)).thenRunAsync(this::saveCache)).handle((unused, throwable) -> {
                if (throwable != null) {
                    throwable.printStackTrace();
                }
                return null;
            });
        }
    }

    public boolean hasSkinUrl(String limbId, boolean slim) {
        Map<String, String> map = this.getMap(slim);
        return map.containsKey(limbId);
    }

    public CompletableFuture<String> getSkinUrl(String limbId, boolean slim) {
        if (this.hasSkinUrl(limbId, slim)) {
            return CompletableFuture.completedFuture(this.getMap(slim).get(limbId));
        }
        return this.requested.computeIfAbsent(limbId, s -> new CompletableFuture());
    }

    public void saveCache() {
        ModelEngineAPI api = ModelEngineAPI.getAPI();
        String cacheId = UUID.nameUUIDFromBytes(this.source.getBytes()).toString();
        String header = cacheId.substring(0, 2);
        String remain = cacheId.substring(2);
        File file = TFile.createFile(api.getDataFolder(), ".data", "userlimbs", header, remain + ".json");
        try (FileWriter writer = new FileWriter(file);){
            String data = api.getGson().toJson((Object)this);
            writer.write(data);
            TLogger.log(LogColor.BRIGHT_GREEN + "Limb data generated: " + this.name);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Map<String, String> getMap(boolean slim) {
        return slim ? this.slim : this.classic;
    }

    private void setGenerated(boolean slim, boolean flag) {
        if (slim) {
            this.slimGenerated = flag;
        } else {
            this.classicGenerated = flag;
        }
    }

    public boolean isGenerated(boolean slim) {
        return slim ? this.slimGenerated : this.classicGenerated;
    }

    @Generated
    public UserLimbCache(String name, String source) {
        this.name = name;
        this.source = source;
    }
}

