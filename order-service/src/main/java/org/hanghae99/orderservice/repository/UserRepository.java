package org.hanghae99.orderservice.repository;

import org.hanghae99.userservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
