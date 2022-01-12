package org.needcoke.connect.receiver;

import io.netty.channel.ChannelHandlerContext;
import org.needcoke.connect.processor.AbstractRequestProcessor;
import org.needcoke.connect.processor.IRequestProcessor;
import org.needcoke.connect.protocol.CokeRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * 抽象接收器
 *
 * @author Gilgamesh
 * @since V1.0
 */
public abstract class AbstractReceiver {

    /* 请求拦截器 */
    protected List<IRequestProcessor> requestProcessors;

    protected AbstractReceiver() {
        this.requestProcessors = new ArrayList<>(4);
    }

    /* 添加通用拦截器 */
    public void addRequestProcessors(IRequestProcessor requestProcessor) {
        this.requestProcessors.add(requestProcessor);
    }

    /**
     * 添加特定的请求拦截器
     *
     * @param requestType      请求类型
     * @param requestProcessor 请求拦截器
     * @since V1.0
     */
    public void addSpecificRequestProcessors(String requestType, IRequestProcessor requestProcessor) {
        requestProcessors.add(new AbstractRequestProcessor() {
            @Override
            public void handle(ChannelHandlerContext ctx, CokeRequest cokeRequest) {
                if (requestType.equals(cokeRequest.getRequestType())) {
                    requestProcessor.handle(ctx, cokeRequest);
                }
            }
        });
    }

    /* 启动接收端 */
    public abstract void start();

}
