package org.needcoke.rpc.net;

import cn.hutool.core.collection.CollUtil;
import org.needcoke.rpc.common.enums.ConnectionExceptionEnum;
import org.needcoke.rpc.common.enums.RpcTypeEnum;
import org.needcoke.rpc.common.exception.CokeConnectException;
import org.needcoke.rpc.invoker.ConnectInvoker;
import org.needcoke.rpc.invoker.InvokeResult;
import org.needcoke.rpc.loadBalance.LoadBalance;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Connector {

    private final String serviceId;

    private final DiscoveryClient discoveryClient;

    private final ConnectInvoker invoker;

    private final LoadBalance loadBalance;

    private ConnectInvoker httpInvoker;

    private final Map<ServiceInstance, RpcTypeEnum> requestTypeMap;

    public RpcTypeEnum getRpcType(ServiceInstance instance){
        return requestTypeMap.get(instance);
    }

    private final Map<ServiceInstance ,Integer> severPortMap;

    public Integer getServerPort(ServiceInstance instance){
        return severPortMap.get(instance);
    }

    public Connector(String serviceId, DiscoveryClient discoveryClient, ConnectInvoker invoker, LoadBalance loadBalance) {
        this.serviceId = serviceId;
        this.discoveryClient = discoveryClient;
        this.invoker = invoker;
        this.loadBalance = loadBalance;
        this.requestTypeMap = new HashMap<>();
        this.severPortMap = new HashMap<>();
    }

    public Map<ServiceInstance, RpcTypeEnum> getRequestTypeMap() {
        return requestTypeMap;
    }

    /**
     * 执行远程方法
     *
     * @param beanName   远程服务上的 bean的名称
     * @param methodName 方法名称 或 @Call value
     * @return 返回远程方法执行结果的json
     */
    public InvokeResult execute(String beanName,
                                String methodName,
                                Map<String, Object> params) {
        List<ServiceInstance> instances = discoveryClient.getInstances(serviceId);
        ServiceInstance instance = loadBalance.choose(serviceId, instances);
        if (!requestTypeMap.containsKey(instance)) {
            Map<String, String> metadata = instance.getMetadata();
            if (CollUtil.isNotEmpty(metadata)) {
                String rpcType = metadata.get("rpcType");
                String cokeServerPort = metadata.get("coke-server-port");
                if (null != rpcType) {
                    RpcTypeEnum rp = RpcTypeEnum.okHttp3;
                    switch (rpcType) {
                        case "netty":
                            rp = RpcTypeEnum.netty;
                            break;
                        case "smart socket":
                        case "smartSocket":

                        case "smart-socket":
                            rp = RpcTypeEnum.smartSocket;
                            break;

                        case "okHttp3":
                            rp = RpcTypeEnum.okHttp3;
                            break;

                        default:
                            rp = RpcTypeEnum.okHttp3;
                    }
                    this.requestTypeMap.put(instance,rp);
                    try {
                        int port = Integer.parseInt(cokeServerPort);
                        this.severPortMap.put(instance,port);
                    }catch (Exception e){
                        throw new CokeConnectException(ConnectionExceptionEnum.THE_FORMAT_OF_THE_REMOTE_SERVICE_PORT_NUMBER_IS_INCORRECT_PLEASE_CHECK_THE_CONFIGURATION_OF_THE_REMOTE_SERVICE_PORT_NUMBER);
                    }
                }
            }
        }
        InvokeResult result = invoker.execute(this, instance, beanName, methodName, params);
        return result;
    }

    public ConnectInvoker getHttpInvoker() {
        return httpInvoker;
    }

    public void setHttpInvoker(ConnectInvoker httpInvoker) {
        this.httpInvoker = httpInvoker;
    }

    public InvokeResult compensationExecute(ServiceInstance instance,
                                            String beanName,
                                            String methodName,
                                            Map<String, Object> params) {
        return this.httpInvoker.execute(this, instance, beanName, methodName, params);
    }
}
