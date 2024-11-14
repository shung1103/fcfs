package org.hanghae99.orderservice.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorResponseDto {
    private int status;
    private String code;
    private String message;

    public static ResponseEntity<ErrorResponseDto> errorResponse(ErrorCode ex) {
        return ResponseEntity
                .status(ex.getHttpStatus())
                .body(ErrorResponseDto.builder()
                        .status(ex.getHttpStatus().value())
                        .code(ex.name())
                        .message(ex.getMessage())
                        .build()
                );
    }
}
