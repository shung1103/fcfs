package org.hanghae99.productservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "orders")
public class Order extends TimeStamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long id;

    @Column(name = "order_user_id", nullable = false)
    private Long orderUserId;

    @Column(name = "order_product_id", nullable = false)
    private Long orderProductId;

    @Column(name = "order_quantity", nullable = false)
    private Integer orderQuantity;

    @Column(name = "order_status")
    private String orderStatus;

    @Column(name = "payment")
    private Long payment;
}