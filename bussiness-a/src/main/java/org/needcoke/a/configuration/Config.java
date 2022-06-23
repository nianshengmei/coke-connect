package org.needcoke.a.configuration;

import lombok.extern.slf4j.Slf4j;
import org.needcoke.rpc.annotation.Call;
import org.needcoke.rpc.annotation.Rpc;
import org.needcoke.rpc.netty.invoker.NettyInvoker;
import org.needcoke.rpc.netty.server.NettyServer;
import org.needcoke.rpc.smartsocket.invoker.SmartSocketInvoker;
import org.needcoke.rpc.smartsocket.server.SmartSocketServer;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 *
 * @author yanming
 * @date 2022/5/11
 */
@Component
@Slf4j
@Rpc
public class Config {


    public String haha(){
        log.info(this.getClass().getName()+":haha()被调用");
        return "haha";
    }


    @Call("hahha2")
    public String haha(String word){
        log.info(this.getClass().getName()+":say()被调用");
        return "say : "+word;
    }

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
