package org.needcoke.rpc.invoker;

import com.alibaba.fastjson.JSONObject;
import com.ejlchina.okhttps.HttpResult;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;

/**
 * 调用器的调用结果
 */

@Data
@Accessors(chain = true)
public class InvokeResult implements Serializable {

    /**
     * 状态
     */
    private int status;

    /**
     * 响应时间
     */
    private long time;

    private Object body;

    public static InvokeResult of(HttpResult httpResult) {
        return new InvokeResult().setStatus(httpResult.getStatus())
                .setTime(httpResult.getTask().httpClient().totalTimeoutMillis())
                .setBody(httpResult.getBody().toString());
    }

    public static  InvokeResult nullResult(){
        return new InvokeResult().setStatus(200)
                .setTime(20L);
    }


    public String toJson(){
        return JSONObject.toJSONString(this);
    }
}
