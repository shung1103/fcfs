package org.hanghae99.userservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class SignupRequestDto {
    /*
     * 영문자, 숫자 4글자 이상
     * */
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-zA-Z]).{4,}$")
    private String username;

    /*
     * 영문자, 숫자, 특수문자가 포함된 4글자 이상
     * */
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[@#$%^&+=!]).{4,}$")
    private String password;

    @NotBlank
    private String realName;

    @NotBlank
    private String address;

    @NotBlank
    private String phone;

    @Pattern(regexp = "^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*\\.[a-zA-Z]{2,3}$", message = "유효하지 않은 이메일 주소입니다.")
    private String email;

    private boolean admin = false;
    private String adminToken = "";
}
