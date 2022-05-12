package org.needcoke.rpc.common.constant;

/**
 * 定义负载均衡策略类型
 *
 * @author Gilgamesh
 * @reference https://blog.csdn.net/whitebearclimb/article/details/108703356
 * @date 2022/4/2
 */
public enum LoadBalanceEnum {
    ROUND_ROBIN("1", "round-robin", "轮询"),
    RANDOM("2", "random", "随机"),
    RETRY_ROUND_ROBIN("3", "retry round-robin", "轮询重试");
    private final String code;

    private final String value;

    private final String note;

    LoadBalanceEnum(String code, String value, String note) {
        this.code = code;
        this.value = value;
        this.note = note;
    }

    public String getCode() {
        return code;
    }

    public String getValue() {
        return value;
    }

    public String getNote() {
        return note;
    }
}
