package org.needcoke.rpc.common.constant;

/**
 * 连接常量
 *
 * @author yanming
 * @date 2022/5/12
 */
public interface ConnectConstant {

    /**
     * 执行远程方法的相对接口路径
     */
    String EXECUTE_RELATIVE_PATH = "/coke/connect/execute";

    String COKE_PORT_RELATIVE_PATH = "/coke/connect/port";

    String COKE_RPC_TYPE_RELATIVE_PATH = "/coke/connect/rpcType";

    /**
     * 实例名称
     */
    String BEAN_NAME = "beanName";

    /**
     * 方法名
     */
    String METHOD_NAME = "methodName";

    /**
     * 警号 #
     */
    String SHARP = "#";

    /**
     * 冒号 :
     */
    String COLON = ":";

    String COKE_REQUEST_ID_HEADER_ID_NAME = "COKE_REQUEST_ID";
}
