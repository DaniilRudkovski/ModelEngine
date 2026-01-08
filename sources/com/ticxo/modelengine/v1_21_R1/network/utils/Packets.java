/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.protocol.Packet
 *  net.minecraft.network.protocol.game.ClientGamePacketListener
 *  org.jetbrains.annotations.NotNull
 */
package com.ticxo.modelengine.v1_21_R1.network.utils;

import com.ticxo.modelengine.api.utils.data.NullableHashSet;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.UUID;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import org.jetbrains.annotations.NotNull;

public class Packets
extends LinkedHashSet<PacketSupplier> {
    public Packets(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    public Packets(int initialCapacity) {
        super(initialCapacity);
    }

    public Packets() {
    }

    public Packets(@NotNull Collection<? extends PacketSupplier> c2) {
        super(c2);
    }

    @Override
    public boolean add(PacketSupplier supplier) {
        if (supplier == null) {
            return false;
        }
        return super.add(supplier);
    }

    @Override
    public boolean add(Packet<ClientGamePacketListener> packet) {
        if (packet == null) {
            return false;
        }
        return this.add((UUID player) -> packet);
    }

    public Collection<Packet<? super ClientGamePacketListener>> compile(UUID player) {
        NullableHashSet<Packet<? super ClientGamePacketListener>> set = new NullableHashSet<Packet<? super ClientGamePacketListener>>();
        for (PacketSupplier supplier : this) {
            set.add(supplier.supply(player));
        }
        return set;
    }

    @FunctionalInterface
    public static interface PacketSupplier {
        public Packet<ClientGamePacketListener> supply(UUID var1);
    }
}

