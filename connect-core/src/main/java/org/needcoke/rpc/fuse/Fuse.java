package org.needcoke.rpc.fuse;

import java.util.concurrent.DelayQueue;
import java.util.concurrent.locks.LockSupport;

public class Fuse implements Runnable {

    private final DelayQueue<FuseTask> queue;

    public Fuse(DelayQueue<FuseTask> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        try {
            FuseTask fuseTask = queue.take();
            Thread thread = fuseTask.getThread();
            if(!thread.getState().equals(Thread.State.WAITING)) {
                LockSupport.unpark(thread);
            }
        } catch (InterruptedException e) {
        }
    }
}
