package org.needcoke.rpc.common.enums;

/**
 * coke connection 连接异常枚举
 *
 * @author yanming
 * @date 2022/5/12
 */
public enum ConnectionExceptionEnum {
    NO_SUCH_BEAN_NAME("0001", "no such bean name", "没有找到对应的beanName的bean"),

    BEAN_WITHOUT_METHOD("0002",
                                "the bean name ${beanName} without method name ${method} ,you can use annotation @call or check method public.",
                                "您请求的bean没有该方法,您可以考虑添加@call注解,或者修改method的访问权限为public。"),

    INVOKE_METHOD_ERROR("0003","invoke remote method error,please check the method name or param list ,if @Call ,you need to use @Call.value"
    ,"调用远程方法失败,请检查对应方法是否存在(参数列表)或者@call注解的值"),

    CAN_NOT_FIND_SUCH_INSTANCE("0004","can't find this instance.","找不到对应的实例"),

    ;
    private final String code;

    private final String value;

    private final String note;

    ConnectionExceptionEnum(String code, String value, String note) {
        this.code = code;
        this.value = value;
        this.note = note;
    }

    public String getErrorCode() {
        return "1001" + code;
    }

    public String getNote() {
        return note;
    }

    public String getValue() {
        return value;
    }

    public String logStatement(String statement){
        StringBuilder builder = new StringBuilder();
        builder.append("error code : ").append(code).append(" , ")
                .append(statement).append(" , ").append(note);
        return builder.toString();
    }
}
