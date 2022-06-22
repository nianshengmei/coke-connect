package org.needcoke.rpc.fuse;

import java.util.concurrent.BlockingQueue;

public class Fuse implements Runnable {

    private final BlockingQueue<FuseTask> queue;

    public Fuse(BlockingQueue<FuseTask> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        FuseTask task = null;
        try {
            while (true) {
                task = queue.take();
                task.execTask();
                FuseTask.taskCount.decrementAndGet();
            }
        } catch (InterruptedException e) {
        }
    }
}
