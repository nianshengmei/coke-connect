package org.needcoke.rpc.fuse;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
public class Fuse implements Runnable {

    private Lock lock = new ReentrantLock();

    private long timeout;

    private TimeUnit unit;

    /* 锁字段 */
    private final Object lockField;

    /* 超时需要唤醒的线程 */
    private final Thread unParkThread;

    private static final Map<Object, Thread> unParkThreadMap = new ConcurrentHashMap<>();
    private static final Map<Object, Boolean> unParkMap = new ConcurrentHashMap<>();

    private static final Map<Object, Lock> lockMap = new ConcurrentHashMap<>();

    public Fuse(long timeout, TimeUnit unit, Object lockField) {
        this.timeout = timeout;
        this.unit = unit;
        this.lockField = lockField;
        this.unParkThread = Thread.currentThread();
        lockMap.put(lockField, lock);
    }

    public Fuse(long timeout, Object lockField) {
        this.timeout = timeout;
        this.unit = TimeUnit.MILLISECONDS;
        this.lockField = lockField;
        this.unParkThread = Thread.currentThread();
        lockMap.put(lockField, lock);
    }


    @Override
    public void run() {
        unParkMap.put(lockField, true);
        unParkThreadMap.put(lockField, unParkThread);
        try {
            unit.sleep(timeout);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        if (lock.tryLock()) {
            lock.lock();
            try {
                if (unParkMap.containsKey(lockField)) {
                    LockSupport.unpark(unParkThread);
                    log.warn("coke connect fuse lockField = {},fuseTimeOut = {}", lockField, timeout);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                lock.unlock();
                unParkMap.remove(lockField);
                unParkThreadMap.remove(lockField);
            }
        }

    }

    public static boolean unPark(Object lockField) {
        Lock tLock = lockMap.get(lockField);
        if(tLock.tryLock()) {
            tLock.lock();
            try {
                if (unParkMap.containsKey(lockField)) {
                    LockSupport.unpark(unParkThreadMap.get(lockField));
                    unParkMap.remove(lockField);
                    return true;
                }
            }catch (Exception e){
                throw new RuntimeException(e);
            }finally {
                tLock.unlock();
            }
        }
        return false;
    }
}
