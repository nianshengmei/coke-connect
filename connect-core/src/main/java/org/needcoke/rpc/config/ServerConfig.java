package org.needcoke.rpc.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class ServerConfig {

    @Value("${coke.server.port:12001}")
    private int cokeServerPort;

    @Value("${coke.server.type:http}")
    private String serverType;
}
