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
import com.ticxo.modelengine.api.model.bone.render.renderer.MountRenderer;
import com.ticxo.modelengine.api.model.bone.type.Mount;
import com.ticxo.modelengine.api.nms.RenderParsers;
import com.ticxo.modelengine.api.utils.data.tracker.CollectionDataTracker;
import com.ticxo.modelengine.api.utils.data.tracker.DataTracker;
import com.ticxo.modelengine.api.utils.data.tracker.UpdateDataTracker;
import com.ticxo.modelengine.api.utils.math.TMath;
import com.ticxo.modelengine.core.model.bone.render.AbstractBehaviorRenderer;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.Generated;
import org.bukkit.entity.Entity;
import org.joml.Vector3f;

public class MountRendererImpl
extends AbstractBehaviorRenderer
implements MountRenderer {
    private final Map<String, MountRenderer.Mount> spawnQueue = new HashMap<String, MountRenderer.Mount>();
    private final Map<String, MountRenderer.Mount> rendered = new HashMap<String, MountRenderer.Mount>();
    private final Map<String, MountRenderer.Mount> destroyQueue = new HashMap<String, MountRenderer.Mount>();
    private boolean initialized;

    public MountRendererImpl(ActiveModel activeModel) {
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
        Optional<? extends Mount> maybeData = modelBone.getBoneBehavior(BoneBehaviorTypes.MOUNT);
        if (maybeData.isEmpty()) {
            return;
        }
        BoneBehavior mountData = (BoneBehavior)((Object)maybeData.get());
        MountImpl mount = new MountImpl(this.nmsHandler.getEntityHandler().getNextEntityId(), UUID.randomUUID(), this.nmsHandler.getEntityHandler().getNextEntityId(), UUID.randomUUID());
        mount.position.set(((Mount)((Object)mountData)).getGlobalLocation());
        mount.yaw.set(TMath.rotToByte(modelBone.getYaw()));
        mount.health.set(Float.valueOf(modelBone.getActiveModel().getModeledEntity().getBase().getHealth()));
        mount.maxHealth.set(Float.valueOf(modelBone.getActiveModel().getModeledEntity().getBase().getMaxHealth()));
        for (Entity entity : ((Mount)((Object)mountData)).getPassengers()) {
            ((Collection)mount.passengers.get()).add(entity.getEntityId());
        }
        this.spawnQueue.put(boneId, mount);
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
            MountRenderer.Mount renderer = (MountRenderer.Mount)this.getQueued(boneId);
            if (renderer != null) {
                this.read(boneId, renderer, modelBone);
                continue;
            }
            this.create(boneId, modelBone);
        }
    }

    private void read(String boneId, MountRenderer.Mount mount, ModelBone modelBone) {
        mount.getYaw().set(TMath.rotToByte(modelBone.getYaw()));
        mount.getHealth().set(Float.valueOf(modelBone.getActiveModel().getModeledEntity().getBase().getHealth()));
        mount.getMaxHealth().set(Float.valueOf(modelBone.getActiveModel().getModeledEntity().getBase().getMaxHealth()));
        Optional<? extends Mount> maybeData = modelBone.getBoneBehavior(BoneBehaviorTypes.MOUNT);
        maybeData.ifPresent(mountData -> {
            mount.getPosition().set(((Mount)((Object)mountData)).getGlobalLocation());
            HashSet<Integer> newIds = new HashSet<Integer>();
            for (Entity entity : ((Mount)((Object)mountData)).getPassengers()) {
                newIds.add(entity.getEntityId());
            }
            mount.getPassengers().retainAll(newIds);
            mount.getPassengers().addAll((Collection<Integer>)newIds);
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
    public Map<String, MountRenderer.Mount> getSpawnQueue() {
        return this.spawnQueue;
    }

    @Override
    @Generated
    public Map<String, MountRenderer.Mount> getRendered() {
        return this.rendered;
    }

    @Override
    @Generated
    public Map<String, MountRenderer.Mount> getDestroyQueue() {
        return this.destroyQueue;
    }

    public static class MountImpl
    implements MountRenderer.Mount {
        private final int pivotId;
        private final UUID pivotUuid;
        private final int mountId;
        private final UUID mountUuid;
        private final DataTracker<Vector3f> position = new UpdateDataTracker<Vector3f>(new Vector3f(), Vector3f::set);
        private final DataTracker<Float> health = new DataTracker<Float>(Float.valueOf(20.0f));
        private final DataTracker<Float> maxHealth = new DataTracker<Float>(Float.valueOf(20.0f));
        private final DataTracker<Byte> yaw = new DataTracker<Byte>((byte)0);
        private final CollectionDataTracker<Integer> passengers = new CollectionDataTracker(new HashSet());

        @Generated
        public MountImpl(int pivotId, UUID pivotUuid, int mountId, UUID mountUuid) {
            this.pivotId = pivotId;
            this.pivotUuid = pivotUuid;
            this.mountId = mountId;
            this.mountUuid = mountUuid;
        }

        @Override
        @Generated
        public int getPivotId() {
            return this.pivotId;
        }

        @Override
        @Generated
        public UUID getPivotUuid() {
            return this.pivotUuid;
        }

        @Override
        @Generated
        public int getMountId() {
            return this.mountId;
        }

        @Override
        @Generated
        public UUID getMountUuid() {
            return this.mountUuid;
        }

        @Override
        @Generated
        public DataTracker<Vector3f> getPosition() {
            return this.position;
        }

        @Override
        @Generated
        public DataTracker<Float> getHealth() {
            return this.health;
        }

        @Override
        @Generated
        public DataTracker<Float> getMaxHealth() {
            return this.maxHealth;
        }

        @Override
        @Generated
        public DataTracker<Byte> getYaw() {
            return this.yaw;
        }

        @Override
        @Generated
        public CollectionDataTracker<Integer> getPassengers() {
            return this.passengers;
        }
    }
}

