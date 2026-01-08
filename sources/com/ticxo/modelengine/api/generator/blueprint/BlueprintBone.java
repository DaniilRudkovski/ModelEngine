/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 *  org.joml.Vector3f
 */
package com.ticxo.modelengine.api.generator.blueprint;

import com.ticxo.modelengine.api.generator.BaseItemEnum;
import com.ticxo.modelengine.api.generator.assets.ItemModelData;
import com.ticxo.modelengine.api.generator.blueprint.ModelBlueprint;
import com.ticxo.modelengine.api.model.bone.behavior.BoneBehaviorType;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import lombok.Generated;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;

public class BlueprintBone {
    private final Map<String, BlueprintBone> children = new LinkedHashMap<String, BlueprintBone>();
    private final Map<String, Map<String, Object>> behaviors = new LinkedHashMap<String, Map<String, Object>>();
    private final transient Map<BoneBehaviorType<?>, BoneBehaviorType.CachedProvider<?>> cachedBehaviorProvider = new LinkedHashMap();
    private String name;
    private UUID uuid;
    private ModelBlueprint modelBlueprint;
    private boolean isRenderer;
    private int scale = 1;
    @Deprecated
    private BaseItemEnum baseItem;
    @Deprecated
    private int dataId;
    private final ItemModelData modelData = new ItemModelData();
    private Vector3f localPosition;
    private Vector3f localRotation;
    private Quaternionf localQuaternion = new Quaternionf();
    private Vector3f globalPosition;
    private Vector3f rotatedGlobalPosition;
    private Vector3f globalRotation;
    private Quaternionf globalQuaternion = new Quaternionf();
    private Quaternionf invGlobalQuaternion = new Quaternionf();
    private BlueprintBone parent;
    private Vector3f modelScale = new Vector3f(1.0f);
    private BlueprintBone dupeTarget;
    private boolean renderByDefault = true;

    public void setGlobalQuaternion(Quaternionf globalQuaternion) {
        this.globalQuaternion.set((Quaternionfc)globalQuaternion);
        this.invGlobalQuaternion.set((Quaternionfc)globalQuaternion.invert());
    }

    @Generated
    public Map<String, BlueprintBone> getChildren() {
        return this.children;
    }

    @Generated
    public Map<String, Map<String, Object>> getBehaviors() {
        return this.behaviors;
    }

    @Generated
    public Map<BoneBehaviorType<?>, BoneBehaviorType.CachedProvider<?>> getCachedBehaviorProvider() {
        return this.cachedBehaviorProvider;
    }

    @Generated
    public String getName() {
        return this.name;
    }

    @Generated
    public UUID getUuid() {
        return this.uuid;
    }

    @Generated
    public ModelBlueprint getModelBlueprint() {
        return this.modelBlueprint;
    }

    @Generated
    public boolean isRenderer() {
        return this.isRenderer;
    }

    @Generated
    public int getScale() {
        return this.scale;
    }

    @Deprecated
    @Generated
    public BaseItemEnum getBaseItem() {
        return this.baseItem;
    }

    @Deprecated
    @Generated
    public int getDataId() {
        return this.dataId;
    }

    @Generated
    public ItemModelData getModelData() {
        return this.modelData;
    }

    @Generated
    public Vector3f getLocalPosition() {
        return this.localPosition;
    }

    @Generated
    public Vector3f getLocalRotation() {
        return this.localRotation;
    }

    @Generated
    public Quaternionf getLocalQuaternion() {
        return this.localQuaternion;
    }

    @Generated
    public Vector3f getGlobalPosition() {
        return this.globalPosition;
    }

    @Generated
    public Vector3f getRotatedGlobalPosition() {
        return this.rotatedGlobalPosition;
    }

    @Generated
    public Vector3f getGlobalRotation() {
        return this.globalRotation;
    }

    @Generated
    public Quaternionf getGlobalQuaternion() {
        return this.globalQuaternion;
    }

    @Generated
    public Quaternionf getInvGlobalQuaternion() {
        return this.invGlobalQuaternion;
    }

    @Generated
    public BlueprintBone getParent() {
        return this.parent;
    }

    @Generated
    public Vector3f getModelScale() {
        return this.modelScale;
    }

    @Generated
    public BlueprintBone getDupeTarget() {
        return this.dupeTarget;
    }

    @Generated
    public boolean isRenderByDefault() {
        return this.renderByDefault;
    }

    @Generated
    public void setName(String name) {
        this.name = name;
    }

    @Generated
    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    @Generated
    public void setModelBlueprint(ModelBlueprint modelBlueprint) {
        this.modelBlueprint = modelBlueprint;
    }

    @Generated
    public void setRenderer(boolean isRenderer) {
        this.isRenderer = isRenderer;
    }

    @Generated
    public void setScale(int scale) {
        this.scale = scale;
    }

    @Deprecated
    @Generated
    public void setBaseItem(BaseItemEnum baseItem) {
        this.baseItem = baseItem;
    }

    @Deprecated
    @Generated
    public void setDataId(int dataId) {
        this.dataId = dataId;
    }

    @Generated
    public void setLocalPosition(Vector3f localPosition) {
        this.localPosition = localPosition;
    }

    @Generated
    public void setLocalRotation(Vector3f localRotation) {
        this.localRotation = localRotation;
    }

    @Generated
    public void setLocalQuaternion(Quaternionf localQuaternion) {
        this.localQuaternion = localQuaternion;
    }

    @Generated
    public void setGlobalPosition(Vector3f globalPosition) {
        this.globalPosition = globalPosition;
    }

    @Generated
    public void setRotatedGlobalPosition(Vector3f rotatedGlobalPosition) {
        this.rotatedGlobalPosition = rotatedGlobalPosition;
    }

    @Generated
    public void setGlobalRotation(Vector3f globalRotation) {
        this.globalRotation = globalRotation;
    }

    @Generated
    public void setParent(BlueprintBone parent) {
        this.parent = parent;
    }

    @Generated
    public void setModelScale(Vector3f modelScale) {
        this.modelScale = modelScale;
    }

    @Generated
    public void setDupeTarget(BlueprintBone dupeTarget) {
        this.dupeTarget = dupeTarget;
    }

    @Generated
    public void setRenderByDefault(boolean renderByDefault) {
        this.renderByDefault = renderByDefault;
    }
}

