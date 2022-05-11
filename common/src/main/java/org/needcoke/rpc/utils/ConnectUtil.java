package org.needcoke.rpc.utils;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSONObject;
import lombok.experimental.Accessors;
import org.smartboot.http.client.HttpClient;
import org.smartboot.http.client.HttpGet;
import org.smartboot.http.client.HttpPost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Gilgamesh
 * @date 2022/4/2
 */
@Component
public class ConnectUtil {

    @Autowired
    private DiscoveryClient dc;

    private static DiscoveryClient discoveryClient;

    @PostConstruct
    public void init(){
        discoveryClient = dc;
    }

    public static String execute(String serviceId,String beanName,
                   String methodName,
                   Map<String, Object> params){
        List<ServiceInstance> instances = discoveryClient.getInstances(serviceId);
        ServiceInstance instance = choose(instances);
        HttpClient httpClient = new HttpClient(instance.getHost(), instance.getPort());
        httpClient.connect();
       httpClient.post("/coke/connect/execute")
        .addQueryParam("beanName",beanName)
        .addQueryParam("methodName",methodName)
                .addQueryParam("params",JSONObject.toJSONString(params))
        .onSuccess(response -> {
                    response.body();
                })
                .onFailure(throwable -> throwable.printStackTrace())
                .send();
        return null;
    }

    public static ServiceInstance choose(List<ServiceInstance> instances){
        if(CollUtil.isNotEmpty(instances)){
            return instances.get(0);
        }
        return null;
    }

}
