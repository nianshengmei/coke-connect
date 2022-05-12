package org.needcoke.rpc.common.exception;

import lombok.Data;
import org.needcoke.rpc.common.constant.ConnectionExceptionEnum;

/**
 * coke通用异常
 *
 * @author yanming
 * @date 2022/5/12
 */
@Data
public class CokeConnectException extends RuntimeException {

    private String errorCode;

    private String note;

    public CokeConnectException(String message, Throwable cause, String errorCode) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public CokeConnectException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public CokeConnectException(ConnectionExceptionEnum connectionExceptionEnum) {
        super(connectionExceptionEnum.getValue());
        this.errorCode = connectionExceptionEnum.getErrorCode();
        this.note = connectionExceptionEnum.getNote();
    }
}
