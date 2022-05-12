package org.needcoke.rpc.utils;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSONObject;
import com.ejlchina.okhttps.HTTP;
import com.ejlchina.okhttps.HttpResult;
import com.ejlchina.okhttps.jackson.JacksonMsgConvertor;
import lombok.extern.slf4j.Slf4j;
import org.needcoke.rpc.common.constant.ConnectConstant;
import org.needcoke.rpc.common.constant.ConnectionExceptionEnum;
import org.needcoke.rpc.common.constant.HttpContentType;
import org.needcoke.rpc.common.exception.CokeConnectException;
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

    @PostConstruct
    public void init() {
        discoveryClient = dc;
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
        ServiceInstance instance = choose(instances);
        HttpResult result = HTTP.builder().addMsgConvertor(new JacksonMsgConvertor()).build()
                    .sync(instance.getUri() + ConnectConstant.EXECUTE_RELATIVE_PATH)
                    .bodyType(HttpContentType.JSON.getValue())
                    .addBodyPara(params)
                    .addUrlPara(ConnectConstant.BEAN_NAME, beanName)
                    .addUrlPara(ConnectConstant.METHOD_NAME, methodName)
                    .post();
            handleResult(serviceId, beanName, methodName, params, result);
        return result.getBody().toString();
    }

    /**
     * 从实例列表选择一个远程服务
     * <p>
     * TODO 扩展负载均衡策略
     *
     * @param instances 从注册中心获取的实例列表
     * @return 某一个特定的实例
     */
    public static ServiceInstance choose(List<ServiceInstance> instances) {
        if (CollUtil.isNotEmpty(instances)) {
            return instances.get(0);
        }
        throw new CokeConnectException(ConnectionExceptionEnum.CAN_NOT_FIND_SUCH_INSTANCE);
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
