/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 *  org.bukkit.entity.ItemDisplay$ItemDisplayTransform
 *  org.bukkit.inventory.ItemStack
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 *  org.joml.Vector3f
 */
package com.ticxo.modelengine.core.model.bone.behavior;

import com.ticxo.modelengine.api.model.bone.ModelBone;
import com.ticxo.modelengine.api.model.bone.behavior.AbstractBoneBehavior;
import com.ticxo.modelengine.api.model.bone.behavior.BoneBehaviorData;
import com.ticxo.modelengine.api.model.bone.behavior.BoneBehaviorType;
import com.ticxo.modelengine.api.model.bone.type.HeldItem;
import com.ticxo.modelengine.api.model.render.DisplayRenderer;
import com.ticxo.modelengine.api.utils.data.io.SavedData;
import java.util.function.Supplier;
import lombok.Generated;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.ItemStack;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;

public class HeldItemImpl
extends AbstractBoneBehavior<HeldItemImpl>
implements HeldItem {
    private final Vector3f location = new Vector3f();
    private final Quaternionf rotation = new Quaternionf();
    private ItemDisplay.ItemDisplayTransform display;
    private HeldItem.ItemStackSupplier itemProvider;

    public HeldItemImpl(ModelBone bone, BoneBehaviorType<HeldItemImpl> type, BoneBehaviorData data) {
        super(bone, type, data);
        this.display = (ItemDisplay.ItemDisplayTransform)data.get("display");
    }

    @Override
    public void onApply() {
        if (this.bone.getActiveModel().getModelRenderer() instanceof DisplayRenderer) {
            this.bone.setRenderer(true);
            this.bone.setModel(EMPTY);
        }
    }

    @Override
    public void onFinalize() {
        float yaw = -this.bone.getYaw() * ((float)Math.PI / 180);
        this.bone.getGlobalTransform().mutatePosition(pos -> pos.rotateY(yaw, this.location));
        this.bone.getGlobalTransform().mutateLeftQuaternion(q -> q.premul((Quaternionfc)this.rotation.rotationZYX(0.0f, yaw, 0.0f), this.rotation));
        if (this.itemProvider == null) {
            this.bone.setModel(EMPTY);
        } else {
            this.bone.setModel(this.itemProvider.supply());
        }
    }

    @Override
    public void save(SavedData data) {
        if (this.itemProvider != null) {
            this.itemProvider.save().ifPresent(data1 -> data.putData("supplier", (SavedData)data1));
        }
    }

    @Override
    public void load(SavedData data) {
        data.getData("supplier").ifPresent(data1 -> {
            switch (data1.getString("type", "")) {
                case "static": {
                    HeldItem.ItemStackSupplier itemStackSupplier = new HeldItem.StaticItemStackSupplier();
                    break;
                }
                case "equipment": {
                    HeldItem.ItemStackSupplier itemStackSupplier = new HeldItem.EquipmentSupplier();
                    break;
                }
                default: {
                    HeldItem.ItemStackSupplier itemStackSupplier = this.itemProvider = null;
                }
            }
            if (this.itemProvider != null) {
                this.itemProvider.load((SavedData)data1);
            }
        });
    }

    @Override
    public void clearItemProvider() {
        this.itemProvider = null;
    }

    @Override
    public void setItemProvider(Supplier<ItemStack> stackSupplier) {
        this.setItemProvider(new HeldItem.TemporaryItemStackSupplier(stackSupplier));
    }

    @Override
    public void setItemProvider(HeldItem.ItemStackSupplier stackSupplier) {
        this.itemProvider = stackSupplier;
    }

    @Override
    public ItemStack getItem() {
        return this.bone.getModel();
    }

    @Override
    @Generated
    public Vector3f getLocation() {
        return this.location;
    }

    @Override
    @Generated
    public Quaternionf getRotation() {
        return this.rotation;
    }

    @Override
    @Generated
    public ItemDisplay.ItemDisplayTransform getDisplay() {
        return this.display;
    }

    @Override
    @Generated
    public void setDisplay(ItemDisplay.ItemDisplayTransform display) {
        this.display = display;
    }

    @Override
    @Generated
    public HeldItem.ItemStackSupplier getItemProvider() {
        return this.itemProvider;
    }
}

