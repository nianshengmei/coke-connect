package org.needcoke.rpc.loadBalance;

import cn.hutool.core.util.RandomUtil;
import org.needcoke.rpc.config.LoadBalance;
import org.springframework.cloud.client.ServiceInstance;

import java.util.List;
import java.util.Random;

public class RandomLoadBalance extends LoadBalance {

    private Random random = RandomUtil.getRandom();
    @Override
    public ServiceInstance loadBalanceChoose(String serviceId, List<ServiceInstance> instances) {
        int size = instances.size();
        int i = random.nextInt(size);
        return instances.get(i);
    }
}
