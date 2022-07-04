package org.needcoke.rpc.netty.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.needcoke.rpc.codec.CokeRequest;
import org.needcoke.rpc.netty.processor.ReadMessageClientProcessor;

public class SenderHandlerAdapter  extends SimpleChannelInboundHandler<CokeRequest> {

    private final ReadMessageClientProcessor clientProcessor;

    public SenderHandlerAdapter(){
        this.clientProcessor = new ReadMessageClientProcessor();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, CokeRequest request) throws Exception {
        clientProcessor.channelRead(ctx,request);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        super.userEventTriggered(ctx, evt);
    }
}
