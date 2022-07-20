package org.needcoke.rpc.config;

import cn.hutool.core.util.StrUtil;
import org.needcoke.rpc.annotation.EnableRpcClient;
import org.needcoke.rpc.annotation.RpcClient;
import org.needcoke.rpc.proxy.RpcClientFactoryBean;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.*;

/**
 * simple rpc 客户端扫描注册器
 *
 * @author warren
 */
public class RpcClientRegister implements ImportBeanDefinitionRegistrar, ResourceLoaderAware, EnvironmentAware {

    /**
     * 资源加载器
     */
    private ResourceLoader resourceLoader;
    /**
     * 环境
     */
    private Environment environment;

    public RpcClientRegister() {
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        // 注册bean客户端
        registerSimpleRpcClients(importingClassMetadata, registry);
    }

    /**
     * 注册simple rpc 客户端
     */
    public void registerSimpleRpcClients(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
        ClassPathScanningCandidateComponentProvider scanner = getScanner();
        scanner.setResourceLoader(resourceLoader);

        AnnotationTypeFilter annotationTypeFilter = new AnnotationTypeFilter(RpcClient.class);
        // 这里只会走if，暂时不会走else去
        scanner.addIncludeFilter(annotationTypeFilter);
        Set<String> basePackages = getBasePackages(metadata);

        // 遍历每一个basePackages
        for (String basePackage : basePackages) {
            // 通过scanner获取候选组件
            Set<BeanDefinition> candidateComponents = scanner.findCandidateComponents(basePackage);
            for (BeanDefinition candidateComponent : candidateComponents) {
                if (candidateComponent instanceof AnnotatedBeanDefinition) {
                    // verify annotated class is an interface
                    AnnotatedBeanDefinition beanDefinition = (AnnotatedBeanDefinition) candidateComponent;
                    AnnotationMetadata annotationMetadata = beanDefinition.getMetadata();
                    Assert.isTrue(annotationMetadata.isInterface(), "@SimpleRpcClient can only be specified on an interface");
                    // 获取SimpleRpcClient注解的属性
                    Map<String, Object> attributes = annotationMetadata.getAnnotationAttributes(RpcClient.class.getCanonicalName());
                    registerSimpleRpcClient(registry, annotationMetadata, attributes,beanDefinition);
                }
            }
        }
    }

    public Class<?> getClass(String beanClassName){
        try {
            return Class.forName(beanClassName);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 注册simple rpc 客户端
     *
     * @param registry           bean定义信息注册器
     * @param annotationMetadata 元数据
     * @param attributes         @SimpleRpcClient注解的属性
     * @author Mr_wenpan@163.com 2022/1/19 2:48 下午
     */
    private void registerSimpleRpcClient(BeanDefinitionRegistry registry,
                                         AnnotationMetadata annotationMetadata,
                                         Map<String, Object> attributes,
                                         AnnotatedBeanDefinition bd) {
        // 类名（接口全限定名）
        String className = annotationMetadata.getClassName();
        BeanDefinitionBuilder definitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(RpcClientFactoryBean.class);
        // 解析出@SimpleRpcClient注解的name
        String name = getName(attributes);
        if (!StringUtils.hasText(name)) {
            throw new RuntimeException(String.format("class [%s] , @SimpleRpcClient name or value can not be null, please check.", className));
        }
        definitionBuilder.addPropertyValue("name", name);
        definitionBuilder.addPropertyValue("type",getClass(className));
        definitionBuilder.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);
        String alias = name + "SimpleRpcClient";
        AbstractBeanDefinition beanDefinition = definitionBuilder.getBeanDefinition();

        beanDefinition.setPrimary(true);

        // 设置qualifier，优先使用qualifier
        String qualifier = getQualifier(attributes);
        if (StringUtils.hasText(qualifier)) {
            alias = qualifier;
        }

        // 注册bean定义信息
        BeanDefinitionHolder holder = new BeanDefinitionHolder(beanDefinition, className, new String[]{alias});
        BeanDefinitionReaderUtils.registerBeanDefinition(holder, registry);
    }

    /**
     * 获取qualifier
     */
    private static String getQualifier(Map<String, Object> client) {
        if (client == null) {
            return null;
        }
        String qualifier = (String) client.get("qualifier");
        if (StringUtils.hasText(qualifier)) {
            return qualifier;
        }
        return null;
    }

    /**
     * 获取name
     */
    protected String getName(Map<String, Object> attributes) {
        String name = (String) attributes.get("name");
        if (!StringUtils.hasText(name)) {
            name = (String) attributes.get("value");
        }
        if (StrUtil.isEmpty(name)) {
            name = resolve(name);
        }
        ;
        return name;
    }

    /**
     * 解析name
     */
    private String resolve(String value) {
        if (StringUtils.hasText(value)) {
            return environment.resolvePlaceholders(value);
        }
        return value;
    }

    /**
     * 获取扫描器
     */
    protected ClassPathScanningCandidateComponentProvider getScanner() {
        return new ClassPathScanningCandidateComponentProvider(false, environment) {
            @Override
            protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
                boolean isCandidate = false;
                if (beanDefinition.getMetadata().isIndependent()) {
                    if (!beanDefinition.getMetadata().isAnnotation()) {
                        isCandidate = true;
                    }
                }
                return isCandidate;
            }
        };
    }

    /**
     * 获取base packages
     */
    protected static Set<String> getBasePackages(AnnotationMetadata importingClassMetadata) {
        // 获取到@EnableSimpleRpcClients注解所有属性
        Map<String, Object> attributes = importingClassMetadata
                .getAnnotationAttributes(EnableRpcClient.class.getCanonicalName());
        if (null == attributes) {
            return null;
        }
        Set<String> basePackages = new HashSet<>();
        assert attributes != null;
        String[] values = (String[]) attributes.get("value");
        for (String pkg : values) {
            if (StringUtils.hasText(pkg)) {
                basePackages.add(pkg);
            }
        }
        String[] bgs = (String[]) attributes.get("basePackages");
        for (String pkg : bgs) {
            if (StringUtils.hasText(pkg)) {
                basePackages.add(pkg);
            }
        }
        // 如果上面两步都没有获取到basePackages，那么这里就默认使用当前项目启动类所在的包为basePackages
        if (basePackages.isEmpty()) {
            basePackages.add(ClassUtils.getPackageName(importingClassMetadata.getClassName()));
        }
        return basePackages;
    }

    /**
     * Helper class to create a {@link TypeFilter} that matches if all the delegates match.
     *
     * @author Oliver Gierke
     */
    private static class AllTypeFilter implements TypeFilter {

        private final List<TypeFilter> delegates;

        /**
         * Creates a new {@link AllTypeFilter} to match if all the given delegates match.
         *
         * @param delegates must not be {@literal null}.
         */
        public AllTypeFilter(List<TypeFilter> delegates) {
            Assert.notNull(delegates, "This argument is required, it must not be null");
            this.delegates = delegates;
        }

        @Override
        public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory) throws IOException {

            for (TypeFilter filter : delegates) {
                if (!filter.match(metadataReader, metadataReaderFactory)) {
                    return false;
                }
            }

            return true;
        }
    }
}