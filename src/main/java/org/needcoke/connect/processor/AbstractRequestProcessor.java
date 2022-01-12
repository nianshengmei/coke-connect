package org.needcoke.connect.processor;

import org.needcoke.connect.advice.Advice;

import java.util.List;

public abstract class AbstractRequestProcessor {

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


}
