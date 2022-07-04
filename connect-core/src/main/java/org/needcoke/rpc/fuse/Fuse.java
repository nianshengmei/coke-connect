package org.needcoke.rpc.fuse;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
public class Fuse extends Thread {

    private final ReentrantLock lock = new ReentrantLock();

    private long timeout;

    private TimeUnit unit;

    /* 锁字段 */
    private final Object lockField;

    /* 超时需要唤醒的线程 */
    private final Thread unParkThread;

    private static final Map<Object, Thread> unParkThreadMap = new ConcurrentHashMap<>();
    private static final Map<Object, Boolean> unParkMap = new ConcurrentHashMap<>();

    private static ArrayBlockingQueue<Runnable> blockQueue;

    public static void setBlockQueue(ArrayBlockingQueue<Runnable> blockQueue) {
        Fuse.blockQueue = blockQueue;
    }

    public Fuse(long timeout, TimeUnit unit, Object lockField) {
        this.timeout = timeout;
        this.unit = unit;
        this.lockField = lockField;
        this.unParkThread = Thread.currentThread();
        FuseContext.lockPool.put(lockField, lock);
        unParkMap.put(lockField, true);
        unParkThreadMap.put(lockField, unParkThread);
    }

    public Fuse(long timeout, Object lockField) {
        this.timeout = timeout;
        this.unit = TimeUnit.MILLISECONDS;
        this.lockField = lockField;
        this.unParkThread = Thread.currentThread();
        FuseContext.lockPool.put(lockField, lock);
        unParkMap.put(lockField, true);
        unParkThreadMap.put(lockField, unParkThread);
    }


    @Override
    public void run() {
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
                FuseContext.lockPool.remove(lockField);
                unParkMap.remove(lockField);
                unParkThreadMap.remove(lockField);
            }
        }

    }

    public static boolean unPark(Object lockField) {
        Lock tLock = FuseContext.lockPool.get(lockField);
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
                FuseContext.lockPool.remove(lockField);
            }
        }
        return false;
    }
}
