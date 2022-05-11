package org.needcoke.a.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yanming
 * @date 2022/5/11
 */
@RestController
@RequestMapping("api/a")
@RequiredArgsConstructor
public class AController {

    private final DiscoveryClient discoveryClient;

    @GetMapping("instance")
    public List<String> instance() {
        return discoveryClient.getServices();
    }

    @GetMapping("instanceInfo")
    public Map<String, List<ServiceInstance>> instanceInfo() {
        List<String> services = discoveryClient.getServices();
        Map<String, List<ServiceInstance>> ret = new HashMap<>();
        for (String service : services) {
            ret.put(service, discoveryClient.getInstances(service));
        }
        return ret;
    }
}
