/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Sets
 *  it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
 *  lombok.Generated
 *  org.bukkit.entity.Display$Billboard
 *  org.bukkit.entity.ItemDisplay$ItemDisplayTransform
 *  org.bukkit.inventory.ItemStack
 *  org.joml.Quaternionf
 *  org.joml.Vector3f
 */
package com.ticxo.modelengine.core.model.render;

import com.google.common.collect.Sets;
import com.ticxo.modelengine.api.lod.BoneSnapshotHandler;
import com.ticxo.modelengine.api.model.bone.BoneItems;
import com.ticxo.modelengine.api.model.render.DisplayBone;
import com.ticxo.modelengine.api.nms.entity.EntityHandler;
import com.ticxo.modelengine.api.utils.data.UpdateScheme;
import com.ticxo.modelengine.api.utils.data.tracker.CollectionDataTracker;
import com.ticxo.modelengine.api.utils.data.tracker.DataTracker;
import com.ticxo.modelengine.api.utils.data.tracker.UpdateDataTracker;
import com.ticxo.modelengine.api.utils.logger.TLogger;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import lombok.Generated;
import org.bukkit.entity.Display;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.ItemStack;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class DisplayBoneImpl
implements DisplayBone {
    private final BoneSnapshotHandler snapshotHandler = new BoneSnapshotHandler(this);
    private final DataTracker<Boolean> render = new DataTracker<Boolean>(true);
    private final DataTracker<Boolean> step = new DataTracker<Boolean>(false);
    private final DataTracker<Vector3f> position = new UpdateDataTracker<Vector3f>(new Vector3f(), Vector3f::set, Vector3f::new);
    private final DataTracker<Quaternionf> leftRotation = new UpdateDataTracker<Quaternionf>(new Quaternionf(), Quaternionf::set, Quaternionf::new);
    private final DataTracker<Vector3f> scale = new UpdateDataTracker<Vector3f>(new Vector3f(), Vector3f::set, Vector3f::new);
    private final DataTracker<Quaternionf> rightRotation = new UpdateDataTracker<Quaternionf>(new Quaternionf(), Quaternionf::set, Quaternionf::new);
    private final DataTracker<Display.Billboard> billboard = new DataTracker<Display.Billboard>(Display.Billboard.FIXED);
    private final DataTracker<ItemDisplay.ItemDisplayTransform> display = new DataTracker<ItemDisplay.ItemDisplayTransform>(ItemDisplay.ItemDisplayTransform.NONE);
    private final DataTracker<Boolean> visibility = new DataTracker<Boolean>(true);
    private final DataTracker<Boolean> glowing = new DataTracker<Boolean>(false);
    private final DataTracker<Integer> glowColor = new DataTracker<Integer>(-1);
    private final DataTracker<Integer> brightness = new DataTracker<Integer>(-1);
    private final Map<Integer, DisplayBone.BoneData> model = new Int2ObjectOpenHashMap();
    private final UpdateScheme<DisplayBone.BoneData> modelUpdateScheme = new UpdateScheme();

    protected void updateBoneData(EntityHandler entityHandler, CollectionDataTracker<Integer> passengerCollector, BoneItems boneItems) {
        Map<Integer, ItemStack> newItems = boneItems.getItems();
        Set<Integer> oldKeys = this.model.keySet();
        Set<Integer> nowKeys = newItems.keySet();
        HashSet added = new HashSet(Sets.difference(nowKeys, oldKeys));
        HashSet removed = new HashSet(Sets.difference(oldKeys, nowKeys));
        HashSet reusableData = new HashSet();
        removed.forEach(hash -> reusableData.add(this.model.remove(hash)));
        Iterator iterator = reusableData.iterator();
        added.forEach(hash -> {
            ItemStack nStack = (ItemStack)newItems.get(hash);
            if (nStack == null) {
                TLogger.log("Item is null???????");
                TLogger.log(newItems);
                TLogger.log(hash);
                return;
            }
            ItemStack stack = nStack.clone();
            if (iterator.hasNext()) {
                DisplayBone.BoneData data = (DisplayBone.BoneData)iterator.next();
                int oldHash = data.getModelHash();
                data.getModel().set(stack);
                this.modelUpdateScheme.addUpdated(data);
                this.model.remove(oldHash);
                this.model.put((Integer)hash, data);
            } else {
                BoneDataImpl data = new BoneDataImpl(entityHandler.getNextEntityId(), this);
                data.getModel().set(stack);
                this.model.put((Integer)hash, data);
                this.modelUpdateScheme.addAdded(data);
                passengerCollector.add(data.getId());
            }
        });
        while (iterator.hasNext()) {
            DisplayBone.BoneData data = (DisplayBone.BoneData)iterator.next();
            this.modelUpdateScheme.addRemove(data);
            passengerCollector.remove(data.getId());
        }
    }

    @Override
    @Generated
    public BoneSnapshotHandler getSnapshotHandler() {
        return this.snapshotHandler;
    }

    @Override
    @Generated
    public DataTracker<Boolean> getRender() {
        return this.render;
    }

    @Override
    @Generated
    public DataTracker<Boolean> getStep() {
        return this.step;
    }

    @Override
    @Generated
    public DataTracker<Vector3f> getPosition() {
        return this.position;
    }

    @Override
    @Generated
    public DataTracker<Quaternionf> getLeftRotation() {
        return this.leftRotation;
    }

    @Override
    @Generated
    public DataTracker<Vector3f> getScale() {
        return this.scale;
    }

    @Override
    @Generated
    public DataTracker<Quaternionf> getRightRotation() {
        return this.rightRotation;
    }

    @Override
    @Generated
    public DataTracker<Display.Billboard> getBillboard() {
        return this.billboard;
    }

    @Override
    @Generated
    public DataTracker<ItemDisplay.ItemDisplayTransform> getDisplay() {
        return this.display;
    }

    @Override
    @Generated
    public DataTracker<Boolean> getVisibility() {
        return this.visibility;
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

    @Override
    @Generated
    public DataTracker<Integer> getBrightness() {
        return this.brightness;
    }

    @Override
    @Generated
    public Map<Integer, DisplayBone.BoneData> getModel() {
        return this.model;
    }

    @Override
    @Generated
    public UpdateScheme<DisplayBone.BoneData> getModelUpdateScheme() {
        return this.modelUpdateScheme;
    }

    @Generated
    public DisplayBoneImpl() {
    }

    public static class BoneDataImpl
    implements DisplayBone.BoneData {
        private final int id;
        private final UUID uuid = UUID.randomUUID();
        private final DisplayBone bone;
        private final DataTracker<ItemStack> model = new DataTracker();

        public boolean equals(Object object) {
            if (object == null || this.getClass() != object.getClass()) {
                return false;
            }
            BoneDataImpl boneData = (BoneDataImpl)object;
            return Objects.equals(this.model, boneData.model);
        }

        public int hashCode() {
            return Objects.hashCode(this.model);
        }

        @Override
        public int getModelHash() {
            return this.model.get().hashCode();
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
        public DisplayBone getBone() {
            return this.bone;
        }

        @Override
        @Generated
        public DataTracker<ItemStack> getModel() {
            return this.model;
        }

        @Generated
        public BoneDataImpl(int id, DisplayBone bone) {
            this.id = id;
            this.bone = bone;
        }
    }
}

