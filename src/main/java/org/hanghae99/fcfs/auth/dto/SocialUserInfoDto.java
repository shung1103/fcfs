package org.hanghae99.fcfs.auth.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SocialUserInfoDto {
    private String id;
    private String username;
    private String email;
    private String phone;
    private String name;
    private String social;

    public SocialUserInfoDto(String id, String username, String email, String phone, String social, String name) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.name = name;
        this.social = social;
    }

    public SocialUserInfoDto(String id, String username, String email, String social, String name) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.name = name;
        this.social = social;
    }
    // email을 username으로 하기로 해서 변경
}