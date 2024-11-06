package org.hanghae99.userservice.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hanghae99.userservice.entity.User;
import org.hanghae99.userservice.entity.UserRoleEnum;
import org.hanghae99.userservice.entity.UserSocialEnum;
import org.springframework.data.domain.Page;

@Getter
@NoArgsConstructor
public class UserResponseDto {
    private String username;
    private String email;
    private String realName;
    private String address;
    private String phone;
    private UserRoleEnum role;
    private UserSocialEnum social;
    private Page<OrderResponseDto> orderList;

    public UserResponseDto(User user, String email, String realName, String address, String phone, Page<OrderResponseDto> orderList) {
        this.username = user.getUsername();
        this.email = email;
        this.realName = realName;
        this.address = address;
        this.phone = phone;
        this.role = user.getRole();
        this.social = user.getSocial();
        this.orderList = orderList;
    }

    public UserResponseDto(User user) {
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.realName = user.getRealName();
        this.address = user.getAddress();
        this.phone = user.getPhone();
        this.role = user.getRole();
        this.social = user.getSocial();
    }
}
