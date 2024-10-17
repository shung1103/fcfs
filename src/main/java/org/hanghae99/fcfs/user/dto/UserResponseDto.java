package org.hanghae99.fcfs.user.dto;

import lombok.Getter;
import org.hanghae99.fcfs.common.entity.UserRoleEnum;
import org.hanghae99.fcfs.user.entity.User;

@Getter
public class UserResponseDto {
    private String username;
    private String email;
    private String realName;
    private String address;
    private String phone;
    private UserRoleEnum role;

    public UserResponseDto(User user, String email, String realName, String address, String phone) {
        this.username = user.getUsername();
        this.email = email;
        this.realName = realName;
        this.address = address;
        this.phone = phone;
        this.role = user.getRole();
    }

    public UserResponseDto(User user) {
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.realName = user.getRealName();
        this.address = user.getAddress();
        this.phone = user.getPhone();
        this.role = user.getRole();
    }
}