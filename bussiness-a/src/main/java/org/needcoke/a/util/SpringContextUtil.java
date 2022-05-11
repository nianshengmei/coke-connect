package org.needcoke.a.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * @author yanming
 * @date 2022/5/11
 */
@Component
public class SpringContextUtil {

    @Autowired
    private static ApplicationContext context;

    @Autowired
    private static Map<String, Method> beanNameMethodMap;

    @Autowired
    private static Map<String, Method> classNameMethodMap;

    public static <T> T getBean(String beanName){
        return (T)context.getBean(beanName);
    }

    public static <T> T getBean(Class<T> clz){
        return context.getBean(clz);
    }

    public static Method getMethod(String beanName,String methodName){
        return beanNameMethodMap.get(beanName+"#"+methodName);
    }

}
