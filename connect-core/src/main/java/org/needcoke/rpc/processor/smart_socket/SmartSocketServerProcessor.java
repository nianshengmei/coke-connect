package org.needcoke.rpc.processor.smart_socket;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.needcoke.rpc.codec.CokeRequest;
import org.needcoke.rpc.common.constant.ConnectConstant;
import org.needcoke.rpc.common.enums.ConnectRequestEnum;
import org.needcoke.rpc.common.enums.ConnectionExceptionEnum;
import org.needcoke.rpc.common.exception.CokeConnectException;
import org.needcoke.rpc.config.RequestIdContextHolder;
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
        String COKE_REQUEST_ID_HEADER_ID_NAME = "COKE_REQUEST_ID";
        if (request.getHeaders().containsKey(COKE_REQUEST_ID_HEADER_ID_NAME)) {
            RequestIdContextHolder.setRequestId(request.getHeaders().get(COKE_REQUEST_ID_HEADER_ID_NAME));
        } else {
            RequestIdContextHolder.setRequestId(RequestIdContextHolder.newRequestId());
        }
        //TODO 将该段代码抽出成公共的
        if (ConnectRequestEnum.INTERNAL_REQUEST == request.getRequestType()) {
            String beanName = request.getBeanName();
            String methodName = request.getMethodName();
            Map<String, Object> params = request.getParams();
            log.info("execute smart socket requestId = {} , -- beanName : {} , methodName : {} , param : {}", RequestIdContextHolder.getRequestId(), beanName, methodName, JSONObject.toJSONString(params));
            Method method = SpringContextUtils.getMethod(beanName, methodName);
            if (null == method) {
                log.error(ConnectionExceptionEnum.BEAN_WITHOUT_METHOD.logStatement("beanName {} , methodName {} , requestId = {}"), beanName, methodName, RequestIdContextHolder.getRequestId());
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
