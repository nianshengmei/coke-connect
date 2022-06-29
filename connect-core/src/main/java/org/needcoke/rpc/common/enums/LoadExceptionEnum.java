package org.needcoke.rpc.common.enums;

public enum LoadExceptionEnum implements EnumInterface{
    RPC_CLIENT_SCAN_ANNOTATION_IS_NOT_ADDED_TO_THE_STARTUP_CLASS("001","Rpcclient scan annotation is not added to the startup class","没有在启动类加上RpcClient扫描注解"),
    ;

    private final String code;

    private final String value;

    private final String note;

    LoadExceptionEnum(String code, String value, String note) {
        this.code = code;
        this.value = value;
        this.note = note;
    }

    public String getErrorCode() {
        return "1002" + code;
    }

    public String getNote() {
        return note;
    }

    public String getValue() {
        return value;
    }
}
