/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.JsonObject
 */
package org.mineskin.response;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.util.Map;
import org.mineskin.data.SkinInfo;
import org.mineskin.response.AbstractMineSkinResponse;
import org.mineskin.response.SkinResponse;

public class SkinResponseImpl
extends AbstractMineSkinResponse<SkinInfo>
implements SkinResponse {
    public SkinResponseImpl(int status, Map<String, String> headers, JsonObject rawBody, Gson gson, Class<SkinInfo> clazz) {
        super(status, headers, rawBody, gson, "skin", clazz);
    }

    @Override
    public SkinInfo getSkin() {
        return (SkinInfo)this.getBody();
    }
}

