/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Ordering
 *  com.google.gson.Gson
 *  it.unimi.dsi.fastutil.Pair
 *  lombok.Generated
 *  org.bukkit.Bukkit
 *  org.bukkit.NamespacedKey
 */
package com.ticxo.modelengine.core.generator;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import com.google.gson.Gson;
import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.error.ErrorCollector;
import com.ticxo.modelengine.api.error.IError;
import com.ticxo.modelengine.api.events.ModelRegistrationEvent;
import com.ticxo.modelengine.api.events.RegisterParserEvent;
import com.ticxo.modelengine.api.generator.ModelGenerator;
import com.ticxo.modelengine.api.generator.assets.BlueprintTexture;
import com.ticxo.modelengine.api.generator.assets.ItemModel;
import com.ticxo.modelengine.api.generator.assets.ItemModelData;
import com.ticxo.modelengine.api.generator.assets.JavaItemModel;
import com.ticxo.modelengine.api.generator.assets.ModelAssets;
import com.ticxo.modelengine.api.generator.blueprint.BlueprintBone;
import com.ticxo.modelengine.api.generator.blueprint.ModelBlueprint;
import com.ticxo.modelengine.api.generator.parser.ModelParser;
import com.ticxo.modelengine.api.model.ModelRegistry;
import com.ticxo.modelengine.api.utils.MiscUtils;
import com.ticxo.modelengine.api.utils.TFile;
import com.ticxo.modelengine.api.utils.config.ConfigProperty;
import com.ticxo.modelengine.api.utils.logger.LogColor;
import com.ticxo.modelengine.api.utils.logger.TLogger;
import com.ticxo.modelengine.api.utils.math.TMath;
import com.ticxo.modelengine.api.utils.ticker.DualTicker;
import com.ticxo.modelengine.core.generator.BaseItemManager;
import com.ticxo.modelengine.core.generator.atlas.AtlasManager;
import com.ticxo.modelengine.core.generator.java.BaseItemTransformer;
import com.ticxo.modelengine.core.generator.parser.blockbench.BlockbenchParser;
import com.ticxo.modelengine.core.java21.Java21Access;
import it.unimi.dsi.fastutil.Pair;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.invoke.LambdaMetafactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.BiFunction;
import java.util.function.IntFunction;
import java.util.zip.ZipOutputStream;
import javax.imageio.ImageIO;
import lombok.Generated;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;

public class ModelGeneratorImpl
implements ModelGenerator {
    private final ModelEngineAPI plugin;
    private final Gson gson;
    private final List<ModelParser> parsers = new ArrayList<ModelParser>();
    private final List<ModelAssets> assets = new ArrayList<ModelAssets>();
    private final AtlasManager atlasManager;
    private final BaseItemManager baseItemManager;
    private final ExecutorService generatorService = Executors.newFixedThreadPool(ConfigProperty.MAX_GENERATOR_THREADS.getIntOrIfNeg(() -> Runtime.getRuntime().availableProcessors()));
    private final File blueprintFolder;
    private final File internalFolder;
    private final File internalExampleFolder;
    private final File internalBlueprintFolder;
    private final File packFolder;
    private final File assetsFolder;
    private final File zippedResourcePack;
    private final Map<ModelGenerator.Phase, Set<Runnable>> tasks = Maps.newConcurrentMap();
    private final Set<ModelGenerator.Phase> executed = new HashSet<ModelGenerator.Phase>();
    private String namespace;
    private File modelFolder;
    private File itemsFolder;
    private boolean generateMeta;
    private boolean deleteOld;
    private boolean initialized;
    private final long[] timer = new long[3];
    private final Map<String, String> textureNameCache = new HashMap<String, String>();

    public ModelGeneratorImpl(ModelEngineAPI plugin) {
        this.plugin = plugin;
        this.gson = plugin.getGson();
        this.blueprintFolder = TFile.createDirectory(plugin.getDataFolder(), "blueprints");
        this.internalFolder = TFile.createDirectory(plugin.getDataFolder(), "internals");
        this.internalExampleFolder = TFile.createDirectory(this.internalFolder, "examples");
        this.internalBlueprintFolder = TFile.createDirectory(this.internalFolder, "blueprints");
        this.packFolder = TFile.createDirectory(plugin.getDataFolder(), "resource pack");
        this.assetsFolder = TFile.createDirectory(this.packFolder, "assets");
        this.zippedResourcePack = TFile.createFile(plugin.getDataFolder(), "resource pack.zip");
        this.atlasManager = new AtlasManager(this);
        this.baseItemManager = new BaseItemManager(this);
        this.baseItemManager.refreshCache();
        plugin.getConfigManager().registerReferenceUpdate(this::updateConfig);
        this.parsers.add(new BlockbenchParser(this));
        ModelEngineAPI.callEvent(new RegisterParserEvent(this.parsers));
    }

    @Override
    public void importModels(boolean isStartup) {
        this.executed.clear();
        this.queueTask(ModelGenerator.Phase.PRE_IMPORT, () -> {
            this.timer[0] = System.nanoTime();
        });
        this.queueTask(ModelGenerator.Phase.POST_IMPORT, () -> {
            this.timer[0] = System.nanoTime() - this.timer[0];
        });
        this.queueTask(ModelGenerator.Phase.PRE_ASSETS, () -> {
            this.timer[1] = System.nanoTime();
        });
        this.queueTask(ModelGenerator.Phase.POST_ASSETS, () -> {
            this.timer[1] = System.nanoTime() - this.timer[1];
        });
        this.queueTask(ModelGenerator.Phase.PRE_ZIPPING, () -> {
            this.timer[2] = System.nanoTime();
        });
        this.queueTask(ModelGenerator.Phase.POST_ZIPPING, () -> {
            this.timer[2] = System.nanoTime() - this.timer[2];
        });
        this.queueTask(ModelGenerator.Phase.FINISHED, () -> {
            TLogger.log(LogColor.BRIGHT_GREEN + "Generator Profiled:");
            TLogger.log(LogColor.BRIGHT_GREEN + " - Import Phase: " + MiscUtils.FORMATTER.format((double)this.timer[0] / 1000000.0) + "ms");
            TLogger.log(LogColor.BRIGHT_GREEN + " - Assets Phase: " + MiscUtils.FORMATTER.format((double)this.timer[1] / 1000000.0) + "ms");
            TLogger.log(LogColor.BRIGHT_GREEN + " - Zipping Phase: " + MiscUtils.FORMATTER.format((double)this.timer[2] / 1000000.0) + "ms");
        });
        if (!isStartup || ConfigProperty.LATE_REGISTER.getBoolean()) {
            DualTicker.queueAsyncTask(() -> CompletableFuture.runAsync(() -> this.importModelsInternal(isStartup)));
        } else {
            this.importModelsInternal(true);
        }
    }

    @Override
    public void generateAssets(boolean isStartup) {
        if (!ConfigProperty.LATE_REGISTER.getBoolean() && ConfigProperty.LATE_ASSETS.getBoolean()) {
            DualTicker.queueAsyncTask(() -> CompletableFuture.runAsync(() -> this.generateAssetsInternal(isStartup)));
        } else {
            this.generateAssetsInternal(true);
        }
    }

    @Override
    public void zipResourcePack(boolean isStartup) {
        if (!ConfigProperty.ZIP.getBoolean()) {
            this.executeQueuedTask(ModelGenerator.Phase.FINISHED);
            return;
        }
        if (!ConfigProperty.LATE_ASSETS.getBoolean() && ConfigProperty.LATE_ZIPPING.getBoolean()) {
            DualTicker.queueAsyncTask(() -> CompletableFuture.runAsync(this::zipResourcePackInternal));
        } else {
            this.zipResourcePackInternal();
        }
    }

    @Override
    public void updateConfig() {
        this.namespace = ConfigProperty.NAMESPACE.getString().toLowerCase(Locale.ENGLISH);
        this.modelFolder = TFile.createDirectory(this.assetsFolder, this.namespace, "models");
        this.itemsFolder = TFile.createDirectory(this.assetsFolder, this.namespace, "items");
        this.baseItemManager.updateModels();
        this.generateMeta = ConfigProperty.MCMETA.getBoolean();
        this.deleteOld = ConfigProperty.DELETE_OLD.getBoolean();
    }

    @Override
    public void queueTask(ModelGenerator.Phase post, Runnable task) {
        if (this.executed.contains((Object)ModelGenerator.Phase.FINISHED) || this.executed.contains((Object)post)) {
            task.run();
        } else {
            this.tasks.computeIfAbsent(post, phase -> new LinkedHashSet()).add(task);
        }
    }

    private void executeQueuedTask(ModelGenerator.Phase phase) {
        this.executed.add(phase);
        if (phase == ModelGenerator.Phase.FINISHED) {
            this.tasks.values().forEach(runnables -> runnables.forEach(Runnable::run));
            this.tasks.clear();
        } else {
            this.tasks.computeIfPresent(phase, (p, runnables) -> {
                runnables.forEach(Runnable::run);
                runnables.clear();
                return runnables;
            });
        }
        ModelEngineAPI.callEvent(new ModelRegistrationEvent(phase));
    }

    /*
     * Unable to fully structure code
     * Could not resolve type clashes
     */
    private void importModelsInternal(boolean isStartup) {
        TLogger.log();
        TLogger.log(LogColor.BOLD + LogColor.CYAN.toString() + "[Importing models]");
        this.initialized = false;
        this.executeQueuedTask(ModelGenerator.Phase.PRE_IMPORT);
        this.baseItemManager.refreshCache();
        this.textureNameCache.clear();
        registry = this.plugin.getModelRegistry();
        registry.clearRegistry();
        this.copyInternalFiles();
        files /* !! */  = Lists.newArrayList();
        fa = this.blueprintFolder.listFiles();
        if (fa != null) {
            files /* !! */ .addAll(Arrays.asList(fa));
        }
        if ((fa = this.internalBlueprintFolder.listFiles()) != null) {
            files /* !! */ .addAll(Arrays.asList(fa));
        }
        if (Bukkit.getPluginManager().getPlugin("MythicMobs") != null) {
            try {
                files /* !! */ .addAll((Collection)Java21Access.getMythicPackModelFiles.call(new Object[0]));
            }
            catch (Throwable ex) {
                ex.printStackTrace();
            }
        }
        if (files /* !! */ .isEmpty()) {
            this.initialized = true;
            this.executeQueuedTask(ModelGenerator.Phase.FINISHED);
            return;
        }
        compartment = new LinkedList<File>();
        files /* !! */ .sort(Ordering.natural());
        importTasks = new ArrayList<CompletionStage>();
        block2: while (true) lbl-1000:
        // 5 sources

        {
            for (File file : files /* !! */ ) {
                if (!file.isFile()) {
                    if (!file.isDirectory()) continue;
                    compartment.add(file);
                    continue;
                }
                if (importTasks.size() >= 12) break block2;
                importTasks.add(CompletableFuture.runAsync((Runnable)LambdaMetafactory.metafactory(null, null, null, ()V, lambda$importModelsInternal$16(java.io.File com.ticxo.modelengine.api.model.ModelRegistry ), ()V)((ModelGeneratorImpl)this, (File)file, (ModelRegistry)registry), this.generatorService).handle((BiFunction<Void, Throwable, Void>)LambdaMetafactory.metafactory(null, null, null, (Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;, lambda$importModelsInternal$17(java.lang.Void java.lang.Throwable ), (Ljava/lang/Void;Ljava/lang/Throwable;)Ljava/lang/Void;)()));
            }
            while (!compartment.isEmpty()) {
                ca = ((File)compartment.poll()).listFiles();
                if (ca == null || ca.length == 0) continue;
                files /* !! */  = Arrays.asList(ca);
                continue block2;
                ** continue;
            }
            break;
        }
        CompletableFuture.allOf((CompletableFuture[])importTasks.toArray((IntFunction<CompletableFuture[]>)LambdaMetafactory.metafactory(null, null, null, (I)Ljava/lang/Object;, lambda$importModelsInternal$18(int ), (I)[Ljava/util/concurrent/CompletableFuture;)())).thenRunAsync((Runnable)LambdaMetafactory.metafactory(null, null, null, ()V, lambda$importModelsInternal$19(boolean ), ()V)((ModelGeneratorImpl)this, (boolean)isStartup)).handle((BiFunction<Void, Throwable, Object>)LambdaMetafactory.metafactory(null, null, null, (Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;, lambda$importModelsInternal$20(java.lang.Void java.lang.Throwable ), (Ljava/lang/Void;Ljava/lang/Throwable;)Ljava/lang/Object;)());
    }

    private void generateAssetsInternal(boolean isStartup) {
        this.executeQueuedTask(ModelGenerator.Phase.PRE_ASSETS);
        this.baseItemManager.clearOverrides();
        this.atlasManager.reset();
        if (this.deleteOld && !TFile.delete(this.packFolder)) {
            TLogger.error("Failed to clear old resource pack folder.");
        }
        ArrayList<CompletionStage> assetTasks = new ArrayList<CompletionStage>();
        for (ModelAssets asset : this.assets) {
            assetTasks.add(CompletableFuture.runAsync(() -> {
                FileWriter writer;
                for (BlueprintTexture blueprintTexture : asset.getTextures()) {
                    if ("minecraft".equals(blueprintTexture.getPath().getNamespace())) continue;
                    this.atlasManager.addSingle(blueprintTexture.getPath());
                    File png = TFile.createFile(this.assetsFolder, "textures", blueprintTexture.getPath(), "png");
                    BufferedImage image = TFile.toImage(blueprintTexture.getSource());
                    try {
                        ImageIO.write((RenderedImage)image, "png", png);
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (blueprintTexture.getMcMeta() == null || !this.shouldGenerateMCMeta(image, blueprintTexture)) continue;
                    HashMap<String, BlueprintTexture.MCMeta> lazyMCMeta = new HashMap<String, BlueprintTexture.MCMeta>();
                    File mcmeta = TFile.createFile(this.assetsFolder, "textures", blueprintTexture.getPath(), "png.mcmeta");
                    try {
                        writer = new FileWriter(mcmeta);
                        try {
                            lazyMCMeta.put("animation", blueprintTexture.getMcMeta());
                            writer.write(this.gson.toJson(lazyMCMeta));
                        }
                        finally {
                            writer.close();
                        }
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                for (Collection collection : asset.getModels().values()) {
                    if (collection.size() == 1) {
                        JavaItemModel model = (JavaItemModel)collection.iterator().next();
                        File modelJson = TFile.createFile(this.modelFolder, asset.getName(), model.getName() + ".json");
                        try (FileWriter writer2 = new FileWriter(modelJson);){
                            writer2.write(this.gson.toJson((Object)model));
                        }
                        catch (IOException e) {
                            e.printStackTrace();
                        }
                        File itemJson = TFile.createFile(this.itemsFolder, asset.getName(), model.getName() + ".json");
                        try (FileWriter writer3 = new FileWriter(itemJson);){
                            ItemModel.Model itemModel = new ItemModel.Model(new NamespacedKey(this.namespace, asset.getName() + "/" + model.getName()));
                            writer3.write(this.gson.toJson(BaseItemTransformer.wrapModel(this.gson, itemModel, asset.getName().equals("head"))));
                        }
                        catch (IOException e) {
                            e.printStackTrace();
                        }
                        continue;
                    }
                    int id = 0;
                    String modelName = "";
                    ItemModel.Composite composite = new ItemModel.Composite();
                    for (JavaItemModel model : collection) {
                        File modelJson;
                        model.finalizeModel();
                        if (id == 0) {
                            modelName = model.getName();
                            modelJson = TFile.createFile(this.modelFolder, asset.getName(), model.getName() + ".json");
                            composite.getModels().add(new ItemModel.Model(new NamespacedKey(this.namespace, asset.getName() + "/" + model.getName())));
                        } else {
                            modelJson = TFile.createFile(this.modelFolder, asset.getName(), model.getName(), id + ".json");
                            composite.getModels().add(new ItemModel.Model(new NamespacedKey(this.namespace, asset.getName() + "/" + model.getName() + "/" + id)));
                        }
                        ++id;
                        try (FileWriter writer4 = new FileWriter(modelJson);){
                            writer4.write(this.gson.toJson((Object)model));
                        }
                        catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    File itemJson = TFile.createFile(this.itemsFolder, asset.getName(), modelName + ".json");
                    try {
                        writer = new FileWriter(itemJson);
                        try {
                            writer.write(this.gson.toJson(BaseItemTransformer.wrapModel(this.gson, composite, asset.getName().equals("head"))));
                        }
                        finally {
                            writer.close();
                        }
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }, this.generatorService).handleAsync((res, ex) -> {
                if (ex != null) {
                    ex.printStackTrace();
                }
                return null;
            }, (Executor)this.generatorService));
        }
        ((CompletableFuture)CompletableFuture.allOf((CompletableFuture[])assetTasks.toArray(CompletableFuture[]::new)).thenRunAsync(() -> {
            this.assets.clear();
            this.baseItemManager.registerModels(this.namespace);
            this.baseItemManager.createModelFiles();
            this.baseItemManager.cleanUp();
            if (ConfigProperty.ATLAS.getBoolean()) {
                this.atlasManager.generateFile();
            }
            this.copyResources();
            this.executeQueuedTask(ModelGenerator.Phase.POST_ASSETS);
            this.zipResourcePack(isStartup);
        }, this.generatorService)).handleAsync((res, ex) -> {
            if (ex != null) {
                ex.printStackTrace();
            }
            return null;
        }, (Executor)this.generatorService);
    }

    private void copyInternalFiles() {
        TFile.copyResource(this.plugin, TFile.createFile(this.internalExampleFolder, "player_model.bbmodel"), "internal/player_model.bbmodel");
        TFile.copyResource(this.plugin, TFile.createFile(this.internalBlueprintFolder, "internal_fire.bbmodel"), "internal/internal_fire.bbmodel");
    }

    private void copyResources() {
        TFile.copyResource(this.plugin, this.packFolder, "pack", "pack.png");
        TFile.copyResource(this.plugin, this.packFolder, "pack", "pack.mcmeta");
        TFile.copyResource(this.plugin, this.packFolder, "pack", "assets/minecraft/items/player_head.json");
        TFile.copyResource(this.plugin, this.packFolder, "pack", "1_21_6/assets/minecraft/items/player_head.json");
        TFile.copyResource(this.plugin, this.packFolder, "pack", "assets/minecraft/models/custom/entities/player/head.json");
        TFile.copyResource(this.plugin, this.packFolder, "pack", "assets/minecraft/models/custom/entities/player/left_arm.json");
        TFile.copyResource(this.plugin, this.packFolder, "pack", "assets/minecraft/models/custom/entities/player/left_leg.json");
        TFile.copyResource(this.plugin, this.packFolder, "pack", "assets/minecraft/models/custom/entities/player/right_arm.json");
        TFile.copyResource(this.plugin, this.packFolder, "pack", "assets/minecraft/models/custom/entities/player/right_leg.json");
        TFile.copyResource(this.plugin, this.packFolder, "pack", "assets/minecraft/models/custom/entities/player/slim_left.json");
        TFile.copyResource(this.plugin, this.packFolder, "pack", "assets/minecraft/models/custom/entities/player/slim_right.json");
        TFile.copyResource(this.plugin, this.packFolder, "pack", "assets/minecraft/models/custom/entities/player/torso.json");
        TFile.copyResource(this.plugin, this.packFolder, "pack", "assets/minecraft/models/item/player_head.json");
        if (ConfigProperty.SHADER.getBoolean()) {
            TFile.copyResource(this.plugin, this.packFolder, "pack", "1_21_2/assets/minecraft/shaders/core/rendertype_entity_translucent.fsh");
            TFile.copyResource(this.plugin, this.packFolder, "pack", "1_21_2/assets/minecraft/shaders/core/rendertype_entity_translucent.json");
            TFile.copyResource(this.plugin, this.packFolder, "pack", "1_21_2/assets/minecraft/shaders/core/rendertype_entity_translucent.vsh");
            TFile.copyResource(this.plugin, this.packFolder, "pack", "1_21_5/assets/minecraft/shaders/core/entity.fsh");
            TFile.copyResource(this.plugin, this.packFolder, "pack", "1_21_5/assets/minecraft/shaders/core/entity.vsh");
            TFile.copyResource(this.plugin, this.packFolder, "pack", "1_21_6/assets/minecraft/shaders/core/entity.fsh");
            TFile.copyResource(this.plugin, this.packFolder, "pack", "1_21_6/assets/minecraft/shaders/core/entity.vsh");
            TFile.copyResource(this.plugin, this.packFolder, "pack", "assets/minecraft/shaders/core/rendertype_entity_translucent.fsh");
            TFile.copyResource(this.plugin, this.packFolder, "pack", "assets/minecraft/shaders/core/rendertype_entity_translucent.json");
            TFile.copyResource(this.plugin, this.packFolder, "pack", "assets/minecraft/shaders/core/rendertype_entity_translucent.vsh");
            TFile.copyResource(this.plugin, this.packFolder, "pack", "assets/minecraft/shaders/include/player_shader.glsl");
        }
    }

    private void zipResourcePackInternal() {
        this.executeQueuedTask(ModelGenerator.Phase.PRE_ZIPPING);
        try {
            FileOutputStream zippedFOS = new FileOutputStream(this.zippedResourcePack);
            ZipOutputStream zipOut = new ZipOutputStream(zippedFOS);
            File[] files = this.packFolder.listFiles();
            if (files == null) {
                this.executeQueuedTask(ModelGenerator.Phase.FINISHED);
                return;
            }
            for (File file : files) {
                TFile.zipFile(file, file.getName(), zipOut);
            }
            zipOut.close();
            zippedFOS.close();
            TLogger.log();
            TLogger.log(LogColor.BRIGHT_GREEN + "Resource pack zipped.");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        this.executeQueuedTask(ModelGenerator.Phase.POST_ZIPPING);
        this.executeQueuedTask(ModelGenerator.Phase.FINISHED);
    }

    private boolean shouldGenerateMCMeta(BufferedImage image, BlueprintTexture texture) {
        float uvRatio;
        if (texture.getMcMeta().isMustGenerate()) {
            return true;
        }
        if (!this.generateMeta) {
            return false;
        }
        float textureRatio = (float)image.getHeight() / (float)image.getWidth();
        if (TMath.isSimilar(textureRatio, uvRatio = (float)texture.getFrameHeight() / (float)texture.getFrameWidth())) {
            return false;
        }
        return textureRatio / uvRatio > 1.0f;
    }

    @Generated
    public ModelEngineAPI getPlugin() {
        return this.plugin;
    }

    @Generated
    public Gson getGson() {
        return this.gson;
    }

    @Generated
    public List<ModelParser> getParsers() {
        return this.parsers;
    }

    @Generated
    public List<ModelAssets> getAssets() {
        return this.assets;
    }

    @Generated
    public AtlasManager getAtlasManager() {
        return this.atlasManager;
    }

    @Generated
    public BaseItemManager getBaseItemManager() {
        return this.baseItemManager;
    }

    @Generated
    public ExecutorService getGeneratorService() {
        return this.generatorService;
    }

    @Generated
    public File getBlueprintFolder() {
        return this.blueprintFolder;
    }

    @Generated
    public File getInternalFolder() {
        return this.internalFolder;
    }

    @Generated
    public File getInternalExampleFolder() {
        return this.internalExampleFolder;
    }

    @Generated
    public File getInternalBlueprintFolder() {
        return this.internalBlueprintFolder;
    }

    @Generated
    public File getPackFolder() {
        return this.packFolder;
    }

    @Generated
    public File getAssetsFolder() {
        return this.assetsFolder;
    }

    @Generated
    public File getZippedResourcePack() {
        return this.zippedResourcePack;
    }

    @Generated
    public Map<ModelGenerator.Phase, Set<Runnable>> getTasks() {
        return this.tasks;
    }

    @Generated
    public Set<ModelGenerator.Phase> getExecuted() {
        return this.executed;
    }

    @Generated
    public String getNamespace() {
        return this.namespace;
    }

    @Generated
    public File getModelFolder() {
        return this.modelFolder;
    }

    @Generated
    public File getItemsFolder() {
        return this.itemsFolder;
    }

    @Generated
    public boolean isGenerateMeta() {
        return this.generateMeta;
    }

    @Generated
    public boolean isDeleteOld() {
        return this.deleteOld;
    }

    @Override
    @Generated
    public boolean isInitialized() {
        return this.initialized;
    }

    @Generated
    public long[] getTimer() {
        return this.timer;
    }

    @Generated
    public Map<String, String> getTextureNameCache() {
        return this.textureNameCache;
    }

    private static /* synthetic */ Object lambda$importModelsInternal$20(Void res, Throwable ex) {
        if (ex != null) {
            ex.printStackTrace();
        }
        return null;
    }

    private /* synthetic */ void lambda$importModelsInternal$19(boolean isStartup) {
        this.initialized = true;
        this.baseItemManager.endSession();
        this.executeQueuedTask(ModelGenerator.Phase.POST_IMPORT);
        this.generateAssets(isStartup);
    }

    private static /* synthetic */ CompletableFuture[] lambda$importModelsInternal$18(int x$0) {
        return new CompletableFuture[x$0];
    }

    private static /* synthetic */ Void lambda$importModelsInternal$17(Void res, Throwable ex) {
        if (ex != null) {
            ex.printStackTrace();
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private /* synthetic */ void lambda$importModelsInternal$16(File file, ModelRegistry registry) {
        boolean fileParsed = false;
        ErrorCollector collector = new ErrorCollector(file.getName());
        for (ModelParser parser : this.parsers) {
            if (!parser.validateFile(file)) continue;
            try {
                Pair<ModelBlueprint, ModelAssets> modelData = parser.generate(file, collector);
                if (modelData == null) {
                    if (!collector.hasError()) continue;
                    collector.logAll();
                    continue;
                }
                ModelBlueprint blueprint = (ModelBlueprint)modelData.left();
                ModelAssets asset = (ModelAssets)modelData.right();
                blueprint.finalizeModel(collector);
                for (Map.Entry<String, BlueprintBone> boneEntry : blueprint.getFlatMap().entrySet()) {
                    BlueprintBone bone = boneEntry.getValue();
                    if (!bone.isRenderer()) continue;
                    BaseItemManager baseItemManager = this.baseItemManager;
                    synchronized (baseItemManager) {
                        this.baseItemManager.requestId(bone, bone.getModelData().getMultiModels().getKeys());
                    }
                    bone.getModelData().setSingleComposite(new ItemModelData.SingleComposite(new NamespacedKey(this.namespace, blueprint.getName() + "/" + bone.getName())));
                }
                List<ModelAssets> list = registry;
                synchronized (list) {
                    registry.registerBlueprint(blueprint);
                }
                list = this.assets;
                synchronized (list) {
                    this.assets.add(asset);
                }
                fileParsed = true;
                break;
            }
            catch (Exception e) {
                e.printStackTrace();
                break;
            }
        }
        if (!fileParsed) {
            IError.UNKNOWN_FORMAT.log(collector);
        }
        Object object = this.timer;
        synchronized (this.timer) {
            collector.logAll();
            // ** MonitorExit[var5_5] (shouldn't be in output)
            return;
        }
    }
}

