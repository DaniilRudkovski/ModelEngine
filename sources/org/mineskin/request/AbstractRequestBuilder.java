/*
 * Decompiled with CFR 0.152.
 */
package org.mineskin.request;

import java.util.UUID;
import org.mineskin.GenerateOptions;
import org.mineskin.data.Variant;
import org.mineskin.data.Visibility;
import org.mineskin.request.GenerateRequest;

public abstract class AbstractRequestBuilder
implements GenerateRequest {
    private GenerateOptions options = GenerateOptions.create();

    @Override
    public GenerateRequest options(GenerateOptions options) {
        this.options = options;
        return this;
    }

    @Override
    public GenerateRequest visibility(Visibility visibility) {
        this.options.visibility(visibility);
        return this;
    }

    @Override
    public GenerateRequest variant(Variant variant) {
        this.options.variant(variant);
        return this;
    }

    @Override
    public GenerateRequest name(String name) {
        this.options.name(name);
        return this;
    }

    @Override
    public GenerateRequest cape(UUID cape) {
        this.options.cape(cape);
        return this;
    }

    @Override
    public GenerateRequest cape(String cape) {
        this.options.cape(cape);
        return this;
    }

    @Override
    public GenerateOptions options() {
        return this.options;
    }
}

