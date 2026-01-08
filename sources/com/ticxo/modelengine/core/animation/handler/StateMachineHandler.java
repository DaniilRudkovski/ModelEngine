/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  lombok.Generated
 *  org.jetbrains.annotations.Nullable
 */
package com.ticxo.modelengine.core.animation.handler;

import com.google.common.collect.Maps;
import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.animation.AnimationPropertyRegistry;
import com.ticxo.modelengine.api.animation.BlueprintAnimation;
import com.ticxo.modelengine.api.animation.ModelState;
import com.ticxo.modelengine.api.animation.Timeline;
import com.ticxo.modelengine.api.animation.handler.AnimationHandler;
import com.ticxo.modelengine.api.animation.handler.IStateMachineHandler;
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
import com.ticxo.modelengine.api.utils.config.ConfigProperty;
import com.ticxo.modelengine.api.utils.data.io.DataIO;
import com.ticxo.modelengine.api.utils.data.io.SavedData;
import com.ticxo.modelengine.api.utils.state.StateMachine;
import com.ticxo.modelengine.api.utils.state.StateNode;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.Stack;
import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Supplier;
import lombok.Generated;
import org.jetbrains.annotations.Nullable;

public class StateMachineHandler
implements IStateMachineHandler {
    private static int DEFAULT_PRIORITY;
    private final ActiveModel activeModel;
    private final ModelBlueprint blueprint;
    private final Map<ModelState, AnimationHandler.DefaultProperty> defaultProperties = Maps.newConcurrentMap();
    private final TreeMap<Integer, AnimationStateMachine> stateMachines = new TreeMap();
    private final Queue<Runnable> actionQueue = new ConcurrentLinkedQueue<Runnable>();
    private boolean firstSpawn = true;

    public StateMachineHandler(ActiveModel activeModel) {
        this.activeModel = activeModel;
        this.blueprint = activeModel.getBlueprint();
        this.setDefaultProperty(new AnimationHandler.DefaultProperty(ModelState.IDLE, 0.25, 0.0, 1.0));
        this.setDefaultProperty(new AnimationHandler.DefaultProperty(ModelState.WALK, 0.25, 0.0, 1.0));
        this.setDefaultProperty(new AnimationHandler.DefaultProperty(ModelState.STRAFE, 0.25, 0.0, 1.0));
        this.setDefaultProperty(new AnimationHandler.DefaultProperty(ModelState.JUMP_START, 0.0, 0.0, 1.0));
        this.setDefaultProperty(new AnimationHandler.DefaultProperty(ModelState.JUMP, 0.0, 0.0, 1.0));
        this.setDefaultProperty(new AnimationHandler.DefaultProperty(ModelState.JUMP_END, 0.0, 0.0, 1.0));
        this.setDefaultProperty(new AnimationHandler.DefaultProperty(ModelState.HOVER, 0.25, 0.0, 1.0));
        this.setDefaultProperty(new AnimationHandler.DefaultProperty(ModelState.FLY, 0.25, 0.0, 1.0));
        this.setDefaultProperty(new AnimationHandler.DefaultProperty(ModelState.SPAWN, 0.0, 0.0, 1.0));
        this.setDefaultProperty(new AnimationHandler.DefaultProperty(ModelState.DEATH, 0.0, 0.0, 1.0));
        this.configureAnimation();
    }

    private void configureAnimation() {
        AnimationStateMachine stateMachine = new AnimationStateMachine(false);
        StateNode<BaseEntity<?>> root = stateMachine.getRootNode();
        StateNode<BaseEntity<?>> spawn = stateMachine.createAnimationNode(() -> this.createStateProperty(ModelState.SPAWN));
        StateNode<BaseEntity<?>> idle = stateMachine.createAnimationNode(() -> this.createStateProperty(ModelState.IDLE));
        StateNode<BaseEntity<?>> walk = stateMachine.createAnimationNode(() -> this.createStateProperty(ModelState.WALK));
        StateNode<BaseEntity<?>> strafe = stateMachine.createAnimationNode(() -> this.createStateProperty(ModelState.STRAFE));
        StateNode<BaseEntity<?>> jumpStart = stateMachine.createAnimationNode(() -> this.createStateProperty(ModelState.JUMP_START));
        StateNode<BaseEntity<?>> jumpLoop = stateMachine.createAnimationNode(() -> this.createStateProperty(ModelState.JUMP));
        StateNode<BaseEntity<?>> jumpEnd = stateMachine.createAnimationNode(() -> this.createStateProperty(ModelState.JUMP_END));
        StateNode<BaseEntity<?>> hover = stateMachine.createAnimationNode(() -> this.createStateProperty(ModelState.HOVER));
        StateNode<BaseEntity<?>> fly = stateMachine.createAnimationNode(() -> this.createStateProperty(ModelState.FLY));
        StateNode<BaseEntity<?>> death = stateMachine.createAnimationNode(() -> {
            this.forceStopAllAnimations();
            return this.createStateProperty(ModelState.DEATH);
        });
        root.addConnectedNode(baseEntity -> !baseEntity.isAlive(), death);
        root.addConnectedNode(baseEntity -> this.hasAnimation(ModelState.SPAWN), spawn);
        root.addConnectedNode(baseEntity -> baseEntity.isFlying() && this.isWalking(this.activeModel) && this.hasAnimation(ModelState.FLY), fly);
        root.addConnectedNode(baseEntity -> baseEntity.isFlying() && this.hasAnimation(ModelState.HOVER), hover);
        root.addConnectedNode(baseEntity -> baseEntity.isJumping() && this.hasAnimation(ModelState.JUMP_START), jumpStart);
        root.addConnectedNode(baseEntity -> baseEntity.isJumping() && this.hasAnimation(ModelState.JUMP), jumpLoop);
        root.addConnectedNode(baseEntity -> baseEntity.isStrafing() && this.hasAnimation(ModelState.STRAFE), strafe);
        root.addConnectedNode(baseEntity -> this.isWalking(this.activeModel), walk);
        root.addConnectedNode(baseEntity -> !this.isWalking(this.activeModel), idle);
        spawn.addConnectedNode(baseEntity -> !baseEntity.isAlive(), death);
        spawn.setCommonPredicate(baseEntity -> stateMachine.hasFinishedPlaying(ModelState.SPAWN));
        spawn.addConnectedNode(baseEntity -> baseEntity.isFlying() && this.isWalking(this.activeModel) && this.hasAnimation(ModelState.FLY), fly);
        spawn.addConnectedNode(baseEntity -> baseEntity.isFlying() && this.hasAnimation(ModelState.HOVER), hover);
        spawn.addConnectedNode(baseEntity -> baseEntity.isJumping() && this.hasAnimation(ModelState.JUMP_START), jumpStart);
        spawn.addConnectedNode(baseEntity -> baseEntity.isJumping() && this.hasAnimation(ModelState.JUMP), jumpLoop);
        spawn.addConnectedNode(baseEntity -> baseEntity.isStrafing() && this.hasAnimation(ModelState.STRAFE), strafe);
        spawn.addConnectedNode(baseEntity -> this.isWalking(this.activeModel), walk);
        spawn.addConnectedNode(baseEntity -> !this.isWalking(this.activeModel), idle);
        idle.addConnectedNode(baseEntity -> !baseEntity.isAlive(), death);
        idle.addConnectedNode(baseEntity -> baseEntity.isFlying() && this.isWalking(this.activeModel) && this.hasAnimation(ModelState.FLY), fly);
        idle.addConnectedNode(baseEntity -> baseEntity.isFlying() && this.hasAnimation(ModelState.HOVER), hover);
        idle.addConnectedNode(baseEntity -> baseEntity.isJumping() && this.hasAnimation(ModelState.JUMP_START), jumpStart);
        idle.addConnectedNode(baseEntity -> baseEntity.isJumping() && this.hasAnimation(ModelState.JUMP), jumpLoop);
        idle.addConnectedNode(baseEntity -> baseEntity.isStrafing() && this.hasAnimation(ModelState.STRAFE), strafe);
        idle.addConnectedNode(baseEntity -> this.isWalking(this.activeModel), walk);
        walk.addConnectedNode(baseEntity -> !baseEntity.isAlive(), death);
        walk.addConnectedNode(baseEntity -> baseEntity.isFlying() && this.isWalking(this.activeModel) && this.hasAnimation(ModelState.FLY), fly);
        walk.addConnectedNode(baseEntity -> baseEntity.isFlying() && this.hasAnimation(ModelState.HOVER), hover);
        walk.addConnectedNode(baseEntity -> baseEntity.isJumping() && this.hasAnimation(ModelState.JUMP_START), jumpStart);
        walk.addConnectedNode(baseEntity -> baseEntity.isJumping() && this.hasAnimation(ModelState.JUMP), jumpLoop);
        walk.addConnectedNode(baseEntity -> baseEntity.isStrafing() && this.hasAnimation(ModelState.STRAFE), strafe);
        walk.addConnectedNode(baseEntity -> !this.isWalking(this.activeModel), idle);
        strafe.addConnectedNode(baseEntity -> !baseEntity.isAlive(), death);
        strafe.addConnectedNode(baseEntity -> baseEntity.isFlying() && this.isWalking(this.activeModel) && this.hasAnimation(ModelState.FLY), fly);
        strafe.addConnectedNode(baseEntity -> baseEntity.isFlying() && this.hasAnimation(ModelState.HOVER), hover);
        strafe.addConnectedNode(baseEntity -> baseEntity.isJumping() && this.hasAnimation(ModelState.JUMP_START), jumpStart);
        strafe.addConnectedNode(baseEntity -> baseEntity.isJumping() && this.hasAnimation(ModelState.JUMP), jumpLoop);
        strafe.addConnectedNode(baseEntity -> this.isWalking(this.activeModel), walk);
        strafe.addConnectedNode(baseEntity -> !this.isWalking(this.activeModel), idle);
        jumpStart.addForceConnectedNode(baseEntity -> !baseEntity.isAlive(), death);
        jumpStart.setCommonPredicate(baseEntity -> stateMachine.hasFinishedPlaying(ModelState.JUMP_START));
        jumpStart.addConnectedNode(baseEntity -> baseEntity.isFlying() && this.isWalking(this.activeModel) && this.hasAnimation(ModelState.FLY), fly);
        jumpStart.addConnectedNode(baseEntity -> baseEntity.isFlying() && this.hasAnimation(ModelState.HOVER), hover);
        jumpStart.addConnectedNode(baseEntity -> !baseEntity.isJumping() && this.hasAnimation(ModelState.JUMP_END), jumpEnd);
        jumpStart.addConnectedNode(baseEntity -> !baseEntity.isJumping() && this.isWalking(this.activeModel), walk);
        jumpStart.addConnectedNode(baseEntity -> !baseEntity.isJumping() && !this.isWalking(this.activeModel), idle);
        jumpLoop.addConnectedNode(baseEntity -> !baseEntity.isAlive(), death);
        jumpLoop.addConnectedNode(baseEntity -> baseEntity.isFlying() && this.isWalking(this.activeModel) && this.hasAnimation(ModelState.FLY), fly);
        jumpLoop.addConnectedNode(baseEntity -> baseEntity.isFlying() && this.hasAnimation(ModelState.HOVER), hover);
        jumpLoop.addConnectedNode(baseEntity -> !baseEntity.isJumping() && this.hasAnimation(ModelState.JUMP_END), jumpEnd);
        jumpLoop.addConnectedNode(baseEntity -> !baseEntity.isJumping() && this.isWalking(this.activeModel), walk);
        jumpLoop.addConnectedNode(baseEntity -> !baseEntity.isJumping() && !this.isWalking(this.activeModel), idle);
        jumpEnd.addForceConnectedNode(baseEntity -> !baseEntity.isAlive(), death);
        jumpEnd.setCommonPredicate(baseEntity -> stateMachine.hasFinishedPlaying(ModelState.JUMP_END));
        jumpEnd.addConnectedNode(baseEntity -> baseEntity.isFlying() && this.isWalking(this.activeModel) && this.hasAnimation(ModelState.FLY), fly);
        jumpEnd.addConnectedNode(baseEntity -> baseEntity.isFlying() && this.hasAnimation(ModelState.HOVER), hover);
        jumpEnd.addConnectedNode(baseEntity -> baseEntity.isJumping() && this.hasAnimation(ModelState.JUMP_START), jumpStart);
        jumpEnd.addConnectedNode(baseEntity -> baseEntity.isStrafing() && this.hasAnimation(ModelState.STRAFE), strafe);
        jumpEnd.addConnectedNode(baseEntity -> this.isWalking(this.activeModel), walk);
        jumpEnd.addConnectedNode(baseEntity -> !this.isWalking(this.activeModel), idle);
        hover.addConnectedNode(baseEntity -> !baseEntity.isAlive(), death);
        hover.addConnectedNode(baseEntity -> baseEntity.isFlying() && this.isWalking(this.activeModel) && this.hasAnimation(ModelState.FLY), fly);
        hover.addConnectedNode(baseEntity -> !baseEntity.isFlying() && baseEntity.isStrafing() && this.hasAnimation(ModelState.STRAFE), strafe);
        hover.addConnectedNode(baseEntity -> !baseEntity.isFlying() && this.isWalking(this.activeModel), walk);
        hover.addConnectedNode(baseEntity -> !baseEntity.isFlying() && !this.isWalking(this.activeModel), idle);
        fly.addConnectedNode(baseEntity -> !baseEntity.isAlive(), death);
        fly.addConnectedNode(baseEntity -> baseEntity.isFlying() && !this.isWalking(this.activeModel) && this.hasAnimation(ModelState.HOVER), hover);
        fly.addConnectedNode(baseEntity -> !baseEntity.isFlying() && baseEntity.isStrafing() && this.hasAnimation(ModelState.STRAFE), strafe);
        fly.addConnectedNode(baseEntity -> !baseEntity.isFlying() && this.isWalking(this.activeModel), walk);
        fly.addConnectedNode(baseEntity -> !baseEntity.isFlying() && !this.isWalking(this.activeModel), idle);
        this.stateMachines.put(DEFAULT_PRIORITY, stateMachine);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void prepare() {
        while (!this.actionQueue.isEmpty()) {
            this.actionQueue.poll().run();
        }
        TreeMap<Integer, AnimationStateMachine> treeMap = this.stateMachines;
        synchronized (treeMap) {
            this.stateMachines.values().forEach(machine -> machine.execute(this.activeModel.getModeledEntity().getBase()));
        }
        this.firstSpawn = false;
    }

    @Override
    public void updateBone(ModelBone bone) {
        bone.setHasGlobalRotation(false);
        KeyframeTypeRegistry registry = ModelEngineAPI.getAPI().getKeyframeTypeRegistry();
        for (String typeName : registry.getKeys()) {
            KeyframeType type = (KeyframeType)registry.get(typeName);
            if (type.isGlobal()) continue;
            Stack<AnimationStateMachine> stack = this.getUpdateStack(type, bone);
            while (!stack.isEmpty()) {
                AnimationStateMachine stateMachine = stack.pop();
                IAnimationProperty currentProperty = stateMachine.currentAnimation;
                IAnimationProperty lastProperty = stateMachine.lastAnimation;
                bone.setHasGlobalRotation(this.isGlobalRotation(currentProperty, bone) || this.isGlobalRotation(lastProperty, bone));
                type.updateBone(IStateMachineHandler.class, this, bone, currentProperty, lastProperty);
            }
        }
    }

    private boolean isGlobalRotation(IAnimationProperty property, ModelBone bone) {
        return Optional.ofNullable(property).map(IAnimationProperty::getBlueprintAnimation).map(BlueprintAnimation::getTimelines).map(map -> (Timeline)map.get(bone.getBlueprintBone().getUuid())).map(Timeline::isGlobalRotation).orElse(false);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean hasFinishedAllAnimations() {
        TreeMap<Integer, AnimationStateMachine> treeMap = this.stateMachines;
        synchronized (treeMap) {
            for (AnimationStateMachine machine : this.stateMachines.values()) {
                if (machine.currentAnimation == null || machine.currentAnimation.isFinished() || machine.currentAnimation.isIgnoreDeath()) continue;
                return false;
            }
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

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void tickGlobal() {
        TreeMap<Integer, AnimationStateMachine> treeMap = this.stateMachines;
        synchronized (treeMap) {
            for (AnimationStateMachine machine : this.stateMachines.values()) {
                if (machine.currentAnimation == null) continue;
                KeyframeTypeRegistry registry = ModelEngineAPI.getAPI().getKeyframeTypeRegistry();
                for (String typeName : registry.getKeys()) {
                    KeyframeType type = (KeyframeType)registry.get(typeName);
                    if (!type.isGlobal()) continue;
                    type.updateModel(IStateMachineHandler.class, this, machine.currentAnimation, machine.lastAnimation);
                }
            }
        }
    }

    @Override
    @Nullable
    public IAnimationProperty playAnimation(String animation, double lerpIn, double lerpOut, double speed, boolean force) {
        return this.playAnimation(1, animation, lerpIn, lerpOut, speed, force);
    }

    @Override
    public boolean playAnimation(IAnimationProperty property, boolean force) {
        return this.playAnimation(1, property, force);
    }

    private Stack<AnimationStateMachine> getUpdateStack(KeyframeType<?, ?> type, ModelBone bone) {
        Stack<AnimationStateMachine> stack = new Stack<AnimationStateMachine>();
        if (this.stateMachines.isEmpty()) {
            return stack;
        }
        UUID boneUuid = bone.getBlueprintBone().getUuid();
        Map.Entry<Integer, AnimationStateMachine> entry = this.stateMachines.lastEntry();
        while (entry != null) {
            AnimationStateMachine machine = entry.getValue();
            IAnimationProperty currentProperty = machine.currentAnimation;
            IAnimationProperty lastProperty = machine.lastAnimation;
            if (currentProperty != null) {
                stack.push(machine);
                if (this.isLastProperty(currentProperty, type, boneUuid) && (lastProperty == null || this.isLastProperty(lastProperty, type, boneUuid))) break;
            }
            entry = this.stateMachines.lowerEntry(entry.getKey());
        }
        return stack;
    }

    private boolean isLastProperty(IAnimationProperty property, KeyframeType<?, ?> type, UUID boneUuid) {
        return property.isOverride() && property.containsKeyframe(type, boneUuid) && property.getPhase() == IAnimationProperty.Phase.PLAY;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    @Nullable
    public IAnimationProperty getAnimation(String animation) {
        TreeMap<Integer, AnimationStateMachine> treeMap = this.stateMachines;
        synchronized (treeMap) {
            for (AnimationStateMachine machine : this.stateMachines.values()) {
                if (!machine.isPlaying(animation)) continue;
                return machine.currentAnimation;
            }
        }
        return null;
    }

    @Override
    public Map<String, IAnimationProperty> getAnimations() {
        HashMap<String, IAnimationProperty> map = new HashMap<String, IAnimationProperty>();
        for (Map.Entry<Integer, AnimationStateMachine> entry : this.stateMachines.entrySet()) {
            int id = entry.getKey();
            AnimationStateMachine stateMachine = entry.getValue();
            IAnimationProperty animation = stateMachine.getCurrentAnimation();
            if (animation == null) continue;
            map.put(id + ":" + animation.getName(), animation);
        }
        return map;
    }

    @Override
    @Nullable
    public IAnimationProperty getAnimation(int priority, ModelBlueprint source, String animation) {
        AnimationStateMachine machine = this.stateMachines.get(priority);
        if (machine != null && machine.isPlaying(source, animation)) {
            return machine.currentAnimation;
        }
        return null;
    }

    @Override
    @Nullable
    public IAnimationProperty playAnimation(int priority, ModelBlueprint source, String animation, double lerpIn, double lerpOut, double speed, boolean force) {
        BlueprintAnimation blueprintAnimation = source.getAnimations().get(animation);
        if (blueprintAnimation == null) {
            return null;
        }
        SimpleProperty property = new SimpleProperty(this.activeModel, blueprintAnimation, lerpIn, lerpOut, speed);
        return this.playAnimation(priority, property, force) ? property : null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean playAnimation(int priority, IAnimationProperty property, boolean force) {
        AnimationPlayEvent event = new AnimationPlayEvent(this.activeModel, property);
        ModelEngineAPI.callEvent(event);
        if (event.isCancelled()) {
            return false;
        }
        TreeMap<Integer, AnimationStateMachine> treeMap = this.stateMachines;
        synchronized (treeMap) {
            AnimationStateMachine machine = this.stateMachines.computeIfAbsent(priority, id -> new AnimationStateMachine(true));
            if (!force && machine.isPlaying(property.getName())) {
                return false;
            }
            StateNode<BaseEntity<?>> currentNode = machine.getCurrentNode();
            StateNode<BaseEntity<?>> node = machine.createAnimationNode(() -> property);
            node.addConnectedNode(baseEntity -> property.isEnded(), machine.getRootNode());
            currentNode.addForceConnectedNode(baseEntity -> true, node);
            return true;
        }
    }

    @Override
    public void refreshState(AnimationHandler.DefaultProperty property) {
        AnimationStateMachine machine = this.stateMachines.get(DEFAULT_PRIORITY);
        if (machine != null && machine.isPlaying(property.getAnimation())) {
            machine.forceReentry(this.activeModel.getModeledEntity().getBase());
        }
    }

    @Override
    public boolean isPlayingAnimation(String animation) {
        return this.getAnimation(animation) != null;
    }

    @Override
    public boolean isPlayingAnimation(int priority, ModelBlueprint source, String animation) {
        return this.getAnimation(priority, source, animation) != null;
    }

    @Override
    public void stopAnimation(String animation) {
        this.stateMachines.keySet().forEach(integer -> this.stopAnimation((int)integer, animation));
    }

    @Override
    public void stopAnimation(int priority, ModelBlueprint source, String animation) {
        AnimationStateMachine machine = this.stateMachines.get(priority);
        if (machine != null && machine.isPlaying(source, animation)) {
            if (machine.currentAnimation.getLerpOut() > (double)1.0E-5f) {
                machine.currentAnimation.stop();
            } else {
                machine.getCurrentNode().addForceConnectedNode(baseEntity -> true, machine.getRootNode());
            }
        }
    }

    @Override
    public void forceStopAnimation(String animation) {
        this.stateMachines.keySet().forEach(integer -> this.forceStopAnimation((int)integer, animation));
    }

    @Override
    public void forceStopAnimation(int priority, ModelBlueprint source, String animation) {
        AnimationStateMachine machine = this.stateMachines.get(priority);
        if (machine != null && machine.isPlaying(source, animation)) {
            machine.getCurrentNode().addForceConnectedNode(baseEntity -> true, machine.getRootNode());
        }
    }

    @Override
    public void forceStopAllAnimations() {
        this.actionQueue.add(() -> this.stateMachines.keySet().removeIf(key -> {
            if (key == DEFAULT_PRIORITY) {
                return false;
            }
            AnimationStateMachine machine = this.stateMachines.get(key);
            return machine.getCurrentAnimation() == null || !machine.getCurrentAnimation().isIgnoreDeath();
        }));
    }

    private boolean hasAnimation(ModelState state) {
        return this.blueprint.getAnimations().containsKey(state.getString());
    }

    @Nullable
    private IAnimationProperty createStateProperty(ModelState state) {
        IAnimationProperty property;
        AnimationHandler.DefaultProperty defProperty = this.getDefaultProperty(state);
        IAnimationProperty iAnimationProperty = property = this.firstSpawn ? defProperty.buildRef(this.activeModel, 0.0, defProperty.getLerpOut(), defProperty.getSpeed()) : defProperty.buildRef(this.activeModel);
        if (property != null) {
            property.setForceLoopMode(state.getLoopMode());
            property.setForceOverride(state.getOverride());
        }
        return property;
    }

    @Override
    public void save(SavedData data) {
        IStateMachineHandler.super.save(data);
        SavedData stateMachinesData = new SavedData();
        this.stateMachines.forEach((id, asm) -> asm.save().ifPresent(smd -> stateMachinesData.putData(Integer.toString(id), (SavedData)smd)));
        data.putData("state_machines", stateMachinesData);
    }

    @Override
    public void load(SavedData data) {
        IStateMachineHandler.super.load(data);
        data.getData("state_machines").ifPresent(stateMachinesData -> {
            for (String key : stateMachinesData.keySet()) {
                int id = Integer.parseInt(key);
                stateMachinesData.getData(key).ifPresent(smd -> {
                    AnimationStateMachine asm = new AnimationStateMachine(true);
                    asm.load((SavedData)smd);
                    this.stateMachines.put(id, asm);
                });
            }
        });
    }

    public static StateMachineHandler create(ActiveModel model, SavedData data) {
        StateMachineHandler handler = new StateMachineHandler(model);
        handler.load(data);
        return handler;
    }

    @Override
    @Generated
    public ActiveModel getActiveModel() {
        return this.activeModel;
    }

    @Override
    @Generated
    public ModelBlueprint getBlueprint() {
        return this.blueprint;
    }

    @Generated
    public TreeMap<Integer, AnimationStateMachine> getStateMachines() {
        return this.stateMachines;
    }

    static {
        ModelEngineAPI.getAPI().getConfigManager().registerReferenceUpdate(() -> {
            DEFAULT_PRIORITY = ConfigProperty.DEFAULT_PRIORITY.getInt();
        });
    }

    public class AnimationStateMachine
    extends StateMachine<BaseEntity<?>>
    implements DataIO {
        protected final boolean saved;
        protected StateNode<BaseEntity<?>> rootNode;
        @Nullable
        protected IAnimationProperty lastAnimation;
        @Nullable
        protected IAnimationProperty currentAnimation;

        @Override
        public StateNode<BaseEntity<?>> getCurrentNode() {
            return this.currentNode == null ? this.getRootNode() : this.currentNode;
        }

        public StateNode<BaseEntity<?>> getRootNode() {
            if (this.rootNode == null) {
                this.rootNode = new StateNode(this);
                this.rootNode.setEntryAction(baseEntity -> {
                    this.currentAnimation = null;
                });
                this.rootNode.setExitAction(baseEntity -> {
                    this.rootNode.clearForceConnectedNodes();
                    this.rootNode.clearConnectedNodes();
                    this.lastAnimation = this.currentAnimation;
                });
                this.setEntryNode(this.rootNode);
            }
            return this.rootNode;
        }

        public StateNode<BaseEntity<?>> createAnimationNode(Supplier<IAnimationProperty> propertySupplier) {
            StateNode node = new StateNode(this);
            node.setEntryAction(baseEntity -> {
                this.currentAnimation = (IAnimationProperty)propertySupplier.get();
            });
            node.setAction(baseEntity -> {
                if (this.currentAnimation != null) {
                    this.currentAnimation.update();
                }
                if (this.lastAnimation != null && this.lastAnimation != this.currentAnimation) {
                    this.lastAnimation.update();
                }
            });
            node.setExitAction(baseEntity -> {
                AnimationEndEvent event = new AnimationEndEvent(StateMachineHandler.this.activeModel, this.currentAnimation);
                ModelEngineAPI.callEvent(event);
                this.lastAnimation = this.currentAnimation;
            });
            return node;
        }

        public boolean hasFinishedPlaying(ModelState modelState) {
            return !this.isPlaying(StateMachineHandler.this.getDefaultProperty(modelState).getAnimation());
        }

        public boolean isPlaying(String animation) {
            return this.isPlaying(StateMachineHandler.this.blueprint, animation);
        }

        /*
         * Enabled aggressive block sorting
         */
        public boolean isPlaying(ModelBlueprint source, String animation) {
            if (this.currentAnimation == null) return false;
            if (this.currentAnimation.getBlueprintAnimation().getModelBlueprint() != source) return false;
            if (!this.currentAnimation.getName().equals(animation)) return false;
            if (this.currentAnimation.getPhase() == IAnimationProperty.Phase.LERPOUT) return false;
            switch (this.currentAnimation.getLoopMode()) {
                case ONCE: {
                    if (!(this.currentAnimation.getTime() < this.currentAnimation.getBlueprintAnimation().getLength())) return false;
                    break;
                }
                case LOOP: {
                    if (!(this.currentAnimation.getTime() < this.currentAnimation.getBlueprintAnimation().getLength() + 0.05)) return false;
                }
            }
            return true;
        }

        public boolean isPlayingOrEnding(String animation) {
            return this.isPlayingOrEnding(StateMachineHandler.this.blueprint, animation);
        }

        public boolean isPlayingOrEnding(ModelBlueprint source, String animation) {
            return this.currentAnimation != null && this.currentAnimation.getBlueprintAnimation().getModelBlueprint() == source && this.currentAnimation.getName().equals(animation) && !this.currentAnimation.isEnded();
        }

        public void forceReentry(BaseEntity<?> base) {
            this.getCurrentNode().acceptEntry(base);
        }

        @Override
        public void save(SavedData data) {
            if (!this.saved) {
                return;
            }
            if (this.lastAnimation != null) {
                this.lastAnimation.save().ifPresent(data1 -> data.putData("last_animation", (SavedData)data1));
            }
            if (this.currentAnimation != null) {
                this.currentAnimation.save().ifPresent(data1 -> data.putData("current_animation", (SavedData)data1));
            }
        }

        @Override
        public void load(SavedData data) {
            AnimationPropertyRegistry registry = ModelEngineAPI.getAnimationPropertyRegistry();
            data.getData("last_animation").ifPresent(property -> {
                this.lastAnimation = registry.createAnimationProperty(StateMachineHandler.this, (SavedData)property);
            });
            data.getData("current_animation").ifPresent(property -> {
                this.currentAnimation = registry.createAnimationProperty(StateMachineHandler.this, (SavedData)property);
            });
            StateNode<BaseEntity<?>> currentNode = this.getCurrentNode();
            if (this.currentAnimation != null) {
                StateNode<BaseEntity<?>> node = this.createAnimationNode(() -> this.currentAnimation);
                node.addConnectedNode(baseEntity -> this.currentAnimation.isEnded(), this.getRootNode());
                currentNode.addForceConnectedNode(baseEntity -> true, node);
            }
        }

        @Generated
        public AnimationStateMachine(boolean saved) {
            this.saved = saved;
        }

        @Nullable
        @Generated
        public IAnimationProperty getLastAnimation() {
            return this.lastAnimation;
        }

        @Generated
        public void setLastAnimation(@Nullable IAnimationProperty lastAnimation) {
            this.lastAnimation = lastAnimation;
        }

        @Nullable
        @Generated
        public IAnimationProperty getCurrentAnimation() {
            return this.currentAnimation;
        }

        @Generated
        public void setCurrentAnimation(@Nullable IAnimationProperty currentAnimation) {
            this.currentAnimation = currentAnimation;
        }
    }
}

