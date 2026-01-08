/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Color
 *  org.bukkit.Location
 *  org.bukkit.entity.Display$Billboard
 *  org.bukkit.inventory.ItemStack
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Matrix4f
 *  org.joml.Vector3f
 */
package com.ticxo.modelengine.api.model.bone;

import com.ticxo.modelengine.api.generator.blueprint.BlueprintBone;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.bone.BoneItems;
import com.ticxo.modelengine.api.model.bone.ManualAnimator;
import com.ticxo.modelengine.api.model.bone.behavior.BoneBehavior;
import com.ticxo.modelengine.api.model.bone.behavior.BoneBehaviorType;
import com.ticxo.modelengine.api.utils.OffsetMode;
import com.ticxo.modelengine.api.utils.StepFlag;
import com.ticxo.modelengine.api.utils.data.io.DataIO;
import com.ticxo.modelengine.api.utils.data.tracker.DataTracker;
import com.ticxo.modelengine.api.utils.math.SafeTransform;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public interface ModelBone
extends DataIO {
    public String getUniqueBoneId();

    public String getBoneId();

    public String getCustomId();

    public void setCustomId(String var1);

    public ActiveModel getActiveModel();

    public BlueprintBone getBlueprintBone();

    @Nullable
    public ModelBone getParent();

    @Nullable
    public ModelBone getPivot();

    public void setParent(@Nullable ModelBone var1);

    public void setPivot(@Nullable ModelBone var1);

    public boolean isPivotOverride();

    public void setPivotLocation(Location var1);

    public Location getPivotLocation();

    public Location calculatePivotLocation();

    public Location getBaseLocation();

    public Map<String, ModelBone> getChildren();

    public boolean isRenderer();

    public void setRenderer(boolean var1);

    public void addTickDependencies(String ... var1);

    public void removeTickDependencies(String ... var1);

    public void clearTickDependencies();

    public Set<String> getTickDependencies();

    public void tick();

    public void lazyTick();

    public boolean isMarkedDestroy();

    public void destroy();

    public float getYaw();

    public void setYaw(float var1);

    public void setModelScale(int var1);

    public void calculateGlobalTransform();

    public void setManualAnimator(ManualAnimator var1);

    public ManualAnimator getManualAnimator();

    public SafeTransform getLocalTransform();

    public SafeTransform getGlobalTransform();

    public Matrix4f getTransformMatrix();

    public boolean hasGlobalRotation();

    public void setHasGlobalRotation(boolean var1);

    @Deprecated
    public ItemStack getModel();

    public BoneItems getModels();

    public void setModel(BlueprintBone var1);

    public void setModel(ItemStack var1);

    public void clearModel();

    public DataTracker<BoneItems> getModelTracker();

    public Color getDefaultTint();

    public void setDefaultTint(Color var1);

    public Color getDamageTint();

    public void setDamageTint(Color var1);

    public boolean isEnchanted();

    public void setEnchanted(boolean var1);

    public boolean isVisible();

    public void setVisible(boolean var1);

    public boolean isGlowing();

    public void setGlowing(@Nullable Boolean var1);

    public int getGlowColor();

    public void setGlowColor(@Nullable Integer var1);

    public int getBlockLight();

    public void setBlockLight(int var1);

    public int getSkyLight();

    public void setSkyLight(int var1);

    public Display.Billboard getBillboard();

    public void setBillboard(Display.Billboard var1);

    default public int getBrightness() {
        int blockLight = this.getBlockLight();
        int skyLight = this.getSkyLight();
        if (blockLight < 0 && skyLight < 0) {
            return -1;
        }
        blockLight = Math.max(blockLight, 0);
        skyLight = Math.max(skyLight, 0);
        return blockLight << 4 | skyLight << 20;
    }

    public boolean isEffectivelyInvisible();

    public boolean shouldStep();

    public void markStep(StepFlag var1, String var2, float var3);

    public boolean pollModelScaleChanged();

    public boolean isRootBone();

    public void setBoneOnGround(boolean var1);

    public Location getLocationUnsafe();

    public Location getLocation();

    default public Location getLocation(OffsetMode mode, Vector3f offset, boolean scale) {
        return this.getLocation(mode, offset, scale, true);
    }

    public Location getLocation(OffsetMode var1, Vector3f var2, boolean var3, boolean var4);

    public void addBoneBehavior(BoneBehavior var1);

    public boolean hasBoneBehavior(BoneBehaviorType<?> var1);

    public <T extends BoneBehavior> Optional<T> getBoneBehavior(BoneBehaviorType<T> var1);

    public <T extends BoneBehavior> Optional<T> removeBoneBehavior(BoneBehaviorType<T> var1);

    public Map<BoneBehaviorType<?>, BoneBehavior> getImmutableBoneBehaviors();

    public void forBehaviors(Consumer<BoneBehavior> var1);
}

