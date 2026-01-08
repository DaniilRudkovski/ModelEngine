/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  lombok.Generated
 *  net.minecraft.network.PacketListener
 *  net.minecraft.network.protocol.Packet
 */
package com.ticxo.modelengine.v1_21_R6.network.utils;

import com.google.common.collect.Maps;
import com.ticxo.modelengine.api.utils.logger.TLogger;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import lombok.Generated;
import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.Packet;

public class PacketInterceptor<P extends PacketListener> {
    private final Map<Class<? extends Packet<? super P>>, Modifier<? extends Packet<? super P>>> registry = Maps.newConcurrentMap();
    private final Map<Class<? extends Packet<? super P>>, Listener<? extends Packet<? super P>>> postRegistry = Maps.newConcurrentMap();

    public <T extends Packet<? super P>> PacketInterceptor<P> register(Class<T> clazz, Function<T, Packet<? super P>> function) {
        this.registry.put(clazz, new Modifier<T>(this, clazz, function));
        return this;
    }

    public <T extends Packet<? super P>> PacketInterceptor<P> registerPost(Class<T> clazz, Function<T, Collection<Packet<? super P>>> consumer) {
        this.postRegistry.put(clazz, new Listener<T>(this, clazz, consumer));
        return this;
    }

    public Packet<? super P> accept(Packet<? super P> original) {
        if (original == null) {
            return null;
        }
        Modifier<Packet<? super P>> modifier = this.registry.get(original.getClass());
        return modifier == null ? original : modifier.modify(original);
    }

    public Collection<Packet<? super P>> acceptPost(Packet<? super P> original) {
        if (original == null) {
            return List.of();
        }
        Listener<Packet<P>> listener = this.postRegistry.get(original.getClass());
        if (listener != null) {
            return listener.listen(original);
        }
        return List.of();
    }

    class Modifier<T extends Packet<? super P>> {
        private final Class<T> clazz;
        private final Function<T, Packet<? super P>> function;

        public Packet<? super P> modify(Packet<? super P> original) {
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
        public Modifier(PacketInterceptor this$0, Class<T> clazz, Function<T, Packet<? super P>> function) {
            this.clazz = clazz;
            this.function = function;
        }
    }

    class Listener<T extends Packet<? super P>> {
        private final Class<T> clazz;
        private final Function<T, Collection<Packet<? super P>>> function;

        public Collection<Packet<? super P>> listen(Packet<?> original) {
            try {
                Collection collection = this.function.apply((Packet)this.clazz.cast(original));
                return collection == null ? List.of() : collection;
            }
            catch (Throwable t) {
                TLogger.error("An error had occurred while intercepting the packet " + this.clazz.getSimpleName());
                t.printStackTrace();
                return List.of();
            }
        }

        @Generated
        public Listener(PacketInterceptor this$0, Class<T> clazz, Function<T, Collection<Packet<? super P>>> function) {
            this.clazz = clazz;
            this.function = function;
        }
    }
}

