package org.needcoke.rpc.processor.smart_socket;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.connect.rpc.link.tracking.common.CommonConstant;
import org.connect.rpc.link.tracking.config.LinkTrackingContextHolder;
import org.connect.rpc.link.tracking.net.LinkTracking;
import org.connect.rpc.link.tracking.util.TrackingUtil;
import org.needcoke.rpc.codec.CokeRequest;
import org.needcoke.rpc.common.constant.ConnectConstant;
import org.needcoke.rpc.common.enums.ConnectRequestEnum;
import org.needcoke.rpc.common.enums.ConnectionExceptionEnum;
import org.needcoke.rpc.common.exception.CokeConnectException;
import org.needcoke.rpc.config.ServerConfig;
import org.needcoke.rpc.invoker.InvokeResult;
import org.needcoke.rpc.utils.SpringContextUtils;
import org.smartboot.socket.StateMachineEnum;
import org.smartboot.socket.transport.AioSession;

import java.lang.reflect.Method;
import java.util.Map;

@Slf4j
public class SmartSocketServerProcessor extends SmartSocketMessageProcessor<CokeRequest> {
    @Override
    public void process(AioSession session, CokeRequest request) {
        String json = request.getHeader(CommonConstant.COKE_REQUEST_ID_HEADER_ID_NAME);
        int mvcPort = SpringContextUtils.getBean(ServerConfig.class).getMvcPort();
        if (StrUtil.isEmpty(json)) {
            LinkTracking linkTracking = new LinkTracking(mvcPort);
            linkTracking.setIndex(1);
            LinkTrackingContextHolder.setLinkTracking(linkTracking);
        } else {
            LinkTracking linkTracking = JSONUtil.toBean(json, LinkTracking.class);
            linkTracking.setIndex(linkTracking.getIndex() + 1);
            linkTracking.changeIp();
            linkTracking.setPort(mvcPort);
            LinkTrackingContextHolder.setLinkTracking(linkTracking);
        }
        //TODO 将该段代码抽出成公共的
        if (ConnectRequestEnum.INTERNAL_REQUEST == request.getRequestType()) {
            String beanName = request.getBeanName();
            String methodName = request.getMethodName();
            Map<String, Object> params = request.getParams();
            log.info("execute smart socket linkTracking = {} , -- beanName : {} , methodName : {} , param : {}", TrackingUtil.linkTrackingJsonStr(), beanName, methodName, JSONObject.toJSONString(params));
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
                this.response(session, request.setRequestType(ConnectRequestEnum.INTERNAL_RESPONSE).setResult(invokeResult));
            } catch (Exception e) {
                log.error(ConnectionExceptionEnum.INVOKE_METHOD_ERROR.logStatement(ConnectConstant.EXECUTE_RELATIVE_PATH));
                throw new CokeConnectException(ConnectionExceptionEnum.INVOKE_METHOD_ERROR, e);
            }
        }


    }

    @Override
    public void stateEvent(AioSession session, StateMachineEnum stateMachineEnum, Throwable throwable) {
        super.stateEvent(session, stateMachineEnum, throwable);
    }
}
