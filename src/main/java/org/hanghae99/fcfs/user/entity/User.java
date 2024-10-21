package org.hanghae99.fcfs.user.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hanghae99.fcfs.common.entity.UserRoleEnum;
import org.hanghae99.fcfs.common.entity.UserSocialEnum;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "real_name", nullable = false)
    private String realName;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "phone", nullable = false)
    private String phone;

    @Column(name = "social_id", nullable = true)
    private String socialId;

    @Column(name = "social", nullable = true)
    @Enumerated(value = EnumType.STRING)
    private UserSocialEnum social;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "role", nullable = false)
    @Enumerated(value = EnumType.STRING)
    private UserRoleEnum role;

    @Column(name = "password_change_count", nullable = false)
    private Integer passwordChangeCount;

    //회원가입 생성자
    public User(String username, String password, String realName, String address, String phone, String email, UserRoleEnum role) {
        this.username = username;
        this.password = password;
        this.realName = realName;
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.role = role;
        this.passwordChangeCount = 0;
    }

    public void updateProfile(String address, String phone) {
        this.address = address;
        this.phone = phone;
    }

    public void updatePassword(String password) {
        this.password = password;
        this.passwordChangeCount++;
    }

    //소셜 회원가입 생성자
    public User(String username, String password, UserRoleEnum role, String email, String socialId, UserSocialEnum social, String phone, String address, String realName) {
        this.username = username;
        this.password = password;
        this.address = address;
        this.phone = phone;
        this.realName = realName;
        this.socialId = socialId;
        this.social = social;
        this.email = email;
        this.role = role;
        this.passwordChangeCount = 0;
    }

    public User socialUpdate(String socialId, UserSocialEnum social) {
        this.socialId = socialId;
        this.social = social;
        return this;
    }
}
