package org.needcoke.rpc.config;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Gilgamesh
 * @date 2022/4/2
 */
@Configuration
public class CokeConfiguration {

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
