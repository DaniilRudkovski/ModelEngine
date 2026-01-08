/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Entity
 */
package com.ticxo.modelengine.core.command.sub;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.command.AbstractCommand;
import com.ticxo.modelengine.api.generator.ModelGenerator;
import com.ticxo.modelengine.api.generator.skin.SkinSplitter;
import com.ticxo.modelengine.api.utils.config.ConfigManager;
import com.ticxo.modelengine.api.utils.logger.LogColor;
import com.ticxo.modelengine.api.utils.logger.TLogger;
import com.ticxo.modelengine.core.command.MECommand;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;

public class ReloadCommand
extends AbstractCommand {
    public ReloadCommand(AbstractCommand parent) {
        super(parent);
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        if (args.length == 0) {
            this.reloadConfig(sender);
            this.reloadModels(sender);
            this.reloadMappings(sender);
            return true;
        }
        switch (args[0]) {
            case "models": {
                this.reloadModels(sender);
                break;
            }
            case "config": {
                this.reloadConfig(sender);
                break;
            }
            case "mappings": {
                this.reloadMappings(sender);
                break;
            }
            default: {
                return false;
            }
        }
        return true;
    }

    private void reloadConfig(CommandSender sender) {
        ConfigManager config = ModelEngineAPI.getAPI().getConfigManager();
        config.reload();
        config.updateReferences();
        MECommand.logSender(sender, ChatColor.GREEN + "[ModelEngine] Config reloaded.", LogColor.BRIGHT_GREEN + "Config reloaded.");
    }

    private void reloadModels(CommandSender sender) {
        ModelGenerator generator = ModelEngineAPI.getAPI().getModelGenerator();
        generator.importModels(false);
        generator.queueTask(ModelGenerator.Phase.POST_IMPORT, () -> {
            String msg = ModelEngineAPI.getAPI().getModelRegistry().getKeys().size() + " models loaded.";
            if (sender instanceof Entity) {
                sender.sendMessage(ChatColor.GREEN + "[ModelEngine] " + msg);
            } else {
                TLogger.log();
                TLogger.log(LogColor.BRIGHT_GREEN + msg);
            }
        });
    }

    private void reloadMappings(CommandSender sender) {
        SkinSplitter generator = ModelEngineAPI.getAPI().getSkinSplitter();
        generator.reloadMapping();
        MECommand.logSender(sender, ChatColor.GREEN + "[ModelEngine] Skin mappings reloaded.", LogColor.BRIGHT_GREEN + "Skin mappings reloaded.");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return List.of("models", "config", "mappings");
    }

    @Override
    public String getPermissionNode() {
        return "modelengine.command.reload";
    }

    @Override
    public boolean isConsoleFriendly() {
        return true;
    }

    @Override
    public String getName() {
        return "reload";
    }
}

