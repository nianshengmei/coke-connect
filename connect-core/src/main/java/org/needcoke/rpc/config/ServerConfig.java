package org.needcoke.rpc.config;

import lombok.Getter;
import org.needcoke.rpc.invoker.ConnectInvoker;
import org.needcoke.rpc.invoker.OkHttpsInvoker;
import org.needcoke.rpc.loadBalance.LoadBalance;
import org.needcoke.rpc.loadBalance.RoundRobinLoadBalance;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration(proxyBeanMethods = false)
public class ServerConfig {

    @Value("${coke.server.port:12001}")
    private int cokeServerPort;

    @Value("${coke.server.type:http}")
    private String serverType;

    @Value("${server.port}")
    private int mvcPort;

    /**
     * coke-connect的默认远程调用组件为okHttps
     */
    @ConditionalOnMissingBean(ConnectInvoker.class)
    @Bean
    public OkHttpsInvoker okHttpsInvoker() {
        return new OkHttpsInvoker();
    }

    /**
     * coke-connect的默认负载均衡策略为轮询
     */
    @ConditionalOnMissingBean(LoadBalance.class)
    @Bean
    public RoundRobinLoadBalance roundRobinLoadBalance() {
        return new RoundRobinLoadBalance();
    }
}
