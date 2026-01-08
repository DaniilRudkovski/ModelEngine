/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  org.bukkit.Bukkit
 */
package com.ticxo.modelengine.api;

import com.google.common.collect.ImmutableSet;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import org.bukkit.Bukkit;

public class ServerInfo {
    public static final boolean IS_PAPER;
    public static final boolean IS_FOLIA;
    public static final String NMS_VERSION;
    public static final int VERSION_NUMBER;
    public static boolean HAS_VIAVERSION;
    public static boolean HAS_CITIZENS;
    private static final Set<UUID> ONLINE_PLAYERS;

    public static void setOnline(UUID uuid, boolean flag) {
        if (flag) {
            ONLINE_PLAYERS.add(uuid);
        } else {
            ONLINE_PLAYERS.remove(uuid);
        }
    }

    public static ImmutableSet<UUID> getOnlinePlayers() {
        return ImmutableSet.copyOf(ONLINE_PLAYERS);
    }

    private static boolean classExists(String path) {
        try {
            Class.forName(path);
            return true;
        }
        catch (ClassNotFoundException ignore) {
            return false;
        }
    }

    static {
        ONLINE_PLAYERS = new HashSet<UUID>();
        IS_PAPER = ServerInfo.classExists("com.destroystokyo.paper.VersionHistoryManager$VersionData");
        IS_FOLIA = ServerInfo.classExists("io.papermc.paper.threadedregions.RegionizedServer");
        NMS_VERSION = Bukkit.getServer().getMinecraftVersion();
        VERSION_NUMBER = Integer.parseInt(NMS_VERSION.split("\\.")[1]);
    }
}

