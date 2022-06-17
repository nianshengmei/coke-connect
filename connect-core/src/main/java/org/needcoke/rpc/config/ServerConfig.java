package org.needcoke.rpc.config;

import lombok.Getter;
import org.needcoke.rpc.common.enums.RpcTypeEnum;
import org.needcoke.rpc.invoker.ConnectInvoker;
import org.needcoke.rpc.invoker.OkHttpsInvoker;
import org.needcoke.rpc.invoker.SmartSocketInvoker;
import org.needcoke.rpc.loadBalance.LoadBalance;
import org.needcoke.rpc.loadBalance.RoundRobinLoadBalance;
import org.needcoke.rpc.server.SmartSocketServer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@Configuration
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
    public OkHttpsInvoker okHttpsInvoker(RpcTypeEnum rpcTypeEnum){
        return new OkHttpsInvoker(rpcTypeEnum);
    }

     /**
      *  coke-connect的默认负载均衡策略为轮询
      */
    @ConditionalOnMissingBean(LoadBalance.class)
    @Bean
    public RoundRobinLoadBalance roundRobinLoadBalance(){
        return new RoundRobinLoadBalance();
    }

    /**
     *  当远程调用方式修改为SmartSocketInvoker时启动SmartSocketServer
     */
    @ConditionalOnBean(SmartSocketInvoker.class)
    @Bean
    public SmartSocketServer smartSocketServer(){
        return  new SmartSocketServer();
    }

    /**
     * server uri -> 端口号
     */
    @ConditionalOnMissingBean(OkHttpsInvoker.class)
    @Bean
    public Map<String,Integer> cokeServerPortMap(){
        return new ConcurrentHashMap<>();
    }

    @Bean
    public RpcTypeEnum rpcType(){
        return RpcTypeEnum.okHttp3;
    }
}
