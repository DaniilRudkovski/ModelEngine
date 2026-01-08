/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 */
package com.ticxo.modelengine.api.utils.logger;

import com.ticxo.modelengine.api.utils.config.ConfigProperty;
import com.ticxo.modelengine.api.utils.logger.LogColor;
import java.util.Objects;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;

public class TLogger {
    public static Logger logger;

    public static void log() {
        TLogger.log("");
    }

    public static void log(String string) {
        TLogger.log(1, string);
    }

    public static void log(int level, String string) {
        if (ConfigProperty.DEBUG_LEVEL.getInt() >= level) {
            logger.log(Level.INFO, TLogger.getThread() + string + LogColor.RESET);
        }
    }

    public static void log(Object object) {
        TLogger.log(object == null ? "null" : object.toString());
    }

    public static <T> void log(Iterable<T> iterable) {
        TLogger.log(1, iterable);
    }

    public static <T> void log(Iterable<T> iterable, Function<T, String> toString) {
        TLogger.log(1, iterable, toString);
    }

    public static <T> void log(int level, Iterable<T> iterable) {
        TLogger.log(level, iterable, Object::toString);
    }

    public static <T> void log(int level, Iterable<T> iterable, Function<T, String> toString) {
        String className = iterable.getClass().getSimpleName();
        StringBuilder builder = new StringBuilder();
        builder.append(className).append(":[");
        boolean isFirst = true;
        for (T value : iterable) {
            if (!isFirst) {
                builder.append(", ");
            } else {
                isFirst = false;
            }
            builder.append(toString.apply(value));
        }
        builder.append("]");
        TLogger.log(level, builder.toString());
    }

    public static <T> void log(T[] array) {
        TLogger.log(1, array);
    }

    public static <T> void log(T[] array, Function<T, String> toString) {
        TLogger.log(1, array, toString);
    }

    public static <T> void log(int level, T[] array) {
        TLogger.log(level, array, Objects::toString);
    }

    public static <T> void log(int level, T[] array, Function<T, String> toString) {
        String className = array.getClass().getSimpleName();
        StringBuilder builder = new StringBuilder();
        builder.append(className).append(":[");
        boolean isFirst = true;
        for (T value : array) {
            if (!isFirst) {
                builder.append(", ");
            } else {
                isFirst = false;
            }
            builder.append(toString.apply(value));
        }
        builder.append("]");
        TLogger.log(level, builder.toString());
    }

    public static void stacktrace() {
        if (!TLogger.isDebugEnabled()) {
            return;
        }
        TLogger.log(Thread.currentThread().getStackTrace(), (T stackTraceElement) -> "\n" + stackTraceElement);
    }

    public static void debug() {
        if (!TLogger.isDebugEnabled()) {
            return;
        }
        TLogger.log();
    }

    public static void debug(String string) {
        if (!TLogger.isDebugEnabled()) {
            return;
        }
        TLogger.log(string);
    }

    public static void debug(int level, String string) {
        if (!TLogger.isDebugEnabled()) {
            return;
        }
        TLogger.log(level, string);
    }

    public static void debug(Object object) {
        if (!TLogger.isDebugEnabled()) {
            return;
        }
        TLogger.log(object);
    }

    public static <T> void debug(Iterable<T> iterable) {
        TLogger.debug(iterable, Objects::toString);
    }

    public static <T> void debug(Iterable<T> iterable, Function<T, String> toString) {
        if (!TLogger.isDebugEnabled()) {
            return;
        }
        TLogger.log(iterable, toString);
    }

    public static <T> void debug(T[] array) {
        TLogger.debug(array, Objects::toString);
    }

    public static <T> void debug(T[] array, Function<T, String> toString) {
        if (!TLogger.isDebugEnabled()) {
            return;
        }
        TLogger.log(array, toString);
    }

    private static boolean isDebugEnabled() {
        return ConfigProperty.DEBUG_LEVEL.getInt() == 157;
    }

    public static void warn(String string) {
        TLogger.warn(1, string);
    }

    public static void warn(int level, String string) {
        if (ConfigProperty.DEBUG_LEVEL.getInt() >= level) {
            logger.log(Level.WARNING, TLogger.getThread() + LogColor.YELLOW + string + LogColor.RESET);
        }
    }

    public static void error(String string) {
        TLogger.error(1, string);
    }

    public static void error(int level, String string) {
        if (ConfigProperty.DEBUG_LEVEL.getInt() >= level) {
            logger.log(Level.WARNING, TLogger.getThread() + LogColor.RED + string + LogColor.RESET);
        }
    }

    private static String getThread() {
        return Bukkit.getServer().isPrimaryThread() ? "[S] " : "[A] ";
    }
}

