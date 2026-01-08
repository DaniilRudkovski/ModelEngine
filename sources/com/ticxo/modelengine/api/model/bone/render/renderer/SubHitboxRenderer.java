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

public interface SubHitboxRenderer
extends BehaviorRenderer,
RenderQueues<SubHitbox> {

    public static interface SubHitbox {
        public int getPivotId();

        public int getHitboxId();

        public UUID getPivotUuid();

        public UUID getHitboxUuid();

        public DataTracker<Vector3f> getPosition();

        public DataTracker<Float> getWidth();

        public DataTracker<Float> getHeight();

        default public boolean isDirty() {
            return this.getWidth().isDirty() || this.getHeight().isDirty();
        }

        default public void clearDirty() {
            this.getWidth().clearDirty();
            this.getHeight().clearDirty();
        }
    }
}

