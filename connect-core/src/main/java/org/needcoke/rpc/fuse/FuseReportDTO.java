package org.needcoke.rpc.fuse;

import lombok.Data;
import org.needcoke.rpc.config.FuseConfig;

import java.io.Serializable;

@Data
public class FuseReportDTO implements Serializable {

    /**
     * 未释放的锁的数量
     */
    private int unParkLockNumber;

    /**
     * 阻塞住的线程数量
     */
    private int parkThreadNumber;

    /**
     *  未解锁的请求数量
     */
    private int unParkNumber;

    /**
     *  关于熔断的一些基础配置
     */
    private FuseConfig fuseConfig;

    /**
     *  被熔断掉的任务数量
     */
    private int fuseRequestNumber;

    /**
     * 实时线程池线程数
     */
    private int poolSize;


}
