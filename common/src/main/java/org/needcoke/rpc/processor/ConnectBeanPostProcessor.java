package org.needcoke.rpc.processor;

import cn.hutool.core.util.StrUtil;
import org.needcoke.rpc.annotation.Call;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author yanming
 * @date 2022/5/12
 */
@Component
public class ConnectBeanPostProcessor implements BeanPostProcessor {

    @Resource(name = "beanNameMethodMap")
    private Map<String, Method> beanNameMethodMap;

    @Resource(name = "classNameMethodMap")
    private Map<String, Method> classNameMethodMap;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Class<?> clz = bean.getClass();
        Method[] methods = clz.getDeclaredMethods();
        for (Method method : methods) {
            String methodName = method.getName();
            Call call = method.getAnnotation(Call.class);
            if(null != call && StrUtil.isNotEmpty(call.value())){
                methodName = call.value();
            }
            beanNameMethodMap.put(beanName+"#"+methodName,method);
            classNameMethodMap.put(clz.getName()+"#"+methodName,method);
        }
        return BeanPostProcessor.super.postProcessBeforeInitialization(bean, beanName);
    }
}
