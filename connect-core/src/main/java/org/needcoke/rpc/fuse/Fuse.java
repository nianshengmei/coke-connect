package org.needcoke.rpc.fuse;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

@Slf4j
public class Fuse implements Runnable{

    private long timeout;

    private TimeUnit unit;

    /* 锁字段 */
    private final Object lockField;

    /* 超时需要唤醒的线程 */
    private Thread unParkThread;

    private static Map<Object,Thread> unParkThreadMap = new ConcurrentHashMap<>();

    private static Map<Object,Boolean> unParkMap = new ConcurrentHashMap<>();

    public Fuse(long timeout, TimeUnit unit, Object lockField) {
        this.timeout = timeout;
        this.unit = unit;
        this.lockField = lockField;
        this.unParkThread = Thread.currentThread();
    }

    public Fuse(long timeout, Object lockField) {
        this.timeout = timeout;
        this.unit = TimeUnit.MILLISECONDS;
        this.lockField = lockField;
        this.unParkThread = Thread.currentThread();
    }



    @Override
    public void run() {
        unParkMap.put(lockField,true);
        unParkThreadMap.put(lockField,unParkThread);
        try {
            unit.sleep(timeout);
            synchronized (lockField){
                if(unParkMap.containsKey(lockField)){
                    unParkMap.remove(lockField);
                    LockSupport.unpark(unParkThread);
                    unParkThreadMap.remove(lockField);
                    log.warn("coke connect fuse lockField = {},fuseTimeOut = {}",lockField,timeout);
                }
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean unPark(final Object lockField){
        synchronized (lockField){
            if(unParkMap.containsKey(lockField)){
                LockSupport.unpark(unParkThreadMap.get(lockField));
                unParkMap.remove(lockField);
                return true;
            }
        }
        return false;
    }
}
