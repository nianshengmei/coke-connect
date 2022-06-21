package org.connect.rpc.link.tracking.config;

import lombok.experimental.UtilityClass;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@UtilityClass
public class LinkTrackingThreadPool {

    ExecutorService executorService = Executors.newFixedThreadPool(3);

    public void submit(Runnable task){
        executorService.submit(task);
    }

    public Future<?> submit(Callable<?> task){
        return executorService.submit(task);
    }
}
