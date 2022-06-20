package org.needcoke.rpc.processor.smart_socket;

import lombok.extern.slf4j.Slf4j;
import org.connect.rpc.link.tracking.util.TrackingUtil;
import org.needcoke.rpc.codec.CokeRequest;
import org.needcoke.rpc.common.enums.ConnectRequestEnum;
import org.needcoke.rpc.utils.ConnectUtil;
import org.smartboot.socket.transport.AioSession;

import java.util.concurrent.locks.LockSupport;

@Slf4j
public class SmartSocketClientProcessor extends SmartSocketMessageProcessor<CokeRequest> {
    @Override
    public void process(AioSession aioSession, CokeRequest request) {

        if (ConnectRequestEnum.INTERNAL_RESPONSE == request.getRequestType()) {
            log.info("smart socket client receive back linkTracking = {} , request json = {}",
                    TrackingUtil.linkTrackingJsonStr(),new String(request.toBytes()));
            ConnectUtil.putRequestMap(TrackingUtil.getRequestId(),request.getResult());
            Thread thread = ConnectUtil.threadMap.get(TrackingUtil.getRequestId());
            LockSupport.unpark(thread);
            //TODO 抛出异常
        }

    }
}
