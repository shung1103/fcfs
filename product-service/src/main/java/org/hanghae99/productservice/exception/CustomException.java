package org.hanghae99.productservice.exception;

import lombok.Getter;
import org.hanghae99.fcfs.common.exception.ErrorCode;

@Getter
public class CustomException extends RuntimeException {
    ErrorCode errorCode;

    public CustomException(ErrorCode errorCode) {
        super(errorCode.getMessage()); // 예외 메시지 설정
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
