/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package org.mineskin.exception;

import javax.annotation.Nullable;
import org.mineskin.exception.IBreadcrumbException;

public class MineskinException
extends RuntimeException
implements IBreadcrumbException {
    private String breadcrumb;

    public MineskinException() {
    }

    public MineskinException(String message) {
        super(message);
    }

    public MineskinException(String message, Throwable cause) {
        super(message, cause);
    }

    public MineskinException(Throwable cause) {
        super(cause);
    }

    public MineskinException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public MineskinException withBreadcrumb(String breadcrumb) {
        this.breadcrumb = breadcrumb;
        return this;
    }

    @Override
    @Nullable
    public String getBreadcrumb() {
        return this.breadcrumb;
    }
}

