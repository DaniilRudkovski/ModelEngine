/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joml.Vector3f
 */
package com.ticxo.modelengine.api.model.bone.render.renderer;

import com.ticxo.modelengine.api.model.bone.render.BehaviorRenderer;
import com.ticxo.modelengine.api.model.bone.render.renderer.RenderQueues;
import com.ticxo.modelengine.api.model.render.DisplayBone;
import com.ticxo.modelengine.api.utils.data.tracker.CollectionDataTracker;
import com.ticxo.modelengine.api.utils.data.tracker.DataTracker;
import java.util.UUID;
import org.joml.Vector3f;

public interface SegmentRenderer
extends BehaviorRenderer,
RenderQueues<Pivot> {

    public static interface SegmentDisplayBone
    extends DisplayBone {
        public Pivot getPivot();
    }

    public static interface Pivot
    extends RenderQueues<SegmentDisplayBone> {
        public int getId();

        public UUID getUuid();

        public void clearDirty();

        public DataTracker<Vector3f> getPosition();

        public DataTracker<Float> getYaw();

        public CollectionDataTracker<Integer> getPassengers();
    }
}

