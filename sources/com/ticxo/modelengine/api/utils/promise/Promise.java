/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.entity.Entity
 *  org.jetbrains.annotations.Nullable
 */
package com.ticxo.modelengine.api.utils.promise;

import com.ticxo.modelengine.api.ServerInfo;
import com.ticxo.modelengine.api.utils.promise.EntityPromise;
import com.ticxo.modelengine.api.utils.promise.GlobalPromise;
import com.ticxo.modelengine.api.utils.promise.LegacyPromise;
import com.ticxo.modelengine.api.utils.promise.RegionPromise;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.Nullable;

public interface Promise<T>
extends Future<T> {
    public static <U> Promise<U> empty() {
        return ServerInfo.IS_FOLIA ? GlobalPromise.empty() : LegacyPromise.empty();
    }

    public static <U> Promise<U> empty(Entity entity) {
        return ServerInfo.IS_FOLIA ? EntityPromise.empty(entity) : Promise.empty();
    }

    public static <U> Promise<U> empty(Location location) {
        return ServerInfo.IS_FOLIA ? RegionPromise.empty(location) : Promise.empty();
    }

    public static Promise<Void> start() {
        return ServerInfo.IS_FOLIA ? GlobalPromise.completed(null) : LegacyPromise.completed(null);
    }

    public static Promise<Void> start(Entity entity) {
        return ServerInfo.IS_FOLIA ? EntityPromise.completed(entity, null) : Promise.start();
    }

    public static Promise<Void> start(Location location) {
        return ServerInfo.IS_FOLIA ? RegionPromise.completed(location, null) : Promise.start();
    }

    public static <U> Promise<U> completed(@Nullable U value) {
        return ServerInfo.IS_FOLIA ? GlobalPromise.completed(value) : LegacyPromise.completed(value);
    }

    public static <U> Promise<U> completed(Entity entity, @Nullable U value) {
        return ServerInfo.IS_FOLIA ? EntityPromise.completed(entity, value) : Promise.completed(value);
    }

    public static <U> Promise<U> completed(Location location, @Nullable U value) {
        return ServerInfo.IS_FOLIA ? RegionPromise.completed(location, value) : Promise.completed(value);
    }

    public static <U> Promise<U> supplyingSync(Supplier<U> supplier) {
        Promise<U> promise = Promise.empty();
        return promise.supplySync(supplier);
    }

    public static <U> Promise<U> supplyingSync(Entity entity, Supplier<U> supplier) {
        Promise<U> promise = Promise.empty(entity);
        return promise.supplySync(supplier);
    }

    public static <U> Promise<U> supplyingSync(Location location, Supplier<U> supplier) {
        Promise<U> promise = Promise.empty(location);
        return promise.supplySync(supplier);
    }

    public static <U> Promise<U> supplyingSyncDelay(Supplier<U> supplier, int delay) {
        Promise<U> promise = Promise.empty();
        return promise.supplySyncDelay(supplier, delay);
    }

    public static <U> Promise<U> supplyingSyncDelay(Entity entity, Supplier<U> supplier, int delay) {
        Promise<U> promise = Promise.empty(entity);
        return promise.supplySyncDelay(supplier, delay);
    }

    public static <U> Promise<U> supplyingSyncDelay(Location location, Supplier<U> supplier, int delay) {
        Promise<U> promise = Promise.empty(location);
        return promise.supplySyncDelay(supplier, delay);
    }

    public static <U> Promise<U> supplyingAsync(Supplier<U> supplier) {
        Promise<U> promise = Promise.empty();
        return promise.supplyAsync(supplier);
    }

    public static <U> Promise<U> supplyingAsyncDelay(Supplier<U> supplier, int delay) {
        Promise<U> promise = Promise.empty();
        return promise.supplyAsyncDelay(supplier, delay);
    }

    public Promise<T> runSync(Runnable var1);

    public Promise<T> runSyncDelay(Runnable var1, int var2);

    public Promise<T> runAsync(Runnable var1);

    public Promise<T> runAsyncDelay(Runnable var1, int var2);

    public Promise<T> supplySync(Supplier<T> var1);

    public Promise<T> supplySyncDelay(Supplier<T> var1, int var2);

    public Promise<T> supplyAsync(Supplier<T> var1);

    public Promise<T> supplyAsyncDelay(Supplier<T> var1, int var2);

    default public Promise<Void> thenRunSync(Runnable runnable) {
        return this.thenApplySync(t -> {
            runnable.run();
            return null;
        });
    }

    default public Promise<Void> thenRunSyncDelay(Runnable runnable, int delay) {
        return this.thenApplySyncDelay(t -> {
            runnable.run();
            return null;
        }, delay);
    }

    default public Promise<Void> thenRunAsync(Runnable runnable) {
        return this.thenApplyAsync(t -> {
            runnable.run();
            return null;
        });
    }

    default public Promise<Void> thenRunAsyncDelay(Runnable runnable, int delay) {
        return this.thenApplyAsyncDelay(t -> {
            runnable.run();
            return null;
        }, delay);
    }

    public <U> Promise<U> thenApplySync(Function<? super T, ? extends U> var1);

    public <U> Promise<U> thenApplySyncDelay(Function<? super T, ? extends U> var1, int var2);

    public <U> Promise<U> thenApplyAsync(Function<? super T, ? extends U> var1);

    public <U> Promise<U> thenApplyAsyncDelay(Function<? super T, ? extends U> var1, int var2);

    default public Promise<Void> thenAcceptSync(Consumer<? super T> consumer) {
        return this.thenApplySync(t -> {
            consumer.accept(t);
            return null;
        });
    }

    default public Promise<Void> thenAcceptSyncDelay(Consumer<? super T> consumer, int delay) {
        return this.thenApplySyncDelay(t -> {
            consumer.accept(t);
            return null;
        }, delay);
    }

    default public Promise<Void> thenAcceptAsync(Consumer<? super T> consumer) {
        return this.thenApplyAsync(t -> {
            consumer.accept(t);
            return null;
        });
    }

    default public Promise<Void> thenAcceptAsyncDelay(Consumer<? super T> consumer, int delay) {
        return this.thenApplyAsyncDelay(t -> {
            consumer.accept(t);
            return null;
        }, delay);
    }
}

