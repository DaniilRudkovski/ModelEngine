/*
 * Decompiled with CFR 0.152.
 */
package org.mineskin.request.source;

import java.io.IOException;
import java.io.InputStream;
import org.mineskin.request.source.UploadSource;

public class InputStreamUploadSource
implements UploadSource {
    private final InputStream inputStream;

    InputStreamUploadSource(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return this.inputStream;
    }
}

