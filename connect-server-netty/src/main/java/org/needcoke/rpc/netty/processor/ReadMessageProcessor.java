package org.needcoke.rpc.netty.processor;

import io.netty.channel.ChannelHandlerContext;
import org.needcoke.rpc.codec.CokeRequest;

public interface ReadMessageProcessor {

    void channelRead(ChannelHandlerContext ctx, CokeRequest request);
}
