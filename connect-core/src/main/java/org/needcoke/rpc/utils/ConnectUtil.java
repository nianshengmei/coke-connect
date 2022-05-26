package org.needcoke.rpc.utils;

import com.ejlchina.okhttps.HTTP;
import com.ejlchina.okhttps.HttpResult;
import com.ejlchina.okhttps.SHttpTask;
import com.ejlchina.okhttps.jackson.JacksonMsgConvertor;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.request.async.DeferredResult;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
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

    public static final Map<Integer, InvokeResult> requestMap = new ConcurrentHashMap();

    public static void putRequestMap(InvokeResult result){
        requestMap.put(requestIdMaker.addAndGet(1),result);
    }

    public static ConcurrentHashMap<Integer,Thread> threadMap = new ConcurrentHashMap<>();

    public static void putRequestMap(Integer requestId,InvokeResult result){
        requestMap.put(requestId,result);
    }

    public static InvokeResult getFromRequestMap(Integer key){
        return requestMap.get(key);
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


    public static Integer getCokeServerPort(ServiceInstance instance){
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        SHttpTask sHttpTask = HTTP.builder().addMsgConvertor(new JacksonMsgConvertor()).build()
                .sync(instance.getUri() + ConnectConstant.COKE_PORT_RELATIVE_PATH)
                .bodyType(HttpContentTypeEnum.JSON.getValue());
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String nextElement = headerNames.nextElement();
            String header = request.getHeader(nextElement);
            sHttpTask.addHeader(nextElement,header);
        }
        HttpResult result = sHttpTask
                .get();
        return result.getBody().toBean(Integer.class);
    }
}
