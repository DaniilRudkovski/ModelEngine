/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 *  org.bukkit.entity.Entity
 *  org.joml.Vector3f
 */
package com.ticxo.modelengine.core.model.bone.render;

import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.bone.BoneBehaviorTypes;
import com.ticxo.modelengine.api.model.bone.ModelBone;
import com.ticxo.modelengine.api.model.bone.behavior.BoneBehavior;
import com.ticxo.modelengine.api.model.bone.render.renderer.LeashRenderer;
import com.ticxo.modelengine.api.model.bone.type.Leash;
import com.ticxo.modelengine.api.nms.RenderParsers;
import com.ticxo.modelengine.api.utils.data.tracker.DataTracker;
import com.ticxo.modelengine.api.utils.data.tracker.UpdateDataTracker;
import com.ticxo.modelengine.core.model.bone.render.AbstractBehaviorRenderer;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.Generated;
import org.bukkit.entity.Entity;
import org.joml.Vector3f;

public class LeashRendererImpl
extends AbstractBehaviorRenderer
implements LeashRenderer {
    private final Map<String, LeashRenderer.Leash> spawnQueue = new HashMap<String, LeashRenderer.Leash>();
    private final Map<String, LeashRenderer.Leash> rendered = new HashMap<String, LeashRenderer.Leash>();
    private final Map<String, LeashRenderer.Leash> destroyQueue = new HashMap<String, LeashRenderer.Leash>();
    private boolean initialized;

    public LeashRendererImpl(ActiveModel activeModel) {
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
        Optional<? extends Leash> maybeData = modelBone.getBoneBehavior(BoneBehaviorTypes.LEASH);
        if (maybeData.isEmpty()) {
            return;
        }
        BoneBehavior leashData = (BoneBehavior)((Object)maybeData.get());
        LeashImpl leash = new LeashImpl(this.nmsHandler.getEntityHandler().getNextEntityId(), ((Leash)((Object)leashData)).getId());
        leash.position.set(((Leash)((Object)leashData)).getLocation());
        leash.connected.set(this.getConnectedId((Leash)((Object)leashData)));
        this.spawnQueue.put(boneId, leash);
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
            LeashRenderer.Leash renderer = (LeashRenderer.Leash)this.getQueued(boneId);
            if (renderer != null) {
                this.read(boneId, renderer, modelBone);
                continue;
            }
            this.create(boneId, modelBone);
        }
    }

    private void read(String boneId, LeashRenderer.Leash leash, ModelBone modelBone) {
        Optional<? extends Leash> maybeData = modelBone.getBoneBehavior(BoneBehaviorTypes.LEASH);
        maybeData.ifPresent(leashData -> {
            leash.getPosition().set(((Leash)((Object)leashData)).getLocation());
            leash.getConnected().set(this.getConnectedId((Leash)((Object)leashData)));
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

    private int getConnectedId(Leash leash) {
        Entity entity = leash.getConnectedEntity();
        if (entity != null) {
            return entity.getEntityId();
        }
        BoneBehavior other = (BoneBehavior)leash.getConnectedLeash();
        if (other != null) {
            return ((Leash)((Object)other)).getId();
        }
        return -1;
    }

    @Override
    @Generated
    public Map<String, LeashRenderer.Leash> getSpawnQueue() {
        return this.spawnQueue;
    }

    @Override
    @Generated
    public Map<String, LeashRenderer.Leash> getRendered() {
        return this.rendered;
    }

    @Override
    @Generated
    public Map<String, LeashRenderer.Leash> getDestroyQueue() {
        return this.destroyQueue;
    }

    public static class LeashImpl
    implements LeashRenderer.Leash {
        private final int pivotId;
        private final UUID pivotUUID = UUID.randomUUID();
        private final int leashId;
        private final UUID leastUUID = UUID.randomUUID();
        private final DataTracker<Vector3f> position = new UpdateDataTracker<Vector3f>(new Vector3f(), Vector3f::set);
        private final DataTracker<Integer> connected = new DataTracker();

        @Generated
        public LeashImpl(int pivotId, int leashId) {
            this.pivotId = pivotId;
            this.leashId = leashId;
        }

        @Override
        @Generated
        public int getPivotId() {
            return this.pivotId;
        }

        @Override
        @Generated
        public UUID getPivotUUID() {
            return this.pivotUUID;
        }

        @Override
        @Generated
        public int getLeashId() {
            return this.leashId;
        }

        @Override
        @Generated
        public UUID getLeastUUID() {
            return this.leastUUID;
        }

        @Override
        @Generated
        public DataTracker<Vector3f> getPosition() {
            return this.position;
        }

        @Override
        @Generated
        public DataTracker<Integer> getConnected() {
            return this.connected;
        }
    }
}

