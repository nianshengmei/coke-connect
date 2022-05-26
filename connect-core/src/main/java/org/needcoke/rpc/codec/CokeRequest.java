package org.needcoke.rpc.codec;

import com.alibaba.fastjson.JSONObject;
import lombok.Getter;
import org.needcoke.rpc.common.enums.ConnectRequestEnum;
import org.needcoke.rpc.invoker.InvokeResult;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Getter
public class CokeRequest {

    /**
     * 请求类型  服务间调用 / 调用返回
     */
    private ConnectRequestEnum requestType = ConnectRequestEnum.INTERNAL_REQUEST;

    private String beanName;

    private String methodName;

    private Map<String, Object> params;

    private Map<String,String> headers;

    @Getter
    private InvokeResult result;

    private Integer requestId;


    public CokeRequest setRequestType(ConnectRequestEnum requestType) {
        this.requestType = requestType;
        return this;
    }

    public CokeRequest setBeanName(String beanName) {
        this.beanName = beanName;
        return this;
    }

    public CokeRequest setMethodName(String methodName) {
        this.methodName = methodName;
        return this;
    }

    public CokeRequest setParams(Map<String, Object> params) {
        this.params = params;
        return this;
    }

    public CokeRequest addParam(String name,Object param) {
        if (null == params) {
            params = new HashMap<>();
        }
        params.put(name,param);
        return this;
    }


    public CokeRequest setHeaders(Map<String, String> headers) {
        this.headers = headers;
        return this;
    }

    public CokeRequest addHeader(String name,String header) {
        if (null == headers) {
            headers = new HashMap<>();
        }
        headers.put(name,header);
        return this;
    }

    public CokeRequest setResult(InvokeResult result) {
        this.result = result;
        return this;
    }

    public byte[] toBytes(){
        String jsonString = JSONObject.toJSONString(this);
        return jsonString.getBytes();
    }

    public CokeRequest setRequestId(Integer requestId){
        this.requestId = requestId;
        return this;
    }
}
