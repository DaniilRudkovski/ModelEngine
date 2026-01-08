/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Entity
 */
package com.ticxo.modelengine.core.command;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.animation.property.IAnimationProperty;
import com.ticxo.modelengine.api.command.AbstractCommand;
import com.ticxo.modelengine.api.generator.blueprint.ModelBlueprint;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.ModeledEntity;
import com.ticxo.modelengine.api.utils.logger.TLogger;
import com.ticxo.modelengine.core.command.sub.DebugCommand;
import com.ticxo.modelengine.core.command.sub.DisguiseCommand;
import com.ticxo.modelengine.core.command.sub.MenuCommand;
import com.ticxo.modelengine.core.command.sub.PluginHealthCommand;
import com.ticxo.modelengine.core.command.sub.ReloadCommand;
import com.ticxo.modelengine.core.command.sub.ResyncCommand;
import com.ticxo.modelengine.core.command.sub.SummonCommand;
import com.ticxo.modelengine.core.command.sub.ToggleLODCommand;
import com.ticxo.modelengine.core.command.sub.UndisguiseCommand;
import java.util.List;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;

public class MECommand
extends AbstractCommand {
    public MECommand(ModelEngineAPI plugin) {
        super(plugin);
        this.addSubCommands(new ReloadCommand(this));
        this.addSubCommands(new SummonCommand(this));
        this.addSubCommands(new DisguiseCommand(this));
        this.addSubCommands(new UndisguiseCommand(this));
        this.addSubCommands(new DebugCommand(this));
        this.addSubCommands(new MenuCommand(this));
        this.addSubCommands(new PluginHealthCommand(this));
        this.addSubCommands(new ResyncCommand(this));
        this.addSubCommands(new ToggleLODCommand(this));
    }

    public static void getModelIdTabComplete(List<String> list, String arg) {
        for (String id : ModelEngineAPI.getAPI().getModelRegistry().getKeys()) {
            if (!id.startsWith(arg)) continue;
            list.add(id);
        }
    }

    public static void getModelIdTabComplete(List<String> list, String arg, ModeledEntity modeledEntity) {
        for (String id : modeledEntity.getModels().keySet()) {
            if (!id.startsWith(arg)) continue;
            list.add(id);
        }
    }

    public static void getStateTabComplete(List<String> list, String arg, ActiveModel model) {
        for (IAnimationProperty animation : model.getAnimationHandler().getAnimations().values()) {
            String name = animation.getName();
            if (!name.startsWith(arg)) continue;
            list.add(name);
        }
    }

    public static void getStateTabComplete(List<String> list, String arg, ModelBlueprint blueprint) {
        for (String animation : blueprint.getAnimations().keySet()) {
            if (!animation.startsWith(arg)) continue;
            list.add(animation);
        }
    }

    public static void logSender(CommandSender sender, String msg) {
        MECommand.logSender(sender, msg, msg);
    }

    public static void logSender(CommandSender sender, String entityMsg, String consoleMsg) {
        if (sender instanceof Entity) {
            sender.sendMessage(entityMsg);
        } else {
            TLogger.log(consoleMsg);
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return null;
    }

    @Override
    public String getPermissionNode() {
        return "modelengine.command";
    }

    @Override
    public boolean isConsoleFriendly() {
        return true;
    }

    @Override
    public String getName() {
        return null;
    }
}

