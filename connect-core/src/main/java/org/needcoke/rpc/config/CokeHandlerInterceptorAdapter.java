package org.needcoke.rpc.config;

import cn.hutool.core.util.StrUtil;
import org.needcoke.rpc.common.constant.ConnectConstant;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class CokeHandlerInterceptorAdapter extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String cokeRequestId = request.getHeader(ConnectConstant.COKE_REQUEST_ID_HEADER_ID_NAME);
        if(StrUtil.isEmpty(cokeRequestId)){
            RequestIdContextHolder.setRequestId(RequestIdContextHolder.newRequestId());
        }else{
            RequestIdContextHolder.setRequestId(cokeRequestId);
        }
        return super.preHandle(request, response, handler);
    }
}
