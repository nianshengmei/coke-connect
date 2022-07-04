package org.needcoke.rpc.proxy;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.Assert;

import java.util.Objects;

public class RpcClientFactoryBean implements FactoryBean<Object>, InitializingBean, ApplicationContextAware {

    /**
     * beanName
     */
    private String name;

    /**
     * object类型
     */
    private Class<?> type;

    private ApplicationContext applicationContext;

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.hasText(name, "Name must be set");
    }

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        applicationContext = context;
    }

    @Override
    public Object getObject() throws Exception {
        // 创建一个代理对象并返回
        Object proxyService = ClientProxyCreateFactory.getProxyService(type);
        return proxyService;
    }

    @Override
    public Class<?> getObjectType() {
        return type;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Class<?> getType() {
        return type;
    }

    public void setType(Class<?> type) {
        this.type = type;
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RpcClientFactoryBean that = (RpcClientFactoryBean) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(type, that.type) &&
                Objects.equals(applicationContext, that.applicationContext);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, type, applicationContext);
    }

    @Override
    public String toString() {
        return "SimpleRpcClientFactoryBean{" +
                "name='" + name + '\'' +
                ", type=" + type +
                ", applicationContext=" + applicationContext +
                '}';
    }
}