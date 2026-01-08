/*
 * Decompiled with CFR 0.152.
 */
package org.mineskin.request;

import java.awt.image.RenderedImage;
import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.UUID;
import org.mineskin.GenerateOptions;
import org.mineskin.data.Variant;
import org.mineskin.data.Visibility;
import org.mineskin.request.UploadRequestBuilder;
import org.mineskin.request.UploadRequestBuilderImpl;
import org.mineskin.request.UrlRequestBuilder;
import org.mineskin.request.UrlRequestBuilderImpl;
import org.mineskin.request.UserRequestBuilder;
import org.mineskin.request.UserRequestBuilderImpl;
import org.mineskin.request.source.UploadSource;

public interface GenerateRequest {
    public static UploadRequestBuilder upload(UploadSource uploadSource) {
        return new UploadRequestBuilderImpl(uploadSource);
    }

    public static UploadRequestBuilder upload(InputStream inputStream) {
        return GenerateRequest.upload(UploadSource.of(inputStream));
    }

    public static UploadRequestBuilder upload(File file) {
        return GenerateRequest.upload(UploadSource.of(file));
    }

    public static UploadRequestBuilder upload(RenderedImage renderedImage) {
        return GenerateRequest.upload(UploadSource.of(renderedImage));
    }

    public static UrlRequestBuilder url(URL url) {
        return new UrlRequestBuilderImpl(url);
    }

    public static UrlRequestBuilder url(URI uri) throws MalformedURLException {
        return GenerateRequest.url(uri.toURL());
    }

    public static UrlRequestBuilder url(String url) throws MalformedURLException {
        return GenerateRequest.url(URI.create(url));
    }

    public static UserRequestBuilder user(UUID uuid) {
        return new UserRequestBuilderImpl(uuid);
    }

    public static UserRequestBuilder user(String uuid) {
        return GenerateRequest.user(UUID.fromString(uuid));
    }

    public GenerateRequest options(GenerateOptions var1);

    public GenerateRequest visibility(Visibility var1);

    public GenerateRequest variant(Variant var1);

    public GenerateRequest name(String var1);

    public GenerateRequest cape(UUID var1);

    public GenerateRequest cape(String var1);

    public GenerateOptions options();
}

