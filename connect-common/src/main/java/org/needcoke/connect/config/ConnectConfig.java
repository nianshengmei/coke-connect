package org.needcoke.connect.config;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 关于连接的配置
 **/
public class ConnectConfig {

    public static Map<String,Object> mainConfig;

    static {
        mainConfig = new ConcurrentHashMap<String, Object>();
        /* 系统所使用的线程池-核心线程数 */
        mainConfig.put("COREPOOLSIZE",3);
        /* 系统所使用的线程池-最大线程数 */
        mainConfig.put("MAXPOOLSIZE",6);
        /* 系统所使用的线程池-保活时间 */
        mainConfig.put("ALIVETIME",100);
        /* 系统所使用的线程池-时间单位 */
        mainConfig.put("TIMEUNIT", TimeUnit.SECONDS);
        /* 系统所使用的线程池-阻塞队列大小 */
        mainConfig.put("MAXBLOCKINGQUEUESIZE",5);
    }
}
