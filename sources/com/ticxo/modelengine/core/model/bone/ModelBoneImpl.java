/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Maps
 *  lombok.Generated
 *  org.apache.commons.lang3.tuple.Triple
 *  org.bukkit.Color
 *  org.bukkit.Location
 *  org.bukkit.enchantments.Enchantment
 *  org.bukkit.entity.Display$Billboard
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.util.Transformation
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 */
package com.ticxo.modelengine.core.model.bone;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.animation.rootmotion.RootMotion;
import com.ticxo.modelengine.api.entity.data.IEntityData;
import com.ticxo.modelengine.api.generator.blueprint.BlueprintBone;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.bone.BoneItems;
import com.ticxo.modelengine.api.model.bone.ManualAnimator;
import com.ticxo.modelengine.api.model.bone.ModelBone;
import com.ticxo.modelengine.api.model.bone.behavior.BoneBehavior;
import com.ticxo.modelengine.api.model.bone.behavior.BoneBehaviorType;
import com.ticxo.modelengine.api.model.bone.behavior.ProceduralType;
import com.ticxo.modelengine.api.utils.OffsetMode;
import com.ticxo.modelengine.api.utils.StepFlag;
import com.ticxo.modelengine.api.utils.data.io.SavedData;
import com.ticxo.modelengine.api.utils.data.tracker.DataTracker;
import com.ticxo.modelengine.api.utils.data.tracker.MapDataTracker;
import com.ticxo.modelengine.api.utils.data.tracker.UpdateDataTracker;
import com.ticxo.modelengine.api.utils.math.SafeTransform;
import com.ticxo.modelengine.api.utils.math.TMath;
import com.ticxo.modelengine.core.misc.Cardinal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import lombok.Generated;
import org.apache.commons.lang3.tuple.Triple;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Display;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Transformation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class ModelBoneImpl
implements ModelBone {
    @NotNull
    private final ActiveModel activeModel;
    @NotNull
    private final BlueprintBone blueprintBone;
    private final Map<String, ModelBone> children = Maps.newConcurrentMap();
    private final Map<BoneBehaviorType<?>, BoneBehavior> boneBehaviors = new LinkedHashMap();
    private final BoneItems itemModels = new BoneItems();
    private final DataTracker<Vector3f> modelScale = new UpdateDataTracker<Vector3f>(new Vector3f(), Vector3f::set);
    private final Set<ProceduralType> proceduralTypes = new HashSet<ProceduralType>();
    private ManualAnimator manualAnimator;
    private final SafeTransform localTransform = new SafeTransform();
    private final SafeTransform globalTransform = new SafeTransform();
    private final Matrix4f transformMatrix = new Matrix4f();
    private final Map<String, StepFlag> stepFlags = new HashMap<String, StepFlag>();
    private final DataTracker<Map<String, StepFlag>> trackedStepFlags = new MapDataTracker(new HashMap());
    private final Set<String> tickDependencies = new HashSet<String>();
    private String customId = null;
    @Nullable
    private ModelBone parent;
    @Nullable
    private ModelBone pivot;
    private boolean pivotOverride;
    private Location pivotLocation;
    private boolean isRenderer;
    private float yaw;
    private boolean hasGlobalRotation;
    private Color lastColor;
    private Color defaultTint;
    private Color damageTint;
    private boolean enchanted;
    private Boolean glowing;
    private Integer glowColor;
    private int blockLight = -1;
    private int skyLight = -1;
    private Display.Billboard billboard;
    private boolean visible = true;
    private boolean markedDestroy;
    private boolean isBoneOnGround;

    public ModelBoneImpl(@NotNull ActiveModel activeModel, @NotNull BlueprintBone blueprintBone) {
        this.activeModel = activeModel;
        this.blueprintBone = blueprintBone;
        this.updateYaw();
        this.globalTransform.setPosition(blueprintBone.getRotatedGlobalPosition());
        this.globalTransform.setLeftQuaternion(blueprintBone.getGlobalQuaternion());
        this.modelScale.set(new Vector3f((Vector3fc)blueprintBone.getModelScale()).mul((float)blueprintBone.getScale()));
        this.globalTransform.setScale(this.modelScale.get());
        this.isRenderer = blueprintBone.isRenderer();
        if (this.isRenderer) {
            this.setModel(blueprintBone);
        } else {
            BlueprintBone dupeTarget = blueprintBone.getDupeTarget();
            if (dupeTarget != null && dupeTarget.isRenderer()) {
                this.setModel(dupeTarget);
                this.isRenderer = true;
            }
        }
        this.setVisible(blueprintBone.isRenderByDefault());
        this.globalTransform.recordLast();
        this.globalTransform.recordSafe();
    }

    @Override
    public String getUniqueBoneId() {
        return this.getCustomId() == null ? this.getBoneId() : this.getCustomId();
    }

    @Override
    public String getBoneId() {
        return this.blueprintBone.getName();
    }

    @Override
    public void setParent(@Nullable ModelBone bone) {
        if (this.parent != null) {
            this.parent.getChildren().remove(this.getUniqueBoneId());
        }
        this.parent = bone;
        if (this.parent != null) {
            this.parent.getChildren().put(this.getUniqueBoneId(), this);
        }
        this.forBehaviors(boneBehavior -> boneBehavior.onParentSwap(this.parent));
    }

    @Override
    public Location calculatePivotLocation() {
        Location loc;
        if (this.pivot != null && (loc = this.pivot.getPivotLocation()) != null) {
            return loc.clone();
        }
        return this.getBaseLocation();
    }

    @Override
    public Location getBaseLocation() {
        return this.activeModel.getModeledEntity().getBase().getLocation().clone();
    }

    @Override
    public void addTickDependencies(String ... boneIds) {
        this.tickDependencies.addAll(Arrays.asList(boneIds));
    }

    @Override
    public void removeTickDependencies(String ... boneIds) {
        for (String id : boneIds) {
            this.tickDependencies.remove(id);
        }
    }

    @Override
    public void clearTickDependencies() {
        this.tickDependencies.clear();
    }

    @Override
    public void tick() {
        this.stepFlags.clear();
        this.trackedStepFlags.clearDirty();
        this.localTransform.recordLast();
        this.globalTransform.recordLast();
        this.tryTintModel();
        this.localTransform.identity();
        this.localTransform.setPosition(this.blueprintBone.getLocalPosition());
        this.localTransform.setLeftEuler(this.blueprintBone.getLocalRotation());
        this.forBehaviors(BoneBehavior::preAnimation);
        if (this.manualAnimator == null) {
            if (this.proceduralTypes.contains((Object)ProceduralType.ANIMATION)) {
                this.forBehaviors(BoneBehavior::onAnimation);
            } else {
                this.activeModel.getAnimationHandler().updateBone(this);
            }
        } else {
            this.manualAnimator.animate(this);
        }
        this.forBehaviors(BoneBehavior::postAnimation);
        this.localTransform.recordSafe();
        this.updateYaw();
        if (this.proceduralTypes.contains((Object)ProceduralType.TRANSFORM)) {
            this.forBehaviors(BoneBehavior::onGlobalCalculation);
        } else {
            this.calculateGlobalTransform();
        }
        this.forBehaviors(BoneBehavior::preTransformDecompose);
        this.decomposeTransform();
        this.forBehaviors(BoneBehavior::postTransformDecompose);
        this.recomposeTransform();
        this.forBehaviors(BoneBehavior::postTransformRecompose);
        this.globalTransform.mutateScale(scale -> scale.mul((Vector3fc)this.modelScale.get()).mul(this.activeModel.getScale()));
        this.globalTransform.mutatePosition(position -> position.mul(this.activeModel.getScale()));
        this.forBehaviors(BoneBehavior::onFinalize);
        if (this.isRootBone()) {
            Vector3f pos = this.localTransform.getSafePosition();
            if (!TMath.isSimilar(pos.lengthSquared(), 0.0f)) {
                this.queueVelocity(new RootMotion(pos, this.getActiveModel().getModeledEntity().getYHeadRot(), this.isBoneOnGround));
            }
            this.globalTransform.mutatePosition(Vector3f::zero);
        }
        this.trackedStepFlags.set(this.stepFlags);
        this.globalTransform.recordSafe();
    }

    @Override
    public void lazyTick() {
        this.updateYaw();
        if (!this.children.isEmpty()) {
            this.children.values().forEach(ModelBone::lazyTick);
        }
    }

    private void updateYaw() {
        this.yaw = this.pivot == null ? this.activeModel.getModeledEntity().getYBodyRot() : this.pivot.getYaw();
        this.forBehaviors(boneBehavior -> {
            this.yaw = boneBehavior.onUpdateYaw(this.yaw);
        });
        this.yaw = Float.isNaN(this.yaw) ? 0.0f : TMath.wrapDegree(this.yaw);
    }

    @Override
    public void destroy() {
        this.markedDestroy = true;
        this.boneBehaviors.values().forEach(BoneBehavior::onRemove);
        this.children.values().forEach(ModelBone::destroy);
        this.children.clear();
        this.getData().markBoneGlowing(this, false);
        if (this.parent != null && !this.parent.isMarkedDestroy()) {
            this.parent.getChildren().remove(this.getUniqueBoneId());
        }
        this.activeModel.removeBone(this.getUniqueBoneId());
    }

    @Override
    public void setModelScale(int scale) {
        this.modelScale.set(new Vector3f((float)scale));
    }

    @Override
    public void calculateGlobalTransform() {
        this.transformMatrix.identity();
        if (this.parent != null) {
            this.transformMatrix.set((Matrix4fc)this.parent.getTransformMatrix());
        }
        this.forBehaviors(BoneBehavior::preGlobalCalculation);
        this.forBehaviors(BoneBehavior::preGlobalTranslate);
        this.transformMatrix.translate((Vector3fc)this.localTransform.getPosition());
        this.forBehaviors(BoneBehavior::postGlobalTranslate);
        this.forBehaviors(BoneBehavior::preGlobalRotate);
        if (this.parent != null && this.hasGlobalRotation) {
            this.transformMatrix.rotate((Quaternionfc)this.parent.getGlobalTransform().getLeftQuaternion().invert(new Quaternionf()));
        }
        this.transformMatrix.rotate((Quaternionfc)this.localTransform.getLeftQuaternion());
        this.forBehaviors(BoneBehavior::postGlobalRotate);
        this.forBehaviors(BoneBehavior::preGlobalScale);
        TMath.clampedScale(this.transformMatrix, this.localTransform.getScale());
        this.forBehaviors(BoneBehavior::postGlobalScale);
        this.forBehaviors(BoneBehavior::preGlobalShear);
        this.transformMatrix.rotate((Quaternionfc)this.localTransform.getRightQuaternion());
        this.forBehaviors(BoneBehavior::postGlobalShear);
        this.forBehaviors(BoneBehavior::postGlobalCalculation);
    }

    @Override
    public boolean hasGlobalRotation() {
        return this.hasGlobalRotation;
    }

    private void decomposeTransform() {
        Transformation t = ModelEngineAPI.getNMSHandler().decompose(this.transformMatrix);
        Triple<Quaternionf, Vector3f, Quaternionf> correct = Triple.of((Object)t.getLeftRotation(), (Object)t.getScale(), (Object)t.getRightRotation());
        Vector3f delta = this.globalTransform.getRightQuaternion().difference(t.getRightRotation()).getEulerAnglesXYZ(new Vector3f());
        if (Math.abs(delta.x) >= 0.7853982f || Math.abs(delta.y) >= 0.7853982f || Math.abs(delta.z) >= 0.7853982f) {
            float len = Float.MAX_VALUE;
            for (Cardinal cardinal : Cardinal.values()) {
                Triple<Quaternionf, Vector3f, Quaternionf> wrapped = cardinal.warpTransform(t.getLeftRotation(), t.getScale(), t.getRightRotation());
                delta = this.globalTransform.getRightQuaternion().difference((Quaternionf)wrapped.getRight()).getEulerAnglesXYZ(new Vector3f());
                float l = delta.lengthSquared();
                if (!(len > l)) continue;
                correct = wrapped;
                len = l;
            }
        }
        this.globalTransform.setPosition(t.getTranslation());
        this.globalTransform.setLeftQuaternion((Quaternionf)correct.getLeft());
        this.globalTransform.setScale((Vector3f)correct.getMiddle());
        this.globalTransform.setRightQuaternion((Quaternionf)correct.getRight());
    }

    private void recomposeTransform() {
        this.transformMatrix.identity().translate((Vector3fc)this.globalTransform.getPosition()).rotate((Quaternionfc)this.globalTransform.getLeftQuaternion()).scale((Vector3fc)this.globalTransform.getScale()).rotate((Quaternionfc)this.globalTransform.getRightQuaternion());
    }

    @Override
    public ItemStack getModel() {
        return this.itemModels.getFirst();
    }

    @Override
    public BoneItems getModels() {
        return this.itemModels;
    }

    @Override
    public void setModel(BlueprintBone bone) {
        this.itemModels.update(bone, this.getColor());
    }

    @Override
    public void setModel(ItemStack stack) {
        if (this.itemModels.isEqual(Set.of(stack))) {
            return;
        }
        this.itemModels.clear();
        this.itemModels.add(stack);
    }

    @Override
    public void clearModel() {
        this.itemModels.clear();
    }

    @Override
    public DataTracker<BoneItems> getModelTracker() {
        return this.itemModels.getTracker();
    }

    @Override
    public Color getDefaultTint() {
        return this.defaultTint == null ? this.activeModel.getDefaultTint() : this.defaultTint;
    }

    @Override
    public Color getDamageTint() {
        return this.damageTint == null ? this.activeModel.getDamageTint() : this.damageTint;
    }

    @Override
    public void setEnchanted(boolean flag) {
        if (this.isEnchanted() == flag) {
            return;
        }
        this.enchanted = flag;
        this.itemModels.forEach(itemStack -> {
            if (flag) {
                itemStack.addUnsafeEnchantment(Enchantment.VANISHING_CURSE, 1);
            } else {
                itemStack.removeEnchantment(Enchantment.VANISHING_CURSE);
            }
        }, true);
    }

    @Override
    public boolean isGlowing() {
        return this.glowing == null ? this.activeModel.isGlowing() : this.glowing.booleanValue();
    }

    @Override
    public void setGlowing(@Nullable Boolean flag) {
        this.glowing = flag;
        this.getData().markBoneGlowing(this, this.glowing != null && this.glowing != false);
    }

    @Override
    public int getGlowColor() {
        return this.glowColor == null ? this.activeModel.getGlowColor() : this.glowColor.intValue();
    }

    @Override
    public int getBlockLight() {
        return this.blockLight == -1 ? this.activeModel.getBlockLight() : this.blockLight;
    }

    @Override
    public int getSkyLight() {
        return this.skyLight == -1 ? this.activeModel.getSkyLight() : this.skyLight;
    }

    @Override
    public Display.Billboard getBillboard() {
        return this.billboard == null ? this.activeModel.getBillboard() : this.billboard;
    }

    @Override
    public boolean isEffectivelyInvisible() {
        return !this.isRenderer || !this.activeModel.isInvisUpdate() && (this.modelScale.get().lengthSquared() < 1.0E-5f || this.globalTransform.getScale().lengthSquared() < 1.0E-5f);
    }

    @Override
    public boolean shouldStep() {
        return this.trackedStepFlags.isDirty();
    }

    @Override
    public void markStep(StepFlag flag, String animation, float key) {
        this.stepFlags.put(animation + ":" + key, flag);
    }

    @Override
    public boolean pollModelScaleChanged() {
        if (this.modelScale.isDirty()) {
            this.modelScale.clearDirty();
            return true;
        }
        return false;
    }

    @Override
    public boolean isRootBone() {
        return this.activeModel.getModeledEntity().getRootMotionHandler().getRootBone() == this;
    }

    @Override
    public Location getLocationUnsafe() {
        return this.getLocation(this.globalTransform.getPosition());
    }

    @Override
    public Location getLocation() {
        return this.getLocation(this.globalTransform.getSafePosition());
    }

    private Location getLocation(Vector3f bonePosition) {
        float angle = -this.getYaw() * ((float)Math.PI / 180);
        bonePosition = bonePosition.rotateY(angle, new Vector3f());
        Location pivot = this.isPivotOverride() ? this.getPivotLocation() : this.calculatePivotLocation();
        return pivot.clone().add((double)bonePosition.x, (double)bonePosition.y, (double)bonePosition.z);
    }

    @Override
    public Location getLocation(OffsetMode mode, Vector3f offset, boolean scale, boolean safe) {
        Quaternionf boneRotation;
        float angle = -this.getYaw() * ((float)Math.PI / 180);
        Vector3f bonePosition = safe ? this.globalTransform.getSafePosition() : this.globalTransform.getPosition();
        Quaternionf quaternionf = boneRotation = safe ? this.globalTransform.getSafeLeftQuaternion() : this.globalTransform.getLeftQuaternion();
        if (scale) {
            offset.mul(this.activeModel.getScale());
        }
        bonePosition = switch (mode) {
            default -> throw new IncompatibleClassChangeError();
            case OffsetMode.LOCAL -> bonePosition.add((Vector3fc)offset.mul(-1.0f, 1.0f, -1.0f).rotate((Quaternionfc)boneRotation)).rotateY(angle);
            case OffsetMode.MODEL -> bonePosition.add((Vector3fc)offset.mul(-1.0f, 1.0f, -1.0f)).rotateY(angle);
            case OffsetMode.GLOBAL -> bonePosition.rotateY(angle).add((Vector3fc)offset);
        };
        Location pivot = this.isPivotOverride() ? this.getPivotLocation() : this.calculatePivotLocation();
        return pivot.clone().add((double)bonePosition.x, (double)bonePosition.y, (double)bonePosition.z);
    }

    @Override
    public void addBoneBehavior(BoneBehavior boneBehavior) {
        this.boneBehaviors.put(boneBehavior.getType(), boneBehavior);
        boneBehavior.onApply();
        this.proceduralTypes.addAll(boneBehavior.getType().getProceduralTypes());
        this.pivotOverride |= boneBehavior.getType().isPivot();
    }

    @Override
    public boolean hasBoneBehavior(BoneBehaviorType<?> type) {
        return this.boneBehaviors.containsKey(type);
    }

    @Override
    public <T extends BoneBehavior> Optional<T> getBoneBehavior(BoneBehaviorType<T> type) {
        BoneBehavior behavior = this.boneBehaviors.get(type);
        return Optional.ofNullable(behavior);
    }

    @Override
    public <T extends BoneBehavior> Optional<T> removeBoneBehavior(BoneBehaviorType<T> type) {
        BoneBehavior behavior = this.boneBehaviors.remove(type);
        if (behavior != null) {
            behavior.onRemove();
        }
        return Optional.ofNullable(behavior);
    }

    @Override
    public Map<BoneBehaviorType<?>, BoneBehavior> getImmutableBoneBehaviors() {
        return ImmutableMap.copyOf(this.boneBehaviors);
    }

    @Override
    public void forBehaviors(Consumer<BoneBehavior> consumer) {
        this.boneBehaviors.values().iterator().forEachRemaining(consumer);
    }

    private void tryTintModel() {
        Color color = this.getColor();
        if (color == this.lastColor) {
            return;
        }
        this.lastColor = color;
        AtomicBoolean b = new AtomicBoolean();
        this.itemModels.forEach(itemStack -> {
            if (ModelEngineAPI.getNMSHandler().colorStack((ItemStack)itemStack, color)) {
                b.set(true);
            }
        }, b::get);
    }

    private Color getColor() {
        return this.activeModel.isMarkedHurt() ? this.getDamageTint() : this.getDefaultTint();
    }

    @Override
    public void save(SavedData data) {
        if (this.defaultTint != null) {
            data.putInt("default_tint", this.defaultTint.asRGB());
        }
        if (this.damageTint != null) {
            data.putInt("damage_tint", this.damageTint.asRGB());
        }
        if (this.blueprintBone.isRenderByDefault() != this.isVisible()) {
            data.putBoolean("visible", this.isVisible());
        }
        if (this.isEnchanted()) {
            data.putBoolean("enchant", true);
        }
        if (this.glowing != null) {
            data.putBoolean("glowing", this.glowing);
        }
        if (this.glowColor != null) {
            data.putInt("glow_color", this.glowColor);
        }
        if (this.blockLight > 0) {
            data.putInt("block_light", this.blockLight);
        }
        if (this.skyLight > 0) {
            data.putInt("sky_light", this.skyLight);
        }
        if (!this.tickDependencies.isEmpty()) {
            data.putList("tick_dependencies", this.tickDependencies);
        }
        this.forBehaviors(behavior -> behavior.save().ifPresent(data1 -> data.putData(behavior.getType().getId(), (SavedData)data1)));
    }

    @Override
    public void load(SavedData data) {
        Boolean visible;
        Integer damageTint;
        Integer defaultTint = data.getInt("default_tint");
        if (defaultTint != null) {
            this.setDefaultTint(Color.fromRGB((int)defaultTint));
        }
        if ((damageTint = data.getInt("damage_tint")) != null) {
            this.setDamageTint(Color.fromRGB((int)damageTint));
        }
        if ((visible = data.getBoolean("visible")) != null) {
            this.setVisible(visible);
        }
        if (data.getBoolean("enchant", false).booleanValue()) {
            this.setEnchanted(true);
        }
        this.setGlowing(data.getBoolean("glowing"));
        this.setGlowColor(data.getInt("glow_color"));
        this.setBlockLight(data.getInt("block_light", -1));
        this.setSkyLight(data.getInt("sky_light", -1));
        List<String> tickDep = data.getList("tick_dependencies", String.class);
        if (tickDep != null) {
            this.tickDependencies.addAll(tickDep);
        }
        this.forBehaviors(behavior -> data.getData(behavior.getType().getId()).ifPresent(behavior::load));
    }

    private IEntityData getData() {
        return this.activeModel.getModeledEntity().getBase().getData();
    }

    private void queueVelocity(RootMotion rootMotion) {
        this.activeModel.getModeledEntity().getRootMotionHandler().queueVelocity(rootMotion);
    }

    @Override
    @NotNull
    @Generated
    public ActiveModel getActiveModel() {
        return this.activeModel;
    }

    @Override
    @NotNull
    @Generated
    public BlueprintBone getBlueprintBone() {
        return this.blueprintBone;
    }

    @Override
    @Generated
    public Map<String, ModelBone> getChildren() {
        return this.children;
    }

    @Generated
    public BoneItems getItemModels() {
        return this.itemModels;
    }

    @Generated
    public DataTracker<Vector3f> getModelScale() {
        return this.modelScale;
    }

    @Generated
    public Set<ProceduralType> getProceduralTypes() {
        return this.proceduralTypes;
    }

    @Override
    @Generated
    public ManualAnimator getManualAnimator() {
        return this.manualAnimator;
    }

    @Override
    @Generated
    public SafeTransform getLocalTransform() {
        return this.localTransform;
    }

    @Override
    @Generated
    public SafeTransform getGlobalTransform() {
        return this.globalTransform;
    }

    @Override
    @Generated
    public Matrix4f getTransformMatrix() {
        return this.transformMatrix;
    }

    @Generated
    public Map<String, StepFlag> getStepFlags() {
        return this.stepFlags;
    }

    @Generated
    public DataTracker<Map<String, StepFlag>> getTrackedStepFlags() {
        return this.trackedStepFlags;
    }

    @Override
    @Generated
    public Set<String> getTickDependencies() {
        return this.tickDependencies;
    }

    @Override
    @Generated
    public String getCustomId() {
        return this.customId;
    }

    @Override
    @Nullable
    @Generated
    public ModelBone getParent() {
        return this.parent;
    }

    @Override
    @Nullable
    @Generated
    public ModelBone getPivot() {
        return this.pivot;
    }

    @Override
    @Generated
    public boolean isPivotOverride() {
        return this.pivotOverride;
    }

    @Override
    @Generated
    public Location getPivotLocation() {
        return this.pivotLocation;
    }

    @Override
    @Generated
    public boolean isRenderer() {
        return this.isRenderer;
    }

    @Override
    @Generated
    public float getYaw() {
        return this.yaw;
    }

    @Generated
    public boolean isHasGlobalRotation() {
        return this.hasGlobalRotation;
    }

    @Generated
    public Color getLastColor() {
        return this.lastColor;
    }

    @Override
    @Generated
    public boolean isEnchanted() {
        return this.enchanted;
    }

    @Override
    @Generated
    public boolean isVisible() {
        return this.visible;
    }

    @Override
    @Generated
    public boolean isMarkedDestroy() {
        return this.markedDestroy;
    }

    @Generated
    public boolean isBoneOnGround() {
        return this.isBoneOnGround;
    }

    @Override
    @Generated
    public void setManualAnimator(ManualAnimator manualAnimator) {
        this.manualAnimator = manualAnimator;
    }

    @Override
    @Generated
    public void setCustomId(String customId) {
        this.customId = customId;
    }

    @Override
    @Generated
    public void setPivot(@Nullable ModelBone pivot) {
        this.pivot = pivot;
    }

    @Override
    @Generated
    public void setPivotLocation(Location pivotLocation) {
        this.pivotLocation = pivotLocation;
    }

    @Override
    @Generated
    public void setRenderer(boolean isRenderer) {
        this.isRenderer = isRenderer;
    }

    @Override
    @Generated
    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    @Override
    @Generated
    public void setHasGlobalRotation(boolean hasGlobalRotation) {
        this.hasGlobalRotation = hasGlobalRotation;
    }

    @Override
    @Generated
    public void setDefaultTint(Color defaultTint) {
        this.defaultTint = defaultTint;
    }

    @Override
    @Generated
    public void setDamageTint(Color damageTint) {
        this.damageTint = damageTint;
    }

    @Override
    @Generated
    public void setGlowColor(Integer glowColor) {
        this.glowColor = glowColor;
    }

    @Override
    @Generated
    public void setBlockLight(int blockLight) {
        this.blockLight = blockLight;
    }

    @Override
    @Generated
    public void setSkyLight(int skyLight) {
        this.skyLight = skyLight;
    }

    @Override
    @Generated
    public void setBillboard(Display.Billboard billboard) {
        this.billboard = billboard;
    }

    @Override
    @Generated
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    @Generated
    public void setMarkedDestroy(boolean markedDestroy) {
        this.markedDestroy = markedDestroy;
    }

    @Override
    @Generated
    public void setBoneOnGround(boolean isBoneOnGround) {
        this.isBoneOnGround = isBoneOnGround;
    }
}

