/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.ItemDisplay$ItemDisplayTransform
 *  org.joml.Vector3f
 */
package com.ticxo.modelengine.core.generator.parser.blockbench.json;

import com.ticxo.modelengine.api.entity.Hitbox;
import com.ticxo.modelengine.api.error.ErrorCollector;
import com.ticxo.modelengine.api.error.IError;
import com.ticxo.modelengine.api.generator.blueprint.BlueprintBone;
import com.ticxo.modelengine.api.generator.blueprint.ModelBlueprint;
import com.ticxo.modelengine.api.generator.parser.blockbench.BlockbenchBehaviorParser;
import com.ticxo.modelengine.api.generator.parser.blockbench.BlockbenchModel;
import com.ticxo.modelengine.api.model.bone.BoneBehaviorTypes;
import com.ticxo.modelengine.api.model.bone.type.PlayerLimb;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Predicate;
import org.bukkit.entity.ItemDisplay;
import org.joml.Vector3f;

public class DefaultBehaviorParser
implements BlockbenchBehaviorParser {
    @Override
    public void processModel(ErrorCollector collector, BlockbenchModel model, ModelBlueprint blueprint) {
        this.searchBone(model, "hitbox", bone -> {
            Hitbox mainHitbox = this.readHitbox(collector, model, (BlockbenchModel.Group)bone);
            if (mainHitbox == null) {
                return false;
            }
            blueprint.setMainHitbox(mainHitbox);
            return true;
        }, () -> IError.NO_HITBOX.log(collector));
        this.searchBone(model, "shadow", bone -> {
            float radius = this.readShadow(model, (BlockbenchModel.Group)bone);
            if (radius < 1.0E-5f) {
                return false;
            }
            blueprint.setShadowRadius(radius);
            return true;
        }, null);
    }

    @Override
    public void processBone(ErrorCollector collector, BlockbenchModel model, BlockbenchModel.Group group, BlueprintBone bone) {
        String name = this.readOptions(model, group, bone);
        bone.setName(name);
    }

    private void searchBone(BlockbenchModel model, String name, Predicate<BlockbenchModel.Group> consumer, Runnable missing) {
        BlockbenchModel.Group group = model.getGroup(name);
        if (group != null && consumer.test(group)) {
            model.removeGroup(group);
        } else if (missing != null) {
            missing.run();
        }
    }

    private Hitbox readHitbox(ErrorCollector collector, BlockbenchModel model, BlockbenchModel.Group hitbox) {
        Float[] boneOrigin = hitbox.getOrigin();
        for (UUID uuid : hitbox.getElement()) {
            BlockbenchModel.Element element = model.getElements().get(uuid);
            if (!(element instanceof BlockbenchModel.Cube)) continue;
            BlockbenchModel.Cube cube = (BlockbenchModel.Cube)element;
            Float[] cubeOrigin = cube.getOrigin();
            float eyeHeight = (boneOrigin[1].floatValue() <= 0.0f ? cubeOrigin[1] : boneOrigin[1]).floatValue();
            if (eyeHeight <= 0.0f) {
                IError.BAD_EYE_HEIGHT.log(collector);
            }
            return new Hitbox(cube.width() * 0.0625f, cube.height() * 0.0625f, cube.depth() * 0.0625f, eyeHeight * 0.0625f);
        }
        return null;
    }

    private float readShadow(BlockbenchModel model, BlockbenchModel.Group shadow) {
        for (UUID uuid : shadow.getElement()) {
            BlockbenchModel.Element element = model.getElements().get(uuid);
            if (!(element instanceof BlockbenchModel.Cube)) continue;
            BlockbenchModel.Cube cube = (BlockbenchModel.Cube)element;
            return Math.max(cube.width(), cube.depth()) * 0.03125f;
        }
        return -1.0f;
    }

    private String readOptions(BlockbenchModel model, BlockbenchModel.Group group, BlueprintBone bone) {
        String name = bone.getName();
        Map<String, Map<String, Object>> options = bone.getBehaviors();
        if (name.startsWith("h_")) {
            name = name.substring(2);
            options.put(BoneBehaviorTypes.HEAD.getId(), new HashMap());
        } else if (name.startsWith("hi_")) {
            name = name.substring(3);
            options.put(BoneBehaviorTypes.HEAD.getId(), (Map<String, Object>)new HashMap<String, Object>(){
                {
                    this.put("inherited", true);
                }
            });
        }
        if (name.equals("mount")) {
            options.put(BoneBehaviorTypes.MOUNT.getId(), (Map<String, Object>)new HashMap<String, Object>(){
                {
                    this.put("driver", true);
                }
            });
            return name;
        }
        boolean isOBB = name.startsWith("ob_");
        if (isOBB || name.startsWith("b_")) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            if (isOBB) {
                map.put("obb", true);
            }
            for (UUID uuid : group.getElement()) {
                BlockbenchModel.Element element = model.getElements().get(uuid);
                if (!(element instanceof BlockbenchModel.Cube)) continue;
                BlockbenchModel.Cube cube = (BlockbenchModel.Cube)element;
                map.put("dimension", new Hitbox((double)cube.width() * 0.0625, (double)cube.height() * 0.0625, (double)cube.depth() * 0.0625, 0.0));
                Float[] from = cube.getFrom();
                Float[] to = cube.getTo();
                map.put("origin", new Vector3f((from[0].floatValue() + to[0].floatValue()) * -0.03125f, (from[1].floatValue() + to[1].floatValue()) * 0.03125f, (from[2].floatValue() + to[2].floatValue()) * -0.03125f));
                Float[] rotation = cube.getRotation();
                map.put("rotation", rotation != null ? new Vector3f(-rotation[0].floatValue(), rotation[1].floatValue(), -rotation[2].floatValue()).mul((float)Math.PI / 180) : new Vector3f());
                Float[] origin = cube.getOrigin();
                map.put("cube_origin", new Vector3f(origin[0].floatValue() * -0.0625f, origin[1].floatValue() * 0.0625f, origin[2].floatValue() * -0.0625f));
                break;
            }
            options.put(BoneBehaviorTypes.SUB_HITBOX.getId(), map);
            return name;
        }
        String[] split = name.split("_(?![^\\[]*\\])");
        if (!group.getElement().isEmpty()) {
            block63: for (int i = 0; i < split.length; ++i) {
                String tag = split[i];
                String[] paramSplit = tag.split("\\[|;|\\]");
                tag = paramSplit[0];
                final HashMap<String, String> paramMap = new HashMap<String, String>();
                for (int p = 1; p < paramSplit.length; ++p) {
                    String[] pair = paramSplit[p].split("=", 2);
                    if (pair.length != 2) continue;
                    paramMap.put(pair[0], pair[1]);
                }
                switch (tag) {
                    case "phead": {
                        options.put(BoneBehaviorTypes.PLAYER_LIMB.getId(), (Map<String, Object>)new HashMap<String, Object>(){
                            {
                                this.put("limb", PlayerLimb.Limb.HEAD);
                            }
                        });
                        continue block63;
                    }
                    case "prarm": {
                        options.put(BoneBehaviorTypes.PLAYER_LIMB.getId(), (Map<String, Object>)new HashMap<String, Object>(){
                            {
                                this.put("limb", PlayerLimb.Limb.RIGHT_ARM);
                            }
                        });
                        continue block63;
                    }
                    case "plarm": {
                        options.put(BoneBehaviorTypes.PLAYER_LIMB.getId(), (Map<String, Object>)new HashMap<String, Object>(){
                            {
                                this.put("limb", PlayerLimb.Limb.LEFT_ARM);
                            }
                        });
                        continue block63;
                    }
                    case "pbody": {
                        options.put(BoneBehaviorTypes.PLAYER_LIMB.getId(), (Map<String, Object>)new HashMap<String, Object>(){
                            {
                                this.put("limb", PlayerLimb.Limb.BODY);
                            }
                        });
                        continue block63;
                    }
                    case "prleg": {
                        options.put(BoneBehaviorTypes.PLAYER_LIMB.getId(), (Map<String, Object>)new HashMap<String, Object>(){
                            {
                                this.put("limb", PlayerLimb.Limb.RIGHT_LEG);
                            }
                        });
                        continue block63;
                    }
                    case "plleg": {
                        options.put(BoneBehaviorTypes.PLAYER_LIMB.getId(), (Map<String, Object>)new HashMap<String, Object>(){
                            {
                                this.put("limb", PlayerLimb.Limb.LEFT_LEG);
                            }
                        });
                        continue block63;
                    }
                    case "limb": {
                        options.put(BoneBehaviorTypes.USER_LIMB.getId(), (Map<String, Object>)new HashMap<String, Object>(){
                            {
                                this.put("type", paramMap.get("type"));
                            }
                        });
                        continue block63;
                    }
                    case "tl": {
                        options.put(BoneBehaviorTypes.TAIL.getId(), (Map<String, Object>)new HashMap<String, Object>(){
                            {
                                this.put("back_pivot", true);
                            }
                        });
                        continue block63;
                    }
                    case "tlf": {
                        options.put(BoneBehaviorTypes.TAIL.getId(), (Map<String, Object>)new HashMap<String, Object>(){
                            {
                                this.put("back_pivot", false);
                            }
                        });
                        continue block63;
                    }
                    default: {
                        StringBuilder builder = new StringBuilder(tag);
                        for (int j = i + 1; j < split.length; ++j) {
                            builder.append("_").append(split[j]);
                        }
                        return builder.toString();
                    }
                }
            }
            return name;
        }
        block66: for (int i = 0; i < split.length; ++i) {
            String tag = split[i];
            String[] paramSplit = tag.split("\\[|;|\\]");
            tag = paramSplit[0];
            final HashMap<String, String> paramMap = new HashMap<String, String>();
            for (int p = 1; p < paramSplit.length; ++p) {
                String[] pair = paramSplit[p].split("=", 2);
                if (pair.length != 2) continue;
                paramMap.put(pair[0], pair[1]);
            }
            switch (tag) {
                case "g": {
                    options.put(BoneBehaviorTypes.GHOST.getId(), new HashMap());
                    continue block66;
                }
                case "tag": {
                    options.put(BoneBehaviorTypes.NAMETAG.getId(), new HashMap());
                    continue block66;
                }
                case "p": {
                    options.put(BoneBehaviorTypes.MOUNT.getId(), (Map<String, Object>)new HashMap<String, Object>(){
                        {
                            this.put("driver", false);
                        }
                    });
                    continue block66;
                }
                case "ir": {
                    options.put(BoneBehaviorTypes.ITEM.getId(), (Map<String, Object>)new HashMap<String, Object>(){
                        {
                            this.put("display", ItemDisplay.ItemDisplayTransform.THIRDPERSON_RIGHTHAND);
                        }
                    });
                    continue block66;
                }
                case "il": {
                    options.put(BoneBehaviorTypes.ITEM.getId(), (Map<String, Object>)new HashMap<String, Object>(){
                        {
                            this.put("display", ItemDisplay.ItemDisplayTransform.THIRDPERSON_LEFTHAND);
                        }
                    });
                    continue block66;
                }
                case "ih": {
                    options.put(BoneBehaviorTypes.ITEM.getId(), (Map<String, Object>)new HashMap<String, Object>(){
                        {
                            this.put("display", ItemDisplay.ItemDisplayTransform.HEAD);
                        }
                    });
                    continue block66;
                }
                case "l": {
                    options.put(BoneBehaviorTypes.LEASH.getId(), new HashMap());
                    continue block66;
                }
                case "phead": {
                    options.put(BoneBehaviorTypes.PLAYER_LIMB.getId(), (Map<String, Object>)new HashMap<String, Object>(){
                        {
                            this.put("limb", PlayerLimb.Limb.HEAD);
                        }
                    });
                    continue block66;
                }
                case "prarm": {
                    options.put(BoneBehaviorTypes.PLAYER_LIMB.getId(), (Map<String, Object>)new HashMap<String, Object>(){
                        {
                            this.put("limb", PlayerLimb.Limb.RIGHT_ARM);
                        }
                    });
                    continue block66;
                }
                case "plarm": {
                    options.put(BoneBehaviorTypes.PLAYER_LIMB.getId(), (Map<String, Object>)new HashMap<String, Object>(){
                        {
                            this.put("limb", PlayerLimb.Limb.LEFT_ARM);
                        }
                    });
                    continue block66;
                }
                case "pbody": {
                    options.put(BoneBehaviorTypes.PLAYER_LIMB.getId(), (Map<String, Object>)new HashMap<String, Object>(){
                        {
                            this.put("limb", PlayerLimb.Limb.BODY);
                        }
                    });
                    continue block66;
                }
                case "prleg": {
                    options.put(BoneBehaviorTypes.PLAYER_LIMB.getId(), (Map<String, Object>)new HashMap<String, Object>(){
                        {
                            this.put("limb", PlayerLimb.Limb.RIGHT_LEG);
                        }
                    });
                    continue block66;
                }
                case "plleg": {
                    options.put(BoneBehaviorTypes.PLAYER_LIMB.getId(), (Map<String, Object>)new HashMap<String, Object>(){
                        {
                            this.put("limb", PlayerLimb.Limb.LEFT_LEG);
                        }
                    });
                    continue block66;
                }
                case "limb": {
                    options.put(BoneBehaviorTypes.USER_LIMB.getId(), (Map<String, Object>)new HashMap<String, Object>(){
                        {
                            this.put("type", paramMap.get("type"));
                        }
                    });
                    continue block66;
                }
                case "seg": {
                    options.put(BoneBehaviorTypes.SEGMENT.getId(), (Map<String, Object>)new HashMap<String, Object>(){
                        {
                            this.put("back_pivot", true);
                        }
                    });
                    continue block66;
                }
                case "segf": {
                    options.put(BoneBehaviorTypes.SEGMENT.getId(), (Map<String, Object>)new HashMap<String, Object>(){
                        {
                            this.put("back_pivot", false);
                        }
                    });
                    continue block66;
                }
                case "tl": {
                    options.put(BoneBehaviorTypes.TAIL.getId(), (Map<String, Object>)new HashMap<String, Object>(){
                        {
                            this.put("back_pivot", true);
                        }
                    });
                    continue block66;
                }
                case "tlf": {
                    options.put(BoneBehaviorTypes.TAIL.getId(), (Map<String, Object>)new HashMap<String, Object>(){
                        {
                            this.put("back_pivot", false);
                        }
                    });
                    continue block66;
                }
                default: {
                    StringBuilder builder = new StringBuilder(tag);
                    for (int j = i + 1; j < split.length; ++j) {
                        builder.append("_").append(split[j]);
                    }
                    return builder.toString();
                }
            }
        }
        return name;
    }
}

