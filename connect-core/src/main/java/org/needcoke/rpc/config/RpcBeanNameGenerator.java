package org.needcoke.rpc.config;

import org.needcoke.rpc.annotation.Rpc;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;

/**
 * @author Gilgamesh
 * @date 2022/4/2
 */
public class RpcBeanNameGenerator extends AnnotationBeanNameGenerator {

    @Override
    public String generateBeanName(BeanDefinition definition, BeanDefinitionRegistry registry) {
        //从自定义注解中拿name
        String name = getNameByServiceFindAnntation(definition,registry);
        if(name != null && !"".equals(name)){
            return name;
        }
        //走父类的方法
        return super.generateBeanName(definition, registry);
    }

    private String getNameByServiceFindAnntation(BeanDefinition definition, BeanDefinitionRegistry registry) {
        String beanClassName = definition.getBeanClassName();
        try {
            Class<?> aClass = Class.forName(beanClassName);
            Rpc annotation = aClass.getAnnotation(Rpc.class);
            if(annotation == null){
                return null;
            }
            //获取到注解name的值并返回
            return annotation.value();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

}
