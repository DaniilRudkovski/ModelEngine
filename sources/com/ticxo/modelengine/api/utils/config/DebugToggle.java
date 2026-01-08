/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package com.ticxo.modelengine.api.utils.config;

import java.util.HashSet;
import java.util.Set;
import org.jetbrains.annotations.Nullable;

public enum DebugToggle {
    SHOW_OBB,
    SHOW_CULL_POINTS,
    NOTIFY_DESYNC;

    private static final Set<DebugToggle> toggles;

    public static void setDebug(DebugToggle debug, boolean flag) {
        if (flag) {
            toggles.add(debug);
        } else {
            toggles.remove((Object)debug);
        }
    }

    public static boolean isDebugging(DebugToggle debug) {
        return toggles.contains((Object)debug);
    }

    @Nullable
    public static DebugToggle get(String value) {
        try {
            return DebugToggle.valueOf(value);
        }
        catch (Throwable ignored) {
            return null;
        }
    }

    static {
        toggles = new HashSet<DebugToggle>();
    }
}

