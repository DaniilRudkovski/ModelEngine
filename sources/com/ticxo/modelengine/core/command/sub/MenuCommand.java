/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 */
package com.ticxo.modelengine.core.command.sub;

import com.ticxo.modelengine.api.command.AbstractCommand;
import com.ticxo.modelengine.core.menu.screen.NavigationScreen;
import java.util.List;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MenuCommand
extends AbstractCommand {
    public MenuCommand(AbstractCommand parent) {
        super(parent);
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player)sender;
            new NavigationScreen(player).openScreen();
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return null;
    }

    @Override
    public String getPermissionNode() {
        return "modelengine.command.menu";
    }

    @Override
    public boolean isConsoleFriendly() {
        return false;
    }

    @Override
    public String getName() {
        return "menu";
    }
}

