package org.needcoke.rpc.smartsocket.invoker;

import cn.hutool.core.date.DateUtil;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.connect.rpc.link.tracking.config.LinkTrackingContextHolder;
import org.connect.rpc.link.tracking.util.TrackingUtil;
import org.needcoke.rpc.Fuse;
import org.needcoke.rpc.codec.CokeRequest;
import org.needcoke.rpc.smartsocket.codec.CokeRequestProtocol;
import org.needcoke.rpc.common.constant.ConnectConstant;
import org.needcoke.rpc.common.enums.ConnectionExceptionEnum;
import org.needcoke.rpc.common.exception.CokeConnectException;
import org.needcoke.rpc.invoker.ConnectInvoker;
import org.needcoke.rpc.invoker.InvokeResult;
import org.needcoke.rpc.invoker.OkHttpsInvoker;
import org.needcoke.rpc.net.Connector;
import org.needcoke.rpc.smartsocket.processor.SmartSocketClientProcessor;
import org.needcoke.rpc.utils.ConnectUtil;
import org.smartboot.socket.transport.AioQuickClient;
import org.smartboot.socket.transport.AioSession;
import org.springframework.cloud.client.ServiceInstance;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.LockSupport;

@Slf4j
@NoArgsConstructor
public class SmartSocketInvoker extends ConnectInvoker {

    /**
     * 给AioQuickClient加个引用，防止垃圾回收。
     */
    private final Map<String, AioQuickClient> clientMap = new ConcurrentHashMap<>();

    private final Map<String, AioSession> sessionMap = new ConcurrentHashMap<>();

    @Override
    public InvokeResult execute(Connector connector, ServiceInstance instance, String beanName, String methodName, Map<String, Object> params) {
        InvokeResult res = runDefaultExecute(connector, instance, beanName, methodName, params);
        if(null != res){
            return res;
        }
        String uri = instance.getHost() + ConnectConstant.COLON + instance.getPort();
        Integer serverPort = connector.getServerPort(instance);
        if (0 == serverPort) {
            throw new CokeConnectException(ConnectionExceptionEnum.REMOTE_SERVICE_DOES_NOT_OPEN_THE_COKE_SERVICE_PORT);
        }
        if (!sessionMap.containsKey(uri)) {
            AioQuickClient aioQuickClient = new AioQuickClient(instance.getHost(), serverPort, new CokeRequestProtocol(), new SmartSocketClientProcessor());
            clientMap.put(uri, aioQuickClient);
            try {
                AioSession session = aioQuickClient.start();
                sessionMap.put(uri, session);
            } catch (IOException e) {
                throw new CokeConnectException(ConnectionExceptionEnum.CONNECTION_WITH_REMOTE_SERVICE_FAILED);
            }
        }
        AioSession session = sessionMap.get(uri);
        CokeRequest request = new CokeRequest().setBeanName(beanName)
                .setMethodName(methodName)
                .setParams(params)
                .addHeader(TrackingUtil.headerKey(), TrackingUtil.headerValue());
        byte[] bytes = request.toBytes();
        InvokeResult tmp = new InvokeResult();
        ConnectUtil.putRequestMap(tmp);
        try {
            session.writeBuffer().writeInt(bytes.length);
            session.writeBuffer().write(bytes);
            session.writeBuffer().flush();
        } catch (IOException e) {
            //一般是session失效了
            log.error(e.getMessage());
            try {
                session = clientMap.get(uri).start();
            } catch (IOException ex) {
                if (null == connector.getHttpInvoker()) {
                    connector.setHttpInvoker(new OkHttpsInvoker());
                }
                return connector.compensationExecute(instance,beanName,methodName,params);
            }
            sessionMap.put(uri,session);
            return execute(connector,instance,beanName,methodName,params);
        }
        Fuse fuse = new Fuse(fuseConfig.getFuseTimeOut(), TrackingUtil.getRequestId());
        fuseThreadPool.newTask(fuse);
        LockSupport.park();
        InvokeResult result = ConnectUtil.getFromRequestMap(TrackingUtil.getRequestId());
        long start = LinkTrackingContextHolder.getLinkTracking().getStartTime();
        long end = DateUtil.current();
        log.info("requestId = {} , start = {} , end = {} ,cost = {}", TrackingUtil.getRequestId(), start, end, end - start);
        result.setTime(end - start);
        return result;
    }
}
