package org.needcoke.rpc.invoker;

import cn.hutool.core.date.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.needcoke.rpc.codec.CokeRequest;
import org.needcoke.rpc.codec.CokeRequestProtocol;
import org.needcoke.rpc.common.constant.ConnectConstant;
import org.needcoke.rpc.processor.smart_socket.SmartSocketClientProcessor;
import org.needcoke.rpc.utils.ConnectUtil;
import org.smartboot.socket.transport.AioQuickClient;
import org.smartboot.socket.transport.AioSession;
import org.springframework.cloud.client.ServiceInstance;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.LockSupport;

@Slf4j
public class SmartSocketInvoker extends ConnectInvoker {

    /**
     * 给AioQuickClient加个引用，防止垃圾回收。
     * //TODO AioSession失效时重建连接
     */
    private final Map<String, AioQuickClient> clientMap = new ConcurrentHashMap<>();

    private final Map<String, AioSession> sessionMap = new ConcurrentHashMap<>();

    @Override
    public InvokeResult execute(ServiceInstance instance, String beanName, String methodName, Map<String, Object> params) {
        String uri = instance.getHost() + ConnectConstant.COLON + instance.getPort();
        Integer serverPort = ConnectUtil.getCokeServerPort(instance);
        if (0 == serverPort) {
            throw new RuntimeException("对方服务未开起server!");
            //TODO 异常统一
        }
        if (!sessionMap.containsKey(uri)) {
            AioQuickClient aioQuickClient = new AioQuickClient(instance.getHost(), serverPort, new CokeRequestProtocol(), new SmartSocketClientProcessor());
            clientMap.put(uri, aioQuickClient);
            try {
                AioSession session = aioQuickClient.start();
                sessionMap.put(uri, session);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        AioSession session = sessionMap.get(uri);
        int requestId = ConnectUtil.requestIdMaker.addAndGet(1);
        CokeRequest request = new CokeRequest().setBeanName(beanName)
                .setMethodName(methodName)
                .setParams(params)
                .setRequestId(requestId);
        byte[] bytes = request.toBytes();
        try {
            session.writeBuffer().writeInt(bytes.length);
            session.writeBuffer().write(bytes);
            session.writeBuffer().flush();
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
        InvokeResult tmp = new InvokeResult();
        long start = DateUtil.current();
        ConnectUtil.putRequestMap(requestId, tmp);
        ConnectUtil.threadMap.put(requestId, Thread.currentThread());
        LockSupport.park();
        InvokeResult result = ConnectUtil.getFromRequestMap(requestId);
        long end = DateUtil.current();
        log.info("requestId = {} , start = {} , end = {} ,cost = {}", requestId, start, end, end - start);
        result.setTime(end - start);
        return result;
    }
}
