package org.hanghae99.fcfs.order.dto;

import lombok.Getter;
import org.hanghae99.fcfs.order.entity.OrderItem;

@Getter
public class OrderItemResponseDto {
    private String title;
    private Integer orderItemQuantity;
    private Boolean markComplete;

    public OrderItemResponseDto(OrderItem orderItem) {
        this.title = orderItem.getProduct().getTitle();
        this.orderItemQuantity = orderItem.getOrderItemQuantity();
        this.markComplete = orderItem.getMarkComplete();
    }
}
