package org.needcoke.rpc.utils;

import org.needcoke.rpc.common.enums.ConnectionExceptionEnum;
import org.needcoke.rpc.common.exception.CokeConnectException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author yanming
 * @date 2022/5/11
 */
@Component
public class SpringContextUtils {

    private static SpringContextUtils instance;


    @Resource
    private ApplicationContext context;

    @Resource(name = "beanNameMethodMap")
    private Map<String, Method> beanNameMethodMap;

    @Resource(name = "classNameMethodMap")
    private Map<String, Method> classNameMethodMap;

    @PostConstruct
    public void init() {
        instance = context.getBean(SpringContextUtils.class);
    }

    public <T> T getBeans(String beanName) {
        try {
            return (T) context.getBean(beanName);
        } catch (NoSuchBeanDefinitionException exception) {
            throw new CokeConnectException(ConnectionExceptionEnum.NO_SUCH_BEAN_NAME);
        }
    }

    public <T> T getBeans(Class<T> clz) {
        return context.getBean(clz);
    }

    /**
     * bean的名称和方法名称
     *
     * @author Gilgamesh
     * @since V1.0
     */
    public Method getMethods(String beanName, String methodName) {
        return beanNameMethodMap.get(beanName + "#" + methodName);
    }

    public static <T> T getBean(String beanName){
        return instance.getBeans(beanName);
    }

    public static  <T> T getBean(Class<T> clz) {
        return instance.getBeans(clz);
    }

    public static Method getMethod(String beanName, String methodName) {
        return instance.getMethods(beanName,methodName);
    }
}
