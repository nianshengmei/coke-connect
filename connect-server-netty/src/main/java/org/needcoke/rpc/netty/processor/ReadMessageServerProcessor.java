package org.needcoke.rpc.netty.processor;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSONObject;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.connect.rpc.link.tracking.util.TrackingUtil;
import org.needcoke.rpc.codec.CokeRequest;
import org.needcoke.rpc.common.constant.ConnectConstant;
import org.needcoke.rpc.common.enums.ConnectRequestEnum;
import org.needcoke.rpc.common.enums.ConnectionExceptionEnum;
import org.needcoke.rpc.common.exception.CokeConnectException;
import org.needcoke.rpc.invoker.InvokeResult;
import org.needcoke.rpc.utils.SpringContextUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

@Slf4j
public class ReadMessageServerProcessor implements ReadMessageProcessor{

    @Override
    public void channelRead(ChannelHandlerContext ctx, CokeRequest request) {
        if(ConnectRequestEnum.INTERNAL_REQUEST == request.getRequestType()){
            String beanName = request.getBeanName();
            String methodName = request.getMethodName();
            Map<String, Object> params = request.getParams();
            log.info("execute smart socket linkTracking = {} , -- beanName : {} , methodName : {} , param : {}",
                    TrackingUtil.linkTrackingJsonStr(), beanName, methodName, JSONObject.toJSONString(params));
            Method method = SpringContextUtils.getMethod(beanName, methodName);
            if (null == method) {
                log.error(ConnectionExceptionEnum.BEAN_WITHOUT_METHOD.logStatement("beanName {} , methodName {} , linkTracking = {}"), beanName, methodName, TrackingUtil.linkTrackingJsonStr());
                throw new CokeConnectException(ConnectionExceptionEnum.BEAN_WITHOUT_METHOD);
            }
            Object bean = SpringContextUtils.getBean(beanName);
            try {
                Object invoke = null;
                if (CollUtil.isEmpty(params)) {
                    invoke = method.invoke(bean);
                } else {
                    invoke = method.invoke(bean, params.values().toArray());
                }
                InvokeResult invokeResult = new InvokeResult().setBody(invoke).setStatus(200).setTime(30L);
                ctx.writeAndFlush(request.setRequestType(ConnectRequestEnum.INTERNAL_RESPONSE)
                        .setResult(invokeResult)
                        .setCokeRequestId(TrackingUtil.getRequestId()));
            } catch (Exception e) {
                log.error(ConnectionExceptionEnum.INVOKE_METHOD_ERROR.logStatement(ConnectConstant.EXECUTE_RELATIVE_PATH));
                if(e instanceof InvocationTargetException){
                    ((InvocationTargetException) e).getTargetException().printStackTrace();
                }
                throw new CokeConnectException(ConnectionExceptionEnum.INVOKE_METHOD_ERROR, e);
            }
        }
    }
}
