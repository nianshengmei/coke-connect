package org.needcoke.rpc.smartsocket.processor;

import lombok.extern.slf4j.Slf4j;
import org.connect.rpc.link.tracking.util.TrackingUtil;
import org.needcoke.rpc.Fuse;
import org.needcoke.rpc.codec.CokeRequest;
import org.needcoke.rpc.common.enums.ConnectRequestEnum;
import org.needcoke.rpc.utils.ConnectUtil;
import org.smartboot.socket.transport.AioSession;

@Slf4j
public class SmartSocketClientProcessor extends SmartSocketMessageProcessor<CokeRequest> {
    @Override
    public void process(AioSession session, CokeRequest request) {

        if (ConnectRequestEnum.INTERNAL_RESPONSE == request.getRequestType()) {
            log.debug("smart socket client receive back linkTracking = {} , request json = {}",
                    TrackingUtil.linkTrackingJsonStr(), new String(request.toBytes()));
            boolean bool = Fuse.unPark(request.getCokeRequestId());
            if(bool) {
                ConnectUtil.putRequestMap(request.getCokeRequestId(), request.getResult());
            }
        }

    }
}
