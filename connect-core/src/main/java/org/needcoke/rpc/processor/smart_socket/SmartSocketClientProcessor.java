package org.needcoke.rpc.processor.smart_socket;

import lombok.extern.slf4j.Slf4j;
import org.needcoke.rpc.codec.CokeRequest;
import org.needcoke.rpc.common.enums.ConnectRequestEnum;
import org.needcoke.rpc.config.RequestIdContextHolder;
import org.needcoke.rpc.utils.ConnectUtil;
import org.smartboot.socket.transport.AioSession;

import java.util.concurrent.locks.LockSupport;

@Slf4j
public class SmartSocketClientProcessor extends SmartSocketMessageProcessor<CokeRequest> {
    @Override
    public void process(AioSession aioSession, CokeRequest request) {

        if (ConnectRequestEnum.INTERNAL_RESPONSE == request.getRequestType()) {
            log.info("smart socket client receive back requestId = {} , request json = {}",
                    RequestIdContextHolder.getRequestId(),new String(request.toBytes()));
            ConnectUtil.putRequestMap(RequestIdContextHolder.getRequestId(),request.getResult());
            Thread thread = ConnectUtil.threadMap.get(RequestIdContextHolder.getRequestId());
            LockSupport.unpark(thread);
            //TODO 抛出异常
        }

    }
}
