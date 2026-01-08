/*
 * Decompiled with CFR 0.152.
 */
package com.ticxo.modelengine.core21.mythic.utils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(value=RetentionPolicy.RUNTIME)
public @interface MythicCondition {
    public String name() default "";

    public String[] aliases() default {};

    public String author() default "";

    public String description() default "";

    public String version() default "4.0";

    public boolean premium() default false;

    public String namespace() default "meg";
}

