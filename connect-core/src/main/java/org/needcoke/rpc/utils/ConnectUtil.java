package org.needcoke.rpc.utils;

import com.ejlchina.okhttps.HTTP;
import com.ejlchina.okhttps.HttpResult;
import com.ejlchina.okhttps.SHttpTask;
import com.ejlchina.okhttps.jackson.JacksonMsgConvertor;
import lombok.extern.slf4j.Slf4j;
import org.connect.rpc.link.tracking.util.TrackingUtil;
import org.needcoke.rpc.common.constant.ConnectConstant;
import org.needcoke.rpc.common.enums.HttpContentTypeEnum;
import org.needcoke.rpc.invoker.ConnectInvoker;
import org.needcoke.rpc.invoker.InvokeResult;
import org.needcoke.rpc.loadBalance.LoadBalance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Gilgamesh
 * @date 2022/4/2
 */
@Component
@AutoConfigureAfter(ConnectInvoker.class)
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
        log.info("ConnectUtil 加载的 loadBalance是 {} , 加载的ConnectInvoker是 {}。",loadBalance.getClass().getSimpleName(),connectInvoker.getClass().getSimpleName());
    }

    private LoadBalance lb;

    @Autowired
    public void setLb(LoadBalance lb) {
        this.lb = lb;
    }
    @Autowired
    public void setCi(ConnectInvoker ci) {
        this.ci = ci;
    }

    public static final AtomicInteger requestIdMaker = new AtomicInteger();

    private static final Map<String, InvokeResult> requestMap = new ConcurrentHashMap();

    public static void putRequestMap(InvokeResult result){
        requestMap.put(TrackingUtil.getRequestId(),result);
    }

    private static Map<String,Thread> threadMap = new ConcurrentHashMap<>();

    public static void putRequestMap(String requestId,InvokeResult result){
        requestMap.put(requestId,result);
    }

    public static InvokeResult getFromRequestMap(String key){
        return requestMap.remove(key);
    }

    public static void putThreadMap(String requestId,Thread thread){
        threadMap.put(requestId,thread);
    }

    public static Thread getFromThreadMap(String requestId){
        return threadMap.remove(requestId);
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
        InvokeResult result = connectInvoker.execute(null,instance, beanName, methodName, params);
        return result;
    }


    public static Integer getCokeServerPort(ServiceInstance instance){
        SHttpTask sHttpTask = HTTP.builder().addMsgConvertor(new JacksonMsgConvertor()).build()
                .sync(instance.getUri() + ConnectConstant.COKE_PORT_RELATIVE_PATH)
                .bodyType(HttpContentTypeEnum.JSON.getValue());
        HttpResult result = sHttpTask
                .get();
        return result.getBody().toBean(Integer.class);
    }
}
