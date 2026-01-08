/*
 * Decompiled with CFR 0.152.
 */
package com.ticxo.modelengine.api.utils.ticker;

import java.util.function.BiConsumer;
import java.util.function.Function;

public interface LoadBalancer<KEY, SER extends Server> {
    public SER getOrRegister(KEY var1);

    public SER get(KEY var1);

    public void unregister(KEY var1);

    public void execute(KEY var1, BiConsumer<KEY, SER> var2);

    public SER supply();

    default public <VAL> VAL map(KEY key, Function<SER, VAL> mapper) {
        return mapper.apply(this.getOrRegister(key));
    }

    public static interface Server {
        public int getLoad();
    }
}

