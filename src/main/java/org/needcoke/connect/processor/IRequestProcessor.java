package org.needcoke.connect.processor;

import io.netty.channel.ChannelHandlerContext;
import org.needcoke.connect.protocol.CokeRequest;

/* 请求拦截器 */
public interface IRequestProcessor {

    void handle(ChannelHandlerContext ctx, CokeRequest cokeRequest);
}
