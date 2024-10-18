package org.hanghae99.fcfs.order.repository;

import org.hanghae99.fcfs.order.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
