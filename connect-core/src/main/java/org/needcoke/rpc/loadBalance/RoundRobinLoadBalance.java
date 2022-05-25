package org.needcoke.rpc.loadBalance;

import org.springframework.cloud.client.ServiceInstance;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
public class RoundRobinLoadBalance extends LoadBalance {

    private final ConcurrentHashMap<String, Integer> loadBalanceMap = new ConcurrentHashMap<>();

    @Override
    public ServiceInstance loadBalanceChoose(String serviceId, List<ServiceInstance> instances) {
        if (!loadBalanceMap.containsKey(serviceId)) {
            loadBalanceMap.put(serviceId,0);
        }
        int offset = loadBalanceMap.get(serviceId);
        loadBalanceMap.put(serviceId, (offset + 1) % instances.size());
        return instances.get(offset);
    }
}
