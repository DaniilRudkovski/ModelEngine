/*
 * Decompiled with CFR 0.152.
 */
package com.ticxo.modelengine.api.utils.data;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class QueuedAtomic<T> {
    private final AtomicReference<T> writer = new AtomicReference();
    private final AtomicReference<T> reader = new AtomicReference();
    private final BiConsumer<T, AtomicReference<T>> setter;
    private final Function<AtomicReference<T>, T> getter;
    private final BiConsumer<AtomicReference<T>, AtomicReference<T>> passer;

    public QueuedAtomic(Supplier<T> value, BiConsumer<T, AtomicReference<T>> setter, Function<AtomicReference<T>, T> getter, BiConsumer<AtomicReference<T>, AtomicReference<T>> passer) {
        this.writer.set(value.get());
        this.reader.set(value.get());
        this.setter = setter;
        this.getter = getter;
        this.passer = passer;
    }

    public void set(T value) {
        this.setter.accept(value, this.writer);
    }

    public void setUnsafe(T value) {
        this.setter.accept(value, this.reader);
    }

    public void pass() {
        this.passer.accept(this.writer, this.reader);
    }

    public T get() {
        return this.getter.apply(this.reader);
    }

    public T getUnsafe() {
        return this.getter.apply(this.writer);
    }

    public static <T> Builder<T> builder() {
        return new Builder();
    }

    public static class Builder<T> {
        private Supplier<T> value = () -> null;
        private BiConsumer<T, AtomicReference<T>> setter = (val, reference) -> reference.set(val);
        private Function<AtomicReference<T>, T> getter = AtomicReference::get;
        private BiConsumer<AtomicReference<T>, AtomicReference<T>> passer = (a, b2) -> b2.set(a.get());

        public Builder<T> value(Supplier<T> value) {
            this.value = value;
            return this;
        }

        public Builder<T> setter(BiConsumer<T, AtomicReference<T>> setter) {
            this.setter = setter;
            return this;
        }

        public Builder<T> getter(Function<AtomicReference<T>, T> getter) {
            this.getter = getter;
            return this;
        }

        public Builder<T> passer(BiConsumer<AtomicReference<T>, AtomicReference<T>> passer) {
            this.passer = passer;
            return this;
        }

        public QueuedAtomic<T> build() {
            return new QueuedAtomic<T>(this.value, this.setter, this.getter, this.passer);
        }
    }
}

