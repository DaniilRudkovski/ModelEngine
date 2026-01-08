/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.lumine.mythic.api.adapters.AbstractEntity
 *  io.lumine.mythic.api.config.MythicLineConfig
 *  io.lumine.mythic.api.skills.ITargetedEntitySkill
 *  io.lumine.mythic.api.skills.SkillMetadata
 *  io.lumine.mythic.api.skills.SkillResult
 *  io.lumine.mythic.api.skills.placeholders.PlaceholderString
 *  io.lumine.mythic.core.drops.EquipSlot
 *  io.lumine.mythic.core.skills.placeholders.PlaceholderMeta
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.LivingEntity
 *  org.bukkit.inventory.EquipmentSlot
 */
package com.ticxo.modelengine.core21.mythic.mechanics.item;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.model.ModeledEntity;
import com.ticxo.modelengine.api.model.bone.BoneBehaviorTypes;
import com.ticxo.modelengine.api.model.bone.type.HeldItem;
import com.ticxo.modelengine.core21.mythic.MythicUtils;
import com.ticxo.modelengine.core21.mythic.utils.MythicMechanic;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.core.drops.EquipSlot;
import io.lumine.mythic.core.skills.placeholders.PlaceholderMeta;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EquipmentSlot;

@MythicMechanic(name="linkitembone", aliases={})
public class LinkItemBoneMechanic
implements ITargetedEntitySkill {
    private final PlaceholderString modelId;
    private final PlaceholderString partId;
    private final PlaceholderString slot;

    public LinkItemBoneMechanic(MythicLineConfig mlc) {
        this.modelId = mlc.getPlaceholderString(new String[]{"m", "mid", "model", "modelid"}, null, new String[0]);
        this.partId = mlc.getPlaceholderString(new String[]{"b", "bone", "p", "pid", "part", "partid"}, null, new String[0]);
        this.slot = mlc.getPlaceholderString(new String[]{"s", "slot"}, null, new String[0]);
    }

    public SkillResult castAtEntity(SkillMetadata meta, AbstractEntity target) {
        ModeledEntity model = ModelEngineAPI.getModeledEntity(target.getUniqueId());
        if (model == null) {
            return SkillResult.CONDITION_FAILED;
        }
        String modelId = MythicUtils.getOrNullLowercase(this.modelId, (PlaceholderMeta)meta, target);
        model.getModel(modelId).ifPresent(activeModel -> {
            String partId = MythicUtils.getOrNullLowercase(this.partId, (PlaceholderMeta)meta, target);
            activeModel.getBone(partId).ifPresent(modelBone -> modelBone.getBoneBehavior(BoneBehaviorTypes.ITEM).ifPresent(heldItem -> {
                String slotString = MythicUtils.getOrNull(this.slot, (PlaceholderMeta)meta, target);
                if (slotString == null) {
                    ((HeldItem)((Object)heldItem)).clearItemProvider();
                    return;
                }
                EquipmentSlot bukkitSlot = EquipSlot.of((String)slotString).getBukkitSlot();
                if (bukkitSlot == null) {
                    ((HeldItem)((Object)heldItem)).clearItemProvider();
                    return;
                }
                Entity patt0$temp = target.getBukkitEntity();
                if (patt0$temp instanceof LivingEntity) {
                    LivingEntity livingEntity = (LivingEntity)patt0$temp;
                    if (livingEntity.getEquipment() == null) {
                        return;
                    }
                    ((HeldItem)((Object)heldItem)).setItemProvider(new HeldItem.EquipmentSupplier(livingEntity, bukkitSlot));
                }
            }));
        });
        return SkillResult.SUCCESS;
    }
}

