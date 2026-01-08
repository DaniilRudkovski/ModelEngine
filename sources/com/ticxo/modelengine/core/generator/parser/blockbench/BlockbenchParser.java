/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  it.unimi.dsi.fastutil.Pair
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 *  org.joml.Vector3d
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 */
package com.ticxo.modelengine.core.generator.parser.blockbench;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.animation.BlueprintAnimation;
import com.ticxo.modelengine.api.animation.Timeline;
import com.ticxo.modelengine.api.animation.keyframe.KeyframeType;
import com.ticxo.modelengine.api.animation.keyframe.KeyframeTypes;
import com.ticxo.modelengine.api.animation.keyframe.data.KeyframeReaderRegistry;
import com.ticxo.modelengine.api.animation.keyframe.type.ScriptKeyframe;
import com.ticxo.modelengine.api.animation.keyframe.type.VectorKeyframe;
import com.ticxo.modelengine.api.error.ErrorCollector;
import com.ticxo.modelengine.api.error.IError;
import com.ticxo.modelengine.api.error.WarnBadTexture;
import com.ticxo.modelengine.api.error.WarnDuplicateTexture;
import com.ticxo.modelengine.api.error.WarningDuplicateBoneName;
import com.ticxo.modelengine.api.events.RegisterBehaviorParserEvent;
import com.ticxo.modelengine.api.generator.assets.BlueprintTexture;
import com.ticxo.modelengine.api.generator.assets.ItemModelData;
import com.ticxo.modelengine.api.generator.assets.JavaItemModel;
import com.ticxo.modelengine.api.generator.assets.ModelAssets;
import com.ticxo.modelengine.api.generator.blueprint.BlueprintBone;
import com.ticxo.modelengine.api.generator.blueprint.ModelBlueprint;
import com.ticxo.modelengine.api.generator.parser.ModelParser;
import com.ticxo.modelengine.api.generator.parser.blockbench.BlockbenchBehaviorParser;
import com.ticxo.modelengine.api.generator.parser.blockbench.BlockbenchDeserializer;
import com.ticxo.modelengine.api.generator.parser.blockbench.BlockbenchModel;
import com.ticxo.modelengine.api.model.bone.behavior.BoneBehaviorType;
import com.ticxo.modelengine.api.utils.MiscUtils;
import com.ticxo.modelengine.api.utils.TFile;
import com.ticxo.modelengine.api.utils.data.ResourceLocation;
import com.ticxo.modelengine.api.utils.math.Direction;
import com.ticxo.modelengine.api.utils.math.TMath;
import com.ticxo.modelengine.core.generator.ModelGeneratorImpl;
import com.ticxo.modelengine.core.generator.parser.blockbench.json.DefaultBehaviorParser;
import com.ticxo.modelengine.core.generator.parser.blockbench.json.MCMetaDeserializer;
import com.ticxo.modelengine.core.generator.processed.ProcessedBone;
import it.unimi.dsi.fastutil.Pair;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class BlockbenchParser
implements ModelParser {
    private final Gson gson = new GsonBuilder().registerTypeAdapter(BlockbenchModel.class, (Object)new BlockbenchDeserializer()).registerTypeAdapter(BlueprintTexture.MCMeta.class, (Object)new MCMetaDeserializer()).create();
    private final Set<BlockbenchBehaviorParser> behaviorParsers = new LinkedHashSet<BlockbenchBehaviorParser>();
    private final ModelGeneratorImpl generator;

    public BlockbenchParser(ModelGeneratorImpl generator) {
        this.generator = generator;
        this.behaviorParsers.add(new DefaultBehaviorParser());
        ModelEngineAPI.callEvent(new RegisterBehaviorParserEvent(this.behaviorParsers));
    }

    @Override
    public boolean validateFile(File file) {
        return TFile.isExtension(file.getName(), "bbmodel");
    }

    @Override
    public Pair<ModelBlueprint, ModelAssets> generate(File file, ErrorCollector collector) throws Exception {
        Pair pair;
        String modelName = TFile.removeExtension(file.getName()).toLowerCase(Locale.ENGLISH);
        FileReader reader = new FileReader(file);
        try {
            BlockbenchModel blockbenchModel = (BlockbenchModel)this.gson.fromJson((Reader)reader, BlockbenchModel.class);
            ModelBlueprint blueprint = new ModelBlueprint();
            blueprint.setName(modelName);
            this.populateBlueprint(blockbenchModel, blueprint, collector);
            blueprint.constructFlatBoneMap(collector);
            blueprint.cacheBoneBehaviors(collector);
            ModelAssets assets = new ModelAssets();
            assets.setName(modelName);
            this.populateAssets(blockbenchModel, blueprint, assets, collector);
            pair = Pair.of((Object)blueprint, (Object)assets);
        }
        catch (Throwable throwable) {
            try {
                try {
                    reader.close();
                }
                catch (Throwable throwable2) {
                    throwable.addSuppressed(throwable2);
                }
                throw throwable;
            }
            catch (Exception e) {
                collector.collect(new IError(){

                    @Override
                    public String getErrorMessage() {
                        return "Error: " + e.getMessage();
                    }

                    @Override
                    public IError.Severity getSeverity() {
                        return IError.Severity.ERROR;
                    }
                });
                return null;
            }
        }
        reader.close();
        return pair;
    }

    private void populateBlueprint(BlockbenchModel bbmodel, ModelBlueprint blueprint, ErrorCollector collector) {
        String[] lines;
        this.behaviorParsers.forEach(parser -> parser.processModel(collector, bbmodel, blueprint));
        HashMap uuidToBone = new HashMap();
        bbmodel.getOutliner().forEach((uuid, group) -> {
            BlueprintBone bone = this.readBone(collector, bbmodel, null, (BlockbenchModel.Group)group, uuidToBone);
            if (blueprint.getBones().containsKey(bone.getName())) {
                new WarningDuplicateBoneName(bone.getName(), bone.getUuid()).log(collector);
                bone.setName(uuid.toString());
            }
            blueprint.getBones().put(bone.getName(), bone);
            uuidToBone.put(bone.getUuid(), bone);
        });
        for (Map.Entry<String, BlockbenchModel.Animation> entry : bbmodel.getAnimations().entrySet()) {
            Object effectChannels;
            Map<Float, BlockbenchModel.Keyframe> script;
            String name = entry.getKey().toLowerCase(Locale.ENGLISH);
            BlockbenchModel.Animation value = entry.getValue();
            BlueprintAnimation blueprintAnimation = new BlueprintAnimation(blueprint, name);
            if (value.getEffects() != null && (script = (effectChannels = value.getEffects().getChannels()).get("timeline")) != null) {
                for (Map.Entry<Float, BlockbenchModel.Keyframe> scriptEntry : script.entrySet()) {
                    BlockbenchModel.Keyframe bbFrame = scriptEntry.getValue();
                    ScriptKeyframe frame = blueprintAnimation.getGlobalTimeline().getKeyframe(scriptEntry.getKey().floatValue(), KeyframeTypes.SCRIPT);
                    for (Map<String, String> data : bbFrame.getData_points()) {
                        String instructions = data.getOrDefault("script", "");
                        for (String instruction : instructions.split("\n")) {
                            frame.getScript().add(ScriptKeyframe.Script.from(instruction));
                        }
                    }
                }
            }
            effectChannels = value.getAnimators().entrySet().iterator();
            while (effectChannels.hasNext()) {
                Map.Entry timelineEntry = (Map.Entry)effectChannels.next();
                BlockbenchModel.Animator animator = (BlockbenchModel.Animator)timelineEntry.getValue();
                Timeline timeline = new Timeline(blueprintAnimation, animator.getRotation_global() != null && animator.getRotation_global() != false);
                BlockbenchParser.putVectorKeyframes(animator, "position", timeline, KeyframeTypes.POSITION, 0.0625f, 0.0625f, -0.0625f);
                BlockbenchParser.putVectorKeyframes(animator, "rotation", timeline, KeyframeTypes.ROTATION, (float)Math.PI / 180, (float)(-Math.PI) / 180, (float)(-Math.PI) / 180);
                BlockbenchParser.putVectorKeyframes(animator, "scale", timeline, KeyframeTypes.SCALE, 1.0f, 1.0f, 1.0f);
                UUID uuid2 = animator.getUuid();
                if (uuid2 == null) continue;
                blueprintAnimation.getTimelines().put(uuid2, timeline);
            }
            blueprintAnimation.setLength(value.getLength().floatValue());
            blueprintAnimation.setLoopMode(BlueprintAnimation.LoopMode.get(value.getLoop()));
            blueprintAnimation.setOverride(value.getOverride());
            blueprint.getAnimations().put(name, blueprintAnimation);
        }
        for (String line : lines = bbmodel.getAnimation_variable_placeholders().split("\n")) {
            String[] pair = line.split("=", 2);
            if (pair.length < 2) continue;
            blueprint.getAnimationsPlaceholders().put(pair[0], pair[1]);
        }
    }

    private BlueprintBone readBone(ErrorCollector collector, BlockbenchModel model, @Nullable BlueprintBone parent, BlockbenchModel.Group group, Map<UUID, BlueprintBone> uuidToBone) {
        Object rotatedLocal;
        BlueprintBone blueprintBone = new BlueprintBone();
        blueprintBone.setName(group.getName().toLowerCase(Locale.ENGLISH));
        blueprintBone.setUuid(group.getUuid());
        blueprintBone.setGlobalPosition(BlockbenchParser.vector3f(group.getOrigin()).mul(-0.0625f, 0.0625f, -0.0625f));
        Vector3f rotation = BlockbenchParser.vector3f(group.getRotation()).mul((float)(-Math.PI) / 180, (float)Math.PI / 180, (float)(-Math.PI) / 180);
        blueprintBone.setLocalRotation(rotation);
        blueprintBone.getLocalQuaternion().rotateZYX(rotation.z, rotation.y, rotation.x);
        if (parent != null) {
            blueprintBone.setLocalPosition(blueprintBone.getGlobalPosition().sub((Vector3fc)parent.getGlobalPosition(), new Vector3f()));
            Quaternionf boneQuaternion = new Quaternionf().rotationZYX(rotation.z, rotation.y, rotation.x);
            Quaternionf parentQuaternion = parent.getGlobalQuaternion();
            parentQuaternion.mul((Quaternionfc)boneQuaternion, boneQuaternion);
            Vector3f global = TMath.getEulerAnglesZYX(boneQuaternion, new Vector3f());
            blueprintBone.setGlobalRotation(new Vector3f(global.x, global.y, global.z));
            blueprintBone.setGlobalQuaternion(boneQuaternion);
            rotatedLocal = blueprintBone.getLocalPosition().rotate((Quaternionfc)parentQuaternion, new Vector3f());
            blueprintBone.setRotatedGlobalPosition(rotatedLocal.add((Vector3fc)parent.getRotatedGlobalPosition()));
        } else {
            blueprintBone.setLocalPosition(new Vector3f((Vector3fc)blueprintBone.getGlobalPosition()));
            blueprintBone.setGlobalRotation(rotation);
            blueprintBone.setGlobalQuaternion(new Quaternionf((Quaternionfc)blueprintBone.getLocalQuaternion()));
            blueprintBone.setRotatedGlobalPosition(new Vector3f((Vector3fc)blueprintBone.getGlobalPosition()));
        }
        blueprintBone.setParent(parent);
        this.behaviorParsers.forEach(parser -> parser.processBone(collector, model, group, blueprintBone));
        for (Map.Entry<UUID, BlockbenchModel.Group> entry : group.getChildGroup().entrySet()) {
            BlueprintBone child = this.readBone(collector, model, blueprintBone, entry.getValue(), uuidToBone);
            if (blueprintBone.getChildren().containsKey(child.getName())) {
                new WarningDuplicateBoneName(child.getName(), child.getUuid()).log(collector);
                child.setName(child.getUuid().toString());
            }
            blueprintBone.getChildren().put(child.getName(), child);
        }
        for (UUID element : group.getElement()) {
            rotatedLocal = model.getElements().get(element);
            if (!(rotatedLocal instanceof BlockbenchModel.AnimatableElement)) continue;
            BlockbenchModel.AnimatableElement animatable = (BlockbenchModel.AnimatableElement)rotatedLocal;
            BlueprintBone child = this.readElementAsBone(blueprintBone, animatable, uuidToBone);
            if (blueprintBone.getChildren().containsKey(child.getName())) {
                new WarningDuplicateBoneName(child.getName(), child.getUuid()).log(collector);
                child.setName(child.getUuid().toString());
            }
            blueprintBone.getChildren().put(child.getName(), child);
        }
        uuidToBone.put(blueprintBone.getUuid(), blueprintBone);
        return blueprintBone;
    }

    private BlueprintBone readElementAsBone(@Nullable BlueprintBone parent, BlockbenchModel.AnimatableElement element, Map<UUID, BlueprintBone> uuidToBone) {
        BlueprintBone blueprintBone = new BlueprintBone();
        blueprintBone.setName(element.getName().toLowerCase(Locale.ENGLISH));
        blueprintBone.setUuid(element.getUuid());
        blueprintBone.setGlobalPosition(BlockbenchParser.vector3f(element.getOrigin()).mul(-0.0625f, 0.0625f, -0.0625f));
        Vector3f rotation = BlockbenchParser.vector3f(element.getRotation()).mul((float)(-Math.PI) / 180, (float)Math.PI / 180, (float)(-Math.PI) / 180);
        blueprintBone.setLocalRotation(rotation);
        blueprintBone.getLocalQuaternion().rotateZYX(rotation.z, rotation.y, rotation.x);
        if (parent != null) {
            blueprintBone.setLocalPosition(blueprintBone.getGlobalPosition().sub((Vector3fc)parent.getGlobalPosition(), new Vector3f()));
            Quaternionf boneQuaternion = new Quaternionf().rotationZYX(rotation.z, rotation.y, rotation.x);
            Quaternionf parentQuaternion = parent.getGlobalQuaternion();
            parentQuaternion.mul((Quaternionfc)boneQuaternion, boneQuaternion);
            Vector3f global = TMath.getEulerAnglesZYX(boneQuaternion, new Vector3f());
            blueprintBone.setGlobalRotation(new Vector3f(global.x, global.y, global.z));
            blueprintBone.setGlobalQuaternion(boneQuaternion);
            Vector3f rotatedLocal = blueprintBone.getLocalPosition().rotate((Quaternionfc)parentQuaternion, new Vector3f());
            blueprintBone.setRotatedGlobalPosition(rotatedLocal.add((Vector3fc)parent.getRotatedGlobalPosition()));
        } else {
            blueprintBone.setLocalPosition(new Vector3f((Vector3fc)blueprintBone.getGlobalPosition()));
            blueprintBone.setGlobalRotation(rotation);
            blueprintBone.setGlobalQuaternion(new Quaternionf((Quaternionfc)blueprintBone.getLocalQuaternion()));
            blueprintBone.setRotatedGlobalPosition(new Vector3f((Vector3fc)blueprintBone.getGlobalPosition()));
        }
        blueprintBone.setParent(parent);
        uuidToBone.put(blueprintBone.getUuid(), blueprintBone);
        return blueprintBone;
    }

    private void populateAssets(BlockbenchModel bbmodel, ModelBlueprint blueprint, ModelAssets assets, ErrorCollector collector) {
        this.populateTexture(bbmodel, blueprint, assets, collector);
        this.populateModel(bbmodel, blueprint, assets);
    }

    private void populateTexture(BlockbenchModel bbmodel, ModelBlueprint blueprint, ModelAssets assets, ErrorCollector collector) {
        Map<Integer, BlockbenchModel.Texture> textures = bbmodel.getTextures();
        for (Map.Entry<Integer, BlockbenchModel.Texture> entry : textures.entrySet()) {
            BlueprintTexture.MCMeta meta;
            Integer id = entry.getKey();
            BlockbenchModel.Texture bbTexture = entry.getValue();
            if (bbTexture.getRaw_mcmeta() == null) {
                meta = new BlueprintTexture.MCMeta();
                meta.setFrametime(bbTexture.getFrame_time());
                meta.setInterpolate(MiscUtils.orDef(false, bbTexture.getFrame_interpolate()) != false ? Boolean.valueOf(true) : null);
                if (bbTexture.getFrame_order() != null && !bbTexture.getFrame_order().isBlank()) {
                    for (String frame : bbTexture.getFrame_order().split(" ")) {
                        meta.addFrame(TMath.tryParse(frame, 0));
                    }
                }
            } else {
                meta = (BlueprintTexture.MCMeta)this.gson.fromJson(bbTexture.getRaw_mcmeta(), BlueprintTexture.MCMeta.class);
                meta.setMustGenerate(true);
            }
            BlueprintTexture texture = this.constructBlueprintTexture(id, bbmodel, bbTexture, meta, blueprint, collector);
            assets.getTextures().add(texture);
        }
    }

    @NotNull
    private BlueprintTexture constructBlueprintTexture(Integer id, BlockbenchModel bbmodel, BlockbenchModel.Texture bbTexture, BlueprintTexture.MCMeta meta, ModelBlueprint blueprint, ErrorCollector collector) {
        Map<String, String> cache;
        BlueprintTexture texture = new BlueprintTexture();
        texture.setId(id);
        texture.setFrameWidth(MiscUtils.or(bbTexture.getUv_width(), bbmodel.getResolution().getWidth()));
        texture.setFrameHeight(MiscUtils.or(bbTexture.getUv_height(), bbmodel.getResolution().getHeight()));
        String namespace = bbTexture.getNamespace().isBlank() ? this.generator.getNamespace() : bbTexture.getNamespace();
        String folder = namespace.equals(this.generator.getNamespace()) || bbTexture.getFolder().isBlank() ? "entity" : bbTexture.getFolder().toLowerCase(Locale.ENGLISH);
        String name = TFile.removeExtension(bbTexture.getName()).toLowerCase(Locale.ENGLISH);
        ResourceLocation location = new ResourceLocation(namespace, folder + "/" + name);
        if (!location.isValid()) {
            ResourceLocation correct = new ResourceLocation(this.generator.getNamespace(), "entity/" + MiscUtils.generateUUIDFromString(name));
            new WarnBadTexture(location, correct).log(collector);
            location = correct;
        }
        if (Optional.ofNullable((cache = this.generator.getTextureNameCache()).get(location.toString())).map(string -> !string.equals(bbTexture.getSource())).orElse(false).booleanValue()) {
            ResourceLocation correct = new ResourceLocation(this.generator.getNamespace(), "entity/" + MiscUtils.generateUUIDFromString(blueprint.getName() + ":" + name));
            new WarnDuplicateTexture(location, correct).log(collector);
            location = correct;
        }
        this.generator.getTextureNameCache().put(location.toString(), bbTexture.getSource());
        texture.setPath(location);
        texture.setMcMeta(meta);
        texture.setSource(bbTexture.getSource());
        return texture;
    }

    private void populateModel(BlockbenchModel bbmodel, ModelBlueprint blueprint, ModelAssets assets) {
        block0: for (Map.Entry<String, BlueprintBone> entry : blueprint.getFlatMap().entrySet()) {
            String name = entry.getKey();
            BlueprintBone bone = entry.getValue();
            BlockbenchModel.Group bbBone = bbmodel.getGroup(bone.getUuid());
            if (bbBone == null || !bbBone.isExport()) continue;
            for (BoneBehaviorType<?> behaviorType : bone.getCachedBehaviorProvider().keySet()) {
                if (!behaviorType.isIgnoreCubes()) continue;
                continue block0;
            }
            ProcessedBone processedBone = this.process(bbmodel, bone, bbBone, assets);
            Set<JavaItemModel> javaModels = processedBone.getModels();
            if (javaModels.isEmpty()) continue;
            bone.setRenderer(true);
            bone.setScale(processedBone.getScale());
            ItemModelData.MultiModels multiModel = bone.getModelData().getMultiModels();
            for (int i = 0; i < javaModels.size(); ++i) {
                multiModel.addSubModel(new ItemModelData.SubModel(blueprint.getName() + ":" + (String)(i == 0 ? bone.getName() : bone.getName() + "/" + i)));
            }
            assets.getModels().put(name, javaModels);
        }
    }

    private ProcessedBone process(BlockbenchModel model, BlueprintBone blueprintBone, BlockbenchModel.Group group, ModelAssets assets) {
        ProcessedBone bone = new ProcessedBone(blueprintBone.getName(), BlockbenchParser.vector3f(group.getOrigin()), BlockbenchParser.vector3f(group.getRotation()));
        for (UUID uuid : group.getElement()) {
            BlockbenchModel.Element element = model.getElements().get(uuid);
            if (!(element instanceof BlockbenchModel.Cube)) continue;
            BlockbenchModel.Cube cube = (BlockbenchModel.Cube)element;
            HashMap<Direction, ProcessedBone.Face> faces = new HashMap<Direction, ProcessedBone.Face>();
            for (Map.Entry<String, BlockbenchModel.Face> faceEntry : cube.getFaces().entrySet()) {
                BlockbenchModel.Face face = faceEntry.getValue();
                if (face.isEmpty()) continue;
                Float[] faceUV = face.getUv();
                ProcessedBone.UV uv = new ProcessedBone.UV(faceUV[0] == null ? 0.0f : faceUV[0].floatValue(), faceUV[1] == null ? 0.0f : faceUV[1].floatValue(), faceUV[2] == null ? 0.0f : faceUV[2].floatValue(), faceUV[3] == null ? 0.0f : faceUV[3].floatValue(), face.getRotation() == null ? 0 : face.getRotation());
                ProcessedBone.Face procFace = new ProcessedBone.Face(uv, face.getTexture());
                faces.put(Direction.valueOf(faceEntry.getKey().toUpperCase(Locale.ENGLISH)), procFace);
            }
            if (faces.isEmpty()) continue;
            ProcessedBone.Cube procCube = new ProcessedBone.Cube(cube.getName(), BlockbenchParser.vector3d(cube.getOrigin()).sub((Vector3fc)bone.getBoneOrigin()), BlockbenchParser.vector3d(cube.getRotation()), BlockbenchParser.vector3d(cube.getFrom()).sub((Vector3fc)bone.getBoneOrigin()), BlockbenchParser.vector3d(cube.getTo()).sub((Vector3fc)bone.getBoneOrigin()), faces, cube.getInflate() == null ? 0.0f : cube.getInflate().floatValue());
            bone.getCubes().add(procCube);
            if (!cube.isTranslucent()) continue;
            blueprintBone.getModelData().setTranslucent(true);
        }
        bone.splitModels(model, assets);
        return bone;
    }

    private static Vector3f vector3f(@Nullable Float[] vec) {
        return vec == null ? new Vector3f() : new Vector3f(vec[0] == null ? 0.0f : vec[0].floatValue(), vec[1] == null ? 0.0f : vec[1].floatValue(), vec[2] == null ? 0.0f : vec[2].floatValue());
    }

    private static Vector3d vector3d(@Nullable Float[] vec) {
        return vec == null ? new Vector3d() : new Vector3d(vec[0] == null ? 0.0 : (double)vec[0].floatValue(), vec[1] == null ? 0.0 : (double)vec[1].floatValue(), vec[2] == null ? 0.0 : (double)vec[2].floatValue());
    }

    private static float[] unwrap(Float[] arr) {
        float[] a = new float[arr.length];
        for (int i = 0; i < arr.length; ++i) {
            a[i] = arr[i].floatValue();
        }
        return a;
    }

    private static void putVectorKeyframes(BlockbenchModel.Animator animator, String channel, Timeline timeline, KeyframeType<VectorKeyframe, Vector3f> keyframeType, float scaleX, float scaleY, float scaleZ) {
        Map<Float, BlockbenchModel.Keyframe> frames = animator.getChannels().get(channel);
        if (frames == null) {
            return;
        }
        for (Map.Entry<Float, BlockbenchModel.Keyframe> entry : frames.entrySet()) {
            Float time = entry.getKey();
            BlockbenchModel.Keyframe keyframe = entry.getValue();
            VectorKeyframe vectorKeyframe = timeline.getKeyframe(time.floatValue(), keyframeType);
            if (!keyframe.getData_points().isEmpty()) {
                KeyframeReaderRegistry reader = ModelEngineAPI.getAPI().getKeyframeReaderRegistry();
                Map<String, String> pre = keyframe.getData_points().get(0);
                vectorKeyframe.setXFactor(scaleX).setYFactor(scaleY).setZFactor(scaleZ).setX(reader.tryParse(pre.getOrDefault("x", "0"))).setY(reader.tryParse(pre.getOrDefault("y", "0"))).setZ(reader.tryParse(pre.getOrDefault("z", "0")));
                if (keyframe.getData_points().size() >= 2) {
                    Map<String, String> post = keyframe.getData_points().get(1);
                    vectorKeyframe.setDiscontinuous(true);
                    vectorKeyframe.setPostX(reader.tryParse(post.getOrDefault("x", "0"))).setPostY(reader.tryParse(post.getOrDefault("y", "0"))).setPostZ(reader.tryParse(post.getOrDefault("z", "0")));
                }
            }
            vectorKeyframe.setInterpolation(keyframe.getInterpolation());
            if (!vectorKeyframe.isBezier()) continue;
            vectorKeyframe.setBezierLeftTime(keyframe.getBezier_left_time()[0], keyframe.getBezier_left_time()[1], keyframe.getBezier_left_time()[2]);
            vectorKeyframe.setBezierLeftValue(keyframe.getBezier_left_value()[0], keyframe.getBezier_left_value()[1], keyframe.getBezier_left_value()[2]);
            vectorKeyframe.setBezierRightTime(keyframe.getBezier_right_time()[0], keyframe.getBezier_right_time()[1], keyframe.getBezier_right_time()[2]);
            vectorKeyframe.setBezierRightValue(keyframe.getBezier_right_value()[0], keyframe.getBezier_right_value()[1], keyframe.getBezier_right_value()[2]);
        }
    }
}

