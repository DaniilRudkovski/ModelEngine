/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.destroystokyo.paper.profile.PlayerProfile
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  org.bukkit.Bukkit
 *  org.bukkit.profile.PlayerTextures
 *  org.bukkit.profile.PlayerTextures$SkinModel
 *  org.jetbrains.annotations.NotNull
 */
package com.ticxo.modelengine.api.utils;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.utils.data.GSONUtils;
import com.ticxo.modelengine.api.utils.math.TMath;
import com.ticxo.modelengine.api.utils.promise.Promise;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import org.bukkit.Bukkit;
import org.bukkit.profile.PlayerTextures;
import org.jetbrains.annotations.NotNull;

public class MojangAPI {
    private static final HttpClient CLIENT = HttpClient.newHttpClient();
    private static final Map<String, UUID> UUID_CACHE = new ConcurrentHashMap<String, UUID>();
    private static final Map<UUID, String> DATA_CACHE = new ConcurrentHashMap<UUID, String>();
    private static final Map<String, String> SKIN_CACHE = new ConcurrentHashMap<String, String>();

    private MojangAPI() {
        throw new IllegalStateException();
    }

    public static CompletableFuture<String> getRawSkinData(PlayerProfile profile) {
        try {
            URL skin = profile.getTextures().getSkin();
            if (skin == null) {
                return CompletableFuture.failedFuture(new RuntimeException("Skin URL is null"));
            }
            URI uri = skin.toURI();
            String data = SKIN_CACHE.get(uri.toString());
            if (data != null) {
                return CompletableFuture.completedFuture(data);
            }
            HttpRequest request = HttpRequest.newBuilder().uri(uri).timeout(Duration.of(30L, ChronoUnit.SECONDS)).GET().build();
            HttpClient client = HttpClient.newHttpClient();
            return ((CompletableFuture)client.sendAsync(request, HttpResponse.BodyHandlers.ofByteArray()).thenApply(HttpResponse::body)).thenApply(bytes -> {
                String base64 = Base64.getEncoder().encodeToString((byte[])bytes);
                SKIN_CACHE.put(uri.toString(), base64);
                return base64;
            });
        }
        catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static PlayerProfile fromBase64(@NotNull String data) {
        PlayerProfile profile = Bukkit.createProfile((UUID)UUID.randomUUID());
        String decoded = new String(Base64.getDecoder().decode(data));
        JsonObject root = (JsonObject)ModelEngineAPI.getAPI().getGson().fromJson(decoded, JsonObject.class);
        GSONUtils.ifPresent((JsonElement)root, "textures", element -> GSONUtils.ifPresent(element, "SKIN", element1 -> {
            String url = GSONUtils.get(element1, "url", JsonElement::getAsString);
            if (url == null) {
                throw new NullPointerException("Skin URL cannot be null.");
            }
            JsonObject metadata = GSONUtils.get(element1, "metadata", JsonElement::getAsJsonObject);
            boolean slim = GSONUtils.get((JsonElement)metadata, "model", element2 -> element2.getAsString().equals("slim"), false);
            try {
                PlayerTextures texture = profile.getTextures();
                texture.setSkin(new URL(url), slim ? PlayerTextures.SkinModel.SLIM : PlayerTextures.SkinModel.CLASSIC);
                profile.setTextures(texture);
            }
            catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        }));
        return profile;
    }

    public static PlayerProfile fromUUID(UUID uuid) {
        String data = DATA_CACHE.computeIfAbsent(uuid, uuid1 -> {
            try {
                HttpRequest request = HttpRequest.newBuilder().uri(new URI("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid)).timeout(Duration.of(30L, ChronoUnit.SECONDS)).GET().build();
                HttpClient client = HttpClient.newHttpClient();
                String result = (String)((CompletableFuture)client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenApply(HttpResponse::body)).get(30L, TimeUnit.SECONDS);
                JsonObject root = (JsonObject)ModelEngineAPI.getAPI().getGson().fromJson(result, JsonObject.class);
                return GSONUtils.get((JsonElement)root, "properties", element -> {
                    JsonArray array = element.getAsJsonArray();
                    return array.isEmpty() ? null : GSONUtils.get(array.get(0), "value", JsonElement::getAsString);
                });
            }
            catch (Throwable e) {
                throw new RuntimeException(e);
            }
        });
        return data == null ? null : MojangAPI.fromBase64(data);
    }

    public static UUID getUUIDFromUsername(String username) {
        return UUID_CACHE.computeIfAbsent(username, s -> {
            try {
                long time = System.currentTimeMillis() / 1000L;
                HttpRequest request = HttpRequest.newBuilder().uri(new URI("https://api.mojang.com/users/profiles/minecraft/" + username + "?at=" + time)).timeout(Duration.of(30L, ChronoUnit.SECONDS)).GET().build();
                String result = (String)((CompletableFuture)CLIENT.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenApply(HttpResponse::body)).get(30L, TimeUnit.SECONDS);
                JsonObject root = (JsonObject)ModelEngineAPI.getAPI().getGson().fromJson(result, JsonObject.class);
                return GSONUtils.get((JsonElement)root, "id", element -> TMath.parseUUID(element.getAsString()));
            }
            catch (Throwable e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static Promise<PlayerProfile> fromUUIDPromise(UUID uuid) {
        return Promise.supplyingAsync(() -> MojangAPI.fromUUID(uuid));
    }

    public static Promise<UUID> getUUIDFromUsernamePromise(String name) {
        return Promise.supplyingAsync(() -> MojangAPI.getUUIDFromUsername(name));
    }
}

