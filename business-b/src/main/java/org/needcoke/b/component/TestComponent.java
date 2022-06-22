package org.needcoke.b.component;

import org.needcoke.rpc.common.enums.RpcTypeEnum;
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
    public NettyInvoker nettyInvoker(RpcTypeEnum rpcTypeEnum){
        return new NettyInvoker(rpcTypeEnum);
    }

    @Bean
    public NettyServer nettyServer(){
        return new NettyServer();
    }

}
