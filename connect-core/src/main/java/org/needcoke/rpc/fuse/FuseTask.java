package org.needcoke.rpc.fuse;

import lombok.Getter;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * 熔断任务实体
 *
 * @author Gilgamesh
 * @since abandon
 */
@Getter
public class FuseTask implements Delayed {

    /* by warren: 请求id */
    private Long requestId;

    /* by warren: 需要释放的线程 */

    private Thread thread;

    /* by warren: 数据失效时间点 */
    private long time;

    public FuseTask(String requestId, Thread thread,long time) {
        this.requestId = Long.parseLong(requestId);
        this.thread = thread;
        this.time = System.currentTimeMillis()+time;
    }

    @Override
    public long getDelay(TimeUnit unit) {
        return unit.convert(this.time - System.currentTimeMillis(), TimeUnit.NANOSECONDS);
    }

    @Override
    public int compareTo(Delayed o) {
        if (o == this)  // compare zero if same object
            return 0;
        if (o instanceof FuseTask) {
            FuseTask x = (FuseTask) o;
            // 优先比较失效时间
            long diff = this.time - x.time;
            if (diff < 0)
                return -1;
            else if (diff > 0)
                return 1;
            else if (this.requestId < x.requestId)    // 剩余时间相同则比较序号
                return -1;
            else
                return 1;
        }
        // 一般不会执行到此处，除非元素不是MessageData类型
        long diff = this.getDelay(TimeUnit.NANOSECONDS) - o.getDelay(TimeUnit.NANOSECONDS);
        return (diff < 0) ? -1 : (diff > 0) ? 1 : 0;
    }
}
