/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonDeserializer
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  org.jetbrains.annotations.Nullable
 */
package com.ticxo.modelengine.api.generator.parser.blockbench;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.ticxo.modelengine.api.generator.parser.blockbench.BlockbenchModel;
import com.ticxo.modelengine.api.utils.data.GSONUtils;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import org.jetbrains.annotations.Nullable;

public class v5_0_Deserializer {
    private static final Gson gson = new GsonBuilder().registerTypeAdapter(BlockbenchModel.Element.class, (Object)new ElementDeserializer()).registerTypeAdapter(BlockbenchModel.Group.class, (Object)new GroupDeserializer()).registerTypeAdapter(BlockbenchModel.Animation.class, (Object)new AnimationDeserializer()).registerTypeAdapter(BlockbenchModel.Animator.class, (Object)new AnimatorDeserializer()).registerTypeAdapter(BlockbenchModel.Face.class, (Object)new FaceDeserializer()).create();

    protected static BlockbenchModel deserialize(JsonObject root) {
        BlockbenchModel.Resolution resolution = (BlockbenchModel.Resolution)gson.fromJson(root.get("resolution"), BlockbenchModel.Resolution.class);
        HashMap<UUID, BlockbenchModel.Element> elements = new HashMap<UUID, BlockbenchModel.Element>();
        GSONUtils.ifArray((JsonElement)root, "elements", e -> {
            BlockbenchModel.Element element = (BlockbenchModel.Element)gson.fromJson(e, BlockbenchModel.Element.class);
            elements.put(element.uuid, element);
        });
        HashMap groups = new HashMap();
        GSONUtils.ifArray((JsonElement)root, "groups", e -> {
            if (e.isJsonObject()) {
                BlockbenchModel.Group group = (BlockbenchModel.Group)gson.fromJson(e, BlockbenchModel.Group.class);
                groups.put(group.uuid, group);
            }
        });
        HashMap<UUID, BlockbenchModel.Group> outliner = new HashMap<UUID, BlockbenchModel.Group>();
        GSONUtils.ifArray((JsonElement)root, "outliner", e -> {
            BlockbenchModel.Group g;
            if (e.isJsonObject() && (g = v5_0_Deserializer.nestGroups(e, groups)) != null) {
                outliner.put(g.uuid, g);
            }
        });
        HashMap<Integer, BlockbenchModel.Texture> textures = new HashMap<Integer, BlockbenchModel.Texture>();
        HashMap textureCache = new HashMap();
        GSONUtils.ifArray((JsonElement)root, "textures", (index, e) -> {
            BlockbenchModel.Texture texture = (BlockbenchModel.Texture)gson.fromJson(e, BlockbenchModel.Texture.class);
            textures.put((Integer)index, texture);
            textureCache.put(texture.uuid, texture);
        });
        LinkedHashMap<String, BlockbenchModel.Animation> animations = new LinkedHashMap<String, BlockbenchModel.Animation>();
        GSONUtils.ifArray((JsonElement)root, "animations", e -> {
            BlockbenchModel.Animation animation = (BlockbenchModel.Animation)gson.fromJson(e, BlockbenchModel.Animation.class);
            animations.put(animation.name, animation);
        });
        GSONUtils.ifPresent((JsonElement)root, "mcmetas", element -> {
            JsonObject elementData = element.getAsJsonObject();
            for (Map.Entry entry : elementData.entrySet()) {
                UUID uuid = UUID.fromString((String)entry.getKey());
                BlockbenchModel.Texture texture = (BlockbenchModel.Texture)textureCache.get(uuid);
                if (texture == null) continue;
                texture.raw_mcmeta = ((JsonElement)entry.getValue()).toString();
            }
        });
        String animationPlaceholder = GSONUtils.get((JsonElement)root, "animation_variable_placeholders", JsonElement::getAsString, "");
        return new BlockbenchModel(resolution, elements, outliner, textures, animations, animationPlaceholder).preprocess();
    }

    @Nullable
    private static BlockbenchModel.Group nestGroups(JsonElement element, Map<UUID, BlockbenchModel.Group> flatGroups) {
        JsonObject obj = element.getAsJsonObject();
        UUID uuid = GSONUtils.getAsUUID(obj.get("uuid"));
        BlockbenchModel.Group root = flatGroups.get(uuid);
        if (root == null) {
            return null;
        }
        GSONUtils.ifArray(element, "children", e -> {
            BlockbenchModel.Group g;
            if (e.isJsonPrimitive()) {
                root.element.add(GSONUtils.getAsUUID(e));
            } else if (e.isJsonObject() && (g = v5_0_Deserializer.nestGroups(e, flatGroups)) != null) {
                root.getChildGroup().put(g.uuid, g);
                g.parentGroup = root.uuid;
            }
        });
        return root;
    }

    static class ElementDeserializer
    implements JsonDeserializer<BlockbenchModel.Element> {
        ElementDeserializer() {
        }

        public BlockbenchModel.Element deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            String elementType;
            JsonObject root = jsonElement.getAsJsonObject();
            return (BlockbenchModel.Element)gson.fromJson(jsonElement, switch (elementType = GSONUtils.get((JsonElement)root, "type", JsonElement::getAsString, "cube")) {
                case "cube" -> BlockbenchModel.Cube.class;
                case "null_object" -> BlockbenchModel.NullObject.class;
                case "locator" -> BlockbenchModel.Locator.class;
                case "camera" -> BlockbenchModel.Camera.class;
                default -> BlockbenchModel.Unknown.class;
            });
        }
    }

    static class GroupDeserializer
    implements JsonDeserializer<BlockbenchModel.Group> {
        GroupDeserializer() {
        }

        public BlockbenchModel.Group deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            BlockbenchModel.Group group = new BlockbenchModel.Group();
            group.name = GSONUtils.get(jsonElement, "name", JsonElement::getAsString);
            group.origin = GSONUtils.get(jsonElement, "origin", GSONUtils::getAsFloatArray);
            group.rotation = GSONUtils.get(jsonElement, "rotation", GSONUtils::getAsFloatArray);
            group.uuid = GSONUtils.get(jsonElement, "uuid", GSONUtils::getAsUUID);
            group.export = GSONUtils.get(jsonElement, "export", JsonElement::getAsBoolean, true);
            GSONUtils.ifArray(jsonElement, "children", e -> {
                if (e.isJsonPrimitive()) {
                    group.element.add(GSONUtils.getAsUUID(e));
                } else if (e.isJsonObject()) {
                    BlockbenchModel.Group childGroup = (BlockbenchModel.Group)gson.fromJson(e, BlockbenchModel.Group.class);
                    group.childGroup.put(childGroup.uuid, childGroup);
                    childGroup.parentGroup = group.uuid;
                }
            });
            return group;
        }
    }

    static class AnimationDeserializer
    implements JsonDeserializer<BlockbenchModel.Animation> {
        AnimationDeserializer() {
        }

        public BlockbenchModel.Animation deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            BlockbenchModel.Animation animation = new BlockbenchModel.Animation();
            animation.uuid = GSONUtils.get(jsonElement, "uuid", GSONUtils::getAsUUID);
            animation.name = GSONUtils.get(jsonElement, "name", JsonElement::getAsString);
            animation.loop = GSONUtils.get(jsonElement, "loop", JsonElement::getAsString);
            animation.override = GSONUtils.get(jsonElement, "override", JsonElement::getAsBoolean);
            animation.length = GSONUtils.get(jsonElement, "length", JsonElement::getAsFloat);
            GSONUtils.ifPresent(jsonElement, "animators", e -> {
                if (!e.isJsonObject()) {
                    return;
                }
                JsonObject animators = e.getAsJsonObject();
                for (String key : animators.keySet()) {
                    BlockbenchModel.Animator animator = (BlockbenchModel.Animator)gson.fromJson((JsonElement)animators.getAsJsonObject(key), BlockbenchModel.Animator.class);
                    if (key.equals("effects")) {
                        animation.effects = animator;
                        continue;
                    }
                    try {
                        UUID uuid;
                        animator.uuid = uuid = UUID.fromString(key);
                        animation.animators.put(uuid, animator);
                    }
                    catch (IllegalArgumentException ignored) {
                        if (animation.unknown_animators == null) {
                            animation.unknown_animators = new HashMap<String, BlockbenchModel.Animator>();
                        }
                        animation.unknown_animators.put(key, animator);
                    }
                }
            });
            return animation;
        }
    }

    static class AnimatorDeserializer
    implements JsonDeserializer<BlockbenchModel.Animator> {
        AnimatorDeserializer() {
        }

        public BlockbenchModel.Animator deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            BlockbenchModel.Animator animator = new BlockbenchModel.Animator();
            animator.name = GSONUtils.get(jsonElement, "name", JsonElement::getAsString);
            animator.rotation_global = GSONUtils.get(jsonElement, "rotation_global", JsonElement::getAsBoolean);
            GSONUtils.ifArray(jsonElement, "keyframes", e -> {
                BlockbenchModel.Keyframe keyframe = (BlockbenchModel.Keyframe)gson.fromJson(e, BlockbenchModel.Keyframe.class);
                switch (keyframe.channel.toUpperCase(Locale.ENGLISH)) {
                    case "POSITION": {
                        keyframe.getData_points().forEach(map -> this.tryFlipSign((Map<String, String>)map, "x"));
                        break;
                    }
                    case "ROTATION": {
                        keyframe.getData_points().forEach(map -> {
                            this.tryFlipSign((Map<String, String>)map, "x");
                            this.tryFlipSign((Map<String, String>)map, "y");
                        });
                    }
                }
                animator.channels.computeIfAbsent(keyframe.channel, s -> new TreeMap()).put(keyframe.time, keyframe);
            });
            return animator;
        }

        private void tryFlipSign(Map<String, String> map, String axis) {
            String val = map.get(axis);
            if (val != null) {
                map.put(axis, this.flipSign(val));
            }
        }

        private String flipSign(String val) {
            try {
                double d = Double.parseDouble(val);
                return Double.toString(-d);
            }
            catch (IllegalArgumentException ignored) {
                return "-(" + val + ")";
            }
        }
    }

    static class FaceDeserializer
    implements JsonDeserializer<BlockbenchModel.Face> {
        FaceDeserializer() {
        }

        public BlockbenchModel.Face deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            BlockbenchModel.Face face = new BlockbenchModel.Face();
            face.uv = GSONUtils.get(jsonElement, "uv", e -> (Float[])gson.fromJson(e, Float[].class));
            face.rotation = GSONUtils.get(jsonElement, "rotation", JsonElement::getAsInt);
            face.texture = GSONUtils.get(jsonElement, "texture", JsonElement::getAsInt);
            return face;
        }
    }
}

