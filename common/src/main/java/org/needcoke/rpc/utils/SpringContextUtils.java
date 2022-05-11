package org.needcoke.rpc.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author yanming
 * @date 2022/5/11
 */
@Component
public class SpringContextUtils {


    private static ApplicationContext context;
    @Autowired
    private ApplicationContext ctx;


    private static Map<String, Method> beanNameMethodMap;
    @Autowired
    @Qualifier(value = "beanNameMethodMap")
    private Map<String, Method> bnmm;

    private static Map<String, Method> classNameMethodMap;

    @Qualifier(value = "classNameMethodMap")
    @Autowired
    private Map<String, Method> cnmm;

    @PostConstruct
    public void init(){
        context = ctx;
        beanNameMethodMap = bnmm;
        classNameMethodMap = cnmm;
    }
    public static <T> T getBean(String beanName){
        return (T)context.getBean(beanName);
    }

    public static <T> T getBean(Class<T> clz){
        return context.getBean(clz);
    }

    /**
     * bean的名称和方法名称
     *
     * @author Gilgamesh
     * @since V1.0
     */
    public static Method getMethod(String beanName,String methodName){
        return beanNameMethodMap.get(beanName+"#"+methodName);
    }

}
