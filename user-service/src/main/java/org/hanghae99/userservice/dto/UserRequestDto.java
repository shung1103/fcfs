package org.hanghae99.userservice.dto;

import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class UserRequestDto {
    private String realName;

    @Pattern(regexp = "^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*\\.[a-zA-Z]{2,3}$", message = "유효하지 않은 이메일 주소입니다.")
    private String email;

    private String address;
    private String phone;
}
