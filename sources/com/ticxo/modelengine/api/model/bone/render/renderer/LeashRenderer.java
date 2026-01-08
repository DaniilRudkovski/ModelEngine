/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joml.Vector3f
 */
package com.ticxo.modelengine.api.model.bone.render.renderer;

import com.ticxo.modelengine.api.model.bone.render.BehaviorRenderer;
import com.ticxo.modelengine.api.model.bone.render.renderer.RenderQueues;
import com.ticxo.modelengine.api.utils.data.tracker.DataTracker;
import java.util.UUID;
import org.joml.Vector3f;

public interface LeashRenderer
extends BehaviorRenderer,
RenderQueues<Leash> {

    public static interface Leash {
        public int getPivotId();

        public UUID getPivotUUID();

        public int getLeashId();

        public UUID getLeastUUID();

        public DataTracker<Vector3f> getPosition();

        public DataTracker<Integer> getConnected();
    }
}

