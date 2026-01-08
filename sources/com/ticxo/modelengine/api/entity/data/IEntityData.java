/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Player
 *  org.jetbrains.annotations.ApiStatus$Internal
 */
package com.ticxo.modelengine.api.entity.data;

import com.ticxo.modelengine.api.entity.BaseEntity;
import com.ticxo.modelengine.api.entity.CullType;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.bone.ModelBone;
import com.ticxo.modelengine.api.utils.data.io.DataIO;
import com.ticxo.modelengine.api.utils.data.io.SavedData;
import com.ticxo.modelengine.api.utils.math.Box;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;

public interface IEntityData
extends DataIO {
    public static final SavedData.DataSaver<CullType> CULL_TYPE_DATA_SAVER = (data, s, cullType) -> data.putString(s, cullType.name());
    public static final SavedData.DataLoader<CullType> CULL_TYPE_DATA_LOADER = (data, key) -> {
        String s = data.getString(key);
        return s == null ? null : CullType.get(s);
    };

    public void asyncUpdate();

    public void syncUpdate();

    public void cullUpdate();

    public void cleanup();

    public void destroy();

    public BaseEntity<?> getBaseEntity();

    public boolean isDataValid();

    public Location getLocation();

    public List<Entity> getPassengers();

    public Set<UUID> getStartTracking();

    @ApiStatus.Internal
    public Map<UUID, CullType> getMutableTracking();

    public Map<UUID, CullType> getTracking();

    public Set<UUID> getStopTracking();

    public boolean hasTracking();

    public Box getCullHitbox();

    public void setCullHitbox(Box var1);

    public Integer getCullInterval();

    public void setCullInterval(Integer var1);

    public int cullInterval();

    public Boolean getVerticalCull();

    public void setVerticalCull(Boolean var1);

    public boolean verticalCull();

    public Double getVerticalCullDistance();

    public void setVerticalCullDistance(Double var1);

    public double verticalCullDistance();

    public CullType getVerticalCullType();

    public void setVerticalCullType(CullType var1);

    public CullType verticalCullType();

    public Boolean getBackCull();

    public void setBackCull(Boolean var1);

    public boolean backCull();

    public Double getBackCullAngle();

    public void setBackCullAngle(Double var1);

    public double backCullAngle();

    public Double getBackCullIgnoreRadius();

    public void setBackCullIgnoreRadius(Double var1);

    public double backCullIgnoreRadius();

    public CullType getBackCullType();

    public void setBackCullType(CullType var1);

    public CullType backCullType();

    public Boolean getBlockedCull();

    public void setBlockedCull(Boolean var1);

    public boolean blockedCull();

    public Double getBlockedCullIgnoreRadius();

    public void setBlockedCullIgnoreRadius(Double var1);

    public double blockedCullIgnoreRadius();

    public CullType getBlockedCullType();

    public void setBlockedCullType(CullType var1);

    public CullType blockedCullType();

    public void markModelGlowing(ActiveModel var1, boolean var2);

    public void markBoneGlowing(ModelBone var1, boolean var2);

    public boolean isModelGlowing();

    public boolean isBaseGlowing();

    @Override
    default public void save(SavedData data) {
        data.saveIfExist("cull_interval", this::getCullInterval, SavedData::putInt);
        data.saveIfExist("vertical_cull", this::getVerticalCull, SavedData::putBoolean);
        data.saveIfExist("vertical_cull_distance", this::getVerticalCullDistance, SavedData::putDouble);
        data.saveIfExist("vertical_cull_type", this::getVerticalCullType, CULL_TYPE_DATA_SAVER);
        data.saveIfExist("back_cull", this::getBackCull, SavedData::putBoolean);
        data.saveIfExist("back_cull_angle", this::getBackCullAngle, SavedData::putDouble);
        data.saveIfExist("back_cull_ignore_radius", this::getBackCullIgnoreRadius, SavedData::putDouble);
        data.saveIfExist("back_cull_type", this::getBackCullType, CULL_TYPE_DATA_SAVER);
        data.saveIfExist("blocked_cull", this::getBlockedCull, SavedData::putBoolean);
        data.saveIfExist("blocked_cull_ignore_radius", this::getBlockedCullIgnoreRadius, SavedData::putDouble);
        data.saveIfExist("blocked_cull_type", this::getBlockedCullType, CULL_TYPE_DATA_SAVER);
    }

    @Override
    default public void load(SavedData data) {
        data.loadIfExist("cull_interval", SavedData::getInt, this::setCullInterval);
        data.loadIfExist("vertical_cull", SavedData::getBoolean, this::setVerticalCull);
        data.loadIfExist("vertical_cull_distance", SavedData::getDouble, this::setVerticalCullDistance);
        data.loadIfExist("vertical_cull_type", CULL_TYPE_DATA_LOADER, this::setVerticalCullType);
        data.loadIfExist("back_cull", SavedData::getBoolean, this::setBackCull);
        data.loadIfExist("back_cull_angle", SavedData::getDouble, this::setBackCullAngle);
        data.loadIfExist("back_cull_ignore_radius", SavedData::getDouble, this::setBackCullIgnoreRadius);
        data.loadIfExist("back_cull_type", CULL_TYPE_DATA_LOADER, this::setBackCullType);
        data.loadIfExist("blocked_cull", SavedData::getBoolean, this::setBlockedCull);
        data.loadIfExist("blocked_cull_ignore_radius", SavedData::getDouble, this::setBlockedCullIgnoreRadius);
        data.loadIfExist("blocked_cull_type", CULL_TYPE_DATA_LOADER, this::setBlockedCullType);
    }

    @FunctionalInterface
    public static interface CullCameraOverride {
        public Location calculate(IEntityData var1, Player var2, Location var3);
    }
}

