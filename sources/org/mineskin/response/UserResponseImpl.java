/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 */
package org.mineskin.response;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Map;
import org.mineskin.data.User;
import org.mineskin.data.UserInfo;
import org.mineskin.response.AbstractMineSkinResponse;
import org.mineskin.response.UserResponse;

public class UserResponseImpl
extends AbstractMineSkinResponse<UserInfo>
implements UserResponse {
    public UserResponseImpl(int status, Map<String, String> headers, JsonObject rawBody, Gson gson, Class<UserInfo> clazz) {
        super(status, headers, rawBody, gson, "skin", clazz);
    }

    @Override
    protected UserInfo parseBody(JsonObject rawBody, Gson gson, String primaryField, Class<UserInfo> clazz) {
        return (UserInfo)gson.fromJson((JsonElement)rawBody, clazz);
    }

    @Override
    public User getUser() {
        return (User)this.getBody();
    }
}

