/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.destroystokyo.paper.profile.PlayerProfile
 *  com.google.gson.Gson
 *  org.bukkit.configuration.ConfigurationSection
 *  org.bukkit.entity.Player
 *  org.bukkit.profile.PlayerTextures$SkinModel
 */
package com.ticxo.modelengine.api.model.limb;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.google.gson.Gson;
import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.model.limb.UserLimbCache;
import com.ticxo.modelengine.api.utils.MiscUtils;
import com.ticxo.modelengine.api.utils.MojangAPI;
import com.ticxo.modelengine.api.utils.TFile;
import com.ticxo.modelengine.api.utils.config.ConfigProperty;
import com.ticxo.modelengine.api.utils.logger.LogColor;
import com.ticxo.modelengine.api.utils.logger.TLogger;
import com.ticxo.modelengine.api.utils.registry.TUnaryRegistry;
import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import javax.imageio.ImageIO;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.profile.PlayerTextures;

public class UserLimbRegistry
extends TUnaryRegistry<UserLimbCache> {
    private final Map<String, String> keyedSources = new HashMap<String, String>();
    private final File skinDir;

    public UserLimbRegistry(ModelEngineAPI plugin) {
        this.skinDir = TFile.createDirectory(plugin.getDataFolder(), "skins");
    }

    public void generateDefaults() {
        ConfigProperty skins = ConfigProperty.DEFAULT_SKINS;
        ConfigurationSection section = skins.getSection();
        for (String key : section.getKeys(false)) {
            ConfigurationSection data = section.getConfigurationSection(key);
            if (data == null) continue;
            TLogger.log(LogColor.CYAN + "Loading default skin: " + key);
            CompletableFuture<String> base64 = this.getImageData(data);
            if (base64 == null) {
                TLogger.warn("Failed to eager load " + key + ": Missing data value.");
                continue;
            }
            boolean slim = data.getBoolean("slim", false);
            base64.handle((s, throwable) -> {
                if (throwable != null) {
                    throwable.printStackTrace();
                } else {
                    this.keyedSources.put(key, (String)s);
                    this.generate(key, (String)s, slim);
                    TLogger.log(LogColor.BRIGHT_GREEN + "Registered default skin: " + key);
                }
                return null;
            });
        }
    }

    private CompletableFuture<String> getImageData(ConfigurationSection section) {
        Iterator iterator = section.getKeys(false).iterator();
        block14: while (iterator.hasNext()) {
            String key;
            switch (key = (String)iterator.next()) {
                case "base64": {
                    return CompletableFuture.supplyAsync(() -> section.getString(key));
                }
                case "url": {
                    HttpRequest request = HttpRequest.newBuilder().uri(URI.create(section.getString(key))).timeout(Duration.of(30L, ChronoUnit.SECONDS)).GET().build();
                    HttpClient client = HttpClient.newHttpClient();
                    return ((CompletableFuture)client.sendAsync(request, HttpResponse.BodyHandlers.ofByteArray()).thenApply(HttpResponse::body)).thenApply(bytes -> Base64.getEncoder().encodeToString((byte[])bytes));
                }
                case "path": {
                    File imgFile = TFile.createFile(this.skinDir, section.getString(key));
                    if (!imgFile.exists()) continue block14;
                    return ((CompletableFuture)CompletableFuture.supplyAsync(() -> {
                        try {
                            return ImageIO.read(imgFile);
                        }
                        catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }).thenApply(image -> {
                        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                        try {
                            ImageIO.write((RenderedImage)image, "png", buffer);
                        }
                        catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        return buffer;
                    })).thenApply(stream -> Base64.getEncoder().encodeToString(stream.toByteArray()));
                }
                case "username": {
                    String name = section.getString(key);
                    return ((CompletableFuture)CompletableFuture.supplyAsync(() -> MojangAPI.getUUIDFromUsername(name)).thenApply(MojangAPI::fromUUID)).thenCompose(MojangAPI::getRawSkinData);
                }
                case "uuid": {
                    UUID uuid = UUID.fromString(section.getString(key));
                    return CompletableFuture.supplyAsync(() -> MojangAPI.fromUUID(uuid)).thenCompose(MojangAPI::getRawSkinData);
                }
            }
        }
        return null;
    }

    public boolean tryLoad(String cacheId) {
        if (this.registry.containsKey(cacheId)) {
            return true;
        }
        ModelEngineAPI api = ModelEngineAPI.getAPI();
        Gson gson = api.getGson();
        String header = cacheId.substring(0, 2);
        String remain = cacheId.substring(2);
        File file = TFile.createFile(api.getDataFolder(), ".data", "userlimbs", header, remain + ".json");
        if (!file.exists()) {
            return false;
        }
        try {
            UserLimbCache cache = (UserLimbCache)gson.fromJson((Reader)new FileReader(file), UserLimbCache.class);
            cache.initialize();
            this.register(cacheId, cache);
            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void generate(Player player) {
        PlayerProfile playerProfile = player.getPlayerProfile();
        boolean slim = playerProfile.getTextures().getSkinModel() == PlayerTextures.SkinModel.SLIM;
        ((CompletableFuture)MojangAPI.getRawSkinData(playerProfile).thenAccept(base64 -> this.generate((String)base64, slim))).handle(MiscUtils::handle);
    }

    public void generate(byte[] data, boolean slim) {
        this.generate(Base64.getEncoder().encodeToString(data), slim);
    }

    public UserLimbCache generate(String base64, boolean slim) {
        String cacheId = UUID.nameUUIDFromBytes(base64.getBytes()).toString();
        if (this.tryLoad(cacheId)) {
            return (UserLimbCache)this.get(cacheId);
        }
        UserLimbCache cache = new UserLimbCache(cacheId, base64);
        this.register(cacheId, cache);
        cache.generate(ModelEngineAPI.getSkinGeneratorService(), slim);
        return cache;
    }

    public UserLimbCache generate(String name, String base64, boolean slim) {
        String cacheId = UUID.nameUUIDFromBytes(base64.getBytes()).toString();
        if (this.tryLoad(cacheId)) {
            return (UserLimbCache)this.get(cacheId);
        }
        UserLimbCache cache = new UserLimbCache(name, base64);
        this.register(cacheId, cache);
        cache.generate(ModelEngineAPI.getSkinGeneratorService(), slim);
        return cache;
    }

    public CompletableFuture<String> getSkinUrlFromKey(String key, String limbType, boolean slim) {
        String src = this.keyedSources.get(key);
        return src == null ? CompletableFuture.failedFuture(new RuntimeException("Unknown limb source with key: " + key)) : this.getSkinUrlFromSource(src, limbType, slim);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public CompletableFuture<String> getSkinUrlFromSource(String source, String limbType, boolean slim) {
        Map map = this.registry;
        synchronized (map) {
            UserLimbCache cache;
            String cacheId = UUID.nameUUIDFromBytes(source.getBytes()).toString();
            if (!this.tryLoad(cacheId) && !this.registry.containsKey(cacheId)) {
                this.register(cacheId, new UserLimbCache(cacheId, source));
            }
            if (!(cache = (UserLimbCache)this.get(cacheId)).isGenerated(slim)) {
                cache.generate(ModelEngineAPI.getSkinGeneratorService(), slim);
            }
            return cache.getSkinUrl(limbType, slim);
        }
    }
}

