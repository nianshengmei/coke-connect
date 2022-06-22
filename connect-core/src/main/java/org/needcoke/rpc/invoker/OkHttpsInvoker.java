package org.needcoke.rpc.invoker;

import com.ejlchina.okhttps.HTTP;
import com.ejlchina.okhttps.HttpResult;
import com.ejlchina.okhttps.SHttpTask;
import com.ejlchina.okhttps.jackson.JacksonMsgConvertor;
import lombok.NoArgsConstructor;
import org.connect.rpc.link.tracking.util.TrackingUtil;
import org.needcoke.rpc.common.constant.ConnectConstant;
import org.needcoke.rpc.common.enums.HttpContentTypeEnum;
import org.needcoke.rpc.common.enums.RpcTypeEnum;
import org.needcoke.rpc.net.Connector;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.Map;

/**
 * 采用okHttps的跨服务调用器
 */
@NoArgsConstructor
public class OkHttpsInvoker extends ConnectInvoker {


    public OkHttpsInvoker(RpcTypeEnum rpcTypeEnum) {
        rpcTypeEnum = RpcTypeEnum.okHttp3;
    }

    @Override
    public InvokeResult execute(Connector connector, ServiceInstance instance, String beanName, String methodName, Map<String, Object> params) {
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        SHttpTask sHttpTask = HTTP.builder().addMsgConvertor(new JacksonMsgConvertor()).build()
                .sync(instance.getUri() + ConnectConstant.EXECUTE_RELATIVE_PATH)
                .bodyType(HttpContentTypeEnum.JSON.getValue())
                .addBodyPara(params)
                .addHeader(TrackingUtil.headerKey(), TrackingUtil.headerValue())
                .addUrlPara(ConnectConstant.BEAN_NAME, beanName)
                .addUrlPara(ConnectConstant.METHOD_NAME, methodName);
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String nextElement = headerNames.nextElement();
            String header = request.getHeader(nextElement);
            sHttpTask.addHeader(nextElement, header);
        }
        HttpResult result = sHttpTask
                .post();
        handleResult(instance.getServiceId(), beanName, methodName, params, result);
        return InvokeResult.of(result);
    }
}
