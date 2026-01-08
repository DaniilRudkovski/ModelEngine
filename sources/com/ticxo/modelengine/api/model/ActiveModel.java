/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Color
 *  org.bukkit.entity.Display$Billboard
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Vector3fc
 */
package com.ticxo.modelengine.api.model;

import com.ticxo.modelengine.api.animation.handler.AnimationHandler;
import com.ticxo.modelengine.api.generator.blueprint.BlueprintBone;
import com.ticxo.modelengine.api.generator.blueprint.ModelBlueprint;
import com.ticxo.modelengine.api.model.ModeledEntity;
import com.ticxo.modelengine.api.model.PivotOverride;
import com.ticxo.modelengine.api.model.bone.BoneBehaviorTypes;
import com.ticxo.modelengine.api.model.bone.ModelBone;
import com.ticxo.modelengine.api.model.bone.behavior.BoneBehavior;
import com.ticxo.modelengine.api.model.bone.behavior.BoneBehaviorType;
import com.ticxo.modelengine.api.model.bone.manager.BehaviorManager;
import com.ticxo.modelengine.api.model.bone.manager.LeashManager;
import com.ticxo.modelengine.api.model.bone.manager.MountManager;
import com.ticxo.modelengine.api.model.bone.render.BehaviorRenderer;
import com.ticxo.modelengine.api.model.bone.type.Leash;
import com.ticxo.modelengine.api.model.bone.type.Mount;
import com.ticxo.modelengine.api.model.render.ModelRenderer;
import com.ticxo.modelengine.api.utils.callback.Callback;
import com.ticxo.modelengine.api.utils.data.io.DataIO;
import com.ticxo.modelengine.api.utils.meta.IMetaHolder;
import java.util.Map;
import java.util.Optional;
import org.bukkit.Color;
import org.bukkit.entity.Display;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3fc;

public interface ActiveModel
extends DataIO,
IMetaHolder {
    public ModeledEntity getModeledEntity();

    public void setModeledEntity(ModeledEntity var1);

    public ModelBlueprint getBlueprint();

    public ModelRenderer getModelRenderer();

    public AnimationHandler getAnimationHandler();

    public Optional<PivotOverride> getPivotOverride();

    public void setPivotOverride(PivotOverride var1);

    public Map<String, ModelBone> getBones();

    public Map<BoneBehaviorType<?>, BehaviorManager<?>> getBehaviorManagers();

    public Map<BoneBehaviorType<?>, BehaviorRenderer> getBehaviorRenderers();

    public boolean isMainHitbox();

    public void setMainHitbox(boolean var1);

    public Vector3fc getScale();

    public void setScale(double var1);

    public Vector3fc getHitboxScale();

    public void setHitboxScale(double var1);

    public void tick();

    public void destroy();

    public boolean isDestroyed();

    public boolean isRemoved();

    public void setRemoved(boolean var1);

    public void setAutoRendererInitialization(boolean var1);

    public boolean isHitboxVisible();

    public void setHitboxVisible(boolean var1);

    public boolean isShadowVisible();

    public void setShadowVisible(boolean var1);

    public void initializeRenderer();

    public void generateModel();

    public void forceGenerateBone(String var1, String var2, BlueprintBone var3);

    public void removeBone(String var1);

    public Callback<Scale> getScaleCallback();

    public void setCanHurt(boolean var1);

    public boolean canHurt();

    public Color getDefaultTint();

    public void setDefaultTint(Color var1);

    public Color getDamageTint();

    public void setDamageTint(Color var1);

    public boolean wasMarkedHurt();

    public boolean isMarkedHurt();

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

    public boolean isOnFire();

    public void setOnFire(Boolean var1);

    public boolean canRenderFire();

    public void setRenderFire(Boolean var1);

    public float getXHeadRot();

    public float getYHeadRot();

    public boolean isLockPitch();

    public void setLockPitch(boolean var1);

    public boolean isLockYaw();

    public void setLockYaw(boolean var1);

    public boolean isInvisUpdate();

    public void setInvisUpdate(boolean var1);

    default public Optional<ModelBone> getBone(String boneId) {
        return Optional.ofNullable(this.getBones().get(boneId));
    }

    public <T extends BoneBehavior> Optional<BehaviorManager<T>> getBehaviorManager(BoneBehaviorType<T> var1);

    public Optional<BehaviorRenderer> getBehaviorRenderer(BoneBehaviorType<?> var1);

    default public <T extends MountManager & BehaviorManager<? extends Mount>> Optional<T> getMountManager() {
        Optional<BehaviorManager<? extends Mount>> maybe = this.getBehaviorManager(BoneBehaviorTypes.MOUNT);
        return maybe.map(behaviorManager -> (MountManager)((Object)behaviorManager));
    }

    default public <T extends LeashManager & BehaviorManager<? extends Leash>> Optional<T> getLeashManager() {
        Optional<BehaviorManager<? extends Leash>> maybe = this.getBehaviorManager(BoneBehaviorTypes.LEASH);
        return maybe.map(behaviorManager -> (LeashManager)((Object)behaviorManager));
    }

    @FunctionalInterface
    public static interface Scale {
        public void onScale(ActiveModel var1, double var2);
    }
}

