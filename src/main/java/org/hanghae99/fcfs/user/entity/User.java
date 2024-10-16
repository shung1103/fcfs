package org.hanghae99.fcfs.user.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hanghae99.fcfs.common.entity.TimeStamped;
import org.hanghae99.fcfs.common.entity.UserRoleEnum;
import org.hanghae99.fcfs.user.dto.SignupRequestDto;

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

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private UserRoleEnum role;

    //회원가입 생성자
    public User(SignupRequestDto requestDto, UserRoleEnum role) {
        this.username = requestDto.getUsername();
        this.password = requestDto.getPassword();
        this.address = requestDto.getAddress();
        this.phone = requestDto.getPhone();
        this.email = requestDto.getEmail();
        this.role = role;
    }
}
