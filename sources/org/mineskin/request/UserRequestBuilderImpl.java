/*
 * Decompiled with CFR 0.152.
 */
package org.mineskin.request;

import java.util.UUID;
import org.mineskin.request.AbstractRequestBuilder;
import org.mineskin.request.UserRequestBuilder;

public class UserRequestBuilderImpl
extends AbstractRequestBuilder
implements UserRequestBuilder {
    private final UUID uuid;

    UserRequestBuilderImpl(UUID uuid) {
        this.uuid = uuid;
    }

    @Override
    public UUID getUuid() {
        return this.uuid;
    }
}

