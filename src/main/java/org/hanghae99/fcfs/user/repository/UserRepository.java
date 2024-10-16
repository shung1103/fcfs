package org.hanghae99.fcfs.user.repository;

import org.hanghae99.fcfs.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    Optional<User> findUserById(Long id);

    Optional<User> findBySocialIdAndSocial(String naverId, String social);


    Optional<User> findByEmail(String email);
}
