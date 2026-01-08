/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.lumine.mythic.api.adapters.AbstractEntity
 *  io.lumine.mythic.api.config.MythicLineConfig
 *  io.lumine.mythic.api.skills.ITargetedEntitySkill
 *  io.lumine.mythic.api.skills.SkillMetadata
 *  io.lumine.mythic.api.skills.SkillResult
 *  io.lumine.mythic.api.skills.placeholders.PlaceholderBoolean
 *  io.lumine.mythic.api.skills.placeholders.PlaceholderFloat
 *  io.lumine.mythic.api.skills.placeholders.PlaceholderString
 *  io.lumine.mythic.core.skills.placeholders.PlaceholderMeta
 */
package com.ticxo.modelengine.core21.mythic.mechanics.bone;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.ModeledEntity;
import com.ticxo.modelengine.api.model.bone.BoneBehaviorTypes;
import com.ticxo.modelengine.api.model.bone.ModelBone;
import com.ticxo.modelengine.api.model.bone.type.Segment;
import com.ticxo.modelengine.core21.mythic.MythicUtils;
import com.ticxo.modelengine.core21.mythic.utils.MythicMechanic;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.placeholders.PlaceholderBoolean;
import io.lumine.mythic.api.skills.placeholders.PlaceholderFloat;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.core.skills.placeholders.PlaceholderMeta;
import java.util.Map;

@MythicMechanic(name="segment", aliases={"seg"})
public class SegmentMechanic
implements ITargetedEntitySkill {
    private final PlaceholderString modelId;
    private final PlaceholderString partId;
    private final PlaceholderBoolean bounded;
    private final PlaceholderFloat angleLimit;
    private final PlaceholderFloat x;
    private final PlaceholderFloat y;
    private final PlaceholderFloat z;
    private final PlaceholderFloat minX;
    private final PlaceholderFloat maxX;
    private final PlaceholderFloat minY;
    private final PlaceholderFloat maxY;
    private final PlaceholderFloat minZ;
    private final PlaceholderFloat maxZ;
    private final PlaceholderFloat extendRate;
    private final PlaceholderFloat elasticity;
    private final boolean exactMatch;

    public SegmentMechanic(MythicLineConfig mlc) {
        this.modelId = mlc.getPlaceholderString(new String[]{"m", "mid", "model", "modelid"}, null, new String[0]);
        this.partId = mlc.getPlaceholderString(new String[]{"p", "pid", "part", "partid"}, "", new String[0]);
        this.bounded = mlc.getPlaceholderBoolean(new String[]{"b", "bd", "bounded"}, null);
        this.angleLimit = mlc.getPlaceholderFloat(new String[]{"a", "al", "angle", "anglelimit"}, null, new String[0]);
        this.x = mlc.getPlaceholderFloat(new String[]{"x"}, null, new String[0]);
        this.y = mlc.getPlaceholderFloat(new String[]{"y"}, null, new String[0]);
        this.z = mlc.getPlaceholderFloat(new String[]{"z"}, null, new String[0]);
        this.minX = mlc.getPlaceholderFloat(new String[]{"minx"}, null, new String[0]);
        this.maxX = mlc.getPlaceholderFloat(new String[]{"maxx"}, null, new String[0]);
        this.minY = mlc.getPlaceholderFloat(new String[]{"miny"}, null, new String[0]);
        this.maxY = mlc.getPlaceholderFloat(new String[]{"maxy"}, null, new String[0]);
        this.minZ = mlc.getPlaceholderFloat(new String[]{"minz"}, null, new String[0]);
        this.maxZ = mlc.getPlaceholderFloat(new String[]{"maxz"}, null, new String[0]);
        this.extendRate = mlc.getPlaceholderFloat(new String[]{"e", "er", "extend", "extendrate"}, null, new String[0]);
        this.elasticity = mlc.getPlaceholderFloat(new String[]{"el", "elasticity"}, null, new String[0]);
        this.exactMatch = mlc.getBoolean(new String[]{"em", "exact", "match", "exactmatch"}, true);
    }

    public SkillResult castAtEntity(SkillMetadata meta, AbstractEntity target) {
        ModeledEntity model = ModelEngineAPI.getModeledEntity(target.getUniqueId());
        if (model == null) {
            return SkillResult.CONDITION_FAILED;
        }
        String partId = MythicUtils.getOrNullLowercase(this.partId, (PlaceholderMeta)meta, target);
        String modelId = MythicUtils.getOrNullLowercase(this.modelId, (PlaceholderMeta)meta, target);
        Boolean bounded = this.bounded == null ? null : Boolean.valueOf(this.bounded.get((PlaceholderMeta)meta, target));
        Float angleLimit = this.angleLimit == null ? null : Float.valueOf(this.angleLimit.get((PlaceholderMeta)meta, target));
        Float x = this.x == null ? angleLimit : Float.valueOf(this.x.get((PlaceholderMeta)meta, target));
        Float y = this.y == null ? angleLimit : Float.valueOf(this.y.get((PlaceholderMeta)meta, target));
        Float z = this.z == null ? angleLimit : Float.valueOf(this.z.get((PlaceholderMeta)meta, target));
        Float minX = this.minX == null ? this.negate(x) : Float.valueOf(this.minX.get((PlaceholderMeta)meta, target));
        Float maxX = this.maxX == null ? x : Float.valueOf(this.maxX.get((PlaceholderMeta)meta, target));
        Float minY = this.minY == null ? this.negate(y) : Float.valueOf(this.minY.get((PlaceholderMeta)meta, target));
        Float maxY = this.maxY == null ? y : Float.valueOf(this.maxY.get((PlaceholderMeta)meta, target));
        Float minZ = this.minZ == null ? this.negate(z) : Float.valueOf(this.minZ.get((PlaceholderMeta)meta, target));
        Float maxZ = this.maxZ == null ? z : Float.valueOf(this.maxZ.get((PlaceholderMeta)meta, target));
        Float extendRate = this.extendRate == null ? null : Float.valueOf(this.extendRate.get((PlaceholderMeta)meta, target));
        Float elasticity = this.elasticity == null ? null : Float.valueOf(this.elasticity.get((PlaceholderMeta)meta, target));
        MythicUtils.executeOptModelId(model, modelId, activeModel -> this.segment((ActiveModel)activeModel, partId, bounded, minX, maxX, minY, maxY, minZ, maxZ, extendRate, elasticity));
        return SkillResult.SUCCESS;
    }

    private Float negate(Float val) {
        return val == null ? null : Float.valueOf(-val.floatValue());
    }

    private void segment(ActiveModel activeModel, String partId, Boolean bounded, Float minX, Float maxX, Float minY, Float maxY, Float minZ, Float maxZ, Float extendRate, Float elasticity) {
        if (partId.isBlank()) {
            for (Map.Entry<String, ModelBone> entry : activeModel.getBones().entrySet()) {
                this.setIfSegment(entry.getValue(), bounded, minX, maxX, minY, maxY, minZ, maxZ, extendRate, elasticity);
            }
            return;
        }
        if (this.exactMatch) {
            activeModel.getBone(partId).ifPresent(bone -> this.setIfSegment((ModelBone)bone, bounded, minX, maxX, minY, maxY, minZ, maxZ, extendRate, elasticity));
            return;
        }
        for (Map.Entry<String, ModelBone> entry : activeModel.getBones().entrySet()) {
            if (!entry.getKey().contains(partId)) continue;
            this.setIfSegment(entry.getValue(), bounded, minX, maxX, minY, maxY, minZ, maxZ, extendRate, elasticity);
        }
    }

    private void setIfSegment(ModelBone bone, Boolean bounded, Float minX, Float maxX, Float minY, Float maxY, Float minZ, Float maxZ, Float extendRate, Float elasticity) {
        bone.getBoneBehavior(BoneBehaviorTypes.SEGMENT).ifPresent(segment -> {
            if (bounded != null) {
                ((Segment)((Object)segment)).setBounded(bounded);
            }
            if (minX != null) {
                ((Segment)((Object)segment)).setMinX(minX.floatValue());
            }
            if (maxX != null) {
                ((Segment)((Object)segment)).setMaxX(maxX.floatValue());
            }
            if (minY != null) {
                ((Segment)((Object)segment)).setMinY(minY.floatValue());
            }
            if (maxY != null) {
                ((Segment)((Object)segment)).setMaxY(maxY.floatValue());
            }
            if (minZ != null) {
                ((Segment)((Object)segment)).setMinZ(minZ.floatValue());
            }
            if (maxZ != null) {
                ((Segment)((Object)segment)).setMaxZ(maxZ.floatValue());
            }
            if (extendRate != null) {
                ((Segment)((Object)segment)).setAlignRate(extendRate.floatValue());
            }
            if (elasticity != null) {
                ((Segment)((Object)segment)).setElasticity(elasticity.floatValue());
            }
        });
        bone.getBoneBehavior(BoneBehaviorTypes.TAIL).ifPresent(segment -> {
            if (bounded != null) {
                ((Segment)((Object)segment)).setBounded(bounded);
            }
            if (minX != null) {
                ((Segment)((Object)segment)).setMinX(minX.floatValue());
            }
            if (maxX != null) {
                ((Segment)((Object)segment)).setMaxX(maxX.floatValue());
            }
            if (minY != null) {
                ((Segment)((Object)segment)).setMinY(minY.floatValue());
            }
            if (maxY != null) {
                ((Segment)((Object)segment)).setMaxY(maxY.floatValue());
            }
            if (minZ != null) {
                ((Segment)((Object)segment)).setMinZ(minZ.floatValue());
            }
            if (maxZ != null) {
                ((Segment)((Object)segment)).setMaxZ(maxZ.floatValue());
            }
            if (extendRate != null) {
                ((Segment)((Object)segment)).setAlignRate(extendRate.floatValue());
            }
            if (elasticity != null) {
                ((Segment)((Object)segment)).setElasticity(elasticity.floatValue());
            }
        });
    }
}

