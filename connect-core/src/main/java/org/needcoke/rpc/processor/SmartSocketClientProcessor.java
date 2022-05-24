package org.needcoke.rpc.processor;

import org.needcoke.rpc.codec.CokeRequest;
import org.needcoke.rpc.common.enums.ConnectRequestEnum;
import org.smartboot.socket.transport.AioSession;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import javax.servlet.http.HttpServletResponse;

public class SmartSocketClientProcessor extends SmartSocketMessageProcessor<CokeRequest> {
    @Override
    public void process(AioSession aioSession, CokeRequest request) {

        if (ConnectRequestEnum.INTERNAL_REQUEST == request.getRequestType()) {
            RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
            HttpServletResponse response = ((ServletRequestAttributes) requestAttributes).getResponse();
            if (null != response){
                this.responseHttp(response,request.getResult());
            }
            //TODO 抛出异常
        }

    }
}
