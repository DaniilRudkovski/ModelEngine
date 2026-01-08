/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Color
 */
package com.ticxo.modelengine.core.animation.script;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.animation.property.IAnimationProperty;
import com.ticxo.modelengine.api.animation.script.ScriptReader;
import com.ticxo.modelengine.api.generator.blueprint.BlueprintBone;
import com.ticxo.modelengine.api.generator.blueprint.ModelBlueprint;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.bone.BoneBehaviorTypes;
import com.ticxo.modelengine.api.model.bone.ModelBone;
import com.ticxo.modelengine.api.model.bone.type.NameTag;
import com.ticxo.modelengine.api.utils.config.ConfigProperty;
import com.ticxo.modelengine.api.utils.logger.TLogger;
import com.ticxo.modelengine.api.utils.registry.TUnaryRegistry;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import org.bukkit.Color;

public class ModelEngineScriptReader
extends TUnaryRegistry<BiConsumer<ActiveModel, Map<String, String>>>
implements ScriptReader {
    private boolean shouldPrintWarning;

    public ModelEngineScriptReader() {
        ModelEngineAPI.getAPI().getConfigManager().registerReferenceUpdate(this::updateConfig);
        this.register("changeparent", this::changeParent);
        this.register("partvis", this::partVisibility);
        this.register("tint", this::tint);
        this.register("enchant", this::enchant);
        this.register("tag", this::tag);
        this.register("changepart", this::changePart);
        this.register("remap", this::remap);
    }

    public void updateConfig() {
        this.shouldPrintWarning = ConfigProperty.SCRIPT_WARNING.getBoolean();
    }

    @Override
    public void read(IAnimationProperty property, String script) {
        String[] parameters;
        String[] scriptSplit = script.split("\\{", 2);
        String name = scriptSplit[0].toLowerCase(Locale.ENGLISH);
        BiConsumer function = (BiConsumer)this.get(name);
        if (function == null) {
            if (this.shouldPrintWarning) {
                TLogger.warn("Unknown Model Engine script: " + script);
            }
            return;
        }
        if (scriptSplit.length != 2) {
            if (this.shouldPrintWarning) {
                TLogger.warn("Invalid Model Engine script: " + script);
            }
            return;
        }
        ActiveModel model = property.getModel();
        ModelBlueprint blueprint = model.getBlueprint();
        HashMap<String, String> map = new HashMap<String, String>();
        for (String param : parameters = scriptSplit[1].substring(0, scriptSplit[1].length() - 1).split(";")) {
            String[] entry = param.split("=", 2);
            map.put(entry[0].strip().toLowerCase(Locale.ENGLISH), entry.length == 2 ? this.getAnimationPlaceholder(blueprint, entry[1].strip()) : "");
        }
        function.accept(model, map);
    }

    private String getAnimationPlaceholder(ModelBlueprint blueprint, String placeholder) {
        if (!placeholder.startsWith("<") || !placeholder.endsWith(">")) {
            return placeholder;
        }
        String key = placeholder.substring(1, placeholder.length() - 1);
        return blueprint.getAnimationsPlaceholders().getOrDefault(key, placeholder);
    }

    private void changeParent(ActiveModel model, Map<String, String> param) {
        String parentPart = param.get("parent");
        String childPart = param.get("child");
        model.getBone(parentPart).ifPresent(parent -> model.getBone(childPart).ifPresent(child -> child.setParent((ModelBone)parent)));
    }

    private void partVisibility(ActiveModel model, Map<String, String> param) {
        String part = param.get("part");
        boolean visible = Boolean.parseBoolean(param.get("visible"));
        boolean exact = Boolean.parseBoolean(param.get("exact"));
        if (exact) {
            model.getBone(part).ifPresent(bone -> bone.setVisible(visible));
        } else {
            for (Map.Entry<String, ModelBone> bone2 : model.getBones().entrySet()) {
                if (!bone2.getKey().contains(part)) continue;
                bone2.getValue().setVisible(visible);
            }
        }
    }

    private void tint(ActiveModel model, Map<String, String> param) {
        String part = param.get("part");
        String colorString = param.get("color");
        if (colorString.startsWith("#")) {
            colorString = colorString.substring(1);
        }
        Color color = Color.fromRGB((int)Integer.parseInt(colorString, 16));
        boolean exact = Boolean.parseBoolean(param.get("exact"));
        boolean damage = Boolean.parseBoolean(param.get("damage"));
        if (part.isBlank()) {
            if (damage) {
                model.setDamageTint(color);
            } else {
                model.setDefaultTint(color);
            }
            return;
        }
        if (exact) {
            model.getBone(part).ifPresent(bone -> {
                if (damage) {
                    bone.setDamageTint(color);
                } else {
                    bone.setDefaultTint(color);
                }
            });
            return;
        }
        for (Map.Entry<String, ModelBone> entry : model.getBones().entrySet()) {
            if (!entry.getKey().contains(part)) continue;
            if (damage) {
                entry.getValue().setDamageTint(color);
                continue;
            }
            entry.getValue().setDefaultTint(color);
        }
    }

    private void enchant(ActiveModel model, Map<String, String> param) {
        String part = param.get("part");
        boolean enchant = Boolean.parseBoolean(param.get("enchant"));
        boolean exact = Boolean.parseBoolean(param.get("exact"));
        if (part == null || part.isBlank()) {
            for (ModelBone value : model.getBones().values()) {
                value.setEnchanted(enchant);
            }
            return;
        }
        if (exact) {
            model.getBone(part).ifPresent(bone -> bone.setEnchanted(enchant));
            return;
        }
        for (Map.Entry<String, ModelBone> entry : model.getBones().entrySet()) {
            if (!entry.getKey().contains(part)) continue;
            entry.getValue().setEnchanted(enchant);
        }
    }

    private void tag(ActiveModel model, Map<String, String> param) {
        String part = param.get("part");
        String tag = param.get("tag");
        boolean visible = Boolean.parseBoolean(param.getOrDefault("visible", "true"));
        model.getBone(part).flatMap(modelBone -> modelBone.getBoneBehavior(BoneBehaviorTypes.NAMETAG)).ifPresent(nameTag -> {
            ((NameTag)((Object)nameTag)).setString(tag);
            ((NameTag)((Object)nameTag)).setVisible(visible);
        });
    }

    private void changePart(ActiveModel model, Map<String, String> param) {
        String part = param.get("part");
        String nModel = param.get("nmodel");
        String nPart = param.get("npart");
        model.getBone(part).ifPresent(bone -> {
            if (!bone.isRenderer()) {
                return;
            }
            ModelBlueprint blueprint = ModelEngineAPI.getBlueprint(nModel);
            if (blueprint == null) {
                return;
            }
            BlueprintBone blueprintBone = blueprint.getFlatMap().get(nPart);
            if (blueprintBone == null || !blueprintBone.isRenderer()) {
                return;
            }
            bone.setModelScale(blueprintBone.getScale());
            bone.setModel(blueprintBone);
        });
    }

    private void remap(ActiveModel model, Map<String, String> param) {
        String nModel = param.get("model");
        String map = param.get("map");
        ModelBlueprint blueprint = ModelEngineAPI.getBlueprint(nModel);
        if (blueprint == null) {
            return;
        }
        if (map != null) {
            ModelBlueprint mapBlueprint = ModelEngineAPI.getBlueprint(map);
            if (mapBlueprint == null) {
                return;
            }
            for (String bone : mapBlueprint.getFlatMap().keySet()) {
                BlueprintBone blueprintBone = blueprint.getFlatMap().get(bone);
                if (blueprintBone == null || !blueprintBone.isRenderer()) continue;
                model.getBone(bone).ifPresent(replaced -> {
                    if (replaced.isRenderer()) {
                        replaced.setModel(blueprintBone);
                    }
                });
            }
        } else {
            Set<String> bones = blueprint.getFlatMap().size() < model.getBones().size() ? blueprint.getFlatMap().keySet() : model.getBones().keySet();
            for (String bone : bones) {
                BlueprintBone blueprintBone = blueprint.getFlatMap().get(bone);
                if (blueprintBone == null || !blueprintBone.isRenderer()) {
                    TLogger.log(blueprint.getName() + ": " + bone);
                    continue;
                }
                model.getBone(bone).ifPresent(replaced -> {
                    if (replaced.isRenderer()) {
                        replaced.setModelScale(blueprintBone.getScale());
                        replaced.setModel(blueprintBone);
                    }
                });
            }
        }
    }
}

