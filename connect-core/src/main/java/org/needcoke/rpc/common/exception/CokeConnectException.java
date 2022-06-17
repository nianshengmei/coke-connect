package org.needcoke.rpc.common.exception;

import lombok.Data;
import org.needcoke.rpc.common.enums.ConnectionExceptionEnum;
import org.needcoke.rpc.config.RequestIdContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * coke通用异常
 *
 * @author yanming
 * @date 2022/5/12
 */
@Data
@ResponseStatus(code = HttpStatus.BAD_GATEWAY)
public class CokeConnectException extends RuntimeException {

    private String errorCode;

    private String note;

    private String requestId;

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

    public CokeConnectException(ConnectionExceptionEnum connectionExceptionEnum,Throwable e) {
        super(connectionExceptionEnum.getValue(),e);
        this.errorCode = connectionExceptionEnum.getErrorCode();
        this.note = connectionExceptionEnum.getNote();
        this.requestId = RequestIdContextHolder.getRequestId();
    }

    public CokeConnectException(String requestId ,ConnectionExceptionEnum connectionExceptionEnum,Throwable e) {
        super(connectionExceptionEnum.getValue(),e);
        this.errorCode = connectionExceptionEnum.getErrorCode();
        this.note = connectionExceptionEnum.getNote();
        this.requestId = requestId;
    }
}
