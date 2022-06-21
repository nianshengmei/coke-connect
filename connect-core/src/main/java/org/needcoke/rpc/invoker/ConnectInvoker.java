package org.needcoke.rpc.invoker;

import com.alibaba.fastjson.JSONObject;
import com.ejlchina.okhttps.HTTP;
import com.ejlchina.okhttps.HttpResult;
import com.ejlchina.okhttps.SHttpTask;
import com.ejlchina.okhttps.jackson.JacksonMsgConvertor;
import lombok.extern.slf4j.Slf4j;
import org.needcoke.rpc.common.constant.ConnectConstant;
import org.needcoke.rpc.common.enums.HttpContentTypeEnum;
import org.needcoke.rpc.common.enums.RpcTypeEnum;
import org.needcoke.rpc.net.Connector;
import org.springframework.cloud.client.ServiceInstance;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public abstract class ConnectInvoker {

    public abstract InvokeResult execute(Connector connector, ServiceInstance instance, String beanName, String methodName, Map<String, Object> params);


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

    private final Map<ServiceInstance, RpcTypeEnum> remoteRpcTypeMap = new HashMap<>();

    public RpcTypeEnum getRemoteRpcType(ServiceInstance instance) {
        if (remoteRpcTypeMap.containsKey(instance)) {
            return remoteRpcTypeMap.get(instance);
        }
        SHttpTask sHttpTask = HTTP.builder().addMsgConvertor(new JacksonMsgConvertor()).build()
                .sync(instance.getUri() + ConnectConstant.COKE_RPC_TYPE_RELATIVE_PATH)
                .bodyType(HttpContentTypeEnum.JSON.getValue());
        HttpResult result = sHttpTask
                .get();
        RpcTypeEnum rpcTypeEnum = result.getBody().toBean(RpcTypeEnum.class);
        remoteRpcTypeMap.put(instance, rpcTypeEnum);
        return rpcTypeEnum;
    }
}
