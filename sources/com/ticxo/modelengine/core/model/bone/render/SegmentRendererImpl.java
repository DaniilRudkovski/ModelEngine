/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 *  org.bukkit.entity.ItemDisplay$ItemDisplayTransform
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Quaternionf
 *  org.joml.Vector3f
 */
package com.ticxo.modelengine.core.model.bone.render;

import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.bone.BoneBehaviorTypes;
import com.ticxo.modelengine.api.model.bone.BoneItems;
import com.ticxo.modelengine.api.model.bone.ModelBone;
import com.ticxo.modelengine.api.model.bone.behavior.BoneBehavior;
import com.ticxo.modelengine.api.model.bone.render.renderer.SegmentRenderer;
import com.ticxo.modelengine.api.model.bone.type.HeldItem;
import com.ticxo.modelengine.api.model.bone.type.Segment;
import com.ticxo.modelengine.api.model.render.DisplayBone;
import com.ticxo.modelengine.api.nms.RenderParsers;
import com.ticxo.modelengine.api.nms.entity.EntityHandler;
import com.ticxo.modelengine.api.utils.data.tracker.CollectionDataTracker;
import com.ticxo.modelengine.api.utils.data.tracker.DataTracker;
import com.ticxo.modelengine.api.utils.data.tracker.UpdateDataTracker;
import com.ticxo.modelengine.api.utils.math.SafeTransform;
import com.ticxo.modelengine.core.model.bone.render.AbstractBehaviorRenderer;
import com.ticxo.modelengine.core.model.render.DisplayBoneImpl;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.Generated;
import org.bukkit.entity.ItemDisplay;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class SegmentRendererImpl
extends AbstractBehaviorRenderer
implements SegmentRenderer {
    private final EntityHandler entityHandler;
    private final Map<String, SegmentRenderer.Pivot> spawnQueue = new HashMap<String, SegmentRenderer.Pivot>();
    private final Map<String, SegmentRenderer.Pivot> rendered = new HashMap<String, SegmentRenderer.Pivot>();
    private final Map<String, SegmentRenderer.Pivot> destroyQueue = new HashMap<String, SegmentRenderer.Pivot>();
    private boolean initialized;
    private final Map<String, ModelBone> unhandled = new HashMap<String, ModelBone>();
    private final Map<String, SegmentRenderer.SegmentDisplayBone> bones = new HashMap<String, SegmentRenderer.SegmentDisplayBone>();

    public SegmentRendererImpl(ActiveModel activeModel) {
        super(activeModel);
        this.entityHandler = this.nmsHandler.getEntityHandler();
    }

    @Override
    public void initialize() {
        for (Map.Entry<String, ModelBone> entry : this.activeModel.getBones().entrySet()) {
            this.createPivotOrBone(entry.getKey(), entry.getValue(), this.unhandled);
        }
        for (Map.Entry<String, ModelBone> entry : this.unhandled.entrySet()) {
            this.createBone(entry.getKey(), entry.getValue(), null);
        }
        this.unhandled.clear();
        this.initialized = true;
    }

    private void createPivotOrBone(String boneId, ModelBone modelBone, Map<String, ModelBone> unhandled) {
        if (!this.createPivot(boneId, modelBone)) {
            this.createBone(boneId, modelBone, unhandled);
        }
    }

    private boolean createPivot(String boneId, ModelBone modelBone) {
        Optional<? extends Segment> maybeData = modelBone.getBoneBehavior(BoneBehaviorTypes.SEGMENT);
        if (maybeData.isEmpty()) {
            return false;
        }
        BoneBehavior segmentData = (BoneBehavior)((Object)maybeData.get());
        PivotImpl pivot = new PivotImpl(this.entityHandler.getNextEntityId());
        pivot.getPosition().set(((Segment)((Object)segmentData)).getWorldLocation());
        pivot.getYaw().set(Float.valueOf(-modelBone.getYaw() * ((float)Math.PI / 180)));
        this.spawnQueue.put(boneId, pivot);
        this.destroyQueue.remove(boneId);
        return true;
    }

    private void createBone(String boneId, ModelBone modelBone, @Nullable Map<String, ModelBone> unhandled) {
        if (!modelBone.isRenderer() || modelBone.getPivot() == null) {
            return;
        }
        PivotImpl pivot = this.getPivot(modelBone.getPivot().getUniqueBoneId());
        if (pivot == null) {
            if (unhandled != null) {
                unhandled.put(boneId, modelBone);
            }
            return;
        }
        SafeTransform transform = modelBone.getGlobalTransform();
        BoneItems models = modelBone.getModels();
        SegmentDisplayBoneImpl bone = new SegmentDisplayBoneImpl(pivot);
        bone.getGlowing().set(modelBone.isGlowing());
        bone.getGlowColor().set(modelBone.getGlowColor());
        bone.getBrightness().set(modelBone.getBrightness());
        bone.getBillboard().set(modelBone.getBillboard());
        bone.getVisibility().set(modelBone.isVisible());
        bone.getPosition().set(transform.getPosition().rotateY(pivot.yaw.get().floatValue()));
        bone.getLeftRotation().set(transform.getLeftQuaternion().rotateLocalY(pivot.yaw.get().floatValue()));
        bone.getScale().set(transform.getScale());
        bone.getRightRotation().set(transform.getRightQuaternion());
        bone.getDisplay().set(ItemDisplay.ItemDisplayTransform.HEAD);
        bone.getSnapshotHandler().recordSnapshot();
        models.forEach((uuid, stack) -> {
            DisplayBoneImpl.BoneDataImpl data = new DisplayBoneImpl.BoneDataImpl(this.entityHandler.getNextEntityId(), bone);
            data.getModel().set(stack.clone());
            bone.getModel().put((Integer)uuid, data);
            pivot.passengers.add(data.getId());
        });
        this.initializeSpecialBehaviorRender(modelBone, bone);
        this.bones.put(boneId, bone);
        pivot.spawnQueue.put(boneId, bone);
        pivot.destroyQueue.remove(boneId);
    }

    private void initializeSpecialBehaviorRender(ModelBone modelBone, DisplayBone bone) {
        modelBone.getBoneBehavior(BoneBehaviorTypes.ITEM).ifPresent(item -> bone.getDisplay().set(((HeldItem)((Object)item)).getDisplay()));
        modelBone.getBoneBehavior(BoneBehaviorTypes.PLAYER_LIMB).ifPresent(limb -> bone.getDisplay().set(ItemDisplay.ItemDisplayTransform.THIRDPERSON_RIGHTHAND));
    }

    @Nullable
    private PivotImpl getPivot(String boneId) {
        SegmentRenderer.Pivot pivot = (SegmentRenderer.Pivot)this.getQueued(boneId);
        if (pivot instanceof PivotImpl) {
            PivotImpl impl = (PivotImpl)pivot;
            return impl;
        }
        return null;
    }

    @Override
    public void readBoneData() {
        if (!this.initialized) {
            return;
        }
        this.destroyQueue.putAll(this.rendered);
        ArrayList<Runnable> boneReadQueue = new ArrayList<Runnable>();
        ArrayList<Runnable> createQueue = new ArrayList<Runnable>();
        for (Map.Entry<String, ModelBone> entry : this.activeModel.getBones().entrySet()) {
            String boneId = entry.getKey();
            ModelBone modelBone = entry.getValue();
            SegmentRenderer.Pivot pivot2 = (SegmentRenderer.Pivot)this.getQueued(boneId);
            if (pivot2 != null) {
                this.readPivotBone(boneId, pivot2, modelBone);
                continue;
            }
            SegmentRenderer.SegmentDisplayBone displayBone = this.bones.get(boneId);
            if (displayBone != null) {
                boneReadQueue.add(() -> this.readDisplayBone(boneId, displayBone, modelBone));
                continue;
            }
            createQueue.add(() -> this.createPivotOrBone(boneId, modelBone, this.unhandled));
        }
        boneReadQueue.forEach(Runnable::run);
        createQueue.forEach(Runnable::run);
        for (Map.Entry<String, ModelBone> entry : this.unhandled.entrySet()) {
            this.createBone(entry.getKey(), entry.getValue(), null);
        }
        this.unhandled.clear();
        this.spawnQueue.forEach((pivotName, pivot) -> pivot.getDestroyQueue().forEach((s, bone) -> bone.getModel().keySet().forEach(id -> pivot.getPassengers().remove(id))));
        this.rendered.forEach((pivotName, pivot) -> pivot.getDestroyQueue().forEach((s, bone) -> bone.getModel().keySet().forEach(id -> pivot.getPassengers().remove(id))));
    }

    private void readPivotBone(String boneId, SegmentRenderer.Pivot pivot, ModelBone modelBone) {
        Optional<? extends Segment> maybeData = modelBone.getBoneBehavior(BoneBehaviorTypes.SEGMENT);
        maybeData.ifPresent(segmentData -> {
            pivot.getPosition().set(((Segment)((Object)segmentData)).getWorldLocation());
            pivot.getYaw().set(Float.valueOf(-modelBone.getYaw() * ((float)Math.PI / 180)));
            this.destroyQueue.remove(boneId);
        });
    }

    private void readDisplayBone(String boneId, SegmentRenderer.SegmentDisplayBone bone, ModelBone modelBone) {
        DataTracker<Boolean> shouldRender = bone.getRender();
        shouldRender.set(!modelBone.isEffectivelyInvisible());
        SegmentRenderer.Pivot pivot = bone.getPivot();
        if (shouldRender.get().booleanValue() || shouldRender.isDirty()) {
            SafeTransform transform = modelBone.getGlobalTransform();
            bone.getStep().set(modelBone.pollModelScaleChanged() || modelBone.shouldStep() || shouldRender.isDirty());
            bone.getPosition().set(transform.getPosition().rotateY(pivot.getYaw().get().floatValue()));
            bone.getLeftRotation().set(transform.getLeftQuaternion().rotateLocalY(pivot.getYaw().get().floatValue(), new Quaternionf()));
            bone.getScale().set(transform.getScale());
            bone.getRightRotation().set(transform.getRightQuaternion());
            bone.getBillboard().set(modelBone.getBillboard());
            bone.getVisibility().set(modelBone.isVisible());
            bone.getGlowing().set(modelBone.isGlowing());
            bone.getGlowColor().set(modelBone.getGlowColor());
            bone.getBrightness().set(modelBone.getBrightness());
            bone.getSnapshotHandler().recordSnapshot();
            BoneItems models = modelBone.getModels();
            if (models.isDirty()) {
                models.clearDirty();
                ((SegmentDisplayBoneImpl)bone).updateBoneData(this.entityHandler, pivot.getPassengers(), models);
            }
            this.updateSpecialBehaviorRender(modelBone, bone);
        }
        shouldRender.clearDirty();
        pivot.getDestroyQueue().remove(boneId);
    }

    private void updateSpecialBehaviorRender(ModelBone modelBone, DisplayBone bone) {
        modelBone.getBoneBehavior(BoneBehaviorTypes.ITEM).ifPresent(item -> bone.getDisplay().set(((HeldItem)((Object)item)).getDisplay()));
    }

    @Override
    public void sendToClient(RenderParsers parsers) {
        if (!this.initialized) {
            return;
        }
        this.destroyQueue.keySet().forEach(s -> {
            SegmentRenderer.Pivot pivot = this.rendered.remove(s);
            if (pivot != null) {
                pivot.getSpawnQueue().forEach((s1, bone) -> this.bones.remove(s1));
                pivot.getRendered().forEach((s1, bone) -> this.bones.remove(s1));
                pivot.getDestroyQueue().forEach((s1, bone) -> this.bones.remove(s1));
            }
        });
        parsers.getBehaviorParser(this).sendToClients(this);
        this.rendered.putAll(this.spawnQueue);
        this.spawnQueue.clear();
        this.destroyQueue.clear();
        this.rendered.forEach((s, pivot) -> {
            pivot.getDestroyQueue().forEach((s1, bone) -> {
                pivot.getRendered().remove(s1);
                this.bones.remove(s1);
            });
            pivot.getRendered().putAll(pivot.getSpawnQueue());
            pivot.getSpawnQueue().clear();
        });
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
    public Map<String, SegmentRenderer.Pivot> getSpawnQueue() {
        return this.spawnQueue;
    }

    @Override
    @Generated
    public Map<String, SegmentRenderer.Pivot> getRendered() {
        return this.rendered;
    }

    @Override
    @Generated
    public Map<String, SegmentRenderer.Pivot> getDestroyQueue() {
        return this.destroyQueue;
    }

    public static class PivotImpl
    implements SegmentRenderer.Pivot {
        private final Map<String, SegmentRenderer.SegmentDisplayBone> spawnQueue = new HashMap<String, SegmentRenderer.SegmentDisplayBone>();
        private final Map<String, SegmentRenderer.SegmentDisplayBone> rendered = new HashMap<String, SegmentRenderer.SegmentDisplayBone>();
        private final Map<String, SegmentRenderer.SegmentDisplayBone> destroyQueue = new HashMap<String, SegmentRenderer.SegmentDisplayBone>();
        private final int id;
        private final UUID uuid = UUID.randomUUID();
        private final DataTracker<Vector3f> position = new UpdateDataTracker<Vector3f>(new Vector3f(), Vector3f::set);
        private final DataTracker<Float> yaw = new DataTracker();
        private final CollectionDataTracker<Integer> passengers = new CollectionDataTracker(new HashSet());

        @Override
        public void clearDirty() {
            this.yaw.clearDirty();
            this.passengers.clearDirty();
        }

        @Override
        @Generated
        public int getId() {
            return this.id;
        }

        @Override
        @Generated
        public UUID getUuid() {
            return this.uuid;
        }

        @Override
        @Generated
        public DataTracker<Vector3f> getPosition() {
            return this.position;
        }

        @Override
        @Generated
        public DataTracker<Float> getYaw() {
            return this.yaw;
        }

        @Override
        @Generated
        public CollectionDataTracker<Integer> getPassengers() {
            return this.passengers;
        }

        @Generated
        public PivotImpl(int id) {
            this.id = id;
        }

        @Override
        @Generated
        public Map<String, SegmentRenderer.SegmentDisplayBone> getSpawnQueue() {
            return this.spawnQueue;
        }

        @Override
        @Generated
        public Map<String, SegmentRenderer.SegmentDisplayBone> getRendered() {
            return this.rendered;
        }

        @Override
        @Generated
        public Map<String, SegmentRenderer.SegmentDisplayBone> getDestroyQueue() {
            return this.destroyQueue;
        }
    }

    public static class SegmentDisplayBoneImpl
    extends DisplayBoneImpl
    implements SegmentRenderer.SegmentDisplayBone {
        private final SegmentRenderer.Pivot pivot;

        @Override
        protected void updateBoneData(EntityHandler entityHandler, CollectionDataTracker<Integer> passengerCollector, BoneItems boneItems) {
            super.updateBoneData(entityHandler, passengerCollector, boneItems);
        }

        @Override
        @Generated
        public SegmentRenderer.Pivot getPivot() {
            return this.pivot;
        }

        @Generated
        public SegmentDisplayBoneImpl(SegmentRenderer.Pivot pivot) {
            this.pivot = pivot;
        }
    }
}

