package org.needcoke.b.component;

import org.needcoke.rpc.annotation.Rpc;
import org.springframework.stereotype.Component;

/**
 * @author Gilgamesh
 * @date 2022/4/2
 */
@Component
@Rpc(serviceId = "bussiness-a")
public class RpcClient {

    @Rpc(beanName = "config",callName = "haha")
    String vvd(){
     return  null;
    }

    @Rpc(beanName = "config",callName = "hahha2")
    String vvc(String a){
        return  null;
    }
}
