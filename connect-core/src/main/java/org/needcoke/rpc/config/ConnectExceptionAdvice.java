package org.needcoke.rpc.config;

import org.connect.rpc.link.tracking.config.LinkTrackingContextHolder;
import org.needcoke.rpc.common.exception.CokeConnectException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * 全局异常处理
 *
 * @author yanming
 * @date 2022/5/12
 */
@RestControllerAdvice
public class ConnectExceptionAdvice {


    public Map<String, String> connectExceptionHandle(CokeConnectException exception) {
        Map<String, String> ret = new HashMap<>();
        ret.put("message", exception.getMessage());
        ret.put("note", exception.getNote());
        ret.put("errorCode", exception.getErrorCode());
        return ret;
    }

    @ExceptionHandler(value = Exception.class)
    public Map<String, String> exceptionHandle(Exception exception) {
        if(exception instanceof CokeConnectException){
            return connectExceptionHandle((CokeConnectException)exception);
        }
        Map<String, String> ret = new HashMap<>();
        ret.put("message", exception.getMessage());
        ret.put("note", exception.getLocalizedMessage());
        ret.put("errorCode", "502");
        return ret;
    }
}
