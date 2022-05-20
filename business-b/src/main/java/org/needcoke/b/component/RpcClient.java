package org.needcoke.b.component;

import org.needcoke.rpc.annotation.Rpc;

/**
 * @author Gilgamesh
 * @date 2022/4/2
 */
@Rpc(serviceId = "bussiness-a")
public class RpcClient {

    @Rpc(beanName = "config",callName = "haha")
    String vvd(){
        return "vvd";
    }

    @Rpc(beanName = "config",callName = "hahha2")
    String vvc(String a){
        return "vvc"+a;
    }
}
