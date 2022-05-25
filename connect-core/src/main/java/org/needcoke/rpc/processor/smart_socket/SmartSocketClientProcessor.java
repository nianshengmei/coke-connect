package org.needcoke.rpc.processor.smart_socket;

import org.needcoke.rpc.codec.CokeRequest;
import org.needcoke.rpc.common.enums.ConnectRequestEnum;
import org.needcoke.rpc.utils.ConnectUtil;
import org.smartboot.socket.transport.AioSession;
import org.springframework.web.context.request.async.DeferredResult;

public class SmartSocketClientProcessor extends SmartSocketMessageProcessor<CokeRequest> {
    @Override
    public void process(AioSession aioSession, CokeRequest request) {

        if (ConnectRequestEnum.INTERNAL_REQUEST == request.getRequestType()) {
            Integer requestId = request.getRequestId();
            DeferredResult deferredResult = ConnectUtil.getFromRequestMap(requestId);
            deferredResult.setResult(request.getResult());
            //TODO 抛出异常
        }

    }
}
