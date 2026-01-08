/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 *  org.jetbrains.annotations.Nullable
 */
package com.ticxo.modelengine.api.animation.handler;

import com.ticxo.modelengine.api.animation.BlueprintAnimation;
import com.ticxo.modelengine.api.animation.ModelState;
import com.ticxo.modelengine.api.animation.property.IAnimationProperty;
import com.ticxo.modelengine.api.animation.property.SimpleProperty;
import com.ticxo.modelengine.api.entity.BaseEntity;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.ModeledEntity;
import com.ticxo.modelengine.api.model.bone.ModelBone;
import com.ticxo.modelengine.api.utils.data.io.DataIO;
import com.ticxo.modelengine.api.utils.data.io.SavedData;
import java.util.Map;
import lombok.Generated;
import org.jetbrains.annotations.Nullable;

public interface AnimationHandler
extends DataIO {
    public ActiveModel getActiveModel();

    public void prepare();

    public void updateBone(ModelBone var1);

    public boolean hasFinishedAllAnimations();

    public void setDefaultProperty(DefaultProperty var1);

    public DefaultProperty getDefaultProperty(ModelState var1);

    public void tickGlobal();

    @Nullable
    public IAnimationProperty playAnimation(String var1, double var2, double var4, double var6, boolean var8);

    public boolean playAnimation(IAnimationProperty var1, boolean var2);

    public boolean isPlayingAnimation(String var1);

    public void stopAnimation(String var1);

    public void forceStopAnimation(String var1);

    public void forceStopAllAnimations();

    @Nullable
    public IAnimationProperty getAnimation(String var1);

    public Map<String, IAnimationProperty> getAnimations();

    public String getId();

    default public boolean isWalking(ActiveModel activeModel) {
        ModeledEntity modeledEntity = activeModel.getModeledEntity();
        BaseEntity<?> baseEntity = modeledEntity.getBase();
        return !modeledEntity.getRootMotionHandler().isQueued() && baseEntity.isWalking();
    }

    @Override
    default public void save(SavedData data) {
        SavedData defaultData = new SavedData();
        for (ModelState state : ModelState.values()) {
            DefaultProperty property = this.getDefaultProperty(state);
            SavedData propertyData = new SavedData();
            propertyData.putDouble("lerp_in", property.lerpIn);
            propertyData.putDouble("lerp_out", property.lerpOut);
            propertyData.putDouble("speed", property.speed);
            defaultData.putData(state.name(), propertyData);
        }
        data.putData("defaults", defaultData);
        data.putString("id", this.getId());
    }

    @Override
    default public void load(SavedData data) {
        data.getData("defaults").ifPresent(defaultData -> {
            for (String key : defaultData.keySet()) {
                ModelState state = ModelState.get(key);
                defaultData.getData(key).ifPresent(propertyData -> this.setDefaultProperty(new DefaultProperty(state, propertyData.getDouble("lerp_in"), propertyData.getDouble("lerp_out"), propertyData.getDouble("speed"))));
            }
        });
    }

    public static class DefaultProperty {
        private final ModelState state;
        private final String animation;
        private final double lerpIn;
        private final double lerpOut;
        private final double speed;
        private final boolean merge;

        public DefaultProperty(ModelState state, double lerpIn, double lerpOut, double speed) {
            this(state, state.getString(), lerpIn, lerpOut, speed, false);
        }

        public DefaultProperty(ModelState state, double lerpIn, double lerpOut, double speed, boolean merge) {
            this(state, state.getString(), lerpIn, lerpOut, speed, merge);
        }

        public DefaultProperty(ModelState state, String animation, double lerpIn, double lerpOut, double speed) {
            this(state, animation, lerpIn, lerpOut, speed, false);
        }

        public DefaultProperty(ModelState state, String animation, double lerpIn, double lerpOut, double speed, boolean merge) {
            this.state = state;
            this.animation = animation;
            this.lerpIn = lerpIn;
            this.lerpOut = lerpOut;
            this.speed = speed;
            this.merge = merge;
        }

        public IAnimationProperty build(ActiveModel model) {
            return this.build(model, this.lerpIn, this.lerpOut, this.speed);
        }

        public IAnimationProperty build(ActiveModel model, double lerpIn, double lerpOut, double speed) {
            BlueprintAnimation blueprintAnimation = model.getBlueprint().getAnimations().get(this.animation);
            if (blueprintAnimation == null) {
                return null;
            }
            SimpleProperty property = new SimpleProperty(model, blueprintAnimation, lerpIn, lerpOut, speed);
            property.setSkipLastFrame(true);
            return property;
        }

        public IAnimationProperty buildRef(ActiveModel model) {
            return this.buildRef(model, this.lerpIn, this.lerpOut, this.speed);
        }

        public IAnimationProperty buildRef(ActiveModel model, double lerpIn, double lerpOut, double speed) {
            BlueprintAnimation blueprintAnimation = model.getBlueprint().getAnimationOrRef(this.animation);
            if (blueprintAnimation == null) {
                return null;
            }
            SimpleProperty property = new SimpleProperty(model, blueprintAnimation, lerpIn, lerpOut, speed);
            property.setSkipLastFrame(true);
            return property;
        }

        @Generated
        public ModelState getState() {
            return this.state;
        }

        @Generated
        public String getAnimation() {
            return this.animation;
        }

        @Generated
        public double getLerpIn() {
            return this.lerpIn;
        }

        @Generated
        public double getLerpOut() {
            return this.lerpOut;
        }

        @Generated
        public double getSpeed() {
            return this.speed;
        }

        @Generated
        public boolean isMerge() {
            return this.merge;
        }

        @Generated
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof DefaultProperty)) {
                return false;
            }
            DefaultProperty other = (DefaultProperty)o;
            if (!other.canEqual(this)) {
                return false;
            }
            if (Double.compare(this.getLerpIn(), other.getLerpIn()) != 0) {
                return false;
            }
            if (Double.compare(this.getLerpOut(), other.getLerpOut()) != 0) {
                return false;
            }
            if (Double.compare(this.getSpeed(), other.getSpeed()) != 0) {
                return false;
            }
            if (this.isMerge() != other.isMerge()) {
                return false;
            }
            ModelState this$state = this.getState();
            ModelState other$state = other.getState();
            if (this$state == null ? other$state != null : !this$state.equals(other$state)) {
                return false;
            }
            String this$animation = this.getAnimation();
            String other$animation = other.getAnimation();
            return !(this$animation == null ? other$animation != null : !this$animation.equals(other$animation));
        }

        @Generated
        protected boolean canEqual(Object other) {
            return other instanceof DefaultProperty;
        }

        @Generated
        public int hashCode() {
            int PRIME = 59;
            int result = 1;
            long $lerpIn = Double.doubleToLongBits(this.getLerpIn());
            result = result * 59 + (int)($lerpIn >>> 32 ^ $lerpIn);
            long $lerpOut = Double.doubleToLongBits(this.getLerpOut());
            result = result * 59 + (int)($lerpOut >>> 32 ^ $lerpOut);
            long $speed = Double.doubleToLongBits(this.getSpeed());
            result = result * 59 + (int)($speed >>> 32 ^ $speed);
            result = result * 59 + (this.isMerge() ? 79 : 97);
            ModelState $state = this.getState();
            result = result * 59 + ($state == null ? 43 : $state.hashCode());
            String $animation = this.getAnimation();
            result = result * 59 + ($animation == null ? 43 : $animation.hashCode());
            return result;
        }

        @Generated
        public String toString() {
            return "AnimationHandler.DefaultProperty(state=" + this.getState() + ", animation=" + this.getAnimation() + ", lerpIn=" + this.getLerpIn() + ", lerpOut=" + this.getLerpOut() + ", speed=" + this.getSpeed() + ", merge=" + this.isMerge() + ")";
        }
    }
}

