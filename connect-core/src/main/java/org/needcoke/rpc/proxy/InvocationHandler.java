package org.needcoke.rpc.proxy;

import cn.hutool.core.util.StrUtil;
import org.needcoke.rpc.annotation.RpcClient;
import org.needcoke.rpc.invoker.InvokeResult;
import org.needcoke.rpc.net.ConnectorFactory;
import org.needcoke.rpc.utils.SpringContextUtils;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class InvocationHandler<T> implements java.lang.reflect.InvocationHandler {
    private Class<T> serviceClass;

    public InvocationHandler(Class<T> serviceClass) {
        this.serviceClass = serviceClass;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RpcClient rpcClient = serviceClass.getAnnotation(RpcClient.class);
        ConnectorFactory connectorFactory = SpringContextUtils.getBean(ConnectorFactory.class);
        if(StrUtil.isEmpty(rpcClient.serviceId())){
            throw new RuntimeException(serviceClass.getName()+" need serviceId");
        }
        Map<String,Object> params = new HashMap<>();
        for (int i = 1; i <= args.length; i++) {
            params.put("arg"+i,args[i]);
        }
        InvokeResult result = connectorFactory.connector(rpcClient.serviceId()).execute(rpcClient.beanName(), method.getName(), params);
        return result.getBody();
    }
}
