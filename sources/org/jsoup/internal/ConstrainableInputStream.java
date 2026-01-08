/*
 * Decompiled with CFR 0.152.
 */
package org.jsoup.internal;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import org.jsoup.helper.DataUtil;
import org.jsoup.helper.Validate;

@Deprecated
public final class ConstrainableInputStream
extends BufferedInputStream {
    private final boolean capped;
    private final int maxSize;
    private long startTime;
    private long timeout = 0L;
    private int remaining;
    private boolean interrupted;

    private ConstrainableInputStream(InputStream in, int bufferSize, int maxSize) {
        super(in, bufferSize);
        Validate.isTrue(maxSize >= 0);
        this.maxSize = maxSize;
        this.remaining = maxSize;
        this.capped = maxSize != 0;
        this.startTime = System.nanoTime();
    }

    public static ConstrainableInputStream wrap(InputStream in, int bufferSize, int maxSize) {
        return in instanceof ConstrainableInputStream ? (ConstrainableInputStream)in : new ConstrainableInputStream(in, bufferSize, maxSize);
    }

    @Override
    public int read(byte[] b2, int off, int len) throws IOException {
        if (this.interrupted || this.capped && this.remaining <= 0) {
            return -1;
        }
        if (Thread.currentThread().isInterrupted()) {
            this.interrupted = true;
            return -1;
        }
        if (this.expired()) {
            throw new SocketTimeoutException("Read timeout");
        }
        if (this.capped && len > this.remaining) {
            len = this.remaining;
        }
        try {
            int read = super.read(b2, off, len);
            this.remaining -= read;
            return read;
        }
        catch (SocketTimeoutException e) {
            return 0;
        }
    }

    public ByteBuffer readToByteBuffer(int max) throws IOException {
        return DataUtil.readToByteBuffer(this, max);
    }

    @Override
    public void reset() throws IOException {
        super.reset();
        this.remaining = this.maxSize - this.markpos;
    }

    public ConstrainableInputStream timeout(long startTimeNanos, long timeoutMillis) {
        this.startTime = startTimeNanos;
        this.timeout = timeoutMillis * 1000000L;
        return this;
    }

    private boolean expired() {
        if (this.timeout == 0L) {
            return false;
        }
        long now = System.nanoTime();
        long dur = now - this.startTime;
        return dur > this.timeout;
    }
}

