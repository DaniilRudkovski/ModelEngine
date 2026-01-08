/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 *  org.bukkit.entity.ItemDisplay$ItemDisplayTransform
 *  org.bukkit.inventory.ItemStack
 *  org.joml.Quaternionf
 *  org.joml.Vector3f
 */
package com.ticxo.modelengine.core.model.bone.render;

import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.bone.BoneBehaviorTypes;
import com.ticxo.modelengine.api.model.bone.ModelBone;
import com.ticxo.modelengine.api.model.bone.behavior.BoneBehavior;
import com.ticxo.modelengine.api.model.bone.render.renderer.HeldItemRenderer;
import com.ticxo.modelengine.api.model.bone.type.HeldItem;
import com.ticxo.modelengine.api.nms.RenderParsers;
import com.ticxo.modelengine.api.utils.data.tracker.CollectionDataTracker;
import com.ticxo.modelengine.api.utils.data.tracker.DataTracker;
import com.ticxo.modelengine.api.utils.data.tracker.UpdateDataTracker;
import com.ticxo.modelengine.core.model.bone.render.AbstractBehaviorRenderer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.Generated;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.ItemStack;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class HeldItemRendererImpl
extends AbstractBehaviorRenderer
implements HeldItemRenderer {
    private final int id;
    private final UUID uuid;
    private final Map<String, HeldItemRenderer.Item> spawnQueue = new HashMap<String, HeldItemRenderer.Item>();
    private final Map<String, HeldItemRenderer.Item> rendered = new HashMap<String, HeldItemRenderer.Item>();
    private final Map<String, HeldItemRenderer.Item> destroyQueue = new HashMap<String, HeldItemRenderer.Item>();
    private final CollectionDataTracker<Integer> passengers = new CollectionDataTracker(new HashSet());
    private boolean initialized;

    public HeldItemRendererImpl(ActiveModel activeModel) {
        super(activeModel);
        this.id = this.nmsHandler.getEntityHandler().getNextEntityId();
        this.uuid = UUID.randomUUID();
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
        Optional<? extends HeldItem> maybeData = modelBone.getBoneBehavior(BoneBehaviorTypes.ITEM);
        if (maybeData.isEmpty()) {
            return;
        }
        BoneBehavior itemData = (BoneBehavior)((Object)maybeData.get());
        ItemImpl item = new ItemImpl(this.nmsHandler.getEntityHandler().getNextEntityId(), UUID.randomUUID());
        item.position.set(((HeldItem)((Object)itemData)).getLocation());
        item.scale.set(modelBone.getGlobalTransform().getScale());
        item.rotation.set(((HeldItem)((Object)itemData)).getRotation());
        item.model.set(modelBone.isVisible() ? modelBone.getModel() : null);
        item.display.set(((HeldItem)((Object)itemData)).getDisplay());
        item.glowing.set(modelBone.isGlowing());
        item.glowColor.set(modelBone.getGlowColor());
        this.spawnQueue.put(boneId, item);
        this.destroyQueue.remove(boneId);
        this.passengers.add(item.id);
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
            HeldItemRenderer.Item renderer = (HeldItemRenderer.Item)this.getQueued(boneId);
            if (renderer != null) {
                this.read(boneId, renderer, modelBone);
                continue;
            }
            this.create(boneId, modelBone);
        }
        this.destroyQueue.forEach((s, item) -> this.passengers.remove(item.getId()));
    }

    private void read(String boneId, HeldItemRenderer.Item item, ModelBone modelBone) {
        Optional<? extends HeldItem> maybeData = modelBone.getBoneBehavior(BoneBehaviorTypes.ITEM);
        maybeData.ifPresent(heldItem -> {
            item.getPosition().set(((HeldItem)((Object)heldItem)).getLocation());
            item.getScale().set(modelBone.getGlobalTransform().getScale());
            item.getRotation().set(((HeldItem)((Object)heldItem)).getRotation());
            item.getGlowing().set(modelBone.isGlowing());
            item.getGlowColor().set(modelBone.getGlowColor());
            if (!modelBone.isVisible()) {
                item.getModel().set(null);
            } else {
                item.getModel().set(modelBone.getModel());
                if (modelBone.getModelTracker().isDirty()) {
                    modelBone.getModelTracker().clearDirty();
                    item.getModel().markDirty();
                }
            }
            item.getDisplay().set(((HeldItem)((Object)heldItem)).getDisplay());
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
    public Map<String, HeldItemRenderer.Item> getSpawnQueue() {
        return this.spawnQueue;
    }

    @Override
    @Generated
    public Map<String, HeldItemRenderer.Item> getRendered() {
        return this.rendered;
    }

    @Override
    @Generated
    public Map<String, HeldItemRenderer.Item> getDestroyQueue() {
        return this.destroyQueue;
    }

    @Override
    @Generated
    public CollectionDataTracker<Integer> getPassengers() {
        return this.passengers;
    }

    public static class ItemImpl
    implements HeldItemRenderer.Item {
        private final int id;
        private final UUID uuid;
        private final DataTracker<Vector3f> position = new UpdateDataTracker<Vector3f>(new Vector3f(), Vector3f::set);
        private final DataTracker<Vector3f> scale = new UpdateDataTracker<Vector3f>(new Vector3f(), Vector3f::set);
        private final DataTracker<Quaternionf> rotation = new UpdateDataTracker<Quaternionf>(new Quaternionf(), Quaternionf::set);
        private final DataTracker<ItemStack> model = new DataTracker();
        private final DataTracker<ItemDisplay.ItemDisplayTransform> display = new DataTracker<ItemDisplay.ItemDisplayTransform>(ItemDisplay.ItemDisplayTransform.NONE);
        private final DataTracker<Boolean> glowing = new DataTracker<Boolean>(false);
        private final DataTracker<Integer> glowColor = new DataTracker<Integer>(-1);

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
        public DataTracker<Vector3f> getScale() {
            return this.scale;
        }

        @Override
        @Generated
        public DataTracker<Quaternionf> getRotation() {
            return this.rotation;
        }

        @Override
        @Generated
        public DataTracker<ItemStack> getModel() {
            return this.model;
        }

        @Override
        @Generated
        public DataTracker<ItemDisplay.ItemDisplayTransform> getDisplay() {
            return this.display;
        }

        @Override
        @Generated
        public DataTracker<Boolean> getGlowing() {
            return this.glowing;
        }

        @Override
        @Generated
        public DataTracker<Integer> getGlowColor() {
            return this.glowColor;
        }

        @Generated
        public ItemImpl(int id, UUID uuid) {
            this.id = id;
            this.uuid = uuid;
        }
    }
}

