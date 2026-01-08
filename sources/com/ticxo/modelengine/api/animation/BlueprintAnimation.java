/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Vector3f
 */
package com.ticxo.modelengine.api.animation;

import com.ticxo.modelengine.api.animation.Timeline;
import com.ticxo.modelengine.api.animation.interpolator.KeyframeInterpolator;
import com.ticxo.modelengine.api.animation.keyframe.KeyframeType;
import com.ticxo.modelengine.api.animation.keyframe.KeyframeTypes;
import com.ticxo.modelengine.api.animation.keyframe.type.AbstractKeyframe;
import com.ticxo.modelengine.api.animation.keyframe.type.ScriptKeyframe;
import com.ticxo.modelengine.api.animation.property.IAnimationProperty;
import com.ticxo.modelengine.api.generator.blueprint.BlueprintBone;
import com.ticxo.modelengine.api.generator.blueprint.ModelBlueprint;
import com.ticxo.modelengine.api.model.bone.ModelBone;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import lombok.Generated;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public class BlueprintAnimation {
    private final ModelBlueprint modelBlueprint;
    private final Map<UUID, Timeline> timelines = new HashMap<UUID, Timeline>();
    private final String name;
    private final Timeline globalTimeline = new Timeline(this, false);
    private double length;
    private LoopMode loopMode;
    private boolean override;

    public Vector3f getPosition(ModelBone bone, IAnimationProperty property) {
        return this.getPosition(bone, bone.getBlueprintBone(), property);
    }

    public Vector3f getPosition(ModelBone bone, BlueprintBone blueprintBone, IAnimationProperty property) {
        Timeline timeline = this.timelines.get(blueprintBone.getUuid());
        if (timeline == null) {
            return null;
        }
        return timeline.getInterpolator(KeyframeTypes.POSITION).interpolate(bone, property);
    }

    public Vector3f getVelocity(ModelBone bone, IAnimationProperty property) {
        return this.getVelocity(bone, bone.getBlueprintBone(), property);
    }

    public Vector3f getVelocity(ModelBone bone, BlueprintBone blueprintBone, IAnimationProperty property) {
        Timeline timeline = this.timelines.get(blueprintBone.getUuid());
        if (timeline == null) {
            return null;
        }
        return timeline.getInterpolator(KeyframeTypes.POSITION).interpolateAndDerive(bone, property);
    }

    public Vector3f getRotation(ModelBone bone, IAnimationProperty property) {
        return this.getRotation(bone, bone.getBlueprintBone(), property);
    }

    public Vector3f getRotation(ModelBone bone, BlueprintBone blueprintBone, IAnimationProperty property) {
        Timeline timeline = this.timelines.get(blueprintBone.getUuid());
        if (timeline == null) {
            return null;
        }
        return timeline.getInterpolator(KeyframeTypes.ROTATION).interpolate(bone, property);
    }

    public Vector3f getScale(ModelBone bone, IAnimationProperty property) {
        return this.getScale(bone, bone.getBlueprintBone(), property);
    }

    public Vector3f getScale(ModelBone bone, BlueprintBone blueprintBone, IAnimationProperty property) {
        Timeline timeline = this.timelines.get(blueprintBone.getUuid());
        if (timeline == null) {
            return null;
        }
        return timeline.getInterpolator(KeyframeTypes.SCALE).interpolate(bone, property);
    }

    public List<ScriptKeyframe.Script> getScript(IAnimationProperty property) {
        return this.globalTimeline.getInterpolator(KeyframeTypes.SCRIPT).interpolate(null, property);
    }

    public String getInterpolation(KeyframeType<?, ?> type, UUID uuid, float time) {
        Timeline timeline = this.timelines.get(uuid);
        if (timeline == null) {
            return null;
        }
        KeyframeInterpolator<?, ?> interpolator = timeline.getInterpolator(type);
        AbstractKeyframe frame = (AbstractKeyframe)interpolator.get(Float.valueOf(time));
        if (frame != null) {
            return frame.getInterpolation();
        }
        frame = (AbstractKeyframe)interpolator.get(Float.valueOf(interpolator.getLowerKey(time)));
        if (frame == null) {
            return null;
        }
        return frame.getInterpolation();
    }

    @Generated
    public ModelBlueprint getModelBlueprint() {
        return this.modelBlueprint;
    }

    @Generated
    public Map<UUID, Timeline> getTimelines() {
        return this.timelines;
    }

    @Generated
    public String getName() {
        return this.name;
    }

    @Generated
    public Timeline getGlobalTimeline() {
        return this.globalTimeline;
    }

    @Generated
    public double getLength() {
        return this.length;
    }

    @Generated
    public LoopMode getLoopMode() {
        return this.loopMode;
    }

    @Generated
    public boolean isOverride() {
        return this.override;
    }

    @Generated
    public BlueprintAnimation(ModelBlueprint modelBlueprint, String name) {
        this.modelBlueprint = modelBlueprint;
        this.name = name;
    }

    @Generated
    public void setLength(double length) {
        this.length = length;
    }

    @Generated
    public void setLoopMode(LoopMode loopMode) {
        this.loopMode = loopMode;
    }

    @Generated
    public void setOverride(boolean override) {
        this.override = override;
    }

    public static enum LoopMode {
        ONCE,
        HOLD,
        LOOP;


        public static LoopMode get(String mode) {
            try {
                return LoopMode.valueOf(mode.toUpperCase(Locale.ENGLISH));
            }
            catch (IllegalArgumentException illegalArgumentException) {
                return ONCE;
            }
        }

        @Nullable
        public static LoopMode getOrNull(String mode) {
            if (mode == null) {
                return null;
            }
            try {
                return LoopMode.valueOf(mode.toUpperCase(Locale.ENGLISH));
            }
            catch (IllegalArgumentException illegalArgumentException) {
                return null;
            }
        }
    }

    public static enum OverrideMode {
        NONE,
        OVERRIDE;


        public static OverrideMode get(String mode) {
            try {
                return OverrideMode.valueOf(mode.toUpperCase(Locale.ENGLISH));
            }
            catch (IllegalArgumentException illegalArgumentException) {
                return NONE;
            }
        }

        @Nullable
        public static OverrideMode getOrNull(String mode) {
            if (mode == null) {
                return null;
            }
            try {
                return OverrideMode.valueOf(mode.toUpperCase(Locale.ENGLISH));
            }
            catch (IllegalArgumentException illegalArgumentException) {
                if ("true".equalsIgnoreCase(mode)) {
                    return OVERRIDE;
                }
                if ("false".equalsIgnoreCase(mode)) {
                    return NONE;
                }
                return null;
            }
        }
    }
}

