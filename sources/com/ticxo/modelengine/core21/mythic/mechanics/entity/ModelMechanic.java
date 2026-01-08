/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.lumine.mythic.api.adapters.AbstractEntity
 *  io.lumine.mythic.api.adapters.AbstractPlayer
 *  io.lumine.mythic.api.config.MythicLineConfig
 *  io.lumine.mythic.api.skills.ITargetedEntitySkill
 *  io.lumine.mythic.api.skills.SkillMetadata
 *  io.lumine.mythic.api.skills.SkillResult
 *  io.lumine.mythic.api.skills.placeholders.PlaceholderBoolean
 *  io.lumine.mythic.api.skills.placeholders.PlaceholderDouble
 *  io.lumine.mythic.api.skills.placeholders.PlaceholderInt
 *  io.lumine.mythic.api.skills.placeholders.PlaceholderString
 *  io.lumine.mythic.bukkit.BukkitAdapter
 *  io.lumine.mythic.core.skills.placeholders.PlaceholderMeta
 *  net.kyori.adventure.text.Component
 *  net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Player
 */
package com.ticxo.modelengine.core21.mythic.mechanics.entity;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.ServerInfo;
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
import com.ticxo.modelengine.api.model.operation.OperationType;
import com.ticxo.modelengine.core.animation.handler.PriorityHandler;
import com.ticxo.modelengine.core.animation.handler.StateMachineHandler;
import com.ticxo.modelengine.core21.mythic.MythicUtils;
import com.ticxo.modelengine.core21.mythic.utils.MythicMechanic;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractPlayer;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.placeholders.PlaceholderBoolean;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.api.skills.placeholders.PlaceholderInt;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.skills.placeholders.PlaceholderMeta;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

@MythicMechanic(name="model", aliases={})
public class ModelMechanic
implements ITargetedEntitySkill {
    private final PlaceholderString modelId;
    private final PlaceholderString nametag;
    private final PlaceholderBoolean hitbox;
    private final PlaceholderBoolean remove;
    private final PlaceholderBoolean killOwner;
    private final PlaceholderBoolean invisible;
    private final PlaceholderBoolean doDamageTint;
    private final PlaceholderBoolean canDrive;
    private final PlaceholderBoolean canRide;
    private final PlaceholderBoolean lockPitch;
    private final PlaceholderBoolean lockYaw;
    private final PlaceholderBoolean initRenderer;
    private final PlaceholderBoolean showHitbox;
    private final PlaceholderBoolean showShadow;
    private final PlaceholderDouble stepHeight;
    private final PlaceholderDouble scale;
    private final PlaceholderDouble hitboxScale;
    private final PlaceholderInt viewRadius;
    private final PlaceholderBoolean useStateMachine;
    private final PlaceholderBoolean invisUpdate;
    private final PlaceholderBoolean syncBodyYaw;
    private final PlaceholderBoolean shouldSave;
    private final PlaceholderBoolean viewSelf;
    private final PlaceholderBoolean pivotOverride;
    private final PlaceholderBoolean renderFire;
    private final PlaceholderBoolean forceFire;
    private final PlaceholderString rootBone;

    public ModelMechanic(MythicLineConfig mlc) {
        this.modelId = mlc.getPlaceholderString(new String[]{"m", "mid", "model", "modelid"}, null, new String[0]);
        this.hitbox = mlc.getPlaceholderBoolean(new String[]{"h", "hitbox"}, Boolean.valueOf(true));
        this.remove = mlc.getPlaceholderBoolean(new String[]{"r", "remove"}, Boolean.valueOf(false));
        this.killOwner = mlc.getPlaceholderBoolean(new String[]{"ko", "killowner"}, Boolean.valueOf(false));
        this.invisible = mlc.getPlaceholderBoolean(new String[]{"i", "invis", "invisible"}, Boolean.valueOf(true));
        this.doDamageTint = mlc.getPlaceholderBoolean(new String[]{"d", "tint", "damagetint"}, Boolean.valueOf(true));
        this.nametag = mlc.getPlaceholderString(new String[]{"n", "name", "nametag"}, null, new String[0]);
        this.canDrive = mlc.getPlaceholderBoolean(new String[]{"drive"}, Boolean.valueOf(false));
        this.canRide = mlc.getPlaceholderBoolean(new String[]{"ride"}, Boolean.valueOf(false));
        this.lockPitch = mlc.getPlaceholderBoolean(new String[]{"lp", "lpitch", "lockpitch"}, Boolean.valueOf(false));
        this.lockYaw = mlc.getPlaceholderBoolean(new String[]{"ly", "lyaw", "lockyaw"}, Boolean.valueOf(false));
        this.stepHeight = mlc.getPlaceholderDouble(new String[]{"s", "step"}, (String)null, new String[0]);
        this.viewRadius = mlc.getPlaceholderInteger(new String[]{"rad", "radius"}, 0, new String[0]);
        this.scale = mlc.getPlaceholderDouble(new String[]{"scale"}, 1.0, new String[0]);
        this.hitboxScale = mlc.getPlaceholderDouble(new String[]{"hitboxscale"}, this.scale, new String[0]);
        this.useStateMachine = mlc.getPlaceholderBoolean(new String[]{"usm", "state", "statemachine", "usestatemachine"}, Boolean.valueOf(false));
        this.invisUpdate = mlc.getPlaceholderBoolean(new String[]{"iu", "invisupdate"}, Boolean.valueOf(false));
        this.initRenderer = mlc.getPlaceholderBoolean(new String[]{"init", "initrender"}, Boolean.valueOf(true));
        this.showHitbox = mlc.getPlaceholderBoolean(new String[]{"showhitbox"}, Boolean.valueOf(true));
        this.showShadow = mlc.getPlaceholderBoolean(new String[]{"showshadow"}, Boolean.valueOf(true));
        this.syncBodyYaw = mlc.getPlaceholderBoolean(new String[]{"syncbody"}, Boolean.valueOf(true));
        this.shouldSave = mlc.getPlaceholderBoolean(new String[]{"save"}, Boolean.valueOf(false));
        this.viewSelf = mlc.getPlaceholderBoolean(new String[]{"vs", "see", "seeself"}, Boolean.valueOf(true));
        this.pivotOverride = mlc.getPlaceholderBoolean(new String[]{"pv", "pivot"}, Boolean.valueOf(false));
        this.renderFire = mlc.getPlaceholderBoolean(new String[]{"rf", "renderfire"}, null);
        this.forceFire = mlc.getPlaceholderBoolean(new String[]{"ff", "forcefire"}, null);
        this.rootBone = mlc.getPlaceholderString(new String[]{"rt", "root", "rootbone"}, null, new String[0]);
    }

    public SkillResult castAtEntity(SkillMetadata meta, AbstractEntity target) {
        if (this.remove.get((PlaceholderMeta)meta, target)) {
            return this.removeModel(meta, target);
        }
        return this.addModel(meta, target);
    }

    private SkillResult removeModel(SkillMetadata meta, AbstractEntity target) {
        ModeledEntity model = ModelEngineAPI.getModeledEntity(target.getUniqueId());
        if (model == null) {
            return SkillResult.CONDITION_FAILED;
        }
        boolean disguise = target.isPlayer();
        String id = MythicUtils.getOrNullLowercase(this.modelId, (PlaceholderMeta)meta, target);
        if (id == null) {
            for (ActiveModel activeModel : model.getModels().values()) {
                model.removeModel(activeModel.getBlueprint().getName()).ifPresent(ActiveModel::destroy);
            }
        } else {
            model.removeModel(id).ifPresent(ActiveModel::destroy);
        }
        if (this.killOwner.get((PlaceholderMeta)meta, target)) {
            target.remove();
            return SkillResult.SUCCESS;
        }
        if (model.getModels().isEmpty()) {
            model.setBaseEntityVisible(true);
            ModelEngineAPI.removeModeledEntity(target.getUniqueId());
            ModelEngineAPI.getEntityHandler().setForcedInvisible(BukkitAdapter.adapt((AbstractEntity)target), false);
            if (disguise) {
                Player player = BukkitAdapter.adapt((AbstractPlayer)target.asPlayer());
                ModelEngineAPI.getEntityHandler().forceSpawn((Entity)player);
            }
        } else {
            model.setBaseEntityVisible(!this.invisible.get((PlaceholderMeta)meta, target));
        }
        return SkillResult.SUCCESS;
    }

    private SkillResult addModel(SkillMetadata meta, AbstractEntity target) {
        OperationResult result = ModelOperation.builder().bukkitEntity(BukkitAdapter.adapt((AbstractEntity)target)).modelId(MythicUtils.getOrNullLowercase(this.modelId, (PlaceholderMeta)meta, target)).overrideHitbox(this.hitbox.get((PlaceholderMeta)meta, target)).stepHeight(this.stepHeight == null ? null : Double.valueOf(this.stepHeight.get((PlaceholderMeta)meta, target))).viewRadius(this.viewRadius.get((PlaceholderMeta)meta, target)).scale(this.scale.get((PlaceholderMeta)meta, target)).hitboxScale(this.hitboxScale.get((PlaceholderMeta)meta, target)).shouldSave(this.shouldSave.get((PlaceholderMeta)meta, target)).baseInvisible(this.invisible.get((PlaceholderMeta)meta, target)).doDamageTint(this.doDamageTint.get((PlaceholderMeta)meta, target)).lockPitch(this.lockPitch.get((PlaceholderMeta)meta, target)).lockYaw(this.lockYaw.get((PlaceholderMeta)meta, target)).invisibleUpdate(this.invisUpdate.get((PlaceholderMeta)meta, target)).initializeRenderer(this.initRenderer.get((PlaceholderMeta)meta, target)).syncBodyYaw(this.syncBodyYaw.get((PlaceholderMeta)meta, target)).useStateMachine(this.useStateMachine.get((PlaceholderMeta)meta, target)).useBaseAsPivot(this.pivotOverride.get((PlaceholderMeta)meta, target)).showHitbox(this.showHitbox.get((PlaceholderMeta)meta, target)).showShadow(this.showShadow.get((PlaceholderMeta)meta, target)).drivable(this.canDrive.get((PlaceholderMeta)meta, target)).rideable(this.canRide.get((PlaceholderMeta)meta, target)).nametagBone(MythicUtils.getOrNullLowercase(this.nametag, (PlaceholderMeta)meta, target)).rootBone(MythicUtils.getOrNullLowercase(this.rootBone, (PlaceholderMeta)meta, target)).renderFire(this.renderFire == null ? null : Boolean.valueOf(this.renderFire.get((PlaceholderMeta)meta, target))).forceFire(this.forceFire == null ? null : Boolean.valueOf(this.forceFire.get((PlaceholderMeta)meta, target))).operate(OperationType.ADD_MODEL);
        if (result.isSuccessful()) {
            return SkillResult.SUCCESS;
        }
        return SkillResult.ERROR;
    }

    private SkillResult addModel0(SkillMetadata meta, AbstractEntity target) {
        String modelId = MythicUtils.getOrNullLowercase(this.modelId, (PlaceholderMeta)meta, target);
        ModelBlueprint blueprint = MythicUtils.getBlueprintOrNull(modelId);
        if (blueprint == null) {
            return SkillResult.INVALID_CONFIG;
        }
        boolean disguise = target.isPlayer();
        Entity bukkitTarget = target.getBukkitEntity();
        ModeledEntity model = ModelEngineAPI.getOrCreateModeledEntity(bukkitTarget);
        if (model.isInitialized() && model.getModel(modelId).isPresent()) {
            return SkillResult.CONDITION_FAILED;
        }
        model.restore();
        Double stepHeight = this.stepHeight == null ? null : Double.valueOf(this.stepHeight.get((PlaceholderMeta)meta, target));
        int viewRadius = this.viewRadius.get((PlaceholderMeta)meta, target);
        double scale = this.scale.get((PlaceholderMeta)meta, target);
        double hitboxScale = this.hitboxScale.get((PlaceholderMeta)meta, target);
        model.queuePostInitTask(() -> {
            IEntityData patt0$temp;
            model.setSaved(this.shouldSave.get((PlaceholderMeta)meta, target));
            model.setBaseEntityVisible(!this.invisible.get((PlaceholderMeta)meta, target));
            if (this.invisible.get((PlaceholderMeta)meta, target)) {
                ModelEngineAPI.getEntityHandler().setForcedInvisible(BukkitAdapter.adapt((AbstractEntity)target), true);
            }
            if (disguise && (patt0$temp = model.getBase().getData()) instanceof BukkitEntityData) {
                BukkitEntityData data = (BukkitEntityData)patt0$temp;
                if (this.viewSelf.get((PlaceholderMeta)meta, target)) {
                    data.getTracked().addForcedPairing(target.getUniqueId());
                }
            }
            if (stepHeight != null) {
                model.getBase().setMaxStepHeight(stepHeight);
            }
            if (viewRadius != 0) {
                model.getBase().setRenderRadius(Math.max(viewRadius, 0));
            }
            if (this.syncBodyYaw.get((PlaceholderMeta)meta, target)) {
                model.getBase().getBodyRotationController().setYBodyRot(bukkitTarget.getLocation().getYaw());
            }
            if (model.getModel(modelId).isEmpty()) {
                String rootBone;
                ActiveModel activeModel = ModelEngineAPI.createActiveModel(modelId, null, am -> this.useStateMachine.get((PlaceholderMeta)meta, target) ? new StateMachineHandler((ActiveModel)am) : new PriorityHandler((ActiveModel)am));
                activeModel.setScale(scale);
                activeModel.setHitboxScale(hitboxScale);
                activeModel.setCanHurt(this.doDamageTint.get((PlaceholderMeta)meta, target));
                activeModel.setLockPitch(this.lockPitch.get((PlaceholderMeta)meta, target));
                activeModel.setLockYaw(this.lockYaw.get((PlaceholderMeta)meta, target));
                activeModel.setInvisUpdate(this.invisUpdate.get((PlaceholderMeta)meta, target));
                activeModel.setAutoRendererInitialization(this.initRenderer.get((PlaceholderMeta)meta, target));
                activeModel.setHitboxVisible(this.showHitbox.get((PlaceholderMeta)meta, target));
                activeModel.setShadowVisible(this.showShadow.get((PlaceholderMeta)meta, target));
                model.addModel(activeModel, this.hitbox.get((PlaceholderMeta)meta, target)).ifPresent(ActiveModel::destroy);
                if (this.pivotOverride.get((PlaceholderMeta)meta, target)) {
                    activeModel.setPivotOverride(ModelEngineAPI.getPivotOverrideRegistry().getOrCreate(bukkitTarget));
                }
                activeModel.getMountManager().ifPresent(mountManager -> {
                    ((MountManager)((Object)mountManager)).setCanRide(this.canRide.get((PlaceholderMeta)meta, target));
                    ((MountManager)((Object)mountManager)).setCanDrive(this.canDrive.get((PlaceholderMeta)meta, target));
                    ((MountData)model.getMountData()).setMainMountManager(mountManager);
                });
                String nametag = MythicUtils.getOrNullLowercase(this.nametag, (PlaceholderMeta)meta, target);
                if (nametag != null) {
                    activeModel.getBone(nametag).flatMap(modelBone -> modelBone.getBoneBehavior(BoneBehaviorTypes.NAMETAG)).ifPresent(nameTag -> {
                        ((NameTag)((Object)nameTag)).setComponentSupplier(() -> {
                            if (ServerInfo.IS_PAPER) {
                                Component name = bukkitTarget.customName();
                                return name == null ? Component.empty() : name;
                            }
                            String name = bukkitTarget.getCustomName();
                            return name == null ? null : LegacyComponentSerializer.legacyAmpersand().deserialize(name);
                        });
                        ((NameTag)((Object)nameTag)).setVisible(true);
                    });
                }
                if ((rootBone = MythicUtils.getOrNullLowercase(this.rootBone, (PlaceholderMeta)meta, target)) != null) {
                    activeModel.getBone(rootBone).ifPresent(bone -> model.getRootMotionHandler().setRootBone((ModelBone)bone));
                }
            }
        });
        return SkillResult.SUCCESS;
    }
}

