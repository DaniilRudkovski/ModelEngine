/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.kyori.adventure.text.Component
 *  net.kyori.adventure.text.format.NamedTextColor
 *  net.kyori.adventure.text.format.TextColor
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandSender
 *  org.bukkit.command.TabExecutor
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.java.JavaPlugin
 *  org.bukkit.util.StringUtil
 *  org.jetbrains.annotations.NotNull
 */
package com.ticxo.modelengine.api.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractCommand
implements TabExecutor {
    protected final JavaPlugin plugin;
    private final Map<String, AbstractCommand> subCommands = new HashMap<String, AbstractCommand>();
    private final Map<String, AbstractCommand> subCommandAliases = new HashMap<String, AbstractCommand>();

    public AbstractCommand(AbstractCommand parent) {
        this(parent.getPlugin());
    }

    public AbstractCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public final void addSubCommands(AbstractCommand ... commands) {
        for (AbstractCommand command : commands) {
            this.subCommands.put(command.getName(), command);
            for (String alias : command.getAliases()) {
                this.subCommandAliases.put(alias, command);
            }
        }
    }

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (this.getPermissionNode() != null && !sender.hasPermission(this.getPermissionNode()) && !sender.hasPermission("modelengine.admin")) {
            sender.sendMessage(Component.text((String)"You don't have permission to do this!").color((TextColor)NamedTextColor.RED));
            return true;
        }
        if (!this.isConsoleFriendly() && !(sender instanceof Player)) {
            sender.sendMessage(Component.text((String)"Only players can do this!").color((TextColor)NamedTextColor.RED));
            return true;
        }
        if (args.length > 0 && this.subCommands.get(args[0].toLowerCase()) != null) {
            AbstractCommand sub = this.subCommands.get(args[0].toLowerCase());
            return sub.onCommand(sender, cmd, label, Arrays.copyOfRange(args, 1, args.length));
        }
        if (args.length > 0 && this.subCommandAliases.get(args[0].toLowerCase()) != null) {
            AbstractCommand sub = this.subCommandAliases.get(args[0].toLowerCase());
            return sub.onCommand(sender, cmd, label, Arrays.copyOfRange(args, 1, args.length));
        }
        return this.onCommand(sender, args);
    }

    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (this.getPermissionNode() != null && !sender.hasPermission(this.getPermissionNode())) {
            return null;
        }
        if (args.length > 1 && this.subCommands.get(args[0].toLowerCase()) != null) {
            AbstractCommand sub = this.subCommands.get(args[0].toLowerCase());
            return sub.onTabComplete(sender, cmd, label, Arrays.copyOfRange(args, 1, args.length));
        }
        if (args.length > 1 && this.subCommandAliases.get(args[0].toLowerCase()) != null) {
            AbstractCommand sub = this.subCommandAliases.get(args[0].toLowerCase());
            return sub.onTabComplete(sender, cmd, label, Arrays.copyOfRange(args, 1, args.length));
        }
        List<String> result = this.onTabComplete(sender, args);
        if (result == null && args.length == 1) {
            result = new ArrayList<String>();
            StringUtil.copyPartialMatches((String)args[0], this.subCommands.keySet(), result);
        }
        return result;
    }

    public abstract boolean onCommand(CommandSender var1, String[] var2);

    public abstract List<String> onTabComplete(CommandSender var1, String[] var2);

    public abstract String getPermissionNode();

    public abstract boolean isConsoleFriendly();

    public String[] getAliases() {
        return new String[0];
    }

    public abstract String getName();

    protected JavaPlugin getPlugin() {
        return this.plugin;
    }
}

