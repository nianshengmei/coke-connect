package org.needcoke.rpc.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.needcoke.rpc.fuse.FuseContext;
import org.needcoke.rpc.fuse.FuseThreadPool;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

/**
 * @author Gilgamesh
 * @date 2022/4/2
 */

@RestController
@Slf4j
@RequestMapping("coke/connect/api")
@RequiredArgsConstructor
public class ApiController {

    private final FuseThreadPool threadPool;

    /**
     * 熔断监控
     */
    @GetMapping(value = "/fuse/monitoring")
    public Map<String,String> fuseMonitoring(){
        Map<String,String> map = new HashMap<>();
        ThreadPoolExecutor pool = (ThreadPoolExecutor)threadPool.getExecutorService();
        int poolSize = pool.getPoolSize();
        long taskCount = pool.getTaskCount();
        int activeCount = pool.getActiveCount();
        long completedTaskCount = pool.getCompletedTaskCount();
        int corePoolSize = pool.getCorePoolSize();
        int largestPoolSize = pool.getLargestPoolSize();
        int maximumPoolSize = pool.getMaximumPoolSize();
        ThreadFactory threadFactory = pool.getThreadFactory();
        String threadFactoryName = threadFactory.getClass().getName();
        RejectedExecutionHandler rejectedExecutionHandler = pool.getRejectedExecutionHandler();
        String rejectedExecutionHandlerName = rejectedExecutionHandler.getClass().getName();
        BlockingQueue<Runnable> queue = pool.getQueue();
        int remainingCapacity = queue.remainingCapacity();
        map.put("poolSize",""+poolSize);
        map.put("taskCount",""+taskCount);
        map.put("activeCount",""+activeCount);
        map.put("remainingCapacity",""+remainingCapacity);
        map.put("completedTaskCount",""+completedTaskCount);

        map.put("corePoolSize",""+corePoolSize);
        map.put("largestPoolSize",""+largestPoolSize);
        map.put("maximumPoolSize",""+maximumPoolSize);
        map.put("threadFactoryName",threadFactoryName);
        map.put("rejectedExecutionHandlerName",rejectedExecutionHandlerName);
        return map;
    }


    @GetMapping(value = "/fuse2/monitoring")
    public Map<String,String> fuse2Monitoring(){
        Map<String,String> map = new HashMap<>();
        map.put("open num",""+ FuseContext.fuse2StartNumber.get());
        map.put("close num",""+ FuseContext.fuse2EndNumber.get());
        return map;
    }
}


