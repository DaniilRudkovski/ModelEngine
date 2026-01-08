/*
 * Decompiled with CFR 0.152.
 */
package org.mineskin.request;

import java.net.URL;
import org.mineskin.request.AbstractRequestBuilder;
import org.mineskin.request.UrlRequestBuilder;

public class UrlRequestBuilderImpl
extends AbstractRequestBuilder
implements UrlRequestBuilder {
    private final URL url;

    UrlRequestBuilderImpl(URL url) {
        this.url = url;
    }

    @Override
    public URL getUrl() {
        return this.url;
    }
}

