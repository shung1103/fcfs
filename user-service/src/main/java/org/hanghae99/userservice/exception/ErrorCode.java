package org.hanghae99.userservice.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ErrorCode {
    /* 403 FORBIDDEN : 사용자가 권한이 없는 요청을 하는 경우 */
    FORBIDDEN(HttpStatus.FORBIDDEN, "권한이 없습니다."),

    /* 401 UNAUTHORIZED : 사용자가 로그인되지 않은 겨우 */
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "로그인 하지 않은 사용자입니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
