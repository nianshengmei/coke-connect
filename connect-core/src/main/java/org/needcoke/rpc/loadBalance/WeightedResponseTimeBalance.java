package org.needcoke.rpc.loadBalance;

import cn.hutool.core.bean.BeanUtil;
import org.needcoke.rpc.CokeServiceInstance;
import org.springframework.cloud.client.ServiceInstance;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
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
        List<Double> weightList = new CopyOnWriteArrayList<>();
        List<Double> wList = new ArrayList<>();
        for (ServiceInstance instance : instances) {
//            instanceWeightMap.put(instance, Double.parseDouble(weightMap.get(instance.getHost() + ":" + instance.getPort())));
            weightList.add(Double.parseDouble(weightMap.get(instance.getHost() + ":" + instance.getPort())));
            wList.add(Double.parseDouble(weightMap.get(instance.getHost() + ":" + instance.getPort())));
        }

        Double sum = weightList.stream().reduce(Double::sum).orElse(0.0);
        int index = 0;
        int p = (int) (offset%sum);
        Double weight = 0.0;
        for (Double d : weightList) {
            weight = Collections.max(weightList);
            if (p>=weight) {
                p -= weight;
                weightList.remove(weight);
            }else{
                index = wList.indexOf(weight);
                break;
            }
        }
        offset++;
        return instances.get(index);
    }

    private CokeServiceInstance toCokeInstance(ServiceInstance instance) {
        CokeServiceInstance cokeServiceInstance = new CokeServiceInstance();
        BeanUtil.copyProperties(instance, cokeServiceInstance);
        cokeServiceInstance.setWeight(instance.getMetadata().get("nacos.weight"));
        return cokeServiceInstance;
    }
}
