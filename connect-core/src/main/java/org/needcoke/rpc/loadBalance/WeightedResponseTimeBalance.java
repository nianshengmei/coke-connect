package org.needcoke.rpc.loadBalance;

import cn.hutool.core.bean.BeanUtil;
import org.needcoke.rpc.CokeServiceInstance;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@ConditionalOnMissingBean(LoadBalance.class)
@Component
public class WeightedResponseTimeBalance extends LoadBalance {

    private final ConcurrentHashMap<String, Integer> loadBalanceMap = new ConcurrentHashMap<>();

    @Override
    public ServiceInstance loadBalanceChoose(String serviceId, List<ServiceInstance> instances) {
        Integer offset = loadBalanceMap.putIfAbsent(serviceId, 0);
        /*
         * 0、1、2
         * 3、4
         * 5
         * 6、7、8
         * 9、10
         * 11
         * */
        List<CokeServiceInstance> cokeServiceInstanceList = instances.stream().map(this::toCokeInstance).collect(Collectors.toList());
        Map<URI, String> weightMap = cokeServiceInstanceList.stream().collect(Collectors.toMap(CokeServiceInstance::getUri, CokeServiceInstance::getWeight));
        Map<ServiceInstance,Integer> instanceWeightMap = new HashMap<>();
        for (ServiceInstance instance : instances) {
            instanceWeightMap.put(instance,Integer.parseInt(weightMap.get(instance.getUri())));
        }
        instanceWeightMap = sortMap(instanceWeightMap);
        Integer weight = 0;
        for (Map.Entry<ServiceInstance, Integer> entry : instanceWeightMap.entrySet()) {
            ServiceInstance mapKey = entry.getKey();

            Integer mapValue = entry.getValue();
            if(offset < mapValue + weight){
                if(offset + 1 == weight){
                    loadBalanceMap.put(serviceId, 0);
                }else {
                    loadBalanceMap.put(serviceId, offset + 1);
                }
                return mapKey;
            }else{
                weight += mapValue;
            }
        }
        return null;
    }

    private CokeServiceInstance toCokeInstance(ServiceInstance instance){
        CokeServiceInstance cokeServiceInstance = new CokeServiceInstance();
        BeanUtil.copyProperties(instance,cokeServiceInstance);
        cokeServiceInstance.setWeight(instance.getMetadata().get("nacos.weight"));
        return cokeServiceInstance;
    }

    private static Map<ServiceInstance, Integer> sortMap(Map<ServiceInstance, Integer> map) {
        //利用Map的entrySet方法，转化为list进行排序
        List<Map.Entry<ServiceInstance, Integer>> entryList = new ArrayList<>(map.entrySet());
        //利用Collections的sort方法对list排序
        Collections.sort(entryList, new Comparator<Map.Entry<ServiceInstance, Integer>>() {
            @Override
            public int compare(Map.Entry<ServiceInstance, Integer> o1, Map.Entry<ServiceInstance, Integer> o2) {
                //正序排列，倒序反过来
                return (o1.getValue() - o2.getValue())*(-1);
            }
        });
        //遍历排序好的list，一定要放进LinkedHashMap，因为只有LinkedHashMap是根据插入顺序进行存储
        LinkedHashMap<ServiceInstance, Integer> linkedHashMap = new LinkedHashMap<ServiceInstance, Integer>();
        for (Map.Entry<ServiceInstance,Integer> e : entryList
        ) {
            linkedHashMap.put(e.getKey(),e.getValue());
        }
        return linkedHashMap;
    }
}
