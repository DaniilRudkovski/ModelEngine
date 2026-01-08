/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  lombok.Generated
 */
package com.ticxo.modelengine.core.generator;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.generator.BaseItemEnum;
import com.ticxo.modelengine.api.generator.assets.ItemModel;
import com.ticxo.modelengine.api.generator.blueprint.BlueprintBone;
import com.ticxo.modelengine.api.utils.TFile;
import com.ticxo.modelengine.api.utils.config.ConfigProperty;
import com.ticxo.modelengine.api.utils.data.GSONUtils;
import com.ticxo.modelengine.api.utils.logger.LogColor;
import com.ticxo.modelengine.api.utils.logger.TLogger;
import com.ticxo.modelengine.core.generator.ModelCacheGroup;
import com.ticxo.modelengine.core.generator.ModelGeneratorImpl;
import com.ticxo.modelengine.core.generator.ModelIdCache;
import com.ticxo.modelengine.core.generator.java.BaseItem;
import com.ticxo.modelengine.core.generator.java.BaseItemTransformer;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import lombok.Generated;

public class BaseItemManager {
    private final ModelEngineAPI plugin;
    private final Gson gson;
    private final Map<BaseItemEnum, BaseItemCache> baseItems = new HashMap<BaseItemEnum, BaseItemCache>();
    private final Map<String, BlueprintBone> requested = new LinkedHashMap<String, BlueprintBone>();
    private final File baseItemFolder;
    private final File itemModelFolder;
    private final File cachedIDJson;

    public BaseItemManager(ModelGeneratorImpl generator) {
        this.plugin = generator.getPlugin();
        this.gson = generator.getGson();
        this.baseItemFolder = TFile.createDirectory(generator.getAssetsFolder(), "minecraft", "models", "item");
        this.itemModelFolder = TFile.createDirectory(generator.getAssetsFolder(), "minecraft", "items");
        this.cachedIDJson = TFile.createFileOrEmpty(this.plugin.getDataFolder(), ".data", "cache.json");
    }

    public void updateModels() {
        Set<BaseItemEnum> items = ConfigProperty.ITEM_MODELS.getBaseItems();
        if (items.isEmpty()) {
            items.add(ConfigProperty.ITEM_MODEL.getBaseItem());
        }
        HashMap<BaseItemEnum, BaseItemCache> prevItems = new HashMap<BaseItemEnum, BaseItemCache>(this.baseItems);
        this.baseItems.clear();
        for (BaseItemEnum base : items) {
            String name = base.name().toLowerCase(Locale.ENGLISH);
            InputStream inputStream = this.plugin.getResource("pack/colorable/" + name + ".json");
            if (inputStream == null) {
                TLogger.warn("Unknown colorable item: " + name + ".");
                continue;
            }
            InputStreamReader itemTemplateReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            BaseItem baseItem = (BaseItem)this.gson.fromJson((Reader)itemTemplateReader, BaseItem.class);
            baseItem.setName(name);
            baseItem.setBaseItemEnum(base);
            this.baseItems.computeIfAbsent(base, baseItemEnum -> {
                BaseItemCache baseItemCache = (BaseItemCache)prevItems.get(baseItemEnum);
                if (baseItemCache == null) {
                    return new BaseItemCache((BaseItemEnum)((Object)baseItemEnum), baseItem, new ModelIdCache());
                }
                return baseItemCache.setBaseItem(baseItem);
            });
        }
    }

    public void requestId(BlueprintBone bone, Set<String> ids) {
        for (String id : ids) {
            this.requested.put(id, bone);
        }
    }

    public void endSession() {
        Object cache;
        ModelCacheGroup group = new ModelCacheGroup();
        for (Map.Entry<BaseItemEnum, BaseItemCache> entry : this.baseItems.entrySet()) {
            cache = entry.getValue().getCache();
            ((ModelIdCache)cache).gatherExistingIds(entry.getKey(), this.requested);
            group.cache.put(entry.getKey().name(), (ModelIdCache)cache);
        }
        for (Map.Entry<Object, Object> entry : this.requested.entrySet()) {
            cache = this.pollOptimalCache();
            ((BaseItemCache)cache).getCache().generateNewIds(((BaseItemCache)cache).getBaseItemEnum(), (String)entry.getKey(), (BlueprintBone)entry.getValue());
        }
        for (Map.Entry<Object, Object> entry : this.baseItems.entrySet()) {
            cache = ((BaseItemCache)entry.getValue()).getCache();
            ((ModelIdCache)cache).endSession();
        }
        try (FileWriter writer = new FileWriter(this.cachedIDJson);){
            writer.write(this.gson.toJson((Object)group));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void clearOverrides() {
        this.baseItems.values().forEach(BaseItemCache::clearOverrides);
    }

    public void registerModels(String namespace) {
        for (Map.Entry<BaseItemEnum, BaseItemCache> entry : this.baseItems.entrySet()) {
            BaseItem baseItem = entry.getValue().getBaseItem();
            baseItem.addModels(namespace, entry.getValue().getCache());
        }
    }

    public void createModelFiles() {
        this.baseItems.values().forEach(baseItemCache -> {
            BaseItem baseItem = baseItemCache.getBaseItem();
            File baseItemFile = TFile.createFile(this.baseItemFolder, baseItem.getName() + ".json");
            try (FileWriter writer = new FileWriter(baseItemFile);){
                writer.write(this.gson.toJson((Object)baseItem));
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            ItemModel ver1214 = BaseItemTransformer.to1214Format(baseItem);
            File ver1214File = TFile.createFile(this.itemModelFolder, baseItem.getName() + ".json");
            try (FileWriter writer = new FileWriter(ver1214File);){
                writer.write(this.gson.toJson(BaseItemTransformer.wrapModel(this.gson, ver1214, true)));
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    /*
     * WARNING - void declaration
     */
    public void refreshCache() {
        TFile.recreateFile(this.cachedIDJson);
        try (FileReader reader = new FileReader(this.cachedIDJson);){
            Set<BaseItemEnum> items;
            BaseItemEnum item;
            ModelCacheGroup cacheGroup = null;
            JsonObject generic = (JsonObject)this.gson.fromJson((Reader)reader, JsonObject.class);
            if (generic != null) {
                void var6_13;
                Object version = GSONUtils.get((JsonElement)generic, "version", JsonElement::getAsString, "R4.0.0");
                if (!generic.has("version") && !generic.has("cachedId")) {
                    version = "B4.0.8";
                }
                TLogger.log(LogColor.BRIGHT_GREEN + "Loading cache version: " + (String)version);
                Object object = version;
                int n = -1;
                switch (((String)object).hashCode()) {
                    case -1897935834: {
                        if (!((String)object).equals("R4.0.0")) break;
                        boolean bl = false;
                        break;
                    }
                    case 1938965054: {
                        if (!((String)object).equals("B4.0.8")) break;
                        boolean bl = true;
                        break;
                    }
                    case -1897935826: {
                        if (!((String)object).equals("R4.0.8")) break;
                        int n2 = 2;
                    }
                }
                switch (var6_13) {
                    case 0: {
                        item = ConfigProperty.ITEM_MODEL.getBaseItem();
                        ModelIdCache cache = (ModelIdCache)this.gson.fromJson((JsonElement)generic, ModelIdCache.class);
                        cacheGroup = new ModelCacheGroup();
                        cacheGroup.cache.put(item.name(), cache);
                        break;
                    }
                    case 1: {
                        JsonObject root = new JsonObject();
                        root.addProperty("version", "R4.0.8");
                        root.add("cache", (JsonElement)generic);
                        cacheGroup = (ModelCacheGroup)this.gson.fromJson((JsonElement)root, ModelCacheGroup.class);
                        break;
                    }
                    case 2: {
                        cacheGroup = (ModelCacheGroup)this.gson.fromJson((JsonElement)generic, ModelCacheGroup.class);
                    }
                }
            }
            if (cacheGroup == null) {
                TLogger.warn("Unable to read the model ID cache file. If the file is missing, ignore this warning.");
                cacheGroup = new ModelCacheGroup();
                for (BaseItemEnum baseItem : ConfigProperty.ITEM_MODELS.getBaseItems()) {
                    cacheGroup.cache.put(baseItem.name(), new ModelIdCache());
                }
            }
            if ((items = ConfigProperty.ITEM_MODELS.getBaseItems()).isEmpty()) {
                items.add(ConfigProperty.ITEM_MODEL.getBaseItem());
            }
            for (Map.Entry entry : cacheGroup.cache.entrySet()) {
                item = BaseItemEnum.get((String)entry.getKey());
                if (item == null || !items.contains((Object)item)) continue;
                this.baseItems.compute(item, (baseItemEnum, baseItemCache) -> {
                    if (baseItemCache == null) {
                        return new BaseItemCache((BaseItemEnum)((Object)((Object)baseItemEnum)), new BaseItem(), (ModelIdCache)entry.getValue());
                    }
                    return baseItemCache.setCache((ModelIdCache)entry.getValue());
                });
            }
        }
        catch (IOException e) {
            TLogger.error("Unable to read the model ID cache file. Is it corrupted?");
            e.printStackTrace();
        }
    }

    public void cleanUp() {
        this.baseItems.values().forEach(baseItemCache -> baseItemCache.getCache().cleanUp());
    }

    private BaseItemCache pollOptimalCache() {
        int load = Integer.MAX_VALUE;
        BaseItemCache cache = null;
        for (BaseItemCache baseItemCache : this.baseItems.values()) {
            int l = baseItemCache.getCache().getCacheLoad();
            if (l >= load) continue;
            load = l;
            cache = baseItemCache;
        }
        if (cache == null) {
            throw new RuntimeException("No cache is available!");
        }
        return cache;
    }

    private static class BaseItemCache {
        private BaseItemEnum baseItemEnum;
        private BaseItem baseItem;
        private ModelIdCache cache;

        public BaseItem getBaseItem() {
            return this.baseItem;
        }

        public BaseItemCache setBaseItem(BaseItem baseItem) {
            this.baseItem = baseItem;
            return this;
        }

        public ModelIdCache getCache() {
            return this.cache;
        }

        public BaseItemCache setCache(ModelIdCache cache) {
            this.cache = cache;
            return this;
        }

        public void clearOverrides() {
            if (this.baseItem != null) {
                this.baseItem.clearOverrides();
            }
        }

        @Generated
        public BaseItemCache(BaseItemEnum baseItemEnum, BaseItem baseItem, ModelIdCache cache) {
            this.baseItemEnum = baseItemEnum;
            this.baseItem = baseItem;
            this.cache = cache;
        }

        @Generated
        public BaseItemEnum getBaseItemEnum() {
            return this.baseItemEnum;
        }
    }
}

