package org.hanghae99.fcfs.order.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hanghae99.fcfs.common.entity.TimeStamped;
import org.hanghae99.fcfs.order.dto.OrderRequestDto;
import org.hanghae99.fcfs.user.entity.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "orders")
public class Order extends TimeStamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "total_price", nullable = false)
    private Long totalPrice;

    @Column(name = "order_complete")
    private String orderStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "order", cascade = CascadeType.REMOVE)
    private List<OrderItem> orderItemList = new ArrayList<>();

    public Order(User user, Long totalPrice, OrderRequestDto orderRequestDto) {
        this.user = user;
        this.totalPrice = totalPrice;
        this.orderStatus = orderRequestDto.getOrderComplete();
    }

    public void updateOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }
}
