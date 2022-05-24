package org.needcoke.rpc.invoker;

import com.alibaba.fastjson.JSONObject;
import com.ejlchina.okhttps.HttpResult;
import lombok.Data;
import lombok.experimental.Accessors;

import java.nio.charset.StandardCharsets;

/**
 * 调用器的调用结果
 */

@Data
@Accessors(chain = true)
public class InvokeResult<T> {

    /**
     * 状态
     */
    private int status;

    /**
     * 响应时间
     */
    private long time;

    private T body;

    public static InvokeResult of(HttpResult httpResult) {
        return new InvokeResult<>().setStatus(httpResult.getStatus())
                .setTime(httpResult.getTask().httpClient().totalTimeoutMillis())
                .setBody(httpResult.getBody());
    }

    public static  <R> InvokeResult<R> of(HttpResult httpResult, Class<R> clz) {
        return new InvokeResult<R>().setStatus(httpResult.getStatus())
                .setTime(httpResult.getTask().httpClient().totalTimeoutMillis())
                .setBody(JSONObject.parseObject(httpResult.getBody().toString(), clz));
    }

    public String toJson(){
        return JSONObject.toJSONString(this);
    }
}
