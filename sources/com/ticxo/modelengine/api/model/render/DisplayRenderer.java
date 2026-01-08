/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joml.Vector3f
 */
package com.ticxo.modelengine.api.model.render;

import com.ticxo.modelengine.api.model.bone.render.renderer.RenderQueues;
import com.ticxo.modelengine.api.model.render.DisplayBone;
import com.ticxo.modelengine.api.model.render.DisplayFire;
import com.ticxo.modelengine.api.model.render.ModelRenderer;
import com.ticxo.modelengine.api.utils.data.PooledCollection;
import com.ticxo.modelengine.api.utils.data.tracker.CollectionDataTracker;
import com.ticxo.modelengine.api.utils.data.tracker.DataTracker;
import java.util.UUID;
import org.joml.Vector3f;

public interface DisplayRenderer
extends ModelRenderer,
RenderQueues<DisplayBone> {
    public int getTick();

    public Pivot getPivot();

    public Hitbox getHitbox();

    public void pushFullUpdate(UUID var1);

    public boolean pollFullUpdate(UUID var1);

    public static interface Hitbox {
        public int getPivotId();

        public UUID getPivotUuid();

        public int getHitboxId();

        public UUID getHitboxUuid();

        public int getShadowId();

        public UUID getShadowUuid();

        public void clearDirty();

        public DataTracker<Vector3f> getPosition();

        public DataTracker<Float> getWidth();

        public DataTracker<Float> getHeight();

        public DataTracker<Float> getShadowRadius();

        public DataTracker<Boolean> getHitboxVisible();

        public DataTracker<Boolean> getShadowVisible();

        public DataTracker<Boolean> getFireVisible();

        public PooledCollection<DisplayFire> getFireDisplay();

        default public boolean isHitboxDirty() {
            return this.getWidth().isDirty() || this.getHeight().isDirty();
        }

        default public boolean isHitboxVisible() {
            return this.getHitboxVisible().get();
        }

        default public boolean isShadowVisible() {
            return this.getShadowVisible().get();
        }

        default public boolean isFireVisible() {
            return this.getFireVisible().get();
        }

        default public boolean isPivotVisible() {
            return this.isHitboxVisible() || this.isShadowVisible() || this.isFireVisible();
        }
    }

    public static interface Pivot {
        public int getId();

        public UUID getUuid();

        public void clearDirty();

        public boolean isOverridden();

        public DataTracker<Vector3f> getPosition();

        public CollectionDataTracker<Integer> getPassengers();
    }
}

