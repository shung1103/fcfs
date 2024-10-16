package org.hanghae99.fcfs.user.dto;

import lombok.Getter;
import org.hanghae99.fcfs.common.entity.UserRoleEnum;
import org.hanghae99.fcfs.user.entity.User;

@Getter
public class UserResponseDto {
    private String username;
    private String email;
    private UserRoleEnum role;
    private String address;

    public UserResponseDto(User user) {
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.role = user.getRole();
        this.address = user.getAddress();
    }
}
