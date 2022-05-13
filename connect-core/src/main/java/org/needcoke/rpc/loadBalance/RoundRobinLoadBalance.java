package org.needcoke.rpc.loadBalance;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@ConditionalOnMissingBean(LoadBalance.class)
@Component
public class RoundRobinLoadBalance extends LoadBalance {

    private final ConcurrentHashMap<String, Integer> loadBalanceMap = new ConcurrentHashMap<>();

    @Override
    public ServiceInstance loadBalanceChoose(String serviceId, List<ServiceInstance> instances) {
        Integer offset = loadBalanceMap.putIfAbsent(serviceId, 0);
        loadBalanceMap.put(serviceId, (offset + 1) % instances.size());
        return instances.get(offset);
    }
}
