package org.needcoke.rpc.netty.server;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.connect.rpc.link.tracking.common.CommonConstant;
import org.connect.rpc.link.tracking.config.LinkTrackingContextHolder;
import org.connect.rpc.link.tracking.net.LinkTracking;
import org.needcoke.rpc.codec.CokeRequest;
import org.needcoke.rpc.config.ServerConfig;
import org.needcoke.rpc.netty.processor.ReadMessageServerProcessor;
import org.needcoke.rpc.utils.SpringContextUtils;

public class NettyServerHandlerAdapter extends ChannelInboundHandlerAdapter {

    private final ReadMessageServerProcessor readMessageProcessor;

    public NettyServerHandlerAdapter() {
        this.readMessageProcessor = new ReadMessageServerProcessor();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        CokeRequest request = (CokeRequest)msg;
        String json = request.getHeader(CommonConstant.COKE_REQUEST_ID_HEADER_ID_NAME);
        int mvcPort = SpringContextUtils.getBean(ServerConfig.class).getMvcPort();
        if (StrUtil.isEmpty(json)) {
            LinkTracking linkTracking = new LinkTracking(mvcPort);
            linkTracking.setIndex(1);
            LinkTrackingContextHolder.setLinkTracking(linkTracking);
        } else {
            LinkTracking linkTracking = JSONUtil.toBean(json, LinkTracking.class);
            linkTracking.setIndex(linkTracking.getIndex() + 1);
            linkTracking.changeIp();
            linkTracking.setPort(mvcPort);
            LinkTrackingContextHolder.setLinkTracking(linkTracking);
        }
        readMessageProcessor.channelRead(ctx,request);
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
    }


}
