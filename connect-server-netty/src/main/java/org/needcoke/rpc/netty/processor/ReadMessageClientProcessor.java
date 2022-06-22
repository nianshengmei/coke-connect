package org.needcoke.rpc.netty.processor;

import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.connect.rpc.link.tracking.util.TrackingUtil;
import org.needcoke.rpc.codec.CokeRequest;
import org.needcoke.rpc.common.enums.ConnectRequestEnum;
import org.needcoke.rpc.utils.ConnectUtil;

import java.util.concurrent.locks.LockSupport;

@Slf4j
public class ReadMessageClientProcessor implements ReadMessageProcessor{
    @Override
    public void channelRead(ChannelHandlerContext ctx, CokeRequest request) {
        if (ConnectRequestEnum.INTERNAL_RESPONSE == request.getRequestType()) {
            log.info("smart socket client receive back linkTracking = {} , request json = {}",
                    TrackingUtil.linkTrackingJsonStr(), new String(request.toBytes()));
            ConnectUtil.putRequestMap(request.getCokeRequestId(), request.getResult());
            Thread thread = ConnectUtil.getFromThreadMap(request.getCokeRequestId());
            LockSupport.unpark(thread);
        }
    }
}
