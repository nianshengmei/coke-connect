package org.needcoke.b.component;

import org.needcoke.rpc.netty.invoker.NettyInvoker;
import org.needcoke.rpc.netty.server.NettyServer;
import org.needcoke.rpc.smartsocket.invoker.SmartSocketInvoker;
import org.needcoke.rpc.smartsocket.server.SmartSocketServer;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * @author Gilgamesh
 * @date 2022/4/2
 */
@Component
public class TestComponent {

//    @Bean
//    @Primary
//    public WeightedResponseTimeBalance weightedResponseTimeBalance(){
//        return new WeightedResponseTimeBalance();
//    }d

    @Bean
    public NettyInvoker nettyInvoker(){
        return new NettyInvoker();
    }

    @Bean
    public NettyServer nettyServer(){
        return new NettyServer();
    }

//    @Bean
//    public SmartSocketInvoker smartSocketInvoker(){
//        return new SmartSocketInvoker();
//    }
//
//    @Bean
//    public SmartSocketServer smartSocketServer(){
//        return new SmartSocketServer();
//    }

}
