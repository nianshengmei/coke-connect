package org.connect.rpc.link.tracking.net;

import cn.hutool.core.collection.CollUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@Data
@EqualsAndHashCode
@Accessors(chain = true)
public class LinkTracking {
    private String ip;

    private int port;

    private Map<String,String> metaData;

    private String requestId;

    private Integer index;

    private String serviceId;

    private long startTime;

    private static final AtomicLong requestIdMaker = new AtomicLong(1);

    public LinkTracking(int port) {
       this.port = port;
       changeIp();
       requestId = ""+requestIdMaker.getAndAdd(1);
       this.index = 1;
    }

    public void changeIp(){
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            String ip = localHost.getHostAddress();
            this.ip = ip;
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public void addMataData(String key,String value){
        if(CollUtil.isEmpty(metaData)){
            this.metaData = new HashMap<>();
        }
        metaData.put(key,value);
    }
}
