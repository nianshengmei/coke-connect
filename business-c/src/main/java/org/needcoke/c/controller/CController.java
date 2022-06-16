package org.needcoke.c.controller;

import org.needcoke.rpc.annotation.Call;
import org.needcoke.rpc.annotation.Rpc;
import org.needcoke.rpc.invoker.InvokeResult;
import org.needcoke.rpc.invoker.SmartSocketInvoker;
import org.needcoke.rpc.utils.ConnectUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component("cCon")
@Rpc
public class CController {

    @Bean
    public SmartSocketInvoker smartSocketInvoker(){
        return new SmartSocketInvoker();
    }

    @Call("cTest")
    public String cTest(String word){
        Map<String,Object> map = new HashMap<>();
        map.put("word",word);
        InvokeResult execute = ConnectUtil.execute("bussiness-a", "config", "hahha2", map);
        return execute.getBody().toString();
    }
}
