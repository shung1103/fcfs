package org.hanghae99.fcfs.order.dto;

import lombok.Getter;
import org.hanghae99.fcfs.order.entity.OrderItem;

@Getter
public class OrderItemResponseDto {
    private String title;
    private Integer quantity;
    private Boolean scoreComplete;

    public OrderItemResponseDto(OrderItem orderItem) {
        this.title = orderItem.getProduct().getTitle();
        this.quantity = orderItem.getQuantity();
        this.scoreComplete = orderItem.getScoreComplete();
    }
}
