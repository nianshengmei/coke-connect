package org.needcoke.rpc.common.constant;

/**
 * Http请求的contentType类型
 *
 * @author yanming
 * @date 2022/5/12
 */
public enum HttpContentType {

    JSON("json");

    private final String value;

    HttpContentType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
