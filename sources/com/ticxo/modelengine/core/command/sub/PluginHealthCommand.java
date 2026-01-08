/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.command.CommandSender
 */
package com.ticxo.modelengine.core.command.sub;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.command.AbstractCommand;
import com.ticxo.modelengine.api.entity.EntityDataTrackers;
import com.ticxo.modelengine.api.utils.Profiler;
import com.ticxo.modelengine.core.command.MECommand;
import java.util.List;
import org.bukkit.command.CommandSender;

public class PluginHealthCommand
extends AbstractCommand {
    public PluginHealthCommand(AbstractCommand parent) {
        super(parent);
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        Profiler updaterProfiler = ModelEngineAPI.getAPI().getModelUpdaters().getProfiler();
        MECommand.logSender(sender, String.format("Model Updaters: Min: %.3f ms, Max: %.3f ms, Avg: %.3f ms", (double)updaterProfiler.getMinTime() / 1000000.0, (double)updaterProfiler.getMaxTime() / 1000000.0, updaterProfiler.getAverageTime() / 1000000.0));
        EntityDataTrackers trackers = ModelEngineAPI.getAPI().getDataTrackers();
        MECommand.logSender(sender, "Entity Data Trackers:");
        for (EntityDataTrackers.Tracker tracker : trackers.getAvailable()) {
            MECommand.logSender(sender, String.format("- %s: %s (%sms)", tracker.getId(), tracker.getLoad(), tracker.getTimings()));
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return null;
    }

    @Override
    public String getPermissionNode() {
        return "modelengine.command.health";
    }

    @Override
    public boolean isConsoleFriendly() {
        return true;
    }

    @Override
    public String getName() {
        return "health";
    }
}

