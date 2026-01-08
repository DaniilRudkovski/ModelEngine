/*
 * Decompiled with CFR 0.152.
 */
package com.ticxo.modelengine.api.animation.keyframe.data;

import com.ticxo.modelengine.api.animation.keyframe.data.DoubleData;
import com.ticxo.modelengine.api.animation.keyframe.data.IKeyframeData;
import com.ticxo.modelengine.api.utils.logger.TLogger;
import com.ticxo.modelengine.api.utils.registry.TUnaryRegistry;
import java.util.function.Function;

public class KeyframeReaderRegistry
extends TUnaryRegistry<Function<String, IKeyframeData>> {
    public IKeyframeData tryParse(String data) {
        if (data == null) {
            return IKeyframeData.EMPTY;
        }
        if ((data = data.trim()).isEmpty()) {
            return IKeyframeData.EMPTY;
        }
        try {
            return new DoubleData(Double.parseDouble(data));
        }
        catch (NumberFormatException numberFormatException) {
            String[] value = data.split(":", 2);
            if (value.length == 1) {
                for (Function func : this.registry.values()) {
                    try {
                        return (IKeyframeData)func.apply(value[0]);
                    }
                    catch (Throwable throwable) {
                    }
                }
            } else {
                Function func = (Function)this.get(value[0]);
                if (func != null) {
                    try {
                        return (IKeyframeData)func.apply(value[1]);
                    }
                    catch (Throwable e) {
                        TLogger.error(2, "------An error occurred while parsing the keyframe: " + data);
                        e.printStackTrace();
                    }
                } else {
                    for (Function func2 : this.registry.values()) {
                        try {
                            return (IKeyframeData)func2.apply(data);
                        }
                        catch (Throwable throwable) {
                        }
                    }
                }
            }
            TLogger.warn(2, "------Unknown keyframe data: " + data);
            return IKeyframeData.EMPTY;
        }
    }
}

