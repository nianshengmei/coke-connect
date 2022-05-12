package org.needcoke.rpc.controller;

import lombok.RequiredArgsConstructor;
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
@RequestMapping("coke/connect")
@RequiredArgsConstructor
public class RpcController {

    @PostMapping("execute")
    public Object execute(@RequestParam String beanName,
                          @RequestParam String methodName,
                          @RequestBody Map<String, Object> params) {
        Method method = SpringContextUtils.getMethod(beanName, methodName);
        Object bean = SpringContextUtils.getBean(beanName);
        Collection<Object> values = params.values();
        try {
            return method.invoke(bean, values.toArray());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
