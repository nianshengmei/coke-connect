package org.needcoke.rpc.utils;

import lombok.extern.slf4j.Slf4j;
import org.needcoke.rpc.invoker.ConnectInvoker;
import org.needcoke.rpc.invoker.InvokeResult;
import org.needcoke.rpc.loadBalance.LoadBalance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;

/**
 * @author Gilgamesh
 * @date 2022/4/2
 */
@Component
@Slf4j
public class ConnectUtil {

    private DiscoveryClient dc;

    private ConnectInvoker ci;
    @Autowired
    public void setDc(DiscoveryClient dc) {
        this.dc = dc;
    }

    private static DiscoveryClient discoveryClient;

    private static LoadBalance loadBalance;

    private static ConnectInvoker connectInvoker;

    @PostConstruct
    public void init() {
        discoveryClient = dc;
        loadBalance = lb;
        connectInvoker = ci;
    }

    private LoadBalance lb;

    @Autowired
    public void setLb(LoadBalance lb) {
        this.lb = lb;
    }

    @Autowired
    public static void setConnectInvoker(ConnectInvoker connectInvoker) {
        ConnectUtil.connectInvoker = connectInvoker;
    }

    /**
     * 执行远程方法
     *
     * @param serviceId  服务名称
     * @param beanName   远程服务上的 bean的名称
     * @param methodName 方法名称 或 @Call value
     * @return 返回远程方法执行结果的json
     */
    public static InvokeResult execute(String serviceId, String beanName,
                                 String methodName,
                                 Map<String, Object> params) {
        List<ServiceInstance> instances = discoveryClient.getInstances(serviceId);
        ServiceInstance instance = loadBalance.choose(serviceId,instances);
        InvokeResult result = connectInvoker.execute(instance, beanName, methodName, params);
        return result;
    }





}
