/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 *  org.joml.Vector3f
 */
package com.ticxo.modelengine.core.model.bone.render;

import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.bone.BoneBehaviorTypes;
import com.ticxo.modelengine.api.model.bone.ModelBone;
import com.ticxo.modelengine.api.model.bone.behavior.BoneBehavior;
import com.ticxo.modelengine.api.model.bone.render.renderer.SubHitboxRenderer;
import com.ticxo.modelengine.api.model.bone.type.SubHitbox;
import com.ticxo.modelengine.api.nms.RenderParsers;
import com.ticxo.modelengine.api.utils.data.tracker.DataTracker;
import com.ticxo.modelengine.api.utils.data.tracker.UpdateDataTracker;
import com.ticxo.modelengine.core.model.bone.render.AbstractBehaviorRenderer;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.Generated;
import org.joml.Vector3f;

public class SubHitboxRendererImpl
extends AbstractBehaviorRenderer
implements SubHitboxRenderer {
    private final Map<String, SubHitboxRenderer.SubHitbox> spawnQueue = new HashMap<String, SubHitboxRenderer.SubHitbox>();
    private final Map<String, SubHitboxRenderer.SubHitbox> rendered = new HashMap<String, SubHitboxRenderer.SubHitbox>();
    private final Map<String, SubHitboxRenderer.SubHitbox> destroyQueue = new HashMap<String, SubHitboxRenderer.SubHitbox>();
    private boolean initialized;

    public SubHitboxRendererImpl(ActiveModel activeModel) {
        super(activeModel);
    }

    @Override
    public void initialize() {
        for (Map.Entry<String, ModelBone> boneEntry : this.activeModel.getBones().entrySet()) {
            String boneId = boneEntry.getKey();
            ModelBone modelBone = boneEntry.getValue();
            this.create(boneId, modelBone);
        }
        this.initialized = true;
    }

    private void create(String boneId, ModelBone modelBone) {
        Optional<? extends SubHitbox> maybeData = modelBone.getBoneBehavior(BoneBehaviorTypes.SUB_HITBOX);
        if (maybeData.isEmpty()) {
            return;
        }
        BoneBehavior subHitboxData = (BoneBehavior)((Object)maybeData.get());
        if (((SubHitbox)((Object)subHitboxData)).isOBB()) {
            return;
        }
        SubHitboxImpl subHitbox = new SubHitboxImpl(this.nmsHandler.getEntityHandler().getNextEntityId(), ((SubHitbox)((Object)subHitboxData)).getHitboxId(), UUID.randomUUID(), UUID.randomUUID());
        subHitbox.position.set(((SubHitbox)((Object)subHitboxData)).getLocation());
        subHitbox.width.set(Float.valueOf(((SubHitbox)((Object)subHitboxData)).getDimension().x));
        subHitbox.height.set(Float.valueOf(((SubHitbox)((Object)subHitboxData)).getDimension().y));
        this.spawnQueue.put(boneId, subHitbox);
        this.destroyQueue.remove(boneId);
    }

    @Override
    public void readBoneData() {
        if (!this.initialized) {
            return;
        }
        this.destroyQueue.putAll(this.rendered);
        for (Map.Entry<String, ModelBone> boneEntry : this.activeModel.getBones().entrySet()) {
            String boneId = boneEntry.getKey();
            ModelBone modelBone = boneEntry.getValue();
            SubHitboxRenderer.SubHitbox renderer = (SubHitboxRenderer.SubHitbox)this.getQueued(boneId);
            if (renderer != null) {
                this.read(boneId, renderer, modelBone);
                continue;
            }
            this.create(boneId, modelBone);
        }
    }

    private void read(String boneId, SubHitboxRenderer.SubHitbox subHitbox, ModelBone modelBone) {
        Optional<? extends SubHitbox> maybeData = modelBone.getBoneBehavior(BoneBehaviorTypes.SUB_HITBOX);
        maybeData.ifPresent(subHitboxData -> {
            subHitbox.getPosition().set(((SubHitbox)((Object)subHitboxData)).getLocation());
            subHitbox.getWidth().set(Float.valueOf(((SubHitbox)((Object)subHitboxData)).getDimension().x));
            subHitbox.getHeight().set(Float.valueOf(((SubHitbox)((Object)subHitboxData)).getDimension().y));
            this.destroyQueue.remove(boneId);
        });
    }

    @Override
    public void sendToClient(RenderParsers parsers) {
        if (!this.initialized) {
            return;
        }
        this.destroyQueue.keySet().forEach(this.rendered::remove);
        parsers.getBehaviorParser(this).sendToClients(this);
        this.rendered.putAll(this.spawnQueue);
        this.spawnQueue.clear();
        this.destroyQueue.clear();
    }

    @Override
    public void destroy(RenderParsers parsers) {
        if (!this.initialized) {
            return;
        }
        parsers.getBehaviorParser(this).destroy(this);
    }

    @Override
    @Generated
    public Map<String, SubHitboxRenderer.SubHitbox> getSpawnQueue() {
        return this.spawnQueue;
    }

    @Override
    @Generated
    public Map<String, SubHitboxRenderer.SubHitbox> getRendered() {
        return this.rendered;
    }

    @Override
    @Generated
    public Map<String, SubHitboxRenderer.SubHitbox> getDestroyQueue() {
        return this.destroyQueue;
    }

    public static class SubHitboxImpl
    implements SubHitboxRenderer.SubHitbox {
        private final int pivotId;
        private final int hitboxId;
        private final UUID pivotUuid;
        private final UUID hitboxUuid;
        private final DataTracker<Vector3f> position = new UpdateDataTracker<Vector3f>(new Vector3f(), Vector3f::set);
        private final DataTracker<Float> width = new DataTracker();
        private final DataTracker<Float> height = new DataTracker();

        @Generated
        public SubHitboxImpl(int pivotId, int hitboxId, UUID pivotUuid, UUID hitboxUuid) {
            this.pivotId = pivotId;
            this.hitboxId = hitboxId;
            this.pivotUuid = pivotUuid;
            this.hitboxUuid = hitboxUuid;
        }

        @Override
        @Generated
        public int getPivotId() {
            return this.pivotId;
        }

        @Override
        @Generated
        public int getHitboxId() {
            return this.hitboxId;
        }

        @Override
        @Generated
        public UUID getPivotUuid() {
            return this.pivotUuid;
        }

        @Override
        @Generated
        public UUID getHitboxUuid() {
            return this.hitboxUuid;
        }

        @Override
        @Generated
        public DataTracker<Vector3f> getPosition() {
            return this.position;
        }

        @Override
        @Generated
        public DataTracker<Float> getWidth() {
            return this.width;
        }

        @Override
        @Generated
        public DataTracker<Float> getHeight() {
            return this.height;
        }
    }
}

