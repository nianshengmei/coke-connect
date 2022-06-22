package org.needcoke.rpc.invoker;

import com.alibaba.fastjson.JSONObject;
import com.ejlchina.okhttps.HttpResult;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.needcoke.rpc.common.enums.RpcTypeEnum;
import org.needcoke.rpc.net.Connector;
import org.springframework.cloud.client.ServiceInstance;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@NoArgsConstructor
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
    protected InvokeResult runDefaultExecute(Connector connector, ServiceInstance instance, String beanName, String methodName, Map<String, Object> params){
        RpcTypeEnum remoteRpcType = connector.getRpcType(instance);
        if(remoteRpcType == RpcTypeEnum.okHttp3){
            if (null == connector.getHttpInvoker()) {
                connector.setHttpInvoker(new OkHttpsInvoker());
            }
            return connector.compensationExecute(instance,beanName,methodName,params);
        }
        return null;
    }
}
