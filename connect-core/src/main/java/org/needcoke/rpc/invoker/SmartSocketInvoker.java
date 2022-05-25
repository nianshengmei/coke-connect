package org.needcoke.rpc.invoker;

import lombok.extern.slf4j.Slf4j;
import org.needcoke.rpc.codec.CokeRequest;
import org.needcoke.rpc.codec.CokeRequestProtocol;
import org.needcoke.rpc.common.constant.ConnectConstant;
import org.needcoke.rpc.processor.smart_socket.SmartSocketServerProcessor;
import org.needcoke.rpc.utils.ConnectUtil;
import org.smartboot.socket.transport.AioQuickClient;
import org.smartboot.socket.transport.AioSession;
import org.smartboot.socket.transport.WriteBuffer;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.web.context.request.async.DeferredResult;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
        if(0 == serverPort){
            throw new RuntimeException("对方服务未开起server!");
            //TODO 异常统一
        }
        if (!sessionMap.containsKey(uri)) {
            AioQuickClient aioQuickClient = new AioQuickClient(instance.getHost(), serverPort, new CokeRequestProtocol(), new SmartSocketServerProcessor());
            clientMap.put(uri, aioQuickClient);
//            try {
//                AioSession session = aioQuickClient.start();
//                sessionMap.put(uri, session);
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
        }
        AioSession session = null;
        try {
            session = clientMap.get(uri).start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
        }finally {
            session.close();
        }
        DeferredResult deferredResult = new DeferredResult(3000L);
        ConnectUtil.putRequestMap(deferredResult);
        return InvokeResult.nullResult().setBody(deferredResult);
    }
}
