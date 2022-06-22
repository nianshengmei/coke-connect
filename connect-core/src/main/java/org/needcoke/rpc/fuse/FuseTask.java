package org.needcoke.rpc.fuse;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class FuseTask implements Delayed {
    private static long currentTime = System.currentTimeMillis();
    protected final String taskName;
    protected final int timeCost;
    protected final long scheduleTime;

    protected static final AtomicInteger taskCount = new AtomicInteger(0);

    public FuseTask(String taskName, int timeCost) {
        this.taskName = taskName;
        this.timeCost = timeCost;
        taskCount.incrementAndGet();
        currentTime += 1000 + (long) (Math.random() * 1000);
        scheduleTime = currentTime;
    }
    @Override
    public long getDelay(TimeUnit unit) {
        long expirationTime = scheduleTime - System.currentTimeMillis();
        return unit.convert(expirationTime, TimeUnit.MILLISECONDS);
    }

    @Override
    public int compareTo(Delayed o) {
        return (int) (this.scheduleTime - ((FuseTask) o).scheduleTime);
    }

    public void execTask() {
        long startTime = System.currentTimeMillis();
        try {
            Thread.sleep(timeCost);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
