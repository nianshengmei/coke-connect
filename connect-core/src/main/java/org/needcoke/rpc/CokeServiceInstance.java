package org.needcoke.rpc;


import lombok.Data;
import org.springframework.cloud.client.ServiceInstance;

import java.net.URI;
import java.util.Map;

@Data
public class CokeServiceInstance implements ServiceInstance {

    private String serviceId;

    private String host;

    private int port;

    private boolean secure;

    private URI uri;

    private Map<String, String> metadata;

    private String weight;

}
