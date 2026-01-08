/*
 * Decompiled with CFR 0.152.
 */
package org.jsoup.internal;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import org.jsoup.helper.Validate;

public class ControllableInputStream
extends FilterInputStream {
    private final BufferedInputStream buff;
    private final boolean capped;
    private final int maxSize;
    private long startTime;
    private long timeout = 0L;
    private int remaining;
    private int markPos;
    private boolean interrupted;

    private ControllableInputStream(BufferedInputStream in, int maxSize) {
        super(in);
        Validate.isTrue(maxSize >= 0);
        this.buff = in;
        this.capped = maxSize != 0;
        this.maxSize = maxSize;
        this.remaining = maxSize;
        this.markPos = -1;
        this.startTime = System.nanoTime();
    }

    public static ControllableInputStream wrap(InputStream in, int bufferSize, int maxSize) {
        if (in instanceof ControllableInputStream) {
            return (ControllableInputStream)in;
        }
        if (in instanceof BufferedInputStream) {
            return new ControllableInputStream((BufferedInputStream)in, maxSize);
        }
        return new ControllableInputStream(new BufferedInputStream(in, bufferSize), maxSize);
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

    public static ByteBuffer readToByteBuffer(InputStream in, int max) throws IOException {
        int read;
        Validate.isTrue(max >= 0, "maxSize must be 0 (unlimited) or larger");
        Validate.notNull(in);
        boolean localCapped = max > 0;
        int bufferSize = localCapped && max < 32768 ? max : 32768;
        byte[] readBuffer = new byte[bufferSize];
        ByteArrayOutputStream outStream = new ByteArrayOutputStream(bufferSize);
        int remaining = max;
        while ((read = in.read(readBuffer, 0, localCapped ? Math.min(remaining, bufferSize) : bufferSize)) != -1) {
            if (localCapped) {
                if (read >= remaining) {
                    outStream.write(readBuffer, 0, remaining);
                    break;
                }
                remaining -= read;
            }
            outStream.write(readBuffer, 0, read);
        }
        return ByteBuffer.wrap(outStream.toByteArray());
    }

    @Override
    public void reset() throws IOException {
        super.reset();
        this.remaining = this.maxSize - this.markPos;
    }

    @Override
    public void mark(int readlimit) {
        super.mark(readlimit);
        this.markPos = this.maxSize - this.remaining;
    }

    public ControllableInputStream timeout(long startTimeNanos, long timeoutMillis) {
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

    public BufferedInputStream inputStream() {
        return this.buff;
    }
}

