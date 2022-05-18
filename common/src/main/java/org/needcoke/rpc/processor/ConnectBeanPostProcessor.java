package org.needcoke.rpc.processor;

import cn.hutool.core.util.StrUtil;
import org.needcoke.rpc.annotation.Call;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author Gilgamesh
 * @date 2022/4/2
 */
@Component
public class ConnectBeanPostProcessor implements BeanPostProcessor {

    @Autowired
    private Map<String, Method> beanNameMethodMap;

    @Autowired
    private Map<String, Method> classNameMethodMap;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        String beanSimpleName = bean.getClass().getSimpleName();
        String beanFullName = bean.getClass().getName();
        try {
            if (!beanFullName.contains("$")) {
                Class<?> aClass = Class.forName(beanFullName);
                if(aClass.getSimpleName().equals("Config")){
                    System.out.println("haha");
                }
                Method[] methods = aClass.getDeclaredMethods();
                for (Method method : methods) {
                    String name = method.getName();
                    Call call = method.getAnnotation(Call.class);
                    if (null != call && StrUtil.isNotEmpty(call.value())) {
                        name = call.value();
                    }
                    beanNameMethodMap.put(beanName + "#" + name, method);
                    classNameMethodMap.put(beanFullName + "#" + name, method);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return BeanPostProcessor.super.postProcessBeforeInitialization(bean, beanName);
    }
}
