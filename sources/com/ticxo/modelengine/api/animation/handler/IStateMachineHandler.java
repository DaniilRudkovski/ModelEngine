/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package com.ticxo.modelengine.api.animation.handler;

import com.ticxo.modelengine.api.animation.handler.AnimationHandler;
import com.ticxo.modelengine.api.animation.property.IAnimationProperty;
import com.ticxo.modelengine.api.generator.blueprint.ModelBlueprint;
import org.jetbrains.annotations.Nullable;

public interface IStateMachineHandler
extends AnimationHandler {
    public ModelBlueprint getBlueprint();

    @Nullable
    default public IAnimationProperty getAnimation(int priority, String animation) {
        return this.getAnimation(priority, this.getBlueprint(), animation);
    }

    @Nullable
    public IAnimationProperty getAnimation(int var1, ModelBlueprint var2, String var3);

    @Nullable
    default public IAnimationProperty playAnimation(int priority, String animation, double lerpIn, double lerpOut, double speed, boolean force) {
        return this.playAnimation(priority, this.getBlueprint(), animation, lerpIn, lerpOut, speed, force);
    }

    @Nullable
    public IAnimationProperty playAnimation(int var1, ModelBlueprint var2, String var3, double var4, double var6, double var8, boolean var10);

    public boolean playAnimation(int var1, IAnimationProperty var2, boolean var3);

    public void refreshState(AnimationHandler.DefaultProperty var1);

    default public boolean isPlayingAnimation(int priority, String animation) {
        return this.isPlayingAnimation(priority, this.getBlueprint(), animation);
    }

    public boolean isPlayingAnimation(int var1, ModelBlueprint var2, String var3);

    default public void stopAnimation(int priority, String animation) {
        this.stopAnimation(priority, this.getBlueprint(), animation);
    }

    public void stopAnimation(int var1, ModelBlueprint var2, String var3);

    default public void forceStopAnimation(int priority, String animation) {
        this.forceStopAnimation(priority, this.getBlueprint(), animation);
    }

    public void forceStopAnimation(int var1, ModelBlueprint var2, String var3);

    @Override
    default public String getId() {
        return "state_machine";
    }
}

