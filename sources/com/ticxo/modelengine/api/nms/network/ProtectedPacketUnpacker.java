/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.channel.ChannelDuplexHandler
 *  io.netty.channel.ChannelHandlerContext
 *  io.netty.channel.ChannelPromise
 */
package com.ticxo.modelengine.api.nms.network;

import com.ticxo.modelengine.api.nms.network.ProtectedPacket;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;

public class ProtectedPacketUnpacker
extends ChannelDuplexHandler {
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (msg instanceof ProtectedPacket) {
            ProtectedPacket protectedPacket = (ProtectedPacket)msg;
            msg = protectedPacket.packet();
        }
        super.write(ctx, msg, promise);
    }
}

