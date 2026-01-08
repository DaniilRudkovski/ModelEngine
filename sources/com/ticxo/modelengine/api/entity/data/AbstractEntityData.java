/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 */
package com.ticxo.modelengine.api.entity.data;

import com.ticxo.modelengine.api.entity.CullType;
import com.ticxo.modelengine.api.entity.data.IEntityData;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.bone.ModelBone;
import com.ticxo.modelengine.api.utils.config.ConfigProperty;
import com.ticxo.modelengine.api.utils.math.Box;
import com.ticxo.modelengine.api.utils.math.TMath;
import java.util.HashSet;
import java.util.Set;
import lombok.Generated;

public abstract class AbstractEntityData
implements IEntityData {
    private static int CULL_INTERVAL;
    private static boolean CULL_VERTICAL;
    private static double CULL_VERTICAL_DISTANCE;
    private static CullType CULL_VERTICAL_TYPE;
    private static boolean CULL_BEHIND;
    private static double VIEW_ANGLE;
    private static double UPDATE_BEHIND_RADIUS_SQR;
    private static CullType CULL_BEHIND_TYPE;
    private static boolean CULL_BLOCKED;
    private static double FORCE_UNBLOCKED_RADIUS_SQR;
    private static CullType CULL_BLOCKED_TYPE;
    private final Set<ActiveModel> glowingModel = new HashSet<ActiveModel>();
    private final Set<ModelBone> glowingBone = new HashSet<ModelBone>();
    private Box cullHitbox;
    private Integer cullInterval;
    private Boolean verticalCull;
    private Double verticalCullDistance;
    private CullType verticalCullType;
    private Boolean backCull;
    private Double backCullAngle;
    private Double backCullIgnoreRadius;
    private CullType backCullType;
    private Boolean blockedCull;
    private Double blockedCullIgnoreRadius;
    private CullType blockedCullType;

    public static void updateConfig() {
        CULL_INTERVAL = Math.max(ConfigProperty.CULL_INTERVAL.getInt(), 1);
        CULL_VERTICAL = ConfigProperty.VERTICAL_CULL_ENABLE.getBoolean();
        CULL_VERTICAL_DISTANCE = ConfigProperty.VERTICAL_CULL_DISTANCE.getDouble();
        CULL_VERTICAL_TYPE = ConfigProperty.VERTICAL_CULL_TYPE.getCullType();
        CULL_BEHIND = ConfigProperty.BACKWARDS_CULL_ENABLED.getBoolean();
        VIEW_ANGLE = Math.cos(TMath.clamp(ConfigProperty.BACKWARDS_CULL_ANGLE.getDouble(), 0.0, 360.0) * 0.5 * 0.01745329238474369);
        UPDATE_BEHIND_RADIUS_SQR = ConfigProperty.BACKWARDS_CULL_IGNORE_RADIUS.getDouble();
        UPDATE_BEHIND_RADIUS_SQR *= UPDATE_BEHIND_RADIUS_SQR;
        CULL_BEHIND_TYPE = ConfigProperty.BACKWARDS_CULL_TYPE.getCullType();
        CULL_BLOCKED = ConfigProperty.BLOCK_CULL_ENABLE.getBoolean();
        FORCE_UNBLOCKED_RADIUS_SQR = ConfigProperty.BLOCK_CULL_IGNORE_RADIUS.getDouble();
        FORCE_UNBLOCKED_RADIUS_SQR *= FORCE_UNBLOCKED_RADIUS_SQR;
        CULL_BLOCKED_TYPE = ConfigProperty.BLOCK_CULL_TYPE.getCullType();
    }

    @Override
    public int cullInterval() {
        return this.cullInterval == null ? CULL_INTERVAL : this.cullInterval;
    }

    @Override
    public boolean verticalCull() {
        return this.verticalCull == null ? CULL_VERTICAL : this.verticalCull;
    }

    @Override
    public double verticalCullDistance() {
        return this.verticalCullDistance == null ? CULL_VERTICAL_DISTANCE : this.verticalCullDistance;
    }

    @Override
    public CullType verticalCullType() {
        return this.verticalCullType == null ? CULL_VERTICAL_TYPE : this.verticalCullType;
    }

    @Override
    public boolean backCull() {
        return this.backCull == null ? CULL_BEHIND : this.backCull;
    }

    @Override
    public double backCullAngle() {
        return this.backCullAngle == null ? VIEW_ANGLE : this.backCullAngle;
    }

    @Override
    public double backCullIgnoreRadius() {
        return this.backCullIgnoreRadius == null ? UPDATE_BEHIND_RADIUS_SQR : this.backCullIgnoreRadius * this.backCullIgnoreRadius;
    }

    @Override
    public CullType backCullType() {
        return this.backCullType == null ? CULL_BEHIND_TYPE : this.backCullType;
    }

    @Override
    public boolean blockedCull() {
        return this.blockedCull == null ? CULL_BLOCKED : this.blockedCull;
    }

    @Override
    public double blockedCullIgnoreRadius() {
        return this.blockedCullIgnoreRadius == null ? FORCE_UNBLOCKED_RADIUS_SQR : this.blockedCullIgnoreRadius * this.blockedCullIgnoreRadius;
    }

    @Override
    public CullType blockedCullType() {
        return this.blockedCullType == null ? CULL_BLOCKED_TYPE : this.blockedCullType;
    }

    @Override
    public void markModelGlowing(ActiveModel model, boolean flag) {
        if (flag) {
            this.glowingModel.add(model);
        } else {
            this.glowingModel.remove(model);
        }
    }

    @Override
    public void markBoneGlowing(ModelBone bone, boolean flag) {
        if (flag) {
            this.glowingBone.add(bone);
        } else {
            this.glowingBone.remove(bone);
        }
    }

    @Override
    public boolean isModelGlowing() {
        return !this.glowingBone.isEmpty() || !this.glowingModel.isEmpty();
    }

    @Override
    @Generated
    public Box getCullHitbox() {
        return this.cullHitbox;
    }

    @Override
    @Generated
    public void setCullHitbox(Box cullHitbox) {
        this.cullHitbox = cullHitbox;
    }

    @Override
    @Generated
    public Integer getCullInterval() {
        return this.cullInterval;
    }

    @Override
    @Generated
    public void setCullInterval(Integer cullInterval) {
        this.cullInterval = cullInterval;
    }

    @Override
    @Generated
    public Boolean getVerticalCull() {
        return this.verticalCull;
    }

    @Override
    @Generated
    public void setVerticalCull(Boolean verticalCull) {
        this.verticalCull = verticalCull;
    }

    @Override
    @Generated
    public Double getVerticalCullDistance() {
        return this.verticalCullDistance;
    }

    @Override
    @Generated
    public void setVerticalCullDistance(Double verticalCullDistance) {
        this.verticalCullDistance = verticalCullDistance;
    }

    @Override
    @Generated
    public CullType getVerticalCullType() {
        return this.verticalCullType;
    }

    @Override
    @Generated
    public void setVerticalCullType(CullType verticalCullType) {
        this.verticalCullType = verticalCullType;
    }

    @Override
    @Generated
    public Boolean getBackCull() {
        return this.backCull;
    }

    @Override
    @Generated
    public void setBackCull(Boolean backCull) {
        this.backCull = backCull;
    }

    @Override
    @Generated
    public Double getBackCullAngle() {
        return this.backCullAngle;
    }

    @Override
    @Generated
    public void setBackCullAngle(Double backCullAngle) {
        this.backCullAngle = backCullAngle;
    }

    @Override
    @Generated
    public Double getBackCullIgnoreRadius() {
        return this.backCullIgnoreRadius;
    }

    @Override
    @Generated
    public void setBackCullIgnoreRadius(Double backCullIgnoreRadius) {
        this.backCullIgnoreRadius = backCullIgnoreRadius;
    }

    @Override
    @Generated
    public CullType getBackCullType() {
        return this.backCullType;
    }

    @Override
    @Generated
    public void setBackCullType(CullType backCullType) {
        this.backCullType = backCullType;
    }

    @Override
    @Generated
    public Boolean getBlockedCull() {
        return this.blockedCull;
    }

    @Override
    @Generated
    public void setBlockedCull(Boolean blockedCull) {
        this.blockedCull = blockedCull;
    }

    @Override
    @Generated
    public Double getBlockedCullIgnoreRadius() {
        return this.blockedCullIgnoreRadius;
    }

    @Override
    @Generated
    public void setBlockedCullIgnoreRadius(Double blockedCullIgnoreRadius) {
        this.blockedCullIgnoreRadius = blockedCullIgnoreRadius;
    }

    @Override
    @Generated
    public CullType getBlockedCullType() {
        return this.blockedCullType;
    }

    @Override
    @Generated
    public void setBlockedCullType(CullType blockedCullType) {
        this.blockedCullType = blockedCullType;
    }
}

