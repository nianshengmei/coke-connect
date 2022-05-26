package org.needcoke.rpc.common.exception;

import lombok.Data;
import org.needcoke.rpc.common.enums.ConnectionExceptionEnum;

@Data
public class CokeSmartSocketException extends RuntimeException {

    private String errorCode;

    private String note;

    public CokeSmartSocketException(String message, Throwable cause, String errorCode) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public CokeSmartSocketException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public CokeSmartSocketException(ConnectionExceptionEnum connectionExceptionEnum) {
        super(connectionExceptionEnum.getValue());
        this.errorCode = connectionExceptionEnum.getErrorCode();
        this.note = connectionExceptionEnum.getNote();
    }

    public CokeSmartSocketException(ConnectionExceptionEnum connectionExceptionEnum,Throwable e) {
        super(connectionExceptionEnum.getValue(),e);
        this.errorCode = connectionExceptionEnum.getErrorCode();
        this.note = connectionExceptionEnum.getNote();
    }
}
