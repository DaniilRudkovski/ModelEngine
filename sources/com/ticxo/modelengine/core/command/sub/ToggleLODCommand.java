/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.command.CommandSender
 */
package com.ticxo.modelengine.core.command.sub;

import com.ticxo.modelengine.api.command.AbstractCommand;
import com.ticxo.modelengine.api.lod.AnimationLODHandler;
import com.ticxo.modelengine.api.utils.logger.LogColor;
import com.ticxo.modelengine.core.command.MECommand;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class ToggleLODCommand
extends AbstractCommand {
    public ToggleLODCommand(AbstractCommand parent) {
        super(parent);
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        if (args.length >= 1) {
            AnimationLODHandler.setGlobalEnabled(Boolean.parseBoolean(args[0]));
        } else {
            MECommand.logSender(sender, ChatColor.GREEN + "[ModelEngine] Animation LOD is " + (AnimationLODHandler.isGlobalEnabled() ? "enabled." : "disabled."), LogColor.BRIGHT_GREEN + "Animation LOD is " + (AnimationLODHandler.isGlobalEnabled() ? "enabled." : "disabled."));
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length != 1) {
            return null;
        }
        ArrayList<String> list = new ArrayList<String>();
        list.add("true");
        list.add("false");
        return list;
    }

    @Override
    public String getPermissionNode() {
        return "modelengine.command.toggle-lod";
    }

    @Override
    public boolean isConsoleFriendly() {
        return true;
    }

    @Override
    public String getName() {
        return "toggle-lod";
    }
}

