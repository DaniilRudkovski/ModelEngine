/*
 * Decompiled with CFR 0.152.
 */
package com.ticxo.modelengine.api.error;

import com.ticxo.modelengine.api.error.ErrorCollector;
import com.ticxo.modelengine.api.error.ErrorUnknownFormat;
import com.ticxo.modelengine.api.error.WarnBadEyeHeight;
import com.ticxo.modelengine.api.error.WarnBoxUV;
import com.ticxo.modelengine.api.error.WarnNoHitbox;
import com.ticxo.modelengine.api.utils.config.ConfigProperty;
import com.ticxo.modelengine.api.utils.logger.TLogger;

public interface IError {
    public static final ErrorUnknownFormat UNKNOWN_FORMAT = new ErrorUnknownFormat();
    public static final WarnBoxUV BOX_UV = new WarnBoxUV();
    public static final WarnBadEyeHeight BAD_EYE_HEIGHT = new WarnBadEyeHeight();
    public static final WarnNoHitbox NO_HITBOX = new WarnNoHitbox();

    public String getErrorMessage();

    public Severity getSeverity();

    default public void log(ErrorCollector collector) {
        collector.collect(this);
    }

    default public void log() {
        if (!ConfigProperty.ERROR.getBoolean()) {
            return;
        }
        switch (this.getSeverity()) {
            case WARN: {
                TLogger.warn("--" + this.getErrorMessage());
                break;
            }
            case ERROR: {
                TLogger.error("--" + this.getErrorMessage());
            }
        }
    }

    public static enum Severity {
        WARN,
        ERROR;

    }

    public static abstract class Error
    implements IError {
        @Override
        public Severity getSeverity() {
            return Severity.ERROR;
        }
    }

    public static abstract class Warn
    implements IError {
        @Override
        public Severity getSeverity() {
            return Severity.WARN;
        }
    }
}

