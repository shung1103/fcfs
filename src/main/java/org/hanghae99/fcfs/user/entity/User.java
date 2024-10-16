package org.hanghae99.fcfs.user.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hanghae99.fcfs.common.entity.TimeStamped;
import org.hanghae99.fcfs.common.entity.UserRoleEnum;
import org.hanghae99.fcfs.user.dto.UserRequestDto;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "users")
public class User extends TimeStamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = true)
    private String socialId;

    @Column(nullable = true)
    private String social;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private UserRoleEnum role;

    @Column(nullable = false)
    private Integer passwordChangeCount;

    //회원가입 생성자
    public User(String username, String password, String address, String phone, String email, UserRoleEnum role) {
        this.username = username;
        this.password = password;
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.role = role;
        this.passwordChangeCount = 0;
    }

    public void updateProfile(UserRequestDto userRequestDto) {
        this.address = userRequestDto.getAddress();
        this.phone = userRequestDto.getPhone();
    }

    public void updatePassword(String password) {
        this.password = password;
        this.passwordChangeCount++;
    }

    //소셜 회원가입 생성자
    public User(String username, String password, UserRoleEnum role, String email, String socialId, String social, String phone, String address) {
        this.username = username;
        this.password = password;
        this.address = address;
        this.phone = phone;
        this.socialId = socialId;
        this.social = social;
        this.email = email;
        this.role = role;
        this.passwordChangeCount = 0;
    }

    public User socialUpdate(String socialId, String social) {
        this.socialId = socialId;
        this.social = social;
        return this;
    }
}
