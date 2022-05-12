package org.needcoke.rpc.common.constant;

/**
 * Http请求的contentType类型
 *
 * @author yanming
 * @date 2022/5/12
 */
public enum HttpContentTypeEnum {

    JSON("json");

    private final String value;

    HttpContentTypeEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
