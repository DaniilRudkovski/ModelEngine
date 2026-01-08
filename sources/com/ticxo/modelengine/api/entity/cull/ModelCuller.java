/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 */
package com.ticxo.modelengine.api.entity.cull;

import com.ticxo.modelengine.api.entity.CullType;
import com.ticxo.modelengine.api.entity.data.IEntityData;
import com.ticxo.modelengine.api.utils.callback.Callback;
import java.util.UUID;
import org.bukkit.Location;

public interface ModelCuller {
    public static final Callback<IEntityData.CullCameraOverride> PLAYER_CALLBACK = new Callback<IEntityData.CullCameraOverride>(cullCameraOverrides -> (data, player, lastLocation) -> {
        Location location = lastLocation;
        for (IEntityData.CullCameraOverride override : cullCameraOverrides) {
            location = override.calculate(data, player, location);
        }
        return location;
    });

    public void setData(IEntityData var1);

    public IEntityData getData();

    public void updateCulledPlayer();

    public CullType put(UUID var1, CullType var2);

    public CullType remove(UUID var1);

    public int getCulledCount();
}

