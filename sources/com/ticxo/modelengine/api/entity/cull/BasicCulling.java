/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 *  org.bukkit.Bukkit
 *  org.bukkit.Location
 *  org.bukkit.entity.Player
 *  org.bukkit.util.BoundingBox
 *  org.bukkit.util.Vector
 */
package com.ticxo.modelengine.api.entity.cull;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.entity.CullType;
import com.ticxo.modelengine.api.entity.cull.ModelCuller;
import com.ticxo.modelengine.api.entity.data.IEntityData;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.mount.MountPairManager;
import java.util.UUID;
import lombok.Generated;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

public class BasicCulling
implements ModelCuller {
    protected IEntityData data;
    protected int lastCulled;
    protected int culledCount;

    @Override
    public void updateCulledPlayer() {
        if (this.data == null || --this.lastCulled > 0) {
            return;
        }
        this.lastCulled = this.data.cullInterval();
        MountPairManager pairManager = ModelEngineAPI.getMountPairManager();
        Location location = this.data.getLocation();
        BoundingBox box = this.data.getCullHitbox() == null ? this.data.getBaseEntity().getBoundingBox() : this.data.getCullHitbox().createBoundingBox(location.toVector());
        for (UUID uuid : this.data.getMutableTracking().keySet()) {
            double distance;
            Player player = Bukkit.getPlayer((UUID)uuid);
            if (player == null) continue;
            ActiveModel mounted = pairManager.getMountedPair(player.getUniqueId());
            if (mounted != null && mounted.getModeledEntity().getBase().getData() == this.data) {
                this.put(player.getUniqueId(), CullType.NO_CULL);
                continue;
            }
            if (location.getWorld() != player.getWorld()) {
                this.put(player.getUniqueId(), CullType.CULLED);
                continue;
            }
            Location eyeLoc = ((IEntityData.CullCameraOverride)PLAYER_CALLBACK.invoker()).calculate(this.data, player, player.getEyeLocation());
            if (this.data.verticalCull() && (distance = Math.max(eyeLoc.getY() - box.getMaxY(), box.getMinY() - eyeLoc.getY())) > this.data.verticalCullDistance()) {
                this.put(player.getUniqueId(), this.data.verticalCullType());
                continue;
            }
            Vector delta = location.clone().subtract(eyeLoc).toVector();
            if (this.data.blockedCull() && !this.data.isBaseGlowing() && !this.data.isModelGlowing() && delta.lengthSquared() > this.data.blockedCullIgnoreRadius() && ModelEngineAPI.getEntityHandler().shouldCull(player, eyeLoc, box)) {
                this.put(player.getUniqueId(), this.data.blockedCullType());
                continue;
            }
            if (this.data.backCull()) {
                if (box.contains(eyeLoc.toVector())) {
                    this.put(player.getUniqueId(), CullType.NO_CULL);
                    continue;
                }
                if (delta.lengthSquared() > this.data.backCullIgnoreRadius() && eyeLoc.getDirection().dot(delta.normalize()) <= this.data.backCullAngle()) {
                    this.put(player.getUniqueId(), this.data.backCullType());
                    continue;
                }
            }
            this.put(player.getUniqueId(), CullType.NO_CULL);
        }
    }

    @Override
    public CullType put(UUID uuid, CullType type) {
        CullType last = this.data.getMutableTracking().put(uuid, type);
        if (last == CullType.CULLED) {
            --this.culledCount;
        }
        if (type == CullType.CULLED) {
            ++this.culledCount;
        }
        return last;
    }

    @Override
    public CullType remove(UUID uuid) {
        CullType removed = this.data.getMutableTracking().remove(uuid);
        if (removed == CullType.CULLED) {
            --this.culledCount;
        }
        return removed;
    }

    @Generated
    public BasicCulling() {
    }

    @Override
    @Generated
    public IEntityData getData() {
        return this.data;
    }

    @Override
    @Generated
    public void setData(IEntityData data) {
        this.data = data;
    }

    @Override
    @Generated
    public int getCulledCount() {
        return this.culledCount;
    }
}

