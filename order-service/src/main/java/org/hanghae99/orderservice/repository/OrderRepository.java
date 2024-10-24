package org.hanghae99.orderservice.repository;

import org.hanghae99.orderservice.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findAllByOrderUserIdOrderByCreatedAtDesc(Long id);
}
