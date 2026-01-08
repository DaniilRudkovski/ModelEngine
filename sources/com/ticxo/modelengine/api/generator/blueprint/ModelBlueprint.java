/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  lombok.Generated
 */
package com.ticxo.modelengine.api.generator.blueprint;

import com.google.common.collect.ImmutableMap;
import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.animation.BlueprintAnimation;
import com.ticxo.modelengine.api.entity.Hitbox;
import com.ticxo.modelengine.api.error.ErrorCollector;
import com.ticxo.modelengine.api.error.ErrorIncompatibleBone;
import com.ticxo.modelengine.api.error.ErrorUnknownBoneBehavior;
import com.ticxo.modelengine.api.error.WarnIncompatibleBoneBehavior;
import com.ticxo.modelengine.api.error.WarningDuplicateBoneName;
import com.ticxo.modelengine.api.generator.blueprint.BlueprintBone;
import com.ticxo.modelengine.api.model.bone.behavior.BoneBehaviorRegistry;
import com.ticxo.modelengine.api.model.bone.behavior.BoneBehaviorType;
import com.ticxo.modelengine.api.model.bone.render.DefaultRenderType;
import com.ticxo.modelengine.api.model.bone.render.IRenderType;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import lombok.Generated;

public class ModelBlueprint {
    private final Map<String, BlueprintBone> bones = new LinkedHashMap<String, BlueprintBone>();
    private final Map<String, BlueprintAnimation> animations = new LinkedHashMap<String, BlueprintAnimation>();
    private final transient Set<String> animationDescendingPriority = new LinkedHashSet<String>();
    private final Map<String, String> animationsPlaceholders = new LinkedHashMap<String, String>();
    private String name;
    private Hitbox mainHitbox = new Hitbox(0.6, 1.8, 0.6, 1.44);
    private float shadowRadius = 0.0f;
    private transient Map<String, BlueprintBone> flatMap;
    private transient boolean flatViewConstructed;
    private transient boolean descendingAnimationConstructed;
    private transient boolean boneBehaviorCached;

    public void finalizeModel(ErrorCollector collector) {
        this.constructFlatBoneMap(collector);
        this.constructDescendingAnimation(collector);
        this.cacheBoneBehaviors(collector);
    }

    public void constructFlatBoneMap(ErrorCollector collector) {
        if (this.flatViewConstructed) {
            return;
        }
        this.flatViewConstructed = true;
        LinkedHashMap<String, BlueprintBone> tempMap = new LinkedHashMap<String, BlueprintBone>();
        LinkedHashSet<BlueprintBone> bones = new LinkedHashSet<BlueprintBone>(this.bones.values());
        LinkedHashSet<BlueprintBone> buffer = new LinkedHashSet<BlueprintBone>();
        while (!bones.isEmpty()) {
            for (BlueprintBone bone : bones) {
                buffer.addAll(bone.getChildren().values());
                if (tempMap.containsKey(bone.getName())) {
                    new WarningDuplicateBoneName(bone.getName(), bone.getUuid()).log(collector);
                    bone.setName(bone.getUuid().toString());
                }
                tempMap.put(bone.getName(), bone);
            }
            bones.clear();
            bones.addAll(buffer);
            buffer.clear();
        }
        this.flatMap = ImmutableMap.copyOf(tempMap);
    }

    public void constructDescendingAnimation(ErrorCollector collector) {
        if (this.descendingAnimationConstructed) {
            return;
        }
        this.descendingAnimationConstructed = true;
        ArrayList list = new ArrayList();
        this.animations.keySet().forEach(s -> list.add(0, s));
        this.animationDescendingPriority.addAll(list);
    }

    public void cacheBoneBehaviors(ErrorCollector collector) {
        if (this.boneBehaviorCached) {
            return;
        }
        this.boneBehaviorCached = true;
        assert (this.flatMap != null) : "Bone flat view map is null.";
        BoneBehaviorRegistry behaviorRegistry = ModelEngineAPI.getAPI().getBoneBehaviorRegistry();
        for (Map.Entry<String, BlueprintBone> boneEntry : this.flatMap.entrySet()) {
            String name = boneEntry.getKey();
            BlueprintBone bone = boneEntry.getValue();
            for (Map.Entry<String, Map<String, Object>> entry : bone.getBehaviors().entrySet()) {
                String bbId = entry.getKey();
                BoneBehaviorType<?> behaviorType = behaviorRegistry.getById(bbId);
                if (behaviorType == null) {
                    new ErrorUnknownBoneBehavior(name, bbId).log(collector);
                    continue;
                }
                if (!this.canBoneUseBehavior(bone, behaviorType)) {
                    new ErrorIncompatibleBone(bone.isRenderer(), name, bbId).log(collector);
                    continue;
                }
                if (!behaviorType.test(bone.getCachedBehaviorProvider().keySet())) {
                    new WarnIncompatibleBoneBehavior(name, bbId).log(collector);
                }
                behaviorType.assignCachedProvider(bone, entry.getValue());
            }
            for (String id : behaviorRegistry.getIds()) {
                BoneBehaviorType<?> type = behaviorRegistry.getById(id);
                if (type == null) continue;
                type.assignForcedCachedProvider(bone);
            }
        }
    }

    public BlueprintAnimation getAnimationOrRef(String animation) {
        ModelBlueprint blueprint;
        String[] split;
        if (animation.startsWith("ref[") && animation.endsWith("]") && (split = (animation = animation.substring(4, animation.length() - 1)).split("\\.", 2)).length >= 2 && (blueprint = ModelEngineAPI.getBlueprint(split[0])) != null) {
            return blueprint.animations.get(split[1]);
        }
        return this.animations.get(animation);
    }

    private boolean canBoneUseBehavior(BlueprintBone bone, BoneBehaviorType<?> type) {
        IRenderType renderType = type.getRenderType();
        if (renderType == DefaultRenderType.ANY) {
            return true;
        }
        if (renderType == DefaultRenderType.MODEL) {
            return bone.isRenderer();
        }
        return !bone.isRenderer();
    }

    @Generated
    public ModelBlueprint() {
    }

    @Generated
    public Map<String, BlueprintBone> getBones() {
        return this.bones;
    }

    @Generated
    public Map<String, BlueprintAnimation> getAnimations() {
        return this.animations;
    }

    @Generated
    public Set<String> getAnimationDescendingPriority() {
        return this.animationDescendingPriority;
    }

    @Generated
    public Map<String, String> getAnimationsPlaceholders() {
        return this.animationsPlaceholders;
    }

    @Generated
    public String getName() {
        return this.name;
    }

    @Generated
    public Hitbox getMainHitbox() {
        return this.mainHitbox;
    }

    @Generated
    public float getShadowRadius() {
        return this.shadowRadius;
    }

    @Generated
    public Map<String, BlueprintBone> getFlatMap() {
        return this.flatMap;
    }

    @Generated
    public boolean isFlatViewConstructed() {
        return this.flatViewConstructed;
    }

    @Generated
    public boolean isDescendingAnimationConstructed() {
        return this.descendingAnimationConstructed;
    }

    @Generated
    public boolean isBoneBehaviorCached() {
        return this.boneBehaviorCached;
    }

    @Generated
    public void setName(String name) {
        this.name = name;
    }

    @Generated
    public void setMainHitbox(Hitbox mainHitbox) {
        this.mainHitbox = mainHitbox;
    }

    @Generated
    public void setShadowRadius(float shadowRadius) {
        this.shadowRadius = shadowRadius;
    }

    @Generated
    public void setFlatViewConstructed(boolean flatViewConstructed) {
        this.flatViewConstructed = flatViewConstructed;
    }

    @Generated
    public void setDescendingAnimationConstructed(boolean descendingAnimationConstructed) {
        this.descendingAnimationConstructed = descendingAnimationConstructed;
    }

    @Generated
    public void setBoneBehaviorCached(boolean boneBehaviorCached) {
        this.boneBehaviorCached = boneBehaviorCached;
    }
}

