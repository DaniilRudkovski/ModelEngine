/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.EntityType
 *  org.bukkit.entity.Player
 */
package com.ticxo.modelengine.core.command.sub;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.command.AbstractCommand;
import com.ticxo.modelengine.api.entity.BukkitEntity;
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
import java.util.Locale;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class SummonCommand
extends AbstractCommand {
    public SummonCommand(AbstractCommand parent) {
        super(parent);
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        Class clazz;
        if (args.length < 1) {
            return false;
        }
        EntityType type = EntityType.PIG;
        if (args.length >= 2) {
            try {
                type = EntityType.valueOf((String)args[1].toUpperCase(Locale.ENGLISH));
            }
            catch (IllegalArgumentException illegalArgumentException) {
                // empty catch block
            }
        }
        if ((clazz = type.getEntityClass()) == null) {
            return false;
        }
        ModelBlueprint blueprint = ModelEngineAPI.getBlueprint(args[0]);
        if (blueprint == null) {
            return false;
        }
        ModelOptionParser options = ModelOptionParser.parse(2, args);
        Player player = (Player)sender;
        Location location = player.getLocation();
        player.getWorld().spawn(location, clazz, entity -> {
            BukkitEntity base = new BukkitEntity((Entity)entity);
            base.getBodyRotationController().setYBodyRot(location.getYaw());
            ModeledEntity modeledEntity = ModelEngineAPI.createModeledEntity(base);
            modeledEntity.setBaseEntityVisible(false);
            ActiveModel activeModel = ModelEngineAPI.createActiveModel(blueprint);
            activeModel.setAutoRendererInitialization(false);
            options.applyDisguiseOptions(activeModel);
            modeledEntity.addModel(activeModel, true).ifPresent(ActiveModel::destroy);
            activeModel.getBones().values().forEach(modelBone -> {
                modelBone.getBoneBehavior(BoneBehaviorTypes.PLAYER_LIMB).ifPresent(playerLimb -> ((PlayerLimb)((Object)playerLimb)).setTexture(player));
                modelBone.getBoneBehavior(BoneBehaviorTypes.USER_LIMB).ifPresent(userLimb -> ((UserLimb)((Object)userLimb)).setTexture(player));
            });
            activeModel.initializeRenderer();
        });
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
            case 2: {
                String arg = args[1];
                for (EntityType type : EntityType.values()) {
                    String name = type.name();
                    if (!name.startsWith(arg.toUpperCase(Locale.ENGLISH))) continue;
                    list.add(name);
                }
                break;
            }
            default: {
                list.addAll(ModelOptionParser.getTabCompletion(args.length > 1 ? 2 : 1, args));
            }
        }
        return list;
    }

    @Override
    public String getPermissionNode() {
        return "modelengine.command.summon";
    }

    @Override
    public boolean isConsoleFriendly() {
        return false;
    }

    @Override
    public String getName() {
        return "summon";
    }
}

