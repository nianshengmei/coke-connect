package org.needcoke.c.controller;

import org.needcoke.rpc.annotation.Call;
import org.needcoke.rpc.annotation.Rpc;
import org.needcoke.rpc.common.enums.RpcTypeEnum;
import org.needcoke.rpc.invoker.InvokeResult;
import org.needcoke.rpc.net.ConnectorFactory;
import org.needcoke.rpc.netty.invoker.NettyInvoker;
import org.needcoke.rpc.netty.server.NettyServer;
import org.needcoke.rpc.smartsocket.invoker.SmartSocketInvoker;
import org.needcoke.rpc.smartsocket.server.SmartSocketServer;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Component("cCon")
@Rpc
public class CController {

//    @Bean
//    public NettyInvoker nettyInvoker(){
//        return new NettyInvoker();
//    }
//
//    @Bean
//    public NettyServer nettyServer(){
//        return new NettyServer();
//    }

    @Bean
    public SmartSocketInvoker smartSocketInvoker(){
        return new SmartSocketInvoker();
    }

    @Bean
    public SmartSocketServer smartSocketServer(){
        return new SmartSocketServer();
    }

    @Resource
    private ConnectorFactory connectorFactory;

    @Call("cTest2")
    public Object cTest(String word){
        Map<String,Object> map = new HashMap<>();
        map.put("word",word);

        InvokeResult execute = connectorFactory.connector("bussiness-a").execute("config", "hahha2", map);
        return execute.getBody();
    }
}
