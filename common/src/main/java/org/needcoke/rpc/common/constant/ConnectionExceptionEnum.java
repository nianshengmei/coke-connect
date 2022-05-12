package org.needcoke.rpc.common.constant;

/**
 * @author yanming
 * @date 2022/5/12
 */
public enum ConnectionExceptionEnum {
    NO_SUCH_BEAN_NAME("0001","no such bean name","没有找到对应的beanName的bean")
    ;

    private final String code;

     private final String value;

     private final String note;

    ConnectionExceptionEnum(String code, String value, String note) {
        this.code = code;
        this.value = value;
        this.note = note;
    }

    public String getErrorCode(){
        return "1001"+code;
    }

    public String getNote() {
        return note;
    }

    public String getValue() {
        return value;
    }
}
