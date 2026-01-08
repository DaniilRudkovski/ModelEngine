/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Maps
 *  lombok.Generated
 *  org.jetbrains.annotations.Nullable
 */
package com.ticxo.modelengine.core.animation.handler;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.animation.AnimationPropertyRegistry;
import com.ticxo.modelengine.api.animation.BlueprintAnimation;
import com.ticxo.modelengine.api.animation.ModelState;
import com.ticxo.modelengine.api.animation.Timeline;
import com.ticxo.modelengine.api.animation.handler.AnimationHandler;
import com.ticxo.modelengine.api.animation.handler.IPriorityHandler;
import com.ticxo.modelengine.api.animation.keyframe.KeyframeType;
import com.ticxo.modelengine.api.animation.keyframe.KeyframeTypeRegistry;
import com.ticxo.modelengine.api.animation.property.IAnimationProperty;
import com.ticxo.modelengine.api.animation.property.SimpleProperty;
import com.ticxo.modelengine.api.entity.BaseEntity;
import com.ticxo.modelengine.api.events.AnimationEndEvent;
import com.ticxo.modelengine.api.events.AnimationPlayEvent;
import com.ticxo.modelengine.api.generator.blueprint.ModelBlueprint;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.bone.ModelBone;
import com.ticxo.modelengine.api.utils.data.io.SavedData;
import com.ticxo.modelengine.api.utils.state.StateMachine;
import com.ticxo.modelengine.api.utils.state.StateNode;
import java.util.Map;
import java.util.Stack;
import java.util.UUID;
import java.util.function.BiConsumer;
import lombok.Generated;
import org.jetbrains.annotations.Nullable;

public class PriorityHandler
implements IPriorityHandler {
    private final ActiveModel activeModel;
    private final ModelBlueprint blueprint;
    private final Map<String, IAnimationProperty> properties = Maps.newConcurrentMap();
    private final Map<String, IAnimationProperty> updatedProperties = Maps.newConcurrentMap();
    private final Map<ModelState, AnimationHandler.DefaultProperty> defaultProperties = Maps.newConcurrentMap();
    private final StateMachine<BaseEntity<?>> stateMachine = new StateMachine();
    private boolean firstSpawn = true;

    public PriorityHandler(ActiveModel activeModel) {
        this.activeModel = activeModel;
        this.blueprint = activeModel.getBlueprint();
        this.setDefaultProperty(new AnimationHandler.DefaultProperty(ModelState.IDLE, 0.25, 0.25, 1.0));
        this.setDefaultProperty(new AnimationHandler.DefaultProperty(ModelState.WALK, 0.25, 0.25, 1.0));
        this.setDefaultProperty(new AnimationHandler.DefaultProperty(ModelState.STRAFE, 0.25, 0.25, 1.0));
        this.setDefaultProperty(new AnimationHandler.DefaultProperty(ModelState.JUMP_START, 0.0, 0.25, 1.0));
        this.setDefaultProperty(new AnimationHandler.DefaultProperty(ModelState.JUMP, 0.0, 0.0, 1.0));
        this.setDefaultProperty(new AnimationHandler.DefaultProperty(ModelState.JUMP_END, 0.0, 0.25, 1.0));
        this.setDefaultProperty(new AnimationHandler.DefaultProperty(ModelState.HOVER, 0.25, 0.25, 1.0));
        this.setDefaultProperty(new AnimationHandler.DefaultProperty(ModelState.FLY, 0.25, 0.25, 1.0));
        this.setDefaultProperty(new AnimationHandler.DefaultProperty(ModelState.SPAWN, 0.0, 0.25, 1.0));
        this.setDefaultProperty(new AnimationHandler.DefaultProperty(ModelState.DEATH, 0.0, 0.0, 1.0));
        this.configureAnimation();
    }

    private void configureAnimation() {
        StateNode<BaseEntity<?>> spawn = this.stateMachine.createNode();
        StateNode<BaseEntity<?>> idle = this.stateMachine.createNode();
        StateNode<BaseEntity<?>> walk = this.stateMachine.createNode();
        StateNode<BaseEntity<?>> strafe = this.stateMachine.createNode();
        StateNode<BaseEntity<?>> jumpStart = this.stateMachine.createNode();
        StateNode<BaseEntity<?>> jumpLoop = this.stateMachine.createNode();
        StateNode<BaseEntity<?>> jumpEnd = this.stateMachine.createNode();
        StateNode<BaseEntity<?>> hover = this.stateMachine.createNode();
        StateNode<BaseEntity<?>> fly = this.stateMachine.createNode();
        StateNode<BaseEntity<?>> death = this.stateMachine.createNode();
        spawn.setExitAction(baseEntity -> this.playState(ModelState.SPAWN));
        spawn.addConnectedNode(baseEntity -> !baseEntity.isAlive(), death);
        spawn.addConnectedNode(baseEntity -> baseEntity.isFlying() && this.isWalking(this.activeModel) && this.hasAnimation(ModelState.FLY), fly);
        spawn.addConnectedNode(baseEntity -> baseEntity.isFlying() && this.hasAnimation(ModelState.HOVER), hover);
        spawn.addConnectedNode(baseEntity -> baseEntity.isJumping() && this.hasAnimation(ModelState.JUMP_START), jumpStart);
        spawn.addConnectedNode(baseEntity -> baseEntity.isStrafing() && this.hasAnimation(ModelState.STRAFE), strafe);
        spawn.addConnectedNode(baseEntity -> this.isWalking(this.activeModel), walk);
        spawn.addConnectedNode(baseEntity -> !this.isWalking(this.activeModel), idle);
        idle.setEntryAction(baseEntity -> this.playState(ModelState.IDLE));
        idle.addForceConnectedNode(baseEntity -> !baseEntity.isAlive(), death);
        idle.addConnectedNode(baseEntity -> baseEntity.isFlying() && this.isWalking(this.activeModel) && this.hasAnimation(ModelState.FLY), fly);
        idle.addConnectedNode(baseEntity -> baseEntity.isFlying() && this.hasAnimation(ModelState.HOVER), hover);
        idle.addConnectedNode(baseEntity -> baseEntity.isJumping() && this.hasAnimation(ModelState.JUMP_START), jumpStart);
        idle.addConnectedNode(baseEntity -> baseEntity.isJumping() && this.hasAnimation(ModelState.JUMP), jumpLoop);
        idle.addConnectedNode(baseEntity -> baseEntity.isStrafing() && this.hasAnimation(ModelState.STRAFE), strafe);
        idle.addConnectedNode(baseEntity -> this.isWalking(this.activeModel), walk);
        idle.setExitAction(baseEntity -> this.stopState(ModelState.IDLE));
        walk.setEntryAction(baseEntity -> this.playState(ModelState.WALK));
        walk.addForceConnectedNode(baseEntity -> !baseEntity.isAlive(), death);
        walk.addConnectedNode(baseEntity -> baseEntity.isFlying() && this.isWalking(this.activeModel) && this.hasAnimation(ModelState.FLY), fly);
        walk.addConnectedNode(baseEntity -> baseEntity.isFlying() && this.hasAnimation(ModelState.HOVER), hover);
        walk.addConnectedNode(baseEntity -> baseEntity.isJumping() && this.hasAnimation(ModelState.JUMP_START), jumpStart);
        walk.addConnectedNode(baseEntity -> baseEntity.isJumping() && this.hasAnimation(ModelState.JUMP), jumpLoop);
        walk.addConnectedNode(baseEntity -> baseEntity.isStrafing() && this.hasAnimation(ModelState.STRAFE), strafe);
        walk.addConnectedNode(baseEntity -> !this.isWalking(this.activeModel), idle);
        walk.setExitAction(baseEntity -> this.stopState(ModelState.WALK));
        strafe.setEntryAction(baseEntity -> this.playState(ModelState.STRAFE));
        strafe.addForceConnectedNode(baseEntity -> !baseEntity.isAlive(), death);
        strafe.addConnectedNode(baseEntity -> baseEntity.isFlying() && this.isWalking(this.activeModel) && this.hasAnimation(ModelState.FLY), fly);
        strafe.addConnectedNode(baseEntity -> baseEntity.isFlying() && this.hasAnimation(ModelState.HOVER), hover);
        strafe.addConnectedNode(baseEntity -> baseEntity.isJumping() && this.hasAnimation(ModelState.JUMP_START), jumpStart);
        strafe.addConnectedNode(baseEntity -> baseEntity.isJumping() && this.hasAnimation(ModelState.JUMP), jumpLoop);
        strafe.addConnectedNode(baseEntity -> this.isWalking(this.activeModel), walk);
        strafe.addConnectedNode(baseEntity -> !this.isWalking(this.activeModel), idle);
        strafe.setExitAction(baseEntity -> this.stopState(ModelState.STRAFE));
        jumpStart.setEntryAction(baseEntity -> this.playState(ModelState.JUMP_START));
        jumpStart.addForceConnectedNode(baseEntity -> !baseEntity.isAlive(), death);
        jumpStart.setCommonPredicate(baseEntity -> this.hasFinishedPlaying(ModelState.JUMP_START));
        jumpStart.addConnectedNode(baseEntity -> baseEntity.isFlying() && this.isWalking(this.activeModel) && this.hasAnimation(ModelState.FLY), fly);
        jumpStart.addConnectedNode(baseEntity -> baseEntity.isFlying() && this.hasAnimation(ModelState.HOVER), hover);
        jumpStart.addConnectedNode(baseEntity -> !baseEntity.isJumping() && this.hasAnimation(ModelState.JUMP_END), jumpEnd);
        jumpStart.addConnectedNode(baseEntity -> !baseEntity.isJumping() && this.isWalking(this.activeModel), walk);
        jumpStart.addConnectedNode(baseEntity -> !baseEntity.isJumping() && !this.isWalking(this.activeModel), idle);
        jumpStart.addConnectedNode(baseEntity -> this.hasFinishedPlaying(ModelState.JUMP_START) && this.hasAnimation(ModelState.JUMP), jumpLoop);
        jumpLoop.setEntryAction(baseEntity -> this.playState(ModelState.JUMP));
        jumpLoop.addForceConnectedNode(baseEntity -> !baseEntity.isAlive(), death);
        jumpLoop.addConnectedNode(baseEntity -> baseEntity.isFlying() && this.isWalking(this.activeModel) && this.hasAnimation(ModelState.FLY), fly);
        jumpLoop.addConnectedNode(baseEntity -> baseEntity.isFlying() && this.hasAnimation(ModelState.HOVER), hover);
        jumpLoop.addConnectedNode(baseEntity -> !baseEntity.isJumping() && this.hasAnimation(ModelState.JUMP_END), jumpEnd);
        jumpLoop.addConnectedNode(baseEntity -> !baseEntity.isJumping() && this.isWalking(this.activeModel), walk);
        jumpLoop.addConnectedNode(baseEntity -> !baseEntity.isJumping() && !this.isWalking(this.activeModel), idle);
        jumpLoop.setExitAction(baseEntity -> this.stopState(ModelState.JUMP));
        jumpEnd.setEntryAction(baseEntity -> this.playState(ModelState.JUMP_END));
        jumpEnd.addForceConnectedNode(baseEntity -> !baseEntity.isAlive(), death);
        jumpEnd.setCommonPredicate(baseEntity -> this.hasFinishedPlaying(ModelState.JUMP_END));
        jumpEnd.addConnectedNode(baseEntity -> baseEntity.isFlying() && this.isWalking(this.activeModel) && this.hasAnimation(ModelState.FLY), fly);
        jumpEnd.addConnectedNode(baseEntity -> baseEntity.isFlying() && this.hasAnimation(ModelState.HOVER), hover);
        jumpEnd.addConnectedNode(baseEntity -> baseEntity.isJumping() && this.hasAnimation(ModelState.JUMP_START), jumpStart);
        jumpEnd.addConnectedNode(baseEntity -> baseEntity.isStrafing() && this.hasAnimation(ModelState.STRAFE), strafe);
        jumpEnd.addConnectedNode(baseEntity -> this.isWalking(this.activeModel), walk);
        jumpEnd.addConnectedNode(baseEntity -> !this.isWalking(this.activeModel), idle);
        hover.setEntryAction(baseEntity -> this.playState(ModelState.HOVER));
        hover.addConnectedNode(baseEntity -> !baseEntity.isAlive(), death);
        hover.addConnectedNode(baseEntity -> baseEntity.isFlying() && this.isWalking(this.activeModel) && this.hasAnimation(ModelState.FLY), fly);
        hover.addConnectedNode(baseEntity -> !baseEntity.isFlying() && baseEntity.isStrafing() && this.hasAnimation(ModelState.STRAFE), strafe);
        hover.addConnectedNode(baseEntity -> !baseEntity.isFlying() && this.isWalking(this.activeModel), walk);
        hover.addConnectedNode(baseEntity -> !baseEntity.isFlying() && !this.isWalking(this.activeModel), idle);
        hover.setExitAction(baseEntity -> this.stopState(ModelState.HOVER));
        fly.setEntryAction(baseEntity -> this.playState(ModelState.FLY));
        fly.addForceConnectedNode(baseEntity -> !baseEntity.isAlive(), death);
        fly.addConnectedNode(baseEntity -> baseEntity.isFlying() && !this.isWalking(this.activeModel) && this.hasAnimation(ModelState.HOVER), hover);
        fly.addConnectedNode(baseEntity -> !baseEntity.isFlying() && baseEntity.isStrafing() && this.hasAnimation(ModelState.STRAFE), strafe);
        fly.addConnectedNode(baseEntity -> !baseEntity.isFlying() && this.isWalking(this.activeModel), walk);
        fly.addConnectedNode(baseEntity -> !baseEntity.isFlying() && !this.isWalking(this.activeModel), idle);
        fly.setExitAction(baseEntity -> this.stopState(ModelState.FLY));
        death.setEntryAction(baseEntity -> {
            this.forceStopAllAnimations();
            this.playState(ModelState.DEATH);
        });
        this.stateMachine.setEntryNode(spawn);
    }

    private boolean hasAnimation(ModelState state) {
        return this.blueprint.getAnimations().containsKey(state.getString());
    }

    private boolean hasFinishedPlaying(ModelState state) {
        IAnimationProperty animation = this.getAnimation(state.getString());
        return animation == null || animation.getPhase() == IAnimationProperty.Phase.LERPOUT;
    }

    private void stopState(ModelState state) {
        AnimationHandler.DefaultProperty defProperty = this.getDefaultProperty(state);
        this.stopAnimation(defProperty.getAnimation());
    }

    @Override
    public void prepare() {
        this.stateMachine.execute(this.activeModel.getModeledEntity().getBase());
        this.firstSpawn = false;
        this.updatedProperties.clear();
        this.updatedProperties.putAll(this.properties);
        for (String name : this.blueprint.getAnimations().keySet()) {
            IAnimationProperty property = this.updatedProperties.get(name);
            if (property == null || property.update()) continue;
            this.updatedProperties.remove(name);
            this.forceStopAnimation(name);
        }
    }

    @Override
    public void updateBone(ModelBone bone) {
        bone.setHasGlobalRotation(false);
        KeyframeTypeRegistry registry = ModelEngineAPI.getAPI().getKeyframeTypeRegistry();
        for (String typeName : registry.getKeys()) {
            KeyframeType type = (KeyframeType)registry.get(typeName);
            if (type.isGlobal()) continue;
            Stack<IAnimationProperty> stack = this.getUpdateStack(type, bone);
            while (!stack.isEmpty()) {
                IAnimationProperty property = stack.pop();
                Timeline timeline = property.getBlueprintAnimation().getTimelines().get(bone.getBlueprintBone().getUuid());
                if (timeline != null && timeline.isGlobalRotation()) {
                    bone.setHasGlobalRotation(true);
                }
                type.updateBone(IPriorityHandler.class, this, bone, property);
            }
        }
    }

    @Override
    public boolean hasFinishedAllAnimations() {
        for (IAnimationProperty property : this.properties.values()) {
            if (property.isFinished()) continue;
            return false;
        }
        return true;
    }

    @Override
    public void setDefaultProperty(AnimationHandler.DefaultProperty defaultProperty) {
        this.defaultProperties.put(defaultProperty.getState(), defaultProperty);
    }

    @Override
    public AnimationHandler.DefaultProperty getDefaultProperty(ModelState state) {
        return this.defaultProperties.get(state);
    }

    private Stack<IAnimationProperty> getUpdateStack(KeyframeType<?, ?> type, ModelBone bone) {
        Stack<IAnimationProperty> stack = new Stack<IAnimationProperty>();
        UUID boneUuid = bone.getBlueprintBone().getUuid();
        for (String name : this.blueprint.getAnimationDescendingPriority()) {
            IAnimationProperty property = this.updatedProperties.get(name);
            if (property == null) continue;
            stack.push(property);
            if (!property.isOverride() || !property.containsKeyframe(type, boneUuid) || property.getPhase() != IAnimationProperty.Phase.PLAY) continue;
            break;
        }
        return stack;
    }

    @Override
    public void tickGlobal() {
        for (String name : this.blueprint.getAnimations().keySet()) {
            IAnimationProperty property = this.properties.get(name);
            if (property == null) continue;
            KeyframeTypeRegistry registry = ModelEngineAPI.getAPI().getKeyframeTypeRegistry();
            for (String typeName : registry.getKeys()) {
                KeyframeType type = (KeyframeType)registry.get(typeName);
                if (!type.isGlobal()) continue;
                type.updateModel(IPriorityHandler.class, this, property);
            }
        }
    }

    @Override
    public void forEachProperty(BiConsumer<String, IAnimationProperty> consumer) {
        this.properties.forEach(consumer);
    }

    @Override
    @Nullable
    public IAnimationProperty getAnimation(String animation) {
        return this.properties.get(animation);
    }

    @Override
    public Map<String, IAnimationProperty> getAnimations() {
        return ImmutableMap.copyOf(this.properties);
    }

    @Override
    @Nullable
    public IAnimationProperty playAnimation(String animation, double lerpIn, double lerpOut, double speed, boolean force) {
        BlueprintAnimation blueprintAnimation = this.blueprint.getAnimations().get(animation);
        if (blueprintAnimation == null) {
            return null;
        }
        SimpleProperty property = new SimpleProperty(this.activeModel, blueprintAnimation, lerpIn, lerpOut, speed);
        return this.playAnimation(property, force) ? property : null;
    }

    @Override
    public boolean playAnimation(IAnimationProperty property, boolean force) {
        AnimationPlayEvent event = new AnimationPlayEvent(this.activeModel, property);
        ModelEngineAPI.callEvent(event);
        if (event.isCancelled()) {
            return false;
        }
        String name = property.getName();
        if (!this.properties.containsKey(name)) {
            this.properties.put(name, property);
            return true;
        }
        IAnimationProperty old = this.properties.get(name);
        if (force || old.canReplace()) {
            this.properties.put(name, property);
            return true;
        }
        return false;
    }

    @Override
    public void playState(ModelState state) {
        IAnimationProperty property;
        AnimationHandler.DefaultProperty defProperty = this.getDefaultProperty(state);
        IAnimationProperty iAnimationProperty = property = this.firstSpawn ? defProperty.build(this.activeModel, 0.0, defProperty.getLerpOut(), defProperty.getSpeed()) : defProperty.build(this.activeModel);
        if (property != null) {
            property.setForceLoopMode(state.getLoopMode());
            property.setForceOverride(state.getOverride());
            this.playAnimation(property, false);
        }
    }

    @Override
    public boolean isPlayingAnimation(String animation) {
        return this.properties.containsKey(animation);
    }

    @Override
    public void stopAnimation(String animation) {
        IAnimationProperty property = this.properties.get(animation);
        if (property != null) {
            if (property.getLerpOut() > (double)1.0E-5f) {
                property.stop();
            } else {
                this.forceStopAnimation(animation);
            }
        }
    }

    @Override
    public void forceStopAnimation(String animation) {
        IAnimationProperty property = this.properties.remove(animation);
        if (property != null) {
            SimpleProperty simpleProperty;
            AnimationEndEvent event = new AnimationEndEvent(this.activeModel, property);
            ModelEngineAPI.callEvent(event);
            if (property instanceof SimpleProperty && (simpleProperty = (SimpleProperty)property).getOnEndTask() != null) {
                simpleProperty.getOnEndTask().run();
            }
        }
    }

    @Override
    public void forceStopAllAnimations() {
        for (IAnimationProperty property : this.properties.values()) {
            SimpleProperty simpleProperty;
            AnimationEndEvent event = new AnimationEndEvent(this.activeModel, property);
            ModelEngineAPI.callEvent(event);
            if (!(property instanceof SimpleProperty) || (simpleProperty = (SimpleProperty)property).getOnEndTask() == null) continue;
            simpleProperty.getOnEndTask().run();
        }
        this.properties.clear();
    }

    @Override
    public void save(SavedData data) {
        IPriorityHandler.super.save(data);
        SavedData animationData = new SavedData();
        this.getAnimations().forEach((key, property) -> property.save().ifPresent(propertyData -> animationData.putData((String)key, (SavedData)propertyData)));
        data.putData("animations", animationData);
    }

    @Override
    public void load(SavedData data) {
        IPriorityHandler.super.load(data);
        AnimationPropertyRegistry registry = ModelEngineAPI.getAnimationPropertyRegistry();
        data.getData("animations").ifPresent(animationData -> {
            for (String key : animationData.keySet()) {
                animationData.getData(key).ifPresent(propertyData -> {
                    IAnimationProperty property = registry.createAnimationProperty(this, (SavedData)propertyData);
                    this.playAnimation(property, true);
                });
            }
        });
    }

    public static PriorityHandler create(ActiveModel model, SavedData data) {
        PriorityHandler handler = new PriorityHandler(model);
        handler.load(data);
        return handler;
    }

    @Override
    @Generated
    public ActiveModel getActiveModel() {
        return this.activeModel;
    }
}

