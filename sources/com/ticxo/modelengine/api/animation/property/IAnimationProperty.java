/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.joml.Vector3f
 */
package com.ticxo.modelengine.api.animation.property;

import com.ticxo.modelengine.api.animation.BlueprintAnimation;
import com.ticxo.modelengine.api.animation.keyframe.KeyframeType;
import com.ticxo.modelengine.api.animation.keyframe.type.ScriptKeyframe;
import com.ticxo.modelengine.api.generator.blueprint.BlueprintBone;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.bone.ModelBone;
import com.ticxo.modelengine.api.utils.data.io.DataIO;
import java.util.List;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public interface IAnimationProperty
extends DataIO {
    public BlueprintAnimation getBlueprintAnimation();

    public boolean update();

    public void stop();

    public boolean isEnded();

    public boolean canReplace();

    public String getName();

    public boolean containsKeyframe(KeyframeType<?, ?> var1, UUID var2);

    default public Vector3f getPositionFrame(ModelBone bone) {
        return this.getPositionFrame(bone, this.getBlueprintBone(bone));
    }

    public Vector3f getPositionFrame(ModelBone var1, BlueprintBone var2);

    default public Vector3f getVelocityFrame(ModelBone bone) {
        return this.getVelocityFrame(bone, this.getBlueprintBone(bone));
    }

    public Vector3f getVelocityFrame(ModelBone var1, BlueprintBone var2);

    default public Vector3f getRotationFrame(ModelBone bone) {
        return this.getRotationFrame(bone, this.getBlueprintBone(bone));
    }

    public Vector3f getRotationFrame(ModelBone var1, BlueprintBone var2);

    default public Vector3f getScaleFrame(ModelBone bone) {
        return this.getScaleFrame(bone, this.getBlueprintBone(bone));
    }

    public Vector3f getScaleFrame(ModelBone var1, BlueprintBone var2);

    public List<ScriptKeyframe.Script> getScriptFrame();

    public double getLerpInRatio();

    public double getLerpOutRatio();

    public boolean isFinished();

    public ActiveModel getModel();

    public double getLerpIn();

    public double getLerpOut();

    public double getLerpInTime();

    public void setLerpInTime(double var1);

    public double getLerpOutTime();

    public void setLerpOutTime(double var1);

    public double getLastTime();

    public double getTime();

    public double getSpeed();

    public void setSpeed(double var1);

    @NotNull
    public Phase getPhase();

    public BlueprintAnimation.LoopMode getForceLoopMode();

    public void setForceLoopMode(BlueprintAnimation.LoopMode var1);

    public BlueprintAnimation.LoopMode getLoopMode();

    public boolean isOverride();

    public BlueprintAnimation.OverrideMode getForceOverride();

    public void setForceOverride(BlueprintAnimation.OverrideMode var1);

    public boolean isEmptyZero();

    public void setEmptyZero(boolean var1);

    public boolean isSkipLastFrame();

    public void setSkipLastFrame(boolean var1);

    public boolean isMergeLerp();

    public void setMergeLerp(boolean var1);

    public boolean isIgnoreDeath();

    public void setIgnoreDeath(boolean var1);

    default public BlueprintBone getBlueprintBone(ModelBone bone) {
        BlueprintBone bBone = bone.getBlueprintBone();
        BlueprintBone bBone2 = this.getBlueprintAnimation().getModelBlueprint().getFlatMap().get(bBone.getName());
        return bBone2 == null ? bBone : bBone2;
    }

    public static enum Phase {
        LERPIN,
        PLAY,
        LERPOUT;

    }
}

