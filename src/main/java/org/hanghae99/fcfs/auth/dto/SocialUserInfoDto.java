package org.hanghae99.fcfs.auth.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hanghae99.fcfs.common.entity.UserSocialEnum;

@Getter
@NoArgsConstructor
public class SocialUserInfoDto {
    private String id;
    private String username;
    private String email;
    private String phone;
    private String name;
    private UserSocialEnum social;

    public SocialUserInfoDto(String id, String username, String email, String phone, UserSocialEnum social, String name) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.name = name;
        this.social = social;
    }

    public SocialUserInfoDto(String id, String username, String email, UserSocialEnum social, String name) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.name = name;
        this.social = social;
    }
}