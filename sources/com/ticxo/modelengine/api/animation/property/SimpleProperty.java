/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 *  org.jetbrains.annotations.NotNull
 *  org.joml.Vector3f
 */
package com.ticxo.modelengine.api.animation.property;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.animation.BlueprintAnimation;
import com.ticxo.modelengine.api.animation.Timeline;
import com.ticxo.modelengine.api.animation.handler.AnimationHandler;
import com.ticxo.modelengine.api.animation.keyframe.KeyframeType;
import com.ticxo.modelengine.api.animation.keyframe.type.ScriptKeyframe;
import com.ticxo.modelengine.api.animation.property.IAnimationProperty;
import com.ticxo.modelengine.api.generator.blueprint.BlueprintBone;
import com.ticxo.modelengine.api.generator.blueprint.ModelBlueprint;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.bone.ModelBone;
import com.ticxo.modelengine.api.utils.data.io.SavedData;
import com.ticxo.modelengine.api.utils.math.TMath;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.Generated;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public class SimpleProperty
implements IAnimationProperty {
    private final ActiveModel model;
    private final BlueprintAnimation blueprintAnimation;
    private final double lerpIn;
    private final double lerpOut;
    private double lerpInTime = 0.0;
    private double lerpOutTime = 0.0;
    private double lastTime = -1.0;
    private double time = -1.0;
    private double speed;
    @NotNull
    private IAnimationProperty.Phase phase = IAnimationProperty.Phase.LERPIN;
    private BlueprintAnimation.LoopMode forceLoopMode = null;
    private BlueprintAnimation.OverrideMode forceOverride = null;
    private boolean emptyZero = ModelEngineAPI.getConfigCache().isEmptyZero();
    private boolean skipLastFrame;
    private boolean mergeLerp = false;
    private boolean ignoreDeath = false;
    private boolean stopping;
    private boolean ended;
    private Runnable onEndTask = null;

    public SimpleProperty(ActiveModel model, BlueprintAnimation blueprintAnimation) {
        this(model, blueprintAnimation, 0.0, 0.0, 1.0);
    }

    public SimpleProperty(ActiveModel model, BlueprintAnimation blueprintAnimation, double lerpIn, double lerpOut, double speed) {
        this.model = model;
        this.blueprintAnimation = blueprintAnimation;
        this.lerpIn = lerpIn;
        this.lerpOut = lerpOut;
        this.speed = speed;
    }

    @Override
    public boolean update() {
        boolean bl;
        this.lastTime = this.time;
        if (this.mergeLerp) {
            bl = this.mergeUpdate();
        } else {
            switch (this.phase) {
                default: {
                    throw new IncompatibleClassChangeError();
                }
                case LERPIN: {
                    bl = this.updateLerpIn();
                    break;
                }
                case PLAY: {
                    bl = this.updateTime();
                    break;
                }
                case LERPOUT: {
                    bl = this.updateLerpOut();
                }
            }
        }
        return bl;
    }

    private boolean mergeUpdate() {
        this.time = Math.max(this.time, 0.0);
        BlueprintAnimation.LoopMode mode = this.getLoopMode();
        double delta = this.speed * 0.05;
        double length = this.blueprintAnimation.getLength();
        switch (mode) {
            case ONCE: 
            case HOLD: {
                this.time = Math.min(this.time + delta, length);
                break;
            }
            case LOOP: {
                this.time = (this.time + delta) % (length + (this.skipLastFrame ? 0.0 : delta));
            }
        }
        boolean reprocess = true;
        while (reprocess) {
            reprocess = false;
            switch (this.phase) {
                case LERPIN: {
                    this.lerpInTime += delta;
                    if (!(this.lerpInTime >= this.lerpIn - (double)1.0E-5f)) break;
                    this.phase = this.stopping ? IAnimationProperty.Phase.LERPOUT : IAnimationProperty.Phase.PLAY;
                    reprocess = true;
                    break;
                }
                case PLAY: {
                    if (!this.stopping && (mode != BlueprintAnimation.LoopMode.ONCE || !(length - this.time <= this.lerpOut))) break;
                    this.phase = IAnimationProperty.Phase.LERPOUT;
                    reprocess = true;
                    break;
                }
                case LERPOUT: {
                    this.lerpOutTime += delta;
                    if (!(this.lerpOutTime >= this.lerpOut - (double)1.0E-5f)) break;
                    this.ended = true;
                }
            }
        }
        return !this.ended;
    }

    private boolean updateLerpIn() {
        if (this.lerpInTime >= this.lerpIn - (double)1.0E-5f) {
            this.time = 0.0;
            return this.stopping ? this.updateLerpOut() : this.updateTime();
        }
        this.lerpInTime += this.speed * 0.05;
        return this.playingOrLerpOut();
    }

    private boolean updateTime() {
        if (this.phase == IAnimationProperty.Phase.LERPIN) {
            this.phase = IAnimationProperty.Phase.PLAY;
            return this.playingOrLerpOut();
        }
        BlueprintAnimation.LoopMode mode = this.getLoopMode();
        switch (mode) {
            case ONCE: {
                if (this.time < this.blueprintAnimation.getLength()) {
                    this.time = Math.min(this.time + this.speed * 0.05, this.blueprintAnimation.getLength());
                    return this.playingOrLerpOut();
                }
                return this.updateLerpOut();
            }
            case HOLD: {
                this.time = Math.min(this.time + this.speed * 0.05, this.blueprintAnimation.getLength());
                return this.playingOrLerpOut();
            }
            case LOOP: {
                this.time = (this.time + this.speed * 0.05) % (this.skipLastFrame ? this.blueprintAnimation.getLength() : this.blueprintAnimation.getLength() + this.speed * 0.05);
                return this.playingOrLerpOut();
            }
        }
        return false;
    }

    private boolean updateLerpOut() {
        if (this.phase != IAnimationProperty.Phase.LERPOUT && this.lerpOut > (double)1.0E-5f) {
            this.phase = IAnimationProperty.Phase.LERPOUT;
            return true;
        }
        if (this.lerpOutTime >= this.lerpOut - (double)1.0E-5f) {
            this.ended = true;
            return false;
        }
        this.lerpOutTime += this.speed * 0.05;
        return true;
    }

    private boolean playingOrLerpOut() {
        return !this.stopping || this.updateLerpOut();
    }

    @Override
    public void stop() {
        this.stopping = true;
    }

    @Override
    public boolean canReplace() {
        return this.stopping || this.phase == IAnimationProperty.Phase.LERPOUT || this.ended;
    }

    @Override
    public String getName() {
        return this.blueprintAnimation.getName();
    }

    @Override
    public boolean containsKeyframe(KeyframeType<?, ?> type, UUID boneUuid) {
        Timeline timeline = this.blueprintAnimation.getTimelines().get(boneUuid);
        if (timeline == null) {
            return false;
        }
        return timeline.hasInterpolator(type) && !timeline.getInterpolator(type).isEmpty();
    }

    @Override
    public Vector3f getPositionFrame(ModelBone bone, BlueprintBone blueprintBone) {
        return this.blueprintAnimation.getPosition(bone, blueprintBone, this);
    }

    @Override
    public Vector3f getVelocityFrame(ModelBone bone, BlueprintBone blueprintBone) {
        return this.blueprintAnimation.getVelocity(bone, blueprintBone, this);
    }

    @Override
    public Vector3f getRotationFrame(ModelBone bone, BlueprintBone blueprintBone) {
        return this.blueprintAnimation.getRotation(bone, blueprintBone, this);
    }

    @Override
    public Vector3f getScaleFrame(ModelBone bone, BlueprintBone blueprintBone) {
        return this.blueprintAnimation.getScale(bone, blueprintBone, this);
    }

    @Override
    public List<ScriptKeyframe.Script> getScriptFrame() {
        return this.blueprintAnimation.getScript(this);
    }

    @Override
    public double getLerpInRatio() {
        return TMath.clamp(this.lerpInTime / this.lerpIn, 0.0, 1.0);
    }

    @Override
    public double getLerpOutRatio() {
        return TMath.clamp(this.lerpOutTime / this.lerpOut, 0.0, 1.0);
    }

    @Override
    public boolean isFinished() {
        return this.phase == IAnimationProperty.Phase.LERPOUT || this.time >= this.blueprintAnimation.getLength();
    }

    @Override
    public BlueprintAnimation.LoopMode getLoopMode() {
        return this.forceLoopMode == null ? this.blueprintAnimation.getLoopMode() : this.forceLoopMode;
    }

    @Override
    public boolean isOverride() {
        return this.forceOverride == null && this.blueprintAnimation.isOverride() || this.forceOverride == BlueprintAnimation.OverrideMode.OVERRIDE;
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.lerpIn, this.lerpOut, this.time, this.speed, this.phase, this.forceLoopMode, this.forceOverride});
    }

    @Override
    public void save(SavedData data) {
        data.putString("id", "simple");
        data.putString("name", this.getName());
        data.putDouble("lerp_in", this.lerpIn);
        data.putDouble("lerp_out", this.lerpOut);
        data.putDouble("lerp_in_time", this.lerpInTime);
        data.putDouble("lerp_out_time", this.lerpOutTime);
        data.putDouble("last_time", this.lastTime);
        data.putDouble("time", this.time);
        data.putDouble("speed", this.speed);
        data.putString("phase", this.phase.name());
        if (this.forceLoopMode != null) {
            data.putString("force_loop_mode", this.forceLoopMode.name());
        }
        if (this.forceOverride != null) {
            data.putString("force_override", this.forceOverride.name());
        }
    }

    @Override
    public void load(SavedData data) {
        this.lerpInTime = data.getDouble("lerp_in_time");
        this.lerpOutTime = data.getDouble("lerp_out_time");
        this.lastTime = data.getDouble("last_time");
        this.time = data.getDouble("time");
        this.phase = IAnimationProperty.Phase.valueOf(data.getString("phase"));
        data.loadIfExist("force_loop_mode", SavedData::getString, val -> {
            this.forceLoopMode = BlueprintAnimation.LoopMode.getOrNull(val);
        });
        data.loadIfExist("force_override", SavedData::getString, val -> {
            if (Boolean.parseBoolean(val)) {
                this.forceOverride = BlueprintAnimation.OverrideMode.OVERRIDE;
            }
            this.forceOverride = BlueprintAnimation.OverrideMode.getOrNull(val);
        });
    }

    public static SimpleProperty create(AnimationHandler handler, SavedData data) {
        ActiveModel model = handler.getActiveModel();
        ModelBlueprint blueprint = model.getBlueprint();
        BlueprintAnimation animation = blueprint.getAnimations().get(data.getString("name"));
        SimpleProperty property = new SimpleProperty(model, animation, data.getDouble("lerp_in", 0.0), data.getDouble("lerp_out", 0.0), data.getDouble("speed", 1.0));
        property.load(data);
        return property;
    }

    @Generated
    public String toString() {
        return "SimpleProperty(model=" + this.getModel() + ", blueprintAnimation=" + this.getBlueprintAnimation() + ", lerpIn=" + this.getLerpIn() + ", lerpOut=" + this.getLerpOut() + ", lerpInTime=" + this.getLerpInTime() + ", lerpOutTime=" + this.getLerpOutTime() + ", lastTime=" + this.getLastTime() + ", time=" + this.getTime() + ", speed=" + this.getSpeed() + ", phase=" + this.getPhase() + ", forceLoopMode=" + this.getForceLoopMode() + ", forceOverride=" + this.getForceOverride() + ", emptyZero=" + this.isEmptyZero() + ", skipLastFrame=" + this.isSkipLastFrame() + ", mergeLerp=" + this.isMergeLerp() + ", ignoreDeath=" + this.isIgnoreDeath() + ", stopping=" + this.isStopping() + ", ended=" + this.isEnded() + ", onEndTask=" + this.getOnEndTask() + ")";
    }

    @Override
    @Generated
    public ActiveModel getModel() {
        return this.model;
    }

    @Override
    @Generated
    public BlueprintAnimation getBlueprintAnimation() {
        return this.blueprintAnimation;
    }

    @Override
    @Generated
    public double getLerpIn() {
        return this.lerpIn;
    }

    @Override
    @Generated
    public double getLerpOut() {
        return this.lerpOut;
    }

    @Override
    @Generated
    public double getLerpInTime() {
        return this.lerpInTime;
    }

    @Override
    @Generated
    public double getLerpOutTime() {
        return this.lerpOutTime;
    }

    @Override
    @Generated
    public double getLastTime() {
        return this.lastTime;
    }

    @Override
    @Generated
    public double getTime() {
        return this.time;
    }

    @Override
    @Generated
    public double getSpeed() {
        return this.speed;
    }

    @Override
    @NotNull
    @Generated
    public IAnimationProperty.Phase getPhase() {
        return this.phase;
    }

    @Override
    @Generated
    public BlueprintAnimation.LoopMode getForceLoopMode() {
        return this.forceLoopMode;
    }

    @Override
    @Generated
    public BlueprintAnimation.OverrideMode getForceOverride() {
        return this.forceOverride;
    }

    @Override
    @Generated
    public boolean isEmptyZero() {
        return this.emptyZero;
    }

    @Override
    @Generated
    public boolean isSkipLastFrame() {
        return this.skipLastFrame;
    }

    @Override
    @Generated
    public boolean isMergeLerp() {
        return this.mergeLerp;
    }

    @Override
    @Generated
    public boolean isIgnoreDeath() {
        return this.ignoreDeath;
    }

    @Generated
    public boolean isStopping() {
        return this.stopping;
    }

    @Override
    @Generated
    public boolean isEnded() {
        return this.ended;
    }

    @Override
    @Generated
    public void setLerpInTime(double lerpInTime) {
        this.lerpInTime = lerpInTime;
    }

    @Override
    @Generated
    public void setLerpOutTime(double lerpOutTime) {
        this.lerpOutTime = lerpOutTime;
    }

    @Override
    @Generated
    public void setSpeed(double speed) {
        this.speed = speed;
    }

    @Generated
    public void setPhase(@NotNull IAnimationProperty.Phase phase) {
        if (phase == null) {
            throw new NullPointerException("phase is marked non-null but is null");
        }
        this.phase = phase;
    }

    @Override
    @Generated
    public void setForceLoopMode(BlueprintAnimation.LoopMode forceLoopMode) {
        this.forceLoopMode = forceLoopMode;
    }

    @Override
    @Generated
    public void setForceOverride(BlueprintAnimation.OverrideMode forceOverride) {
        this.forceOverride = forceOverride;
    }

    @Override
    @Generated
    public void setEmptyZero(boolean emptyZero) {
        this.emptyZero = emptyZero;
    }

    @Override
    @Generated
    public void setSkipLastFrame(boolean skipLastFrame) {
        this.skipLastFrame = skipLastFrame;
    }

    @Override
    @Generated
    public void setMergeLerp(boolean mergeLerp) {
        this.mergeLerp = mergeLerp;
    }

    @Override
    @Generated
    public void setIgnoreDeath(boolean ignoreDeath) {
        this.ignoreDeath = ignoreDeath;
    }

    @Generated
    public Runnable getOnEndTask() {
        return this.onEndTask;
    }

    @Generated
    public void setOnEndTask(Runnable onEndTask) {
        this.onEndTask = onEndTask;
    }
}

