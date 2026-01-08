/*
 * Decompiled with CFR 0.152.
 */
package org.mineskin.request.source;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.mineskin.request.source.UploadSource;

public class FileUploadSource
implements UploadSource {
    private final File file;

    FileUploadSource(File file) {
        this.file = file;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new FileInputStream(this.file);
    }
}

