package org.needcoke.rpc.netty.invoker;

import cn.hutool.core.date.DateUtil;
import io.netty.channel.Channel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.connect.rpc.link.tracking.util.TrackingUtil;
import org.needcoke.rpc.codec.CokeRequest;
import org.needcoke.rpc.common.constant.ConnectConstant;
import org.needcoke.rpc.common.enums.ConnectionExceptionEnum;
import org.needcoke.rpc.common.enums.RpcTypeEnum;
import org.needcoke.rpc.common.exception.CokeConnectException;
import org.needcoke.rpc.invoker.ConnectInvoker;
import org.needcoke.rpc.invoker.InvokeResult;
import org.needcoke.rpc.invoker.OkHttpsInvoker;
import org.needcoke.rpc.net.Connector;
import org.needcoke.rpc.netty.client.NettyClient;
import org.needcoke.rpc.utils.ConnectUtil;
import org.springframework.cloud.client.ServiceInstance;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.LockSupport;

@Slf4j
@NoArgsConstructor
public class NettyInvoker extends ConnectInvoker {

    private final Map<String, NettyClient> clientMap = new ConcurrentHashMap<>();

    private final Map<String, Channel> channelMap = new ConcurrentHashMap<>();
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
        if (!channelMap.containsKey(uri)) {
            NettyClient nettyClient = new NettyClient(instance.getHost(), serverPort);
            clientMap.put(uri, nettyClient);
            try {
                Channel channel = nettyClient.start();
                channelMap.put(uri, channel);
            }  catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        Channel channel = channelMap.get(uri);
        CokeRequest request = new CokeRequest().setBeanName(beanName)
                .setMethodName(methodName)
                .setParams(params)
                .addHeader(TrackingUtil.headerKey(), TrackingUtil.headerValue());
        byte[] bytes = request.toBytes();
        try {
            channel.writeAndFlush(request);
        } catch (Exception e) {
            //一般是channel close失效了
            log.error(e.getMessage());
            try {
                channel = clientMap.get(uri).start();
            } catch (InterruptedException ex) {
                if (null == connector.getHttpInvoker()) {
                    connector.setHttpInvoker(new OkHttpsInvoker());
                }
                return connector.compensationExecute(instance,beanName,methodName,params);
            }
            channelMap.put(uri,channel);
            return execute(connector,instance,beanName,methodName,params);
        }
        InvokeResult tmp = new InvokeResult();
        long start = DateUtil.current();
        ConnectUtil.putRequestMap(tmp);
        ConnectUtil.putThreadMap(TrackingUtil.getRequestId(), Thread.currentThread());
        LockSupport.park();
        InvokeResult result = ConnectUtil.getFromRequestMap(TrackingUtil.getRequestId());
        long end = DateUtil.current();
        log.info("requestId = {} , start = {} , end = {} ,cost = {}", TrackingUtil.getRequestId(), start, end, end - start);
        result.setTime(end - start);
        return result;
    }
}
