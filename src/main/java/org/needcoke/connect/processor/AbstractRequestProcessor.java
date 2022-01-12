package org.needcoke.connect.processor;

import io.netty.channel.ChannelHandlerContext;
import org.needcoke.connect.advice.Advice;
import org.needcoke.connect.protocol.CokeRequest;

import java.util.List;

public abstract class AbstractRequestProcessor implements IRequestProcessor{

    /* first advice */
    protected Advice firstAdvice;

    /* last advice */
    protected Advice lastAdvice;

    /* 中间advice */
    protected List<Advice> middleAdvices;

    /* 执行通知 */
    public void doAdvice() {
        if (null != firstAdvice) firstAdvice.handle();
        for (Advice advice : middleAdvices) {
            advice.handle();
        }
        if (null != lastAdvice) lastAdvice.handle();
    }

    @Override
    public abstract void handle(ChannelHandlerContext ctx, CokeRequest cokeRequest);
}
