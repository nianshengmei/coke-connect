package org.needcoke.c.controller;

import org.needcoke.rpc.annotation.Call;
import org.needcoke.rpc.annotation.Rpc;
import org.needcoke.rpc.invoker.InvokeResult;
import org.needcoke.rpc.invoker.SmartSocketInvoker;
import org.needcoke.rpc.net.ConnectorFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Component("cCon")
@Rpc
public class CController {

//    @Bean
//    public SmartSocketInvoker smartSocketInvoker(RpcTypeEnum rpcTypeEnum){
//        return new SmartSocketInvoker(rpcTypeEnum);
//    }

    @Resource
    private ConnectorFactory connectorFactory;

    @Call("cTest2")
    public String cTest(String word){
        Map<String,Object> map = new HashMap<>();
        map.put("word",word);
        InvokeResult execute = connectorFactory.connector("bussiness-a").execute("config", "hahha2", map);
        return execute.getBody().toString();
    }
}
