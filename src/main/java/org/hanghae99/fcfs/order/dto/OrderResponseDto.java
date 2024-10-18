package org.hanghae99.fcfs.order.dto;

import lombok.Getter;
import org.hanghae99.fcfs.order.entity.Order;

import java.util.List;

@Getter
public class OrderResponseDto {
    private Long orderid;
    private String username;
    private Long totalPrice;
    private List<OrderItemResponseDto> orderItemList;
    private String orderStatus;

    public OrderResponseDto(Order order) {
        this.orderid = order.getId();
        this.username = order.getUser().getUsername();
        this.totalPrice = order.getTotalPrice();
        this.orderItemList = order.getOrderItemList().stream().map(OrderItemResponseDto::new).toList();
        this.orderStatus = order.getOrderStatus();
    }
}
