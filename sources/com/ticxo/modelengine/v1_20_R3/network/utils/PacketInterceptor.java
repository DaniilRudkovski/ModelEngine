/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  lombok.Generated
 *  net.minecraft.network.protocol.Packet
 */
package com.ticxo.modelengine.v1_20_R3.network.utils;

import com.google.common.collect.Maps;
import com.ticxo.modelengine.api.utils.logger.TLogger;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import lombok.Generated;
import net.minecraft.network.protocol.Packet;

public class PacketInterceptor {
    private final Map<Class<? extends Packet>, Modifier<? extends Packet>> registry = Maps.newConcurrentMap();
    private final Map<Class<? extends Packet>, Listener<? extends Packet>> postRegistry = Maps.newConcurrentMap();

    public <T extends Packet> PacketInterceptor register(Class<T> clazz, Function<T, Packet> function) {
        this.registry.put(clazz, new Modifier<T>(clazz, function));
        return this;
    }

    public <T extends Packet> PacketInterceptor registerPost(Class<T> clazz, Function<T, Collection<Packet>> consumer) {
        this.postRegistry.put(clazz, new Listener<T>(clazz, consumer));
        return this;
    }

    public Packet accept(Packet original) {
        if (original == null) {
            return null;
        }
        Modifier<? extends Packet> modifier = this.registry.get(original.getClass());
        return modifier == null ? original : modifier.modify(original);
    }

    public Collection<Packet> acceptPost(Packet original) {
        if (original == null) {
            return List.of();
        }
        Listener<? extends Packet> listener = this.postRegistry.get(original.getClass());
        if (listener != null) {
            return listener.listen(original);
        }
        return List.of();
    }

    static class Modifier<T extends Packet> {
        private final Class<T> clazz;
        private final Function<T, Packet> function;

        public Packet modify(Packet original) {
            try {
                return this.function.apply((Packet)this.clazz.cast(original));
            }
            catch (Throwable t) {
                TLogger.error("An error had occurred while modifying the packet " + this.clazz.getSimpleName());
                t.printStackTrace();
                return original;
            }
        }

        @Generated
        public Modifier(Class<T> clazz, Function<T, Packet> function) {
            this.clazz = clazz;
            this.function = function;
        }
    }

    static class Listener<T extends Packet> {
        private final Class<T> clazz;
        private final Function<T, Collection<Packet>> function;

        public Collection<Packet> listen(Packet original) {
            try {
                Collection<Packet> collection = this.function.apply((Packet)this.clazz.cast(original));
                return collection == null ? List.of() : collection;
            }
            catch (Throwable t) {
                TLogger.error("An error had occurred while intercepting the packet " + this.clazz.getSimpleName());
                t.printStackTrace();
                return List.of();
            }
        }

        @Generated
        public Listener(Class<T> clazz, Function<T, Collection<Packet>> function) {
            this.clazz = clazz;
            this.function = function;
        }
    }
}

