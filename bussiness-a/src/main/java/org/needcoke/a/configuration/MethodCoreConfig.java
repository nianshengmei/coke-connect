package org.needcoke.a.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author yanming
 * @date 2022/5/11
 */
@Configuration
public class MethodCoreConfig {

    /**
     * ${beanName}#${methodName} -> method
     */
    @Bean(value = "beanNameMethodMap")
    public Map<String, Method> beanNameMethodMap(){
        return new HashMap<>();
    }

    /**
     * ${class full Name}#${methodName} -> method
     */
    @Bean(value = "classNameMethodMap")
    public Map<String, Method> classNameMethodMap(){
        return new HashMap<>();
    }
}
