package org.needcoke.rpc.config;

import org.needcoke.rpc.annotation.Rpc;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.util.Set;

/**
 * 类路径bean扫描器
 *
 * @author Gilgamesh
 * @date 2022/4/2
 */
public class ConnectionClassPathBeanDefinitionScanner extends ClassPathBeanDefinitionScanner {

    public ConnectionClassPathBeanDefinitionScanner(BeanDefinitionRegistry registry, boolean useDefaultFilters) {
        super(registry, useDefaultFilters);
    }

    @Override
    protected Set<BeanDefinitionHolder> doScan(String... basePackages) {
        //添加过滤条件，这里是只添加了@NRpcServer的注解才会被扫描到
        addIncludeFilter(new AnnotationTypeFilter(Rpc.class));
        //调用spring的扫描
        return super.doScan(basePackages);
    }
}
