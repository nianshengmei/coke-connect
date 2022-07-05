package org.needcoke.b.controller;

import lombok.RequiredArgsConstructor;
import org.needcoke.rpc.invoker.InvokeResult;
import org.needcoke.rpc.loadBalance.LoadBalance;
import org.needcoke.rpc.net.ConnectorFactory;
import org.needcoke.rpc.utils.ConnectUtil;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Gilgamesh
 * @date 2022/4/2
 */
@RestController
@RequestMapping("api/b")
@RequiredArgsConstructor
public class TestController {

    private final ConnectorFactory connectorFactory;

    @GetMapping("test")
    public InvokeResult test(){
        Map<String,Object> map = new HashMap<>();
        map.put("word","12345");
        InvokeResult execute = connectorFactory.connector("bussiness-c").execute( "cCon", "cTest2", map);
        return execute;
    }

    @Resource
    private DiscoveryClient discoveryClient;

    @Resource
    private LoadBalance loadBalance;

}
