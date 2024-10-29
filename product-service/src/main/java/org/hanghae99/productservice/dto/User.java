package org.hanghae99.productservice.dto;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import org.hanghae99.productservice.entity.UserRoleEnum;
import org.hanghae99.productservice.entity.UserSocialEnum;

@Getter
public class User {
    private Long id;
    private String username;
    private String password;
    private String realName;
    private String address;
    private String phone;
    private String socialId;

    @Enumerated(value = EnumType.STRING)
    private UserSocialEnum social;

    private String email;

    @Enumerated(value = EnumType.STRING)
    private UserRoleEnum role;

    private String passwordVersion;
}
