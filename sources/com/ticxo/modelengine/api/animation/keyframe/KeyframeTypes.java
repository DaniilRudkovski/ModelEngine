/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 */
package com.ticxo.modelengine.api.animation.keyframe;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.animation.handler.AnimationHandler;
import com.ticxo.modelengine.api.animation.handler.IPriorityHandler;
import com.ticxo.modelengine.api.animation.handler.IStateMachineHandler;
import com.ticxo.modelengine.api.animation.interpolator.KeyframeInterpolator;
import com.ticxo.modelengine.api.animation.interpolator.PrePostInterpolator;
import com.ticxo.modelengine.api.animation.interpolator.ScriptInterpolator;
import com.ticxo.modelengine.api.animation.keyframe.KeyframeType;
import com.ticxo.modelengine.api.animation.keyframe.type.ScriptKeyframe;
import com.ticxo.modelengine.api.animation.keyframe.type.VectorKeyframe;
import com.ticxo.modelengine.api.animation.property.IAnimationProperty;
import com.ticxo.modelengine.api.animation.script.ScriptReader;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.utils.StepFlag;
import com.ticxo.modelengine.api.utils.math.SafeTransform;
import com.ticxo.modelengine.api.utils.math.TMath;
import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public final class KeyframeTypes {
    public static final KeyframeType<VectorKeyframe, Vector3f> POSITION = KeyframeType.Builder.of("position", VectorKeyframe::new).interpolator(timeline -> new PrePostInterpolator<VectorKeyframe, Vector3f>((ctx, prev, next, ratio) -> KeyframeTypes.standard(ctx, prev, next, ratio, StepFlag.POSITION), (ctx, vectorKeyframe) -> KeyframeTypes.markStep(ctx, vectorKeyframe, StepFlag.POSITION), KeyframeTypes::derive)).registerBoneUpdater(IPriorityHandler.class, (handler, bone, data) -> {
        IAnimationProperty property = (IAnimationProperty)data[0];
        SafeTransform transform = bone.getLocalTransform();
        Vector3f output = transform.getPosition();
        if (bone.isRootBone()) {
            if (property.getPhase() == IAnimationProperty.Phase.PLAY) {
                Vector3f vel = property.getVelocityFrame(bone);
                if (vel == null && property.isEmptyZero()) {
                    vel = new Vector3f();
                }
                if (vel != null) {
                    if (property.isOverride()) {
                        output.set((Vector3fc)bone.getBlueprintBone().getLocalPosition());
                    }
                    output.add((Vector3fc)vel);
                }
                transform.setPosition(output);
                Vector3f val = property.getPositionFrame(bone);
                bone.setBoneOnGround(val == null || TMath.isSimilar(val.y, 0.0f));
            }
        } else {
            Vector3f val = property.getPositionFrame(bone);
            if (!property.isOverride()) {
                if (val == null) {
                    val = new Vector3f();
                }
                switch (property.getPhase()) {
                    case LERPIN: {
                        val = TMath.lerp(new Vector3f(), val, property.getLerpInRatio());
                        break;
                    }
                    case LERPOUT: {
                        val = TMath.lerp(val, new Vector3f(), property.getLerpOutRatio());
                    }
                }
                output.add((Vector3fc)val);
            } else {
                if (val == null && property.isEmptyZero()) {
                    val = new Vector3f();
                }
                if (val != null) {
                    Vector3f local = bone.getBlueprintBone().getLocalPosition();
                    switch (property.getPhase()) {
                        case LERPIN: {
                            output.set((Vector3fc)TMath.lerp(output, val.add((Vector3fc)local), property.getLerpInRatio()));
                            break;
                        }
                        case PLAY: {
                            output.set((Vector3fc)local).add((Vector3fc)val);
                            break;
                        }
                        case LERPOUT: {
                            output.set((Vector3fc)TMath.lerp(val.add((Vector3fc)local), output, property.getLerpOutRatio()));
                        }
                    }
                }
            }
            transform.setPosition(output);
        }
    }).registerBoneUpdater(IStateMachineHandler.class, (handler, bone, data) -> {
        IAnimationProperty currProperty = (IAnimationProperty)data[0];
        if (currProperty == null) {
            return;
        }
        SafeTransform transform = bone.getLocalTransform();
        if (bone.isRootBone()) {
            Vector3f output = transform.getPosition();
            if (currProperty.getPhase() == IAnimationProperty.Phase.PLAY) {
                Vector3f vel = currProperty.getVelocityFrame(bone);
                if (vel == null && currProperty.isEmptyZero()) {
                    vel = new Vector3f();
                }
                if (vel != null) {
                    if (currProperty.isOverride()) {
                        output.set((Vector3fc)bone.getBlueprintBone().getLocalPosition());
                    }
                    output.add((Vector3fc)vel);
                }
                transform.setPosition(output);
                Vector3f val = currProperty.getPositionFrame(bone);
                bone.setBoneOnGround(val == null || TMath.isSimilar(val.y, 0.0f));
            }
        } else {
            Vector3f currVal = new Vector3f((Vector3fc)transform.getPosition());
            Vector3f val = currProperty.getPositionFrame(bone);
            if (val == null && currProperty.isEmptyZero()) {
                val = new Vector3f();
            }
            if (val != null) {
                if (currProperty.isOverride()) {
                    currVal.set((Vector3fc)bone.getBlueprintBone().getLocalPosition());
                }
                currVal.add((Vector3fc)val);
            }
            switch (currProperty.getPhase()) {
                case PLAY: {
                    transform.setPosition(currVal);
                    return;
                }
                case LERPOUT: {
                    transform.setPosition(TMath.lerp(currVal, transform.getPosition(), currProperty.getLerpOutRatio()));
                    return;
                }
            }
            IAnimationProperty lastProperty = (IAnimationProperty)data[1];
            if (lastProperty == null) {
                transform.setPosition(TMath.lerp(transform.getPosition(), currVal, currProperty.getLerpInRatio()));
                return;
            }
            Vector3f lastVal = new Vector3f((Vector3fc)transform.getPosition());
            Vector3f val2 = lastProperty.getPositionFrame(bone);
            if (val2 == null && lastProperty.isEmptyZero()) {
                val2 = new Vector3f();
            }
            if (val2 != null) {
                if (lastProperty.isOverride()) {
                    lastVal.set((Vector3fc)bone.getBlueprintBone().getLocalPosition());
                }
                lastVal.add((Vector3fc)val2);
            }
            transform.setPosition(TMath.lerp(lastVal, currVal, currProperty.getLerpInRatio()));
        }
    }).build();
    public static final KeyframeType<VectorKeyframe, Vector3f> ROTATION = KeyframeType.Builder.of("rotation", VectorKeyframe::new).interpolator(timeline -> new PrePostInterpolator<VectorKeyframe, Vector3f>((ctx, prev, next, ratio) -> KeyframeTypes.standard(ctx, prev, next, ratio, StepFlag.ROTATION), (ctx, vectorKeyframe) -> KeyframeTypes.markStep(ctx, vectorKeyframe, StepFlag.ROTATION), KeyframeTypes::derive)).registerBoneUpdater(IPriorityHandler.class, (handler, bone, data) -> {
        IAnimationProperty property = (IAnimationProperty)data[0];
        SafeTransform transform = bone.getLocalTransform();
        Vector3f output = transform.getLeftEuler();
        Vector3f val = property.getRotationFrame(bone);
        if (!property.isOverride()) {
            if (val == null) {
                val = new Vector3f();
            }
            switch (property.getPhase()) {
                case LERPIN: {
                    val = TMath.slerp(new Vector3f(), val, property.getLerpInRatio());
                    break;
                }
                case LERPOUT: {
                    val = TMath.slerp(val, new Vector3f(), property.getLerpOutRatio());
                }
            }
            output.add((Vector3fc)val);
        } else {
            if (val == null && property.isEmptyZero()) {
                val = new Vector3f();
            }
            if (val != null) {
                Vector3f local = bone.getBlueprintBone().getLocalRotation();
                switch (property.getPhase()) {
                    case LERPIN: {
                        output.set((Vector3fc)TMath.slerp(output, val.add((Vector3fc)local), property.getLerpInRatio()));
                        break;
                    }
                    case PLAY: {
                        output.set((Vector3fc)local).add((Vector3fc)val);
                        break;
                    }
                    case LERPOUT: {
                        output.set((Vector3fc)TMath.slerp(val.add((Vector3fc)local), output, property.getLerpOutRatio()));
                    }
                }
            }
        }
        transform.setLeftEuler(output);
    }).registerBoneUpdater(IStateMachineHandler.class, (handler, bone, data) -> {
        IAnimationProperty currProperty = (IAnimationProperty)data[0];
        if (currProperty == null) {
            return;
        }
        SafeTransform transform = bone.getLocalTransform();
        Vector3f currVal = new Vector3f((Vector3fc)transform.getLeftEuler());
        Vector3f val = currProperty.getRotationFrame(bone);
        if (val == null && currProperty.isEmptyZero()) {
            val = new Vector3f();
        }
        if (val != null) {
            if (currProperty.isOverride()) {
                currVal.set((Vector3fc)bone.getBlueprintBone().getLocalRotation());
            }
            currVal.add((Vector3fc)val);
        }
        switch (currProperty.getPhase()) {
            case PLAY: {
                transform.setLeftEuler(currVal);
                return;
            }
            case LERPOUT: {
                transform.setLeftEuler(TMath.slerp(currVal, transform.getLeftEuler(), currProperty.getLerpOutRatio()));
                return;
            }
        }
        IAnimationProperty lastProperty = (IAnimationProperty)data[1];
        if (lastProperty == null) {
            transform.setLeftEuler(TMath.slerp(transform.getLeftEuler(), currVal, currProperty.getLerpInRatio()));
            return;
        }
        Vector3f lastVal = new Vector3f((Vector3fc)transform.getLeftEuler());
        Vector3f val2 = lastProperty.getRotationFrame(bone);
        if (val2 == null && lastProperty.isEmptyZero()) {
            val2 = new Vector3f();
        }
        if (val2 != null) {
            if (lastProperty.isOverride()) {
                lastVal.set((Vector3fc)bone.getBlueprintBone().getLocalRotation());
            }
            lastVal.add((Vector3fc)val2);
        }
        transform.setLeftEuler(TMath.slerp(lastVal, currVal, currProperty.getLerpInRatio()));
    }).build();
    public static final KeyframeType<VectorKeyframe, Vector3f> SCALE = KeyframeType.Builder.of("scale", VectorKeyframe::new).interpolator(timeline -> new PrePostInterpolator<VectorKeyframe, Vector3f>((ctx, prev, next, ratio) -> KeyframeTypes.standard(ctx, prev, next, ratio, StepFlag.SCALE), (ctx, vectorKeyframe) -> KeyframeTypes.markStep(ctx, vectorKeyframe, StepFlag.SCALE), KeyframeTypes::derive)).registerBoneUpdater(IPriorityHandler.class, (handler, bone, data) -> {
        IAnimationProperty property = (IAnimationProperty)data[0];
        SafeTransform transform = bone.getLocalTransform();
        Vector3f output = transform.getScale();
        Vector3f val = property.getScaleFrame(bone);
        if (!property.isOverride()) {
            if (val == null) {
                val = new Vector3f(1.0f);
            }
            switch (property.getPhase()) {
                case LERPIN: {
                    val = TMath.lerp(new Vector3f(1.0f), val, property.getLerpInRatio());
                    break;
                }
                case LERPOUT: {
                    val = TMath.lerp(val, new Vector3f(1.0f), property.getLerpOutRatio());
                }
            }
            output.mul((Vector3fc)val);
        } else {
            if (val == null && property.isEmptyZero()) {
                val = new Vector3f(1.0f);
            }
            if (val != null) {
                switch (property.getPhase()) {
                    case LERPIN: {
                        output.set((Vector3fc)TMath.lerp(output, val, property.getLerpInRatio()));
                        break;
                    }
                    case PLAY: {
                        output.set((Vector3fc)val);
                        break;
                    }
                    case LERPOUT: {
                        output.set((Vector3fc)TMath.lerp(val, output, property.getLerpOutRatio()));
                    }
                }
            }
        }
        transform.setScale(output);
    }).registerBoneUpdater(IStateMachineHandler.class, (handler, bone, data) -> {
        IAnimationProperty currProperty = (IAnimationProperty)data[0];
        if (currProperty == null) {
            return;
        }
        SafeTransform transform = bone.getLocalTransform();
        Vector3f currVal = new Vector3f((Vector3fc)transform.getScale());
        Vector3f val = currProperty.getScaleFrame(bone);
        if (val == null && currProperty.isEmptyZero()) {
            val = new Vector3f(1.0f);
        }
        if (val != null) {
            if (currProperty.isOverride()) {
                currVal.set(1.0f);
            }
            currVal.mul((Vector3fc)val);
        }
        switch (currProperty.getPhase()) {
            case PLAY: {
                transform.setScale(currVal);
                return;
            }
            case LERPOUT: {
                transform.setScale(TMath.lerp(currVal, transform.getScale(), currProperty.getLerpOutRatio()));
                return;
            }
        }
        IAnimationProperty lastProperty = (IAnimationProperty)data[1];
        if (lastProperty == null) {
            transform.setScale(TMath.lerp(transform.getScale(), currVal, currProperty.getLerpInRatio()));
            return;
        }
        Vector3f lastVal = new Vector3f((Vector3fc)transform.getScale());
        Vector3f val2 = lastProperty.getScaleFrame(bone);
        if (val2 == null && lastProperty.isEmptyZero()) {
            val2 = new Vector3f(1.0f);
        }
        if (val2 != null) {
            if (lastProperty.isOverride()) {
                lastVal.set(1.0f);
            }
            lastVal.mul((Vector3fc)val2);
        }
        transform.setScale(TMath.lerp(lastVal, currVal, currProperty.getLerpInRatio()));
    }).build();
    public static final KeyframeType<ScriptKeyframe, List<ScriptKeyframe.Script>> SCRIPT = KeyframeType.Builder.of("script", ScriptKeyframe::new).interpolator(blueprintAnimation -> new ScriptInterpolator(ArrayList::new, List::addAll)).registerModelUpdater(IPriorityHandler.class, KeyframeTypes::standardScript).registerModelUpdater(IStateMachineHandler.class, KeyframeTypes::standardScript).global().build();

    private static Vector3f standard(KeyframeInterpolator.Context<VectorKeyframe, Vector3f> ctx, Vector3f prev, Vector3f next, float ratio, StepFlag stepFlag) {
        String animation = ctx.property.getBlueprintAnimation().getName();
        String prevMode = ((VectorKeyframe)ctx.interpolator.get(Float.valueOf(ctx.prevKey))).getInterpolation();
        if (prevMode.equals("step")) {
            ctx.bone.markStep(stepFlag, animation, ctx.prevKey);
            return prev;
        }
        VectorKeyframe nextFrame = (VectorKeyframe)ctx.interpolator.get(Float.valueOf(ctx.nextKey));
        if (nextFrame.isDiscontinuous() && ctx.property.getTime() + ctx.property.getSpeed() * 0.05 > (double)ctx.nextKey) {
            ctx.bone.markStep(stepFlag, animation, ctx.nextKey);
        }
        String nextMode = nextFrame.getInterpolation();
        if (prevMode.equals("catmullrom") || nextMode.equals("catmullrom")) {
            float nNextKey = ctx.interpolator.getHigherKey(ctx.nextKey);
            float pPrevKey = ctx.interpolator.getLowerKey(ctx.prevKey);
            VectorKeyframe nextControlVector = (VectorKeyframe)ctx.interpolator.get(Float.valueOf(nNextKey));
            VectorKeyframe lastControlVector = (VectorKeyframe)ctx.interpolator.get(Float.valueOf(pPrevKey));
            return TMath.smoothLerp(lastControlVector.getValue(0, ctx.property), prev, next, nextControlVector.getValue(0, ctx.property), ratio);
        }
        return prev.lerp((Vector3fc)next, ratio, new Vector3f());
    }

    private static void markStep(KeyframeInterpolator.Context<VectorKeyframe, Vector3f> ctx, VectorKeyframe frame, StepFlag stepFlag) {
        String animation = ctx.property.getBlueprintAnimation().getName();
        if ("step".equals(frame.getInterpolation()) || frame.isDiscontinuous()) {
            ctx.bone.markStep(stepFlag, animation, ctx.nextKey);
        }
    }

    private static Vector3f derive(@Nullable Vector3f prev, @Nullable Vector3f curr) {
        if (prev == null || curr == null) {
            return new Vector3f();
        }
        return curr.sub((Vector3fc)prev, new Vector3f());
    }

    private static void standardScript(AnimationHandler handler, ActiveModel model, Object ... data) {
        IAnimationProperty property = (IAnimationProperty)data[0];
        List<ScriptKeyframe.Script> scripts = property.getScriptFrame();
        if (scripts == null || scripts.isEmpty()) {
            return;
        }
        for (ScriptKeyframe.Script script : scripts) {
            ScriptReader reader = (ScriptReader)ModelEngineAPI.getAPI().getScriptReaderRegistry().get(script.reader());
            if (reader == null) continue;
            reader.read(property, script.script());
        }
    }
}

