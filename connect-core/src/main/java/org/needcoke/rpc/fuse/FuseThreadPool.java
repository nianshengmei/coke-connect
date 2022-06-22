package org.needcoke.rpc.fuse;


import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.needcoke.rpc.config.FuseConfig;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.*;

@Component
public class FuseThreadPool {

    @Resource
    private FuseConfig fuseConfig;

    /**
     * 自定义线程名称,方便的出错的时候溯源
     */
    private final ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("coke-connect-Fuse-pool-%d").build();





    /**
     * corePoolSize    线程池核心池的大小
     * maximumPoolSize 线程池中允许的最大线程数量
     * keepAliveTime   当线程数大于核心时，此为终止前多余的空闲线程等待新任务的最长时间
     * unit            keepAliveTime 的时间单位
     * workQueue       用来储存等待执行任务的队列
     * threadFactory   创建线程的工厂类
     * handler         拒绝策略类,当线程池数量达到上线并且workQueue队列长度达到上限时就需要对到来的任务做拒绝处理
     */
    private ExecutorService service = new ThreadPoolExecutor(
            fuseConfig.getCoreThreadPoolSize(),
            fuseConfig.getMaximumPoolSize(),
            fuseConfig.getKeepAliveTime(),
            TimeUnit.MILLISECONDS,
            new ArrayBlockingQueue<>(fuseConfig.getPoolCapacity()),
            namedThreadFactory,
            new ThreadPoolExecutor.AbortPolicy()
    );

    /**
     * 获取线程池
     * @return 线程池
     */
    public ExecutorService getExecutorService() {
        return service;
    }

    /**
     * 使用线程池创建线程并异步执行任务
     * @param r 任务
     */
    public void newTask(Runnable r) {
        service.execute(r);
    }

    /**
     * 使用线程池创建线程并异步执行任务
     * @param r 任务
     */
    public Future<?> newTask(Callable<?> r){
        return service.submit(r);
    }
}
