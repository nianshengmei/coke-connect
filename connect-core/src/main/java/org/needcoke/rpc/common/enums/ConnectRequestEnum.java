package org.needcoke.rpc.common.enums;

import lombok.Getter;

/**
 * 连接的请求类型
 */
@Getter
public enum ConnectRequestEnum {

    INTERNAL_REQUEST("001", "Internal request", "内部服务间请求"),
    INTERNAL_RESPONSE("002", "Internal response", "内部服务间响应"),
    ;

    private final String code;

    private final String value;

    private final String note;

    ConnectRequestEnum(String code, String value, String note) {
        this.code = code;
        this.value = value;
        this.note = note;
    }
}
