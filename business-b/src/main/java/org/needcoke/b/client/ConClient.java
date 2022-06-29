package org.needcoke.b.client;

import org.needcoke.rpc.annotation.RpcClient;

@RpcClient(name = "conClient2" , serviceId = "business-c" ,beanName = "cCon")
public interface ConClient extends org.needcoke.rpc.proxy.RpcClient {

    Object cTest2(String word);
}
