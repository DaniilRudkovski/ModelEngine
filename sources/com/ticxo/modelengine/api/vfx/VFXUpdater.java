/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 */
package com.ticxo.modelengine.api.vfx;

import com.google.common.collect.Maps;
import com.ticxo.modelengine.api.entity.BaseEntity;
import com.ticxo.modelengine.api.vfx.VFX;
import java.util.Map;
import java.util.UUID;

public class VFXUpdater {
    private final Map<Integer, UUID> entityIdLookup = Maps.newConcurrentMap();
    private final Map<UUID, VFX> uuidLookup = Maps.newConcurrentMap();

    public void updateAllVFXs() {
        for (Map.Entry<UUID, VFX> entry : this.uuidLookup.entrySet()) {
            VFX entity = entry.getValue();
            if (!entity.isInitialized()) continue;
            if (!entity.tick()) {
                this.uuidLookup.remove(entry.getKey());
                this.entityIdLookup.remove(entity.getBase().getEntityId());
                entity.getRenderer().destroy();
                entity.getBase().getData().cleanup();
                entity.getBase().setForcedAlive(false);
                entity.destroy();
                continue;
            }
            entity.getRenderer().sendToClient();
            entity.getBase().getData().cleanup();
        }
    }

    public void registerVFX(BaseEntity<?> base, VFX vfx) {
        this.entityIdLookup.put(base.getEntityId(), base.getUUID());
        this.uuidLookup.put(base.getUUID(), vfx);
    }

    public VFX getVFX(int id) {
        return this.getVFX(this.entityIdLookup.get(id));
    }

    public VFX getVFX(UUID uuid) {
        return uuid == null ? null : this.uuidLookup.get(uuid);
    }

    public VFX removeVFX(int id) {
        return this.removeVFX(this.entityIdLookup.get(id));
    }

    public VFX removeVFX(UUID uuid) {
        if (uuid == null) {
            return null;
        }
        VFX vfx = this.uuidLookup.get(uuid);
        if (vfx != null) {
            vfx.markRemoved();
        }
        return vfx;
    }
}

