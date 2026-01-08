/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Player
 */
package com.ticxo.modelengine.core.command.sub;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.command.AbstractCommand;
import com.ticxo.modelengine.api.entity.data.BukkitEntityData;
import com.ticxo.modelengine.api.entity.data.IEntityData;
import com.ticxo.modelengine.api.generator.blueprint.ModelBlueprint;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.ModeledEntity;
import com.ticxo.modelengine.api.model.bone.BoneBehaviorTypes;
import com.ticxo.modelengine.api.model.bone.type.PlayerLimb;
import com.ticxo.modelengine.api.model.bone.type.UserLimb;
import com.ticxo.modelengine.core.command.MECommand;
import com.ticxo.modelengine.core.command.ModelOptionParser;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class DisguiseCommand
extends AbstractCommand {
    public DisguiseCommand(AbstractCommand parent) {
        super(parent);
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        Player player = (Player)sender;
        if (args.length < 1) {
            return false;
        }
        ModelBlueprint blueprint = ModelEngineAPI.getBlueprint(args[0]);
        if (blueprint == null) {
            return false;
        }
        ModelOptionParser options = ModelOptionParser.parse(1, args);
        ModeledEntity modeledEntity = ModelEngineAPI.getOrCreateModeledEntity((Entity)player);
        modeledEntity.getBase().getBodyRotationController().setPlayerMode(true);
        modeledEntity.setBaseEntityVisible(options.hideSelf == false);
        IEntityData iEntityData = modeledEntity.getBase().getData();
        if (iEntityData instanceof BukkitEntityData) {
            BukkitEntityData data = (BukkitEntityData)iEntityData;
            if (args.length < 2 || !options.hideSelfDisguise.booleanValue()) {
                data.getTracked().addForcedPairing(player.getUniqueId());
            }
        }
        if (options.hideSelf.booleanValue()) {
            ModelEngineAPI.getEntityHandler().setForcedInvisible((Entity)player, true);
        }
        if (modeledEntity.getModel(args[0]).isEmpty()) {
            ActiveModel activeModel = ModelEngineAPI.createActiveModel(blueprint);
            options.applyDisguiseOptions(activeModel);
            modeledEntity.addModel(activeModel, false).ifPresent(ActiveModel::destroy);
            if (options.pivotOverride.booleanValue()) {
                activeModel.setPivotOverride(ModelEngineAPI.getPivotOverrideRegistry().getOrCreate((Entity)player));
            }
            activeModel.getBones().values().forEach(modelBone -> {
                modelBone.getBoneBehavior(BoneBehaviorTypes.PLAYER_LIMB).ifPresent(playerLimb -> ((PlayerLimb)((Object)playerLimb)).setTexture(player));
                modelBone.getBoneBehavior(BoneBehaviorTypes.USER_LIMB).ifPresent(playerLimb -> ((UserLimb)((Object)playerLimb)).setTexture(player));
            });
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        ArrayList<String> list = new ArrayList<String>();
        switch (args.length) {
            case 1: {
                MECommand.getModelIdTabComplete(list, args[0]);
                break;
            }
            default: {
                list.addAll(ModelOptionParser.getTabCompletion(1, args));
            }
        }
        return list;
    }

    @Override
    public String getPermissionNode() {
        return "modelengine.command.disguise";
    }

    @Override
    public boolean isConsoleFriendly() {
        return false;
    }

    @Override
    public String getName() {
        return "disguise";
    }
}

