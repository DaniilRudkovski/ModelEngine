/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.reflect.TypeToken
 */
package org.mineskin.data;

import com.google.gson.reflect.TypeToken;
import java.util.List;

public record CodeAndMessage(String code, String message) {
    public static final TypeToken<List<CodeAndMessage>> LIST_TYPE_TOKEN = new TypeToken<List<CodeAndMessage>>(){};
}

