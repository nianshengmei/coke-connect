package org.needcoke.rpc.net;

import org.needcoke.rpc.common.enums.RpcTypeEnum;
import org.needcoke.rpc.invoker.ConnectInvoker;
import org.needcoke.rpc.invoker.InvokeResult;
import org.needcoke.rpc.loadBalance.LoadBalance;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Connector {

    private final String serviceId;

    private final DiscoveryClient discoveryClient;

    private final ConnectInvoker invoker;

    private final LoadBalance loadBalance;

    private ConnectInvoker httpInvoker;

    private final ConcurrentHashMap<ServiceInstance, RpcTypeEnum> requestTypeMap;

    public Connector(String serviceId, DiscoveryClient discoveryClient, ConnectInvoker invoker, LoadBalance loadBalance) {
        this.serviceId = serviceId;
        this.discoveryClient = discoveryClient;
        this.invoker = invoker;
        this.loadBalance = loadBalance;
        this.requestTypeMap = new ConcurrentHashMap<>();
    }

    public ConcurrentHashMap<ServiceInstance, RpcTypeEnum> getRequestTypeMap() {
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
