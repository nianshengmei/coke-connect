package org.needcoke.rpc.invoker;

import org.needcoke.rpc.codec.CokeRequest;
import org.needcoke.rpc.codec.CokeRequestProtocol;
import org.needcoke.rpc.common.constant.ConnectConstant;
import org.needcoke.rpc.processor.SmartSocketServerProcessor;
import org.smartboot.socket.transport.AioQuickClient;
import org.smartboot.socket.transport.AioSession;
import org.springframework.cloud.client.ServiceInstance;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SmartSocketInvoker extends ConnectInvoker{

    private final Map<String, AioQuickClient> clientMap = new HashMap<>();

    private final Map<String, AioSession> sessionMap = new HashMap<>();
    @Override
    public InvokeResult execute(ServiceInstance instance, String beanName, String methodName, Map<String, Object> params) {
        String uri = instance.getHost()+ ConnectConstant.COLON+ instance.getPort();
        if (!sessionMap.containsKey(uri)) {
            AioQuickClient aioQuickClient = new AioQuickClient(instance.getHost(),instance.getPort(), new CokeRequestProtocol(),new SmartSocketServerProcessor());
            clientMap.put(uri,aioQuickClient);
            try {
                AioSession session = aioQuickClient.start();
                sessionMap.put(uri,session);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
        AioSession session = sessionMap.get(uri);
        CokeRequest request = new CokeRequest().setBeanName(beanName)
                .setMethodName(methodName)
                .setParams(params);
        byte[] bytes = request.getBytes();
        try {
            session.writeBuffer().writeInt(bytes.length);
            session.writeBuffer().write(bytes);
            session.writeBuffer().flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
