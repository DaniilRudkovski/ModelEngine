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
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.ModeledEntity;
import com.ticxo.modelengine.core.command.MECommand;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class UndisguiseCommand
extends AbstractCommand {
    public UndisguiseCommand(AbstractCommand parent) {
        super(parent);
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        Player player = (Player)sender;
        ModeledEntity modeledEntity = ModelEngineAPI.getModeledEntity(player.getUniqueId());
        if (modeledEntity == null) {
            return true;
        }
        if (args.length == 0) {
            modeledEntity.markRemoved();
            ModelEngineAPI.getEntityHandler().setForcedInvisible((Entity)player, false);
            ModelEngineAPI.getEntityHandler().forceSpawn((Entity)player);
        } else {
            for (String modelId : args) {
                modeledEntity.removeModel(modelId).ifPresent(ActiveModel::destroy);
            }
            if (modeledEntity.getModels().isEmpty()) {
                modeledEntity.markRemoved();
                ModelEngineAPI.getEntityHandler().setForcedInvisible((Entity)player, false);
                ModelEngineAPI.getEntityHandler().forceSpawn((Entity)player);
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        ArrayList<String> list = new ArrayList<String>();
        Player player = (Player)sender;
        ModeledEntity modeledEntity = ModelEngineAPI.getModeledEntity(player.getUniqueId());
        if (modeledEntity == null) {
            return list;
        }
        if (args.length > 0) {
            String arg = args[args.length - 1];
            MECommand.getModelIdTabComplete(list, arg, modeledEntity);
        }
        return list;
    }

    @Override
    public String getPermissionNode() {
        return "modelengine.command.undisguise";
    }

    @Override
    public boolean isConsoleFriendly() {
        return false;
    }

    @Override
    public String getName() {
        return "undisguise";
    }
}

