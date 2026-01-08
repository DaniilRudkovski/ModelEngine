/*
 * Decompiled with CFR 0.152.
 */
package com.ticxo.modelengine.api.utils;

import com.ticxo.modelengine.api.utils.ticker.DualTicker;
import java.util.ConcurrentModificationException;

public class RaceConditionUtil {
    public static void wrapConmod(Runnable runnable) {
        RaceConditionUtil.wrapConmod(runnable, 1);
    }

    public static void wrapConmod(Runnable runnable, int delay) {
        RaceConditionUtil.wrapConmod(runnable, delay, 1);
    }

    public static void wrapConmod(Runnable runnable, int delay, int attempts) {
        try {
            runnable.run();
        }
        catch (ConcurrentModificationException ignored) {
            if (attempts > 0) {
                DualTicker.queueDelayedSyncTask(() -> RaceConditionUtil.wrapConmod(runnable, delay, attempts - 1), delay);
            }
        }
        catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static void wrapAll(Runnable runnable) {
        RaceConditionUtil.wrapAll(runnable, 1);
    }

    public static void wrapAll(Runnable runnable, int delay) {
        RaceConditionUtil.wrapAll(runnable, delay, 1);
    }

    public static void wrapAll(Runnable runnable, int delay, int attempts) {
        try {
            runnable.run();
        }
        catch (Throwable e) {
            if (attempts > 0) {
                DualTicker.queueDelayedSyncTask(() -> RaceConditionUtil.wrapConmod(runnable, delay, attempts - 1), delay);
            }
            e.printStackTrace();
        }
    }
}

