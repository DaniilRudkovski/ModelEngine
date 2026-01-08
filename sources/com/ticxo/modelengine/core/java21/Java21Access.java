/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 */
package com.ticxo.modelengine.core.java21;

import com.ticxo.modelengine.api.utils.CompatibilityManager;
import com.ticxo.modelengine.api.utils.MiscUtils;
import com.ticxo.modelengine.api.utils.ReflectionUtils;
import com.ticxo.modelengine.core.ModelEngine;
import lombok.Generated;

public enum Java21Access implements ReflectionUtils.MethodEnum
{
    registerCompatibility(CompatibilityManager.class, ModelEngine.class),
    createNMSHandler(String.class),
    getMythicPackModelFiles(new Class[0]);

    private static Class<?> HELPER;
    private final Class<?>[] parameterClasses;

    private static Class<?> getHelper() {
        if (!MiscUtils.isJava21OrHigher()) {
            throw new RuntimeException("Cannot get Java 21 Helper: Server is not using Java 21+!");
        }
        if (HELPER == null) {
            try {
                HELPER = Class.forName("com.ticxo.modelengine.core21.Java21Helper");
            }
            catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        return HELPER;
    }

    private Java21Access(Class<?> ... parameterClasses) {
        this.parameterClasses = parameterClasses;
    }

    @Override
    public Class<?> getTarget() {
        return Java21Access.getHelper();
    }

    @Override
    public String getObfuscated() {
        return this.toString();
    }

    @Override
    public String getMapped() {
        return this.toString();
    }

    public <T> T call(Object ... parameters) {
        if (!MiscUtils.isJava21OrHigher()) {
            return null;
        }
        return ReflectionUtils.call(null, this, parameters);
    }

    @Override
    @Generated
    public Class<?>[] getParameterClasses() {
        return this.parameterClasses;
    }
}

