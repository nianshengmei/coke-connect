package org.needcoke.rpc.proxy;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.needcoke.rpc.annotation.RpcClient;
import org.needcoke.rpc.invoker.InvokeResult;
import org.needcoke.rpc.net.ConnectorFactory;
import org.needcoke.rpc.utils.SpringContextUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

/**
 * 客户端代理创建工厂
 */
@Slf4j
public class ClientProxyCreateFactory {

    /**
     * 通过接口的class创建该接口的代理对象(这里直接基于JDK提供的创建动态代理的工具来创建代理对象)
     *
     * @param serviceClass 接口的class
     * @return T 代理对象
     */
    public static <T> T getProxyService(Class<T> serviceClass) {
        // 该接口的Class对象是被那个类加载器加载的
        ClassLoader classLoader = serviceClass.getClassLoader();
        // 获取到该接口所有的interface
        Class<?>[] interfaces = {serviceClass};
        // jdk代理必须的handler，代理对象的方法执行就会调用这里的invoke方法。自动传入调用的方法 + 方法参数
        Object proxy = Proxy.newProxyInstance(classLoader, interfaces, new InvocationHandler<T>(serviceClass));
        // 返回代理对象
        return (T) proxy;
    }
}
