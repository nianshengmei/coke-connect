package org.needcoke.connect.handle;

import org.needcoke.connect.config.ConnectConfig;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
* 核心线程池
**/
public enum WorkHandlePool {

    MSG;

    private ExecutorService executorService;

    private ArrayBlockingQueue<Runnable> blockingDeque;

    WorkHandlePool() {
        /**
         * 初始化阻塞队列
         **/
        Map<String, Object> config = ConnectConfig.mainConfig;
        this.blockingDeque = new ArrayBlockingQueue<Runnable>(
                (Integer) config.get("MAXBLOCKINGQUEUESIZE")
        );
        /**
         * 初始化线程池
         **/
        this.executorService = new ThreadPoolExecutor(
                (Integer) config.get("COREPOOLSIZE"),
                (Integer) config.get("MAXPOOLSIZE"),
                (Integer)config.get("ALIVETIME"),
                (TimeUnit) config.get("TIMEUNIT"),
                blockingDeque);
    }

    /**
     * 获取线程池方法
     **/
    public  static ExecutorService getExecutorService() {
        return MSG.executorService;
    }
}
