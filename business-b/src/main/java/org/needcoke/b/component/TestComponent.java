package org.needcoke.b.component;

import org.needcoke.rpc.common.enums.RpcTypeEnum;
import org.needcoke.rpc.invoker.SmartSocketInvoker;
import org.needcoke.rpc.loadBalance.RoundRobinLoadBalance;
import org.needcoke.rpc.loadBalance.WeightedResponseTimeBalance;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author Gilgamesh
 * @date 2022/4/2
 */
@Component
public class TestComponent {

//    @Bean
//    @Primary
//    public WeightedResponseTimeBalance weightedResponseTimeBalance(){
//        return new WeightedResponseTimeBalance();
//    }

    @Bean
    public SmartSocketInvoker smartSocketInvoker(RpcTypeEnum rpcTypeEnum){
        return new SmartSocketInvoker(rpcTypeEnum);
    }

}
