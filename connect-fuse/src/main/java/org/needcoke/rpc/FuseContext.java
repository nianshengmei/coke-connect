package org.needcoke.rpc;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

public class FuseContext {

    /**
     *  拒绝策略锁池
     */
    public static final Map<Object, ReentrantLock> lockPool = new ConcurrentHashMap<>();

    public static AtomicInteger fuse2StartNumber = new AtomicInteger(0);

    public static AtomicInteger fuse2EndNumber = new AtomicInteger(0);
}
