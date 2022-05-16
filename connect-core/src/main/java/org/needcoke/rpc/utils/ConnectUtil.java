package org.needcoke.rpc.utils;

import com.alibaba.fastjson.JSONObject;
import com.ejlchina.okhttps.HTTP;
import com.ejlchina.okhttps.HttpResult;
import com.ejlchina.okhttps.jackson.JacksonMsgConvertor;
import lombok.extern.slf4j.Slf4j;
import org.needcoke.rpc.common.constant.ConnectConstant;
import org.needcoke.rpc.common.constant.HttpContentTypeEnum;
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
    @Autowired
    public void setDc(DiscoveryClient dc) {
        this.dc = dc;
    }

    private static DiscoveryClient discoveryClient;

    private static LoadBalance loadBalance;

    @PostConstruct
    public void init() {
        discoveryClient = dc;
        loadBalance = lb;
    }

    private LoadBalance lb;

    @Autowired
    public void setLb(LoadBalance lb) {
        this.lb = lb;
    }

    /**
     * 执行远程方法
     *
     * @param serviceId  服务名称
     * @param beanName   远程服务上的 bean的名称
     * @param methodName 方法名称 或 @Call value
     * @return 返回远程方法执行结果的json
     */
    public static String execute(String serviceId, String beanName,
                                 String methodName,
                                 Map<String, Object> params) {
        List<ServiceInstance> instances = discoveryClient.getInstances(serviceId);
        ServiceInstance instance = loadBalance.choose(serviceId,instances);
        HttpResult result = HTTP.builder().addMsgConvertor(new JacksonMsgConvertor()).build()
                .sync(instance.getUri() + ConnectConstant.EXECUTE_RELATIVE_PATH)
                .bodyType(HttpContentTypeEnum.JSON.getValue())
                .addBodyPara(params)
                .addUrlPara(ConnectConstant.BEAN_NAME, beanName)
                .addUrlPara(ConnectConstant.METHOD_NAME, methodName)
                .post();
        handleResult(serviceId, beanName, methodName, params, result);
        return result.getBody().toString();
    }



    /**
     * 处理远程调用的执行结果，目前仅记录日志
     */
    public static void handleResult(String serviceId, String beanName,
                                    String methodName,
                                    Map<String, Object> params, HttpResult result) {
        String builder = "status = " + result.getStatus() +
                " , serviceId = " + serviceId +
                " , beanName = " + beanName +
                " , methodName = " + methodName +
                " , params = " + JSONObject.toJSONString(params);
        log.debug(builder);
    }

}
