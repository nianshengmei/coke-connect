package org.needcoke.rpc.controller;

import com.alibaba.fastjson.JSONObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.needcoke.rpc.common.constant.ConnectConstant;
import org.needcoke.rpc.common.enums.ConnectionExceptionEnum;
import org.needcoke.rpc.common.exception.CokeConnectException;
import org.needcoke.rpc.utils.SpringContextUtils;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;

/**
 * @author Gilgamesh
 * @date 2022/4/2
 */
@RestController
@Slf4j
@RequestMapping("coke/connect")
@RequiredArgsConstructor
public class RpcController {

    @PostMapping("execute")
    public Object execute(@RequestParam String beanName,
                          @RequestParam String methodName,
                          @RequestBody Map<String, Object> params) {
        log.info("execute http -- beanName : {} , methodName : {} , param : {}",beanName,methodName, JSONObject.toJSONString(params));
        Method method = SpringContextUtils.getMethod(beanName, methodName);
        if (null == method) {
            log.error(ConnectionExceptionEnum.BEAN_WITHOUT_METHOD.logStatement(ConnectConstant.EXECUTE_RELATIVE_PATH));
            throw new CokeConnectException(ConnectionExceptionEnum.BEAN_WITHOUT_METHOD);
        }
        Object bean = SpringContextUtils.getBean(beanName);
        Collection<Object> values = params.values();
        try {
            return method.invoke(bean, values.toArray());
        } catch (Exception e) {
            log.error(ConnectionExceptionEnum.INVOKE_METHOD_ERROR.logStatement(ConnectConstant.EXECUTE_RELATIVE_PATH));
            throw new CokeConnectException(ConnectionExceptionEnum.INVOKE_METHOD_ERROR);
        }
    }
}
