package org.needcoke.rpc.config;

import lombok.Getter;
import org.needcoke.rpc.fuse.FuseTask;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.concurrent.DelayQueue;

@Getter
@Configuration
public class FuseConfig {

    @Value("${coke.fuse.timeout:2000}")
    private long fuseTimeOut;

    @Value("${coke.fuse.pool.coreThreadPoolSize:2}")
    private int coreThreadPoolSize;

    @Value("${coke.fuse.pool.maximumPoolSize:4}")
    private int maximumPoolSize;

    @Value("${coke.fuse.pool.keepAliveTime:2000}")
    private long keepAliveTime;

    @Value("${coke.fuse.pool.poolCapacity:9999}")
    private int poolCapacity;

    @Bean
    public DelayQueue<FuseTask> fuseTaskDelayQueue(){
        return new DelayQueue<>();
    }

}
