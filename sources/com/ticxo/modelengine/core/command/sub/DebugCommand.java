/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.command.CommandSender
 */
package com.ticxo.modelengine.core.command.sub;

import com.ticxo.modelengine.api.command.AbstractCommand;
import com.ticxo.modelengine.api.utils.config.DebugToggle;
import com.ticxo.modelengine.api.utils.logger.LogColor;
import com.ticxo.modelengine.core.command.MECommand;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class DebugCommand
extends AbstractCommand {
    public DebugCommand(AbstractCommand parent) {
        super(parent);
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        if (args.length == 0) {
            return false;
        }
        DebugToggle debugToggle = DebugToggle.get(args[0]);
        if (debugToggle == null) {
            MECommand.logSender(sender, ChatColor.RED + "[ModelEngine] Unknown debug: " + args[0] + ".", LogColor.RED + "Unknown debug: " + args[0] + ".");
            return false;
        }
        if (args.length == 1) {
            MECommand.logSender(sender, ChatColor.GREEN + "[ModelEngine] " + debugToggle.name() + " is " + (DebugToggle.isDebugging(debugToggle) ? "enabled." : "disabled."), LogColor.BRIGHT_GREEN + debugToggle.name() + " is " + (DebugToggle.isDebugging(debugToggle) ? "enabled." : "disabled."));
            return true;
        }
        boolean flag = Boolean.parseBoolean(args[1]);
        DebugToggle.setDebug(debugToggle, flag);
        MECommand.logSender(sender, ChatColor.GREEN + "[ModelEngine] Set " + debugToggle.name() + " to " + flag + ".", LogColor.BRIGHT_GREEN + "Set " + debugToggle.name() + " to " + flag + ".");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        ArrayList<String> list = new ArrayList<String>();
        switch (args.length) {
            case 1: {
                for (DebugToggle debug : DebugToggle.values()) {
                    list.add(debug.name());
                }
                break;
            }
            case 2: {
                list.add("true");
                list.add("false");
            }
        }
        return list;
    }

    @Override
    public String getPermissionNode() {
        return "modelengine.command.debug";
    }

    @Override
    public boolean isConsoleFriendly() {
        return true;
    }

    @Override
    public String getName() {
        return "debug";
    }
}

