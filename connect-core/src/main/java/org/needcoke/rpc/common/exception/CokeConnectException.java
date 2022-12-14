package org.needcoke.rpc.common.exception;

import lombok.Data;
import org.connect.rpc.link.tracking.util.TrackingUtil;
import org.needcoke.rpc.common.enums.ConnectionExceptionEnum;
import org.needcoke.rpc.common.enums.EnumInterface;
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

    public CokeConnectException(EnumInterface connectionExceptionEnum) {
        super(connectionExceptionEnum.getValue());
        this.errorCode = connectionExceptionEnum.getErrorCode();
        this.note = connectionExceptionEnum.getNote();
    }

    public CokeConnectException(EnumInterface connectionExceptionEnum,Throwable e) {
        super(connectionExceptionEnum.getValue(),e);
        this.errorCode = connectionExceptionEnum.getErrorCode();
        this.note = connectionExceptionEnum.getNote();
    }

    public CokeConnectException(String requestId ,ConnectionExceptionEnum connectionExceptionEnum,Throwable e) {
        super(connectionExceptionEnum.getValue(),e);
        this.errorCode = connectionExceptionEnum.getErrorCode();
        this.note = connectionExceptionEnum.getNote();
        this.requestId = requestId;
    }
}
