package org.needcoke.rpc.utils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.http.ContentType;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.ejlchina.okhttps.HTTP;
import com.ejlchina.okhttps.HttpResult;
import com.ejlchina.okhttps.HttpUtils;
import com.ejlchina.okhttps.SHttpTask;
import com.ejlchina.okhttps.jackson.JacksonMsgConvertor;
import lombok.experimental.Accessors;
import org.smartboot.http.client.BodyStream;
import org.smartboot.http.client.HttpClient;
import org.smartboot.http.client.HttpGet;
import org.smartboot.http.client.HttpPost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
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

    private static RestTemplate rt = new RestTemplate();

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

        HttpResult result = HTTP.builder().addMsgConvertor(new JacksonMsgConvertor()).build()
                .sync(instance.getUri() + "/coke/connect/execute")
                .bodyType("json")
                .addBodyPara(params)
                .addUrlPara("beanName", beanName)
                .addUrlPara("methodName", methodName)
                .post();
        return result.getBody().toString();
    }

    public static ServiceInstance choose(List<ServiceInstance> instances){
        if(CollUtil.isNotEmpty(instances)){
            return instances.get(0);
        }
        return null;
    }

}
