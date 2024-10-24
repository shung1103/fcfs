package org.hanghae99.userservice.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GoogleUserInfoDto {

    private String id;
    private String email;
    private String social;

    public void setSocial(String social) {
        this.social = social;
    }

    public GoogleUserInfoDto(String id, String email){
        this.id = id;
        this.email = email;

    }

    @Override
    public String toString() {
        return "GoogleUserInfoDto{" +
                "id='" + id + '\'' +
                ", email='" + email + '\'' +
                ", social='" + social + '\'' +
                '}';
    }
}
