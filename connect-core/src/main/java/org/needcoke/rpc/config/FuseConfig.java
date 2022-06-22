package org.needcoke.rpc.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FuseConfig {

    @Value("${coke.fuse.timeout}")
    private long fuseTimeOut;

}
