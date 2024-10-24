package org.hanghae99.userservice.repository;

import jakarta.validation.constraints.Pattern;
import org.hanghae99.userservice.entity.User;
import org.hanghae99.userservice.entity.UserSocialEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    Optional<User> findBySocialIdAndSocial(String naverId, UserSocialEnum social);


    Optional<User> findByEmail(String email);

    boolean existsByUsername(@Pattern(regexp = "^(?=.*[0-9])(?=.*[a-zA-Z]).{4,}$") String username);

    boolean existsByEmail(@Pattern(regexp = "^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*\\.[a-zA-Z]{2,3}$", message = "유효하지 않은 이메일 주소입니다.") String email);
}
