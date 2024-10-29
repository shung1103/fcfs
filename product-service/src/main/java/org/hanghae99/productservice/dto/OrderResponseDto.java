package org.hanghae99.productservice.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OrderResponseDto {
    private Long orderId;
    private Long userId;
    private String title;
    private Long totalPrice;
    private String orderStatus;

    public OrderResponseDto(Long userId, String title, Order order) {
        this.orderId = order.getId();
        this.userId = userId;
        this.title = title;
        this.totalPrice = order.getPayment();
        this.orderStatus = order.getOrderStatus();
    }
}
