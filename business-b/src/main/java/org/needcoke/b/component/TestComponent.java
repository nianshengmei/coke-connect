package org.needcoke.b.component;

import org.needcoke.rpc.loadBalance.RoundRobinLoadBalance;
import org.needcoke.rpc.loadBalance.WeightedResponseTimeBalance;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

/**
 * @author Gilgamesh
 * @date 2022/4/2
 */
@Component
public class TestComponent {


    @Bean
    @Primary
    public WeightedResponseTimeBalance weightedResponseTimeBalance(){
        return new WeightedResponseTimeBalance();
    }


}
