package org.needcoke.rpc.net;

import org.needcoke.rpc.invoker.ConnectInvoker;
import org.needcoke.rpc.loadBalance.LoadBalance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class ConnectorFactory {

    private DiscoveryClient dc;

    private ConnectInvoker ci;

    private LoadBalance lb;

    @Autowired
    public void setLb(LoadBalance lb) {
        this.lb = lb;
    }
    @Autowired
    @Lazy
    public void setCi(ConnectInvoker ci) {
        this.ci = ci;
    }

    @Autowired
    public void setDc(DiscoveryClient dc) {
        this.dc = dc;
    }

    private final ConcurrentHashMap<String, Connector> connectorMap = new ConcurrentHashMap<>();

    /**
     * 创建或者获取连接器
     */
    //建立连接并记录
    public Connector connector(String serviceId){
        if (connectorMap.containsKey(serviceId)) {
            return connectorMap.get(serviceId);
        }
        Connector connector = new Connector(serviceId, dc, ci, lb);
        connectorMap.put(serviceId,connector);
        return connector;
    }

}
