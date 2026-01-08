/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.lumine.mythic.api.adapters.AbstractEntity
 *  io.lumine.mythic.api.config.MythicLineConfig
 *  io.lumine.mythic.api.skills.ITargetedEntitySkill
 *  io.lumine.mythic.api.skills.SkillMetadata
 *  io.lumine.mythic.api.skills.SkillResult
 *  io.lumine.mythic.api.skills.placeholders.PlaceholderDouble
 *  io.lumine.mythic.api.skills.placeholders.PlaceholderString
 *  io.lumine.mythic.core.skills.placeholders.PlaceholderMeta
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 *  org.joml.Vector3f
 */
package com.ticxo.modelengine.core21.mythic.mechanics.bone;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.ModeledEntity;
import com.ticxo.modelengine.api.model.bone.ManualAnimator;
import com.ticxo.modelengine.api.model.bone.ModelBone;
import com.ticxo.modelengine.api.model.bone.SimpleManualAnimator;
import com.ticxo.modelengine.api.utils.logger.TLogger;
import com.ticxo.modelengine.core21.mythic.MythicUtils;
import com.ticxo.modelengine.core21.mythic.utils.MythicMechanic;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.core.skills.placeholders.PlaceholderMeta;
import java.util.Locale;
import java.util.Map;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;

@MythicMechanic(name="animatemodel", aliases={})
public class AnimateModelMechanic
implements ITargetedEntitySkill {
    private static final String[] POSITION = new String[]{"pos", "position"};
    private static final String[] ROTATION = new String[]{"rot", "rotation"};
    private static final String[] SCALE = new String[]{"scl", "scale"};
    private final PlaceholderString modelId;
    private final PlaceholderString partId;
    private final boolean exactMatch;
    private final boolean relative;
    private final boolean override;
    private final boolean copy;
    private final PlaceholderDouble[] position = new PlaceholderDouble[3];
    private final boolean hasPosition;
    private final PlaceholderDouble[] rotation = new PlaceholderDouble[3];
    private final boolean hasRotation;
    private final PlaceholderDouble[] scale = new PlaceholderDouble[3];
    private final boolean hasScale;
    private final PlaceholderString rotationModeString;
    private RotationMode rotationMode;

    public AnimateModelMechanic(MythicLineConfig mlc) {
        this.modelId = mlc.getPlaceholderString(new String[]{"m", "mid", "model", "modelid"}, null, new String[0]);
        this.partId = mlc.getPlaceholderString(new String[]{"p", "pid", "part", "partid"}, "", new String[0]);
        this.exactMatch = mlc.getBoolean(new String[]{"em", "exact", "match", "exactmatch"}, true);
        this.relative = mlc.getBoolean(new String[]{"r", "rel", "relative"}, false);
        this.override = mlc.getBoolean(new String[]{"o", "ov", "override"}, false);
        this.copy = mlc.getBoolean(new String[]{"copy"}, true);
        this.hasPosition = this.readTransform(mlc, POSITION, this.position, "position", "0");
        this.hasRotation = this.readTransform(mlc, ROTATION, this.rotation, "rotation", "0");
        this.hasScale = this.readTransform(mlc, SCALE, this.scale, "scale", "1");
        this.rotationModeString = mlc.getPlaceholderString(new String[]{"rm", "rotmode"}, "LOCAL", new String[0]);
    }

    public SkillResult castAtEntity(SkillMetadata meta, AbstractEntity target) {
        ModeledEntity model = ModelEngineAPI.getModeledEntity(target.getUniqueId());
        if (model == null) {
            return SkillResult.CONDITION_FAILED;
        }
        String partId = MythicUtils.getOrNullLowercase(this.partId, (PlaceholderMeta)meta, target);
        String modelId = MythicUtils.getOrNullLowercase(this.modelId, (PlaceholderMeta)meta, target);
        this.rotationMode = RotationMode.get(this.rotationModeString.get((PlaceholderMeta)meta, target).toUpperCase(Locale.ENGLISH));
        MythicUtils.executeOptModelId(model, modelId, activeModel -> this.transform((ActiveModel)activeModel, partId, meta, target));
        return SkillResult.SUCCESS;
    }

    private boolean readTransform(MythicLineConfig mlc, String[] attrs, PlaceholderDouble[] axis, String axisName, String def) {
        String coords = mlc.getString(attrs, null, new String[0]);
        if (coords == null) {
            return false;
        }
        String[] split = coords.split(",");
        try {
            axis[0] = PlaceholderDouble.of((String)split[0]);
            axis[1] = PlaceholderDouble.of((String)split[1]);
            axis[2] = PlaceholderDouble.of((String)split[2]);
        }
        catch (Exception var7) {
            TLogger.error("The '" + axisName + "' attribute must be in the format " + axisName + "=x,y,z.");
            axis[0] = PlaceholderDouble.of((String)def);
            axis[1] = PlaceholderDouble.of((String)def);
            axis[2] = PlaceholderDouble.of((String)def);
        }
        return true;
    }

    private void transform(ActiveModel activeModel, String partId, SkillMetadata meta, AbstractEntity target) {
        if (this.override) {
            if (this.exactMatch) {
                activeModel.getBone(partId).ifPresent(bone -> this.transform(this.getAnimator((ModelBone)bone), meta, target));
                return;
            }
            for (Map.Entry<String, ModelBone> entry : activeModel.getBones().entrySet()) {
                if (!entry.getKey().contains(partId)) continue;
                this.transform(this.getAnimator(entry.getValue()), meta, target);
            }
        } else {
            if (this.exactMatch) {
                activeModel.getBone(partId).ifPresent(bone -> bone.setManualAnimator(null));
                return;
            }
            for (Map.Entry<String, ModelBone> entry : activeModel.getBones().entrySet()) {
                if (!entry.getKey().contains(partId)) continue;
                entry.getValue().setManualAnimator(null);
            }
        }
    }

    private SimpleManualAnimator getAnimator(ModelBone bone) {
        ManualAnimator manualAnimator = bone.getManualAnimator();
        if (manualAnimator instanceof SimpleManualAnimator) {
            SimpleManualAnimator animator = (SimpleManualAnimator)manualAnimator;
            return animator;
        }
        SimpleManualAnimator a = this.copy ? new SimpleManualAnimator(bone) : new SimpleManualAnimator();
        bone.setManualAnimator(a);
        return a;
    }

    private void transform(SimpleManualAnimator animator, SkillMetadata meta, AbstractEntity target) {
        this.transform(this.hasPosition, animator.getPosition(), this.position, meta, target);
        this.transformRotation(this.hasRotation, animator.getRotation(), this.rotation, meta, target);
        this.transform(this.hasScale, animator.getScale(), this.scale, meta, target);
    }

    private void transform(boolean hasValue, Vector3f data, PlaceholderDouble[] values, SkillMetadata meta, AbstractEntity target) {
        if (!hasValue) {
            return;
        }
        if (this.relative) {
            data.add((float)values[0].get((PlaceholderMeta)meta, target), (float)values[1].get((PlaceholderMeta)meta, target), (float)values[2].get((PlaceholderMeta)meta, target));
        } else {
            data.set((float)values[0].get((PlaceholderMeta)meta, target), (float)values[1].get((PlaceholderMeta)meta, target), (float)values[2].get((PlaceholderMeta)meta, target));
        }
    }

    private void transformRotation(boolean hasValue, Quaternionf data, PlaceholderDouble[] values, SkillMetadata meta, AbstractEntity target) {
        if (!hasValue) {
            return;
        }
        Quaternionf delta = new Quaternionf().rotationZYX((float)values[2].get((PlaceholderMeta)meta, target) * ((float)Math.PI / 180), (float)values[1].get((PlaceholderMeta)meta, target) * ((float)Math.PI / 180), (float)values[0].get((PlaceholderMeta)meta, target) * ((float)Math.PI / 180));
        if (this.relative) {
            switch (this.rotationMode.ordinal()) {
                case 0: {
                    data.mul((Quaternionfc)delta);
                    break;
                }
                case 1: {
                    delta.mul((Quaternionfc)data, data);
                }
            }
        } else {
            data.set((Quaternionfc)delta);
        }
    }

    static enum RotationMode {
        LOCAL,
        GLOBAL;


        static RotationMode get(String value) {
            try {
                return RotationMode.valueOf(value);
            }
            catch (Exception ignored) {
                return LOCAL;
            }
        }
    }
}

