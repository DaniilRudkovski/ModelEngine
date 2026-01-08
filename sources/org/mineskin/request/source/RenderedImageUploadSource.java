/*
 * Decompiled with CFR 0.152.
 */
package org.mineskin.request.source;

import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
import org.mineskin.request.source.UploadSource;

public class RenderedImageUploadSource
implements UploadSource {
    private final RenderedImage image;

    public RenderedImageUploadSource(RenderedImage image) {
        this.image = image;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(this.image, "png", outputStream);
        return new ByteArrayInputStream(outputStream.toByteArray());
    }
}

