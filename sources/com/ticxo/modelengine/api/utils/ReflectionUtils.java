/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  lombok.Generated
 *  org.jetbrains.annotations.Nullable
 */
package com.ticxo.modelengine.api.utils;

import com.google.common.collect.Maps;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Generated;
import org.jetbrains.annotations.Nullable;

public class ReflectionUtils {
    private static final Map<Class<?>, Class<?>> PRIMITIVE_EQUIV = new HashMap<Class<?>, Class<?>>(){
        {
            this.put(Byte.TYPE, Byte.class);
            this.put(Short.TYPE, Short.class);
            this.put(Integer.TYPE, Integer.class);
            this.put(Long.TYPE, Long.class);
            this.put(Float.TYPE, Float.class);
            this.put(Double.TYPE, Double.class);
            this.put(Boolean.TYPE, Boolean.class);
            this.put(Character.TYPE, Character.class);
        }
    };
    private static final Map<Class<?>, ConcurrentHashMap<String, ReflectionEnum>> DYNAMIC_FIELDS = Maps.newConcurrentMap();
    private static final Map<Class<?>, ConcurrentHashMap<ReflectionEnum, Field>> FIELD_MAP = Maps.newConcurrentMap();
    private static final Map<Class<?>, ConcurrentHashMap<MethodEnum, Method>> METHOD_MAP = Maps.newConcurrentMap();
    private static final boolean IS_MAPPED = Boolean.getBoolean("modelengine.mapped");

    public static Field unlockField(ReflectionEnum field) {
        try {
            Field f = field.getTarget().getDeclaredField(field.get(IS_MAPPED));
            f.setAccessible(true);
            return f;
        }
        catch (IllegalArgumentException | NoSuchFieldException | SecurityException e) {
            try {
                Field f = field.getTarget().getDeclaredField(field.get(!IS_MAPPED));
                f.setAccessible(true);
                return f;
            }
            catch (IllegalArgumentException | NoSuchFieldException | SecurityException e2) {
                throw new RuntimeException("An error occurred while unlocking field: " + field.getMapped(), e);
            }
        }
    }

    public static Method unlockMethod(MethodEnum method) {
        try {
            Method m = method.getTarget().getDeclaredMethod(method.get(IS_MAPPED), method.getParameterClasses());
            m.setAccessible(true);
            return m;
        }
        catch (IllegalArgumentException | NoSuchMethodException | SecurityException e) {
            try {
                Method m = method.getTarget().getDeclaredMethod(method.get(!IS_MAPPED), method.getParameterClasses());
                m.setAccessible(true);
                return m;
            }
            catch (IllegalArgumentException | NoSuchMethodException | SecurityException e2) {
                throw new RuntimeException("An error occurred while unlocking method: " + method.getMapped(), e);
            }
        }
    }

    public static Field getField(Class<?> clazz, String field) {
        return ReflectionUtils.getField(DYNAMIC_FIELDS.computeIfAbsent(clazz, aClass -> new ConcurrentHashMap()).computeIfAbsent(field, s -> new RuntimeReflection(clazz, field)));
    }

    public static Field getField(ReflectionEnum field) {
        return FIELD_MAP.computeIfAbsent(field.getTarget(), aClass -> new ConcurrentHashMap()).computeIfAbsent(field, ReflectionUtils::unlockField);
    }

    public static Method getMethod(MethodEnum method) {
        return METHOD_MAP.computeIfAbsent(method.getTarget(), aClass -> new ConcurrentHashMap()).computeIfAbsent(method, ReflectionUtils::unlockMethod);
    }

    @Nullable
    public static <T> T get(Object object, ReflectionEnum field) {
        try {
            return (T)ReflectionUtils.getField(field).get(object);
        }
        catch (IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Nullable
    public static <T> T get(Object object, ReflectionEnum field, T def) {
        try {
            return (T)ReflectionUtils.getField(field).get(object);
        }
        catch (IllegalAccessException e) {
            e.printStackTrace();
            return def;
        }
    }

    @Nullable
    public static <T> T get(ReflectionEnum field) {
        return ReflectionUtils.get(null, field);
    }

    public static boolean set(Object object, ReflectionEnum field, Object val) {
        try {
            ReflectionUtils.getField(field).set(object, val);
            return true;
        }
        catch (IllegalAccessException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean set(ReflectionEnum field, Object val) {
        return ReflectionUtils.set(null, field, val);
    }

    public static <T> T call(Object object, MethodEnum method, Object ... parameters) {
        try {
            Class<?>[] paramClasses = method.getParameterClasses();
            if (paramClasses.length > parameters.length) {
                throw new RuntimeException(String.format("Invalid method call: Missing parameters. Expected %s, got %s.", paramClasses.length, parameters.length));
            }
            for (int i = 0; i < paramClasses.length; ++i) {
                Class<?> expectClass = paramClasses[i];
                Class<?> givenClass = parameters[i].getClass();
                if (expectClass.isAssignableFrom(givenClass) || !ReflectionUtils.isDifferentPrimitive(givenClass, expectClass)) continue;
                throw new RuntimeException(String.format("Invalid method call: Invalid parameter at position %s. Expected %s, got %s.", i, expectClass.getSimpleName(), givenClass.getSimpleName()));
            }
            Method m = ReflectionUtils.getMethod(method);
            return (T)m.invoke(object, parameters);
        }
        catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <T> T call(MethodEnum method, Object ... parameters) {
        return ReflectionUtils.call(null, method, parameters);
    }

    private static boolean isDifferentPrimitive(Class<?> givenClass, Class<?> expectClass) {
        if (!expectClass.isPrimitive()) {
            return true;
        }
        Class<?> boxed = PRIMITIVE_EQUIV.get(expectClass);
        return !boxed.isAssignableFrom(givenClass);
    }

    public static interface ReflectionEnum {
        public Class<?> getTarget();

        public String getObfuscated();

        public String getMapped();

        default public String get(boolean mapped) {
            return mapped ? this.getMapped() : this.getObfuscated();
        }
    }

    public static interface MethodEnum
    extends ReflectionEnum {
        public Class<?>[] getParameterClasses();
    }

    static class RuntimeReflection
    implements ReflectionEnum {
        private final Class<?> target;
        private final String field;

        @Override
        public String getObfuscated() {
            return this.field;
        }

        @Override
        public String getMapped() {
            return this.field;
        }

        @Generated
        public RuntimeReflection(Class<?> target, String field) {
            this.target = target;
            this.field = field;
        }

        @Override
        @Generated
        public Class<?> getTarget() {
            return this.target;
        }

        @Generated
        public String getField() {
            return this.field;
        }
    }
}

