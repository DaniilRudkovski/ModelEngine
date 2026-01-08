/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joml.Vector3f
 */
package com.ticxo.modelengine.api.model.bone.render.renderer;

import com.ticxo.modelengine.api.model.bone.render.BehaviorRenderer;
import com.ticxo.modelengine.api.model.bone.render.renderer.RenderQueues;
import com.ticxo.modelengine.api.utils.data.tracker.CollectionDataTracker;
import com.ticxo.modelengine.api.utils.data.tracker.DataTracker;
import java.util.UUID;
import org.joml.Vector3f;

public interface MountRenderer
extends BehaviorRenderer,
RenderQueues<Mount> {

    public static interface Mount {
        public int getPivotId();

        public UUID getPivotUuid();

        public int getMountId();

        public UUID getMountUuid();

        public DataTracker<Vector3f> getPosition();

        public DataTracker<Byte> getYaw();

        public DataTracker<Float> getMaxHealth();

        public DataTracker<Float> getHealth();

        public CollectionDataTracker<Integer> getPassengers();
    }
}

