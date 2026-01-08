/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 *  net.kyori.adventure.text.Component
 *  net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
 *  org.apache.commons.lang3.NotImplementedException
 *  org.bukkit.entity.Entity
 */
package com.ticxo.modelengine.api.model.operation;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.ServerInfo;
import com.ticxo.modelengine.api.entity.BaseEntity;
import com.ticxo.modelengine.api.entity.data.BukkitEntityData;
import com.ticxo.modelengine.api.entity.data.IEntityData;
import com.ticxo.modelengine.api.generator.blueprint.ModelBlueprint;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.ModeledEntity;
import com.ticxo.modelengine.api.model.bone.BoneBehaviorTypes;
import com.ticxo.modelengine.api.model.bone.ModelBone;
import com.ticxo.modelengine.api.model.bone.manager.MountData;
import com.ticxo.modelengine.api.model.bone.manager.MountManager;
import com.ticxo.modelengine.api.model.bone.type.NameTag;
import com.ticxo.modelengine.api.model.operation.ModelOperation;
import com.ticxo.modelengine.api.model.operation.OperationResult;
import java.util.Objects;
import java.util.function.Function;
import lombok.Generated;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.apache.commons.lang3.NotImplementedException;
import org.bukkit.entity.Entity;

public enum OperationType {
    ADD_MODEL(operation -> {
        IEntityData patt1921$temp;
        Entity entity;
        double hitboxScale;
        ModelBlueprint blueprint = ModelEngineAPI.getBlueprint(operation.modelId);
        if (blueprint == null) {
            return new OperationResult(operation.getBaseEntity(), null, null, new RuntimeException("Unknown blueprint: " + operation.modelId));
        }
        boolean disguise = operation.isDisguise();
        ModeledEntity model = operation.getOrCreateModeledEntity();
        if (model.isInitialized() && model.getModel(operation.modelId).isPresent()) {
            return new OperationResult(operation.getBaseEntity(), null, null, new RuntimeException("Base entity already has model: " + operation.modelId));
        }
        model.restore();
        BaseEntity<?> base = model.getBase();
        double d = hitboxScale = operation.hitboxScale == null ? operation.scale : operation.hitboxScale;
        if (model.getModel(operation.modelId).isPresent()) {
            return new OperationResult(base, model, null, new RuntimeException("Base entity already has model: " + operation.modelId));
        }
        Object obj = base.getOriginal();
        Entity bukkitEntity = obj instanceof Entity ? (entity = (Entity)obj) : null;
        model.setSaved(operation.shouldSave);
        if (operation.baseInvisible && bukkitEntity != null) {
            ModelEngineAPI.getEntityHandler().setForcedInvisible(bukkitEntity, true);
        }
        if (disguise && (patt1921$temp = model.getBase().getData()) instanceof BukkitEntityData) {
            BukkitEntityData data = (BukkitEntityData)patt1921$temp;
            Objects.requireNonNull(operation);
            data.getTracked().addForcedPairing(base.getUUID());
        }
        if (operation.stepHeight != null) {
            model.getBase().setMaxStepHeight(operation.stepHeight);
        }
        if (operation.viewRadius != 0) {
            model.getBase().setRenderRadius(Math.max(operation.viewRadius, 0));
        }
        if (operation.syncBodyYaw && bukkitEntity != null) {
            model.getBase().getBodyRotationController().setYBodyRot(bukkitEntity.getLocation().getYaw());
        }
        ActiveModel activeModel = ModelEngineAPI.createActiveModel(operation.modelId, null, am -> operation.useStateMachine ? ModelEngineAPI.createStateMachineHandler(am) : ModelEngineAPI.createPriorityHandler(am));
        activeModel.setScale(operation.scale);
        activeModel.setHitboxScale(hitboxScale);
        activeModel.setCanHurt(operation.doDamageTint);
        activeModel.setLockPitch(operation.lockPitch);
        activeModel.setLockYaw(operation.lockYaw);
        activeModel.setInvisUpdate(operation.invisibleUpdate);
        activeModel.setAutoRendererInitialization(operation.initializeRenderer);
        activeModel.setHitboxVisible(operation.showHitbox);
        activeModel.setShadowVisible(operation.showShadow);
        activeModel.setRenderFire(operation.renderFire);
        activeModel.setOnFire(operation.forceFire);
        if (operation.useBaseAsPivot && bukkitEntity != null) {
            activeModel.setPivotOverride(ModelEngineAPI.getPivotOverrideRegistry().getOrCreate(bukkitEntity));
        } else if (operation.pivotOverride != null) {
            activeModel.setPivotOverride(operation.pivotOverride);
        }
        model.setBaseEntityVisible(!operation.baseInvisible);
        model.addModel(activeModel, operation.overrideHitbox).ifPresent(ActiveModel::destroy);
        activeModel.getMountManager().ifPresent(mountManager -> {
            ((MountManager)((Object)mountManager)).setCanRide(operation.rideable);
            ((MountManager)((Object)mountManager)).setCanDrive(operation.drivable);
            ((MountData)model.getMountData()).setMainMountManager(mountManager);
        });
        if (operation.nametagBone != null && bukkitEntity != null) {
            activeModel.getBone(operation.nametagBone).flatMap(modelBone -> modelBone.getBoneBehavior(BoneBehaviorTypes.NAMETAG)).ifPresent(nameTag -> {
                ((NameTag)((Object)nameTag)).setComponentSupplier(() -> {
                    if (ServerInfo.IS_PAPER) {
                        Component name = bukkitEntity.customName();
                        return name == null ? Component.empty() : name;
                    }
                    String name = bukkitEntity.getCustomName();
                    return name == null ? null : LegacyComponentSerializer.legacyAmpersand().deserialize(name);
                });
                ((NameTag)((Object)nameTag)).setVisible(true);
            });
        }
        if (operation.rootBone != null) {
            activeModel.getBone(operation.rootBone).ifPresent(bone -> model.getRootMotionHandler().setRootBone((ModelBone)bone));
        }
        return new OperationResult(base, model, activeModel, null);
    }),
    REMOVE_MODEL(operation -> new OperationResult(null, null, null, (Throwable)new NotImplementedException("Remove functionality is still work in progress.")));

    private final Function<ModelOperation, OperationResult> processor;

    public OperationResult process(ModelOperation operation) {
        return this.processor.apply(operation);
    }

    @Generated
    private OperationType(Function<ModelOperation, OperationResult> processor) {
        this.processor = processor;
    }
}

