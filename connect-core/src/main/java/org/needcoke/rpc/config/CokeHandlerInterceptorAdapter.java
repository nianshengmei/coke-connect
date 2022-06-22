package org.needcoke.rpc.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.RequestFacade;
import org.connect.rpc.link.tracking.config.LinkTrackingContextHolder;
import org.connect.rpc.link.tracking.net.LinkTracking;
import org.connect.rpc.link.tracking.util.TrackingUtil;
import org.needcoke.rpc.utils.SpringContextUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Slf4j
public class CokeHandlerInterceptorAdapter implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        ServerConfig bean = SpringContextUtils.getBean(ServerConfig.class);
        int port = bean.getMvcPort();
        TrackingUtil.preHttp(request,response,handler,port);
        LinkTracking linkTracking = LinkTrackingContextHolder.getLinkTracking();
        if(request instanceof RequestFacade){
            String requestURI = request.getRequestURI();
            linkTracking.addMataData("http path",requestURI);
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        LinkTrackingContextHolder.clear();
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
