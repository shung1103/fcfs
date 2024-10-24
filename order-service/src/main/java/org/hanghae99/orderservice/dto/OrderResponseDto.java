package org.hanghae99.orderservice.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hanghae99.orderservice.entity.Order;

@Getter
@NoArgsConstructor
public class OrderResponseDto {
    private Long orderId;
    private String username;
    private String title;
    private Long totalPrice;
    private String orderStatus;

    public OrderResponseDto(String username, String title, Order order) {
        this.orderId = order.getId();
        this.username = username;
        this.title = title;
        this.totalPrice = order.getPayment();
        this.orderStatus = order.getOrderStatus();
    }
}
