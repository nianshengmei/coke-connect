package org.needcoke.rpc.invoker;

import com.alibaba.fastjson.JSONObject;
import com.ejlchina.okhttps.HttpResult;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.connect.rpc.link.tracking.util.TrackingUtil;
import org.needcoke.rpc.common.enums.RpcTypeEnum;
import org.needcoke.rpc.config.FuseConfig;
import org.needcoke.rpc.fuse.Fuse;
import org.needcoke.rpc.fuse.FuseTask;
import org.needcoke.rpc.fuse.FuseThreadPool;
import org.needcoke.rpc.net.Connector;
import org.needcoke.rpc.utils.SpringContextUtils;
import org.springframework.cloud.client.ServiceInstance;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.DelayQueue;

@Slf4j
public abstract class ConnectInvoker {

    private DelayQueue<FuseTask> fuseTaskDelayQueue;

    private FuseConfig fuseConfig;

    private FuseThreadPool fuseThreadPool;

    public ConnectInvoker() {
        this.fuseTaskDelayQueue = SpringContextUtils.getBean("fuseTaskDelayQueue");
        this.fuseConfig = SpringContextUtils.getBean("fuseConfig");
        this.fuseThreadPool = SpringContextUtils.getBean("fuseThreadPool");
    }

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

    protected void fuse(){
        fuseTaskDelayQueue.put(
                new FuseTask(TrackingUtil.getRequestId(),
                        Thread.currentThread(),
                        fuseConfig.getFuseTimeOut()
                )
        );
        fuseThreadPool.newTask(new Fuse(fuseTaskDelayQueue));
    }
}
