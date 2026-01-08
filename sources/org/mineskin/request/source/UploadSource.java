/*
 * Decompiled with CFR 0.152.
 */
package org.mineskin.request.source;

import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import org.mineskin.request.source.FileUploadSource;
import org.mineskin.request.source.InputStreamUploadSource;
import org.mineskin.request.source.RenderedImageUploadSource;

public interface UploadSource {
    public static UploadSource of(InputStream inputStream) {
        return new InputStreamUploadSource(inputStream);
    }

    public static UploadSource of(File file) {
        return new FileUploadSource(file);
    }

    public static UploadSource of(RenderedImage renderedImage) {
        return new RenderedImageUploadSource(renderedImage);
    }

    public InputStream getInputStream() throws IOException;
}

