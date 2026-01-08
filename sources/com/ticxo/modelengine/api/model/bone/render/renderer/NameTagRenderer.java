/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Display$Billboard
 *  org.joml.Vector3f
 */
package com.ticxo.modelengine.api.model.bone.render.renderer;

import com.ticxo.modelengine.api.model.bone.render.BehaviorRenderer;
import com.ticxo.modelengine.api.model.bone.render.renderer.RenderQueues;
import com.ticxo.modelengine.api.utils.data.tracker.DataTracker;
import java.util.UUID;
import org.bukkit.entity.Display;
import org.joml.Vector3f;

public interface NameTagRenderer
extends BehaviorRenderer,
RenderQueues<NameTag> {

    public static interface NameTag {
        public static final int DEFAULT_BACKGROUND_COLOR = 0x40000000;

        public int getPivotId();

        public UUID getPivotUuid();

        public int getTagId();

        public UUID getTagUuid();

        public DataTracker<Vector3f> getPosition();

        public DataTracker<String> getJsonString();

        public DataTracker<Boolean> getVisibility();

        public DataTracker<Integer> getBackgroundColor();

        public DataTracker<Display.Billboard> getBillboard();

        public DataTracker<Byte> getTextOpacity();

        public DataTracker<Integer> getLineWidth();

        public DataTracker<Vector3f> getScale();

        public DataTracker<Byte> getStyle();

        default public boolean isDirty() {
            return this.getJsonString().isDirty() || this.getVisibility().isDirty() || this.getBillboard().isDirty() || this.getBackgroundColor().isDirty() || this.getTextOpacity().isDirty() || this.getLineWidth().isDirty() || this.getStyle().isDirty() || this.getScale().isDirty();
        }

        default public void clearDirty() {
            this.getJsonString().clearDirty();
            this.getVisibility().clearDirty();
            this.getBillboard().clearDirty();
            this.getBackgroundColor().clearDirty();
            this.getTextOpacity().clearDirty();
            this.getLineWidth().clearDirty();
            this.getScale().clearDirty();
            this.getScale().clearDirty();
        }
    }
}

