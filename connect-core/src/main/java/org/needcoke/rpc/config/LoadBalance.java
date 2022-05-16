package org.needcoke.rpc.config;

import cn.hutool.core.collection.CollUtil;
import org.needcoke.rpc.common.constant.ConnectionExceptionEnum;
import org.needcoke.rpc.common.exception.CokeConnectException;
import org.springframework.cloud.client.ServiceInstance;

import java.util.List;

/**
 * @author Gilgamesh
 * @date 2022/4/2
 */
public abstract class LoadBalance {

    /**
     * 从实例列表选择一个远程服务
     * <p>
     * TODO 扩展负载均衡策略
     *
     * @param instances 从注册中心获取的实例列表
     * @return 某一个特定的实例
     */
    public ServiceInstance choose(String serviceId, List<ServiceInstance> instances) {

        if (CollUtil.isNotEmpty(instances)) {
            return loadBalanceChoose(serviceId,instances);
        }
        throw new CokeConnectException(ConnectionExceptionEnum.CAN_NOT_FIND_SUCH_INSTANCE);
    }

    public abstract ServiceInstance loadBalanceChoose(String serviceId, List<ServiceInstance> instances) ;


}
