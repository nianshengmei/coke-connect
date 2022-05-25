package org.needcoke.rpc.loadBalance;

import cn.hutool.core.bean.BeanUtil;
import org.needcoke.rpc.CokeServiceInstance;
import org.springframework.cloud.client.ServiceInstance;
import java.util.*;
import java.util.stream.Collectors;

public class WeightedResponseTimeBalance extends LoadBalance {

    private volatile int offset = 0;

    @Override
    public ServiceInstance loadBalanceChoose(String serviceId, List<ServiceInstance> instances) {
        /*
         * 0、1、2
         * 3、4
         * 5
         * 6、7、8
         * 9、10
         * 11
         * */
        List<CokeServiceInstance> cokeServiceInstanceList = instances.stream().map(this::toCokeInstance).collect(Collectors.toList());
        Map<String, String> weightMap = cokeServiceInstanceList.stream().collect(Collectors.toMap((i) -> i.getHost() + ":" + i.getPort(), CokeServiceInstance::getWeight));
        Map<ServiceInstance, Double> instanceWeightMap = new HashMap<>();
        for (ServiceInstance instance : instances) {
            instanceWeightMap.put(instance, Double.parseDouble(weightMap.get(instance.getHost() + ":" + instance.getPort())));
        }
        List<ServiceInstance> list = new ArrayList<>();

        Set<ServiceInstance> keySet = instanceWeightMap.keySet();

        for (ServiceInstance serviceInstance : keySet) {
            Double d = instanceWeightMap.get(serviceInstance);
            long round = Math.round(d);
            for (int i = 0; i < round; i++) {
                list.add(serviceInstance);
            }
        }

        ServiceInstance serviceInstance = list.get(offset % list.size());
        offset++;
        return serviceInstance;
    }

    private CokeServiceInstance toCokeInstance(ServiceInstance instance) {
        CokeServiceInstance cokeServiceInstance = new CokeServiceInstance();
        BeanUtil.copyProperties(instance, cokeServiceInstance);
        cokeServiceInstance.setWeight(instance.getMetadata().get("nacos.weight"));
        return cokeServiceInstance;
    }
}
