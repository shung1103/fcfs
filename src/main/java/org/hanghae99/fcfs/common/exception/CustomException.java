package org.hanghae99.fcfs.common.exception;

import lombok.Getter;

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
