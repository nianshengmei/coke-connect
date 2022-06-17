package org.needcoke.rpc.invoker;

import cn.hutool.core.date.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.needcoke.rpc.codec.CokeRequest;
import org.needcoke.rpc.codec.CokeRequestProtocol;
import org.needcoke.rpc.common.constant.ConnectConstant;
import org.needcoke.rpc.common.enums.ConnectionExceptionEnum;
import org.needcoke.rpc.common.enums.RpcTypeEnum;
import org.needcoke.rpc.common.exception.CokeConnectException;
import org.needcoke.rpc.config.RequestIdContextHolder;
import org.needcoke.rpc.net.Connector;
import org.needcoke.rpc.net.ConnectorFactory;
import org.needcoke.rpc.processor.smart_socket.SmartSocketClientProcessor;
import org.needcoke.rpc.utils.ConnectUtil;
import org.needcoke.rpc.utils.SpringContextUtils;
import org.smartboot.socket.transport.AioQuickClient;
import org.smartboot.socket.transport.AioSession;
import org.springframework.cloud.client.ServiceInstance;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.LockSupport;

@Slf4j
public class SmartSocketInvoker extends ConnectInvoker {

    private RpcTypeEnum rpcTypeEnum;

    public SmartSocketInvoker(RpcTypeEnum rpcTypeEnum) {
        this.rpcTypeEnum = rpcTypeEnum;
    }

    /**
     * 给AioQuickClient加个引用，防止垃圾回收。
     */
    private final Map<String, AioQuickClient> clientMap = new ConcurrentHashMap<>();

    private final Map<String, AioSession> sessionMap = new ConcurrentHashMap<>();

    @Override
    public InvokeResult execute(Connector connector, ServiceInstance instance, String beanName, String methodName, Map<String, Object> params) {
        RpcTypeEnum remoteRpcType = getRemoteRpcType(instance);
        if(remoteRpcType == RpcTypeEnum.okHttp3){
            if (null == connector.getHttpInvoker()) {
                connector.setHttpInvoker(new OkHttpsInvoker(RpcTypeEnum.okHttp3));
            }
            return connector.compensationExecute(instance,beanName,methodName,params);
        }

        String uri = instance.getHost() + ConnectConstant.COLON + instance.getPort();
        Integer serverPort = ConnectUtil.getCokeServerPort(instance);
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
        int requestId = ConnectUtil.requestIdMaker.addAndGet(1);
        String COKE_REQUEST_ID_HEADER_ID_NAME = "COKE_REQUEST_ID";
        CokeRequest request = new CokeRequest().setBeanName(beanName)
                .setMethodName(methodName)
                .setParams(params)
                .addHeader(COKE_REQUEST_ID_HEADER_ID_NAME, RequestIdContextHolder.getRequestId());
        byte[] bytes = request.toBytes();
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
                    connector.setHttpInvoker(new OkHttpsInvoker(RpcTypeEnum.okHttp3));
                }
                return connector.compensationExecute(instance,beanName,methodName,params);
            }
            sessionMap.put(uri,session);
            return execute(connector,instance,beanName,methodName,params);
        }
        InvokeResult tmp = new InvokeResult();
        long start = DateUtil.current();
        ConnectUtil.putRequestMap(tmp);
        ConnectUtil.threadMap.put(requestId, Thread.currentThread());
        LockSupport.park();
        InvokeResult result = ConnectUtil.getFromRequestMap(requestId);
        long end = DateUtil.current();
        log.info("requestId = {} , start = {} , end = {} ,cost = {}", requestId, start, end, end - start);
        result.setTime(end - start);
        return result;
    }
}
